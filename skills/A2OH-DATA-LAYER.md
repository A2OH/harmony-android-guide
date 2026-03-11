# A2OH-DATA-LAYER: Data Storage Conversion Skill

## Quick Reference: Android → OpenHarmony Data APIs

| Android | OpenHarmony | Import |
|---------|-------------|--------|
| `SQLiteOpenHelper` | `relationalStore.getRdbStore()` | `import relationalStore from '@ohos.data.relationalStore'` |
| `SQLiteDatabase` | `RdbStore` | (same) |
| `ContentValues` | `ValuesBucket` (plain `{ key: value }` object) | (same) |
| `Cursor` | `ResultSet` | (same) |
| `db.rawQuery(sql, args)` | `rdbStore.querySql(sql, args)` | (same) |
| `db.execSQL(sql)` | `rdbStore.executeSql(sql)` | (same) |
| `SharedPreferences` | `Preferences` | `import dataPreferences from '@ohos.data.preferences'` |
| `ContentProvider` | `DataShareExtensionAbility` | `import DataShareExtensionAbility from '@ohos.application.DataShareExtensionAbility'` |
| `ContentResolver` | `dataShareHelper` | `import dataShare from '@ohos.data.dataShare'` |

---

## Conversion Rules

### RULE-D1: SQLiteOpenHelper → getRdbStore
- Delete the `SQLiteOpenHelper` subclass entirely.
- Replace with a call to `relationalStore.getRdbStore(context, storeConfig)`.
- Execute `CREATE TABLE` via `rdbStore.executeSql()` after obtaining the store.
- `onUpgrade` logic becomes version checks inside the same initialization function.

### RULE-D2: ContentValues → ValuesBucket
- Replace `ContentValues cv = new ContentValues()` / `cv.put(k, v)` with a plain object literal `{ k: v }`.
- OH `ValuesBucket` is just `Record<string, number | string | boolean | Uint8Array | null>`.

### RULE-D3: Cursor → ResultSet
- Replace `cursor.moveToFirst()` / `cursor.moveToNext()` loop with `resultSet.goToFirstRow()` / `resultSet.goToNextRow()`.
- Replace `cursor.getString(cursor.getColumnIndex("col"))` with `resultSet.getString(resultSet.getColumnIndex("col"))`.
- Always call `resultSet.close()` in a `finally` block.

### RULE-D4: Query builder → RdbPredicates
- Replace raw `WHERE` clauses with chained `RdbPredicates` methods.
- `selection = "age > ? AND name = ?"` becomes `new RdbPredicates(table).greaterThan('age', 18).and().equalTo('name', 'Alice')`.

### RULE-D5: Transactions
- Replace `db.beginTransaction()` / `db.setTransactionSuccessful()` / `db.endTransaction()` with `rdbStore.beginTransaction()` / `rdbStore.commit()` / `rdbStore.rollBack()`.

### RULE-D6: SharedPreferences → Preferences
- Replace `getSharedPreferences(name, MODE_PRIVATE)` with `await dataPreferences.getPreferences(context, name)`.
- Replace `edit().putString(k, v).apply()` with `await prefs.put(k, v); await prefs.flush()`.
- `flush()` is required — without it data is not persisted. There is no separate `apply` vs `commit`.

### RULE-D7: ContentProvider → DataShareExtensionAbility
- Convert each `query`/`insert`/`update`/`delete` override into the corresponding method on `DataShareExtensionAbility`.
- URI matching (`UriMatcher`) becomes manual URI string parsing or a switch on the path.
- Register the ability in `module.json5` with `"type": "dataShare"`.

### RULE-D8: ContentResolver → DataShareHelper
- Replace `getContentResolver().query(uri, ...)` with `dataShareHelper.query(uri, predicates, columns)`.
- Create helper via `dataShare.createDataShareHelper(context, uri)`.

### RULE-D9: All data APIs are async
- Every OH data operation returns a `Promise`. Always `await` or handle with `.then()`.
- Android synchronous calls (e.g., `db.insert(...)`) must become `await rdbStore.insert(...)`.

---

## 1. SQLiteOpenHelper → RdbStore Setup

### Android (Java)
```java
public class TaskDbHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "tasks.db";
    private static final int DB_VERSION = 1;

    public TaskDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE tasks ("
            + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "title TEXT NOT NULL,"
            + "done INTEGER DEFAULT 0)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        db.execSQL("DROP TABLE IF EXISTS tasks");
        onCreate(db);
    }
}
```

### OpenHarmony (ArkTS)
```typescript
import relationalStore from '@ohos.data.relationalStore';
import common from '@ohos.app.ability.common';

const STORE_CONFIG: relationalStore.StoreConfig = {
  name: 'tasks.db',
  securityLevel: relationalStore.SecurityLevel.S1
};

const CREATE_TABLE_SQL = `CREATE TABLE IF NOT EXISTS tasks (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  title TEXT NOT NULL,
  done INTEGER DEFAULT 0
)`;

export async function getTaskStore(context: common.UIAbilityContext): Promise<relationalStore.RdbStore> {
  const rdbStore = await relationalStore.getRdbStore(context, STORE_CONFIG);
  await rdbStore.executeSql(CREATE_TABLE_SQL);
  return rdbStore;
}
```

---

## 2. SQLiteDatabase CRUD → RdbStore CRUD

### Android (Java)
```java
// INSERT
ContentValues cv = new ContentValues();
cv.put("title", "Buy milk");
cv.put("done", 0);
long id = db.insert("tasks", null, cv);

// QUERY
Cursor cursor = db.query("tasks",
    new String[]{"id", "title", "done"},
    "done = ?", new String[]{"0"},
    null, null, "title ASC");
List<Task> tasks = new ArrayList<>();
try {
    while (cursor.moveToNext()) {
        Task t = new Task();
        t.id = cursor.getInt(cursor.getColumnIndex("id"));
        t.title = cursor.getString(cursor.getColumnIndex("title"));
        t.done = cursor.getInt(cursor.getColumnIndex("done")) == 1;
        tasks.add(t);
    }
} finally {
    cursor.close();
}

// UPDATE
ContentValues update = new ContentValues();
update.put("done", 1);
int rows = db.update("tasks", update, "id = ?", new String[]{String.valueOf(taskId)});

// DELETE
int deleted = db.delete("tasks", "id = ?", new String[]{String.valueOf(taskId)});
```

### OpenHarmony (ArkTS)
```typescript
import relationalStore from '@ohos.data.relationalStore';

interface Task {
  id: number;
  title: string;
  done: boolean;
}

// INSERT
const valueBucket: relationalStore.ValuesBucket = {
  title: 'Buy milk',
  done: 0
};
const id: number = await rdbStore.insert('tasks', valueBucket);

// QUERY
const predicates = new relationalStore.RdbPredicates('tasks');
predicates.equalTo('done', 0);
predicates.orderByAsc('title');
const resultSet = await rdbStore.query(predicates, ['id', 'title', 'done']);
const tasks: Task[] = [];
try {
  while (resultSet.goToNextRow()) {
    tasks.push({
      id: resultSet.getLong(resultSet.getColumnIndex('id')),
      title: resultSet.getString(resultSet.getColumnIndex('title')),
      done: resultSet.getLong(resultSet.getColumnIndex('done')) === 1
    });
  }
} finally {
  resultSet.close();
}

// UPDATE
const updateBucket: relationalStore.ValuesBucket = { done: 1 };
const updatePredicates = new relationalStore.RdbPredicates('tasks');
updatePredicates.equalTo('id', taskId);
const rows: number = await rdbStore.update(updateBucket, updatePredicates);

// DELETE
const deletePredicates = new relationalStore.RdbPredicates('tasks');
deletePredicates.equalTo('id', taskId);
const deleted: number = await rdbStore.delete(deletePredicates);
```

---

## 3. Transactions

### Android (Java)
```java
db.beginTransaction();
try {
    db.execSQL("UPDATE accounts SET balance = balance - 100 WHERE id = 1");
    db.execSQL("UPDATE accounts SET balance = balance + 100 WHERE id = 2");
    db.setTransactionSuccessful();
} finally {
    db.endTransaction();
}
```

### OpenHarmony (ArkTS)
```typescript
try {
  rdbStore.beginTransaction();
  await rdbStore.executeSql('UPDATE accounts SET balance = balance - 100 WHERE id = 1');
  await rdbStore.executeSql('UPDATE accounts SET balance = balance + 100 WHERE id = 2');
  rdbStore.commit();
} catch (e) {
  rdbStore.rollBack();
  throw e;
}
```

---

## 4. SharedPreferences → @ohos.data.preferences

### Android (Java)
```java
// Write
SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
prefs.edit()
    .putString("username", "alice")
    .putBoolean("darkMode", true)
    .putInt("loginCount", 5)
    .apply();

// Read
String username = prefs.getString("username", "");
boolean darkMode = prefs.getBoolean("darkMode", false);
int loginCount = prefs.getInt("loginCount", 0);

// Listener
prefs.registerOnSharedPreferenceChangeListener((sp, key) -> {
    Log.d("Prefs", "Changed: " + key);
});
```

### OpenHarmony (ArkTS)
```typescript
import dataPreferences from '@ohos.data.preferences';
import common from '@ohos.app.ability.common';
import { hilog } from '@kit.PerformanceAnalysisKit';

const context: common.UIAbilityContext = getContext(this) as common.UIAbilityContext;

// Write
const prefs = await dataPreferences.getPreferences(context, 'settings');
await prefs.put('username', 'alice');
await prefs.put('darkMode', true);
await prefs.put('loginCount', 5);
await prefs.flush(); // REQUIRED — equivalent to apply()/commit()

// Read
const username: string = await prefs.get('username', '') as string;
const darkMode: boolean = await prefs.get('darkMode', false) as boolean;
const loginCount: number = await prefs.get('loginCount', 0) as number;

// Listener
prefs.on('change', (key: string) => {
  hilog.debug(0x0000, 'Prefs', 'Changed: %{public}s', key);
});
```

---

## 5. ContentProvider → DataShareExtensionAbility

### Android (Java)
```java
public class TaskProvider extends ContentProvider {
    private TaskDbHelper dbHelper;

    @Override
    public boolean onCreate() {
        dbHelper = new TaskDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        return db.query("tasks", projection, selection, selectionArgs,
                        null, null, sortOrder);
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long id = db.insert("tasks", null, values);
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    // update() and delete() omitted for brevity
}
```

### OpenHarmony (ArkTS)
```typescript
import DataShareExtensionAbility from '@ohos.application.DataShareExtensionAbility';
import relationalStore from '@ohos.data.relationalStore';
import dataSharePredicates from '@ohos.data.dataSharePredicates';

let rdbStore: relationalStore.RdbStore;

export default class TaskShareAbility extends DataShareExtensionAbility {
  async onCreate(want: Want, callback: Function): Promise<void> {
    rdbStore = await relationalStore.getRdbStore(this.context, {
      name: 'tasks.db',
      securityLevel: relationalStore.SecurityLevel.S1
    });
    await rdbStore.executeSql(
      'CREATE TABLE IF NOT EXISTS tasks (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, done INTEGER DEFAULT 0)'
    );
    callback();
  }

  async query(
    uri: string,
    predicates: dataSharePredicates.DataSharePredicates,
    columns: string[],
    callback: Function
  ): Promise<void> {
    const rdbPredicates = await rdbStore.query(
      new relationalStore.RdbPredicates('tasks'),
      columns
    );
    callback(null, rdbPredicates);
  }

  async insert(uri: string, valueBucket: relationalStore.ValuesBucket, callback: Function): Promise<void> {
    const id = await rdbStore.insert('tasks', valueBucket);
    callback(null, id);
  }
}
```

Register in `module.json5`:
```json
{
  "extensionAbilities": [
    {
      "name": "TaskShareAbility",
      "type": "dataShare",
      "srcEntry": "./ets/datashare/TaskShareAbility.ts",
      "uri": "datashare:///com.example.tasks"
    }
  ]
}
```

---

## 6. ContentResolver → DataShareHelper

### Android (Java)
```java
ContentResolver resolver = getContentResolver();
Cursor cursor = resolver.query(
    Uri.parse("content://com.example.tasks/tasks"),
    new String[]{"id", "title"}, null, null, null);
```

### OpenHarmony (ArkTS)
```typescript
import dataShare from '@ohos.data.dataShare';
import dataSharePredicates from '@ohos.data.dataSharePredicates';

const uri = 'datashare:///com.example.tasks';
const helper = await dataShare.createDataShareHelper(context, uri);
const predicates = new dataSharePredicates.DataSharePredicates();
const resultSet = await helper.query(uri + '/tasks', predicates, ['id', 'title']);
```

---

## Common Pitfalls

1. **Forgetting `flush()`** — Preferences writes are in-memory until `flush()` is called.
2. **Not closing `ResultSet`** — Always wrap iteration in `try/finally` with `resultSet.close()`.
3. **Synchronous assumptions** — Every OH data call is async. Use `await` or the callback will fire after your function returns.
4. **Integer booleans** — OH RdbStore has no boolean column type. Use `INTEGER` (0/1) like Android, and convert in code.
5. **Security level** — `StoreConfig.securityLevel` is required. Use `S1` for normal app data, `S2`+ for sensitive data.
6. **Column index** — `ResultSet.getColumnIndex()` returns `-1` if the column does not exist; this will cause a silent wrong read. Validate column names.
