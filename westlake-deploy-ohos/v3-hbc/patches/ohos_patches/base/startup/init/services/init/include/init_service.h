/*
 * Copyright (c) 2020-2021 Huawei Device Co., Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
#ifndef BASE_STARTUP_INITLITE_SERVICE_H
#define BASE_STARTUP_INITLITE_SERVICE_H
#include <stdbool.h>
#include <stdint.h>
#include <sys/types.h>
#include <sched.h>
#include <stdio.h>

#include "cJSON.h"
#include "init_cmds.h"
#include "init_error.h"
#include "init_service_file.h"
#include "init_service_socket.h"
#include "list.h"
#include "loop_event.h"
#ifdef __cplusplus
#if __cplusplus
extern "C" {
#endif
#endif

// return value
#define SERVICE_FAILURE (-1)
#define SERVICE_SUCCESS 0

// service attributes
#define SERVICE_ATTR_INVALID 0x001      // option invalid
#define SERVICE_ATTR_ONCE 0x002         // do not restart when it exits
#define SERVICE_ATTR_NEED_RESTART 0x004 // will restart in the near future
#define SERVICE_ATTR_NEED_STOP 0x008    // will stop in reap
#define SERVICE_ATTR_IMPORTANT 0x010    // will reboot if it crash
#define SERVICE_ATTR_CRITICAL 0x020     // critical, will reboot if it crash 4 times in 4 minutes
#define SERVICE_ATTR_DISABLED 0x040     // disabled
#define SERVICE_ATTR_CONSOLE 0x080      // console
#define SERVICE_ATTR_ONDEMAND 0x100     // ondemand, manage socket by init
#define SERVICE_ATTR_TIMERSTART 0x200   // Mark a service will be started by timer
#define SERVICE_ATTR_NEEDWAIT 0x400     // service should execute waitpid while stopping
#define SERVICE_ATTR_WITHOUT_SANDBOX 0x800     // make service not enter sandbox

#define SERVICE_ATTR_NOTIFY_STATE 0x1000     // service notify state
#define SERVICE_ATTR_MODULE_UPDATE 0x2000     // module update
#define SERVICE_ATTR_KMSG 0x4000      // kmsg
#define SERVICE_ATTR_PERIOD 0x8000    //service period

#define SERVICE_ATTR_SETUID 0x10000 //service can setuid with no fixup
#ifdef INIT_FEATURE_SUPPORT_SASPAWN
#define SERVICE_ATTR_SASPAWN 0X20000 //service support sa spawn
#endif

#define MAX_SERVICE_NAME 32
#define MAX_APL_NAME 32
#define MAX_ENV_NAME 64
#define MAX_JOB_NAME 128
#define MAX_WRITEPID_FILES 100
#define MAX_ENV_VALUE 1024  // adapter project: MAX_ENV_VALUE bumped for BOOTCLASSPATH

#define FULL_CAP 0xFFFFFFFF
// init
#define DEFAULT_UMASK_INIT 022

#define CAP_NUM 2

#define SERVICES_ARR_NAME_IN_JSON "services"
#ifdef INIT_FEATURE_SUPPORT_SASPAWN
#define SERVICES_FILE_PATH_ANON "anon_inode:[signalfd]"
#define SERVICES_FILE_PATH_SOCKET "socket"
#define STRTOL_BASE 10
#define SERVICES_FILE_PATH_KMSG "/dev/kmsg"
#define SERVICES_PROC_SELF_FD "/proc/self/fd"
#define SERVICES_STR_LEN_MAX 96
#define SERVICES_STANDARD_INPUT 0
#define SERVICES_STANDARD_OUTPUT 1
#define SERVICES_STANDARD_ERROR 2
#define SERVICES_SASPAWN_LIBRARY_NAME "libsa_start.z.so"
#define SERVICES_SASPAWN_FUNCTION_NAME "StartSA"
#define SERVICES_SYSTEM_BIN_SA_MAIN "/system/bin/sa_main"
#endif

#define SERVICES_EXIT_INFO_IS_SASPAWN 1
#define SERVICES_EXIT_INFO_NOT_SASPAWN 0

#define IsOnDemandService(service) \
    (((service)->attribute & SERVICE_ATTR_ONDEMAND) == SERVICE_ATTR_ONDEMAND)

#define MarkServiceAsOndemand(service) \
    ((service)->attribute |= SERVICE_ATTR_ONDEMAND)

#define UnMarkServiceAsOndemand(service) \
    ((service)->attribute &= ~SERVICE_ATTR_ONDEMAND)

#define IsServiceWithTimerEnabled(service) \
    (((service)->attribute & SERVICE_ATTR_TIMERSTART) == SERVICE_ATTR_TIMERSTART)

#define DisableServiceTimer(service) \
    ((service)->attribute &= ~SERVICE_ATTR_TIMERSTART)

#define EnableServiceTimer(service) \
    ((service)->attribute |= SERVICE_ATTR_TIMERSTART)

#define MarkServiceWithoutSandbox(service) \
    ((service)->attribute |= SERVICE_ATTR_WITHOUT_SANDBOX)

#define MarkServiceWithSandbox(service) \
    ((service)->attribute &= ~SERVICE_ATTR_WITHOUT_SANDBOX)

#ifdef INIT_FEATURE_SUPPORT_SASPAWN
#define ServiceSupportSaSpawn(service) \
    ((service)->attribute |= SERVICE_ATTR_SASPAWN)

#define ServiceResetSupportSaSpawn(service) \
    ((service)->attribute &= ~SERVICE_ATTR_SASPAWN)
#endif

#pragma pack(4)
typedef enum {
    START_MODE_CONDITION,
    START_MODE_BOOT,
    START_MODE_NORMAL,
} ServiceStartMode;

typedef enum {
    END_PRE_FORK,
    END_AFTER_FORK,
    END_AFTER_EXEC,
    END_RECV_READY,
} ServiceEndMode;

typedef struct {
    uid_t uID;
    gid_t *gIDArray;
    int gIDCnt;
    unsigned int *caps;
    unsigned int capsCnt;
} Perms;

typedef struct {
    int count;
    char **argv;
} ServiceArgs;

typedef enum {
    JOB_ON_BOOT,
    JOB_PRE_START,
    JOB_ON_START,
    JOB_ON_STOP,
    JOB_ON_RESTART,
    JOB_ON_MAX
} ServiceJobType;

typedef struct {
    char *jobsName[JOB_ON_MAX];
} ServiceJobs;

typedef struct {
    char name[MAX_ENV_NAME];
    char value[MAX_ENV_VALUE];
} ServiceEnv;

typedef struct Service {
    char *name;
    int pid;
    int crashCnt;
    uint32_t firstCrashTime;
    int crashCount;
    int crashTime;
    unsigned int attribute;
    int importance;
    int startMode : 4; // startCondition/ startBoot / startNormal
    int endMode : 4; // preFork/ fork / exec / ready
    int status : 4; // ServiceStatus
    uint64_t tokenId;
    char *apl;
    uint64_t period;
    ServiceEnv *env;
    int envCnt;
    bool isCgroupEnabled;
    ServiceArgs capsArgs;
    ServiceArgs permArgs;
    ServiceArgs permAclsArgs;
    char *kernelPerms;
    Perms servPerm;
    ServiceArgs pathArgs;
    ServiceArgs writePidArgs;
    CmdLines *restartArg;
    ServiceSocket *socketCfg;
    ServiceFile *fileCfg;
    int *fds;
    size_t fdCount;
    TimerHandle timer;
    ServiceJobs serviceJobs;
    cpu_set_t *cpuSet;
    struct ListNode extDataNode;
    ConfigContext context;
    InitErrno lastErrno;
} Service;
#pragma pack()

Service *GetServiceByPid(pid_t pid);
Service *GetServiceByName(const char *servName);
int ServiceStart(Service *service, ServiceArgs *pathArgs);
int ServiceStop(Service *service);
int ServiceTerm(Service *service);
void ServiceReap(Service *service);
void ReapService(Service *service);

void NotifyServiceChange(Service *service, int status);
int IsForbidden(const char *fieldStr);
int SetImportantValue(Service *curServ, const char *attrName, int value, int flag);
int InitServiceCaps(const cJSON *curArrItem, Service *curServ);
int ServiceExec(Service *service, const ServiceArgs *pathArgs);
#ifdef INIT_FEATURE_SUPPORT_SASPAWN
int InitServiceBySaspawn(Service *service, const ServiceArgs *pathArgs);
#endif
void CloseServiceFds(Service *service, bool needFree);
int UpdaterServiceFds(Service *service, int *fds, size_t fdCount);
int SetAccessToken(const Service *service);
void GetAccessToken(void);
void ServiceStopTimer(Service *service);
void ServiceStartTimer(Service *service, uint64_t timeout);
void ServiceReStartTimer(Service *service, uint64_t timeout);
void IsEnableSandbox(void);
void EnterServiceSandbox(Service *service);
int SetServiceEnterSandbox(const Service *service, const char *execPath);
#ifdef INIT_FEATURE_SUPPORT_SASPAWN
void CloseFileResource(void);
void ResetSignalResource(void);
#endif

int CreateServiceFile(Service *service);
void CloseServiceFile(ServiceFile *fileOpt);
#ifndef OHOS_LITE
int ProcessServiceDied(Service *service);
int ProcessServiceAdd(Service *service);
#endif
#ifdef __cplusplus
#if __cplusplus
}
#endif
#endif

#endif // BASE_STARTUP_INITLITE_SERVICE_H
