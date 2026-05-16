#!/bin/bash
# Build libjavacore.so stub for Dalvik VM boot
JNI=/home/dspfac/aosp-android-11/libnativehelper/include_jni
gcc -shared -fPIC -o libjavacore.so -I"$JNI" javacore_stub.c
echo "Built libjavacore.so ($(ls -lh libjavacore.so | awk '{print $5}'))"
