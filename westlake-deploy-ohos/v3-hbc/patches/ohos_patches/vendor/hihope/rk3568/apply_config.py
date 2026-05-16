#!/usr/bin/env python3
"""apply_config.py — register oh_adapter subsystem in rk3568 product config.

Adds the oh_adapter subsystem (declared in /build/subsystem_config.json as
path='adapter') to vendor/hihope/rk3568/config.json's "subsystems" array so
that ninja's build graph includes oh_adapter's targets (oh_adapter_bridge,
apk_installer, appspawn-x). Without this, `build.sh --build-target
oh_adapter_bridge` fails with "unknown target" because the target is outside
the product's component set.

Fully idempotent: re-running is a no-op.

Usage:
    apply_config.py [--config /path/to/rk3568/config.json]
"""
import argparse
import json
import os
import sys

DEFAULT_CFG = "/home/HanBingChen/oh/vendor/hihope/rk3568/config.json"


def main() -> int:
    p = argparse.ArgumentParser()
    p.add_argument("--config", default=os.environ.get("RK3568_CFG", DEFAULT_CFG))
    args = p.parse_args()

    if not os.path.isfile(args.config):
        print(f"[apply_config] ERROR: not found: {args.config}", file=sys.stderr)
        return 1

    with open(args.config) as f:
        cfg = json.load(f)

    subsystems = cfg.get("subsystems", [])
    if any(s.get("subsystem") == "oh_adapter" for s in subsystems):
        print("[apply_config] oh_adapter already registered (idempotent skip)")
        return 0

    subsystems.append({
        "subsystem": "oh_adapter",
        "components": [
            {"component": "oh_adapter", "features": []}
        ]
    })
    cfg["subsystems"] = subsystems

    with open(args.config, "w") as f:
        json.dump(cfg, f, indent=2)
        f.write("\n")

    print(f"[apply_config] appended oh_adapter subsystem to {args.config}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
