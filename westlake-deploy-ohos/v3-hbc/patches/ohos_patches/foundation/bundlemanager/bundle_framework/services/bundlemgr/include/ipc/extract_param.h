/*
 * Copyright (c) 2022 Huawei Device Co., Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#ifndef FOUNDATION_APPEXECFWK_SERVICES_BUNDLEMGR_INCLUDE_IPC_EXTRACT_PARAM_H
#define FOUNDATION_APPEXECFWK_SERVICES_BUNDLEMGR_INCLUDE_IPC_EXTRACT_PARAM_H

#include "message_parcel.h"

namespace OHOS {
namespace AppExecFwk {
enum ExtractFileType : uint8_t {
    ALL,
    SO,
    AN,
    PATCH,
    AP,
    RESOURCE,
    RES_FILE,
    HNPS_FILE,
    APK_RESOURCES_HAP  // adapter project: APK_RESOURCES_HAP
};

struct ExtractParam : public Parcelable {
    std::string srcPath;
    std::string targetPath;
    std::string cpuAbi;
    ExtractFileType extractFileType = ExtractFileType::ALL;
    bool needRemoveOld = false;

    std::string ToString() const;
    bool ReadFromParcel(Parcel &parcel);
    virtual bool Marshalling(Parcel &parcel) const override;
    static ExtractParam *Unmarshalling(Parcel &parcel);
};
}  // namespace AppExecFwk
}  // namespace OHOS
#endif  // FOUNDATION_APPEXECFWK_SERVICES_BUNDLEMGR_INCLUDE_IPC_EXTRACT_PARAM_H
