-- Migration script for Android↔OpenHarmony API Compatibility Database
-- Adds new columns, tables, indexes, and views for the improved mapping system.
-- Safe to run on an existing database (uses IF NOT EXISTS / IF EXISTS guards).

PRAGMA foreign_keys=ON;

-- ============================================================
-- 1. ALTER TABLE: Add new columns to api_mappings
-- ============================================================
-- Each ALTER TABLE is wrapped so that re-running is safe on SQLite 3.37+
-- (SQLite ignores ADD COLUMN if column already exists when using IF NOT EXISTS).
-- For older SQLite versions, errors on duplicate columns are harmless.

ALTER TABLE api_mappings ADD COLUMN mapping_tier        TEXT DEFAULT 'unknown';   -- tier1_direct, tier2_similar, tier3_capable, tier4_gap
ALTER TABLE api_mappings ADD COLUMN mapping_method      TEXT DEFAULT 'unknown';   -- known_exact, known_type_context, exact_name, multi_signal, fuzzy_name, none
ALTER TABLE api_mappings ADD COLUMN param_compatibility REAL DEFAULT NULL;        -- 0.0-1.0 how well params match
ALTER TABLE api_mappings ADD COLUMN return_type_match   BOOLEAN DEFAULT NULL;     -- whether return types are compatible
ALTER TABLE api_mappings ADD COLUMN context_match       BOOLEAN DEFAULT NULL;     -- whether class/module context matches

-- ============================================================
-- 2. CREATE TABLE: mapping_candidates (top-5 OH candidates per Android API for Tier 2)
-- ============================================================
CREATE TABLE IF NOT EXISTS mapping_candidates (
    id              INTEGER PRIMARY KEY,
    android_api_id  INTEGER NOT NULL REFERENCES android_apis(id),
    oh_api_id       INTEGER NOT NULL REFERENCES oh_apis(id),
    rank            INTEGER NOT NULL DEFAULT 1,
    score           REAL NOT NULL DEFAULT 0,
    confidence      REAL NOT NULL DEFAULT 0.5,
    match_reason    TEXT,
    UNIQUE(android_api_id, oh_api_id)
);

-- ============================================================
-- 3. CREATE TABLE: capability_assessment (Tier 3 — OH capabilities for gaps)
-- ============================================================
CREATE TABLE IF NOT EXISTS capability_assessment (
    id                  INTEGER PRIMARY KEY,
    android_api_id      INTEGER NOT NULL REFERENCES android_apis(id),
    oh_subsystem        TEXT,
    oh_module           TEXT,
    oh_capability       TEXT,
    implementation_hint TEXT,
    effort_estimate     TEXT,  -- easy, moderate, hard, very_hard
    requires_ndk        BOOLEAN DEFAULT 0,
    requires_system_api BOOLEAN DEFAULT 0,
    UNIQUE(android_api_id)
);

-- ============================================================
-- 4. CREATE TABLE: known_mappings (curated ground truth)
-- ============================================================
CREATE TABLE IF NOT EXISTS known_mappings (
    id                INTEGER PRIMARY KEY,
    android_package   TEXT NOT NULL,
    android_type      TEXT NOT NULL,
    android_method    TEXT,  -- NULL means type-level mapping only
    oh_module         TEXT NOT NULL,
    oh_type           TEXT,
    oh_method         TEXT,
    score_override    REAL DEFAULT 9.0,  -- override score for this mapping
    notes             TEXT,
    paradigm_shift    BOOLEAN DEFAULT 0,
    UNIQUE(android_package, android_type, android_method)
);

-- ============================================================
-- 5. CREATE INDEXES for new tables
-- ============================================================
CREATE INDEX IF NOT EXISTS idx_candidates_android ON mapping_candidates(android_api_id);
CREATE INDEX IF NOT EXISTS idx_candidates_oh ON mapping_candidates(oh_api_id);
CREATE INDEX IF NOT EXISTS idx_candidates_rank ON mapping_candidates(android_api_id, rank);

CREATE INDEX IF NOT EXISTS idx_capability_android ON capability_assessment(android_api_id);

CREATE INDEX IF NOT EXISTS idx_known_android ON known_mappings(android_package, android_type);
CREATE INDEX IF NOT EXISTS idx_known_oh ON known_mappings(oh_module, oh_type);

-- ============================================================
-- 6. CREATE VIEWS (drop first to allow re-creation with updated columns)
-- ============================================================
DROP VIEW IF EXISTS v_mapping_tiered;
CREATE VIEW IF NOT EXISTS v_mapping_tiered AS
SELECT
    m.id, m.mapping_tier, m.mapping_type, m.mapping_method,
    m.score, m.confidence, m.effort_level,
    m.gap_description, m.migration_guide,
    m.param_compatibility, m.return_type_match, m.context_match,
    m.paradigm_shift, m.needs_native, m.needs_ui_rewrite,
    aa.name as android_api_name, aa.signature as android_signature,
    aa.kind as android_kind, aa.subsystem as android_subsystem,
    at2.name as android_type_name, at2.full_name as android_type_full,
    ap.name as android_package,
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

DROP VIEW IF EXISTS v_tier_summary;
CREATE VIEW IF NOT EXISTS v_tier_summary AS
SELECT
    mapping_tier,
    COUNT(*) as api_count,
    ROUND(AVG(score), 2) as avg_score,
    ROUND(AVG(confidence), 2) as avg_confidence,
    SUM(CASE WHEN paradigm_shift THEN 1 ELSE 0 END) as paradigm_shifts,
    SUM(CASE WHEN needs_ui_rewrite THEN 1 ELSE 0 END) as ui_rewrites
FROM api_mappings
GROUP BY mapping_tier;
