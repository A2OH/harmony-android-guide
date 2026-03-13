/*
 * RAII wrapper for pthread_mutex_t.
 */
#ifndef SCOPED_PTHREAD_MUTEX_LOCK_H
#define SCOPED_PTHREAD_MUTEX_LOCK_H

#include <pthread.h>

class ScopedPthreadMutexLock {
public:
    explicit ScopedPthreadMutexLock(pthread_mutex_t* mutex) : mutex_(mutex) {
        pthread_mutex_lock(mutex_);
    }
    ~ScopedPthreadMutexLock() {
        pthread_mutex_unlock(mutex_);
    }
private:
    pthread_mutex_t* mutex_;
    ScopedPthreadMutexLock(const ScopedPthreadMutexLock&) = delete;
    ScopedPthreadMutexLock& operator=(const ScopedPthreadMutexLock&) = delete;
};

#endif
