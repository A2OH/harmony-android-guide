package android.system;

/**
 * Android-compatible OsConstants shim.
 * Provides POSIX/Linux constants for use with android.system.Os.
 * Values match Linux/aarch64 kernel headers.
 */
public final class OsConstants {
    private OsConstants() {}

    // --- File open flags ---
    public static final int O_RDONLY   = 0;
    public static final int O_WRONLY   = 1;
    public static final int O_RDWR     = 2;
    public static final int O_CREAT    = 0x40;
    public static final int O_EXCL     = 0x80;
    public static final int O_NOCTTY   = 0x100;
    public static final int O_TRUNC    = 0x200;
    public static final int O_APPEND   = 0x400;
    public static final int O_NONBLOCK = 0x800;
    public static final int O_SYNC     = 0x101000;
    public static final int O_DSYNC    = 0x1000;
    public static final int O_CLOEXEC  = 0x80000;
    public static final int O_DIRECTORY = 0x10000;
    public static final int O_NOFOLLOW = 0x20000;
    public static final int O_LARGEFILE = 0x8000;
    public static final int O_PATH     = 0x200000;

    // --- lseek whence ---
    public static final int SEEK_SET = 0;
    public static final int SEEK_CUR = 1;
    public static final int SEEK_END = 2;

    // --- access() mode bits ---
    public static final int F_OK = 0;
    public static final int X_OK = 1;
    public static final int W_OK = 2;
    public static final int R_OK = 4;

    // --- File mode permission bits ---
    public static final int S_ISUID  = 0x800;
    public static final int S_ISGID  = 0x400;
    public static final int S_ISVTX  = 0x200;
    public static final int S_IRWXU  = 0x1C0;
    public static final int S_IRUSR  = 0x100;
    public static final int S_IWUSR  = 0x80;
    public static final int S_IXUSR  = 0x40;
    public static final int S_IRWXG  = 0x38;
    public static final int S_IRGRP  = 0x20;
    public static final int S_IWGRP  = 0x10;
    public static final int S_IXGRP  = 0x08;
    public static final int S_IRWXO  = 0x07;
    public static final int S_IROTH  = 0x04;
    public static final int S_IWOTH  = 0x02;
    public static final int S_IXOTH  = 0x01;
    // File type bits
    public static final int S_IFMT   = 0xF000;
    public static final int S_IFSOCK = 0xC000;
    public static final int S_IFLNK  = 0xA000;
    public static final int S_IFREG  = 0x8000;
    public static final int S_IFBLK  = 0x6000;
    public static final int S_IFDIR  = 0x4000;
    public static final int S_IFCHR  = 0x2000;
    public static final int S_IFIFO  = 0x1000;

    public static boolean S_ISBLK(int mode)  { return (mode & S_IFMT) == S_IFBLK; }
    public static boolean S_ISCHR(int mode)  { return (mode & S_IFMT) == S_IFCHR; }
    public static boolean S_ISDIR(int mode)  { return (mode & S_IFMT) == S_IFDIR; }
    public static boolean S_ISFIFO(int mode) { return (mode & S_IFMT) == S_IFIFO; }
    public static boolean S_ISLNK(int mode)  { return (mode & S_IFMT) == S_IFLNK; }
    public static boolean S_ISREG(int mode)  { return (mode & S_IFMT) == S_IFREG; }
    public static boolean S_ISSOCK(int mode) { return (mode & S_IFMT) == S_IFSOCK; }

    // --- Address families ---
    public static final int AF_UNSPEC  = 0;
    public static final int AF_UNIX    = 1;
    public static final int AF_LOCAL   = 1;
    public static final int AF_INET    = 2;
    public static final int AF_AX25    = 3;
    public static final int AF_IPX     = 4;
    public static final int AF_INET6   = 10;
    public static final int AF_NETLINK = 16;
    public static final int AF_PACKET  = 17;

    // --- Socket types ---
    public static final int SOCK_STREAM    = 1;
    public static final int SOCK_DGRAM     = 2;
    public static final int SOCK_RAW       = 3;
    public static final int SOCK_RDM       = 4;
    public static final int SOCK_SEQPACKET = 5;
    public static final int SOCK_CLOEXEC   = 0x80000;
    public static final int SOCK_NONBLOCK  = 0x800;

    // --- IP protocols ---
    public static final int IPPROTO_IP    = 0;
    public static final int IPPROTO_ICMP  = 1;
    public static final int IPPROTO_TCP   = 6;
    public static final int IPPROTO_UDP   = 17;
    public static final int IPPROTO_IPV6  = 41;
    public static final int IPPROTO_RAW   = 255;

    // --- SOL_SOCKET and socket options ---
    public static final int SOL_SOCKET     = 1;
    public static final int SO_DEBUG       = 1;
    public static final int SO_REUSEADDR   = 2;
    public static final int SO_TYPE        = 3;
    public static final int SO_ERROR       = 4;
    public static final int SO_DONTROUTE   = 5;
    public static final int SO_BROADCAST   = 6;
    public static final int SO_SNDBUF      = 7;
    public static final int SO_RCVBUF      = 8;
    public static final int SO_KEEPALIVE   = 9;
    public static final int SO_OOBINLINE   = 10;
    public static final int SO_LINGER      = 13;
    public static final int SO_RCVTIMEO    = 20;
    public static final int SO_SNDTIMEO    = 21;
    public static final int SO_RCVLOWAT    = 18;
    public static final int SO_SNDLOWAT    = 19;
    public static final int SO_REUSEPORT   = 15;
    // TCP options
    public static final int TCP_NODELAY    = 1;
    public static final int TCP_KEEPIDLE   = 4;
    public static final int TCP_KEEPINTVL  = 5;
    public static final int TCP_KEEPCNT    = 6;

    // --- poll() event flags ---
    public static final short POLLIN   = 0x001;
    public static final short POLLPRI  = 0x002;
    public static final short POLLOUT  = 0x004;
    public static final short POLLERR  = 0x008;
    public static final short POLLHUP  = 0x010;
    public static final short POLLNVAL = 0x020;
    public static final short POLLRDNORM = 0x040;
    public static final short POLLWRNORM = 0x100;

    // --- fcntl() commands ---
    public static final int F_DUPFD   = 0;
    public static final int F_GETFD   = 1;
    public static final int F_SETFD   = 2;
    public static final int F_GETFL   = 3;
    public static final int F_SETFL   = 4;
    public static final int F_GETLK   = 5;
    public static final int F_SETLK   = 6;
    public static final int F_SETLKW  = 7;
    public static final int FD_CLOEXEC = 1;

    // --- mmap() protection flags ---
    public static final int PROT_NONE  = 0;
    public static final int PROT_READ  = 1;
    public static final int PROT_WRITE = 2;
    public static final int PROT_EXEC  = 4;

    // --- mmap() flags ---
    public static final int MAP_SHARED    = 1;
    public static final int MAP_PRIVATE   = 2;
    public static final int MAP_FIXED     = 0x10;
    public static final int MAP_ANONYMOUS = 0x20;
    public static final int MAP_ANON      = 0x20;
    public static final int MAP_FAILED    = -1; // sentinel, not a real address

    // --- Signals ---
    public static final int SIGHUP  = 1;
    public static final int SIGINT  = 2;
    public static final int SIGQUIT = 3;
    public static final int SIGILL  = 4;
    public static final int SIGTRAP = 5;
    public static final int SIGABRT = 6;
    public static final int SIGBUS  = 7;
    public static final int SIGFPE  = 8;
    public static final int SIGKILL = 9;
    public static final int SIGUSR1 = 10;
    public static final int SIGSEGV = 11;
    public static final int SIGUSR2 = 12;
    public static final int SIGPIPE = 13;
    public static final int SIGALRM = 14;
    public static final int SIGTERM = 15;
    public static final int SIGCHLD = 17;
    public static final int SIGCONT = 18;
    public static final int SIGSTOP = 19;
    public static final int SIGTSTP = 20;
    public static final int SIGTTIN = 21;
    public static final int SIGTTOU = 22;
    public static final int SIGURG  = 23;
    public static final int SIGXCPU = 24;
    public static final int SIGXFSZ = 25;
    public static final int SIGVTALRM = 26;
    public static final int SIGPROF = 27;
    public static final int SIGWINCH = 28;
    public static final int SIGPOLL = 29;
    public static final int SIGPWR  = 30;
    public static final int SIGSYS  = 31;

    // --- sysconf() names ---
    public static final int _SC_ARG_MAX     = 0;
    public static final int _SC_CHILD_MAX   = 1;
    public static final int _SC_CLK_TCK     = 2;
    public static final int _SC_NGROUPS_MAX = 3;
    public static final int _SC_OPEN_MAX    = 4;
    public static final int _SC_JOB_CONTROL = 7;
    public static final int _SC_SAVED_IDS   = 8;
    public static final int _SC_VERSION     = 29;
    public static final int _SC_PAGE_SIZE   = 30;
    public static final int _SC_PAGESIZE    = 30;
    public static final int _SC_NPROCESSORS_CONF = 83;
    public static final int _SC_NPROCESSORS_ONLN = 84;
    public static final int _SC_PHYS_PAGES  = 85;

    // --- errno values ---
    public static final int EPERM         = 1;
    public static final int ENOENT        = 2;
    public static final int ESRCH         = 3;
    public static final int EINTR         = 4;
    public static final int EIO           = 5;
    public static final int ENXIO         = 6;
    public static final int E2BIG         = 7;
    public static final int ENOEXEC       = 8;
    public static final int EBADF         = 9;
    public static final int ECHILD        = 10;
    public static final int EAGAIN        = 11;
    public static final int EWOULDBLOCK   = 11;
    public static final int ENOMEM        = 12;
    public static final int EACCES        = 13;
    public static final int EFAULT        = 14;
    public static final int EBUSY         = 16;
    public static final int EEXIST        = 17;
    public static final int EXDEV         = 18;
    public static final int ENODEV        = 19;
    public static final int ENOTDIR       = 20;
    public static final int EISDIR        = 21;
    public static final int EINVAL        = 22;
    public static final int ENFILE        = 23;
    public static final int EMFILE        = 24;
    public static final int ENOTTY        = 25;
    public static final int EFBIG         = 27;
    public static final int ENOSPC        = 28;
    public static final int ESPIPE        = 29;
    public static final int EROFS         = 30;
    public static final int EMLINK        = 31;
    public static final int EPIPE         = 32;
    public static final int ERANGE        = 34;
    public static final int EDEADLK       = 35;
    public static final int ENAMETOOLONG  = 36;
    public static final int ENOLCK        = 37;
    public static final int ENOSYS        = 38;
    public static final int ENOTEMPTY     = 39;
    public static final int ELOOP         = 40;
    public static final int ENOMSG        = 42;
    public static final int EIDRM         = 43;
    public static final int ENOSTR        = 60;
    public static final int ENODATA       = 61;
    public static final int ETIME         = 62;
    public static final int ENOSR         = 63;
    public static final int EREMOTE       = 66;
    public static final int ENOLINK       = 67;
    public static final int EPROTO        = 71;
    public static final int EMULTIHOP     = 72;
    public static final int EBADMSG       = 74;
    public static final int EOVERFLOW     = 75;
    public static final int EILSEQ        = 84;
    public static final int EUSERS        = 87;
    public static final int ENOTSOCK      = 88;
    public static final int EDESTADDRREQ  = 89;
    public static final int EMSGSIZE      = 90;
    public static final int EPROTOTYPE    = 91;
    public static final int ENOPROTOOPT   = 92;
    public static final int EPROTONOSUPPORT = 93;
    public static final int ESOCKTNOSUPPORT = 94;
    public static final int EOPNOTSUPP    = 95;
    public static final int EAFNOSUPPORT  = 97;
    public static final int EADDRINUSE    = 98;
    public static final int EADDRNOTAVAIL = 99;
    public static final int ENETDOWN      = 100;
    public static final int ENETUNREACH   = 101;
    public static final int ENETRESET     = 102;
    public static final int ECONNABORTED  = 103;
    public static final int ECONNRESET    = 104;
    public static final int ENOBUFS       = 105;
    public static final int EISCONN       = 106;
    public static final int ENOTCONN      = 107;
    public static final int ESHUTDOWN     = 108;
    public static final int ETOOMANYREFS  = 109;
    public static final int ETIMEDOUT     = 110;
    public static final int ECONNREFUSED  = 111;
    public static final int EHOSTDOWN     = 112;
    public static final int EHOSTUNREACH  = 113;
    public static final int EALREADY      = 114;
    public static final int EINPROGRESS   = 115;
    public static final int ESTALE        = 116;
    public static final int ECANCELED     = 125;

    // --- Linux capabilities ---
    public static final int CAP_CHOWN            = 0;
    public static final int CAP_DAC_OVERRIDE      = 1;
    public static final int CAP_DAC_READ_SEARCH   = 2;
    public static final int CAP_FOWNER            = 3;
    public static final int CAP_FSETID            = 4;
    public static final int CAP_KILL              = 5;
    public static final int CAP_SETGID            = 6;
    public static final int CAP_SETUID            = 7;
    public static final int CAP_SETPCAP           = 8;
    public static final int CAP_LINUX_IMMUTABLE   = 9;
    public static final int CAP_NET_BIND_SERVICE  = 10;
    public static final int CAP_NET_BROADCAST     = 11;
    public static final int CAP_NET_ADMIN         = 12;
    public static final int CAP_NET_RAW           = 13;
    public static final int CAP_IPC_LOCK          = 14;
    public static final int CAP_IPC_OWNER         = 15;
    public static final int CAP_SYS_MODULE        = 16;
    public static final int CAP_SYS_RAWIO         = 17;
    public static final int CAP_SYS_CHROOT        = 18;
    public static final int CAP_SYS_PTRACE        = 19;
    public static final int CAP_SYS_PACCT         = 20;
    public static final int CAP_SYS_ADMIN         = 21;
    public static final int CAP_SYS_BOOT          = 22;
    public static final int CAP_SYS_NICE          = 23;
    public static final int CAP_SYS_RESOURCE      = 24;
    public static final int CAP_SYS_TIME          = 25;
    public static final int CAP_SYS_TTY_CONFIG    = 26;
    public static final int CAP_MKNOD             = 27;
    public static final int CAP_LEASE             = 28;
    public static final int CAP_AUDIT_WRITE       = 29;
    public static final int CAP_AUDIT_CONTROL     = 30;
    public static final int CAP_SETFCAP           = 31;
    public static final int CAP_MAC_OVERRIDE      = 32;
    public static final int CAP_MAC_ADMIN         = 33;
    public static final int CAP_SYSLOG            = 34;
    public static final int CAP_WAKE_ALARM        = 35;
    public static final int CAP_BLOCK_SUSPEND     = 36;
    public static final int CAP_AUDIT_READ        = 37;

    // --- Miscellaneous ---
    public static final int EXIT_SUCCESS = 0;
    public static final int EXIT_FAILURE = 1;
    public static final int STDIN_FILENO  = 0;
    public static final int STDOUT_FILENO = 1;
    public static final int STDERR_FILENO = 2;

    // --- IP options ---
    public static final int IPTOS_LOWDELAY    = 0x10;
    public static final int IPTOS_THROUGHPUT  = 0x08;
    public static final int IPTOS_RELIABILITY = 0x04;
    public static final int IP_TOS            = 1;
    public static final int IP_TTL            = 2;
    public static final int IP_MULTICAST_IF   = 32;
    public static final int IP_MULTICAST_TTL  = 33;
    public static final int IP_MULTICAST_LOOP = 34;
    public static final int IP_ADD_MEMBERSHIP = 35;
    public static final int IPV6_MULTICAST_IF = 17;
    public static final int IPV6_MULTICAST_HOPS = 18;
    public static final int IPV6_MULTICAST_LOOP = 19;
    public static final int IPV6_V6ONLY       = 26;
    public static final int IPV6_TCLASS       = 67;
}
