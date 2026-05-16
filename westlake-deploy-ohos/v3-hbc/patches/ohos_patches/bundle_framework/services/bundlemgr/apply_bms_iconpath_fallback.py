#!/usr/bin/env python3
"""apply_bms_iconpath_fallback.py — when GetMediaData has iconId=0, fall back
to reading the file from abilityInfo.iconPath.

Background:
    BundleDataMgr::GetMediaData uses ResourceManager::GetMediaDataById(iconId).
    For Android-APK bundles registered by adapter (no OH resources.index),
    iconId is always 0 → ResourceManager fails → launcher can't get icon →
    desktop shows label only, no icon.

Fix:
    Right after FindAbilityInfoInBundleInfo + IsAbilityEnabledV9 succeed,
    BEFORE calling ResourceManager:
      if (abilityInfo.iconId == 0 && !abilityInfo.iconPath.empty()) {
          read iconPath file → mediaDataPtr;
          return ERR_OK;
      }
    Otherwise continue normal ResourceManager flow.

Idempotent.
"""
import argparse
import os
import sys

DEFAULT = (
    "/home/HanBingChen/oh/foundation/bundlemanager/bundle_framework/"
    "services/bundlemgr/src/bundle_data_mgr.cpp"
)

PATCH_MARKER = "// adapter project: iconPath fallback when iconId=0"

# Anchor on the GLOBAL_RESMGR_ENABLE block start, inject our fallback right
# after the IsAbilityEnabledV9 success path closes (just before the closing
# brace of the inner scope) and BEFORE the resourceManager construction.
ANCHOR_OLD = (
    "        if (!isEnable) {\n"
    "            APP_LOGE(\"%{public}s ability disabled: %{public}s\", bundleName.c_str(), abilityName.c_str());\n"
    "            return ERR_BUNDLE_MANAGER_ABILITY_DISABLED;\n"
    "        }\n"
    "    }\n"
    "    std::shared_ptr<Global::Resource::ResourceManager> resourceManager =\n"
)

ANCHOR_NEW = (
    "        if (!isEnable) {\n"
    "            APP_LOGE(\"%{public}s ability disabled: %{public}s\", bundleName.c_str(), abilityName.c_str());\n"
    "            return ERR_BUNDLE_MANAGER_ABILITY_DISABLED;\n"
    "        }\n"
    "    }\n"
    "    // adapter project: iconPath fallback when iconId=0\n"
    "    // Android-APK bundles registered without OH resources.index have iconId=0.\n"
    "    // Read PNG bytes from abilityInfo.iconPath directly, bypass ResourceManager.\n"
    "    if (abilityInfo.iconId == 0 && !abilityInfo.iconPath.empty()) {\n"
    "        std::ifstream ifs(abilityInfo.iconPath, std::ios::binary | std::ios::ate);\n"
    "        if (ifs) {\n"
    "            std::streamsize sz = ifs.tellg();\n"
    "            if (sz > 0 && sz < (10 * 1024 * 1024)) {\n"
    "                ifs.seekg(0);\n"
    "                mediaDataPtr = std::make_unique<uint8_t[]>(static_cast<size_t>(sz));\n"
    "                if (ifs.read(reinterpret_cast<char*>(mediaDataPtr.get()), sz)) {\n"
    "                    len = static_cast<size_t>(sz);\n"
    "                    APP_LOGI(\"adapter iconPath fallback: %{public}s (%{public}zu bytes)\",\n"
    "                        abilityInfo.iconPath.c_str(), len);\n"
    "                    return ERR_OK;\n"
    "                }\n"
    "                mediaDataPtr.reset();\n"
    "            }\n"
    "        }\n"
    "        APP_LOGW(\"adapter iconPath fallback failed for %{public}s\", abilityInfo.iconPath.c_str());\n"
    "    }\n"
    "    std::shared_ptr<Global::Resource::ResourceManager> resourceManager =\n"
)

INCLUDE_MARKER = "#include <fstream>"
INCLUDE_ANCHOR_OLD = '#include "bundle_data_mgr.h"\n'
INCLUDE_ANCHOR_NEW = (
    '#include "bundle_data_mgr.h"\n'
    '#ifdef GLOBAL_RESMGR_ENABLE\n'
    '#include <fstream>  // adapter project: iconPath fallback\n'
    '#endif\n'
)


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--src", default=DEFAULT)
    args = ap.parse_args()

    if not os.path.isfile(args.src):
        sys.exit(f"ERROR: source not found: {args.src}")

    with open(args.src, "r", encoding="utf-8") as f:
        text = f.read()

    if PATCH_MARKER in text:
        print(f"[skip] {args.src} already has iconPath fallback")
        return

    bak = args.src + ".adapter_orig_iconpath_fallback"
    if not os.path.exists(bak):
        with open(bak, "w", encoding="utf-8") as f:
            f.write(text)
        print(f"[backup] {bak}")

    if ANCHOR_OLD not in text:
        sys.exit("ERROR: GetMediaData anchor not found")

    if INCLUDE_MARKER not in text:
        text = text.replace(INCLUDE_ANCHOR_OLD, INCLUDE_ANCHOR_NEW, 1)

    text = text.replace(ANCHOR_OLD, ANCHOR_NEW, 1)

    with open(args.src, "w", encoding="utf-8") as f:
        f.write(text)
    print(f"[patched] {args.src} — GetMediaData iconPath fallback installed")


if __name__ == "__main__":
    main()
