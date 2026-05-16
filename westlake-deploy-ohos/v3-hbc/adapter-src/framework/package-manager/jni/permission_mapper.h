/*
 * permission_mapper.h
 *
 * Bidirectional permission name mapping between Android and OpenHarmony.
 *
 * Android permissions use "android.permission.*" naming convention.
 * OH permissions use "ohos.permission.*" naming convention.
 * This mapper provides conversion in both directions, used by:
 *   - ApkBundleParser (install-time: android → ohos)
 *   - PackageInfoBuilder (query-time: ohos → android)
 */
#ifndef PERMISSION_MAPPER_H
#define PERMISSION_MAPPER_H

#include <string>

namespace oh_adapter {

class PermissionMapper {
public:
    /**
     * Map Android permission name to OH permission name.
     * e.g. "android.permission.CAMERA" -> "ohos.permission.CAMERA"
     * Unknown permissions are mapped with "ohos.permission.android." prefix.
     */
    static std::string MapToOH(const std::string& androidPermission);

    /**
     * Map OH permission name to Android permission name.
     * e.g. "ohos.permission.CAMERA" -> "android.permission.CAMERA"
     * Unknown permissions are returned as-is.
     */
    static std::string MapToAndroid(const std::string& ohPermission);
};

}  // namespace oh_adapter

#endif  // PERMISSION_MAPPER_H
