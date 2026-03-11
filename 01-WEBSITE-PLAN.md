# Android↔OpenHarmony API Compatibility Portal — Full Plan

## 1. System Overview

```
┌─────────────────────────────────────────────────────────────────────────┐
│                         DATA PIPELINE                                   │
│                                                                         │
│  ┌──────────────┐    ┌──────────────┐    ┌────────────────────────────┐ │
│  │ Android 11   │    │ OpenHarmony  │    │ AI Scoring Engine          │ │
│  │ current.txt  │───▶│ .d.ts / .h   │───▶│ (Claude API batch)        │ │
│  │ 56,387 APIs  │    │ 36,599 APIs  │    │ Score + Description +     │ │
│  └──────────────┘    └──────────────┘    │ Migration Notes per API   │ │
│                                          └─────────────┬──────────────┘ │
│                                                        │                │
│                                                        ▼                │
│                                          ┌─────────────────────────────┐│
│                                          │   SQLite Database           ││
│                                          │   api_compat.db             ││
│                                          └─────────────┬───────────────┘│
└────────────────────────────────────────────────────────┼────────────────┘
                                                         │
┌────────────────────────────────────────────────────────┼────────────────┐
│                         WEB APPLICATION                │                │
│                                                        ▼                │
│  ┌──────────────────────────────────────────────────────────────────┐   │
│  │  Backend: Python FastAPI                                         │   │
│  │  - REST API: /api/android/, /api/oh/, /api/mappings/            │   │
│  │  - Full-text search (SQLite FTS5)                               │   │
│  │  - Filter by subsystem, score, type, deprecated                 │   │
│  │  - Stats/aggregation endpoints                                  │   │
│  └──────────────────────────────────────┬───────────────────────────┘   │
│                                         │                               │
│  ┌──────────────────────────────────────▼───────────────────────────┐   │
│  │  Frontend: React + Vite + TailwindCSS                            │   │
│  │                                                                   │   │
│  │  Pages:                                                           │   │
│  │  ┌─────────────┐ ┌──────────────┐ ┌──────────────────────────┐   │   │
│  │  │  Dashboard   │ │  API Browser │ │  Side-by-Side Compare    │   │   │
│  │  │  - Stats     │ │  - Search    │ │  - Android vs OH         │   │   │
│  │  │  - Scores    │ │  - Filters   │ │  - Score visualization   │   │   │
│  │  │  - Heatmap   │ │  - Tree view │ │  - Migration notes       │   │   │
│  │  └─────────────┘ └──────────────┘ └──────────────────────────┘   │   │
│  │  ┌─────────────┐ ┌──────────────┐ ┌──────────────────────────┐   │   │
│  │  │  Subsystem   │ │  Gap Report  │ │  Migration Guide         │   │   │
│  │  │  Overview    │ │  - Unmapped  │ │  - Per-app assessment    │   │   │
│  │  │  - Per area  │ │  - Critical  │ │  - Effort estimator      │   │   │
│  │  └─────────────┘ └──────────────┘ └──────────────────────────┘   │   │
│  └───────────────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## 2. Database Schema (SQLite + FTS5)

### 2.1 Core Tables

```sql
-- ============================================================
-- ANDROID APIs
-- ============================================================
CREATE TABLE android_packages (
    id          INTEGER PRIMARY KEY,
    name        TEXT NOT NULL UNIQUE,       -- e.g. "android.app"
    subsystem   TEXT NOT NULL,              -- e.g. "App Framework"
    description TEXT
);

CREATE TABLE android_types (
    id          INTEGER PRIMARY KEY,
    package_id  INTEGER NOT NULL REFERENCES android_packages(id),
    name        TEXT NOT NULL,              -- e.g. "Activity"
    full_name   TEXT NOT NULL,              -- e.g. "Activity<T extends Context>"
    kind        TEXT NOT NULL,              -- "class" | "interface" | "enum"
    is_abstract BOOLEAN DEFAULT 0,
    is_final    BOOLEAN DEFAULT 0,
    is_static   BOOLEAN DEFAULT 0,
    is_deprecated BOOLEAN DEFAULT 0,
    extends     TEXT,                       -- parent class
    implements  TEXT,                       -- interfaces
    description TEXT,                       -- AI-generated description
    UNIQUE(package_id, name)
);

CREATE TABLE android_apis (
    id          INTEGER PRIMARY KEY,
    type_id     INTEGER NOT NULL REFERENCES android_types(id),
    kind        TEXT NOT NULL,              -- "method" | "field" | "constructor" | "enum_constant"
    name        TEXT NOT NULL,              -- e.g. "onCreate", "EXTRA_INTENT"
    signature   TEXT NOT NULL,              -- full signature from current.txt
    return_type TEXT,                       -- for methods
    params      TEXT,                       -- JSON array of parameter types
    is_deprecated BOOLEAN DEFAULT 0,
    is_static   BOOLEAN DEFAULT 0,
    since_api   INTEGER,                   -- API level introduced
    description TEXT,                       -- AI-generated description

    -- Tags for filtering
    subsystem   TEXT,                       -- "App Framework", "UI", "Network", etc.
    category    TEXT,                       -- "lifecycle", "event", "data", "config", etc.

    -- Compatibility info (filled by scoring pipeline)
    compat_score    REAL,                  -- 1.0 to 10.0
    compat_summary  TEXT,                  -- one-line summary
    migration_notes TEXT,                  -- detailed migration guidance
    effort_level    TEXT                   -- "trivial" | "easy" | "moderate" | "hard" | "rewrite" | "impossible"
);

-- ============================================================
-- OPENHARMONY APIs
-- ============================================================
CREATE TABLE oh_modules (
    id          INTEGER PRIMARY KEY,
    name        TEXT NOT NULL UNIQUE,       -- e.g. "@ohos.net.http"
    sdk_type    TEXT NOT NULL,              -- "js" | "ndk"
    subsystem   TEXT NOT NULL,              -- e.g. "Communication"
    file_path   TEXT,                       -- source .d.ts or .h path
    description TEXT
);

CREATE TABLE oh_types (
    id          INTEGER PRIMARY KEY,
    module_id   INTEGER NOT NULL REFERENCES oh_modules(id),
    name        TEXT NOT NULL,              -- e.g. "HttpRequest"
    kind        TEXT NOT NULL,              -- "interface" | "class" | "enum" | "type" | "struct" | "function"
    is_deprecated BOOLEAN DEFAULT 0,
    description TEXT,
    UNIQUE(module_id, name)
);

CREATE TABLE oh_apis (
    id          INTEGER PRIMARY KEY,
    type_id     INTEGER REFERENCES oh_types(id),   -- NULL for module-level functions
    module_id   INTEGER NOT NULL REFERENCES oh_modules(id),
    kind        TEXT NOT NULL,              -- "method" | "property" | "function" | "enum_value" | "c_function" | "macro" | "typedef"
    name        TEXT NOT NULL,
    signature   TEXT NOT NULL,
    return_type TEXT,
    params      TEXT,                       -- JSON
    is_deprecated BOOLEAN DEFAULT 0,
    description TEXT,

    subsystem   TEXT,
    category    TEXT
);

-- ============================================================
-- API MAPPINGS (the core value)
-- ============================================================
CREATE TABLE api_mappings (
    id              INTEGER PRIMARY KEY,
    android_api_id  INTEGER NOT NULL REFERENCES android_apis(id),
    oh_api_id       INTEGER REFERENCES oh_apis(id),  -- NULL = no equivalent

    -- Compatibility scoring
    score           REAL NOT NULL,          -- 1.0 (impossible) to 10.0 (identical)
    confidence      REAL DEFAULT 0.8,       -- AI confidence in this mapping

    -- Classification
    mapping_type    TEXT NOT NULL,          -- "direct" | "near" | "partial" | "composite" | "none"
    effort_level    TEXT NOT NULL,          -- "trivial" | "easy" | "moderate" | "hard" | "rewrite" | "impossible"

    -- Detailed descriptions
    android_description TEXT NOT NULL,      -- What the Android API does
    oh_description      TEXT,              -- What the OH API does (NULL if no equivalent)
    gap_description     TEXT,              -- What's different/missing
    migration_guide     TEXT,              -- Step-by-step how to migrate
    code_example_android TEXT,             -- Sample Android usage
    code_example_oh      TEXT,             -- Equivalent OH code

    -- Tags
    paradigm_shift  BOOLEAN DEFAULT 0,     -- requires fundamental approach change
    needs_native    BOOLEAN DEFAULT 0,     -- requires NDK/native bridge
    needs_ui_rewrite BOOLEAN DEFAULT 0,    -- UI layer must be rewritten
    security_impact BOOLEAN DEFAULT 0,     -- security model differences

    UNIQUE(android_api_id, oh_api_id)
);

-- ============================================================
-- SUBSYSTEMS (shared taxonomy)
-- ============================================================
CREATE TABLE subsystems (
    id          INTEGER PRIMARY KEY,
    name        TEXT NOT NULL UNIQUE,       -- "App Lifecycle"
    android_packages TEXT,                  -- JSON: ["android.app", "android.app.job"]
    oh_modules  TEXT,                       -- JSON: ["@ohos.app.ability"]
    description TEXT,
    overall_score REAL,                     -- average compat score for this subsystem
    api_count_android INTEGER,
    api_count_oh INTEGER,
    coverage_pct REAL                       -- % of Android APIs with OH equivalent
);

-- ============================================================
-- TAGS (flexible tagging)
-- ============================================================
CREATE TABLE tags (
    id          INTEGER PRIMARY KEY,
    name        TEXT NOT NULL UNIQUE
);

CREATE TABLE api_tags (
    api_id      INTEGER NOT NULL,
    api_platform TEXT NOT NULL,             -- "android" | "oh"
    tag_id      INTEGER NOT NULL REFERENCES tags(id),
    PRIMARY KEY (api_id, api_platform, tag_id)
);

-- ============================================================
-- FULL-TEXT SEARCH (FTS5)
-- ============================================================
CREATE VIRTUAL TABLE android_apis_fts USING fts5(
    name, signature, description, migration_notes,
    content='android_apis', content_rowid='id'
);

CREATE VIRTUAL TABLE oh_apis_fts USING fts5(
    name, signature, description,
    content='oh_apis', content_rowid='id'
);

CREATE VIRTUAL TABLE mappings_fts USING fts5(
    android_description, oh_description, gap_description, migration_guide,
    content='api_mappings', content_rowid='id'
);

-- ============================================================
-- INDEXES
-- ============================================================
CREATE INDEX idx_android_apis_type ON android_apis(type_id);
CREATE INDEX idx_android_apis_subsystem ON android_apis(subsystem);
CREATE INDEX idx_android_apis_score ON android_apis(compat_score);
CREATE INDEX idx_android_apis_effort ON android_apis(effort_level);
CREATE INDEX idx_android_apis_kind ON android_apis(kind);
CREATE INDEX idx_android_types_package ON android_types(package_id);

CREATE INDEX idx_oh_apis_type ON oh_apis(type_id);
CREATE INDEX idx_oh_apis_module ON oh_apis(module_id);
CREATE INDEX idx_oh_apis_subsystem ON oh_apis(subsystem);

CREATE INDEX idx_mappings_android ON api_mappings(android_api_id);
CREATE INDEX idx_mappings_oh ON api_mappings(oh_api_id);
CREATE INDEX idx_mappings_score ON api_mappings(score);
CREATE INDEX idx_mappings_type ON api_mappings(mapping_type);
CREATE INDEX idx_mappings_effort ON api_mappings(effort_level);

-- ============================================================
-- VIEWS (convenience)
-- ============================================================
CREATE VIEW v_android_api_full AS
SELECT
    a.id, a.name as api_name, a.signature, a.kind, a.is_deprecated,
    a.compat_score, a.effort_level, a.subsystem,
    t.name as type_name, t.kind as type_kind,
    p.name as package_name, p.subsystem as package_subsystem
FROM android_apis a
JOIN android_types t ON a.type_id = t.id
JOIN android_packages p ON t.package_id = p.id;

CREATE VIEW v_mapping_full AS
SELECT
    m.*,
    aa.name as android_api_name, aa.signature as android_signature,
    at.name as android_type_name, ap.name as android_package,
    oa.name as oh_api_name, oa.signature as oh_signature,
    ot.name as oh_type_name, om.name as oh_module
FROM api_mappings m
JOIN android_apis aa ON m.android_api_id = aa.id
JOIN android_types at ON aa.type_id = at.id
JOIN android_packages ap ON at.package_id = ap.id
LEFT JOIN oh_apis oa ON m.oh_api_id = oa.id
LEFT JOIN oh_types ot ON oa.type_id = ot.id
LEFT JOIN oh_modules om ON oa.module_id = om.id;

CREATE VIEW v_subsystem_summary AS
SELECT
    s.name as subsystem,
    s.api_count_android,
    s.api_count_oh,
    s.coverage_pct,
    s.overall_score,
    COUNT(CASE WHEN m.score >= 8 THEN 1 END) as direct_mappable,
    COUNT(CASE WHEN m.score >= 5 AND m.score < 8 THEN 1 END) as partial_mappable,
    COUNT(CASE WHEN m.score >= 3 AND m.score < 5 THEN 1 END) as hard_to_map,
    COUNT(CASE WHEN m.score < 3 THEN 1 END) as no_equivalent
FROM subsystems s
LEFT JOIN android_apis a ON a.subsystem = s.name
LEFT JOIN api_mappings m ON m.android_api_id = a.id
GROUP BY s.name;
```

### 2.2 Estimated Row Counts

| Table | Rows | Notes |
|-------|-----:|-------|
| android_packages | 138 | android.* packages only |
| android_types | ~2,050 | classes, interfaces, enums |
| android_apis | ~42,000 | methods, fields, ctors (android.* only) |
| oh_modules | ~200 | @ohos.* modules + NDK headers |
| oh_types | ~1,200 | interfaces, classes, enums, structs |
| oh_apis | ~12,000 | JS/TS methods + NDK functions |
| api_mappings | ~42,000 | one per Android API (some map to NULL) |
| subsystems | ~25 | logical groupings |
| tags | ~100 | flexible tags |
| **Total DB size** | **~50MB** | with descriptions and FTS indexes |

---

## 3. Data Pipeline

### 3.1 Step 1: Parse & Load Android APIs

```
Source: ~/aosp-android-11/frameworks/base/api/current.txt
Parser: parse_api.py (already built)
Output: Populate android_packages, android_types, android_apis tables
```

### 3.2 Step 2: Parse & Load OpenHarmony APIs

```
Source: ~/openharmony/interface/sdk-js/api/*.d.ts
        ~/openharmony/interface/sdk_c/**/*.h
Parser: New TypeScript .d.ts parser + C header parser
Output: Populate oh_modules, oh_types, oh_apis tables
```

### 3.3 Step 3: Auto-Map by Name Similarity

```
Algorithm:
1. Exact name match (e.g., "startActivity" ↔ "startAbility")
2. Fuzzy name match (Levenshtein, token overlap)
3. Semantic match by subsystem (e.g., android.media.* → @ohos.multimedia.*)
4. Signature similarity (parameter count, types)

Output: Initial api_mappings rows with mapping_type="auto", low confidence
```

### 3.4 Step 4: AI Scoring Pipeline (Claude API batch)

```
For each Android API (or batch of related APIs):
  Input:  Android API signature + context
          Candidate OH API(s) from Step 3
          Both platform's review reports for context

  Output: {
    score: 1-10,
    mapping_type: "direct"|"near"|"partial"|"composite"|"none",
    effort_level: "trivial"|...|"impossible",
    android_description: "...",
    oh_description: "...",
    gap_description: "...",
    migration_guide: "...",
    code_example_android: "...",
    code_example_oh: "..."
  }

Batch strategy:
  - Group by subsystem (process all android.net.* together)
  - ~42,000 APIs / ~50 APIs per batch = ~840 API calls
  - Use Claude API batch mode for cost efficiency
  - Estimated: ~$50-100 in API costs
```

### 3.5 Step 5: Manual Review & Override

```
Website has admin mode:
  - Flag auto-scored mappings for review
  - Override scores, descriptions
  - Add manual mappings AI missed
  - Mark verified mappings as "confirmed"
```

---

## 4. Backend (Python FastAPI)

### 4.1 Project Structure

```
backend/
├── main.py                 # FastAPI app entry
├── database.py             # SQLite connection + helpers
├── models.py               # Pydantic models
├── routers/
│   ├── android.py          # /api/android/...
│   ├── openharmony.py      # /api/oh/...
│   ├── mappings.py         # /api/mappings/...
│   ├── search.py           # /api/search/...
│   ├── subsystems.py       # /api/subsystems/...
│   └── stats.py            # /api/stats/...
├── services/
│   ├── search_service.py   # FTS5 search logic
│   ├── scoring_service.py  # AI scoring pipeline
│   └── import_service.py   # Data import pipeline
└── data/
    └── api_compat.db       # SQLite database
```

### 4.2 Key API Endpoints

```
GET  /api/android/packages                    # List all packages
GET  /api/android/packages/{name}/types       # Types in package
GET  /api/android/types/{id}/apis             # APIs in type
GET  /api/android/apis/{id}                   # Full API detail + mapping

GET  /api/oh/modules                          # List all OH modules
GET  /api/oh/modules/{name}/types             # Types in module
GET  /api/oh/apis/{id}                        # Full OH API detail

GET  /api/mappings?subsystem=&score_min=&score_max=&effort=&type=
GET  /api/mappings/{android_api_id}           # Mapping for specific API

GET  /api/search?q=&platform=&subsystem=&score_min=&score_max=
GET  /api/search/autocomplete?q=

GET  /api/subsystems                          # Subsystem overview with stats
GET  /api/subsystems/{name}                   # Detailed subsystem report

GET  /api/stats/overview                      # Dashboard data
GET  /api/stats/score-distribution            # Histogram of scores
GET  /api/stats/coverage-by-subsystem         # Coverage percentages
GET  /api/stats/effort-breakdown              # Effort level distribution
GET  /api/stats/gaps                          # Top gaps/blockers
```

---

## 5. Frontend (React + Vite + TailwindCSS)

### 5.1 Project Structure

```
frontend/
├── src/
│   ├── App.tsx
│   ├── main.tsx
│   ├── api/                    # API client
│   │   └── client.ts
│   ├── components/
│   │   ├── layout/
│   │   │   ├── Sidebar.tsx     # Subsystem navigation tree
│   │   │   ├── Header.tsx      # Search bar + filters
│   │   │   └── Layout.tsx
│   │   ├── search/
│   │   │   ├── SearchBar.tsx   # Full-text search with autocomplete
│   │   │   └── FilterPanel.tsx # Score range, subsystem, effort, deprecated
│   │   ├── api/
│   │   │   ├── ApiCard.tsx          # Single API display card
│   │   │   ├── ApiList.tsx          # Paginated list of APIs
│   │   │   ├── ApiDetail.tsx        # Full detail view
│   │   │   ├── ComparisonView.tsx   # Side-by-side Android vs OH
│   │   │   ├── SignatureDisplay.tsx  # Syntax-highlighted signature
│   │   │   └── ScoreBadge.tsx       # Color-coded compatibility score
│   │   ├── charts/
│   │   │   ├── ScoreHeatmap.tsx     # Subsystem × score heatmap
│   │   │   ├── CoverageChart.tsx    # Pie/bar chart of coverage
│   │   │   ├── EffortBreakdown.tsx  # Stacked bar of effort levels
│   │   │   └── SubsystemRadar.tsx   # Radar chart per subsystem
│   │   └── migration/
│   │       ├── MigrationGuide.tsx   # Step-by-step for an API
│   │       ├── CodeComparison.tsx   # Side-by-side code examples
│   │       └── GapReport.tsx        # Unmapped APIs list
│   ├── pages/
│   │   ├── Dashboard.tsx            # Overview with stats + charts
│   │   ├── AndroidBrowser.tsx       # Browse Android APIs (tree + list)
│   │   ├── OhBrowser.tsx            # Browse OH APIs
│   │   ├── MappingExplorer.tsx      # Explore all mappings with filters
│   │   ├── SubsystemDetail.tsx      # Deep dive per subsystem
│   │   ├── ApiDetail.tsx            # Single API full detail page
│   │   ├── GapAnalysis.tsx          # All unmapped/low-score APIs
│   │   └── MigrationEstimator.tsx   # Paste APK manifest → get effort estimate
│   └── utils/
│       ├── colors.ts                # Score → color mapping
│       └── formatters.ts
├── package.json
├── vite.config.ts
└── tailwind.config.js
```

### 5.2 Page Designs

#### Dashboard (Landing Page)
```
┌─────────────────────────────────────────────────────────────────┐
│  Android → OpenHarmony API Compatibility Portal                  │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │  🔍 Search APIs: [_________________________________]    │    │
│  └─────────────────────────────────────────────────────────┘    │
│                                                                  │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────────────┐   │
│  │  56,387  │ │  36,599  │ │   67%    │ │  Avg Score: 6.2  │   │
│  │ Android  │ │ OH APIs  │ │ Mapped   │ │  ████████░░ /10  │   │
│  │  APIs    │ │          │ │          │ │                   │   │
│  └──────────┘ └──────────┘ └──────────┘ └──────────────────┘   │
│                                                                  │
│  ┌─ Score Distribution ──────────┐ ┌─ Coverage by Subsystem ──┐ │
│  │  10: ██████████ 12%           │ │  Networking    ████████ 85%│ │
│  │   9: ████████ 10%             │ │  Storage       ███████ 78% │ │
│  │   8: ██████████████ 18%       │ │  Media         ██████ 65%  │ │
│  │   7: ████████████ 15%         │ │  App Lifecycle █████ 55%   │ │
│  │   6: ██████████ 12%           │ │  Security      ████ 45%    │ │
│  │   5: ████████ 10%             │ │  UI/Widgets    ██ 15%      │ │
│  │   4: ██████ 8%                │ │  IPC/Binder    █ 10%       │ │
│  │  1-3: ██████████████ 15%      │ │                             │ │
│  └───────────────────────────────┘ └─────────────────────────────┘ │
│                                                                  │
│  ┌─ Effort Breakdown ───────────────────────────────────────────┐ │
│  │  Trivial ████████████████ 25%                                 │ │
│  │  Easy    ██████████████ 20%                                   │ │
│  │  Moderate████████████ 18%                                     │ │
│  │  Hard    ████████ 12%                                         │ │
│  │  Rewrite ██████████████ 20%                                   │ │
│  │  Impossible ██ 5%                                              │ │
│  └───────────────────────────────────────────────────────────────┘ │
└──────────────────────────────────────────────────────────────────┘
```

#### API Browser Page
```
┌─────────────────────────────────────────────────────────────────┐
│ ┌─ Sidebar (Tree) ───┐ ┌─ API List ──────────────────────────┐ │
│ │                     │ │                                      │ │
│ │ ▼ android.app       │ │  Filters: Score [1-10] Effort [All]│ │
│ │   ▶ Activity        │ │  Showing 1,116 APIs in android.app  │ │
│ │   ▶ Service         │ │                                      │ │
│ │   ▶ Fragment        │ │  ┌─────────────────────────────────┐ │ │
│ │   ▶ Notification*   │ │  │ Activity.onCreate(Bundle)       │ │ │
│ │   ▶ AlarmManager    │ │  │ Score: ██████░░░░ 7/10          │ │ │
│ │ ▼ android.content   │ │  │ → UIAbility.onCreate(Want)      │ │ │
│ │   ▶ Context         │ │  │ Effort: moderate                 │ │ │
│ │   ▶ Intent          │ │  └─────────────────────────────────┘ │ │
│ │   ▶ ContentProvider │ │  ┌─────────────────────────────────┐ │ │
│ │ ▼ android.view      │ │  │ Activity.startActivity(Intent)  │ │ │
│ │   ▶ View            │ │  │ Score: █████░░░░░ 5/10          │ │ │
│ │   ▶ ViewGroup       │ │  │ → UIAbilityContext.startAbility │ │ │
│ │ ▼ android.widget    │ │  │ Effort: hard (Intent→Want)      │ │ │
│ │   ▶ TextView        │ │  └─────────────────────────────────┘ │ │
│ │   ▶ Button          │ │  ┌─────────────────────────────────┐ │ │
│ │   ▶ RecyclerView    │ │  │ Activity.finish()               │ │ │
│ │ ▼ android.net       │ │  │ Score: █████████░ 9/10          │ │ │
│ │ ▼ android.media     │ │  │ → UIAbilityContext.terminateSelf│ │ │
│ │ ...                 │ │  │ Effort: trivial                  │ │ │
│ └─────────────────────┘ │  └─────────────────────────────────┘ │ │
│                         └──────────────────────────────────────┘ │
└──────────────────────────────────────────────────────────────────┘
```

#### API Detail Page (Side-by-Side Comparison)
```
┌──────────────────────────────────────────────────────────────────┐
│  Activity.startActivityForResult(Intent, int)                     │
│  Compatibility Score: ██████░░░░ 5/10  |  Effort: HARD           │
│  Subsystem: App Lifecycle  |  Tags: #navigation #intent          │
├──────────────────────────────┬───────────────────────────────────┤
│  ANDROID                     │  OPENHARMONY                      │
├──────────────────────────────┼───────────────────────────────────┤
│  Package: android.app        │  Module: @ohos.app.ability         │
│  Class: Activity             │  Class: UIAbilityContext           │
│                              │                                    │
│  Signature:                  │  Signature:                        │
│  void startActivityForResult │  startAbilityForResult(            │
│    (Intent intent,           │    want: Want,                     │
│     int requestCode)         │    options?: StartOptions          │
│                              │  ): Promise<AbilityResult>         │
│                              │                                    │
│  Description:                │  Description:                      │
│  Launches a new Activity     │  Starts a UIAbility and returns    │
│  and expects a result back   │  a result via Promise. Uses Want   │
│  via onActivityResult()      │  instead of Intent. Result is      │
│  callback with requestCode.  │  returned as Promise, not callback.│
├──────────────────────────────┴───────────────────────────────────┤
│  GAP ANALYSIS                                                     │
│                                                                   │
│  • Intent → Want: Must translate action, data, extras, flags      │
│  • Callback → Promise: Different async model                      │
│  • Implicit intents: Not supported in OH (explicit only)          │
│  • requestCode: Not used in OH (Promise-based)                    │
│  • Intent extras (Bundle) → Want parameters (Record<string,any>)  │
├──────────────────────────────────────────────────────────────────┤
│  MIGRATION GUIDE                                                  │
│                                                                   │
│  1. Replace Intent with Want object                               │
│  2. Map intent.setAction() → want.abilityName                     │
│  3. Map intent.putExtra() → want.parameters                       │
│  4. Replace onActivityResult callback with Promise.then()         │
│  5. Handle implicit intent resolution manually (if used)          │
├──────────────────────────────┬───────────────────────────────────┤
│  CODE EXAMPLE (Android)      │  CODE EXAMPLE (OpenHarmony)        │
│                              │                                    │
│  ```java                     │  ```typescript                     │
│  Intent i = new Intent(      │  let want: Want = {                │
│    this, DetailActivity.class│    deviceId: '',                   │
│  );                          │    bundleName: 'com.example',      │
│  i.putExtra("id", 123);     │    abilityName: 'DetailAbility',   │
│  startActivityForResult(     │    parameters: { id: 123 }         │
│    i, REQ_DETAIL);           │  };                                │
│                              │  let result = await context        │
│  @Override                   │    .startAbilityForResult(want);   │
│  void onActivityResult(      │  // result.resultCode              │
│    int req, int res,         │  // result.want?.parameters        │
│    Intent data) {            │                                    │
│    if (req == REQ_DETAIL)    │                                    │
│      // handle result        │                                    │
│  }                           │                                    │
│  ```                         │  ```                               │
└──────────────────────────────┴───────────────────────────────────┘
```

#### Migration Estimator Page
```
┌──────────────────────────────────────────────────────────────────┐
│  APK Migration Effort Estimator                                   │
│                                                                   │
│  Paste your AndroidManifest.xml or list Android APIs used:        │
│  ┌──────────────────────────────────────────────────────────────┐ │
│  │  <manifest xmlns:android="...">                               │ │
│  │    <uses-permission android:name="INTERNET"/>                 │ │
│  │    <uses-permission android:name="CAMERA"/>                   │ │
│  │    <application>                                              │ │
│  │      <activity android:name=".MainActivity">                  │ │
│  │        <intent-filter>                                        │ │
│  │          <action android:name="android.intent.action.MAIN"/> │ │
│  │  ...                                                          │ │
│  └──────────────────────────────────────────────────────────────┘ │
│  [Analyze]                                                        │
│                                                                   │
│  ═══════════════════════════════════════════════════════════════  │
│  RESULTS:                                                         │
│                                                                   │
│  Overall Feasibility: MODERATE (Score: 6.5/10)                   │
│                                                                   │
│  ┌─ API Usage Detected ──────┬─ Score ─┬─ Effort ─┐             │
│  │ Activity (5 activities)    │   7/10  │ moderate │             │
│  │ Service (2 services)       │   5/10  │ hard     │             │
│  │ BroadcastReceiver (3)      │   6/10  │ moderate │             │
│  │ ContentProvider (1)        │   3/10  │ rewrite  │             │
│  │ Camera2 API                │   7/10  │ moderate │             │
│  │ SQLite database            │   8/10  │ easy     │             │
│  │ HTTP networking            │   9/10  │ trivial  │             │
│  │ SharedPreferences          │   9/10  │ trivial  │             │
│  │ RecyclerView               │   2/10  │ rewrite  │             │
│  │ Custom Views (3)           │   1/10  │ rewrite  │             │
│  └────────────────────────────┴─────────┴──────────┘             │
│                                                                   │
│  Blockers: Custom Views, ContentProvider, implicit intents        │
│  Estimated effort: 4-6 weeks for experienced developer            │
└──────────────────────────────────────────────────────────────────┘
```

---

## 6. Tech Stack Summary

| Component | Technology | Why |
|-----------|-----------|-----|
| **Database** | SQLite + FTS5 | Portable, no server, fast full-text search, ~50MB |
| **Backend** | Python FastAPI | Fast to build, great async, auto-generated OpenAPI docs |
| **Frontend** | React + Vite + TailwindCSS | Modern, fast, component-based |
| **Charts** | Recharts or Chart.js | Score distribution, coverage, effort breakdown |
| **Code highlighting** | Prism.js or Shiki | Syntax-highlighted code examples |
| **Search** | SQLite FTS5 + debounced autocomplete | Good enough for ~50K entries |
| **Deployment** | Single binary (uvicorn) + static files | Easy to share, no infra needed |
| **Data pipeline** | Python scripts + Claude API | Parse APIs, generate scores |
| **AI scoring** | Claude API (batch mode) | Cost-efficient bulk scoring |

---

## 7. Build Phases

### Phase A: Database + Data Import (Day 1)
1. Create SQLite database with schema
2. Import Android APIs from `current.txt` (parser exists)
3. Import OpenHarmony JS APIs from `.d.ts` files
4. Import OpenHarmony NDK APIs from `.h` headers
5. Auto-map by name/subsystem similarity

### Phase B: AI Scoring Pipeline (Day 1-2)
1. Build batch prompt templates
2. Process ~42K Android APIs through Claude API
3. Store scores, descriptions, migration guides
4. Validate sample of scores manually

### Phase C: Backend API (Day 2)
1. FastAPI app with all endpoints
2. FTS5 search integration
3. Filter/pagination logic
4. Stats aggregation endpoints

### Phase D: Frontend (Day 2-3)
1. Dashboard page with charts
2. API Browser with tree + list + filters
3. API Detail page with side-by-side comparison
4. Search with autocomplete
5. Subsystem overview pages

### Phase E: Polish (Day 3)
1. Migration Estimator page
2. Gap Analysis report page
3. Export functionality (CSV, JSON)
4. Performance optimization
5. Documentation

---

## 8. File Structure (Complete Project)

```
~/android-to-openharmony-migration/
├── 00-ANALYSIS-PLAN.md
├── 01-WEBSITE-PLAN.md          # This file
├── database/
│   ├── schema.sql
│   ├── import_android.py       # Parse current.txt → SQLite
│   ├── import_oh_js.py         # Parse .d.ts → SQLite
│   ├── import_oh_ndk.py        # Parse .h → SQLite
│   ├── auto_mapper.py          # Initial name-based mapping
│   ├── ai_scorer.py            # Claude API batch scoring
│   └── api_compat.db           # The database
├── backend/
│   ├── main.py
│   ├── database.py
│   ├── models.py
│   ├── routers/
│   └── requirements.txt
├── frontend/
│   ├── src/
│   ├── package.json
│   ├── vite.config.ts
│   └── tailwind.config.js
└── reports/                    # Generated analysis reports
    ├── domain-mappings/
    ├── gap-analysis/
    └── feasibility/
```
