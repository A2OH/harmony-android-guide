#!/bin/bash
# Self-contained QEMU MockDonalds test
set -x

OH_ROOT=/home/dspfac/openharmony
IMAGES=$OH_ROOT/out/qemu-arm-linux/packages/phone/images
QEMU=$OH_ROOT/tools/qemu-extracted/usr/bin/qemu-system-arm
QEMU_SHARE=$OH_ROOT/tools/qemu-extracted/usr/share/qemu
export LD_LIBRARY_PATH=$OH_ROOT/tools/qemu-extracted/usr/lib/x86_64-linux-gnu

pkill -9 -f qemu-system-arm 2>/dev/null
sleep 1

rm -f /tmp/qemu_pipe /tmp/qemu_result.log
mkfifo /tmp/qemu_pipe

# Start QEMU in background
$QEMU -M virt -cpu cortex-a7 -smp 4 -m 1024 -nographic -L $QEMU_SHARE \
  -drive if=none,file=$IMAGES/userdata.img,format=raw,id=ud -device virtio-blk-device,drive=ud \
  -drive if=none,file=$IMAGES/vendor.img,format=raw,id=vd -device virtio-blk-device,drive=vd \
  -drive if=none,file=$IMAGES/system.img,format=raw,id=sd -device virtio-blk-device,drive=sd \
  -drive if=none,file=$IMAGES/updater.img,format=raw,id=up -device virtio-blk-device,drive=up \
  -kernel $IMAGES/zImage-dtb -initrd $IMAGES/ramdisk.img \
  -append 'console=ttyAMA0,115200 init=/bin/init hardware=qemu.arm.linux default_boot_device=a003e00.virtio_mmio root=/dev/ram0 rw ohos.required_mount.system=/dev/block/vdb@/usr@ext4@ro,barrier=1@wait,required ohos.required_mount.vendor=/dev/block/vdc@/vendor@ext4@ro,barrier=1@wait,required ohos.required_mount.data=/dev/block/vdd@/data@ext4@nosuid,nodev,noatime,barrier=1@wait,required' \
  < /tmp/qemu_pipe > /tmp/qemu_result.log 2>&1 &
QEMU_PID=$!

# Open pipe for writing (keep it open with fd 3)
exec 3>/tmp/qemu_pipe

echo "QEMU PID: $QEMU_PID, waiting 28s for boot..."
sleep 28

# Send commands
echo "" >&3
sleep 3
echo "chmod 755 /data/a2oh/* /data/android-root/bin/*" >&3
sleep 1
echo "export ANDROID_DATA=/data/android-data ANDROID_ROOT=/data/android-root LD_LIBRARY_PATH=/data/a2oh" >&3
sleep 1
echo "sh /data/a2oh/run.sh" >&3

echo "Waiting 60s for execution..."
sleep 60

# Close pipe
exec 3>&-

echo ""
echo "=== QEMU Result Log ==="
echo "Lines: $(wc -l < /tmp/qemu_result.log)"
echo ""
echo "=== Key output ==="
grep -E "PASS|FAIL|EXCEPTION|Results|ALL TESTS|MockDonalds|NoClass|creating VM|Hello|Done|Error|signal|abort" /tmp/qemu_result.log || echo "(no matches)"
echo ""
echo "=== Non-kernel output ==="
grep -v "^\[" /tmp/qemu_result.log | grep -v "^$" | tail -30

# Cleanup
kill $QEMU_PID 2>/dev/null
rm -f /tmp/qemu_pipe
