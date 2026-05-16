/*
 * lifecycle_state_mapper.h
 *
 * C++ layer lifecycle state mapping (C++ counterpart of Java LifecycleAdapter).
 */
#ifndef LIFECYCLE_STATE_MAPPER_H
#define LIFECYCLE_STATE_MAPPER_H

namespace oh_adapter {

// OH Ability lifecycle states
enum OHLifecycleState {
    OH_LIFECYCLE_INITIAL = 0,
    OH_LIFECYCLE_INACTIVE = 1,
    OH_LIFECYCLE_FOREGROUND = 2,
    OH_LIFECYCLE_BACKGROUND = 4,
};

// Android Activity lifecycle states
enum AndroidLifecycleState {
    ANDROID_LIFECYCLE_CREATED = 1,
    ANDROID_LIFECYCLE_STARTED = 2,
    ANDROID_LIFECYCLE_RESUMED = 3,
    ANDROID_LIFECYCLE_PAUSED = 4,
    ANDROID_LIFECYCLE_STOPPED = 5,
    ANDROID_LIFECYCLE_DESTROYED = 6,
};

/**
 * Map OH state to Android state.
 */
inline AndroidLifecycleState mapOHToAndroid(OHLifecycleState ohState) {
    switch (ohState) {
        case OH_LIFECYCLE_INACTIVE:   return ANDROID_LIFECYCLE_STARTED;
        case OH_LIFECYCLE_FOREGROUND: return ANDROID_LIFECYCLE_RESUMED;
        case OH_LIFECYCLE_BACKGROUND: return ANDROID_LIFECYCLE_STOPPED;
        case OH_LIFECYCLE_INITIAL:    return ANDROID_LIFECYCLE_DESTROYED;
        default:                      return ANDROID_LIFECYCLE_DESTROYED;
    }
}

/**
 * Map Android state to OH state.
 */
inline OHLifecycleState mapAndroidToOH(AndroidLifecycleState androidState) {
    switch (androidState) {
        case ANDROID_LIFECYCLE_CREATED:
        case ANDROID_LIFECYCLE_STARTED:   return OH_LIFECYCLE_INACTIVE;
        case ANDROID_LIFECYCLE_RESUMED:   return OH_LIFECYCLE_FOREGROUND;
        case ANDROID_LIFECYCLE_PAUSED:    return OH_LIFECYCLE_FOREGROUND;
        case ANDROID_LIFECYCLE_STOPPED:   return OH_LIFECYCLE_BACKGROUND;
        case ANDROID_LIFECYCLE_DESTROYED: return OH_LIFECYCLE_INITIAL;
        default:                          return OH_LIFECYCLE_INITIAL;
    }
}

}  // namespace oh_adapter

#endif  // LIFECYCLE_STATE_MAPPER_H
