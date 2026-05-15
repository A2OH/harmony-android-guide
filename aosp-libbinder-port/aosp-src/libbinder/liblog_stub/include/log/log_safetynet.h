// libbinder-port: empty stub.  Access.cpp only includes log_safetynet.h for
// android_errorWriteLog() inside #ifdef __ANDROID__ paths that we don't take.
#pragma once

#define android_errorWriteLog(tag, subTag) (void)(tag), (void)(subTag)
