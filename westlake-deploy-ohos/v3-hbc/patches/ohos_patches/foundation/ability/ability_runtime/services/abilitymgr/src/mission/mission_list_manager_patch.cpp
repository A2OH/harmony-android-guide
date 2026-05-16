/*
 * mission_list_manager_patch.cpp
 *
 * Patch additions for MissionListManager to support multi-Ability Mission stacks.
 * These methods are added to the existing MissionListManager class.
 *
 * Apply by adding to mission_list_manager.cpp or including as a separate
 * compilation unit linked with the MissionListManager class.
 *
 * Changes:
 *   1. New method: StartAbilityInMission()
 *      - Pushes a new Ability onto an existing Mission's stack
 *      - Backgrounds the current stack top, foregrounds the new Ability
 *
 *   2. Modified: TerminateAbilityLocked()
 *      - When mission->IsMultiAbilityMode() && stack size > 1:
 *        Pops the stack instead of destroying the Mission
 *      - Otherwise: existing behavior unchanged
 */

// ==================== ADD TO mission_list_manager.h ====================
// public:
//     int StartAbilityInMission(AbilityRequest &abilityRequest, int32_t missionId);

// ==================== ADD TO mission_list_manager.cpp ====================

/*
 * StartAbilityInMission: push a new Ability onto an existing Mission's stack.
 *
 * Flow:
 *   1. Find the target Mission by missionId
 *   2. Create new AbilityRecord from the request
 *   3. Background the current stack top
 *   4. Push new AbilityRecord onto Mission stack
 *   5. Foreground the new AbilityRecord
 *   6. Update MissionInfo (label/want become new stack top's)
 */

#if 0  // Paste into MissionListManager class implementation

int MissionListManager::StartAbilityInMission(
    AbilityRequest &abilityRequest, int32_t missionId)
{
    std::lock_guard<std::recursive_mutex> guard(managerLock_);

    TAG_LOGI(AAFwkTag::ABILITYMGR,
        "StartAbilityInMission: missionId=%{public}d, ability=%{public}s",
        missionId, abilityRequest.abilityInfo.name.c_str());

    // 1. Find target Mission
    std::shared_ptr<Mission> targetMission = nullptr;
    for (auto &missionList : currentMissionLists_) {
        if (!missionList) continue;
        targetMission = missionList->GetMissionById(missionId);
        if (targetMission) break;
    }
    // Also search default lists
    if (!targetMission && defaultStandardList_) {
        targetMission = defaultStandardList_->GetMissionById(missionId);
    }
    if (!targetMission && defaultSingleList_) {
        targetMission = defaultSingleList_->GetMissionById(missionId);
    }

    if (!targetMission) {
        TAG_LOGE(AAFwkTag::ABILITYMGR,
            "StartAbilityInMission: Mission %{public}d not found", missionId);
        return ERR_INVALID_VALUE;
    }

    // 2. Handle FLAG_ACTIVITY_CLEAR_TOP via android_clear_top Want parameter
    bool clearTop = abilityRequest.want.GetBoolParam("android_clear_top", false);
    if (clearTop && targetMission->IsMultiAbilityMode()) {
        std::string targetAbilityName = abilityRequest.abilityInfo.name;
        auto existingRecord = targetMission->FindAbilityByName(targetAbilityName);
        if (existingRecord) {
            // Pop all abilities above the target (and the target itself for recreate)
            auto popped = targetMission->PopAbilitiesAbove(targetAbilityName, false);
            for (auto &record : popped) {
                if (record) {
                    record->Terminate(nullptr);
                    terminateAbilityList_.push_back(record);
                }
            }
            TAG_LOGI(AAFwkTag::ABILITYMGR,
                "StartAbilityInMission: CLEAR_TOP popped %{public}zu abilities above %{public}s",
                popped.size(), targetAbilityName.c_str());

            // Foreground the existing target ability (now the new stack top)
            auto newTop = targetMission->GetAbilityRecord();
            if (newTop) {
                newTop->ProcessForegroundAbility(abilityRequest.callerToken);
            }

            // Update MissionInfo
            InnerMissionInfo clearInfo;
            auto clearInfoMgr = DelayedSingleton<MissionInfoMgr>::GetInstance();
            if (clearInfoMgr && clearInfoMgr->GetInnerMissionInfoById(missionId, clearInfo) == ERR_OK) {
                if (newTop) {
                    clearInfo.missionInfo.want = newTop->GetWant();
                    clearInfo.missionInfo.label = newTop->GetLabel();
                }
                clearInfo.missionInfo.time = GetCurrentTime();
                clearInfoMgr->UpdateMissionInfo(clearInfo);
            }

            if (listenerController_) {
                listenerController_->NotifyMissionSnapshotChanged(missionId);
            }
            return ERR_OK;
        }
        // Target not found in stack — fall through to normal push
    }

    // 3. Create new AbilityRecord
    auto newRecord = MissionAbilityRecord::CreateAbilityRecord(abilityRequest);
    if (!newRecord) {
        TAG_LOGE(AAFwkTag::ABILITYMGR,
            "StartAbilityInMission: Failed to create AbilityRecord");
        return ERR_INVALID_VALUE;
    }

    // 4. Background current stack top
    auto currentTop = targetMission->GetAbilityRecord();
    if (currentTop && currentTop->IsAbilityState(AbilityState::FOREGROUND)) {
        MoveToBackgroundTask(currentTop);
    }

    // 5. Push new Ability onto Mission stack
    targetMission->PushAbility(newRecord);

    // 6. Move Mission to top of its MissionList
    auto missionList = targetMission->GetMissionList();
    if (missionList) {
        missionList->AddMissionToTop(targetMission);
    }

    // 7. Foreground the new Ability
    newRecord->ProcessForegroundAbility(abilityRequest.callerToken);

    // 8. Update MissionInfo — label/want/time now reflect new stack top
    InnerMissionInfo innerInfo;
    auto infoMgr = DelayedSingleton<MissionInfoMgr>::GetInstance();
    if (infoMgr && infoMgr->GetInnerMissionInfoById(missionId, innerInfo) == ERR_OK) {
        innerInfo.missionInfo.want = newRecord->GetWant();
        innerInfo.missionInfo.label = newRecord->GetLabel();
        innerInfo.missionInfo.time = GetCurrentTime();
        infoMgr->UpdateMissionInfo(innerInfo);
    }

    // 9. Notify listeners
    if (listenerController_) {
        listenerController_->NotifyMissionSnapshotChanged(missionId);
    }

    return ERR_OK;
}

#endif

// ==================== MODIFY TerminateAbilityLocked ====================
// Add this branch at the beginning of TerminateAbilityLocked,
// before the existing RemoveTerminatingAbility call:

#if 0  // Insert at top of TerminateAbilityLocked

    // --- Multi-Ability stack: pop instead of destroy ---
    auto missionId = abilityRecord->GetMissionId();
    auto mission = GetMissionById(missionId);
    if (mission && mission->IsMultiAbilityMode() && mission->GetAbilityStackSize() > 1) {
        // Pop the terminating ability from the stack
        auto popped = mission->PopAbility();
        if (popped) {
            // Add to termination queue for cleanup
            terminateAbilityList_.push_back(popped);

            // Foreground the new stack top
            auto newTop = mission->GetAbilityRecord();
            if (newTop) {
                newTop->ProcessForegroundAbility(nullptr);
            }

            // Update MissionInfo to reflect new stack top
            InnerMissionInfo innerInfo;
            auto infoMgr = DelayedSingleton<MissionInfoMgr>::GetInstance();
            if (infoMgr && infoMgr->GetInnerMissionInfoById(missionId, innerInfo) == ERR_OK) {
                if (newTop) {
                    innerInfo.missionInfo.want = newTop->GetWant();
                    innerInfo.missionInfo.label = newTop->GetLabel();
                }
                innerInfo.missionInfo.time = GetCurrentTime();
                infoMgr->UpdateMissionInfo(innerInfo);
            }

            // Notify listeners for snapshot update
            if (listenerController_) {
                listenerController_->NotifyMissionSnapshotChanged(missionId);
            }

            TAG_LOGI(AAFwkTag::ABILITYMGR,
                "TerminateAbility: popped from mission %{public}d stack, "
                "remaining=%{public}zu", missionId, mission->GetAbilityStackSize());
        }

        return ERR_OK;
    }
    // --- End multi-Ability stack handling ---
    // (existing TerminateAbilityLocked code follows below unchanged)

#endif

// ==================== MODIFY CleanMissionLocked ====================
// Add stack cleanup before existing logic:

#if 0  // Insert at top of CleanMissionLocked

    auto mission = GetMissionById(missionId);
    if (mission && mission->IsMultiAbilityMode() && mission->GetAbilityStackSize() > 1) {
        // Terminate all stacked abilities from top to bottom
        while (mission->GetAbilityStackSize() > 1) {
            auto popped = mission->PopAbility();
            if (popped) {
                popped->Terminate(nullptr);
                terminateAbilityList_.push_back(popped);
            }
        }
    }
    // Then continue with existing CleanMissionLocked logic
    // which handles the last remaining ability and Mission destruction

#endif
