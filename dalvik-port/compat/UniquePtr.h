/*
 * UniquePtr — KitKat used a custom smart pointer, replaced by std::unique_ptr.
 */
#ifndef UNIQUE_PTR_H
#define UNIQUE_PTR_H

#include <memory>

/* KitKat's UniquePtr is essentially std::unique_ptr with a different default deleter */
template <typename T, typename D = std::default_delete<T>>
using UniquePtr = std::unique_ptr<T, D>;

/* For array types */
template <typename T>
struct FreeDelete {
    void operator()(T* p) const { free(p); }
};

#endif
