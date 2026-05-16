// Stub: incfs_support/util.h
// On OH (non-bionic), IncFS utilities are provided as simple inlines.
#pragma once

#include <utility>

namespace incfs {
namespace util {

template <typename Container>
inline void clearAndFree(Container& c) {
    Container().swap(c);
}

}  // namespace util
}  // namespace incfs
