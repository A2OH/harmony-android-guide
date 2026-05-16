/*
 * Copyright (c) 2021-2023 Huawei Device Co., Ltd.
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

#include "mission.h"

#include "hilog_tag_wrapper.h"
#include "mission_list.h"

namespace OHOS {
namespace AAFwk {
Mission::Mission(int32_t id, MissionAbilityRecordPtr abilityRecord, const std::string &missionName,
    int32_t startMethod)
    : missionId_(id), startMethod_(startMethod), missionName_(missionName)
{
    // Initialize the ability stack with the first ability
    if (abilityRecord) {
        abilityStack_.push_back(abilityRecord);
    }
}

Mission::Mission(const std::shared_ptr<Mission> &mission)
{
    if (!mission) {
        return;
    }

    missionId_ = mission->missionId_;
    startMethod_ = mission->startMethod_;
    abilityStack_ = mission->abilityStack_;
    missionName_ = mission->missionName_;
    lockedState_ = mission->lockedState_;
    ownerMissionList_ = mission->ownerMissionList_;
    unclearable_ = mission->unclearable_;
    multiAbilityMode_ = mission->multiAbilityMode_;
}

Mission::~Mission()
{}

MissionAbilityRecordPtr Mission::GetAbilityRecord() const
{
    // Return the stack top (most recent ability).
    // For OH native apps (stack size 1), this returns the only ability.
    // For Android adapted apps, this returns the top Activity.
    if (abilityStack_.empty()) {
        return nullptr;
    }
    return abilityStack_.back();
}

int32_t Mission::GetMissionId() const
{
    return missionId_;
}

bool Mission::IsSingletonAbility() const
{
    // Use the base (first) ability to determine launch mode,
    // as it defines the Mission's identity.
    if (!abilityStack_.empty()) {
        return abilityStack_.front()->GetAbilityInfo().launchMode == AppExecFwk::LaunchMode::SINGLETON;
    }
    return false;
}

bool Mission::IsSpecifiedAbility() const
{
    if (!abilityStack_.empty()) {
        return abilityStack_.front()->GetAbilityInfo().launchMode == AppExecFwk::LaunchMode::SPECIFIED;
    }
    return false;
}

bool Mission::IsStandardAbility() const
{
    if (!abilityStack_.empty()) {
        return abilityStack_.front()->GetAbilityInfo().launchMode == AppExecFwk::LaunchMode::STANDARD;
    }
    return false;
}

void Mission::SetSpecifiedFlag(const std::string &flag)
{
    specifiedFlag_ = flag;
}

std::string Mission::GetSpecifiedFlag() const
{
    return specifiedFlag_;
}

std::shared_ptr<MissionList> Mission::GetMissionList()
{
    return ownerMissionList_.lock();
}

std::string Mission::GetMissionName() const
{
    return missionName_;
}

void Mission::SetMissionList(const std::shared_ptr<MissionList> &missionList)
{
    ownerMissionList_ = missionList;
}

void Mission::SetLockedState(bool lockedState)
{
    lockedState_ = lockedState;
}

bool Mission::IsLockedState() const
{
    return lockedState_;
}

void Mission::SetMovingState(bool movingState)
{
    isMovingToFront_ = movingState;
}

bool Mission::IsMovingState() const
{
    return isMovingToFront_;
}

void Mission::SetANRState(bool state)
{
    isANRState_ = state;
}

bool Mission::IsANRState() const
{
    return isANRState_;
}

void Mission::Dump(std::vector<std::string> &info)
{
    std::string dumpInfo = "    Mission ID #" + std::to_string(missionId_);
    dumpInfo += "  mission name #[" + missionName_ + "]" + "  lockedState #" + std::to_string(lockedState_)
        + "  ANR State #" + std::to_string(isANRState_)
        + "  multiAbility #" + std::to_string(multiAbilityMode_)
        + "  stackSize #" + std::to_string(abilityStack_.size());
    info.push_back(dumpInfo);

    // Dump all abilities in the stack (top to bottom)
    for (auto it = abilityStack_.rbegin(); it != abilityStack_.rend(); ++it) {
        if (*it) {
            (*it)->Dump(info);
        }
    }
}

bool Mission::IsStartByCall()
{
    return static_cast<int32_t>(StartMethod::START_CALL) == startMethod_;
}

bool Mission::UpdateMissionId(int32_t id, int32_t method)
{
    if (method == startMethod_ && id > 0) {
        return false;
    }

    startMethod_ = method;
    missionId_ = id;
    return true;
}

// ==================== Multi-Ability Stack Methods ====================

void Mission::PushAbility(MissionAbilityRecordPtr abilityRecord)
{
    if (!abilityRecord) {
        return;
    }
    abilityRecord->SetMissionId(missionId_);
    abilityStack_.push_back(abilityRecord);
    TAG_LOGI(AAFwkTag::ABILITYMGR,
        "Mission %{public}d: pushed ability %{public}s, stack size=%{public}zu",
        missionId_, abilityRecord->GetAbilityInfo().name.c_str(), abilityStack_.size());
}

MissionAbilityRecordPtr Mission::PopAbility()
{
    // Do not pop the last ability — Mission must always have at least one.
    // The last ability is removed through Mission destruction.
    if (abilityStack_.size() <= 1) {
        return nullptr;
    }

    auto top = abilityStack_.back();
    abilityStack_.pop_back();
    TAG_LOGI(AAFwkTag::ABILITYMGR,
        "Mission %{public}d: popped ability %{public}s, stack size=%{public}zu",
        missionId_, top ? top->GetAbilityInfo().name.c_str() : "null", abilityStack_.size());
    return top;
}

size_t Mission::GetAbilityStackSize() const
{
    return abilityStack_.size();
}

MissionAbilityRecordPtr Mission::GetBaseAbilityRecord() const
{
    if (abilityStack_.empty()) {
        return nullptr;
    }
    return abilityStack_.front();
}

MissionAbilityRecordPtr Mission::FindAbilityByName(const std::string &abilityName) const
{
    // Search from top to bottom
    for (auto it = abilityStack_.rbegin(); it != abilityStack_.rend(); ++it) {
        if (*it && (*it)->GetAbilityInfo().name == abilityName) {
            return *it;
        }
    }
    return nullptr;
}

bool Mission::ContainsAbility(const std::shared_ptr<AbilityRecord> &abilityRecord) const
{
    if (!abilityRecord) {
        return false;
    }

    for (const auto &record : abilityStack_) {
        if (record == abilityRecord) {
            return true;
        }
    }
    return false;
}

std::vector<MissionAbilityRecordPtr> Mission::PopAbilitiesAbove(
    const std::string &abilityName, bool includeSelf)
{
    std::vector<MissionAbilityRecordPtr> popped;

    // Find the target ability in the stack (search from bottom)
    auto targetIt = abilityStack_.end();
    for (auto it = abilityStack_.begin(); it != abilityStack_.end(); ++it) {
        if (*it && (*it)->GetAbilityInfo().name == abilityName) {
            targetIt = it;
            break;
        }
    }

    if (targetIt == abilityStack_.end()) {
        return popped;  // Target not found
    }

    // Determine where to start popping
    auto startPop = includeSelf ? targetIt : (targetIt + 1);

    // Collect abilities to pop
    for (auto it = startPop; it != abilityStack_.end(); ++it) {
        popped.push_back(*it);
    }

    // Erase from stack
    abilityStack_.erase(startPop, abilityStack_.end());

    TAG_LOGI(AAFwkTag::ABILITYMGR,
        "Mission %{public}d: popped %{public}zu abilities above %{public}s, stack size=%{public}zu",
        missionId_, popped.size(), abilityName.c_str(), abilityStack_.size());

    return popped;
}

}  // namespace AAFwk
}  // namespace OHOS
