#!/bin/bash
# Run MockDonalds on OHOS QEMU ARM32
set -e

OH_ROOT=/home/dspfac/openharmony
DALVIK_PORT=/home/dspfac/android-to-openharmony-migration/dalvik-port
IMAGES=$OH_ROOT/out/qemu-arm-linux/packages/phone/images
QEMU=$OH_ROOT/tools/qemu-extracted/usr/bin/qemu-system-arm
QEMU_LIB=$OH_ROOT/tools/qemu-extracted/usr/lib/x86_64-linux-gnu
QEMU_SHARE=$OH_ROOT/tools/qemu-extracted/usr/share/qemu

# Kill previous
pkill -9 -f qemu-system-arm 2>/dev/null || true
sleep 1

# Build command sequence to send to QEMU console after boot
cat > /tmp/qemu_commands.sh << 'CMDS'
sleep 25
echo ""
sleep 3
echo "chmod 755 /data/a2oh/*"
sleep 1
echo "export ANDROID_DATA=/data/android-data ANDROID_ROOT=/data/android-root"
sleep 1
echo "/data/a2oh/dalvikvm -Xverify:none -Xdexopt:none -Xbootclasspath:/data/a2oh/core.jar:/data/a2oh/mockdonalds.dex -classpath /data/a2oh/mockdonalds.dex MockDonaldsRunner 2>/dev/null"
sleep 45
echo ""
CMDS

# Run QEMU with commands piped in, capture output
echo "=== Starting OHOS QEMU + MockDonalds ==="
(bash /tmp/qemu_commands.sh | LD_LIBRARY_PATH=$QEMU_LIB $QEMU -M virt -cpu cortex-a7 -smp 4 -m 1024 -nographic -L $QEMU_SHARE \
  -drive if=none,file=$IMAGES/userdata.img,format=raw,id=ud -device virtio-blk-device,drive=ud \
  -drive if=none,file=$IMAGES/vendor.img,format=raw,id=vd -device virtio-blk-device,drive=vd \
  -drive if=none,file=$IMAGES/system.img,format=raw,id=sd -device virtio-blk-device,drive=sd \
  -drive if=none,file=$IMAGES/updater.img,format=raw,id=up -device virtio-blk-device,drive=up \
  -kernel $IMAGES/zImage-dtb -initrd $IMAGES/ramdisk.img \
  -append 'console=ttyAMA0,115200 init=/bin/init hardware=qemu.arm.linux default_boot_device=a003e00.virtio_mmio root=/dev/ram0 rw ohos.required_mount.system=/dev/block/vdb@/usr@ext4@ro,barrier=1@wait,required ohos.required_mount.vendor=/dev/block/vdc@/vendor@ext4@ro,barrier=1@wait,required ohos.required_mount.data=/dev/block/vdd@/data@ext4@nosuid,nodev,noatime,barrier=1@wait,required') 2>&1 | tee /tmp/qemu_mockdonalds.log &

QEMU_BG=$!
echo "QEMU background PID: $QEMU_BG"

# Wait for execution
sleep 80

echo ""
echo "=== MockDonalds Results ==="
grep -E "\[PASS\]|\[FAIL\]|EXCEPTION|Results|ALL TESTS|SOME TESTS|Hello|Error|NoClassDef|signal|CRASH" /tmp/qemu_mockdonalds.log 2>/dev/null || echo "(no matching output found)"

# Cleanup
kill $QEMU_BG 2>/dev/null
pkill -9 -f qemu-system-arm 2>/dev/null || true
echo "=== Done ==="
