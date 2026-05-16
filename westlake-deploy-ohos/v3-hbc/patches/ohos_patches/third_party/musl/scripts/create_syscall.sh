#!/usr/bin/env sh
# Modified create_syscall.sh - generates bits/syscall.h from syscall.h.in
# with aarch64 time64 compatibility aliases appended unconditionally.
#
# Original: copies syscall.h.in + sed __NR_ -> SYS_
# Modified: also appends time64 aliases needed by musl's src/internal/syscall.h
#
# Why: musl's syscall.h redirects SYS_futex -> SYS_futex_time64 (for 32-bit time),
# but aarch64 kernel headers don't define time64 variants (not needed on 64-bit).
# GN build ordering issue: create_porting_src may not copy patched syscall.h.in
# before this script runs, so we unconditionally append the aliases.

if [ $# -ne 2 ]; then
    echo "$0 need 2 parameters, but the actual number is $#, please check!"
    exit 1
fi

rm -f $2

cp $1 $2

sed  -n -e s/__NR_/SYS_/p < $1 >> $2

# aarch64 time64 compatibility aliases (unconditional, guarded by #ifndef)
cat >> $2 << 'TIME64'

/* aarch64 time64 aliases (auto-appended by create_syscall.sh) */
#ifndef SYS_futex_time64
#define __NR_futex_time64            __NR_futex
#define __NR_ppoll_time64            __NR_ppoll
#define __NR_clock_gettime64         __NR_clock_gettime
#define __NR_clock_settime64         __NR_clock_settime
#define __NR_clock_adjtime64         __NR_clock_adjtime
#define __NR_clock_getres_time64     __NR_clock_getres
#define __NR_clock_nanosleep_time64  __NR_clock_nanosleep
#define __NR_timer_gettime64         __NR_timer_gettime
#define __NR_timer_settime64         __NR_timer_settime
#define __NR_timerfd_gettime64       __NR_timerfd_gettime
#define __NR_timerfd_settime64       __NR_timerfd_settime
#define __NR_utimensat_time64        __NR_utimensat
#define __NR_recvmmsg_time64         __NR_recvmmsg
#define __NR_mq_timedsend_time64     __NR_mq_timedsend
#define __NR_mq_timedreceive_time64  __NR_mq_timedreceive
#define __NR_rt_sigtimedwait_time64  __NR_rt_sigtimedwait
#define __NR_sched_rr_get_interval_time64  __NR_sched_rr_get_interval
#define SYS_futex_time64             __NR_futex
#define SYS_ppoll_time64             __NR_ppoll
#define SYS_clock_gettime64          __NR_clock_gettime
#define SYS_clock_settime64          __NR_clock_settime
#define SYS_clock_adjtime64          __NR_clock_adjtime
#define SYS_clock_getres_time64      __NR_clock_getres
#define SYS_clock_nanosleep_time64   __NR_clock_nanosleep
#define SYS_timer_gettime64          __NR_timer_gettime
#define SYS_timer_settime64          __NR_timer_settime
#define SYS_timerfd_gettime64        __NR_timerfd_gettime
#define SYS_timerfd_settime64        __NR_timerfd_settime
#define SYS_utimensat_time64         __NR_utimensat
#define SYS_recvmmsg_time64          __NR_recvmmsg
#define SYS_mq_timedsend_time64      __NR_mq_timedsend
#define SYS_mq_timedreceive_time64   __NR_mq_timedreceive
#define SYS_rt_sigtimedwait_time64   __NR_rt_sigtimedwait
#define SYS_sched_rr_get_interval_time64  __NR_sched_rr_get_interval
#endif
TIME64
