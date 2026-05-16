/*
 * apk_signature_verifier.h
 *
 * APK signature verification for V1 (JAR Signing) and V2 (APK Signature Scheme v2).
 *
 * V1: Validates META-INF/MANIFEST.MF file hashes and CERT.RSA/SF signatures.
 * V2: Validates the APK Signing Block (located before the Central Directory),
 *     which contains whole-file content digests.
 *
 * Phase 1 supports V1 and V2 verification. V3 (key rotation) is deferred.
 */
#ifndef APK_SIGNATURE_VERIFIER_H
#define APK_SIGNATURE_VERIFIER_H

#include <cstdint>
#include <string>
#include <vector>

namespace oh_adapter {

class ApkSignatureVerifier {
public:
    enum class SignatureScheme {
        NONE = 0,
        V1_JAR = 1,
        V2_APK = 2,
        V3_APK = 3,  // Not supported in Phase 1
    };

    struct VerifyResult {
        bool verified = false;
        SignatureScheme scheme = SignatureScheme::NONE;
        std::string certFingerprint;   // SHA-256 hex fingerprint of signing certificate
        std::string errorMsg;
    };

    /**
     * Verify the signature of an APK file.
     * Tries V2 first (stronger), falls back to V1.
     *
     * @param apkPath Path to the APK file
     * @return VerifyResult with verification status and certificate info
     */
    static VerifyResult Verify(const std::string& apkPath);

    /**
     * Check if two APKs are signed with the same certificate.
     * Used during update to ensure signature consistency.
     *
     * @param apkPath1 Path to first APK
     * @param apkPath2 Path to second APK
     * @return true if both APKs have the same signing certificate
     */
    static bool HasSameCertificate(const std::string& apkPath1,
                                    const std::string& apkPath2);

private:
    /**
     * Verify APK Signature Scheme v2.
     * Locates the APK Signing Block before the Central Directory,
     * validates content digests against the signed data.
     */
    static VerifyResult VerifyV2(const std::string& apkPath);

    /**
     * Verify JAR Signature (V1).
     * Reads META-INF/MANIFEST.MF, verifies each file's SHA-256 digest,
     * then verifies CERT.SF signature against CERT.RSA certificate.
     */
    static VerifyResult VerifyV1(const std::string& apkPath);

    /**
     * Find the APK Signing Block in the file.
     * The signing block is located immediately before the Central Directory.
     *
     * @param apkPath Path to the APK file
     * @param outBlockOffset Output: offset of the signing block
     * @param outBlockSize Output: size of the signing block
     * @return true if the signing block was found
     */
    static bool FindSigningBlock(const std::string& apkPath,
                                  uint64_t& outBlockOffset,
                                  uint64_t& outBlockSize);

    /**
     * Compute SHA-256 hash of data.
     */
    static std::vector<uint8_t> ComputeSHA256(const uint8_t* data, size_t size);

    /**
     * Convert binary hash to hex string.
     */
    static std::string ToHexString(const std::vector<uint8_t>& data);
};

}  // namespace oh_adapter

#endif  // APK_SIGNATURE_VERIFIER_H
