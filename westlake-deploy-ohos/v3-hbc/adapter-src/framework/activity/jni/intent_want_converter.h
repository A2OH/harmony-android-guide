/*
 * intent_want_converter.h
 *
 * C++ layer Want parameter structure definition.
 * Fields extracted by Java-layer IntentWantConverter are passed here via JNI.
 */
#ifndef INTENT_WANT_CONVERTER_H
#define INTENT_WANT_CONVERTER_H

#include <string>

namespace oh_adapter {

/**
 * Want parameters (converted from Android Intent).
 * Maps to core fields of OH OHOS::AAFwk::Want.
 */
struct WantParams {
    std::string bundleName;      // OH Want.element.bundleName
    std::string abilityName;     // OH Want.element.abilityName
    std::string action;          // OH Want.action
    std::string uri;             // OH Want.uri
    std::string parametersJson;  // OH Want.parameters (serialized as JSON)
};

}  // namespace oh_adapter

#endif  // INTENT_WANT_CONVERTER_H
