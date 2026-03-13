fn main() {
    // OH SDK sysroot — set via environment or default path
    let oh_sdk = std::env::var("OH_SDK_ROOT")
        .unwrap_or_else(|_| "/home/dspfac/openharmony".to_string());

    // Library search paths (adjust for actual OH build output)
    let lib_dirs = [
        format!("{}/out/default/lib", oh_sdk),
        format!("{}/prebuilts/ohos-sdk/linux/native/sysroot/usr/lib/aarch64-linux-ohos", oh_sdk),
    ];

    for dir in &lib_dirs {
        println!("cargo:rustc-link-search=native={}", dir);
    }

    // Real NDK libraries
    println!("cargo:rustc-link-lib=dylib=hilog_ndk.z");       // HiLog
    println!("cargo:rustc-link-lib=dylib=native_rdb_ndk.z");  // RdbStore
    println!("cargo:rustc-link-lib=dylib=deviceinfo_ndk.z");   // DeviceInfo
    println!("cargo:rustc-link-lib=dylib=net_connection");     // Net Connection
    println!("cargo:rustc-link-lib=dylib=ace_ndk.z");          // ArkUI Native Node API

    // Our C++ shim (must be built separately and placed in lib path)
    // Links: libpreferences, libans_innerkits, libability_runtime, libwant, libcurl, libace_napi, libace_ndk.z
    println!("cargo:rustc-link-lib=dylib=oh_cpp_shim");

    // Header include paths (for any bindgen usage)
    let include_dirs = [
        format!("{}/interface/sdk_c/hiviewdfx/hilog/include", oh_sdk),
        format!("{}/interface/sdk_c/distributeddatamgr/relational_store/include", oh_sdk),
        format!("{}/interface/sdk_c/startup/init/syscap/include", oh_sdk),
        format!("{}/interface/sdk_c/network/netmanager/include", oh_sdk),
        format!("{}/foundation/arkui/ace_engine/interfaces/native", oh_sdk),   // ArkUI native node API
        format!("{}/interface/sdk_c/arkui/ace_engine/native", oh_sdk),          // XComponent
    ];

    for dir in &include_dirs {
        println!("cargo:include={}", dir);
    }
}
