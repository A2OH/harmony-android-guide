/*
 * Bridge native implementations for libcore classes.
 *
 * In stock Android, these live in libjavacore.so loaded by the Zygote.
 * For our standalone port, we register them directly during VM startup.
 */
#include "Dalvik.h"
#include <jni.h>

#include <errno.h>
#include <limits.h>
#include <fcntl.h>
#include <netdb.h>
#include <net/if.h>
#include <netinet/in.h>
#include <netinet/tcp.h>
#include <poll.h>
#include <signal.h>
#include <pwd.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>
#include <sys/ioctl.h>
#include <sys/mman.h>
#include <sys/socket.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <sys/un.h>
#include <sys/utsname.h>
#include <sys/wait.h>
#include <unistd.h>

/* ----------------------------------------------------------------
 * libcore.io.OsConstants.initConstants()V
 * ---------------------------------------------------------------- */

static void initConstant(JNIEnv* env, jclass c, const char* name, int value) {
    jfieldID fid = env->GetStaticFieldID(c, name, "I");
    if (fid == NULL) {
        env->ExceptionClear();
        return;
    }
    env->SetStaticIntField(c, fid, value);
}

static void JNICALL OsConstants_initConstants(JNIEnv* env, jclass c) {
    initConstant(env, c, "AF_INET", AF_INET);
    initConstant(env, c, "AF_INET6", AF_INET6);
    initConstant(env, c, "AF_UNIX", AF_UNIX);
    initConstant(env, c, "AF_UNSPEC", AF_UNSPEC);
#ifdef AF_PACKET
    initConstant(env, c, "AF_PACKET", AF_PACKET);
#endif
#ifdef AF_NETLINK
    initConstant(env, c, "AF_NETLINK", AF_NETLINK);
#endif
    initConstant(env, c, "AI_ADDRCONFIG", AI_ADDRCONFIG);
    initConstant(env, c, "AI_ALL", AI_ALL);
    initConstant(env, c, "AI_CANONNAME", AI_CANONNAME);
    initConstant(env, c, "AI_NUMERICHOST", AI_NUMERICHOST);
#ifdef AI_NUMERICSERV
    initConstant(env, c, "AI_NUMERICSERV", AI_NUMERICSERV);
#endif
    initConstant(env, c, "AI_PASSIVE", AI_PASSIVE);
    initConstant(env, c, "AI_V4MAPPED", AI_V4MAPPED);
    initConstant(env, c, "E2BIG", E2BIG);
    initConstant(env, c, "EACCES", EACCES);
    initConstant(env, c, "EADDRINUSE", EADDRINUSE);
    initConstant(env, c, "EADDRNOTAVAIL", EADDRNOTAVAIL);
    initConstant(env, c, "EAFNOSUPPORT", EAFNOSUPPORT);
    initConstant(env, c, "EAGAIN", EAGAIN);
    initConstant(env, c, "EALREADY", EALREADY);
    initConstant(env, c, "EBADF", EBADF);
    initConstant(env, c, "EBADMSG", EBADMSG);
    initConstant(env, c, "EBUSY", EBUSY);
    initConstant(env, c, "ECANCELED", ECANCELED);
    initConstant(env, c, "ECHILD", ECHILD);
    initConstant(env, c, "ECONNABORTED", ECONNABORTED);
    initConstant(env, c, "ECONNREFUSED", ECONNREFUSED);
    initConstant(env, c, "ECONNRESET", ECONNRESET);
    initConstant(env, c, "EDEADLK", EDEADLK);
    initConstant(env, c, "EDESTADDRREQ", EDESTADDRREQ);
    initConstant(env, c, "EDOM", EDOM);
    initConstant(env, c, "EEXIST", EEXIST);
    initConstant(env, c, "EFAULT", EFAULT);
    initConstant(env, c, "EFBIG", EFBIG);
    initConstant(env, c, "EHOSTUNREACH", EHOSTUNREACH);
    initConstant(env, c, "EILSEQ", EILSEQ);
    initConstant(env, c, "EINPROGRESS", EINPROGRESS);
    initConstant(env, c, "EINTR", EINTR);
    initConstant(env, c, "EINVAL", EINVAL);
    initConstant(env, c, "EIO", EIO);
    initConstant(env, c, "EISCONN", EISCONN);
    initConstant(env, c, "EISDIR", EISDIR);
    initConstant(env, c, "ELOOP", ELOOP);
    initConstant(env, c, "EMFILE", EMFILE);
    initConstant(env, c, "EMLINK", EMLINK);
    initConstant(env, c, "EMSGSIZE", EMSGSIZE);
    initConstant(env, c, "ENAMETOOLONG", ENAMETOOLONG);
    initConstant(env, c, "ENETDOWN", ENETDOWN);
    initConstant(env, c, "ENETRESET", ENETRESET);
    initConstant(env, c, "ENETUNREACH", ENETUNREACH);
    initConstant(env, c, "ENFILE", ENFILE);
    initConstant(env, c, "ENOBUFS", ENOBUFS);
    initConstant(env, c, "ENODEV", ENODEV);
    initConstant(env, c, "ENOENT", ENOENT);
    initConstant(env, c, "ENOEXEC", ENOEXEC);
    initConstant(env, c, "ENOLCK", ENOLCK);
    initConstant(env, c, "ENOMEM", ENOMEM);
    initConstant(env, c, "ENOPROTOOPT", ENOPROTOOPT);
    initConstant(env, c, "ENOSPC", ENOSPC);
    initConstant(env, c, "ENOSYS", ENOSYS);
    initConstant(env, c, "ENOTCONN", ENOTCONN);
    initConstant(env, c, "ENOTDIR", ENOTDIR);
    initConstant(env, c, "ENOTEMPTY", ENOTEMPTY);
    initConstant(env, c, "ENOTSOCK", ENOTSOCK);
    initConstant(env, c, "ENOTSUP", ENOTSUP);
    initConstant(env, c, "ENOTTY", ENOTTY);
    initConstant(env, c, "ENXIO", ENXIO);
    initConstant(env, c, "EOPNOTSUPP", EOPNOTSUPP);
    initConstant(env, c, "EOVERFLOW", EOVERFLOW);
    initConstant(env, c, "EPERM", EPERM);
    initConstant(env, c, "EPIPE", EPIPE);
    initConstant(env, c, "ERANGE", ERANGE);
    initConstant(env, c, "EROFS", EROFS);
    initConstant(env, c, "ESPIPE", ESPIPE);
    initConstant(env, c, "ESRCH", ESRCH);
    initConstant(env, c, "ETIMEDOUT", ETIMEDOUT);
    initConstant(env, c, "EWOULDBLOCK", EWOULDBLOCK);
    initConstant(env, c, "EXDEV", EXDEV);
    initConstant(env, c, "EAI_AGAIN", EAI_AGAIN);
    initConstant(env, c, "EAI_BADFLAGS", EAI_BADFLAGS);
    initConstant(env, c, "EAI_FAIL", EAI_FAIL);
    initConstant(env, c, "EAI_FAMILY", EAI_FAMILY);
    initConstant(env, c, "EAI_MEMORY", EAI_MEMORY);
    initConstant(env, c, "EAI_NODATA", EAI_NODATA);
    initConstant(env, c, "EAI_NONAME", EAI_NONAME);
    initConstant(env, c, "EAI_SERVICE", EAI_SERVICE);
    initConstant(env, c, "EAI_SOCKTYPE", EAI_SOCKTYPE);
    initConstant(env, c, "EAI_SYSTEM", EAI_SYSTEM);
#ifdef EAI_OVERFLOW
    initConstant(env, c, "EAI_OVERFLOW", EAI_OVERFLOW);
#endif
    initConstant(env, c, "F_DUPFD", F_DUPFD);
    initConstant(env, c, "F_GETFD", F_GETFD);
    initConstant(env, c, "F_GETFL", F_GETFL);
    initConstant(env, c, "F_GETLK", F_GETLK);
    initConstant(env, c, "F_RDLCK", F_RDLCK);
    initConstant(env, c, "F_SETFD", F_SETFD);
    initConstant(env, c, "F_SETFL", F_SETFL);
    initConstant(env, c, "F_SETLK", F_SETLK);
    initConstant(env, c, "F_SETLKW", F_SETLKW);
    initConstant(env, c, "F_UNLCK", F_UNLCK);
    initConstant(env, c, "F_WRLCK", F_WRLCK);
    initConstant(env, c, "FD_CLOEXEC", FD_CLOEXEC);
    initConstant(env, c, "MAP_FIXED", MAP_FIXED);
    initConstant(env, c, "MAP_PRIVATE", MAP_PRIVATE);
    initConstant(env, c, "MAP_SHARED", MAP_SHARED);
#ifdef MAP_ANONYMOUS
    initConstant(env, c, "MAP_ANONYMOUS", MAP_ANONYMOUS);
#endif
    initConstant(env, c, "PROT_EXEC", PROT_EXEC);
    initConstant(env, c, "PROT_NONE", PROT_NONE);
    initConstant(env, c, "PROT_READ", PROT_READ);
    initConstant(env, c, "PROT_WRITE", PROT_WRITE);
    initConstant(env, c, "MS_ASYNC", MS_ASYNC);
    initConstant(env, c, "MS_INVALIDATE", MS_INVALIDATE);
    initConstant(env, c, "MS_SYNC", MS_SYNC);
    initConstant(env, c, "O_ACCMODE", O_ACCMODE);
    initConstant(env, c, "O_APPEND", O_APPEND);
    initConstant(env, c, "O_CREAT", O_CREAT);
    initConstant(env, c, "O_EXCL", O_EXCL);
    initConstant(env, c, "O_NOCTTY", O_NOCTTY);
    initConstant(env, c, "O_NOFOLLOW", O_NOFOLLOW);
    initConstant(env, c, "O_NONBLOCK", O_NONBLOCK);
    initConstant(env, c, "O_RDONLY", O_RDONLY);
    initConstant(env, c, "O_RDWR", O_RDWR);
    initConstant(env, c, "O_SYNC", O_SYNC);
    initConstant(env, c, "O_TRUNC", O_TRUNC);
    initConstant(env, c, "O_WRONLY", O_WRONLY);
    initConstant(env, c, "POLLERR", POLLERR);
    initConstant(env, c, "POLLHUP", POLLHUP);
    initConstant(env, c, "POLLIN", POLLIN);
    initConstant(env, c, "POLLNVAL", POLLNVAL);
    initConstant(env, c, "POLLOUT", POLLOUT);
    initConstant(env, c, "POLLRDBAND", POLLRDBAND);
    initConstant(env, c, "POLLRDNORM", POLLRDNORM);
    initConstant(env, c, "POLLWRBAND", POLLWRBAND);
    initConstant(env, c, "POLLWRNORM", POLLWRNORM);
    initConstant(env, c, "SEEK_CUR", SEEK_CUR);
    initConstant(env, c, "SEEK_END", SEEK_END);
    initConstant(env, c, "SEEK_SET", SEEK_SET);
    initConstant(env, c, "SHUT_RD", SHUT_RD);
    initConstant(env, c, "SHUT_RDWR", SHUT_RDWR);
    initConstant(env, c, "SHUT_WR", SHUT_WR);
    initConstant(env, c, "SIGABRT", SIGABRT);
    initConstant(env, c, "SIGALRM", SIGALRM);
    initConstant(env, c, "SIGBUS", SIGBUS);
    initConstant(env, c, "SIGCHLD", SIGCHLD);
    initConstant(env, c, "SIGCONT", SIGCONT);
    initConstant(env, c, "SIGFPE", SIGFPE);
    initConstant(env, c, "SIGHUP", SIGHUP);
    initConstant(env, c, "SIGILL", SIGILL);
    initConstant(env, c, "SIGINT", SIGINT);
    initConstant(env, c, "SIGKILL", SIGKILL);
    initConstant(env, c, "SIGPIPE", SIGPIPE);
    initConstant(env, c, "SIGQUIT", SIGQUIT);
    initConstant(env, c, "SIGSEGV", SIGSEGV);
    initConstant(env, c, "SIGSTOP", SIGSTOP);
    initConstant(env, c, "SIGTERM", SIGTERM);
    initConstant(env, c, "SIGTRAP", SIGTRAP);
    initConstant(env, c, "SIGTSTP", SIGTSTP);
    initConstant(env, c, "SIGTTIN", SIGTTIN);
    initConstant(env, c, "SIGTTOU", SIGTTOU);
    initConstant(env, c, "SIGUSR1", SIGUSR1);
    initConstant(env, c, "SIGUSR2", SIGUSR2);
    initConstant(env, c, "SOCK_DGRAM", SOCK_DGRAM);
    initConstant(env, c, "SOCK_RAW", SOCK_RAW);
    initConstant(env, c, "SOCK_SEQPACKET", SOCK_SEQPACKET);
    initConstant(env, c, "SOCK_STREAM", SOCK_STREAM);
    initConstant(env, c, "SOL_SOCKET", SOL_SOCKET);
    initConstant(env, c, "SO_BROADCAST", SO_BROADCAST);
    initConstant(env, c, "SO_DEBUG", SO_DEBUG);
    initConstant(env, c, "SO_ERROR", SO_ERROR);
    initConstant(env, c, "SO_KEEPALIVE", SO_KEEPALIVE);
    initConstant(env, c, "SO_LINGER", SO_LINGER);
    initConstant(env, c, "SO_OOBINLINE", SO_OOBINLINE);
    initConstant(env, c, "SO_RCVBUF", SO_RCVBUF);
    initConstant(env, c, "SO_RCVLOWAT", SO_RCVLOWAT);
    initConstant(env, c, "SO_RCVTIMEO", SO_RCVTIMEO);
    initConstant(env, c, "SO_REUSEADDR", SO_REUSEADDR);
    initConstant(env, c, "SO_SNDBUF", SO_SNDBUF);
    initConstant(env, c, "SO_SNDLOWAT", SO_SNDLOWAT);
    initConstant(env, c, "SO_SNDTIMEO", SO_SNDTIMEO);
    initConstant(env, c, "SO_TYPE", SO_TYPE);
    initConstant(env, c, "IPPROTO_ICMP", IPPROTO_ICMP);
    initConstant(env, c, "IPPROTO_ICMPV6", IPPROTO_ICMPV6);
    initConstant(env, c, "IPPROTO_IP", IPPROTO_IP);
    initConstant(env, c, "IPPROTO_IPV6", IPPROTO_IPV6);
    initConstant(env, c, "IPPROTO_RAW", IPPROTO_RAW);
    initConstant(env, c, "IPPROTO_TCP", IPPROTO_TCP);
    initConstant(env, c, "IPPROTO_UDP", IPPROTO_UDP);
    initConstant(env, c, "IP_MULTICAST_IF", IP_MULTICAST_IF);
    initConstant(env, c, "IP_MULTICAST_LOOP", IP_MULTICAST_LOOP);
    initConstant(env, c, "IP_MULTICAST_TTL", IP_MULTICAST_TTL);
    initConstant(env, c, "IP_TOS", IP_TOS);
    initConstant(env, c, "IP_TTL", IP_TTL);
    initConstant(env, c, "S_IFBLK", S_IFBLK);
    initConstant(env, c, "S_IFCHR", S_IFCHR);
    initConstant(env, c, "S_IFDIR", S_IFDIR);
    initConstant(env, c, "S_IFIFO", S_IFIFO);
    initConstant(env, c, "S_IFLNK", S_IFLNK);
    initConstant(env, c, "S_IFMT", S_IFMT);
    initConstant(env, c, "S_IFREG", S_IFREG);
    initConstant(env, c, "S_IFSOCK", S_IFSOCK);
    initConstant(env, c, "S_IRGRP", S_IRGRP);
    initConstant(env, c, "S_IROTH", S_IROTH);
    initConstant(env, c, "S_IRUSR", S_IRUSR);
    initConstant(env, c, "S_IRWXG", S_IRWXG);
    initConstant(env, c, "S_IRWXO", S_IRWXO);
    initConstant(env, c, "S_IRWXU", S_IRWXU);
    initConstant(env, c, "S_ISGID", S_ISGID);
    initConstant(env, c, "S_ISUID", S_ISUID);
    initConstant(env, c, "S_ISVTX", S_ISVTX);
    initConstant(env, c, "S_IWGRP", S_IWGRP);
    initConstant(env, c, "S_IWOTH", S_IWOTH);
    initConstant(env, c, "S_IWUSR", S_IWUSR);
    initConstant(env, c, "S_IXGRP", S_IXGRP);
    initConstant(env, c, "S_IXOTH", S_IXOTH);
    initConstant(env, c, "S_IXUSR", S_IXUSR);
    initConstant(env, c, "TCP_NODELAY", TCP_NODELAY);
    initConstant(env, c, "WCONTINUED", WCONTINUED);
    initConstant(env, c, "WEXITED", WEXITED);
    initConstant(env, c, "WNOHANG", WNOHANG);
    initConstant(env, c, "WNOWAIT", WNOWAIT);
    initConstant(env, c, "WSTOPPED", WSTOPPED);
    initConstant(env, c, "WUNTRACED", WUNTRACED);
    initConstant(env, c, "STDERR_FILENO", STDERR_FILENO);
    initConstant(env, c, "STDIN_FILENO", STDIN_FILENO);
    initConstant(env, c, "STDOUT_FILENO", STDOUT_FILENO);
    initConstant(env, c, "R_OK", R_OK);
    initConstant(env, c, "W_OK", W_OK);
    initConstant(env, c, "X_OK", X_OK);
    initConstant(env, c, "F_OK", F_OK);
#ifdef _SC_PAGE_SIZE
    initConstant(env, c, "_SC_PAGE_SIZE", _SC_PAGE_SIZE);
#endif
#ifdef _SC_NPROCESSORS_CONF
    initConstant(env, c, "_SC_NPROCESSORS_CONF", _SC_NPROCESSORS_CONF);
#endif
#ifdef _SC_NPROCESSORS_ONLN
    initConstant(env, c, "_SC_NPROCESSORS_ONLN", _SC_NPROCESSORS_ONLN);
#endif
}

/* ----------------------------------------------------------------
 * libcore.io.Posix native methods
 * ---------------------------------------------------------------- */

static jstring JNICALL Posix_getenv(JNIEnv* env, jobject, jstring jname) {
    if (jname == NULL) return NULL;
    const char* name = env->GetStringUTFChars(jname, NULL);
    if (name == NULL) return NULL;
    const char* val = getenv(name);
    env->ReleaseStringUTFChars(jname, name);
    return val ? env->NewStringUTF(val) : NULL;
}

static jint JNICALL Posix_getuid(JNIEnv*, jobject) { return getuid(); }
static jint JNICALL Posix_getgid(JNIEnv*, jobject) { return getgid(); }
static jint JNICALL Posix_geteuid(JNIEnv*, jobject) { return geteuid(); }
static jint JNICALL Posix_getegid(JNIEnv*, jobject) { return getegid(); }
static jint JNICALL Posix_getpid(JNIEnv*, jobject) { return getpid(); }
static jint JNICALL Posix_getppid(JNIEnv*, jobject) { return getppid(); }
static jint JNICALL Posix_umask(JNIEnv*, jobject, jint mask) { return umask(mask); }
static jlong JNICALL Posix_sysconf(JNIEnv*, jobject, jint name) { return sysconf(name); }
static jboolean JNICALL Posix_isatty(JNIEnv*, jobject, jobject) { return JNI_FALSE; }

static void JNICALL Posix_setenv(JNIEnv* env, jobject, jstring jname, jstring jval, jboolean overwrite) {
    if (jname == NULL) return;
    const char* name = env->GetStringUTFChars(jname, NULL);
    const char* val = jval ? env->GetStringUTFChars(jval, NULL) : "";
    setenv(name, val, overwrite);
    if (jval) env->ReleaseStringUTFChars(jval, val);
    env->ReleaseStringUTFChars(jname, name);
}

static jobject JNICALL Posix_getpwuid(JNIEnv* env, jobject, jint uid) {
    struct passwd* pw = getpwuid(uid);
    if (pw == NULL) return NULL;
    jclass cls = env->FindClass("libcore/io/StructPasswd");
    if (cls == NULL) { env->ExceptionClear(); return NULL; }
    jmethodID ctor = env->GetMethodID(cls, "<init>",
        "(Ljava/lang/String;IILjava/lang/String;Ljava/lang/String;)V");
    if (ctor == NULL) { env->ExceptionClear(); return NULL; }
    jstring pw_name = env->NewStringUTF(pw->pw_name ? pw->pw_name : "");
    jstring pw_dir  = env->NewStringUTF(pw->pw_dir ? pw->pw_dir : "/");
    jstring pw_shell = env->NewStringUTF(pw->pw_shell ? pw->pw_shell : "/bin/sh");
    return env->NewObject(cls, ctor, pw_name, (jint)pw->pw_uid,
                          (jint)pw->pw_gid, pw_dir, pw_shell);
}

static jobject JNICALL Posix_stat(JNIEnv* env, jobject, jstring jpath) {
    if (jpath == NULL) return NULL;
    const char* path = env->GetStringUTFChars(jpath, NULL);
    struct stat sb;
    int rc = stat(path, &sb);
    env->ReleaseStringUTFChars(jpath, path);
    if (rc != 0) return NULL;
    jclass cls = env->FindClass("libcore/io/StructStat");
    if (cls == NULL) { env->ExceptionClear(); return NULL; }
    jmethodID ctor = env->GetMethodID(cls, "<init>", "(JJIJIIJJJJJJJ)V");
    if (ctor == NULL) { env->ExceptionClear(); return NULL; }
    return env->NewObject(cls, ctor,
        (jlong)sb.st_dev, (jlong)sb.st_ino, (jint)sb.st_mode,
        (jlong)sb.st_nlink, (jint)sb.st_uid, (jint)sb.st_gid,
        (jlong)sb.st_rdev, (jlong)sb.st_size,
        (jlong)sb.st_atime, (jlong)sb.st_mtime, (jlong)sb.st_ctime,
        (jlong)sb.st_blksize, (jlong)sb.st_blocks);
}

static jobject JNICALL Posix_uname(JNIEnv* env, jobject) {
    struct utsname buf;
    if (uname(&buf) != 0) return NULL;
    jclass cls = env->FindClass("libcore/io/StructUtsname");
    if (cls == NULL) { env->ExceptionClear(); return NULL; }
    jmethodID ctor = env->GetMethodID(cls, "<init>",
        "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V");
    if (ctor == NULL) { env->ExceptionClear(); return NULL; }
    return env->NewObject(cls, ctor,
        env->NewStringUTF(buf.sysname), env->NewStringUTF(buf.nodename),
        env->NewStringUTF(buf.release), env->NewStringUTF(buf.version),
        env->NewStringUTF(buf.machine));
}

static jint JNICALL Posix_open(JNIEnv* env, jobject, jstring jpath, jint flags, jint mode) {
    if (jpath == NULL) return -1;
    const char* path = env->GetStringUTFChars(jpath, NULL);
    int fd = open(path, flags, mode);
    env->ReleaseStringUTFChars(jpath, path);
    return fd;
}

static void JNICALL Posix_close(JNIEnv*, jobject, jobject) { }

static jint JNICALL Posix_read_bytes(JNIEnv*, jobject, jobject, jbyteArray, jint, jint) {
    return 0;
}

static jint JNICALL Posix_write_bytes_arr(JNIEnv*, jobject, jobject, jbyteArray, jint, jint len) {
    return len;
}

/* writeBytes with Object parameter (jar uses this signature) */
static jint JNICALL Posix_writeBytes(JNIEnv* env, jobject, jobject /*fd*/,
        jobject jbuf, jint off, jint len) {
    if (jbuf == NULL || len <= 0) return 0;
    jclass byteArrayClass = env->FindClass("[B");
    if (!env->IsInstanceOf(jbuf, byteArrayClass)) return len;
    jbyteArray arr = (jbyteArray)jbuf;
    jbyte* bytes = env->GetByteArrayElements(arr, NULL);
    jint written = (jint)write(STDOUT_FILENO, bytes + off, len);
    env->ReleaseByteArrayElements(arr, bytes, JNI_ABORT);
    return written > 0 ? written : len;
}

/* ----------------------------------------------------------------
 * libcore.icu.ICU native stubs
 * ---------------------------------------------------------------- */

static jstring JNICALL ICU_getIcuVersion(JNIEnv* env, jclass) {
    return env->NewStringUTF("4.4.2");
}
static jstring JNICALL ICU_getUnicodeVersion(JNIEnv* env, jclass) {
    return env->NewStringUTF("6.2");
}
static jobjectArray JNICALL ICU_getAvailableLocalesNative(JNIEnv* env, jclass) {
    jclass strClass = env->FindClass("java/lang/String");
    jobjectArray arr = env->NewObjectArray(1, strClass, NULL);
    env->SetObjectArrayElement(arr, 0, env->NewStringUTF("en_US"));
    return arr;
}
static jstring JNICALL ICU_getDefaultLocale(JNIEnv* env, jclass) {
    return env->NewStringUTF("en_US");
}
static jstring JNICALL ICU_getCldrVersion(JNIEnv* env, jclass) {
    return env->NewStringUTF("24");
}
static jstring JNICALL ICU_toLowerCase(JNIEnv*, jclass, jstring js, jstring) { return js; }
static jstring JNICALL ICU_toUpperCase(JNIEnv*, jclass, jstring js, jstring) { return js; }
static void JNICALL ICU_setDefaultLocale(JNIEnv*, jclass, jstring) { }
static jboolean JNICALL ICU_initLocaleDataNative(JNIEnv*, jclass, jstring, jobject) {
    return JNI_FALSE;
}
static jstring JNICALL ICU_getCurrencyCode(JNIEnv* env, jclass, jstring) {
    return env->NewStringUTF("USD");
}
static jstring JNICALL ICU_getISO3Country(JNIEnv* env, jclass, jstring) {
    return env->NewStringUTF("USA");
}
static jstring JNICALL ICU_getISO3Language(JNIEnv* env, jclass, jstring) {
    return env->NewStringUTF("eng");
}
static jstring JNICALL ICU_getScript(JNIEnv* env, jclass, jstring) {
    return env->NewStringUTF("");
}
static jstring JNICALL ICU_getBestDateTimePatternNative(JNIEnv* env, jclass, jstring, jstring) {
    return env->NewStringUTF("yyyy-MM-dd HH:mm:ss");
}
static jobjectArray JNICALL ICU_getISOLanguagesNative(JNIEnv* env, jclass) {
    jclass strClass = env->FindClass("java/lang/String");
    jobjectArray arr = env->NewObjectArray(1, strClass, NULL);
    env->SetObjectArrayElement(arr, 0, env->NewStringUTF("en"));
    return arr;
}
static jobjectArray JNICALL ICU_getISOCountriesNative(JNIEnv* env, jclass) {
    jclass strClass = env->FindClass("java/lang/String");
    jobjectArray arr = env->NewObjectArray(1, strClass, NULL);
    env->SetObjectArrayElement(arr, 0, env->NewStringUTF("US"));
    return arr;
}

/* ----------------------------------------------------------------
 * java.lang.System native methods
 * ---------------------------------------------------------------- */

static jlong JNICALL System_currentTimeMillis(JNIEnv*, jclass) {
    struct timespec ts;
    clock_gettime(CLOCK_REALTIME, &ts);
    return (jlong)ts.tv_sec * 1000 + ts.tv_nsec / 1000000;
}
static jlong JNICALL System_nanoTime(JNIEnv*, jclass) {
    struct timespec ts;
    clock_gettime(CLOCK_MONOTONIC, &ts);
    return (jlong)ts.tv_sec * 1000000000LL + ts.tv_nsec;
}
static jint JNICALL System_identityHashCode(JNIEnv*, jclass, jobject obj) {
    return (jint)(uintptr_t)obj;
}
static jstring JNICALL System_mapLibraryName(JNIEnv* env, jclass, jstring name) {
    const char* utf = env->GetStringUTFChars(name, NULL);
    char buf[256];
    snprintf(buf, sizeof(buf), "lib%s.so", utf);
    env->ReleaseStringUTFChars(name, utf);
    return env->NewStringUTF(buf);
}
static void JNICALL System_log(JNIEnv* env, jclass, jchar type, jstring msg, jthrowable) {
    const char* utf = msg ? env->GetStringUTFChars(msg, NULL) : "(null)";
    ALOGI("System.log(%c): %s", (char)type, utf);
    if (msg) env->ReleaseStringUTFChars(msg, utf);
}

static jobjectArray JNICALL System_specialProperties(JNIEnv* env, jclass) {
    const char* props[] = {
#if defined(__aarch64__)
        "os.arch=aarch64",
#elif defined(__x86_64__)
        "os.arch=x86_64",
#else
        "os.arch=unknown",
#endif
        "os.name=Linux",
        "java.io.tmpdir=/tmp",
        "java.home=/tmp/android-root",
        "java.class.path=.",
        "java.library.path=/tmp",
        "java.vm.name=Dalvik",
        "java.vm.version=1.6.0",
        "java.vm.vendor=A2OH",
        "user.home=/tmp",
        "user.dir=/tmp",
        "user.name=shell",
        "user.language=en",
        "user.region=US",
        "file.separator=/",
        "line.separator=\n",
        "path.separator=:",
        "file.encoding=UTF-8",
        "android.icu.impl.ICUBinary.dataPath=",
    };
    int count = sizeof(props) / sizeof(props[0]);
    jclass strClass = env->FindClass("java/lang/String");
    jobjectArray arr = env->NewObjectArray(count, strClass, NULL);
    for (int i = 0; i < count; i++)
        env->SetObjectArrayElement(arr, i, env->NewStringUTF(props[i]));
    return arr;
}

/* ----------------------------------------------------------------
 * java.io.File native methods
 * ---------------------------------------------------------------- */

static jstring JNICALL File_realpath(JNIEnv* env, jclass, jstring jpath) {
    if (jpath == NULL) return NULL;
    const char* path = env->GetStringUTFChars(jpath, NULL);
    char resolved[PATH_MAX];
    char* result = realpath(path, resolved);
    env->ReleaseStringUTFChars(jpath, path);
    if (result == NULL) return jpath;
    return env->NewStringUTF(resolved);
}

static jboolean JNICALL File_setLastModifiedImpl(JNIEnv*, jclass, jstring, jlong) {
    return JNI_FALSE;
}

/* ----------------------------------------------------------------
 * libcore.icu.NativeConverter -- charset conversion bridge
 *
 * Provides UTF-8, US-ASCII, ISO-8859-1 without ICU.
 * Fake converter handles: 1=UTF-8, 2=US-ASCII, 3=ISO-8859-1
 * ---------------------------------------------------------------- */

#define CONV_UTF8      1L
#define CONV_ASCII     2L
#define CONV_LATIN1    3L

static long converterForName(const char* name) {
    if (strcasecmp(name, "UTF-8") == 0 || strcasecmp(name, "UTF8") == 0)
        return CONV_UTF8;
    if (strcasecmp(name, "US-ASCII") == 0 || strcasecmp(name, "ASCII") == 0)
        return CONV_ASCII;
    if (strcasecmp(name, "ISO-8859-1") == 0 || strcasecmp(name, "ISO8859_1") == 0 ||
        strcasecmp(name, "ISO-LATIN-1") == 0 || strcasecmp(name, "LATIN1") == 0)
        return CONV_LATIN1;
    return 0;
}

static jobject JNICALL NativeConverter_charsetForName(JNIEnv* env, jclass, jstring jname) {
    if (jname == NULL) return NULL;
    const char* name = env->GetStringUTFChars(jname, NULL);
    long handle = converterForName(name);
    env->ReleaseStringUTFChars(jname, name);
    if (handle == 0) return NULL;

    const char* canonicalName;
    const char* icuCanonicalName;
    const char** aliases;
    int aliasCount;
    const char* utf8Aliases[] = { "UTF-8", "UTF8", "unicode-1-1-utf-8" };
    const char* asciiAliases[] = { "US-ASCII", "ASCII", "iso-ir-6", "cp367" };
    const char* latin1Aliases[] = { "ISO-8859-1", "ISO8859_1", "iso-ir-100", "latin1", "cp819" };

    switch (handle) {
    case CONV_UTF8:
        canonicalName = "UTF-8"; icuCanonicalName = "UTF-8";
        aliases = utf8Aliases; aliasCount = 3; break;
    case CONV_ASCII:
        canonicalName = "US-ASCII"; icuCanonicalName = "US-ASCII";
        aliases = asciiAliases; aliasCount = 4; break;
    case CONV_LATIN1:
        canonicalName = "ISO-8859-1"; icuCanonicalName = "ISO-8859-1";
        aliases = latin1Aliases; aliasCount = 5; break;
    default: return NULL;
    }

    jclass strClass = env->FindClass("java/lang/String");
    jobjectArray jaliases = env->NewObjectArray(aliasCount, strClass, NULL);
    for (int i = 0; i < aliasCount; i++)
        env->SetObjectArrayElement(jaliases, i, env->NewStringUTF(aliases[i]));

    jclass charsetICU = env->FindClass("java/nio/charset/CharsetICU");
    if (charsetICU == NULL) { env->ExceptionClear(); return NULL; }
    jmethodID ctor = env->GetMethodID(charsetICU, "<init>",
        "(Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;)V");
    if (ctor == NULL) { env->ExceptionClear(); return NULL; }

    return env->NewObject(charsetICU, ctor,
        env->NewStringUTF(canonicalName), env->NewStringUTF(icuCanonicalName), jaliases);
}

static jlong JNICALL NativeConverter_openConverter(JNIEnv* env, jclass, jstring jname) {
    if (jname == NULL) return 0;
    const char* name = env->GetStringUTFChars(jname, NULL);
    long handle = converterForName(name);
    env->ReleaseStringUTFChars(jname, name);
    return (jlong)handle;
}

static void JNICALL NativeConverter_closeConverter(JNIEnv*, jclass, jlong) { }
static void JNICALL NativeConverter_resetByteToChar(JNIEnv*, jclass, jlong) { }
static void JNICALL NativeConverter_resetCharToByte(JNIEnv*, jclass, jlong) { }
static jfloat JNICALL NativeConverter_getAveBytesPerChar(JNIEnv*, jclass, jlong h) {
    return (h == CONV_UTF8) ? 1.1f : 1.0f;
}
static jfloat JNICALL NativeConverter_getAveCharsPerByte(JNIEnv*, jclass, jlong) { return 1.0f; }
static jint JNICALL NativeConverter_getMaxBytesPerChar(JNIEnv*, jclass, jlong h) {
    return (h == CONV_UTF8) ? 4 : 1;
}
static jint JNICALL NativeConverter_getMinBytesPerChar(JNIEnv*, jclass, jlong) { return 1; }

static jbyteArray JNICALL NativeConverter_getSubstitutionBytes(JNIEnv* env, jclass, jlong) {
    jbyteArray arr = env->NewByteArray(1);
    jbyte q = '?';
    env->SetByteArrayRegion(arr, 0, 1, &q);
    return arr;
}
static jboolean JNICALL NativeConverter_contains(JNIEnv*, jclass, jstring, jstring) { return JNI_FALSE; }

static jobjectArray JNICALL NativeConverter_getAvailableCharsetNames(JNIEnv* env, jclass) {
    const char* names[] = { "UTF-8", "US-ASCII", "ISO-8859-1" };
    jclass strClass = env->FindClass("java/lang/String");
    jobjectArray arr = env->NewObjectArray(3, strClass, NULL);
    for (int i = 0; i < 3; i++)
        env->SetObjectArrayElement(arr, i, env->NewStringUTF(names[i]));
    return arr;
}

static void JNICALL NativeConverter_setCallbackDecode(JNIEnv*, jclass, jlong, jint, jint, jstring) { }
static void JNICALL NativeConverter_setCallbackEncode(JNIEnv*, jclass, jlong, jint, jint, jbyteArray) { }

/* encode: char[] -> byte[] */
static jint JNICALL NativeConverter_encode(JNIEnv* env, jclass, jlong handle,
        jcharArray jinput, jint inEnd, jbyteArray joutput, jint outEnd,
        jintArray jdata, jboolean flush) {
    jint data[3];
    env->GetIntArrayRegion(jdata, 0, 3, data);
    int inStart = data[0], outStart = data[1];
    jchar* input = env->GetCharArrayElements(jinput, NULL);
    jbyte* output = env->GetByteArrayElements(joutput, NULL);
    int inPos = inStart, outPos = outStart, result = 0;

    if (handle == CONV_UTF8) {
        while (inPos < inEnd && outPos < outEnd) {
            jchar ch = input[inPos];
            if (ch < 0x80) { output[outPos++] = (jbyte)ch; inPos++; }
            else if (ch < 0x800) {
                if (outPos + 2 > outEnd) { result = 15; break; }
                output[outPos++] = (jbyte)(0xC0 | (ch >> 6));
                output[outPos++] = (jbyte)(0x80 | (ch & 0x3F)); inPos++;
            } else {
                if (outPos + 3 > outEnd) { result = 15; break; }
                output[outPos++] = (jbyte)(0xE0 | (ch >> 12));
                output[outPos++] = (jbyte)(0x80 | ((ch >> 6) & 0x3F));
                output[outPos++] = (jbyte)(0x80 | (ch & 0x3F)); inPos++;
            }
        }
    } else if (handle == CONV_ASCII) {
        while (inPos < inEnd && outPos < outEnd) {
            jchar ch = input[inPos];
            if (ch > 0x7F) { data[2] = 1; result = 12; break; }
            output[outPos++] = (jbyte)ch; inPos++;
        }
    } else {
        while (inPos < inEnd && outPos < outEnd) {
            jchar ch = input[inPos];
            if (ch > 0xFF) { data[2] = 1; result = 10; break; }
            output[outPos++] = (jbyte)ch; inPos++;
        }
    }
    if (result == 0 && inPos < inEnd && outPos >= outEnd) result = 15;
    data[0] = inPos - inStart; data[1] = outPos - outStart;
    env->ReleaseCharArrayElements(jinput, input, JNI_ABORT);
    env->ReleaseByteArrayElements(joutput, output, 0);
    env->SetIntArrayRegion(jdata, 0, 3, data);
    return result;
}

/* decode: byte[] -> char[] */
static jint JNICALL NativeConverter_decode(JNIEnv* env, jclass, jlong handle,
        jbyteArray jinput, jint inEnd, jcharArray joutput, jint outEnd,
        jintArray jdata, jboolean flush) {
    jint data[3];
    env->GetIntArrayRegion(jdata, 0, 3, data);
    int inStart = data[0], outStart = data[1];
    jbyte* input = env->GetByteArrayElements(jinput, NULL);
    jchar* output = env->GetCharArrayElements(joutput, NULL);
    int inPos = inStart, outPos = outStart, result = 0;

    if (handle == CONV_UTF8) {
        while (inPos < inEnd && outPos < outEnd) {
            unsigned char b = (unsigned char)input[inPos];
            if (b < 0x80) { output[outPos++] = (jchar)b; inPos++; }
            else if ((b & 0xE0) == 0xC0) {
                if (inPos + 1 >= inEnd) { if (flush) { data[2]=1; result=11; } break; }
                output[outPos++] = (jchar)(((b & 0x1F) << 6) | (input[inPos+1] & 0x3F));
                inPos += 2;
            } else if ((b & 0xF0) == 0xE0) {
                if (inPos + 2 >= inEnd) { if (flush) { data[2]=1; result=11; } break; }
                output[outPos++] = (jchar)(((b & 0x0F) << 12) |
                    ((input[inPos+1] & 0x3F) << 6) | (input[inPos+2] & 0x3F));
                inPos += 3;
            } else {
                if (inPos + 3 >= inEnd) { if (flush) { data[2]=1; result=11; } break; }
                output[outPos++] = (jchar)0xFFFD; inPos += 4;
            }
        }
    } else {
        while (inPos < inEnd && outPos < outEnd) {
            output[outPos++] = (jchar)((unsigned char)input[inPos]); inPos++;
        }
    }
    if (result == 0 && inPos < inEnd && outPos >= outEnd) result = 15;
    data[0] = inPos - inStart; data[1] = outPos - outStart;
    env->ReleaseByteArrayElements(jinput, input, JNI_ABORT);
    env->ReleaseCharArrayElements(joutput, output, 0);
    env->SetIntArrayRegion(jdata, 0, 3, data);
    return result;
}

/* ----------------------------------------------------------------
 * java.nio.charset.Charsets -- low-level byte conversion
 * ---------------------------------------------------------------- */

static jbyteArray JNICALL Charsets_toUtf8Bytes(JNIEnv* env, jclass,
        jcharArray jchars, jint offset, jint length) {
    jchar* chars = env->GetCharArrayElements(jchars, NULL);
    jbyte* buf = (jbyte*)malloc(length * 3);
    int pos = 0;
    for (int i = 0; i < length; i++) {
        jchar ch = chars[offset + i];
        if (ch < 0x80) { buf[pos++] = (jbyte)ch; }
        else if (ch < 0x800) {
            buf[pos++] = (jbyte)(0xC0 | (ch >> 6));
            buf[pos++] = (jbyte)(0x80 | (ch & 0x3F));
        } else {
            buf[pos++] = (jbyte)(0xE0 | (ch >> 12));
            buf[pos++] = (jbyte)(0x80 | ((ch >> 6) & 0x3F));
            buf[pos++] = (jbyte)(0x80 | (ch & 0x3F));
        }
    }
    env->ReleaseCharArrayElements(jchars, chars, JNI_ABORT);
    jbyteArray result = env->NewByteArray(pos);
    env->SetByteArrayRegion(result, 0, pos, buf);
    free(buf);
    return result;
}

static jbyteArray JNICALL Charsets_toAsciiBytes(JNIEnv* env, jclass,
        jcharArray jchars, jint offset, jint length) {
    jchar* chars = env->GetCharArrayElements(jchars, NULL);
    jbyteArray result = env->NewByteArray(length);
    jbyte* buf = env->GetByteArrayElements(result, NULL);
    for (int i = 0; i < length; i++) {
        jchar ch = chars[offset + i];
        buf[i] = (ch <= 0x7F) ? (jbyte)ch : (jbyte)'?';
    }
    env->ReleaseByteArrayElements(result, buf, 0);
    env->ReleaseCharArrayElements(jchars, chars, JNI_ABORT);
    return result;
}

static jbyteArray JNICALL Charsets_toIsoLatin1Bytes(JNIEnv* env, jclass,
        jcharArray jchars, jint offset, jint length) {
    jchar* chars = env->GetCharArrayElements(jchars, NULL);
    jbyteArray result = env->NewByteArray(length);
    jbyte* buf = env->GetByteArrayElements(result, NULL);
    for (int i = 0; i < length; i++) {
        jchar ch = chars[offset + i];
        buf[i] = (ch <= 0xFF) ? (jbyte)ch : (jbyte)'?';
    }
    env->ReleaseByteArrayElements(result, buf, 0);
    env->ReleaseCharArrayElements(jchars, chars, JNI_ABORT);
    return result;
}

static void JNICALL Charsets_asciiBytesToChars(JNIEnv* env, jclass,
        jbyteArray jbytes, jint offset, jint length, jcharArray jchars) {
    jbyte* bytes = env->GetByteArrayElements(jbytes, NULL);
    jchar* chars = env->GetCharArrayElements(jchars, NULL);
    for (int i = 0; i < length; i++) chars[i] = (jchar)(bytes[offset + i] & 0x7F);
    env->ReleaseCharArrayElements(jchars, chars, 0);
    env->ReleaseByteArrayElements(jbytes, bytes, JNI_ABORT);
}

static void JNICALL Charsets_isoLatin1BytesToChars(JNIEnv* env, jclass,
        jbyteArray jbytes, jint offset, jint length, jcharArray jchars) {
    jbyte* bytes = env->GetByteArrayElements(jbytes, NULL);
    jchar* chars = env->GetCharArrayElements(jchars, NULL);
    for (int i = 0; i < length; i++) chars[i] = (jchar)(bytes[offset + i] & 0xFF);
    env->ReleaseCharArrayElements(jchars, chars, 0);
    env->ReleaseByteArrayElements(jbytes, bytes, JNI_ABORT);
}

/* ----------------------------------------------------------------
 * Registration tables
 * ---------------------------------------------------------------- */

static bool registerClass(JNIEnv* env, const char* className,
                          JNINativeMethod* methods, int count) {
    jclass c = env->FindClass(className);
    if (c == NULL) {
        env->ExceptionClear();
        ALOGW("Class %s not found - skipping bridge registration", className);
        return true;
    }
    int registered = 0;
    for (int i = 0; i < count; i++) {
        if (env->RegisterNatives(c, &methods[i], 1) < 0) {
            env->ExceptionClear();
            ALOGV("Skipped non-native: %s.%s", className, methods[i].name);
        } else {
            registered++;
        }
    }
    ALOGV("Registered libcore bridge: %s (%d/%d methods)", className, registered, count);
    return true;
}

static JNINativeMethod gOsConstantsMethods[] = {
    { "initConstants", "()V", (void*) OsConstants_initConstants },
};

static JNINativeMethod gPosixMethods[] = {
    { "getenv",    "(Ljava/lang/String;)Ljava/lang/String;", (void*) Posix_getenv },
    { "getuid",    "()I", (void*) Posix_getuid },
    { "getgid",    "()I", (void*) Posix_getgid },
    { "geteuid",   "()I", (void*) Posix_geteuid },
    { "getegid",   "()I", (void*) Posix_getegid },
    { "getpid",    "()I", (void*) Posix_getpid },
    { "getppid",   "()I", (void*) Posix_getppid },
    { "sysconf",   "(I)J", (void*) Posix_sysconf },
    { "isatty",    "(Ljava/io/FileDescriptor;)Z", (void*) Posix_isatty },
    { "setenv",    "(Ljava/lang/String;Ljava/lang/String;Z)V", (void*) Posix_setenv },
    { "umask",     "(I)I", (void*) Posix_umask },
    { "getpwuid",  "(I)Llibcore/io/StructPasswd;", (void*) Posix_getpwuid },
    { "stat",      "(Ljava/lang/String;)Llibcore/io/StructStat;", (void*) Posix_stat },
    { "uname",     "()Llibcore/io/StructUtsname;", (void*) Posix_uname },
    { "open",      "(Ljava/lang/String;II)Ljava/io/FileDescriptor;", (void*) Posix_open },
    { "close",     "(Ljava/io/FileDescriptor;)V", (void*) Posix_close },
    { "read",      "(Ljava/io/FileDescriptor;[BII)I", (void*) Posix_read_bytes },
    { "write",     "(Ljava/io/FileDescriptor;[BII)I", (void*) Posix_write_bytes_arr },
    { "writeBytes","(Ljava/io/FileDescriptor;Ljava/lang/Object;II)I", (void*) Posix_writeBytes },
};

static JNINativeMethod gICUMethods[] = {
    { "getIcuVersion", "()Ljava/lang/String;", (void*) ICU_getIcuVersion },
    { "getUnicodeVersion", "()Ljava/lang/String;", (void*) ICU_getUnicodeVersion },
    { "getCldrVersion", "()Ljava/lang/String;", (void*) ICU_getCldrVersion },
    { "getAvailableLocalesNative", "()[Ljava/lang/String;", (void*) ICU_getAvailableLocalesNative },
    { "getDefaultLocale", "()Ljava/lang/String;", (void*) ICU_getDefaultLocale },
    { "setDefaultLocale", "(Ljava/lang/String;)V", (void*) ICU_setDefaultLocale },
    { "toLowerCase", "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;", (void*) ICU_toLowerCase },
    { "toUpperCase", "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;", (void*) ICU_toUpperCase },
    { "initLocaleDataNative", "(Ljava/lang/String;Llibcore/icu/LocaleData;)Z", (void*) ICU_initLocaleDataNative },
    { "getCurrencyCode", "(Ljava/lang/String;)Ljava/lang/String;", (void*) ICU_getCurrencyCode },
    { "getISO3Country", "(Ljava/lang/String;)Ljava/lang/String;", (void*) ICU_getISO3Country },
    { "getISO3Language", "(Ljava/lang/String;)Ljava/lang/String;", (void*) ICU_getISO3Language },
    { "getScript", "(Ljava/lang/String;)Ljava/lang/String;", (void*) ICU_getScript },
    { "getBestDateTimePatternNative", "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;", (void*) ICU_getBestDateTimePatternNative },
    { "getISOLanguagesNative", "()[Ljava/lang/String;", (void*) ICU_getISOLanguagesNative },
    { "getISOCountriesNative", "()[Ljava/lang/String;", (void*) ICU_getISOCountriesNative },
};

static JNINativeMethod gNativeConverterMethods[] = {
    { "charsetForName", "(Ljava/lang/String;)Ljava/nio/charset/Charset;", (void*) NativeConverter_charsetForName },
    { "openConverter", "(Ljava/lang/String;)J", (void*) NativeConverter_openConverter },
    { "closeConverter", "(J)V", (void*) NativeConverter_closeConverter },
    { "resetByteToChar", "(J)V", (void*) NativeConverter_resetByteToChar },
    { "resetCharToByte", "(J)V", (void*) NativeConverter_resetCharToByte },
    { "getAveBytesPerChar", "(J)F", (void*) NativeConverter_getAveBytesPerChar },
    { "getAveCharsPerByte", "(J)F", (void*) NativeConverter_getAveCharsPerByte },
    { "getMaxBytesPerChar", "(J)I", (void*) NativeConverter_getMaxBytesPerChar },
    { "getMinBytesPerChar", "(J)I", (void*) NativeConverter_getMinBytesPerChar },
    { "getSubstitutionBytes", "(J)[B", (void*) NativeConverter_getSubstitutionBytes },
    { "contains", "(Ljava/lang/String;Ljava/lang/String;)Z", (void*) NativeConverter_contains },
    { "getAvailableCharsetNames", "()[Ljava/lang/String;", (void*) NativeConverter_getAvailableCharsetNames },
    { "encode", "(J[CI[BI[IZ)I", (void*) NativeConverter_encode },
    { "decode", "(J[BI[CI[IZ)I", (void*) NativeConverter_decode },
    { "setCallbackDecode", "(JIILjava/lang/String;)V", (void*) NativeConverter_setCallbackDecode },
    { "setCallbackEncode", "(JII[B)V", (void*) NativeConverter_setCallbackEncode },
};

static JNINativeMethod gFileMethods[] = {
    { "realpath", "(Ljava/lang/String;)Ljava/lang/String;", (void*) File_realpath },
    { "setLastModifiedImpl", "(Ljava/lang/String;J)Z", (void*) File_setLastModifiedImpl },
};

static JNINativeMethod gCharsetsMethods[] = {
    { "toUtf8Bytes", "([CII)[B", (void*) Charsets_toUtf8Bytes },
    { "toAsciiBytes", "([CII)[B", (void*) Charsets_toAsciiBytes },
    { "toIsoLatin1Bytes", "([CII)[B", (void*) Charsets_toIsoLatin1Bytes },
    { "asciiBytesToChars", "([BII[C)V", (void*) Charsets_asciiBytesToChars },
    { "isoLatin1BytesToChars", "([BII[C)V", (void*) Charsets_isoLatin1BytesToChars },
};

static JNINativeMethod gSystemMethods[] = {
    { "specialProperties", "()[Ljava/lang/String;", (void*) System_specialProperties },
    { "currentTimeMillis", "()J", (void*) System_currentTimeMillis },
    { "nanoTime", "()J", (void*) System_nanoTime },
    { "identityHashCode", "(Ljava/lang/Object;)I", (void*) System_identityHashCode },
    { "mapLibraryName", "(Ljava/lang/String;)Ljava/lang/String;", (void*) System_mapLibraryName },
    { "log", "(CLjava/lang/String;Ljava/lang/Throwable;)V", (void*) System_log },
};

bool dvmRegisterLibcoreBridge(JNIEnv* env) {
    if (!registerClass(env, "libcore/io/OsConstants", gOsConstantsMethods, 1))
        return false;
    if (!registerClass(env, "libcore/io/Posix",
                       gPosixMethods, sizeof(gPosixMethods)/sizeof(gPosixMethods[0])))
        return false;
    if (!registerClass(env, "libcore/icu/ICU",
                       gICUMethods, sizeof(gICUMethods)/sizeof(gICUMethods[0])))
        return false;
    if (!registerClass(env, "libcore/icu/NativeConverter",
                       gNativeConverterMethods, sizeof(gNativeConverterMethods)/sizeof(gNativeConverterMethods[0])))
        return false;
    if (!registerClass(env, "java/lang/System",
                       gSystemMethods, sizeof(gSystemMethods)/sizeof(gSystemMethods[0])))
        return false;
    if (!registerClass(env, "java/io/File",
                       gFileMethods, sizeof(gFileMethods)/sizeof(gFileMethods[0])))
        return false;
    if (!registerClass(env, "java/nio/charset/Charsets",
                       gCharsetsMethods, sizeof(gCharsetsMethods)/sizeof(gCharsetsMethods[0])))
        return false;
    return true;
}
