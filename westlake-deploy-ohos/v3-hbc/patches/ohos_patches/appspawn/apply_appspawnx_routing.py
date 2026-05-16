#!/usr/bin/env python3
"""apply_appspawnx_routing.py — add APPSPAWNX_SERVER_NAME / APPSPAWNX_SOCKET_NAME
recognition to OH appspawn client + ability_runtime AppSpawnClient ctor so
GetAndroidSpawnClient() (already added by adapter, see remote_client_manager.cpp:29
which constructs AppSpawnClient("appspawn-x")) actually connects to /dev/unix/socket/AppSpawnX
instead of falling back to NWEBSPAWN_SOCKET_NAME.

Background:
    OH 7.0.0.18 source already has Android route (BundleType::APP_ANDROID + AndroidSpawnClient).
    But AppSpawnClient(const char*) ctor only recognizes "appspawn"/"cjappspawn"/"nwebspawn"/
    "nativespawn"/"hybridspawn" — anything else falls through to NWEBSPAWN. So our
    "appspawn-x" service name silently maps to the wrong socket. Need to register it.

Affects 4 files:
    1. base/startup/appspawn/interfaces/innerkits/include/appspawn.h
       (add APPSPAWNX_SERVER_NAME constant)
    2. base/startup/appspawn/modules/module_engine/include/appspawn_msg.h
       (add APPSPAWNX_SOCKET_NAME constant)
    3. base/startup/appspawn/interfaces/innerkits/client/appspawn_client.c
       (add CLIENT_FOR_APPSPAWNX case in GetSocketName)
    4. foundation/ability/ability_runtime/services/appmgr/src/app_spawn_client.cpp
       (add APPSPAWNX_SERVER_NAME branch in ctor's if-cascade)

Idempotent.
"""
import argparse
import os
import sys

OH_DEFAULT = "/home/HanBingChen/oh"


def patch_file(path, marker, anchor_old, anchor_new, label):
    if not os.path.isfile(path):
        sys.exit(f"ERROR: source not found: {path}")
    with open(path, "r", encoding="utf-8") as f:
        text = f.read()
    if marker in text:
        print(f"[skip] {label}: already patched")
        return
    bak = path + ".adapter_orig_appspawnx_routing"
    if not os.path.exists(bak):
        with open(bak, "w", encoding="utf-8") as f:
            f.write(text)
        print(f"[backup] {bak}")
    if anchor_old not in text:
        sys.exit(f"ERROR: anchor not found in {path}")
    text = text.replace(anchor_old, anchor_new, 1)
    with open(path, "w", encoding="utf-8") as f:
        f.write(text)
    print(f"[patched] {label}: {path}")


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--oh-root", default=OH_DEFAULT)
    args = ap.parse_args()
    OH = args.oh_root

    # 1. appspawn.h — add APPSPAWNX_SERVER_NAME after APPSPAWNDF_SERVER_NAME
    patch_file(
        path=f"{OH}/base/startup/appspawn/interfaces/innerkits/include/appspawn.h",
        marker='APPSPAWNX_SERVER_NAME',
        anchor_old='#define APPSPAWNDF_SERVER_NAME "appspawndf"\n',
        anchor_new=(
            '#define APPSPAWNDF_SERVER_NAME "appspawndf"\n'
            '#define APPSPAWNX_SERVER_NAME "appspawn-x"  // adapter project: Android APK spawner\n'
        ),
        label="appspawn.h",
    )

    # 2. appspawn_msg.h — add APPSPAWNX_SOCKET_NAME
    patch_file(
        path=f"{OH}/base/startup/appspawn/modules/module_engine/include/appspawn_msg.h",
        marker='APPSPAWNX_SOCKET_NAME',
        anchor_old='#define APPSPAWNDF_SOCKET_NAME "AppSpawndf"\n',
        anchor_new=(
            '#define APPSPAWNDF_SOCKET_NAME "AppSpawndf"\n'
            '#define APPSPAWNX_SOCKET_NAME "AppSpawnX"  // adapter project: Android APK spawner\n'
        ),
        label="appspawn_msg.h",
    )

    # 3. appspawn_client.c GetSocketName — add CLIENT_FOR_APPSPAWNX case
    # Need to also add CLIENT_FOR_APPSPAWNX to the enum first; we'll piggyback on
    # the existing string-based dispatch in ability_runtime by adding name comparison.
    # For simplicity: hijack one of the defaults — when service name == APPSPAWNX_SERVER_NAME,
    # return APPSPAWNX_SOCKET_NAME. Achieved via separate string branch.
    #
    # The client uses an enum (CLIENT_FOR_APPSPAWN etc.) that's set once at init from
    # service name. So we add APPSPAWNX into the enum + the GetSocketName switch.
    patch_file(
        path=f"{OH}/base/startup/appspawn/interfaces/innerkits/client/appspawn_client.h",
        marker='CLIENT_FOR_APPSPAWNX',
        anchor_old=(
            '    CLIENT_FOR_APPSPAWNDF,\n'
            '    CLIENT_MAX\n'
        ),
        anchor_new=(
            '    CLIENT_FOR_APPSPAWNDF,\n'
            '    CLIENT_FOR_APPSPAWNX,  // adapter project: Android APK spawner\n'
            '    CLIENT_MAX\n'
        ),
        label="appspawn_client.h enum",
    )

    patch_file(
        path=f"{OH}/base/startup/appspawn/interfaces/innerkits/client/appspawn_client.c",
        marker='APPSPAWNX_SOCKET_NAME',
        anchor_old=(
            '        case CLIENT_FOR_APPSPAWNDF:\n'
            '            socketName = APPSPAWNDF_SOCKET_NAME;\n'
            '            break;\n'
        ),
        anchor_new=(
            '        case CLIENT_FOR_APPSPAWNDF:\n'
            '            socketName = APPSPAWNDF_SOCKET_NAME;\n'
            '            break;\n'
            '        case CLIENT_FOR_APPSPAWNX:  // adapter project: Android APK spawner\n'
            '            socketName = APPSPAWNX_SOCKET_NAME;\n'
            '            break;\n'
        ),
        label="appspawn_client.c GetSocketName",
    )

    # Also add appspawn_msg.h include if missing in appspawn_client.c (might already be).
    # And patch the enum->name map in AppSpawnClientInit so "appspawn-x" string maps to CLIENT_FOR_APPSPAWNX.
    patch_file(
        path=f"{OH}/base/startup/appspawn/interfaces/innerkits/client/appspawn_client.c",
        marker='APPSPAWNX_SERVER_NAME',
        anchor_old=(
            '    AppSpawnClientType type = CLIENT_FOR_APPSPAWN;\n'
            '    if (strcmp(serviceName, CJAPPSPAWN_SERVER_NAME) == 0) {\n'
        ),
        anchor_new=(
            '    AppSpawnClientType type = CLIENT_FOR_APPSPAWN;\n'
            '    if (strcmp(serviceName, APPSPAWNX_SERVER_NAME) == 0) {  // adapter project\n'
            '        type = CLIENT_FOR_APPSPAWNX;\n'
            '    } else if (strcmp(serviceName, CJAPPSPAWN_SERVER_NAME) == 0) {\n'
        ),
        label="appspawn_client.c AppSpawnClientInit",
    )

    # 4. ability_runtime app_spawn_client.cpp — add APPSPAWNX_SERVER_NAME branch
    patch_file(
        path=f"{OH}/foundation/ability/ability_runtime/services/appmgr/src/app_spawn_client.cpp",
        marker='APPSPAWNX_SERVER_NAME',
        anchor_old=(
            '    } else if (serviceName__ == HYBRIDSPAWN_SERVER_NAME) {\n'
            '        serviceName_ = HYBRIDSPAWN_SERVER_NAME;\n'
            '    } else {\n'
        ),
        anchor_new=(
            '    } else if (serviceName__ == HYBRIDSPAWN_SERVER_NAME) {\n'
            '        serviceName_ = HYBRIDSPAWN_SERVER_NAME;\n'
            '    } else if (serviceName__ == APPSPAWNX_SERVER_NAME) {  // adapter project\n'
            '        serviceName_ = APPSPAWNX_SERVER_NAME;\n'
            '    } else {\n'
        ),
        label="app_spawn_client.cpp ctor",
    )

    print("\n[done] All 4 source files patched. Rebuild libappspawn_client + libappkit + foundation libs.")


if __name__ == "__main__":
    main()
