/*
 * Minimal OH AppSpawn TLV protocol test client.
 *
 * Connects to /dev/unix/socket/AppSpawnX and sends a canned binary spawn
 * request (magic 0xEF201234 + 280B header + TLV_BUNDLE_INFO + TLV_DAC_INFO).
 * Used to verify adapter appspawn-x's binary TLV parser without depending
 * on foundation/appms routing fix.
 */
#include <arpa/inet.h>
#include <errno.h>
#include <stdint.h>
#include <stdio.h>
#include <string.h>
#include <sys/socket.h>
#include <sys/types.h>
#include <sys/un.h>
#include <unistd.h>

#pragma pack(4)
typedef struct {
    uint32_t magic;
    uint32_t msgType;
    uint32_t msgLen;
    uint32_t msgId;
    uint32_t tlvCount;
    char processName[256];
} AppSpawnMsg;

typedef struct {
    uint16_t tlvLen;
    uint16_t tlvType;
} AppSpawnTlv;

typedef struct {
    uint32_t bundleIndex;
    char bundleName[128];
} AppSpawnMsgBundleInfo;

typedef struct {
    uint32_t uid;
    uint32_t gid;
    uint32_t gidCount;
    uint32_t gidTable[64];
    char userName[64];
} AppDacInfo;

typedef struct {
    int result;
    int pid;
    uint64_t checkPointId;
} AppSpawnResult;

typedef struct {
    AppSpawnMsg msgHdr;
    AppSpawnResult result;
    uint64_t checkPointId;
} AppSpawnResponseMsg;
#pragma pack()

int main(int argc, char** argv) {
    const char* sock_path = (argc > 1) ? argv[1] : "/dev/unix/socket/AppSpawnX";

    int fd = socket(AF_UNIX, SOCK_STREAM, 0);
    if (fd < 0) { perror("socket"); return 1; }

    struct sockaddr_un addr = {0};
    addr.sun_family = AF_UNIX;
    strncpy(addr.sun_path, sock_path, sizeof(addr.sun_path) - 1);
    if (connect(fd, (struct sockaddr*)&addr, sizeof(addr)) < 0) {
        perror("connect"); close(fd); return 1;
    }
    printf("connected to %s\n", sock_path);

    // Compose a minimal spawn message:
    // Header (280B) + TLV_BUNDLE_INFO + TLV_DAC_INFO, all 4-byte aligned.
    // TLV #0: BUNDLE_INFO payload = sizeof(bundleIndex=4) + "com.example.helloworld\0" padded
    const char* bundle = "com.example.helloworld";
    size_t bundleStrLen = strlen(bundle) + 1;
    size_t bundleTlvPayload = sizeof(uint32_t) + ((bundleStrLen + 3) & ~3u);
    // TLV #1: DAC_INFO full struct
    size_t dacTlvPayload = sizeof(AppDacInfo);

    uint32_t tlvBytes = (uint32_t)(sizeof(AppSpawnTlv) + bundleTlvPayload
                                 + sizeof(AppSpawnTlv) + dacTlvPayload);
    uint32_t msgLen = (uint32_t)sizeof(AppSpawnMsg) + tlvBytes;

    uint8_t buf[4096] = {0};
    AppSpawnMsg* hdr = (AppSpawnMsg*)buf;
    hdr->magic = 0xEF201234u;
    hdr->msgType = 0;  // MSG_APP_SPAWN
    hdr->msgLen = msgLen;
    hdr->msgId = 0x12345678u;
    hdr->tlvCount = 2;
    strncpy(hdr->processName, "com.example.helloworld", sizeof(hdr->processName) - 1);

    size_t off = sizeof(AppSpawnMsg);

    // TLV_BUNDLE_INFO
    AppSpawnTlv* tlv0 = (AppSpawnTlv*)(buf + off);
    tlv0->tlvType = 0;  // TLV_BUNDLE_INFO
    tlv0->tlvLen = (uint16_t)(sizeof(AppSpawnTlv) + bundleTlvPayload);
    off += sizeof(AppSpawnTlv);
    AppSpawnMsgBundleInfo* bi = (AppSpawnMsgBundleInfo*)(buf + off);
    bi->bundleIndex = 0;
    strncpy(bi->bundleName, bundle, sizeof(bi->bundleName) - 1);
    off += bundleTlvPayload;

    // TLV_DAC_INFO
    AppSpawnTlv* tlv1 = (AppSpawnTlv*)(buf + off);
    tlv1->tlvType = 2;  // TLV_DAC_INFO
    tlv1->tlvLen = (uint16_t)(sizeof(AppSpawnTlv) + dacTlvPayload);
    off += sizeof(AppSpawnTlv);
    AppDacInfo* dac = (AppDacInfo*)(buf + off);
    dac->uid = 20010042;
    dac->gid = 20010042;
    dac->gidCount = 2;
    dac->gidTable[0] = 9998;
    dac->gidTable[1] = 9997;
    strncpy(dac->userName, "testuser", sizeof(dac->userName) - 1);
    off += dacTlvPayload;

    printf("sending msg: total %u bytes (header 280 + tlv %u), tlvCount=%u\n",
           msgLen, tlvBytes, hdr->tlvCount);
    ssize_t n = send(fd, buf, off, 0);
    if (n < 0) { perror("send"); close(fd); return 1; }
    printf("sent %zd bytes\n", n);

    AppSpawnResponseMsg resp = {0};
    n = recv(fd, &resp, sizeof(resp), MSG_WAITALL);
    if (n != sizeof(resp)) {
        printf("recv n=%zd errno=%s (expected %zu)\n",
               n, strerror(errno), sizeof(resp));
    } else {
        printf("response: magic=0x%08x msgType=%u msgLen=%u msgId=0x%x "
               "tlvCount=%u result=%d pid=%d proc='%s'\n",
               resp.msgHdr.magic, resp.msgHdr.msgType, resp.msgHdr.msgLen,
               resp.msgHdr.msgId, resp.msgHdr.tlvCount,
               resp.result.result, resp.result.pid, resp.msgHdr.processName);
    }

    close(fd);
    return 0;
}
