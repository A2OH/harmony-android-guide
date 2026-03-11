# OpenHarmony 4.1 Security Subsystem -- Comprehensive Code Review

**Reviewer:** Claude Opus 4.6 (automated)
**Date:** 2026-03-10
**Scope:** `/base/security/` (all subdirectories) and `/base/useriam/`
**Severity Scale:** CRITICAL / HIGH / MEDIUM / LOW / INFO

---

## Executive Summary

The OpenHarmony security subsystem is large and architecturally sound in many areas. It uses well-established libraries (OpenSSL, mbedTLS) rather than custom crypto, applies `memset_s` for key zeroization in most critical paths, and generally enforces IPC permission checks. However, this review identified several **critical** and **high** severity findings, most notably **hardcoded cryptographic keys compiled into production code**, **use of unsafe memory functions**, and **AES-ECB mode availability**. Several medium-severity issues around missing IV/nonce uniqueness enforcement and potential integer overflow edge cases were also found.

---

## CRITICAL Findings

### SEC-C01: Hardcoded Platform Private Key in Source Code
- **Severity:** CRITICAL
- **File:** `base/security/huks/services/huks_standard/huks_engine/main/core_dependency/src/hks_chipset_platform_key_hardcoded.c:25-28`
- **Description:** A 32-byte ECC private key (`PLATFORM_KEY_PLATFORM_PRI_KEY`) is hardcoded as a `static const` byte array in source code. This key is used in `HksChipsetPlatformDeriveKeyAndEcdh()` (line 105) for ECDH key agreement. The salt parameter is explicitly ignored (line 110: `(void)(salt);`) with a comment that it "SHOULD be used in true hardware based implementations."
- **Impact:** Any party with access to the source code or the compiled binary can extract this private key and impersonate the platform or decrypt data protected by derived keys. This completely undermines the chipset platform key security model.
- **Recommendation:** This file should only be used in development/testing builds. Production builds MUST source platform keys from hardware-backed storage (TEE/HSM). Add build-time guards (`#error`) to prevent this file from being compiled into release images.

### SEC-C02: Hardcoded Default Auth Token Keys
- **Severity:** CRITICAL
- **File:** `base/security/huks/services/huks_standard/huks_engine/main/core/src/hks_keyblob.c:616-627`
- **Description:** When `HKS_SUPPORT_GET_AT_KEY` is NOT defined, the auth token MAC key and cipher key are derived from static ASCII strings: `"huks_default_user_auth_token_mac"` and `"huks_default_user_auth_cipherkey"`. These are deterministic and identical across all devices.
- **Impact:** An attacker can forge authentication tokens or decrypt protected key blobs on any device where this fallback is active. User auth access control for HUKS-managed keys is completely bypassed.
- **Recommendation:** Remove the fallback path entirely or ensure `HKS_SUPPORT_GET_AT_KEY` is always defined in production. Add a compile-time assertion to prevent builds without this flag in release configurations.

---

## HIGH Findings

### SEC-H01: AES-ECB Mode Exposed in Crypto Framework and HUKS
- **Severity:** HIGH
- **File:** `base/security/crypto_framework/plugin/openssl_plugin/crypto_operation/cipher/src/cipher_aes_openssl.c:52-65, 203-206`
- **File:** `base/security/huks/frameworks/huks_standard/main/common/src/hks_base_check.c:100, 135, 1317`
- **Description:** AES-ECB mode is available and is even the **default** cipher type returned by `DefaultCiherType()` (line 203-206). ECB mode does not provide semantic security -- identical plaintext blocks produce identical ciphertext blocks, enabling pattern analysis attacks.
- **Impact:** Applications that fail to specify a mode or explicitly choose ECB will use cryptographically weak encryption. This is especially dangerous for image/data encryption where patterns are preserved.
- **Recommendation:** Remove ECB as the default mode. Return an error instead of silently falling back. Consider deprecating or removing ECB entirely, or at minimum logging a warning when ECB is selected.

### SEC-H02: Unsafe `memcpy` Without Bounds Checking in SELinux Shared Memory
- **Severity:** HIGH
- **File:** `base/security/selinux_adapter/interfaces/policycoreutils/src/selinux_share_mem.c:61`
- **Description:** The function `WriteSharedMem()` uses bare `memcpy(sharedMem, data, length)` without verifying that `length` does not exceed the shared memory region size. The shared memory size is determined at `InitSharedMem()` time via `spaceSize`, but `WriteSharedMem()` has no reference to this bound.
- **Impact:** A caller providing a `length` larger than the mapped shared memory region causes a buffer overflow, potentially leading to code execution or privilege escalation in a security-critical SELinux policy component.
- **Recommendation:** Pass the total shared memory size to `WriteSharedMem()` and validate `length <= spaceSize` before the copy. Use `memcpy_s` from securec.

### SEC-H03: Missing Permission Check on `VerifyAccessTokenInner` IPC
- **Severity:** HIGH
- **File:** `base/security/access_token/services/accesstokenmanager/main/cpp/src/service/accesstoken_manager_stub.cpp:106-112`
- **Description:** The `VerifyAccessTokenInner()` handler reads a `tokenID` and `permissionName` from the IPC message and directly calls `VerifyAccessToken()` without any permission check on the caller. While this function performs a read-only query, it allows any process to probe whether any arbitrary token has any arbitrary permission.
- **Impact:** An unprivileged application can enumerate permissions granted to any other application on the system, enabling reconnaissance for privilege escalation attacks. Sensitive permission state (e.g., camera, microphone grants) can be discovered by malicious apps.
- **Recommendation:** Require callers to hold `GET_SENSITIVE_PERMISSIONS` or be a native/privileged process. Alternatively, restrict `tokenID` to the caller's own token unless a permission is held.

### SEC-H04: GCM IV Max Length Set to 16 Bytes (Not Enforcing 12-Byte Recommended)
- **Severity:** HIGH
- **File:** `base/security/crypto_framework/plugin/openssl_plugin/crypto_operation/cipher/src/cipher_aes_openssl.c:29-30`
- **Description:** The AES-GCM implementation accepts IVs from 1 to 16 bytes (`GCM_IV_MIN_LEN = 1`, `GCM_IV_MAX_LEN = 16`). While technically valid per the NIST specification, NIST SP 800-38D strongly recommends 12-byte IVs for GCM. IVs of lengths other than 12 bytes undergo GHASH processing, which can degrade security guarantees and introduce subtle vulnerabilities.
- **Impact:** Short IVs (1-11 bytes) significantly reduce the randomness space, increasing collision probability. The extremely permissive minimum of 1 byte is particularly dangerous, as it makes IV collisions (and thus full authentication key recovery) near-certain under normal use.
- **Recommendation:** Enforce `GCM_IV_MIN_LEN = 12` and `GCM_IV_MAX_LEN = 12` (or at minimum set the minimum to 12). Log a deprecation warning for non-12-byte IVs.

---

## MEDIUM Findings

### SEC-M01: No IV/Nonce Uniqueness Enforcement in AES Cipher Operations
- **Severity:** MEDIUM
- **File:** `base/security/crypto_framework/plugin/openssl_plugin/crypto_operation/cipher/src/cipher_aes_openssl.c:275-286`
- **Description:** The IV/nonce parameters in GCM, CCM, CBC, and CTR modes are accepted as-is from the caller. There is no mechanism to detect or prevent IV reuse with the same key. For GCM and CTR, IV reuse with the same key is catastrophic (enables plaintext recovery and authentication key extraction for GCM).
- **Impact:** Applications that mismanage IVs (reusing them) will silently produce insecure ciphertext.
- **Recommendation:** While enforcing uniqueness at the framework level is complex, consider: (1) providing an auto-increment/random IV generation mode, (2) documenting the requirement prominently, (3) logging if the same IV is detected for the same key within a session.

### SEC-M02: Integer Overflow Check Flawed in Base64 Encoding
- **Severity:** MEDIUM
- **File:** `base/security/device_security_level/baselib/utils/src/utils_base64.c:68`
- **Description:** The overflow check `if (toLen >= (toLen * 4))` is logically broken. Due to unsigned integer overflow semantics in C, if `toLen` is large enough that `toLen * 4` overflows and wraps to a value less than `toLen`, the check passes correctly. However, if `toLen` is 0 (which it cannot be since `fromLen > 0` implies `toLen >= 1`), or if `toLen * 4` happens to overflow to exactly `toLen`, the check fails. A standard overflow check like `toLen > UINT32_MAX / 4` would be clearer and more reliable.
- **Recommendation:** Replace with `if (toLen > UINT32_MAX / 4)` for a clear, correct overflow check.

### SEC-M03: `HKS_FREE_BLOB` Does Not Zeroize Before Freeing
- **Severity:** MEDIUM
- **File:** `base/security/huks/frameworks/huks_standard/main/common/include/hks_mem.h:49-55`
- **Description:** The `HKS_FREE_BLOB` macro frees blob data without zeroing it first. Only `HKS_MEMSET_FREE_BLOB` performs zeroization. Multiple call sites (e.g., `hks_client_service_ipc.c`) use `HKS_FREE_BLOB` to release blobs that may contain key material, leaving sensitive data in freed memory.
- **Impact:** Key material may persist in freed heap memory and be recoverable through memory analysis, heap spraying attacks, or core dumps.
- **Recommendation:** Audit all `HKS_FREE_BLOB` call sites. Where blobs contain key material, session keys, or other secrets, replace with `HKS_MEMSET_FREE_BLOB`. Consider making `HKS_FREE_BLOB` always zeroize, since the performance cost of `memset_s` on small buffers is negligible.

### SEC-M04: Token Sync SoftBus Channel Lacks Data Authentication
- **Severity:** MEDIUM
- **File:** `base/security/access_token/services/tokensyncmanager/src/remote/soft_bus_channel.cpp:262-307`
- **Description:** Token synchronization data is transmitted over SoftBus with zlib compression/decompression but without apparent message authentication or integrity verification at this layer. The `Decompress()` function allocates 1MB (`RPC_TRANSFER_BYTES_MAX_LENGTH`) for decompression without any HMAC or signature check on the incoming compressed data.
- **Impact:** If the SoftBus transport layer does not provide integrity protection (or if that protection is compromised), an attacker could inject crafted compressed data to manipulate token synchronization, potentially granting unauthorized permissions to remote tokens. The 1MB decompression buffer is also a potential decompression bomb vector.
- **Recommendation:** Add message authentication (HMAC or signature) at the application layer before compression/after decompression. Validate decompressed size against expected bounds. Consider using authenticated encryption for token sync payloads.

### SEC-M05: Scrypt PIN Derivation Uses Pass-by-Value for Sensitive Data
- **Severity:** MEDIUM
- **File:** `base/useriam/pin_auth/frameworks/scrypt/src/scrypt.cpp:48, 89`
- **Description:** The `DoScrypt()` and `GetScrypt()` functions accept the PIN `data` parameter by value (`std::vector<uint8_t> data`), creating copies of the sensitive PIN material on the stack. While `ClearPinData()` zeroizes the local copy (line 83), the caller's copy and any intermediate copies created during vector copying are not zeroized.
- **Impact:** PIN material may remain in multiple locations in memory after the function completes.
- **Recommendation:** Pass by reference (`std::vector<uint8_t> &data`) to avoid creating unnecessary copies of PIN material.

### SEC-M06: UserAuth IPC Stub Missing Authorization Checks on Several Endpoints
- **Severity:** MEDIUM
- **File:** `base/useriam/user_auth_framework/frameworks/native/ipc/src/user_auth_stub.cpp:37-72`
- **Description:** The `OnRemoteRequest` handler dispatches to various handlers (`GetAvailableStatusStub`, `GetPropertyStub`, `AuthStub`, etc.) based on the IPC code. The descriptor token check is present (line 40), but individual handlers must perform their own permission checks. The `GetAvailableStatusStub()` handler (line 74-103) performs no permission check. While the check may happen deeper in the call chain (`UserAuthService`), defense-in-depth requires checks at the stub layer.
- **Recommendation:** Add permission checks at the stub layer for all sensitive operations, not just in the service implementation. Document which operations are intentionally public.

### SEC-M07: Certificate Manager Directory Permission 0705 Allows World Execute
- **Severity:** MEDIUM
- **File:** `base/security/certificate_manager/services/cert_manager_standard/cert_manager_engine/main/core/src/cert_manager_file_operator.c:242`
- **Description:** The default directory permission `S_IRWXU | S_IROTH | S_IXOTH` (0705) allows "other" users to read and traverse certificate storage directories.
- **Impact:** Other processes may list and read certificate files, potentially accessing private certificates or CA trust stores.
- **Recommendation:** Use 0700 (`S_IRWXU`) as the default, allowing only the owner process to access the directory.

---

## LOW Findings

### SEC-L01: Potential Denial of Service via Unbounded Parcel Reads
- **File:** `base/security/access_token/services/accesstokenmanager/main/cpp/src/service/accesstoken_manager_stub.cpp:126-141`
- **Description:** `GetDefPermissionsInner()` reads a list of permissions from the storage and writes all of them to the reply parcel without an upper bound check. While `GetSelfPermissionsStateInner()` (line 182) properly bounds with `MAX_PERMISSION_SIZE`, the `GetDefPermissions` path does not.
- **Impact:** Could cause excessive memory allocation in reply parcel construction if the permission list is very large.
- **Recommendation:** Add a size limit check consistent with `MAX_PERMISSION_SIZE`.

### SEC-L02: Debug Log Leak of Key Alias Bytes
- **File:** `base/security/device_auth/services/legacy/authenticators/src/account_unrelated/pake_task/pake_v1_task/pake_v1_protocol_task/pake_v1_protocol_task_common.c:147`
- **Description:** `LOGI("Psk alias(HEX): %x%x%x%x****.", pskKeyAliasVal[0], pskKeyAliasVal[1], pskKeyAliasVal[2], pskKeyAliasVal[3]);` logs the first 4 bytes of a key alias in production logs. While this is an alias (not key material), it leaks information that could aid key enumeration.
- **Recommendation:** Remove or gate behind a debug flag.

### SEC-L03: strnlen Used Incorrectly for Off-by-One Length Checks
- **File:** `base/security/device_auth/services/key_agree_sdk/src/key_agree_sdk.c:40, 48, 229`
- **Description:** Checks like `strnlen((const char *)(sharedSecret->data), sharedSecret->length) > sharedSecret->length + 1` have a questionable `+ 1`. If the string is exactly `length` bytes (no null terminator within the buffer), `strnlen` returns `length`, and the check `> length + 1` is false, which is the intended behavior. But the semantic intent is confusing and error-prone.
- **Recommendation:** Clarify the intent and simplify the bounds check.

### SEC-L04: SQL Uses String Formatting Rather than Parameterized Queries in Some Paths
- **File:** `base/security/access_token/services/common/database/src/sqlite_helper.cpp:153`
- **Description:** `sqlite3_exec(db_, sql.c_str(), ...)` is used with a pre-built SQL string. While the `Statement` class at line 27-29 does use `sqlite3_prepare_v2` with parameter binding, some direct exec paths could be vulnerable if SQL strings are constructed from user input.
- **Impact:** Low risk -- most SQL strings appear to be schema DDL and internally generated queries, not user-input-derived.
- **Recommendation:** Audit all `sqlite3_exec` call sites to confirm no user input enters the SQL string.

---

## INFO / Positive Observations

### SEC-I01: Good Use of memset_s for Key Material Zeroization
- **Files:** `crypto_framework/plugin/openssl_plugin/key/sym_key_generator/src/sym_key_openssl.c:75, 179`
- Symmetric key material is properly zeroized in both `ClearMem()` and `DestroySymKeySpi()` using `memset_s` before freeing. This is correct practice.

### SEC-I02: Proper RAND_priv_bytes for Key Generation
- **File:** `crypto_framework/plugin/openssl_plugin/key/sym_key_generator/src/sym_key_openssl.c:124`
- Symmetric key generation uses `Openssl_RAND_priv_bytes()`, which is the appropriate API for generating sensitive random material (not `RAND_bytes` or `rand()`).

### SEC-I03: No Use of Insecure PRNG (rand/srand/random)
- No instances of `rand()`, `srand()`, or `random()` were found in the security subsystem C source files. This confirms that cryptographically secure random number generation is used throughout.

### SEC-I04: Consistent IPC Permission Checks in Access Token Manager
- The `accesstoken_manager_stub.cpp` consistently applies permission checks (`IsPrivilegedCalling()`, `VerifyAccessToken()`, `IsNativeProcessCalling()`, `IsSystemAppCalling()`) on virtually all IPC handlers that modify permission state (grant, revoke, delete, update).

### SEC-I05: HUKS Process Name Injection Prevention
- **File:** `base/security/huks/services/huks_standard/huks_service/main/core/src/hks_client_service.c:285-288`
- The HUKS service explicitly checks for and rejects client-supplied `HKS_TAG_PROCESS_NAME` parameters, preventing a client from impersonating another process's key namespace. This is good defense against parameter injection.

### SEC-I06: HUKS Key Blob Nonces Generated Randomly
- **File:** `base/security/huks/services/huks_standard/huks_engine/main/core/src/hks_keyblob.c:294-296`
- Nonces for key blob encryption are generated using `HksCryptoHalFillRandom()`, ensuring proper randomness for AEAD operations.

### SEC-I07: Device Auth PAKE Implementation Properly Zeroizes Return Keys
- **File:** `base/security/device_auth/services/legacy/authenticators/src/account_unrelated/pake_task/pake_v1_task/pake_v1_protocol_task/pake_v1_protocol_task_common.c:36`
- Return key material is zeroized with `memset_s` before freeing.

### SEC-I08: Asset Module (Rust) Provides Memory Safety
- **File:** `base/security/asset/services/core_service/src/operations/operation_add.rs`
- The Asset storage module is implemented in Rust, which provides memory safety guarantees (no buffer overflows, use-after-free, etc.). Secret data encryption uses a proper key-per-owner model with AAD binding.

### SEC-I09: Fuzz Testing Infrastructure Present
- Extensive fuzz test harnesses are present across access_token, HUKS, crypto_framework, and device_auth, covering critical IPC interfaces and API surfaces.

---

## Summary of Findings by Severity

| Severity | Count | Key Issues |
|----------|-------|------------|
| CRITICAL | 2 | Hardcoded platform key, hardcoded auth token keys |
| HIGH | 4 | AES-ECB default, unsafe memcpy, missing IPC auth, weak GCM IV bounds |
| MEDIUM | 7 | No IV reuse detection, int overflow check, key zeroization gaps, token sync auth, PIN copy, UserAuth stubs, cert dir permissions |
| LOW | 4 | Unbounded parcel reads, debug key alias leak, strnlen logic, SQL exec paths |
| INFO | 9 | Positive observations on crypto practices, PRNG usage, permission checks |

---

## Top Recommendations (Priority Order)

1. **Immediately gate hardcoded key files** (`hks_chipset_platform_key_hardcoded.c`, `hks_keyblob.c` default AT keys) with `#error` for production builds. These represent complete security bypasses.
2. **Remove AES-ECB as default** cipher mode and enforce minimum 12-byte GCM IVs.
3. **Add bounds validation** to `WriteSharedMem()` in the SELinux adapter.
4. **Add permission checks** to `VerifyAccessTokenInner` to prevent cross-app permission state enumeration.
5. **Audit all `HKS_FREE_BLOB` usage** and replace with `HKS_MEMSET_FREE_BLOB` where blobs may contain secrets.
6. **Add message authentication** to the token sync SoftBus channel.
7. **Fix PIN scrypt function signatures** to pass sensitive data by reference.
