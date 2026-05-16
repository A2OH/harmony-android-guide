/*
 * intent_want_converter.cpp
 *
 * C++ layer Want construction helper.
 * Currently Want parameters are converted in Java layer and passed via JNI.
 * This file is reserved for future direct OH Want object construction in C++.
 */
#include "intent_want_converter.h"

namespace oh_adapter {

// Reserved: direct OH Want object construction for later phases
// OHOS::AAFwk::Want convertToOHWant(const WantParams& params) {
//     OHOS::AAFwk::Want want;
//     OHOS::AppExecFwk::ElementName element("", params.bundleName, params.abilityName);
//     want.SetElement(element);
//     want.SetAction(params.action);
//     want.SetUri(params.uri);
//     return want;
// }

}  // namespace oh_adapter
