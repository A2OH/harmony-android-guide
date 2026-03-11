# A2OH-JAVA-TO-ARKTS: Java/Kotlin to ArkTS Language Translation

This skill covers foundational syntax and API translation from Java/Kotlin to ArkTS, OpenHarmony's TypeScript-based language. ArkTS is a strict superset of TypeScript with additional static checking and UI decorators.

---

## 1. Syntax Conversion Rules

### Variable Declarations
```java
// Java
int count = 0;
final String name = "hello";
String mutable = "world";
```
```typescript
// ArkTS
let count: number = 0;
const name: string = 'hello';
let mutable: string = 'world';
```

### Function / Method Signatures
```java
// Java
public int add(int a, int b) { return a + b; }
private void doWork(String input) { ... }
public static List<String> getNames() { ... }
```
```typescript
// ArkTS
add(a: number, b: number): number { return a + b; }
private doWork(input: string): void { ... }
static getNames(): Array<string> { ... }
```

### Access Modifiers
- Java `public` on a class method -> ArkTS: omit (public by default) or write `public`.
- Java `private`, `protected` -> ArkTS: same keywords.
- Java `package-private` (no modifier) -> ArkTS: no direct equivalent; use `public` or restructure.

### Classes and Inheritance
```java
// Java
public class Dog extends Animal implements Runnable {
    private final String breed;
    public Dog(String breed) {
        super();
        this.breed = breed;
    }
    @Override
    public void run() { ... }
}
```
```typescript
// ArkTS
class Dog extends Animal implements Runnable {
    private readonly breed: string;
    constructor(breed: string) {
        super();
        this.breed = breed;
    }
    run(): void { ... }
}
```

### Interfaces
```java
// Java
public interface Callback<T> {
    void onSuccess(T result);
    void onError(Exception e);
}
```
```typescript
// ArkTS
interface Callback<T> {
    onSuccess(result: T): void;
    onError(e: Error): void;
}
```

### Enums
```java
// Java
public enum Color { RED, GREEN, BLUE }
```
```typescript
// ArkTS
enum Color { RED, GREEN, BLUE }
```
For enums with fields or methods, convert to a class with static readonly instances or a union type.

### Lambdas and Functional Interfaces
```java
// Java
list.forEach(item -> System.out.println(item));
Runnable r = () -> doWork();
Comparator<Integer> cmp = (a, b) -> a - b;
```
```typescript
// ArkTS
list.forEach((item) => { console.log(item); });
let r = (): void => { doWork(); };
let cmp = (a: number, b: number): number => a - b;
```

---

## 2. Primitive and Basic Type Mapping

| Java Type | ArkTS Type | Notes |
|---|---|---|
| `int`, `long`, `short`, `byte` | `number` | All numeric primitives collapse to `number` |
| `float`, `double` | `number` | Same |
| `boolean` | `boolean` | Direct match |
| `char` | `string` | Single-character string |
| `String` | `string` | Lowercase in ArkTS |
| `void` | `void` | Or omit return type annotation |
| `Object` | `Object` or specific type | Prefer a concrete type |
| `byte[]` | `ArrayBuffer` or `Uint8Array` | `Uint8Array` for indexed access, `ArrayBuffer` for I/O |
| `int[]` / `double[]` | `number[]` | Or `Array<number>` |
| `String[]` | `string[]` | Or `Array<string>` |
| `Integer`, `Long`, etc. (boxed) | `number` | No boxing in ArkTS |
| `Boolean` (boxed) | `boolean` | Same |

---

## 3. Collections Mapping

| Java Collection | ArkTS Equivalent | Example |
|---|---|---|
| `ArrayList<T>` | `Array<T>` or `T[]` | `let items: string[] = [];` |
| `LinkedList<T>` | `Array<T>` | No linked list; use array |
| `HashMap<K,V>` | `Map<K,V>` | `let map = new Map<string, number>();` |
| `TreeMap<K,V>` | `Map<K,V>` | Sort manually if order needed |
| `HashSet<T>` | `Set<T>` | `let set = new Set<string>();` |
| `Queue<T>` | `Array<T>` | Use `push()` / `shift()` |
| `Stack<T>` | `Array<T>` | Use `push()` / `pop()` |
| `Collections.unmodifiableList` | `ReadonlyArray<T>` | Or spread into new array |

### Common Operations
```java
// Java
list.add(item);          list.get(0);         list.size();
map.put(key, val);       map.get(key);        map.containsKey(key);
set.add(item);           set.contains(item);  set.remove(item);
```
```typescript
// ArkTS
list.push(item);         list[0];             list.length;
map.set(key, val);       map.get(key);        map.has(key);
set.add(item);           set.has(item);       set.delete(item);
```

### Java Streams to Array Methods
```java
// Java
List<String> names = users.stream()
    .filter(u -> u.isActive())
    .map(u -> u.getName())
    .sorted()
    .collect(Collectors.toList());
```
```typescript
// ArkTS
let names: string[] = users
    .filter((u) => u.isActive())
    .map((u) => u.getName())
    .sort();
```

---

## 4. Java Stdlib to ArkTS/OH Equivalents

| Java API | ArkTS / OH Equivalent |
|---|---|
| `System.out.println()` | `console.log()` |
| `Log.d(TAG, msg)` | `hilog.info(0x0000, 'TAG', '%{public}s', msg)` |
| `Log.e(TAG, msg)` | `hilog.error(0x0000, 'TAG', '%{public}s', msg)` |
| `String.format(...)` | Template literal: `` `Hello ${name}` `` |
| `Integer.parseInt(s)` | `Number.parseInt(s)` or `parseInt(s)` |
| `Math.max/min/abs` | `Math.max()` / `Math.min()` / `Math.abs()` (same) |
| `System.currentTimeMillis()` | `Date.now()` |
| `UUID.randomUUID()` | `util.generateRandomUUID()` from `@ohos.util` |
| `Base64.encode/decode` | `util.Base64Helper` from `@ohos.util` |
| `Pattern` / `Matcher` | `RegExp` / `string.match()` |
| `StringBuilder` | String concatenation or template literals |
| `Arrays.sort()` | `array.sort()` |
| `Collections.sort()` | `array.sort()` |

### Logging Setup
```typescript
import { hilog } from '@kit.PerformanceAnalysisKit';

const TAG: string = 'MyComponent';
const DOMAIN: number = 0x0000;

hilog.info(DOMAIN, TAG, 'User logged in: %{public}s', username);
hilog.error(DOMAIN, TAG, 'Failed to load: %{public}s', error.message);
```

---

## 5. Async Patterns

### Thread / Runnable to TaskPool
```java
// Java
new Thread(() -> {
    String result = heavyWork();
    runOnUiThread(() -> updateUI(result));
}).start();
```
```typescript
// ArkTS
import { taskpool } from '@kit.ArkTS';

@Concurrent
function heavyWork(): string { /* ... */ return result; }

let task = new taskpool.Task(heavyWork);
taskpool.execute(task).then((result: Object) => {
    // Back on calling thread — update UI here
});
```

### AsyncTask to async/await
```java
// Java
new AsyncTask<String, Void, Result>() {
    protected Result doInBackground(String... params) { return fetch(params[0]); }
    protected void onPostExecute(Result r) { display(r); }
}.execute(url);
```
```typescript
// ArkTS
async function loadData(url: string): Promise<void> {
    let result: Result = await fetch(url);
    display(result);
}
```

### Handler.postDelayed to setTimeout
```java
// Java
new Handler(Looper.getMainLooper()).postDelayed(() -> refresh(), 3000);
```
```typescript
// ArkTS
setTimeout(() => { refresh(); }, 3000);
```

### Handler periodic to setInterval
```java
// Java — repeating via Handler
handler.postDelayed(runnable, interval);  // inside runnable re-posts itself
```
```typescript
// ArkTS
let id = setInterval(() => { poll(); }, 5000);
// Stop with: clearInterval(id);
```

---

## 6. Null Safety

### Nullable Types
```java
// Java
@Nullable String name;           // may be null
if (name != null) { use(name); }
```
```typescript
// ArkTS
let name: string | undefined;   // or string | null
if (name) { use(name); }
```

### Optional Chaining and Nullish Coalescing
```java
// Java
String city = user != null && user.getAddress() != null ? user.getAddress().getCity() : "Unknown";
```
```typescript
// ArkTS
let city: string = user?.address?.city ?? 'Unknown';
```

### Java Optional
```java
// Java
Optional<User> opt = findUser(id);
String name = opt.map(User::getName).orElse("anon");
```
```typescript
// ArkTS
let user: User | undefined = findUser(id);
let name: string = user?.name ?? 'anon';
```

**Rule:** Replace all `Optional<T>` with `T | undefined`. Replace `.isPresent()` with `!== undefined`, `.get()` with direct access (after null check), `.orElse(x)` with `?? x`.

---

## 7. Exception Handling

Java and ArkTS both use `try/catch/finally`. Key differences:

- ArkTS `catch` parameter is untyped by default (type is `unknown` or `Error`).
- No multi-catch (`catch (A | B e)`) — use `instanceof` checks.
- No checked exceptions in ArkTS; do not translate `throws` clauses.

```java
// Java
try {
    parse(input);
} catch (IOException | ParseException e) {
    Log.e(TAG, "Error", e);
} finally {
    cleanup();
}
```
```typescript
// ArkTS
try {
    parse(input);
} catch (e) {
    let error = e as Error;
    hilog.error(0x0000, TAG, 'Error: %{public}s', error.message);
} finally {
    cleanup();
}
```

Custom exceptions become classes extending `Error`:
```typescript
class ValidationError extends Error {
    constructor(message: string) {
        super(message);
        this.name = 'ValidationError';
    }
}
```

---

## 8. Serialization

### Serializable / Parcelable to Plain Objects
Java `Serializable` and Android `Parcelable` have no direct ArkTS equivalent. Convert to plain objects or interfaces and use JSON for transport.

```java
// Java
public class UserData implements Parcelable {
    public String name;
    public int age;
    // ... Parcelable boilerplate
}
```
```typescript
// ArkTS
interface UserData {
    name: string;
    age: number;
}

// Serialize
let json: string = JSON.stringify(userData);
// Deserialize
let restored: UserData = JSON.parse(json) as UserData;
```

For cross-process transfer in OH, use `@ohos.rpc.MessageSequence` if needed.

---

## 9. File I/O

```java
// Java
File file = new File(getFilesDir(), "data.txt");
FileOutputStream fos = new FileOutputStream(file);
fos.write("hello".getBytes());
fos.close();
String content = new String(Files.readAllBytes(file.toPath()));
```
```typescript
// ArkTS
import { fileIo as fs } from '@kit.CoreFileKit';

let context = getContext(this);
let filePath: string = context.filesDir + '/data.txt';

// Write
let file = fs.openSync(filePath, fs.OpenMode.CREATE | fs.OpenMode.WRITE_ONLY);
fs.writeSync(file.fd, 'hello');
fs.closeSync(file);

// Read
let readFile = fs.openSync(filePath, fs.OpenMode.READ_ONLY);
let buf = new ArrayBuffer(1024);
let bytesRead = fs.readSync(readFile.fd, buf);
fs.closeSync(readFile);
let content: string = String.fromCharCode(...new Uint8Array(buf.slice(0, bytesRead)));
```

### Async File I/O
```typescript
import { fileIo as fs } from '@kit.CoreFileKit';

async function readFileAsync(path: string): Promise<string> {
    let file = await fs.open(path, fs.OpenMode.READ_ONLY);
    let stat = await fs.stat(path);
    let buf = new ArrayBuffer(stat.size);
    await fs.read(file.fd, buf);
    await fs.close(file);
    return String.fromCharCode(...new Uint8Array(buf));
}
```

---

## 10. JSON Handling

### org.json / Gson to Built-in JSON
```java
// Java (org.json)
JSONObject obj = new JSONObject();
obj.put("name", "Alice");
obj.put("age", 30);
String json = obj.toString();

JSONObject parsed = new JSONObject(jsonString);
String name = parsed.getString("name");
```
```typescript
// ArkTS
interface Person { name: string; age: number; }

let obj: Person = { name: 'Alice', age: 30 };
let json: string = JSON.stringify(obj);

let parsed: Person = JSON.parse(jsonString) as Person;
let name: string = parsed.name;
```

### Gson with Custom Types
```java
// Java (Gson)
Gson gson = new Gson();
User user = gson.fromJson(jsonString, User.class);
String json = gson.toJson(user);
```
```typescript
// ArkTS — no reflection, use interfaces and JSON directly
let user: User = JSON.parse(jsonString) as User;
let json: string = JSON.stringify(user);
// For validation, manually check fields after parsing.
```

---

## 11. Date and Time

### java.time to ArkTS Date / Intl
```java
// Java
LocalDateTime now = LocalDateTime.now();
long millis = System.currentTimeMillis();
String formatted = DateTimeFormatter.ofPattern("yyyy-MM-dd").format(now);
Duration diff = Duration.between(start, end);
```
```typescript
// ArkTS
let now: Date = new Date();
let millis: number = Date.now();
let formatted: string = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}-${String(now.getDate()).padStart(2, '0')}`;
let diffMs: number = end.getTime() - start.getTime();
```

### Locale-Aware Formatting with @ohos.intl
```typescript
import { intl } from '@kit.LocalizationKit';

let formatter = new intl.DateTimeFormat('en-US', {
    year: 'numeric', month: '2-digit', day: '2-digit',
    hour: '2-digit', minute: '2-digit'
});
let display: string = formatter.format(new Date());
```

---

## 12. Quick Reference: Keyword and Pattern Map

| Java / Kotlin | ArkTS |
|---|---|
| `final` variable | `const` |
| `final` field | `readonly` |
| `static` | `static` |
| `instanceof` | `instanceof` |
| `this` | `this` |
| `super` | `super` |
| `new Foo()` | `new Foo()` |
| `for (T item : list)` | `for (let item of list)` |
| `for (int i=0; i<n; i++)` | `for (let i = 0; i < n; i++)` |
| `switch/case` | `switch/case` (same) |
| `@Override` | omit |
| `@Nullable` | `T \| undefined` |
| `synchronized` | N/A (use `taskpool` / `worker` for concurrency) |
| `volatile` | N/A |
| `throws Exception` | omit (no checked exceptions) |
| `package com.foo` | file-level `export` / `import` |
| `import com.foo.Bar` | `import { Bar } from './Bar'` |
| Kotlin `val` | `const` or `let` (depending on scope) |
| Kotlin `var` | `let` |
| Kotlin `data class` | `interface` or `class` with fields |
| Kotlin `?.` | `?.` (same) |
| Kotlin `?:` | `??` |
| Kotlin `it` | explicit parameter name in arrow function |
| Kotlin `suspend fun` | `async function` returning `Promise` |
| Kotlin `coroutineScope` | `Promise.all()` or `taskpool` |
