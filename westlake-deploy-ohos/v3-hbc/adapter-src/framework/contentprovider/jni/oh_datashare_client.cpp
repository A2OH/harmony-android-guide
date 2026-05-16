/*
 * oh_datashare_client.cpp
 *
 * OpenHarmony DataShareHelper IPC client implementation (Stage model).
 *
 * Connects to OH DataShareExtensionAbility via DataShareHelper API and
 * provides CRUD, file, and type operations. Converts between Android-style
 * SQL selection strings and OH DataSharePredicates.
 *
 * This file also contains JNI native method implementations called by
 * ContentProviderBridge.java.
 *
 * Reference paths:
 *   OH: distributeddatamgr/data_share/interfaces/inner_api/common/include/datashare_helper.h
 *   OH: ability_rt/interfaces/inner_api/ability_manager/include/ability_manager_client.h
 */
#include "oh_datashare_client.h"
#include <android/log.h>
#include <map>
#include <sstream>
#include <regex>

// OH SDK headers (provided at build time)
#include "datashare_helper.h"
#include "datashare_predicates.h"
#include "datashare_values_bucket.h"
#include "datashare_result_set.h"
#include "ipc_skeleton.h"
#include "uri.h"

#define LOG_TAG "OH_DataShareClient"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN, LOG_TAG, __VA_ARGS__)

namespace oh_adapter {

// ==================== JNI Cache ====================

// Cached JNI class/method IDs for MatrixCursor construction
static jclass g_matrixCursorClass = nullptr;
static jmethodID g_matrixCursorInit = nullptr;    // (String[])V
static jmethodID g_matrixCursorNewRow = nullptr;   // ()MatrixCursor.RowBuilder
static jclass g_rowBuilderClass = nullptr;
static jmethodID g_rowBuilderAdd = nullptr;        // (Object)MatrixCursor.RowBuilder

/**
 * Initialize JNI class/method ID cache. Called once from JNI_OnLoad context.
 */
bool initDataShareJniCache(JNIEnv* env) {
    jclass cls = env->FindClass("android/database/MatrixCursor");
    if (!cls) {
        LOGE("Failed to find MatrixCursor class");
        return false;
    }
    g_matrixCursorClass = (jclass)env->NewGlobalRef(cls);

    g_matrixCursorInit = env->GetMethodID(cls, "<init>", "([Ljava/lang/String;)V");
    g_matrixCursorNewRow = env->GetMethodID(cls, "newRow",
        "()Landroid/database/MatrixCursor$RowBuilder;");
    if (!g_matrixCursorInit || !g_matrixCursorNewRow) {
        LOGE("Failed to find MatrixCursor methods");
        return false;
    }

    jclass rbCls = env->FindClass("android/database/MatrixCursor$RowBuilder");
    if (!rbCls) {
        LOGE("Failed to find MatrixCursor.RowBuilder class");
        return false;
    }
    g_rowBuilderClass = (jclass)env->NewGlobalRef(rbCls);
    g_rowBuilderAdd = env->GetMethodID(rbCls, "add",
        "(Ljava/lang/Object;)Landroid/database/MatrixCursor$RowBuilder;");
    if (!g_rowBuilderAdd) {
        LOGE("Failed to find RowBuilder.add method");
        return false;
    }

    LOGI("DataShare JNI cache initialized");
    return true;
}

// ==================== OHDataShareClient ====================

OHDataShareClient::OHDataShareClient(const std::string& bundleName)
    : bundleName_(bundleName) {
}

OHDataShareClient::~OHDataShareClient() {
    disconnect();
}

std::shared_ptr<OHDataShareClient> OHDataShareClient::create(
        const std::string& bundleName) {
    auto client = std::shared_ptr<OHDataShareClient>(
        new OHDataShareClient(bundleName));
    if (!client->connect()) {
        LOGE("Failed to connect DataShareHelper for bundle=%s", bundleName.c_str());
        return nullptr;
    }
    return client;
}

bool OHDataShareClient::connect() {
    LOGI("Connecting DataShareHelper: bundle=%s", bundleName_.c_str());

    // Construct base URI for this DataShare provider
    std::string baseUri = "datashare:///" + bundleName_;

    // Create DataShareHelper using the URI + CreateOptions factory.
    // V7 inner_api Creator(token, uri) overload requires a real system-context
    // sptr<IRemoteObject>; for our adapter-as-client use we don't have one. The
    // (uri, options, ...) overload routes through the silent-access path and
    // doesn't require a token at construction time (still deprecated in V7 but
    // functional — full migration to DataShareHelper::Create is a follow-up).
    OHOS::DataShare::CreateOptions options;
    options.isProxy_ = false;
    helper_ = OHOS::DataShare::DataShareHelper::Creator(baseUri, options, bundleName_);

    if (helper_ == nullptr) {
        LOGE("DataShareHelper::Creator returned null for %s", baseUri.c_str());
        return false;
    }

    connected_ = true;
    LOGI("Connected DataShareHelper: bundle=%s", bundleName_.c_str());
    return true;
}

void OHDataShareClient::disconnect() {
    std::lock_guard<std::mutex> lock(mutex_);
    if (helper_) {
        helper_->Release();
        helper_ = nullptr;
    }
    connected_ = false;
    LOGI("Disconnected DataShareHelper: bundle=%s", bundleName_.c_str());
}

// ==================== CRUD Operations ====================

jobject OHDataShareClient::query(const std::string& uri,
        const std::vector<std::string>& columns,
        const std::string& selection,
        const std::vector<std::string>& selArgs,
        const std::string& sortOrder,
        JNIEnv* env) {
    std::lock_guard<std::mutex> lock(mutex_);
    if (!helper_) {
        LOGE("query: helper is null");
        return nullptr;
    }

    OHOS::Uri ohUri(uri);
    auto predicates = buildPredicates(selection, selArgs);

    // Add sort order if provided
    if (!sortOrder.empty()) {
        // Parse "column ASC" or "column DESC"
        std::string col = sortOrder;
        bool asc = true;
        size_t space = sortOrder.rfind(' ');
        if (space != std::string::npos) {
            std::string dir = sortOrder.substr(space + 1);
            col = sortOrder.substr(0, space);
            if (dir == "DESC" || dir == "desc") {
                asc = false;
            }
        }
        if (asc) {
            predicates->OrderByAsc(col);
        } else {
            predicates->OrderByDesc(col);
        }
    }

    // Execute query — V7 inner_api takes columns as non-const reference, so
    // copy from our const& parameter into a mutable local.
    std::vector<std::string> columnsCopy = columns;
    auto resultSet = helper_->Query(ohUri, *predicates, columnsCopy);
    if (resultSet == nullptr) {
        LOGW("query returned null ResultSet for %s", uri.c_str());
        return nullptr;
    }

    LOGI("query: uri=%s, resultSet obtained", uri.c_str());
    return resultSetToCursor(resultSet, env);
}

int OHDataShareClient::insert(const std::string& uri,
        const std::vector<ContentValueEntry>& entries) {
    std::lock_guard<std::mutex> lock(mutex_);
    if (!helper_) {
        LOGE("insert: helper is null");
        return -1;
    }

    OHOS::Uri ohUri(uri);
    auto bucket = buildValuesBucket(entries);

    int result = helper_->Insert(ohUri, *bucket);
    LOGI("insert: uri=%s, result=%d", uri.c_str(), result);
    return result;
}

int OHDataShareClient::update(const std::string& uri,
        const std::vector<ContentValueEntry>& entries,
        const std::string& selection,
        const std::vector<std::string>& selArgs) {
    std::lock_guard<std::mutex> lock(mutex_);
    if (!helper_) {
        LOGE("update: helper is null");
        return -1;
    }

    OHOS::Uri ohUri(uri);
    auto predicates = buildPredicates(selection, selArgs);
    auto bucket = buildValuesBucket(entries);

    int result = helper_->Update(ohUri, *predicates, *bucket);
    LOGI("update: uri=%s, result=%d", uri.c_str(), result);
    return result;
}

int OHDataShareClient::deleteRecords(const std::string& uri,
        const std::string& selection,
        const std::vector<std::string>& selArgs) {
    std::lock_guard<std::mutex> lock(mutex_);
    if (!helper_) {
        LOGE("deleteRecords: helper is null");
        return -1;
    }

    OHOS::Uri ohUri(uri);
    auto predicates = buildPredicates(selection, selArgs);

    int result = helper_->Delete(ohUri, *predicates);
    LOGI("deleteRecords: uri=%s, result=%d", uri.c_str(), result);
    return result;
}

std::string OHDataShareClient::getType(const std::string& uri) {
    std::lock_guard<std::mutex> lock(mutex_);
    if (!helper_) return "";

    OHOS::Uri ohUri(uri);
    std::string type = helper_->GetType(ohUri);
    LOGI("getType: uri=%s, type=%s", uri.c_str(), type.c_str());
    return type;
}

int OHDataShareClient::openFile(const std::string& uri, const std::string& mode) {
    std::lock_guard<std::mutex> lock(mutex_);
    if (!helper_) {
        LOGE("openFile: helper is null");
        return -1;
    }

    OHOS::Uri ohUri(uri);
    int fd = helper_->OpenFile(ohUri, mode);
    LOGI("openFile: uri=%s, mode=%s, fd=%d", uri.c_str(), mode.c_str(), fd);
    return fd;
}

// ==================== Data Conversion Helpers ====================

std::shared_ptr<OHOS::DataShare::DataSharePredicates>
OHDataShareClient::buildPredicates(const std::string& selection,
                                    const std::vector<std::string>& selArgs) {
    auto predicates = std::make_shared<OHOS::DataShare::DataSharePredicates>();

    if (selection.empty()) {
        return predicates;
    }

    // Parse SQL-style selection into OH DataSharePredicates.
    //
    // Supports basic patterns:
    //   "column = ?"          -> EqualTo(column, value)
    //   "column != ?"         -> NotEqualTo(column, value)
    //   "column > ?"          -> GreaterThan(column, value)
    //   "column < ?"          -> LessThan(column, value)
    //   "column >= ?"         -> GreaterThanOrEqualTo(column, value)
    //   "column <= ?"         -> LessThanOrEqualTo(column, value)
    //   "column LIKE ?"       -> Like(column, value)
    //   "column IS NULL"      -> IsNull(column)
    //   "column IS NOT NULL"  -> IsNotNull(column)
    //   Connected by AND / OR

    // Tokenize by AND / OR
    std::regex splitPattern(R"(\s+(AND|OR)\s+)", std::regex::icase);
    std::sregex_token_iterator iter(selection.begin(), selection.end(), splitPattern, {-1, 1});
    std::sregex_token_iterator end;

    int argIdx = 0;
    bool firstClause = true;

    while (iter != end) {
        std::string token = iter->str();
        ++iter;

        // Trim whitespace
        size_t start = token.find_first_not_of(" \t");
        size_t stop = token.find_last_not_of(" \t");
        if (start == std::string::npos) continue;
        token = token.substr(start, stop - start + 1);

        // Check if this token is a connector (AND/OR)
        if (token == "AND" || token == "and" || token == "And") {
            predicates->And();
            continue;
        }
        if (token == "OR" || token == "or" || token == "Or") {
            predicates->Or();
            continue;
        }

        // Parse condition: "column operator value_or_?"
        // Handle IS NULL / IS NOT NULL
        std::regex isNullPattern(R"((\w+)\s+IS\s+NULL)", std::regex::icase);
        std::regex isNotNullPattern(R"((\w+)\s+IS\s+NOT\s+NULL)", std::regex::icase);
        std::smatch match;

        if (std::regex_match(token, match, isNullPattern)) {
            if (!firstClause) predicates->And();
            predicates->IsNull(match[1].str());
            firstClause = false;
            continue;
        }

        if (std::regex_match(token, match, isNotNullPattern)) {
            if (!firstClause) predicates->And();
            predicates->IsNotNull(match[1].str());
            firstClause = false;
            continue;
        }

        // Parse comparison: column op ?
        std::regex compPattern(R"((\w+)\s*(>=|<=|!=|<>|=|>|<|LIKE|like)\s*\?)");
        if (std::regex_match(token, match, compPattern)) {
            std::string column = match[1].str();
            std::string op = match[2].str();
            std::string value;
            if (argIdx < (int)selArgs.size()) {
                value = selArgs[argIdx++];
            }

            if (!firstClause) {
                // Default AND if no explicit connector was parsed
            }

            if (op == "=" || op == "==") {
                predicates->EqualTo(column, value);
            } else if (op == "!=" || op == "<>") {
                predicates->NotEqualTo(column, value);
            } else if (op == ">") {
                predicates->GreaterThan(column, value);
            } else if (op == "<") {
                predicates->LessThan(column, value);
            } else if (op == ">=") {
                predicates->GreaterThanOrEqualTo(column, value);
            } else if (op == "<=") {
                predicates->LessThanOrEqualTo(column, value);
            } else if (op == "LIKE" || op == "like") {
                predicates->Like(column, value);
            }

            firstClause = false;
            continue;
        }

        // Fallback: if unparseable, use SetWhereClause for raw SQL support
        LOGW("buildPredicates: unparsed clause '%s', using raw where", token.c_str());
        predicates->SetWhereClause(selection);
        predicates->SetWhereArgs(selArgs);
        return predicates;
    }

    return predicates;
}

std::shared_ptr<OHOS::DataShare::DataShareValuesBucket>
OHDataShareClient::buildValuesBucket(const std::vector<ContentValueEntry>& entries) {
    auto bucket = std::make_shared<OHOS::DataShare::DataShareValuesBucket>();

    // V7 unified API: DataShareValuesBucket::Put(name, DataShareValueObject).
    // Type-tagged via DataShareValueObject's std::variant<int64_t,double,string,bool,blob> ctors.
    using DSV = OHOS::DataShare::DataShareValueObject;
    for (const auto& entry : entries) {
        switch (entry.type) {
            case TYPE_NULL:
                bucket->Put(entry.key, DSV());
                break;
            case TYPE_STRING:
                bucket->Put(entry.key, DSV(entry.value));
                break;
            case TYPE_INT:
                bucket->Put(entry.key, DSV(static_cast<int64_t>(std::stoi(entry.value))));
                break;
            case TYPE_LONG:
                bucket->Put(entry.key, DSV(static_cast<int64_t>(std::stoll(entry.value))));
                break;
            case TYPE_FLOAT:
            case TYPE_DOUBLE:
                bucket->Put(entry.key, DSV(std::stod(entry.value)));
                break;
            case TYPE_BLOB:
                bucket->Put(entry.key, DSV(entry.value));
                LOGW("buildValuesBucket: blob key=%s stored as string (TODO: binary)",
                     entry.key.c_str());
                break;
            default:
                bucket->Put(entry.key, DSV(entry.value));
                break;
        }
    }
    return bucket;
}

jobject OHDataShareClient::resultSetToCursor(
        const std::shared_ptr<OHOS::DataShare::DataShareResultSet>& resultSet,
        JNIEnv* env) {
    if (!env || !resultSet) return nullptr;
    if (!g_matrixCursorClass || !g_matrixCursorInit) {
        LOGE("resultSetToCursor: JNI cache not initialized");
        return nullptr;
    }

    // 1. Get column names
    std::vector<std::string> columnNames;
    resultSet->GetAllColumnNames(columnNames);
    int colCount = columnNames.size();

    // Create Java String[] for column names
    jclass stringClass = env->FindClass("java/lang/String");
    jobjectArray jColumns = env->NewObjectArray(colCount, stringClass, nullptr);
    for (int i = 0; i < colCount; i++) {
        jstring jCol = env->NewStringUTF(columnNames[i].c_str());
        env->SetObjectArrayElement(jColumns, i, jCol);
        env->DeleteLocalRef(jCol);
    }

    // 2. Create MatrixCursor
    jobject cursor = env->NewObject(g_matrixCursorClass, g_matrixCursorInit, jColumns);
    env->DeleteLocalRef(jColumns);

    if (cursor == nullptr) {
        LOGE("resultSetToCursor: failed to create MatrixCursor");
        return nullptr;
    }

    // 3. Populate rows
    int rowCount = 0;
    while (resultSet->GoToNextRow() == 0) {
        jobject rowBuilder = env->CallObjectMethod(cursor, g_matrixCursorNewRow);

        for (int col = 0; col < colCount; col++) {
            OHOS::DataShare::DataType dataType;
            resultSet->GetDataType(col, dataType);

            switch (dataType) {
                case OHOS::DataShare::DataType::TYPE_STRING: {
                    std::string val;
                    resultSet->GetString(col, val);
                    jstring jVal = env->NewStringUTF(val.c_str());
                    env->CallObjectMethod(rowBuilder, g_rowBuilderAdd, jVal);
                    env->DeleteLocalRef(jVal);
                    break;
                }
                case OHOS::DataShare::DataType::TYPE_INTEGER: {
                    int64_t val;
                    resultSet->GetLong(col, val);
                    jclass longClass = env->FindClass("java/lang/Long");
                    jmethodID longValueOf = env->GetStaticMethodID(longClass,
                        "valueOf", "(J)Ljava/lang/Long;");
                    jobject jVal = env->CallStaticObjectMethod(longClass, longValueOf, (jlong)val);
                    env->CallObjectMethod(rowBuilder, g_rowBuilderAdd, jVal);
                    env->DeleteLocalRef(jVal);
                    env->DeleteLocalRef(longClass);
                    break;
                }
                case OHOS::DataShare::DataType::TYPE_FLOAT: {
                    double val;
                    resultSet->GetDouble(col, val);
                    jclass doubleClass = env->FindClass("java/lang/Double");
                    jmethodID doubleValueOf = env->GetStaticMethodID(doubleClass,
                        "valueOf", "(D)Ljava/lang/Double;");
                    jobject jVal = env->CallStaticObjectMethod(doubleClass,
                        doubleValueOf, (jdouble)val);
                    env->CallObjectMethod(rowBuilder, g_rowBuilderAdd, jVal);
                    env->DeleteLocalRef(jVal);
                    env->DeleteLocalRef(doubleClass);
                    break;
                }
                case OHOS::DataShare::DataType::TYPE_NULL:
                default: {
                    env->CallObjectMethod(rowBuilder, g_rowBuilderAdd, nullptr);
                    break;
                }
                // Note: BLOB type not handled via MatrixCursor (limitation).
                // For blob columns, store null. Full blob support requires
                // custom Cursor implementation.
            }
        }
        env->DeleteLocalRef(rowBuilder);
        rowCount++;
    }

    LOGI("resultSetToCursor: %d columns, %d rows", colCount, rowCount);
    return cursor;
}

// ==================== OHDataShareClientManager ====================

OHDataShareClientManager& OHDataShareClientManager::getInstance() {
    static OHDataShareClientManager instance;
    return instance;
}

long OHDataShareClientManager::createClient(const std::string& bundleName) {
    auto client = OHDataShareClient::create(bundleName);
    if (!client) return 0;

    long handle = reinterpret_cast<long>(client.get());
    {
        std::lock_guard<std::mutex> lock(mutex_);
        clients_[handle] = client;
    }
    LOGI("createClient: bundle=%s, handle=%ld", bundleName.c_str(), handle);
    return handle;
}

std::shared_ptr<OHDataShareClient> OHDataShareClientManager::getClient(long handle) {
    std::lock_guard<std::mutex> lock(mutex_);
    auto it = clients_.find(handle);
    if (it != clients_.end()) {
        return it->second;
    }
    return nullptr;
}

void OHDataShareClientManager::destroyClient(long handle) {
    std::lock_guard<std::mutex> lock(mutex_);
    auto it = clients_.find(handle);
    if (it != clients_.end()) {
        LOGI("destroyClient: handle=%ld, bundle=%s",
             handle, it->second->getBundleName().c_str());
        clients_.erase(it);
    }
}

}  // namespace oh_adapter

// ==================== JNI Helper Functions ====================

static std::string jstringToStdString(JNIEnv* env, jstring jstr) {
    if (!jstr) return "";
    const char* raw = env->GetStringUTFChars(jstr, nullptr);
    std::string result(raw);
    env->ReleaseStringUTFChars(jstr, raw);
    return result;
}

static std::vector<std::string> jstringArrayToVector(JNIEnv* env, jobjectArray arr) {
    std::vector<std::string> result;
    if (!arr) return result;
    int len = env->GetArrayLength(arr);
    result.reserve(len);
    for (int i = 0; i < len; i++) {
        jstring js = (jstring)env->GetObjectArrayElement(arr, i);
        result.push_back(jstringToStdString(env, js));
        if (js) env->DeleteLocalRef(js);
    }
    return result;
}

static std::vector<oh_adapter::ContentValueEntry> buildEntries(
        JNIEnv* env, jobjectArray keys, jobjectArray values, jintArray types) {
    std::vector<oh_adapter::ContentValueEntry> entries;
    if (!keys) return entries;

    int len = env->GetArrayLength(keys);
    jint* typeArr = types ? env->GetIntArrayElements(types, nullptr) : nullptr;

    for (int i = 0; i < len; i++) {
        oh_adapter::ContentValueEntry entry;
        jstring jKey = (jstring)env->GetObjectArrayElement(keys, i);
        jstring jVal = (jstring)env->GetObjectArrayElement(values, i);
        entry.key = jstringToStdString(env, jKey);
        entry.value = jstringToStdString(env, jVal);
        entry.type = typeArr ? typeArr[i] : oh_adapter::TYPE_STRING;

        // Handle null marker
        if (entry.value == std::string("\0NULL\0", 6)) {
            entry.type = oh_adapter::TYPE_NULL;
            entry.value = "";
        }

        entries.push_back(std::move(entry));
        if (jKey) env->DeleteLocalRef(jKey);
        if (jVal) env->DeleteLocalRef(jVal);
    }

    if (typeArr) env->ReleaseIntArrayElements(types, typeArr, 0);
    return entries;
}

// ==================== JNI Native Method Implementations ====================
// Called by ContentProviderBridge.java

extern "C" {

using namespace oh_adapter;

JNIEXPORT jlong JNICALL
Java_adapter_contentprovider_ContentProviderBridge_nativeConnect(
        JNIEnv* env, jclass clazz, jstring bundleName) {
    std::string bundle = jstringToStdString(env, bundleName);
    return OHDataShareClientManager::getInstance().createClient(bundle);
}

JNIEXPORT void JNICALL
Java_adapter_contentprovider_ContentProviderBridge_nativeDisconnect(
        JNIEnv* env, jclass clazz, jlong handle) {
    OHDataShareClientManager::getInstance().destroyClient(handle);
}

JNIEXPORT jobject JNICALL
Java_adapter_contentprovider_ContentProviderBridge_nativeQuery(
        JNIEnv* env, jclass clazz, jlong handle, jstring ohUri,
        jobjectArray projection, jstring selection, jobjectArray selectionArgs,
        jstring sortOrder) {
    auto client = OHDataShareClientManager::getInstance().getClient(handle);
    if (!client) {
        LOGE("nativeQuery: invalid handle %ld", (long)handle);
        return nullptr;
    }

    return client->query(
        jstringToStdString(env, ohUri),
        jstringArrayToVector(env, projection),
        jstringToStdString(env, selection),
        jstringArrayToVector(env, selectionArgs),
        jstringToStdString(env, sortOrder),
        env);
}

JNIEXPORT jint JNICALL
Java_adapter_contentprovider_ContentProviderBridge_nativeInsert(
        JNIEnv* env, jclass clazz, jlong handle, jstring ohUri,
        jobjectArray keys, jobjectArray values, jintArray types) {
    auto client = OHDataShareClientManager::getInstance().getClient(handle);
    if (!client) {
        LOGE("nativeInsert: invalid handle %ld", (long)handle);
        return -1;
    }

    return client->insert(
        jstringToStdString(env, ohUri),
        buildEntries(env, keys, values, types));
}

JNIEXPORT jint JNICALL
Java_adapter_contentprovider_ContentProviderBridge_nativeUpdate(
        JNIEnv* env, jclass clazz, jlong handle, jstring ohUri,
        jobjectArray keys, jobjectArray values, jintArray types,
        jstring selection, jobjectArray selectionArgs) {
    auto client = OHDataShareClientManager::getInstance().getClient(handle);
    if (!client) {
        LOGE("nativeUpdate: invalid handle %ld", (long)handle);
        return -1;
    }

    return client->update(
        jstringToStdString(env, ohUri),
        buildEntries(env, keys, values, types),
        jstringToStdString(env, selection),
        jstringArrayToVector(env, selectionArgs));
}

JNIEXPORT jint JNICALL
Java_adapter_contentprovider_ContentProviderBridge_nativeDelete(
        JNIEnv* env, jclass clazz, jlong handle, jstring ohUri,
        jstring selection, jobjectArray selectionArgs) {
    auto client = OHDataShareClientManager::getInstance().getClient(handle);
    if (!client) {
        LOGE("nativeDelete: invalid handle %ld", (long)handle);
        return -1;
    }

    return client->deleteRecords(
        jstringToStdString(env, ohUri),
        jstringToStdString(env, selection),
        jstringArrayToVector(env, selectionArgs));
}

JNIEXPORT jstring JNICALL
Java_adapter_contentprovider_ContentProviderBridge_nativeGetType(
        JNIEnv* env, jclass clazz, jlong handle, jstring ohUri) {
    auto client = OHDataShareClientManager::getInstance().getClient(handle);
    if (!client) return nullptr;

    std::string type = client->getType(jstringToStdString(env, ohUri));
    if (type.empty()) return nullptr;
    return env->NewStringUTF(type.c_str());
}

JNIEXPORT jint JNICALL
Java_adapter_contentprovider_ContentProviderBridge_nativeOpenFile(
        JNIEnv* env, jclass clazz, jlong handle, jstring ohUri, jstring mode) {
    auto client = OHDataShareClientManager::getInstance().getClient(handle);
    if (!client) {
        LOGE("nativeOpenFile: invalid handle %ld", (long)handle);
        return -1;
    }

    return client->openFile(
        jstringToStdString(env, ohUri),
        jstringToStdString(env, mode));
}

JNIEXPORT jobject JNICALL
Java_adapter_contentprovider_ContentProviderBridge_nativeCall(
        JNIEnv* env, jclass clazz, jlong handle, jstring authority,
        jstring method, jstring arg, jobject extras) {
    // call() is not directly supported by DataShareHelper in Stage model.
    // Return null for now.
    LOGW("nativeCall: not supported in Stage model DataShare");
    return nullptr;
}

}  // extern "C"
