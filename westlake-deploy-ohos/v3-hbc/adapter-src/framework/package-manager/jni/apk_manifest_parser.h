/*
 * apk_manifest_parser.h
 *
 * Parser for AndroidManifest.xml in AXML binary format inside APK files.
 *
 * APK uses Android's binary XML (AXML) format for the manifest, which consists of:
 *   - File header (magic 0x00080003)
 *   - String pool (chunk 0x001C0001): UTF-8/UTF-16 string table
 *   - Resource ID table (chunk 0x00080180): attribute name -> resource ID mapping
 *   - XML elements (chunks 0x00100102/0x00100103): start/end element tags with attributes
 *
 * Phase 1 implementation leverages AOSP's androidfw/ResourceTypes.h (ResXMLParser)
 * which is available on OH as part of the cross-compiled ART runtime.
 */
#ifndef APK_MANIFEST_PARSER_H
#define APK_MANIFEST_PARSER_H

#include <cstdint>
#include <string>
#include <vector>

namespace oh_adapter {

class ApkManifestParser {
public:
    // ===== Parsed data structures =====

    struct IntentFilterData {
        std::vector<std::string> actions;      // <action android:name="...">
        std::vector<std::string> categories;   // <category android:name="...">
        struct UriData {
            std::string scheme;
            std::string host;
            std::string path;
            std::string type;  // mimeType
        };
        std::vector<UriData> dataSpecs;        // <data android:scheme/host/path/mimeType>
    };

    struct ActivityData {
        std::string name;              // Fully-qualified class name (android:name)
        std::string label;             // android:label
        int32_t launchMode = 0;        // 0=standard, 1=singleTop, 2=singleTask, 3=singleInstance
        int32_t screenOrientation = -1;// android:screenOrientation
        bool exported = false;         // android:exported
        std::string taskAffinity;      // android:taskAffinity
        std::string permission;        // android:permission
        int32_t theme = 0;             // android:theme (resource ID)
        std::vector<IntentFilterData> intentFilters;
    };

    struct ServiceData {
        std::string name;
        bool exported = false;
        std::string permission;
        std::vector<IntentFilterData> intentFilters;
    };

    struct ReceiverData {
        std::string name;
        bool exported = false;
        std::string permission;
        std::vector<IntentFilterData> intentFilters;
    };

    struct ProviderData {
        std::string name;
        std::string authorities;       // android:authorities (semicolon-separated)
        bool exported = false;
        std::string readPermission;
        std::string writePermission;
        bool grantUriPermissions = false;
        bool multiprocess = false;
        int32_t initOrder = 0;
        std::string processName;       // android:process
    };

    struct PermissionData {
        std::string name;
        int32_t protectionLevel = 0;   // 0=normal, 1=dangerous, 2=signature
    };

    struct ManifestData {
        // <manifest> attributes
        std::string packageName;
        int32_t versionCode = 0;
        std::string versionName;
        int32_t minSdkVersion = 0;
        int32_t targetSdkVersion = 0;
        std::string sharedUserId;

        // <application> attributes
        std::string appClassName;      // android:name
        std::string appLabel;
        bool debuggable = false;
        bool extractNativeLibs = true;
        // P2 (2026-04-30) — fields for AppBindData per ability_manager_ipc_adapter_design §1.1.4
        int32_t appTheme = 0;                  // android:theme on <application>
        std::string appProcessName;            // android:process on <application>
        std::string appComponentFactory;       // android:appComponentFactory
        std::string classLoaderName;           // android:classLoader
        int32_t networkSecurityConfigRes = 0;  // android:networkSecurityConfig
        bool largeHeap = false;                // android:largeHeap
        bool allowBackup = true;               // android:allowBackup
        bool persistent = false;               // android:persistent
        bool hardwareAccelerated = true;       // android:hardwareAccelerated
        std::vector<std::string> sharedLibraryFiles; // <uses-library android:name>

        // Four component types
        std::vector<ActivityData> activities;
        std::vector<ServiceData> services;
        std::vector<ReceiverData> receivers;
        std::vector<ProviderData> providers;

        // Permissions
        std::vector<std::string> usesPermissions;
        std::vector<PermissionData> permissionDeclarations;
    };

    /**
     * Parse AndroidManifest.xml from an APK file.
     * Extracts the manifest from the APK ZIP, parses the AXML binary format,
     * and populates the ManifestData structure.
     *
     * @param apkPath Path to the APK file
     * @param outData Output parsed manifest data
     * @return true on success, false on parse error
     */
    static bool Parse(const std::string& apkPath, ManifestData& outData);

private:
    /**
     * Parse AXML binary data into ManifestData.
     * Uses AOSP ResXMLParser for chunk-level parsing.
     */
    static bool ParseAXML(const uint8_t* data, size_t size, ManifestData& outData);

    /**
     * Resolve a potentially relative class name to fully-qualified form.
     * e.g. ".MainActivity" with package "com.example" -> "com.example.MainActivity"
     */
    static std::string ResolveClassName(const std::string& name,
                                         const std::string& packageName);
};

}  // namespace oh_adapter

#endif  // APK_MANIFEST_PARSER_H
