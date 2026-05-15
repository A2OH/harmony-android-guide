#!/usr/bin/env python3
"""Patch Android core-oj.jar for Westlake standalone stock-app launches.

These are tactical, guarded DEX patches for the current deployed
ohos-deploy/arm64-a15/core-oj.jar. They keep method shapes and code-item sizes
unchanged while Westlake's durable runtime/libcore support catches up.

The patches cover real McDonald's APK blockers found on 2026-04-29:

* Realm/File startup: avoid UnixFileSystem.list0() SIGBUS by returning an empty
  directory listing from java.io.UnixFileSystem.list(File).
* Atomic VarHandle gaps: replace small AtomicInteger/AtomicReference/AtomicLong
  methods with direct field fallbacks.
* Standalone Unsafe access: let both Unsafe.getUnsafe() variants return their
  singleton in Westlake's non-zygote boot path.
* Standalone System properties: make System.getProperty tolerate a null
  System.props table by returning null/default instead of crashing callers,
  and make System.setProperty return null when the table is absent.
* Standalone security providers: make java.security.Security.<clinit>
  initialize Android's built-in provider properties without resource loading,
  because boot-classpath resource lookup is not complete in Westlake yet.
  Keep only the providers that load in standalone mode; BouncyCastle currently
  crashes during reflective algorithm discovery through Class.classForName.
* Stream compatibility: make BufferedInputStream(InputStream, int) tolerate a
  non-positive size by using Android's default buffer size, preserving stock
  image/header decode paths that otherwise abort before BitmapFactory.
* Standalone file cache compatibility: avoid SIGBUS through libcore.io.Linux
  remove/rename/getLength while Glide's disk cache probes resource-cache files.

Every patch validates the original or already-patched byte sequence before
writing and recomputes the DEX header checksums.
"""

import argparse
import hashlib
import struct
import sys
import tempfile
import zlib
import zipfile
from pathlib import Path


PATCHES = (
    {
        "name": "UnixFileSystem.list(File) returns empty String[]",
        "offset": 0x0EE92C,
        "original": bytes.fromhex(
            "7110f46f0200"
            "6e106d450200"
            "0c00"
            "1100"
        ),
        "replacement": bytes.fromhex(
            "1200"
            "2300 2e11"
            "1100"
        ) + b"\x00" * 8,
    },
    {
        "name": "UnixFileSystem.delete(File) returns success",
        "offset": 0x0EE1A8,
        "original": bytes.fromhex(
            "54202a03"
            "6e10e6020000"
            "54202d03"
            "6e10e6020000"
            "62009f2c"
            "6e100e030300"
            "0c01"
            "7220e1861000"
            "1210"
            "0f00"
            "0d00"
            "1201"
            "0f01"
        ),
        "replacement": bytes.fromhex("12100f00") + b"\x00" * 44,
    },
    {
        "name": "UnixFileSystem.rename(File,File) returns success",
        "offset": 0x0EE218,
        "original": bytes.fromhex(
            "54302a03"
            "6e10e6020000"
            "54302d03"
            "6e10e6020000"
            "62009f2c"
            "6e100e030400"
            "0c01"
            "6e100e030500"
            "0c02"
            "7230e2861002"
            "1210"
            "0f00"
            "0d00"
            "1201"
            "0f01"
        ),
        "replacement": bytes.fromhex("12100f00") + b"\x00" * 52,
    },
    {
        "name": "UnixFileSystem.getLength(File) returns 0",
        "offset": 0x0EE9AC,
        "original": bytes.fromhex(
            "62009f2c"
            "6e100e030400"
            "0c01"
            "7220e8861000"
            "0c00"
            "53006e00"
            "1000"
            "0d00"
            "16010000"
            "1001"
        ),
        "replacement": bytes.fromhex("160000001000") + b"\x00" * 28,
    },
    {
        "name": "AtomicInteger.getAndIncrement direct field fallback",
        "offset": 0x259E40,
        "original": bytes.fromhex(
            "62000721"
            "1211"
            "fa4074164021d002"
            "0a00"
            "0f00"
        ),
        "replacement": bytes.fromhex(
            "52200721"
            "d8010001"
            "59210721"
            "0f00"
        ) + b"\x00" * 4,
    },
    {
        "name": "AtomicReference.getAndSet direct field fallback",
        "offset": 0x25C5C4,
        "original": bytes.fromhex(
            "62002221"
            "fa4079164012d002"
            "0c00"
            "1100"
        ),
        "replacement": bytes.fromhex(
            "54102421"
            "5b122421"
            "1100"
        ) + b"\x00" * 6,
    },
    {
        "name": "jdk.internal.misc.Unsafe.getUnsafe returns singleton",
        "offset": 0x2B3154,
        "original": bytes.fromhex(
            "7100e9930000" "0c00" "39000400" "1201" "2805"
            "6e1091090000" "0c01" "38011300" "1c02310e"
            "6e1091090200" "0c02" "33210300" "2809"
            "22028901" "1a03aa48" "7020b50d3200" "2702"
            "6202662b" "1102"
        ),
        "replacement": bytes.fromhex("6200662b" "1100") + b"\x00" * 62,
    },
    {
        "name": "sun.misc.Unsafe.getUnsafe returns singleton",
        "offset": 0x2C4D68,
        "original": bytes.fromhex(
            "7100e9930000" "0c00" "39000400" "1201" "2805"
            "6e1091090000" "0c01" "38011300" "1c02be0e"
            "6e1091090200" "0c02" "33210300" "2809"
            "22028901" "1a03aa48" "7020b50d3200" "2702"
            "6202342d" "1102"
        ),
        "replacement": bytes.fromhex("6200342d" "1100") + b"\x00" * 62,
    },
    {
        "name": "AtomicLong.compareAndSet direct field fallback",
        "offset": 0x25B0E4,
        "original": bytes.fromhex(
            "62000f21"
            "0761"
            "0472"
            "0494"
            "fb066f160000e31f"
            "0a00"
            "0f00"
        ),
        "replacement": bytes.fromhex(
            "53601221"
            "31000007"
            "38000400"
            "1200"
            "0f00"
            "5a691221"
            "0f06"
        ),
    },
    {
        "name": "AtomicLong.addAndGet direct field fallback",
        "offset": 0x25B2A4,
        "original": bytes.fromhex(
            "62000f21"
            "fa4074162043d602"
            "0b00"
            "bb301000"
        ),
        "replacement": bytes.fromhex(
            "53201221"
            "bb30"
            "5a201221"
            "1000"
        ) + b"\x00" * 6,
    },
    {
        "name": "AtomicLong.decrementAndGet direct field fallback",
        "offset": 0x25B340,
        "original": bytes.fromhex(
            "62000f21"
            "1601ffff"
            "fa4074164021d602"
            "0b00"
            "16020100"
            "bc20"
            "1000"
        ),
        "replacement": bytes.fromhex(
            "53401221"
            "1602ffff"
            "bb20"
            "5a401221"
            "1000"
        ) + b"\x00" * 10,
    },
    {
        "name": "AtomicLong.getAndAdd direct field fallback",
        "offset": 0x25B3FC,
        "original": bytes.fromhex(
            "62000f21"
            "fa4074162043d602"
            "0b00"
            "1000"
        ),
        "replacement": bytes.fromhex(
            "53201221"
            "bb03"
            "5a231221"
            "1000"
        ) + b"\x00" * 4,
    },
    {
        "name": "AtomicLong.getAndDecrement register window",
        "offset": 0x25B40C,
        "original": bytes.fromhex("0400"),
        "replacement": bytes.fromhex("0500"),
    },
    {
        "name": "AtomicLong.getAndDecrement direct field fallback",
        "offset": 0x25B41C,
        "original": bytes.fromhex(
            "62000f21"
            "1601ffff"
            "fa4074163021d602"
            "0b00"
            "1000"
        ),
        "replacement": bytes.fromhex(
            "53401221"
            "1602ffff"
            "bb02"
            "5a421221"
            "1000"
        ) + b"\x00" * 4,
    },
    {
        "name": "AtomicLong.getAndIncrement register window",
        "offset": 0x25B430,
        "original": bytes.fromhex("0400"),
        "replacement": bytes.fromhex("0500"),
    },
    {
        "name": "AtomicLong.getAndIncrement direct field fallback",
        "offset": 0x25B440,
        "original": bytes.fromhex(
            "62000f21"
            "16010100"
            "fa4074163021d602"
            "0b00"
            "1000"
        ),
        "replacement": bytes.fromhex(
            "53401221"
            "16020100"
            "bb02"
            "5a421221"
            "1000"
        ) + b"\x00" * 4,
    },
    {
        "name": "AtomicLong.getAndSet direct field fallback",
        "offset": 0x25B464,
        "original": bytes.fromhex(
            "62000f21"
            "fa4080162043d602"
            "0b00"
            "1000"
        ),
        "replacement": bytes.fromhex(
            "53201221"
            "5a231221"
            "1000"
        ) + b"\x00" * 6,
    },
    {
        "name": "AtomicLong.incrementAndGet direct field fallback",
        "offset": 0x25B51C,
        "original": bytes.fromhex(
            "62000f21"
            "16010100"
            "fa4074165021d602"
            "0b03"
            "bb13"
            "1003"
        ),
        "replacement": bytes.fromhex(
            "53501221"
            "16020100"
            "bb20"
            "5a501221"
            "1000"
        ) + b"\x00" * 6,
    },
    {
        "name": "System.getProperty(String) tolerates null props",
        "offset": 0x113CFC,
        "original": bytes.fromhex(
            "71106c110200"
            "710075110000"
            "0c00"
            "38000500"
            "6e20cf0d2000"
            "62011d07"
            "6e20f9522100"
            "0c01"
            "1101"
        ),
        "replacement": bytes.fromhex(
            "71106c110200"
            "62001d07"
            "39000300"
            "1100"
            "6e20f9522000"
            "0c00"
            "1100"
        ) + b"\x00" * 12,
    },
    {
        "name": "System.getProperty(String,String) tolerates null props",
        "offset": 0x113D34,
        "original": bytes.fromhex(
            "71106c110200"
            "710075110000"
            "0c00"
            "38000500"
            "6e20cf0d2000"
            "62011d07"
            "6e30fa522103"
            "0c01"
            "1101"
        ),
        "replacement": bytes.fromhex(
            "71106c110200"
            "62001d07"
            "39000300"
            "1103"
            "6e30fa522003"
            "0c00"
            "1100"
        ) + b"\x00" * 12,
    },
    {
        "name": "System.setProperty(String,String) tolerates null props",
        "offset": 0x113DB8,
        "original": bytes.fromhex(
            "71106c110300"
            "710075110000"
            "0c00"
            "38000c00"
            "2201af07"
            "1a02e098"
            "703021533102"
            "6e20cb0d1000"
            "62011d07"
            "6e3014533104"
            "0c01"
            "1f01a901"
            "1101"
        ),
        "replacement": bytes.fromhex(
            "71106c110300"
            "62011d07"
            "39010300"
            "1101"
            "6e3014533104"
            "0c01"
            "1f01a901"
            "1101"
        ) + b"\x00" * 26,
    },
    {
        "name": "Security.<clinit> deterministic provider init",
        "offset": 0x17BFE4,
        "original": bytes.fromhex(
            "22009909"
            "7010e5660000"
            "6900210f"
            "2200ae07"
            "7010e3520000"
            "69001f0f"
            "1200"
            "1201"
            "1c027704"
            "1a036989"
            "6e20d1093200"
            "0c02"
            "39020800"
            "1a038a10"
            "711080110300"
            "280d"
            "22036b00"
            "7020f1012300"
            "0731"
            "62031f0f"
            "6e2001531300"
            "1210"
            "38011600"
            "6e10d9030100"
            "2811"
            "0d02"
            "28fe"
            "0d02"
            "281a"
            "0d02"
            "1a039210"
            "712081112300"
            "38010600"
            "6e10d9030100"
            "28f0"
            "39000500"
            "7100aa2c0000"
            "2200db08"
            "7010415e0000"
            "6900200f"
            "0e00"
            "38010800"
            "6e10d9030100"
            "2803"
            "0d03"
            "28fe"
            "2702"
        ),
        "replacement": bytes.fromhex(
            "22009909"
            "7010e5660000"
            "6900210f"
            "2200ae07"
            "7010e3520000"
            "69001f0f"
            "7100aa2c0000"
            "2200db08"
            "7010415e0000"
            "6900200f"
            "0e00"
        ) + b"\x00" * 126,
    },
    {
        "name": "Security.initializeStatic skips BouncyCastle provider",
        "offset": 0x17C0F8,
        "original": bytes.fromhex(
            "62001f0f"
            "1a016b89"
            "1a028257"
            "6e3008531002"
            "62001f0f"
            "1a016c89"
            "1a023b90"
            "6e3008531002"
            "62001f0f"
            "1a016d89"
            "1a028057"
            "6e3008531002"
            "62001f0f"
            "1a016e89"
            "1a028157"
            "6e3008531002"
            "0e00"
        ),
        "replacement": bytes.fromhex(
            "62001f0f"
            "1a016b89"
            "1a028257"
            "6e3008531002"
            "62001f0f"
            "1a016c89"
            "1a023b90"
            "6e3008531002"
            "0e00"
        ) + b"\x00" * 36,
    },
    {
        "name": "BufferedInputStream clamps non-positive buffer size",
        "offset": 0x0D5E06,
        "original": bytes.fromhex(
            "22003e01"
            "1a01bc0c"
            "70201f0b1000"
            "2700"
        ),
        "replacement": bytes.fromhex(
            "13040020"
            "23400011"
            "5b203001"
            "0e00"
            "0000"
        ),
    },
    {
        "name": "BufferedOutputStream clamps non-positive buffer size",
        "offset": 0x0D601C,
        "original": bytes.fromhex(
            "22003e01"
            "1a01bc0c"
            "70201f0b1000"
            "2700"
        ),
        "replacement": bytes.fromhex(
            "13040020"
            "28f9"
        ) + b"\x00" * 10,
    },
    {
        "name": "BufferedReader clamps non-positive buffer size",
        "offset": 0x0D6828,
        "original": bytes.fromhex(
            "22003e01"
            "1a01bc0c"
            "70201f0b1000"
            "2700"
        ),
        "replacement": bytes.fromhex(
            "13040020"
            "28f3"
        ) + b"\x00" * 10,
    },
    {
        "name": "BufferedWriter clamps non-positive buffer size",
        "offset": 0x0D6AF6,
        "original": bytes.fromhex(
            "22003e01"
            "1a01bc0c"
            "70201f0b1000"
            "2700"
        ),
        "replacement": bytes.fromhex(
            "13040020"
            "28f2"
        ) + b"\x00" * 10,
    },
)


def patch_dex(dex: bytearray) -> list[str]:
    applied = []
    for patch in PATCHES:
        off = patch["offset"]
        original = patch["original"]
        replacement = patch["replacement"]
        if len(original) != len(replacement):
            raise ValueError(f"{patch['name']}: replacement length changed")
        actual = bytes(dex[off : off + len(original)])
        if actual == replacement:
            applied.append(f"{patch['name']}: already patched")
            continue
        if actual != original:
            raise ValueError(
                f"{patch['name']}: byte guard failed at 0x{off:x}; "
                f"expected {original.hex()} got {actual.hex()}"
            )
        dex[off : off + len(original)] = replacement
        applied.append(f"{patch['name']}: patched")

    # DEX header: SHA-1 signature covers bytes[32:], Adler32 covers bytes[12:].
    dex[12:32] = hashlib.sha1(dex[32:]).digest()
    dex[8:12] = struct.pack("<I", zlib.adler32(dex[12:]) & 0xFFFFFFFF)
    return applied


def patch_jar(src: Path, dst: Path) -> list[str]:
    if not src.exists():
        raise FileNotFoundError(src)
    with tempfile.TemporaryDirectory() as tmp:
        tmp_path = Path(tmp)
        dex_path = tmp_path / "classes.dex"
        with zipfile.ZipFile(src, "r") as zin:
            dex = bytearray(zin.read("classes.dex"))
            applied = patch_dex(dex)
            dex_path.write_bytes(dex)
            with zipfile.ZipFile(dst, "w") as zout:
                for info in zin.infolist():
                    if info.filename == "classes.dex":
                        data = dex
                    else:
                        data = zin.read(info.filename)
                    out = zipfile.ZipInfo(info.filename, info.date_time)
                    out.compress_type = info.compress_type
                    out.external_attr = info.external_attr
                    out.comment = info.comment
                    zout.writestr(out, data)
        return applied


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("src", type=Path)
    parser.add_argument("dst", type=Path)
    args = parser.parse_args()

    try:
        applied = patch_jar(args.src, args.dst)
    except Exception as exc:
        print(f"ERROR: {exc}", file=sys.stderr)
        return 1

    for line in applied:
        print(line)
    print(args.dst)
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
