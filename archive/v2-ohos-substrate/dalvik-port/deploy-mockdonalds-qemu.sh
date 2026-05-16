#!/bin/bash
# Deploy and run MockDonalds on OHOS QEMU ARM32
# Usage: ./deploy-mockdonalds-qemu.sh [timeout_seconds]
#
# Prerequisites:
#   - OHOS QEMU min build images at ~/openharmony/out/qemu-arm-linux/packages/phone/images/
#   - dalvikvm ARM32 built: make TARGET=ohos-arm32
#   - MockDonalds DEX built: /tmp/mockdonalds.dex

set -e
TIMEOUT=${1:-90}

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
OH_ROOT=/home/dspfac/openharmony

DALVIK_VM=$SCRIPT_DIR/build-ohos-arm32/dalvikvm
DEXOPT=$SCRIPT_DIR/build-ohos-arm32/dexopt
CORE_JAR=$SCRIPT_DIR/core-android-x86.jar
MOCKDONALDS_DEX=/tmp/mockdonalds.dex
HELLO_DEX=$SCRIPT_DIR/hello.dex

IMAGES=$OH_ROOT/out/qemu-arm-linux/packages/phone/images
QEMU=$OH_ROOT/tools/qemu-extracted/usr/bin/qemu-system-arm
QEMU_LIB=$OH_ROOT/tools/qemu-extracted/usr/lib/x86_64-linux-gnu
QEMU_SHARE=$OH_ROOT/tools/qemu-extracted/usr/share/qemu
MKE2FS=$OH_ROOT/out/qemu-arm-linux/clang_x64/thirdparty/e2fsprogs/mke2fs

# Check prerequisites
for f in "$DALVIK_VM" "$CORE_JAR" "$MOCKDONALDS_DEX" "$QEMU" "$IMAGES/zImage-dtb"; do
    [ -f "$f" ] || { echo "ERROR: $f not found"; exit 1; }
done

echo "=== Preparing userdata.img ==="
USERDATA=$IMAGES/userdata.img
$MKE2FS -t ext4 -b 4096 -I 256 -O ^64bit,^metadata_csum -L userdata $USERDATA 25600 2>/dev/null

debugfs -w $USERDATA << CMDS 2>/dev/null
mkdir a2oh
mkdir dalvik-cache
mkdir android-data
mkdir android-data/dalvik-cache
mkdir android-root
mkdir android-root/bin
write $DALVIK_VM a2oh/dalvikvm
write $DEXOPT a2oh/dexopt
write $CORE_JAR a2oh/core.jar
write $HELLO_DEX a2oh/hello.dex
write $MOCKDONALDS_DEX a2oh/mockdonalds.dex
write $DEXOPT android-root/bin/dexopt
CMDS
echo "Files injected into userdata.img"

echo "=== Booting QEMU (${TIMEOUT}s timeout) ==="
pkill -9 -f qemu-system-arm 2>/dev/null; sleep 1

# Start QEMU in tmux
tmux kill-session -t qemu 2>/dev/null
tmux new-session -d -s qemu "LD_LIBRARY_PATH=$QEMU_LIB $QEMU -M virt -cpu cortex-a7 -smp 4 -m 1024 -nographic -L $QEMU_SHARE \
  -drive if=none,file=$IMAGES/userdata.img,format=raw,id=ud -device virtio-blk-device,drive=ud \
  -drive if=none,file=$IMAGES/vendor.img,format=raw,id=vd -device virtio-blk-device,drive=vd \
  -drive if=none,file=$IMAGES/system.img,format=raw,id=sd -device virtio-blk-device,drive=sd \
  -drive if=none,file=$IMAGES/updater.img,format=raw,id=up -device virtio-blk-device,drive=up \
  -kernel $IMAGES/zImage-dtb -initrd $IMAGES/ramdisk.img \
  -append 'console=ttyAMA0,115200 init=/bin/init hardware=qemu.arm.linux default_boot_device=a003e00.virtio_mmio root=/dev/ram0 rw ohos.required_mount.system=/dev/block/vdb@/usr@ext4@ro,barrier=1@wait,required ohos.required_mount.vendor=/dev/block/vdc@/vendor@ext4@ro,barrier=1@wait,required ohos.required_mount.data=/dev/block/vdd@/data@ext4@nosuid,nodev,noatime,barrier=1@wait,required'"

echo "Waiting for boot..."
sleep 25

# Get shell
tmux send-keys -t qemu '' Enter
sleep 3

# Setup
tmux send-keys -t qemu 'chmod 755 /data/a2oh/* /data/android-root/bin/*' Enter
sleep 1
tmux send-keys -t qemu 'export ANDROID_DATA=/data/android-data ANDROID_ROOT=/data/android-root' Enter
sleep 1

# Run the requested DEX
DEX_NAME="${2:-hello}"
if [ "$DEX_NAME" = "mockdonalds" ]; then
    echo "=== Running MockDonalds ==="
    tmux send-keys -t qemu '/data/a2oh/dalvikvm -Xverify:none -Xdexopt:none -Xbootclasspath:/data/a2oh/core.jar:/data/a2oh/mockdonalds.dex -classpath /data/a2oh/mockdonalds.dex MockDonaldsRunner 2>/dev/null' Enter
else
    echo "=== Running Hello World ==="
    tmux send-keys -t qemu '/data/a2oh/dalvikvm -Xverify:none -Xdexopt:none -Xbootclasspath:/data/a2oh/core.jar:/data/a2oh/hello.dex -classpath /data/a2oh/hello.dex Hello 2>/dev/null' Enter
fi

echo "Waiting for execution..."
sleep 30

# Capture results
tmux capture-pane -t qemu -p -S -50 > /tmp/qemu_result.txt
echo ""
echo "=== OUTPUT ==="
grep -v "^\[" /tmp/qemu_result.txt | grep -v "^$" | tail -20

# Cleanup
tmux kill-session -t qemu 2>/dev/null
echo ""
echo "=== Done ==="
