/*
 * apk_installer.cpp
 *
 * APK file deployment implementation.
 * Handles: APK copy, native lib extraction, dex2oat, data directory creation.
 */
#include "apk_installer.h"

#include <cerrno>
#include <cstring>
#include <fstream>
#include <sys/stat.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <unistd.h>
#include <unzip.h>  // minizip
#include <zip.h>    // minizip writer

#include "hilog/log.h"
#include "directory_ex.h"  // OHOS::ForceCreateDirectory
#include "template_entry_hap.h"  // ohos_adapter_template_resources_hap[]

namespace oh_adapter {

namespace {

#undef LOG_DOMAIN
#undef LOG_TAG
constexpr unsigned int LOG_DOMAIN = 0xD001802;
constexpr const char* LOG_TAG = "ApkInstaller";

#define LOGI(...) OHOS::HiviewDFX::HiLog::Info({LOG_CORE, LOG_DOMAIN, LOG_TAG}, __VA_ARGS__)
#define LOGW(...) OHOS::HiviewDFX::HiLog::Warn({LOG_CORE, LOG_DOMAIN, LOG_TAG}, __VA_ARGS__)
#define LOGE(...) OHOS::HiviewDFX::HiLog::Error({LOG_CORE, LOG_DOMAIN, LOG_TAG}, __VA_ARGS__)

// Buffer size for file copy and ZIP extraction
constexpr size_t COPY_BUFFER_SIZE = 65536;

bool MkdirRecursive(const std::string& path, mode_t mode) {
    bool ok = OHOS::ForceCreateDirectory(path);
    if (!ok) {
        LOGE("MkdirRecursive FAIL path=%{public}s errno=%{public}d (%{public}s)",
             path.c_str(), errno, strerror(errno));
    }
    return ok;
}

}  // namespace

const std::vector<std::string>& ApkInstaller::GetSupportedAbis() {
    static const std::vector<std::string> abis = {
        "arm64-v8a",      // Primary: 64-bit ARM
        "armeabi-v7a",    // Fallback: 32-bit ARM
        "armeabi"         // Legacy: ARMv5
    };
    return abis;
}

std::string ApkInstaller::SelectPrimaryAbi(const std::string& apkPath) {
    unzFile zip = unzOpen(apkPath.c_str());
    if (zip == nullptr) {
        LOGE("SelectPrimaryAbi: failed to open APK: %{public}s", apkPath.c_str());
        return "";
    }

    std::string selectedAbi;
    for (const auto& abi : GetSupportedAbis()) {
        std::string prefix = "lib/" + abi + "/";
        // Check if any entry starts with this prefix
        if (unzGoToFirstFile(zip) != UNZ_OK) break;
        do {
            char filename[256];
            unz_file_info fileInfo;
            if (unzGetCurrentFileInfo(zip, &fileInfo, filename, sizeof(filename),
                                       nullptr, 0, nullptr, 0) == UNZ_OK) {
                if (std::string(filename).compare(0, prefix.size(), prefix) == 0) {
                    selectedAbi = abi;
                    break;
                }
            }
        } while (unzGoToNextFile(zip) == UNZ_OK);

        if (!selectedAbi.empty()) break;
    }

    unzClose(zip);
    if (!selectedAbi.empty()) {
        LOGI("Selected ABI: %{public}s for %{public}s", selectedAbi.c_str(), apkPath.c_str());
    }
    return selectedAbi;
}

ApkInstaller::InstallResult ApkInstaller::DeployApk(
        const std::string& srcApkPath,
        const std::string& packageName,
        int32_t uid, int32_t gid) {

    InstallResult result;
    std::string installDir = std::string(ANDROID_INSTALL_DIR) + "/" + packageName;

    LOGI("DeployApk: package=%{public}s, src=%{public}s",
         packageName.c_str(), srcApkPath.c_str());

    // 1. Create installation directories
    if (!CreateInstallDirs(packageName)) {
        result.errorMsg = "Failed to create installation directories";
        LOGE("%{public}s", result.errorMsg.c_str());
        return result;
    }

    // 2. Copy APK to install directory
    std::string destApk = installDir + "/base.apk";
    if (!CopyApk(srcApkPath, destApk)) {
        result.errorMsg = "Failed to copy APK file";
        LOGE("%{public}s", result.errorMsg.c_str());
        return result;
    }
    result.installedApkPath = destApk;

    // 3. Extract native libraries
    std::string primaryAbi = SelectPrimaryAbi(destApk);
    if (!primaryAbi.empty()) {
        std::string libDir = installDir + "/lib/" + primaryAbi;
        if (!ExtractNativeLibs(destApk, libDir, primaryAbi)) {
            result.errorMsg = "Failed to extract native libraries";
            LOGE("%{public}s", result.errorMsg.c_str());
            return result;
        }
        result.nativeLibPath = libDir;
    }

    // 4. DEX optimization (dex2oat)
    std::string isa = "arm64";
    std::string oatDir = installDir + "/oat/" + isa;
    MkdirRecursive(oatDir, 0755);
    if (!RunDexOpt(destApk, oatDir, uid, isa, "speed")) {
        // DEX opt failure is non-fatal; app can still run in interpreted mode
        LOGW("DEX optimization failed, app will run in interpreted mode");
    } else {
        result.oatDir = oatDir;
    }

    // 5. Create Android data directories
    if (!CreateDataDirs(packageName, uid, gid)) {
        result.errorMsg = "Failed to create data directories";
        LOGE("%{public}s", result.errorMsg.c_str());
        return result;
    }

    // 6. Set ownership on install directory
    SetPermissions(installDir, uid, gid, 0755);

    result.success = true;
    LOGI("DeployApk completed: package=%{public}s, apk=%{public}s",
         packageName.c_str(), result.installedApkPath.c_str());
    return result;
}

// ============================================================================
// ExtractAndPackResourceHap — synthesize OH "resources HAP" from APK icon
// (方案 2b: template + ZIP byte-level replacement, see appendix C of
// doc/apk_installation_design.html)
// ============================================================================
//
// Steps:
//   1. Open APK, find the launcher icon (try mipmap-xxxhdpi → xxhdpi → hdpi
//      → mdpi). Read bytes into memory.
//   2. Write embedded template HAP bytes to a temp file (minizip needs file IO).
//   3. Open temp file as ZIP READ, open outHapPath as ZIP WRITE.
//   4. For each entry in template:
//        - If name is resources/base/media/{icon,app_icon}.png →
//          write the APK icon bytes instead of template's placeholder
//        - Else copy bytes verbatim
//   5. Close, remove temp file, chmod outHapPath to 0644.
//
// Why a temp file:
//   minizip's unzOpen takes a file path, not memory. Could use ioapi_mem.c
//   for in-memory read, but it's not always linked into OH minizip. File-based
//   path keeps integration simple (~10 ms overhead for 15KB write+read).
//
// Why we replace BOTH icon.png AND app_icon.png:
//   resources.index has two media entries (one app-level, one ability-level).
//   Both should render the same APK icon for visual consistency. The
//   template's placeholder PNG is ~6KB, the APK icon is ~25KB.

namespace {

constexpr size_t kZipBufSize = 65536;

// APK launcher icon search order. Higher density first.
// Most modern APKs ship xxxhdpi as primary; older ones may only have hdpi.
const std::vector<std::string>& GetApkIconCandidates() {
    static const std::vector<std::string> paths = {
        "res/mipmap-xxxhdpi-v4/ic_launcher.png",
        "res/mipmap-xxhdpi-v4/ic_launcher.png",
        "res/mipmap-xhdpi-v4/ic_launcher.png",
        "res/mipmap-hdpi-v4/ic_launcher.png",
        "res/mipmap-mdpi-v4/ic_launcher.png",
        "res/mipmap-xxxhdpi-v4/ic_launcher.webp",
        "res/mipmap-xxhdpi-v4/ic_launcher.webp",
        "res/mipmap-xhdpi-v4/ic_launcher.webp",
        "res/mipmap-hdpi-v4/ic_launcher.webp",
        "res/mipmap-mdpi-v4/ic_launcher.webp",
        // Drawable fallback (older APKs without mipmap)
        "res/drawable-xxxhdpi-v4/ic_launcher.png",
        "res/drawable-xxhdpi-v4/ic_launcher.png",
        "res/drawable-xhdpi-v4/ic_launcher.png",
        "res/drawable-hdpi-v4/ic_launcher.png",
        "res/drawable-mdpi-v4/ic_launcher.png",
    };
    return paths;
}

bool ReadApkLauncherIcon(const std::string& apkPath, std::vector<uint8_t>& out) {
    unzFile zf = unzOpen(apkPath.c_str());
    if (zf == nullptr) {
        LOGE("ReadApkLauncherIcon: cannot open APK %{public}s", apkPath.c_str());
        return false;
    }
    bool found = false;
    for (const auto& candidate : GetApkIconCandidates()) {
        if (unzLocateFile(zf, candidate.c_str(), 0) != UNZ_OK) continue;
        unz_file_info info{};
        if (unzGetCurrentFileInfo(zf, &info, nullptr, 0, nullptr, 0, nullptr, 0) != UNZ_OK) continue;
        if (unzOpenCurrentFile(zf) != UNZ_OK) continue;
        out.resize(info.uncompressed_size);
        int n = unzReadCurrentFile(zf, out.data(), info.uncompressed_size);
        unzCloseCurrentFile(zf);
        if (n != static_cast<int>(info.uncompressed_size)) {
            LOGE("ReadApkLauncherIcon: short read for %{public}s (got %{public}d, want %{public}u)",
                 candidate.c_str(), n, info.uncompressed_size);
            continue;
        }
        LOGI("ReadApkLauncherIcon: extracted %{public}s (%{public}zu bytes)",
             candidate.c_str(), out.size());
        found = true;
        break;
    }
    unzClose(zf);
    return found;
}

bool CopyZipEntry(unzFile src, zipFile dst, const std::string& name,
                  const unz_file_info& info,
                  const std::vector<uint8_t>* overrideData = nullptr) {
    zip_fileinfo zfi{};
    // Preserve mtime if available (info.dosDate); leave as 0 otherwise — OH
    // restool-built HAPs use epoch 1981-01-01 placeholders too.
    if (zipOpenNewFileInZip(dst, name.c_str(), &zfi,
                            nullptr, 0, nullptr, 0, nullptr,
                            Z_DEFLATED, Z_DEFAULT_COMPRESSION) != ZIP_OK) {
        LOGE("CopyZipEntry: zipOpenNewFileInZip failed for %{public}s", name.c_str());
        return false;
    }

    bool ok = true;
    if (overrideData) {
        if (zipWriteInFileInZip(dst, overrideData->data(), overrideData->size()) != ZIP_OK) {
            LOGE("CopyZipEntry: zipWriteInFileInZip override failed for %{public}s", name.c_str());
            ok = false;
        }
    } else {
        if (unzOpenCurrentFile(src) != UNZ_OK) {
            LOGE("CopyZipEntry: unzOpenCurrentFile failed for %{public}s", name.c_str());
            ok = false;
        } else {
            std::vector<uint8_t> buf(kZipBufSize);
            int n;
            while ((n = unzReadCurrentFile(src, buf.data(), kZipBufSize)) > 0) {
                if (zipWriteInFileInZip(dst, buf.data(), n) != ZIP_OK) {
                    LOGE("CopyZipEntry: zipWriteInFileInZip failed for %{public}s", name.c_str());
                    ok = false;
                    break;
                }
            }
            if (n < 0) {
                LOGE("CopyZipEntry: unzReadCurrentFile error for %{public}s n=%{public}d",
                     name.c_str(), n);
                ok = false;
            }
            unzCloseCurrentFile(src);
        }
    }
    zipCloseFileInZip(dst);
    (void)info;  // info unused for now; reserved for future mtime preservation
    return ok;
}

}  // anonymous namespace

bool ApkInstaller::ExtractAndPackResourceHap(const std::string& srcApkPath,
                                             const std::string& outHapPath) {
    LOGI("ExtractAndPackResourceHap: apk=%{public}s out=%{public}s",
         srcApkPath.c_str(), outHapPath.c_str());

    std::vector<uint8_t> apkIconBytes;
    if (!ReadApkLauncherIcon(srcApkPath, apkIconBytes)) {
        LOGW("ExtractAndPackResourceHap: APK has no recognizable launcher icon, "
             "synthesized HAP will use template's placeholder icon");
        // Continue anyway — at least the resources.index structure is valid.
    }

    // 1. Write embedded template HAP to a temp file so minizip can read it.
    // Use the same dir as outHapPath — caller is expected to pass a path that
    // foundation can write (typically /data/app/android/<pkg>/_resources.hap).
    std::string templateTmp = outHapPath + ".template.tmp";
    {
        std::ofstream f(templateTmp, std::ios::binary | std::ios::trunc);
        if (!f) {
            LOGE("ExtractAndPackResourceHap: cannot open template tmp %{public}s errno=%{public}d (%{public}s)",
                 templateTmp.c_str(), errno, strerror(errno));
            return false;
        }
        f.write(reinterpret_cast<const char*>(ohos_adapter_template_resources_hap),
                ohos_adapter_template_resources_hap_len);
    }

    // 2. Open template (read) and outHap (write).
    unzFile src = unzOpen(templateTmp.c_str());
    if (src == nullptr) {
        LOGE("ExtractAndPackResourceHap: cannot open template hap as zip");
        ::unlink(templateTmp.c_str());
        return false;
    }
    zipFile dst = zipOpen(outHapPath.c_str(), APPEND_STATUS_CREATE);
    if (dst == nullptr) {
        LOGE("ExtractAndPackResourceHap: cannot create output hap %{public}s",
             outHapPath.c_str());
        unzClose(src);
        ::unlink(templateTmp.c_str());
        return false;
    }

    // 3. Iterate template entries; replace icon entries with APK icon bytes.
    bool ok = true;
    int rc = unzGoToFirstFile(src);
    while (rc == UNZ_OK) {
        char name[512];
        unz_file_info info{};
        if (unzGetCurrentFileInfo(src, &info, name, sizeof(name),
                                  nullptr, 0, nullptr, 0) != UNZ_OK) {
            ok = false;
            break;
        }
        std::string entryName(name);

        const std::vector<uint8_t>* overrideData = nullptr;
        if (!apkIconBytes.empty() &&
            (entryName == "resources/base/media/icon.png" ||
             entryName == "resources/base/media/app_icon.png")) {
            overrideData = &apkIconBytes;
        }

        if (!CopyZipEntry(src, dst, entryName, info, overrideData)) {
            ok = false;
            break;
        }
        rc = unzGoToNextFile(src);
    }

    zipClose(dst, nullptr);
    unzClose(src);
    ::unlink(templateTmp.c_str());

    if (!ok) {
        ::unlink(outHapPath.c_str());
        return false;
    }

    ::chmod(outHapPath.c_str(), 0644);
    struct stat st{};
    if (::stat(outHapPath.c_str(), &st) == 0) {
        LOGI("ExtractAndPackResourceHap: wrote %{public}s (%{public}lld bytes)",
             outHapPath.c_str(), static_cast<long long>(st.st_size));
    }
    return true;
}

bool ApkInstaller::RemoveApk(const std::string& packageName) {
    LOGI("RemoveApk: package=%{public}s", packageName.c_str());

    std::string installDir = std::string(ANDROID_INSTALL_DIR) + "/" + packageName;
    std::string dataDir = std::string(ANDROID_DATA_DIR) + "/" + packageName;
    std::string extDir = std::string(ANDROID_EXT_DIR) + "/" + packageName;

    bool success = true;
    if (!OHOS::ForceRemoveDirectory(installDir)) {
        LOGW("Failed to remove install dir: %{public}s", installDir.c_str());
        success = false;
    }
    if (!OHOS::ForceRemoveDirectory(dataDir)) {
        LOGW("Failed to remove data dir: %{public}s", dataDir.c_str());
        success = false;
    }
    if (!OHOS::ForceRemoveDirectory(extDir)) {
        LOGW("Failed to remove ext dir: %{public}s", extDir.c_str());
        success = false;
    }

    return success;
}

bool ApkInstaller::CreateInstallDirs(const std::string& packageName) {
    std::string installDir = std::string(ANDROID_INSTALL_DIR) + "/" + packageName;
    std::string libDir = installDir + "/lib";
    std::string oatDir = installDir + "/oat";

    return MkdirRecursive(installDir, 0755) &&
           MkdirRecursive(libDir, 0755) &&
           MkdirRecursive(oatDir, 0755);
}

bool ApkInstaller::CopyApk(const std::string& src, const std::string& dst) {
    std::ifstream in(src, std::ios::binary);
    if (!in) {
        LOGE("CopyApk: failed to open source: %{public}s", src.c_str());
        return false;
    }

    std::ofstream out(dst, std::ios::binary | std::ios::trunc);
    if (!out) {
        LOGE("CopyApk: failed to create destination: %{public}s", dst.c_str());
        return false;
    }

    char buffer[COPY_BUFFER_SIZE];
    while (in.read(buffer, sizeof(buffer)) || in.gcount() > 0) {
        out.write(buffer, in.gcount());
        if (!out) {
            LOGE("CopyApk: write failed");
            return false;
        }
    }

    // Set read-only for APK file
    chmod(dst.c_str(), 0644);
    return true;
}

bool ApkInstaller::ExtractNativeLibs(const std::string& apkPath,
                                      const std::string& libDir,
                                      const std::string& primaryAbi) {
    MkdirRecursive(libDir, 0755);

    unzFile zip = unzOpen(apkPath.c_str());
    if (zip == nullptr) {
        LOGE("ExtractNativeLibs: failed to open APK");
        return false;
    }

    std::string prefix = "lib/" + primaryAbi + "/";
    int extractedCount = 0;

    if (unzGoToFirstFile(zip) == UNZ_OK) {
        do {
            char filename[512];
            unz_file_info fileInfo;
            if (unzGetCurrentFileInfo(zip, &fileInfo, filename, sizeof(filename),
                                       nullptr, 0, nullptr, 0) != UNZ_OK) {
                continue;
            }

            std::string entryName(filename);
            if (entryName.compare(0, prefix.size(), prefix) != 0) continue;
            if (entryName.size() <= prefix.size()) continue;  // Skip directory entry

            // Extract .so filename
            std::string soName = entryName.substr(prefix.size());
            if (soName.find('/') != std::string::npos) continue;  // Skip subdirectories

            // Extract file
            if (unzOpenCurrentFile(zip) != UNZ_OK) {
                LOGW("ExtractNativeLibs: failed to open entry: %{public}s", entryName.c_str());
                continue;
            }

            std::string outPath = libDir + "/" + soName;
            std::ofstream out(outPath, std::ios::binary | std::ios::trunc);
            if (!out) {
                LOGW("ExtractNativeLibs: failed to create: %{public}s", outPath.c_str());
                unzCloseCurrentFile(zip);
                continue;
            }

            char buffer[COPY_BUFFER_SIZE];
            int bytesRead;
            while ((bytesRead = unzReadCurrentFile(zip, buffer, sizeof(buffer))) > 0) {
                out.write(buffer, bytesRead);
            }

            out.close();
            unzCloseCurrentFile(zip);

            // Set executable permission for .so files
            chmod(outPath.c_str(), 0755);
            extractedCount++;

            LOGI("Extracted: %{public}s", soName.c_str());
        } while (unzGoToNextFile(zip) == UNZ_OK);
    }

    unzClose(zip);
    LOGI("ExtractNativeLibs: extracted %{public}d files from %{public}s",
         extractedCount, primaryAbi.c_str());
    return true;
}

bool ApkInstaller::RunDexOpt(const std::string& apkPath, const std::string& oatDir,
                              int32_t uid, const std::string& isa,
                              const std::string& compilerFilter) {
    std::string oatFile = oatDir + "/base.odex";

    std::vector<std::string> args = {
        DEX2OAT_PATH,
        "--dex-file=" + apkPath,
        "--oat-file=" + oatFile,
        "--instruction-set=" + isa,
        "--compiler-filter=" + compilerFilter,
        "--boot-image=" + std::string(BOOT_IMAGE_PATH),
        "--android-root=" + std::string(ANDROID_ROOT),
    };

    LOGI("RunDexOpt: %{public}s -> %{public}s (filter=%{public}s)",
         apkPath.c_str(), oatFile.c_str(), compilerFilter.c_str());

    pid_t pid = fork();
    if (pid < 0) {
        LOGE("RunDexOpt: fork failed: %{public}s", strerror(errno));
        return false;
    }

    if (pid == 0) {
        // Child process
        setuid(uid);

        std::vector<const char*> argv;
        for (const auto& arg : args) {
            argv.push_back(arg.c_str());
        }
        argv.push_back(nullptr);

        execv(argv[0], const_cast<char**>(argv.data()));
        // execv failed
        _exit(127);
    }

    // Parent: wait for dex2oat to finish
    int status;
    waitpid(pid, &status, 0);

    if (WIFEXITED(status) && WEXITSTATUS(status) == 0) {
        LOGI("RunDexOpt: success");
        return true;
    }

    LOGE("RunDexOpt: dex2oat exited with status %{public}d", WEXITSTATUS(status));
    return false;
}

bool ApkInstaller::CreateDataDirs(const std::string& packageName,
                                   int32_t uid, int32_t gid) {
    std::string baseDir = std::string(ANDROID_DATA_DIR) + "/" + packageName;

    // Android-style subdirectories
    const std::vector<std::pair<std::string, mode_t>> dirs = {
        {"",              0771},
        {"/cache",        0771},
        {"/code_cache",   0771},
        {"/databases",    0771},
        {"/files",        0771},
        {"/shared_prefs", 0771},
        {"/lib",          0755},
    };

    for (const auto& [sub, mode] : dirs) {
        std::string path = baseDir + sub;
        if (!MkdirRecursive(path, mode)) {
            LOGE("CreateDataDirs: failed to create: %{public}s", path.c_str());
            return false;
        }
        chmod(path.c_str(), mode);
        chown(path.c_str(), uid, gid);
    }

    // External storage directory
    std::string extDir = std::string(ANDROID_EXT_DIR) + "/" + packageName;
    MkdirRecursive(extDir, 0771);
    chown(extDir.c_str(), uid, 1015);  // sdcard_rw GID

    LOGI("CreateDataDirs: created for %{public}s", packageName.c_str());
    return true;
}

bool ApkInstaller::SetPermissions(const std::string& path,
                                   int32_t uid, int32_t gid, mode_t mode) {
    if (chmod(path.c_str(), mode) != 0) {
        LOGW("SetPermissions: chmod failed for %{public}s: %{public}s",
             path.c_str(), strerror(errno));
        return false;
    }
    if (chown(path.c_str(), uid, gid) != 0) {
        LOGW("SetPermissions: chown failed for %{public}s: %{public}s",
             path.c_str(), strerror(errno));
        return false;
    }
    return true;
}

}  // namespace oh_adapter
