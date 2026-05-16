// Stub: incfs_support/signal_handling.h
// On OH (non-bionic), SIGBUS handling for IncFS is not needed.
#pragma once

#define SCOPED_SIGBUS_HANDLER(code)
#define SCOPED_SIGBUS_HANDLER_CONDITIONAL(condition, code)

namespace incfs {
inline void enableSignalHandling() {}
}  // namespace incfs
