import struct, sys, hashlib, zlib

def read_uleb128(data, offset):
    result, shift = 0, 0
    while True:
        b = data[offset]; offset += 1
        result |= (b & 0x7F) << shift
        if (b & 0x80) == 0: break
        shift += 7
    return result, offset

def encode_uleb128_padded(value, target_len):
    """Encode ULEB128 padded to exactly target_len bytes"""
    result = []
    for i in range(target_len):
        b = value & 0x7F
        value >>= 7
        if i < target_len - 1:
            b |= 0x80  # continuation bit for all but last byte
        result.append(b)
    return bytes(result)

def find_string_idx(data, target):
    sids_size = struct.unpack_from('<I', data, 56)[0]
    sids_off = struct.unpack_from('<I', data, 60)[0]
    for i in range(sids_size):
        soff = struct.unpack_from('<I', data, sids_off + i*4)[0]
        _, pos = read_uleb128(data, soff)
        end = data.index(0, pos)
        if data[pos:end].decode('utf-8','replace') == target: return i
    return -1

def find_type_idx(data, desc):
    si = find_string_idx(data, desc)
    if si < 0: return -1
    tids_size = struct.unpack_from('<I', data, 64)[0]
    tids_off = struct.unpack_from('<I', data, 68)[0]
    for i in range(tids_size):
        if struct.unpack_from('<I', data, tids_off + i*4)[0] == si: return i
    return -1

def patch_method(data, class_desc, method_name):
    cti = find_type_idx(data, class_desc)
    mni = find_string_idx(data, method_name)
    if cti < 0: return data, f"class {class_desc} not found"
    if mni < 0: return data, f"method {method_name} not in strings"

    mids_size = struct.unpack_from('<I', data, 88)[0]
    mids_off = struct.unpack_from('<I', data, 92)[0]
    tgt = -1
    for i in range(mids_size):
        o = mids_off + i*8
        if struct.unpack_from('<H', data, o)[0] == cti and struct.unpack_from('<I', data, o+4)[0] == mni:
            tgt = i; break
    if tgt < 0: return data, f"{method_name} not in method_ids"

    cdefs_size = struct.unpack_from('<I', data, 96)[0]
    cdefs_off = struct.unpack_from('<I', data, 100)[0]
    for ci in range(cdefs_size):
        cdo = cdefs_off + ci*32
        if struct.unpack_from('<I', data, cdo)[0] != cti: continue
        cd_off = struct.unpack_from('<I', data, cdo+24)[0]
        if cd_off == 0: return data, "no class_data"

        pos = cd_off
        sf, pos = read_uleb128(data, pos)
        iff, pos = read_uleb128(data, pos)
        dm, pos = read_uleb128(data, pos)
        vm, pos = read_uleb128(data, pos)
        for _ in range(sf): _, pos = read_uleb128(data, pos); _, pos = read_uleb128(data, pos)
        for _ in range(iff): _, pos = read_uleb128(data, pos); _, pos = read_uleb128(data, pos)

        prev = 0
        for mi in range(dm + vm):
            if mi == dm: prev = 0
            idd, pos = read_uleb128(data, pos)
            prev += idd
            af_start = pos
            af, pos = read_uleb128(data, pos)
            af_end = pos
            co_start = pos
            co, pos = read_uleb128(data, pos)
            co_end = pos

            if prev == tgt:
                if af & 0x0100: return data, f"{method_name} already native"
                # Total bytes for flags+code must stay the same
                total = (af_end - af_start) + (co_end - co_start)
                new_af = af | 0x0100
                new_co = 0
                # Try different size splits
                for af_bytes in range(1, total):
                    co_bytes = total - af_bytes
                    if co_bytes < 1: continue
                    af_enc = encode_uleb128_padded(new_af, af_bytes)
                    co_enc = encode_uleb128_padded(new_co, co_bytes)
                    # Verify decoding
                    v1, _ = read_uleb128(af_enc, 0)
                    v2, _ = read_uleb128(co_enc, 0)
                    if v1 == new_af and v2 == new_co:
                        data = bytearray(data)
                        data[af_start:co_end] = af_enc + co_enc
                        return bytes(data), f"PATCHED {method_name} (0x{af:x}->0x{new_af:x})"
                return data, f"can't fit {method_name} in {total} bytes"
    return data, "class_def not found"

def fix_checksum(data):
    data = bytearray(data)
    data[12:32] = hashlib.sha1(data[32:]).digest()
    struct.pack_into('<I', data, 8, zlib.adler32(bytes(data[12:])) & 0xFFFFFFFF)
    return bytes(data)

with open(sys.argv[1], 'rb') as f: data = f.read()
print(f"Patching {sys.argv[1]} ({len(data)} bytes)")

patches = [
    ("Ljava/lang/System;", "initSystemProperties"),
    ("Llibcore/icu/ICU;", "getDefaultLocale"),
    ("Llibcore/icu/ICU;", "getIcuVersion"),
    ("Llibcore/icu/ICU;", "getUnicodeVersion"),
    ("Llibcore/icu/ICU;", "getCldrVersion"),
]
n = 0
for cls, meth in patches:
    data, msg = patch_method(data, cls, meth)
    print(f"  {msg}")
    if "PATCHED" in msg: n += 1

data = fix_checksum(data)
with open(sys.argv[2], 'wb') as f: f.write(data)
print(f"\nDone: {n} methods patched")
