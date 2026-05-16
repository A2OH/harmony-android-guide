/*
 * oh_datashare_client.h
 *
 * OpenHarmony DataShareHelper IPC client (Stage model).
 *
 * Provides CRUD, file, and observer operations against OH DataShareExtensionAbility.
 * Each instance holds a connection to one DataShareHelper (one URI/bundleName).
 *
 * Used by ContentProviderBridge (Java) via JNI to bridge Android ContentProvider
 * operations to OH DataShare.
 *
 * Reference paths:
 *   OH: distributeddatamgr/data_share/interfaces/inner_api/common/include/datashare_helper.h
 *   OH: distributeddatamgr/data_share/interfaces/inner_api/common/include/datashare_predicates.h
 *   OH: distributeddatamgr/data_share/interfaces/inner_api/common/include/datashare_values_bucket.h
 *   OH: distributeddatamgr/data_share/interfaces/inner_api/common/include/datashare_result_set.h
 */
#ifndef OH_DATASHARE_CLIENT_H
#define OH_DATASHARE_CLIENT_H
#include <map>

#include <jni.h>
#include <string>
#include <vector>
#include <memory>
#include <mutex>

// OH DataShare types (provided by OH SDK at build time)
namespace OHOS {
    class IRemoteObject;
    namespace DataShare {
        class DataShareHelper;
        class DataSharePredicates;
        class DataShareValuesBucket;
        class DataShareResultSet;
    }
}

namespace oh_adapter {

/**
 * Value type codes, matching Java-side ContentProviderBridge type codes.
 */
enum ContentValueType {
    TYPE_NULL   = 0,
    TYPE_STRING = 1,
    TYPE_INT    = 2,
    TYPE_LONG   = 3,
    TYPE_FLOAT  = 4,
    TYPE_DOUBLE = 5,
    TYPE_BLOB   = 6,
};

/**
 * Represents one ContentValues entry for JNI transfer.
 */
struct ContentValueEntry {
    std::string key;
    std::string value;  // string representation
    int type;           // ContentValueType
};

/**
 * OH DataShareHelper wrapper for a single DataShare provider.
 */
class OHDataShareClient {
public:
    /**
     * Create and connect to OH DataShareHelper for the given bundleName.
     * @param bundleName OH application bundleName (used in datashare:// URI)
     * @return client instance (connected), or nullptr on failure
     */
    static std::shared_ptr<OHDataShareClient> create(const std::string& bundleName);

    ~OHDataShareClient();

    /**
     * Query data from OH DataShareExtensionAbility.
     * @param uri        datashare:// URI
     * @param columns    Column names to return (empty = all)
     * @param selection  SQL-style WHERE clause (e.g., "age > ?")
     * @param selArgs    Arguments for ? placeholders in selection
     * @param sortOrder  SQL-style ORDER BY clause
     * @param env        JNI environment (for building MatrixCursor)
     * @return Java MatrixCursor object (local ref), or nullptr on error
     */
    jobject query(const std::string& uri,
                  const std::vector<std::string>& columns,
                  const std::string& selection,
                  const std::vector<std::string>& selArgs,
                  const std::string& sortOrder,
                  JNIEnv* env);

    /**
     * Insert a record.
     * @param uri     datashare:// URI
     * @param entries Key-value-type entries
     * @return row index (>= 0 on success, -1 on error)
     */
    int insert(const std::string& uri,
               const std::vector<ContentValueEntry>& entries);

    /**
     * Update records matching selection.
     * @return number of rows updated, or -1 on error
     */
    int update(const std::string& uri,
               const std::vector<ContentValueEntry>& entries,
               const std::string& selection,
               const std::vector<std::string>& selArgs);

    /**
     * Delete records matching selection.
     * @return number of rows deleted, or -1 on error
     */
    int deleteRecords(const std::string& uri,
                      const std::string& selection,
                      const std::vector<std::string>& selArgs);

    /**
     * Get MIME type for URI.
     */
    std::string getType(const std::string& uri);

    /**
     * Open file and return file descriptor.
     * @return fd >= 0 on success, -1 on error
     */
    int openFile(const std::string& uri, const std::string& mode);

    bool isConnected() const { return connected_; }
    const std::string& getBundleName() const { return bundleName_; }

private:
    OHDataShareClient(const std::string& bundleName);
    bool connect();
    void disconnect();

    /**
     * Build OH DataSharePredicates from SQL selection string.
     */
    std::shared_ptr<OHOS::DataShare::DataSharePredicates> buildPredicates(
        const std::string& selection,
        const std::vector<std::string>& selArgs);

    /**
     * Build OH DataShareValuesBucket from ContentValueEntry list.
     */
    std::shared_ptr<OHOS::DataShare::DataShareValuesBucket> buildValuesBucket(
        const std::vector<ContentValueEntry>& entries);

    /**
     * Convert OH DataShareResultSet to Java MatrixCursor.
     */
    jobject resultSetToCursor(
        const std::shared_ptr<OHOS::DataShare::DataShareResultSet>& resultSet,
        JNIEnv* env);

    std::string bundleName_;
    bool connected_ = false;
    std::shared_ptr<OHOS::DataShare::DataShareHelper> helper_;
    std::mutex mutex_;
};

/**
 * Global manager for OHDataShareClient instances.
 * Maps handle (intptr_t) to client instance.
 */
class OHDataShareClientManager {
public:
    static OHDataShareClientManager& getInstance();

    /**
     * Create a new client and return its handle.
     * @return handle (cast from pointer), 0 on failure
     */
    long createClient(const std::string& bundleName);

    /**
     * Get client by handle.
     */
    std::shared_ptr<OHDataShareClient> getClient(long handle);

    /**
     * Destroy client and release its handle.
     */
    void destroyClient(long handle);

private:
    OHDataShareClientManager() = default;
    std::mutex mutex_;
    std::map<long, std::shared_ptr<OHDataShareClient>> clients_;
};

}  // namespace oh_adapter

#endif  // OH_DATASHARE_CLIENT_H
