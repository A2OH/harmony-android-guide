-- Android↔OpenHarmony API Compatibility Database
-- SQLite + FTS5

PRAGMA journal_mode=WAL;
PRAGMA foreign_keys=ON;

-- ============================================================
-- ANDROID APIs
-- ============================================================
CREATE TABLE IF NOT EXISTS android_packages (
    id          INTEGER PRIMARY KEY,
    name        TEXT NOT NULL UNIQUE,
    subsystem   TEXT NOT NULL DEFAULT 'Other',
    description TEXT
);

CREATE TABLE IF NOT EXISTS android_types (
    id            INTEGER PRIMARY KEY,
    package_id    INTEGER NOT NULL REFERENCES android_packages(id),
    name          TEXT NOT NULL,
    full_name     TEXT NOT NULL,
    kind          TEXT NOT NULL,  -- class, interface, enum
    is_abstract   BOOLEAN DEFAULT 0,
    is_final      BOOLEAN DEFAULT 0,
    is_static     BOOLEAN DEFAULT 0,
    is_deprecated BOOLEAN DEFAULT 0,
    extends_type  TEXT,
    implements    TEXT,
    annotations   TEXT,
    description   TEXT,
    UNIQUE(package_id, name)
);

CREATE TABLE IF NOT EXISTS android_apis (
    id            INTEGER PRIMARY KEY,
    type_id       INTEGER NOT NULL REFERENCES android_types(id),
    kind          TEXT NOT NULL,  -- method, field, constructor, enum_constant
    name          TEXT NOT NULL,
    signature     TEXT NOT NULL,
    return_type   TEXT,
    params        TEXT,          -- JSON array
    is_deprecated BOOLEAN DEFAULT 0,
    is_static     BOOLEAN DEFAULT 0,
    subsystem     TEXT,
    category      TEXT,
    compat_score  REAL,
    compat_summary TEXT,
    migration_notes TEXT,
    effort_level  TEXT           -- trivial, easy, moderate, hard, rewrite, impossible
);

-- ============================================================
-- OPENHARMONY APIs
-- ============================================================
CREATE TABLE IF NOT EXISTS oh_modules (
    id          INTEGER PRIMARY KEY,
    name        TEXT NOT NULL UNIQUE,
    sdk_type    TEXT NOT NULL DEFAULT 'js',  -- js, ndk
    subsystem   TEXT NOT NULL DEFAULT 'Other',
    file_path   TEXT,
    kit         TEXT,
    syscap      TEXT,
    since_version INTEGER,
    description TEXT
);

CREATE TABLE IF NOT EXISTS oh_types (
    id            INTEGER PRIMARY KEY,
    module_id     INTEGER NOT NULL REFERENCES oh_modules(id),
    name          TEXT NOT NULL,
    kind          TEXT NOT NULL,  -- interface, class, enum, type, struct
    is_deprecated BOOLEAN DEFAULT 0,
    syscap        TEXT,
    since_version INTEGER,
    description   TEXT,
    UNIQUE(module_id, name)
);

CREATE TABLE IF NOT EXISTS oh_apis (
    id            INTEGER PRIMARY KEY,
    type_id       INTEGER REFERENCES oh_types(id),
    module_id     INTEGER NOT NULL REFERENCES oh_modules(id),
    kind          TEXT NOT NULL,  -- method, property, function, enum_value, c_function, macro, typedef
    name          TEXT NOT NULL,
    signature     TEXT NOT NULL,
    return_type   TEXT,
    params        TEXT,
    is_deprecated BOOLEAN DEFAULT 0,
    is_readonly   BOOLEAN DEFAULT 0,
    is_optional   BOOLEAN DEFAULT 0,
    syscap        TEXT,
    since_version INTEGER,
    subsystem     TEXT,
    category      TEXT,
    description   TEXT
);

-- ============================================================
-- API MAPPINGS
-- ============================================================
CREATE TABLE IF NOT EXISTS api_mappings (
    id                  INTEGER PRIMARY KEY,
    android_api_id      INTEGER NOT NULL REFERENCES android_apis(id),
    oh_api_id           INTEGER REFERENCES oh_apis(id),
    score               REAL NOT NULL DEFAULT 0,
    confidence          REAL DEFAULT 0.5,
    mapping_type        TEXT NOT NULL DEFAULT 'none',  -- direct, near, partial, composite, none
    effort_level        TEXT NOT NULL DEFAULT 'unknown',
    android_description TEXT,
    oh_description      TEXT,
    gap_description     TEXT,
    migration_guide     TEXT,
    code_example_android TEXT,
    code_example_oh     TEXT,
    paradigm_shift      BOOLEAN DEFAULT 0,
    needs_native        BOOLEAN DEFAULT 0,
    needs_ui_rewrite    BOOLEAN DEFAULT 0,
    security_impact     BOOLEAN DEFAULT 0,
    verified            BOOLEAN DEFAULT 0,
    UNIQUE(android_api_id, oh_api_id)
);

-- ============================================================
-- SUBSYSTEMS
-- ============================================================
CREATE TABLE IF NOT EXISTS subsystems (
    id                  INTEGER PRIMARY KEY,
    name                TEXT NOT NULL UNIQUE,
    android_packages    TEXT,  -- JSON
    oh_modules          TEXT,  -- JSON
    description         TEXT,
    overall_score       REAL,
    api_count_android   INTEGER DEFAULT 0,
    api_count_oh        INTEGER DEFAULT 0,
    coverage_pct        REAL DEFAULT 0
);

-- ============================================================
-- TAGS
-- ============================================================
CREATE TABLE IF NOT EXISTS tags (
    id   INTEGER PRIMARY KEY,
    name TEXT NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS api_tags (
    api_id       INTEGER NOT NULL,
    api_platform TEXT NOT NULL,  -- android, oh
    tag_id       INTEGER NOT NULL REFERENCES tags(id),
    PRIMARY KEY (api_id, api_platform, tag_id)
);

-- ============================================================
-- INDEXES
-- ============================================================
CREATE INDEX IF NOT EXISTS idx_android_apis_type ON android_apis(type_id);
CREATE INDEX IF NOT EXISTS idx_android_apis_subsystem ON android_apis(subsystem);
CREATE INDEX IF NOT EXISTS idx_android_apis_score ON android_apis(compat_score);
CREATE INDEX IF NOT EXISTS idx_android_apis_effort ON android_apis(effort_level);
CREATE INDEX IF NOT EXISTS idx_android_apis_kind ON android_apis(kind);
CREATE INDEX IF NOT EXISTS idx_android_apis_name ON android_apis(name);
CREATE INDEX IF NOT EXISTS idx_android_types_package ON android_types(package_id);
CREATE INDEX IF NOT EXISTS idx_android_types_name ON android_types(name);

CREATE INDEX IF NOT EXISTS idx_oh_apis_type ON oh_apis(type_id);
CREATE INDEX IF NOT EXISTS idx_oh_apis_module ON oh_apis(module_id);
CREATE INDEX IF NOT EXISTS idx_oh_apis_subsystem ON oh_apis(subsystem);
CREATE INDEX IF NOT EXISTS idx_oh_apis_name ON oh_apis(name);

CREATE INDEX IF NOT EXISTS idx_mappings_android ON api_mappings(android_api_id);
CREATE INDEX IF NOT EXISTS idx_mappings_oh ON api_mappings(oh_api_id);
CREATE INDEX IF NOT EXISTS idx_mappings_score ON api_mappings(score);
CREATE INDEX IF NOT EXISTS idx_mappings_type ON api_mappings(mapping_type);

-- ============================================================
-- FTS5 FULL-TEXT SEARCH
-- ============================================================
CREATE VIRTUAL TABLE IF NOT EXISTS android_apis_fts USING fts5(
    name, signature, subsystem, description,
    content='android_apis', content_rowid='id'
);

CREATE VIRTUAL TABLE IF NOT EXISTS oh_apis_fts USING fts5(
    name, signature, subsystem, description,
    content='oh_apis', content_rowid='id'
);

-- ============================================================
-- VIEWS
-- ============================================================
CREATE VIEW IF NOT EXISTS v_android_api_full AS
SELECT
    a.id, a.name as api_name, a.signature, a.kind, a.is_deprecated,
    a.compat_score, a.effort_level, a.subsystem, a.return_type,
    t.name as type_name, t.kind as type_kind, t.is_abstract as type_is_abstract,
    t.extends_type, t.implements as type_implements,
    p.name as package_name, p.subsystem as package_subsystem
FROM android_apis a
JOIN android_types t ON a.type_id = t.id
JOIN android_packages p ON t.package_id = p.id;

CREATE VIEW IF NOT EXISTS v_oh_api_full AS
SELECT
    a.id, a.name as api_name, a.signature, a.kind, a.is_deprecated,
    a.subsystem, a.return_type, a.syscap, a.since_version,
    t.name as type_name, t.kind as type_kind,
    m.name as module_name, m.sdk_type, m.kit, m.subsystem as module_subsystem
FROM oh_apis a
LEFT JOIN oh_types t ON a.type_id = t.id
JOIN oh_modules m ON a.module_id = m.id;

CREATE VIEW IF NOT EXISTS v_mapping_full AS
SELECT
    m.*,
    aa.name as android_api_name, aa.signature as android_signature,
    aa.kind as android_kind, aa.return_type as android_return_type,
    at2.name as android_type_name, ap.name as android_package,
    oa.name as oh_api_name, oa.signature as oh_signature,
    oa.kind as oh_kind,
    ot.name as oh_type_name, om.name as oh_module
FROM api_mappings m
JOIN android_apis aa ON m.android_api_id = aa.id
JOIN android_types at2 ON aa.type_id = at2.id
JOIN android_packages ap ON at2.package_id = ap.id
LEFT JOIN oh_apis oa ON m.oh_api_id = oa.id
LEFT JOIN oh_types ot ON oa.type_id = ot.id
LEFT JOIN oh_modules om ON oa.module_id = om.id;
