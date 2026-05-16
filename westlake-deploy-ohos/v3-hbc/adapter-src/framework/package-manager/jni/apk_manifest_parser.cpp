/*
 * apk_manifest_parser.cpp
 *
 * Parser for AndroidManifest.xml in AXML binary format inside APK files.
 *
 * Uses adapter-internal AxmlParser (no libandroidfw dependency). See
 * axml_parser.h for scope limits. The APK is opened as a ZIP archive using
 * minizip/unzip.h (statically baked into libapk_installer.so).
 */
#include "apk_manifest_parser.h"
#include "axml_parser.h"

#include <cstring>
#include <memory>
#include <unzip.h>  // minizip (statically compiled into libapk_installer.so)

#include "hilog/log.h"

namespace oh_adapter {

namespace {

#undef LOG_DOMAIN
#undef LOG_TAG
constexpr unsigned int LOG_DOMAIN = 0xD001800;
constexpr const char* LOG_TAG = "ApkManifestParser";

#define LOGI(...) OHOS::HiviewDFX::HiLog::Info({LOG_CORE, LOG_DOMAIN, LOG_TAG}, __VA_ARGS__)
#define LOGW(...) OHOS::HiviewDFX::HiLog::Warn({LOG_CORE, LOG_DOMAIN, LOG_TAG}, __VA_ARGS__)
#define LOGE(...) OHOS::HiviewDFX::HiLog::Error({LOG_CORE, LOG_DOMAIN, LOG_TAG}, __VA_ARGS__)

// Android resource IDs for manifest attributes (from frameworks/base/core/res/res/values/public.xml)
constexpr uint32_t ATTR_NAME              = 0x01010003;
constexpr uint32_t ATTR_LABEL             = 0x01010001;
constexpr uint32_t ATTR_ICON              = 0x01010002;
constexpr uint32_t ATTR_VERSION_CODE      = 0x0101021b;
constexpr uint32_t ATTR_VERSION_NAME      = 0x0101021c;
constexpr uint32_t ATTR_MIN_SDK_VERSION   = 0x0101020c;
constexpr uint32_t ATTR_TARGET_SDK_VERSION = 0x01010270;
constexpr uint32_t ATTR_SHARED_USER_ID    = 0x01010005;
constexpr uint32_t ATTR_DEBUGGABLE        = 0x0101000f;
constexpr uint32_t ATTR_EXPORTED          = 0x01010010;
constexpr uint32_t ATTR_LAUNCH_MODE       = 0x0101001d;
constexpr uint32_t ATTR_SCREEN_ORIENTATION = 0x0101001e;
constexpr uint32_t ATTR_PERMISSION        = 0x01010006;
constexpr uint32_t ATTR_TASK_AFFINITY     = 0x01010012;
constexpr uint32_t ATTR_THEME             = 0x01010000;
constexpr uint32_t ATTR_AUTHORITIES       = 0x01010018;
constexpr uint32_t ATTR_READ_PERMISSION   = 0x01010007;
constexpr uint32_t ATTR_WRITE_PERMISSION  = 0x01010008;
constexpr uint32_t ATTR_SCHEME            = 0x010100c7;
constexpr uint32_t ATTR_HOST              = 0x010100c8;
constexpr uint32_t ATTR_PATH              = 0x010100c9;
constexpr uint32_t ATTR_MIME_TYPE         = 0x01010026;
constexpr uint32_t ATTR_EXTRACT_NATIVE_LIBS = 0x010104ea;
constexpr uint32_t ATTR_PROTECTION_LEVEL  = 0x01010009;
// P2 (2026-04-30) — Application + Provider extras for AppBindData per
// ability_manager_ipc_adapter_design §1.1.4
constexpr uint32_t ATTR_PROCESS                  = 0x01010011;  // android:process
constexpr uint32_t ATTR_LARGE_HEAP               = 0x0101023a;  // android:largeHeap
constexpr uint32_t ATTR_ALLOW_BACKUP             = 0x01010280;  // android:allowBackup
constexpr uint32_t ATTR_PERSISTENT               = 0x0101000d;  // android:persistent
constexpr uint32_t ATTR_HW_ACCELERATED           = 0x010102d3;  // android:hardwareAccelerated
constexpr uint32_t ATTR_APP_COMPONENT_FACTORY    = 0x0101057a;  // android:appComponentFactory
constexpr uint32_t ATTR_CLASS_LOADER             = 0x01010512;  // android:classLoader
constexpr uint32_t ATTR_NETWORK_SECURITY_CONFIG  = 0x01010527;  // android:networkSecurityConfig
constexpr uint32_t ATTR_GRANT_URI_PERMISSIONS    = 0x0101001b;  // android:grantUriPermissions
constexpr uint32_t ATTR_MULTIPROCESS             = 0x01010013;  // android:multiprocess
constexpr uint32_t ATTR_INIT_ORDER               = 0x0101001a;  // android:initOrder
constexpr uint32_t ATTR_USES_LIBRARY_NAME        = 0x01010003;  // <uses-library android:name> (same as ATTR_NAME)

// Helper to get string attribute (UTF-8 already from AxmlParser).
std::string GetStringAttr(const AxmlParser& parser, size_t attrIndex) {
    size_t len = 0;
    const char* s = parser.getAttributeStringValue(attrIndex, &len);
    if (s != nullptr && len > 0) return std::string(s, len);
    return "";
}

// Helper to get int attribute.
int32_t GetIntAttr(const AxmlParser& parser, size_t attrIndex, int32_t defValue) {
    ResValue value;
    if (parser.getAttributeValue(attrIndex, &value) >= 0) {
        if (value.dataType == ResValue::TYPE_INT_DEC ||
            value.dataType == ResValue::TYPE_INT_HEX) {
            return static_cast<int32_t>(value.data);
        }
    }
    return defValue;
}

// Helper to get bool attribute.
bool GetBoolAttr(const AxmlParser& parser, size_t attrIndex, bool defValue) {
    ResValue value;
    if (parser.getAttributeValue(attrIndex, &value) >= 0) {
        if (value.dataType == ResValue::TYPE_INT_BOOLEAN) {
            return value.data != 0;
        }
    }
    return defValue;
}

// Find attribute index by resource ID.
ssize_t FindAttrByResId(const AxmlParser& parser, uint32_t resId) {
    size_t attrCount = parser.getAttributeCount();
    for (size_t i = 0; i < attrCount; i++) {
        if (parser.getAttributeNameResID(i) == resId) {
            return static_cast<ssize_t>(i);
        }
    }
    return -1;
}

// Extract a ZIP entry to memory
bool ExtractZipEntry(const std::string& zipPath, const std::string& entryName,
                     std::vector<uint8_t>& outData) {
    unzFile zip = unzOpen(zipPath.c_str());
    if (zip == nullptr) {
        LOGE("Failed to open ZIP: %{public}s", zipPath.c_str());
        return false;
    }

    if (unzLocateFile(zip, entryName.c_str(), 0) != UNZ_OK) {
        LOGE("Entry not found in ZIP: %{public}s", entryName.c_str());
        unzClose(zip);
        return false;
    }

    unz_file_info fileInfo;
    if (unzGetCurrentFileInfo(zip, &fileInfo, nullptr, 0, nullptr, 0, nullptr, 0) != UNZ_OK) {
        unzClose(zip);
        return false;
    }

    if (unzOpenCurrentFile(zip) != UNZ_OK) {
        unzClose(zip);
        return false;
    }

    outData.resize(fileInfo.uncompressed_size);
    int bytesRead = unzReadCurrentFile(zip, outData.data(), outData.size());
    unzCloseCurrentFile(zip);
    unzClose(zip);

    if (bytesRead < 0 || static_cast<size_t>(bytesRead) != outData.size()) {
        LOGE("Failed to read ZIP entry: expected %{public}zu, got %{public}d",
             outData.size(), bytesRead);
        return false;
    }

    return true;
}

}  // namespace

std::string ApkManifestParser::ResolveClassName(const std::string& name,
                                                 const std::string& packageName) {
    if (name.empty()) return name;
    if (name[0] == '.') {
        // Relative: ".MainActivity" -> "com.example.MainActivity"
        return packageName + name;
    }
    if (name.find('.') == std::string::npos) {
        // Simple name: "MainActivity" -> "com.example.MainActivity"
        return packageName + "." + name;
    }
    // Already fully qualified
    return name;
}

bool ApkManifestParser::Parse(const std::string& apkPath, ManifestData& outData) {
    LOGI("Parsing APK manifest: %{public}s", apkPath.c_str());

    // 1. Extract AndroidManifest.xml from APK
    std::vector<uint8_t> manifestData;
    if (!ExtractZipEntry(apkPath, "AndroidManifest.xml", manifestData)) {
        LOGE("Failed to extract AndroidManifest.xml from APK");
        return false;
    }

    // 2. Parse AXML binary format
    if (!ParseAXML(manifestData.data(), manifestData.size(), outData)) {
        LOGE("Failed to parse AXML data");
        return false;
    }

    LOGI("Parsed APK: package=%{public}s, versionCode=%{public}d, "
         "activities=%{public}zu, services=%{public}zu, receivers=%{public}zu, providers=%{public}zu",
         outData.packageName.c_str(), outData.versionCode,
         outData.activities.size(), outData.services.size(),
         outData.receivers.size(), outData.providers.size());

    return true;
}

bool ApkManifestParser::ParseAXML(const uint8_t* data, size_t size, ManifestData& outData) {
    AxmlParser tree;
    if (tree.setTo(data, size) != 0) {
        LOGE("AxmlParser::setTo failed");
        return false;
    }

    // Parsing state machine
    enum class State {
        NONE,
        IN_MANIFEST,
        IN_APPLICATION,
        IN_ACTIVITY,
        IN_SERVICE,
        IN_RECEIVER,
        IN_PROVIDER,
        IN_INTENT_FILTER,
        IN_USES_SDK,
        IN_USES_PERMISSION,
        IN_PERMISSION,
    };

    State state = State::NONE;
    // Stack to track nested elements
    std::vector<State> stateStack;

    // Current component being parsed
    ActivityData currentActivity;
    ServiceData currentService;
    ReceiverData currentReceiver;
    ProviderData currentProvider;
    IntentFilterData currentFilter;
    IntentFilterData* activeFilter = nullptr;  // Points to the filter being populated

    AxmlParser::EventCode event;
    while ((event = tree.next()) != AxmlParser::EC_END_DOCUMENT) {
        if (event == AxmlParser::EC_BAD_DOCUMENT) {
            LOGE("Bad AXML document");
            return false;
        }

        if (event == AxmlParser::EC_START_TAG) {
            size_t nameLen = 0;
            const char* name = tree.getElementName(&nameLen);
            if (name == nullptr) continue;
            std::string elemName(name, nameLen);

            stateStack.push_back(state);

            if (elemName == "manifest") {
                state = State::IN_MANIFEST;
                // Parse manifest attributes
                ssize_t idx;
                if ((idx = FindAttrByResId(tree, ATTR_NAME)) >= 0) {
                    // package is stored as "name" in some cases, but typically
                    // it's a separate attribute. Try the dedicated package attr.
                }
                // The "package" attribute doesn't have a resource ID;
                // search by name string
                size_t attrCount = tree.getAttributeCount();
                for (size_t i = 0; i < attrCount; i++) {
                    size_t attrNameLen = 0;
                    const char* attrName = tree.getAttributeName(i, &attrNameLen);
                    if (attrName == nullptr) continue;
                    if (std::string(attrName, attrNameLen) == "package") {
                        outData.packageName = GetStringAttr(tree, i);
                    }
                }
                if ((idx = FindAttrByResId(tree, ATTR_VERSION_CODE)) >= 0) {
                    outData.versionCode = GetIntAttr(tree, idx, 0);
                }
                if ((idx = FindAttrByResId(tree, ATTR_VERSION_NAME)) >= 0) {
                    outData.versionName = GetStringAttr(tree, idx);
                }
                if ((idx = FindAttrByResId(tree, ATTR_SHARED_USER_ID)) >= 0) {
                    outData.sharedUserId = GetStringAttr(tree, idx);
                }
            } else if (elemName == "uses-sdk") {
                state = State::IN_USES_SDK;
                ssize_t idx;
                if ((idx = FindAttrByResId(tree, ATTR_MIN_SDK_VERSION)) >= 0) {
                    outData.minSdkVersion = GetIntAttr(tree, idx, 0);
                }
                if ((idx = FindAttrByResId(tree, ATTR_TARGET_SDK_VERSION)) >= 0) {
                    outData.targetSdkVersion = GetIntAttr(tree, idx, 0);
                }
            } else if (elemName == "application") {
                state = State::IN_APPLICATION;
                ssize_t idx;
                if ((idx = FindAttrByResId(tree, ATTR_NAME)) >= 0) {
                    outData.appClassName = ResolveClassName(
                        GetStringAttr(tree, idx), outData.packageName);
                }
                if ((idx = FindAttrByResId(tree, ATTR_LABEL)) >= 0) {
                    outData.appLabel = GetStringAttr(tree, idx);
                }
                if ((idx = FindAttrByResId(tree, ATTR_DEBUGGABLE)) >= 0) {
                    outData.debuggable = GetBoolAttr(tree, idx, false);
                }
                if ((idx = FindAttrByResId(tree, ATTR_EXTRACT_NATIVE_LIBS)) >= 0) {
                    outData.extractNativeLibs = GetBoolAttr(tree, idx, true);
                }
                // P2 fields per ability_manager_ipc_adapter_design §1.1.4
                if ((idx = FindAttrByResId(tree, ATTR_THEME)) >= 0) {
                    outData.appTheme = GetIntAttr(tree, idx, 0);
                }
                if ((idx = FindAttrByResId(tree, ATTR_PROCESS)) >= 0) {
                    outData.appProcessName = GetStringAttr(tree, idx);
                }
                if ((idx = FindAttrByResId(tree, ATTR_APP_COMPONENT_FACTORY)) >= 0) {
                    outData.appComponentFactory = ResolveClassName(
                        GetStringAttr(tree, idx), outData.packageName);
                }
                if ((idx = FindAttrByResId(tree, ATTR_CLASS_LOADER)) >= 0) {
                    outData.classLoaderName = GetStringAttr(tree, idx);
                }
                if ((idx = FindAttrByResId(tree, ATTR_NETWORK_SECURITY_CONFIG)) >= 0) {
                    outData.networkSecurityConfigRes = GetIntAttr(tree, idx, 0);
                }
                if ((idx = FindAttrByResId(tree, ATTR_LARGE_HEAP)) >= 0) {
                    outData.largeHeap = GetBoolAttr(tree, idx, false);
                }
                if ((idx = FindAttrByResId(tree, ATTR_ALLOW_BACKUP)) >= 0) {
                    outData.allowBackup = GetBoolAttr(tree, idx, true);
                }
                if ((idx = FindAttrByResId(tree, ATTR_PERSISTENT)) >= 0) {
                    outData.persistent = GetBoolAttr(tree, idx, false);
                }
                if ((idx = FindAttrByResId(tree, ATTR_HW_ACCELERATED)) >= 0) {
                    outData.hardwareAccelerated = GetBoolAttr(tree, idx, true);
                }
            } else if (elemName == "uses-library" && state == State::IN_APPLICATION) {
                ssize_t idx = FindAttrByResId(tree, ATTR_NAME);
                if (idx >= 0) {
                    std::string libName = GetStringAttr(tree, idx);
                    if (!libName.empty()) {
                        outData.sharedLibraryFiles.push_back(libName);
                    }
                }
            } else if (elemName == "activity" || elemName == "activity-alias") {
                state = State::IN_ACTIVITY;
                currentActivity = ActivityData();
                ssize_t idx;
                if ((idx = FindAttrByResId(tree, ATTR_NAME)) >= 0) {
                    currentActivity.name = ResolveClassName(
                        GetStringAttr(tree, idx), outData.packageName);
                }
                if ((idx = FindAttrByResId(tree, ATTR_LABEL)) >= 0) {
                    currentActivity.label = GetStringAttr(tree, idx);
                }
                if ((idx = FindAttrByResId(tree, ATTR_LAUNCH_MODE)) >= 0) {
                    currentActivity.launchMode = GetIntAttr(tree, idx, 0);
                }
                if ((idx = FindAttrByResId(tree, ATTR_SCREEN_ORIENTATION)) >= 0) {
                    currentActivity.screenOrientation = GetIntAttr(tree, idx, -1);
                }
                if ((idx = FindAttrByResId(tree, ATTR_EXPORTED)) >= 0) {
                    currentActivity.exported = GetBoolAttr(tree, idx, false);
                }
                if ((idx = FindAttrByResId(tree, ATTR_TASK_AFFINITY)) >= 0) {
                    currentActivity.taskAffinity = GetStringAttr(tree, idx);
                }
                if ((idx = FindAttrByResId(tree, ATTR_PERMISSION)) >= 0) {
                    currentActivity.permission = GetStringAttr(tree, idx);
                }
                if ((idx = FindAttrByResId(tree, ATTR_THEME)) >= 0) {
                    currentActivity.theme = GetIntAttr(tree, idx, 0);
                }
            } else if (elemName == "service") {
                state = State::IN_SERVICE;
                currentService = ServiceData();
                ssize_t idx;
                if ((idx = FindAttrByResId(tree, ATTR_NAME)) >= 0) {
                    currentService.name = ResolveClassName(
                        GetStringAttr(tree, idx), outData.packageName);
                }
                if ((idx = FindAttrByResId(tree, ATTR_EXPORTED)) >= 0) {
                    currentService.exported = GetBoolAttr(tree, idx, false);
                }
                if ((idx = FindAttrByResId(tree, ATTR_PERMISSION)) >= 0) {
                    currentService.permission = GetStringAttr(tree, idx);
                }
            } else if (elemName == "receiver") {
                state = State::IN_RECEIVER;
                currentReceiver = ReceiverData();
                ssize_t idx;
                if ((idx = FindAttrByResId(tree, ATTR_NAME)) >= 0) {
                    currentReceiver.name = ResolveClassName(
                        GetStringAttr(tree, idx), outData.packageName);
                }
                if ((idx = FindAttrByResId(tree, ATTR_EXPORTED)) >= 0) {
                    currentReceiver.exported = GetBoolAttr(tree, idx, false);
                }
                if ((idx = FindAttrByResId(tree, ATTR_PERMISSION)) >= 0) {
                    currentReceiver.permission = GetStringAttr(tree, idx);
                }
            } else if (elemName == "provider") {
                state = State::IN_PROVIDER;
                currentProvider = ProviderData();
                ssize_t idx;
                if ((idx = FindAttrByResId(tree, ATTR_NAME)) >= 0) {
                    currentProvider.name = ResolveClassName(
                        GetStringAttr(tree, idx), outData.packageName);
                }
                if ((idx = FindAttrByResId(tree, ATTR_AUTHORITIES)) >= 0) {
                    currentProvider.authorities = GetStringAttr(tree, idx);
                }
                if ((idx = FindAttrByResId(tree, ATTR_EXPORTED)) >= 0) {
                    currentProvider.exported = GetBoolAttr(tree, idx, false);
                }
                if ((idx = FindAttrByResId(tree, ATTR_READ_PERMISSION)) >= 0) {
                    currentProvider.readPermission = GetStringAttr(tree, idx);
                }
                if ((idx = FindAttrByResId(tree, ATTR_WRITE_PERMISSION)) >= 0) {
                    currentProvider.writePermission = GetStringAttr(tree, idx);
                }
                if ((idx = FindAttrByResId(tree, ATTR_GRANT_URI_PERMISSIONS)) >= 0) {
                    currentProvider.grantUriPermissions = GetBoolAttr(tree, idx, false);
                }
                if ((idx = FindAttrByResId(tree, ATTR_MULTIPROCESS)) >= 0) {
                    currentProvider.multiprocess = GetBoolAttr(tree, idx, false);
                }
                if ((idx = FindAttrByResId(tree, ATTR_INIT_ORDER)) >= 0) {
                    currentProvider.initOrder = GetIntAttr(tree, idx, 0);
                }
                if ((idx = FindAttrByResId(tree, ATTR_PROCESS)) >= 0) {
                    currentProvider.processName = GetStringAttr(tree, idx);
                }
            } else if (elemName == "intent-filter") {
                state = State::IN_INTENT_FILTER;
                currentFilter = IntentFilterData();
            } else if (elemName == "action" && state == State::IN_INTENT_FILTER) {
                ssize_t idx;
                if ((idx = FindAttrByResId(tree, ATTR_NAME)) >= 0) {
                    currentFilter.actions.push_back(GetStringAttr(tree, idx));
                }
            } else if (elemName == "category" && state == State::IN_INTENT_FILTER) {
                ssize_t idx;
                if ((idx = FindAttrByResId(tree, ATTR_NAME)) >= 0) {
                    currentFilter.categories.push_back(GetStringAttr(tree, idx));
                }
            } else if (elemName == "data" && state == State::IN_INTENT_FILTER) {
                IntentFilterData::UriData uri;
                ssize_t idx;
                if ((idx = FindAttrByResId(tree, ATTR_SCHEME)) >= 0) {
                    uri.scheme = GetStringAttr(tree, idx);
                }
                if ((idx = FindAttrByResId(tree, ATTR_HOST)) >= 0) {
                    uri.host = GetStringAttr(tree, idx);
                }
                if ((idx = FindAttrByResId(tree, ATTR_PATH)) >= 0) {
                    uri.path = GetStringAttr(tree, idx);
                }
                if ((idx = FindAttrByResId(tree, ATTR_MIME_TYPE)) >= 0) {
                    uri.type = GetStringAttr(tree, idx);
                }
                if (!uri.scheme.empty() || !uri.host.empty() ||
                    !uri.path.empty() || !uri.type.empty()) {
                    currentFilter.dataSpecs.push_back(uri);
                }
            } else if (elemName == "uses-permission") {
                state = State::IN_USES_PERMISSION;
                ssize_t idx;
                if ((idx = FindAttrByResId(tree, ATTR_NAME)) >= 0) {
                    outData.usesPermissions.push_back(GetStringAttr(tree, idx));
                }
            } else if (elemName == "permission") {
                state = State::IN_PERMISSION;
                PermissionData perm;
                ssize_t idx;
                if ((idx = FindAttrByResId(tree, ATTR_NAME)) >= 0) {
                    perm.name = GetStringAttr(tree, idx);
                }
                if ((idx = FindAttrByResId(tree, ATTR_PROTECTION_LEVEL)) >= 0) {
                    perm.protectionLevel = GetIntAttr(tree, idx, 0);
                }
                if (!perm.name.empty()) {
                    outData.permissionDeclarations.push_back(perm);
                }
            }

        } else if (event == AxmlParser::EC_END_TAG) {
            size_t nameLen = 0;
            const char* name = tree.getElementName(&nameLen);
            std::string elemName;
            if (name != nullptr) elemName.assign(name, nameLen);

            if (elemName == "intent-filter") {
                // Attach completed filter to current component
                State parentState = stateStack.empty() ? State::NONE : stateStack.back();
                if (parentState == State::IN_ACTIVITY) {
                    currentActivity.intentFilters.push_back(currentFilter);
                } else if (parentState == State::IN_SERVICE) {
                    currentService.intentFilters.push_back(currentFilter);
                } else if (parentState == State::IN_RECEIVER) {
                    currentReceiver.intentFilters.push_back(currentFilter);
                }
            } else if (elemName == "activity" || elemName == "activity-alias") {
                // Auto-detect exported if has intent-filters
                if (!currentActivity.intentFilters.empty() && !currentActivity.exported) {
                    // Android default: exported=true if has intent-filter (targetSdk < 31)
                    currentActivity.exported = true;
                }
                outData.activities.push_back(currentActivity);
            } else if (elemName == "service") {
                if (!currentService.intentFilters.empty() && !currentService.exported) {
                    currentService.exported = true;
                }
                outData.services.push_back(currentService);
            } else if (elemName == "receiver") {
                if (!currentReceiver.intentFilters.empty() && !currentReceiver.exported) {
                    currentReceiver.exported = true;
                }
                outData.receivers.push_back(currentReceiver);
            } else if (elemName == "provider") {
                outData.providers.push_back(currentProvider);
            }

            // Pop state
            if (!stateStack.empty()) {
                state = stateStack.back();
                stateStack.pop_back();
            } else {
                state = State::NONE;
            }
        }
    }

    // Validate required fields
    if (outData.packageName.empty()) {
        LOGE("Missing package name in AndroidManifest.xml");
        return false;
    }

    return true;
}

}  // namespace oh_adapter
