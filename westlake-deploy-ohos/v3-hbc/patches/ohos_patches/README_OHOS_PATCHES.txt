OpenHarmony Source Patches for Android Adapter
===============================================

These patches modify OH ability_rt source to support multi-Ability Mission stacks,
enabling Android apps to maintain their Task/Activity stack behavior on OH.

Changes are minimal and backward-compatible:
  - OH native apps: behavior completely unchanged (single Ability per Mission)
  - Android adapted apps: Mission holds an Ability stack (multiple Activities)

How to apply:
  1. Copy patched files over the originals in the OH source tree
  2. Rebuild ability_rt module

Files modified:
  ability_rt/services/abilitymgr/include/mission/mission.h
    - abilityRecord_ changed to abilityStack_ (vector)
    - Added: PushAbility, PopAbility, GetAbilityStackSize, GetBaseAbilityRecord,
             FindAbilityByName, PopAbilitiesAbove, IsMultiAbilityMode, SetMultiAbilityMode

  ability_rt/services/abilitymgr/src/mission/mission.cpp
    - Implementation of all new methods
    - GetAbilityRecord() returns stack top (backward compatible)
    - Constructor puts initial ability into stack (backward compatible)

  ability_rt/interfaces/inner_api/ability_manager/include/ability_manager_interface.h
    - Added: StartAbilityInMission(Want, missionId) IPC method
