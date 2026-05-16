#!/usr/bin/env python3
"""Post-link patcher for musl static binaries with Skia on OHOS ARM32.
Fixes 3 musl bugs: __init_tls redirect, do_init_fini NULL check, __init_ssp no-op.
Usage: python3 patch_musl_static.py <binary> <objdump-path>
"""
import struct, subprocess, sys, re

def patch(path, objdump_path):
    with open(path, 'rb') as f:
        data = bytearray(f.read())
    phoff = struct.unpack_from('<I', data, 0x1C)[0]
    phnum = struct.unpack_from('<H', data, 0x2A)[0]
    def va_to_off(va):
        for i in range(phnum):
            off = phoff + i * 32
            pt, po, pv, _, pfs, _ = struct.unpack_from('<IIIIII', data, off)
            if pt == 1 and pv <= va < pv + pfs: return po + (va - pv)
        return None
    r = subprocess.run([objdump_path, '-d', path], capture_output=True, text=True)
    addrs = {}
    bl_va = None
    for line in r.stdout.split('\n'):
        for sym in ['do_init_fini>:', '__init_ssp>:', 'static_init_tls>:']:
            if sym in line: addrs[sym[:-1]] = int(line.split()[0], 16)
        if 'bl' in line and '__init_tls>' in line and 'static_init_tls' not in line:
            m = re.match(r'\s*([0-9a-f]+):', line)
            if m: bl_va = int(m.group(1), 16)
    patched = 0
    if bl_va and 'static_init_tls>' in addrs:
        off = va_to_off(bl_va); tgt = addrs['static_init_tls>']
        if off:
            rel = (tgt - (bl_va + 8)) // 4
            struct.pack_into('<I', data, off, 0xEB000000 | (rel & 0x00FFFFFF))
            print(f"  Fix 1: bl __init_tls -> static_init_tls"); patched += 1
    if 'do_init_fini>' in addrs:
        off = va_to_off(addrs['do_init_fini>'])
        if off:
            struct.pack_into('<I', data, off, 0xe3500000); struct.pack_into('<I', data, off+4, 0x012fff1e)
            print(f"  Fix 2: do_init_fini NULL check"); patched += 1
    if '__init_ssp>' in addrs:
        off = va_to_off(addrs['__init_ssp>'])
        if off:
            struct.pack_into('<I', data, off, 0xe12fff1e)
            print(f"  Fix 3: __init_ssp no-op"); patched += 1
    with open(path, 'wb') as f: f.write(data)
    print(f"Applied {patched}/3 fixes to {path}")

if __name__ == '__main__':
    if len(sys.argv) < 3: print(f"Usage: {sys.argv[0]} <binary> <objdump>"); sys.exit(1)
    patch(sys.argv[1], sys.argv[2])
