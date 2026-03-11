/**
 * Static client: runs SQL queries client-side via sql.js (for GitHub Pages).
 * Mirrors the same interface as client.ts fetch functions.
 */
import { getDb, queryAll, queryOne } from './sqldb';
import type {
  StatsOverview, SubsystemCoverage, EffortItem, ScoreDistItem,
  PaginatedResponse, AndroidApi, AndroidPackage, MappingItem,
  SearchResponse, SearchResultItem, SubsystemDetail,
} from './client';

// --- Stats ---
export async function getStatsOverview(): Promise<StatsOverview> {
  const db = await getDb();
  const totalAndroid = queryOne(db, 'SELECT COUNT(*) as c FROM android_apis')!.c;
  const totalTypes = queryOne(db, 'SELECT COUNT(*) as c FROM android_types')!.c;
  const totalPkgs = queryOne(db, 'SELECT COUNT(*) as c FROM android_packages')!.c;
  const totalOh = queryOne(db, 'SELECT COUNT(*) as c FROM oh_apis')!.c;
  const totalOhTypes = queryOne(db, 'SELECT COUNT(*) as c FROM oh_types')!.c;
  const totalOhMods = queryOne(db, 'SELECT COUNT(*) as c FROM oh_modules')!.c;
  const totalMappings = queryOne(db, 'SELECT COUNT(*) as c FROM api_mappings')!.c;
  const mapped = queryOne(db, 'SELECT COUNT(DISTINCT android_api_id) as c FROM api_mappings WHERE oh_api_id IS NOT NULL')!.c;
  const avgScore = queryOne(db, 'SELECT AVG(score) as a FROM api_mappings')!.a || 0;

  const scoreDist: Record<string, number> = {};
  queryAll(db, 'SELECT ROUND(score) as s, COUNT(*) as c FROM api_mappings GROUP BY ROUND(score) ORDER BY s').forEach((r: any) => {
    scoreDist[String(r.s)] = r.c;
  });

  const effortDist: Record<string, number> = {};
  queryAll(db, 'SELECT effort_level, COUNT(*) as c FROM api_mappings GROUP BY effort_level').forEach((r: any) => {
    effortDist[r.effort_level] = r.c;
  });

  return {
    total_android_apis: totalAndroid,
    total_android_types: totalTypes,
    total_android_packages: totalPkgs,
    total_oh_apis: totalOh,
    total_oh_types: totalOhTypes,
    total_oh_modules: totalOhMods,
    total_mappings: totalMappings,
    mapped_count: mapped,
    unmapped_count: totalAndroid - mapped,
    avg_score: avgScore,
    score_distribution: scoreDist,
    effort_distribution: effortDist,
  };
}

export async function getCoverageBySubsystem(): Promise<SubsystemCoverage[]> {
  const db = await getDb();
  return queryAll(db, `
    SELECT p.subsystem,
      COUNT(a.id) as total_apis,
      SUM(CASE WHEN m.score >= 7 THEN 1 ELSE 0 END) as well_mapped,
      SUM(CASE WHEN m.score >= 4 AND m.score < 7 THEN 1 ELSE 0 END) as partially_mapped,
      SUM(CASE WHEN m.score < 4 OR m.score IS NULL THEN 1 ELSE 0 END) as gaps,
      ROUND(AVG(m.score), 1) as avg_score,
      ROUND(100.0 * SUM(CASE WHEN m.score >= 5 THEN 1 ELSE 0 END) / COUNT(a.id), 1) as coverage_pct
    FROM android_apis a
    JOIN android_types t ON a.type_id = t.id
    JOIN android_packages p ON t.package_id = p.id
    LEFT JOIN api_mappings m ON m.android_api_id = a.id
    GROUP BY p.subsystem
    ORDER BY total_apis DESC
  `);
}

export async function getEffortBreakdown(): Promise<EffortItem[]> {
  const db = await getDb();
  const total = queryOne(db, 'SELECT COUNT(*) as c FROM api_mappings')!.c;
  return queryAll(db, `
    SELECT effort_level, COUNT(*) as count,
      ROUND(100.0 * COUNT(*) / ${total}, 1) as percentage
    FROM api_mappings GROUP BY effort_level ORDER BY count DESC
  `);
}

export async function getScoreDistribution(): Promise<ScoreDistItem[]> {
  const db = await getDb();
  const total = queryOne(db, 'SELECT COUNT(*) as c FROM api_mappings')!.c;
  const buckets = [
    { bucket: 'Direct (9-10)', min: 9, max: 10 },
    { bucket: 'Good (7-8)', min: 7, max: 8.99 },
    { bucket: 'Partial (5-6)', min: 5, max: 6.99 },
    { bucket: 'Hard (3-4)', min: 3, max: 4.99 },
    { bucket: 'Gap (1-2)', min: 1, max: 2.99 },
  ];
  return buckets.map(b => {
    const row = queryOne(db, 'SELECT COUNT(*) as c FROM api_mappings WHERE score >= ? AND score <= ?', [b.min, b.max]);
    return { bucket: b.bucket, count: row!.c, percentage: Math.round(1000 * row!.c / total) / 10 };
  });
}

// --- Android ---
export async function getAndroidPackages(sub?: string): Promise<AndroidPackage[]> {
  const db = await getDb();
  const where = sub ? 'WHERE p.subsystem = ?' : '';
  const params = sub ? [sub] : [];
  return queryAll(db, `
    SELECT p.id, p.name, p.subsystem,
      COUNT(DISTINCT t.id) as type_count,
      COUNT(a.id) as api_count
    FROM android_packages p
    LEFT JOIN android_types t ON t.package_id = p.id
    LEFT JOIN android_apis a ON a.type_id = t.id
    ${where}
    GROUP BY p.id ORDER BY p.name
  `, params);
}

export async function getAndroidApis(paramsStr: string): Promise<PaginatedResponse<AndroidApi>> {
  const db = await getDb();
  const params = new URLSearchParams(paramsStr);
  const page = Number(params.get('page') || '1');
  const pageSize = Number(params.get('page_size') || '50');
  const offset = (page - 1) * pageSize;

  let where = 'WHERE 1=1';
  const binds: any[] = [];

  if (params.get('package')) { where += ' AND p.name = ?'; binds.push(params.get('package')); }
  if (params.get('search')) { where += ' AND a.name LIKE ?'; binds.push(`%${params.get('search')}%`); }
  if (params.get('kind')) { where += ' AND a.kind = ?'; binds.push(params.get('kind')); }
  if (params.get('score_min')) { where += ' AND m.score >= ?'; binds.push(Number(params.get('score_min'))); }
  if (params.get('score_max')) { where += ' AND m.score <= ?'; binds.push(Number(params.get('score_max'))); }
  if (params.get('effort')) { where += ' AND m.effort_level = ?'; binds.push(params.get('effort')); }

  const countRow = queryOne(db, `
    SELECT COUNT(*) as c FROM android_apis a
    JOIN android_types t ON a.type_id = t.id
    JOIN android_packages p ON t.package_id = p.id
    LEFT JOIN api_mappings m ON m.android_api_id = a.id
    ${where}
  `, binds);
  const total = countRow!.c;

  const items = queryAll(db, `
    SELECT a.id, a.type_id, t.name as type_name, p.name as package_name,
      a.kind, a.name, a.signature, a.return_type, a.is_deprecated,
      p.subsystem, m.score as compat_score, m.effort_level
    FROM android_apis a
    JOIN android_types t ON a.type_id = t.id
    JOIN android_packages p ON t.package_id = p.id
    LEFT JOIN api_mappings m ON m.android_api_id = a.id
    ${where}
    ORDER BY a.name
    LIMIT ? OFFSET ?
  `, [...binds, pageSize, offset]);

  return { items, total, page, page_size: pageSize, total_pages: Math.ceil(total / pageSize) };
}

export async function getAndroidApi(id: number): Promise<any> {
  const db = await getDb();
  const api = queryOne(db, `
    SELECT a.*, t.name as type_name, p.name as package_name, p.subsystem,
      m.score as compat_score, m.effort_level
    FROM android_apis a
    JOIN android_types t ON a.type_id = t.id
    JOIN android_packages p ON t.package_id = p.id
    LEFT JOIN api_mappings m ON m.android_api_id = a.id
    WHERE a.id = ?
  `, [id]);

  if (api) {
    api.mappings = queryAll(db, `
      SELECT m.*, o.name as oh_api_name, o.signature as oh_signature, o.kind as oh_kind,
        ot.name as oh_type_name, om.name as oh_module
      FROM api_mappings m
      LEFT JOIN oh_apis o ON m.oh_api_id = o.id
      LEFT JOIN oh_types ot ON o.type_id = ot.id
      LEFT JOIN oh_modules om ON ot.module_id = om.id
      WHERE m.android_api_id = ?
    `, [id]);
  }
  return api;
}

// --- Mappings ---
export async function getMappings(paramsStr: string): Promise<PaginatedResponse<MappingItem>> {
  const db = await getDb();
  const params = new URLSearchParams(paramsStr);
  const page = Number(params.get('page') || '1');
  const pageSize = Number(params.get('page_size') || '50');
  const offset = (page - 1) * pageSize;

  let where = 'WHERE 1=1';
  const binds: any[] = [];

  if (params.get('search')) { where += ' AND (a.name LIKE ? OR o.name LIKE ?)'; const s = `%${params.get('search')}%`; binds.push(s, s); }
  if (params.get('score_min')) { where += ' AND m.score >= ?'; binds.push(Number(params.get('score_min'))); }
  if (params.get('score_max')) { where += ' AND m.score <= ?'; binds.push(Number(params.get('score_max'))); }
  if (params.get('effort')) { where += ' AND m.effort_level = ?'; binds.push(params.get('effort')); }
  if (params.get('mapping_type')) { where += ' AND m.mapping_type = ?'; binds.push(params.get('mapping_type')); }

  const countRow = queryOne(db, `
    SELECT COUNT(*) as c FROM api_mappings m
    JOIN android_apis a ON m.android_api_id = a.id
    JOIN android_types t ON a.type_id = t.id
    JOIN android_packages p ON t.package_id = p.id
    LEFT JOIN oh_apis o ON m.oh_api_id = o.id
    LEFT JOIN oh_types ot ON o.type_id = ot.id
    LEFT JOIN oh_modules om ON ot.module_id = om.id
    ${where}
  `, binds);
  const total = countRow!.c;

  const items = queryAll(db, `
    SELECT m.id, m.score, m.mapping_type, m.effort_level, m.paradigm_shift, m.needs_ui_rewrite,
      m.android_api_id, a.name as android_api_name, a.signature as android_signature,
      a.kind as android_kind, p.subsystem, t.name as android_type_name, p.name as android_package,
      m.oh_api_id, o.name as oh_api_name, o.signature as oh_signature, o.kind as oh_kind,
      ot.name as oh_type_name, om.name as oh_module
    FROM api_mappings m
    JOIN android_apis a ON m.android_api_id = a.id
    JOIN android_types t ON a.type_id = t.id
    JOIN android_packages p ON t.package_id = p.id
    LEFT JOIN oh_apis o ON m.oh_api_id = o.id
    LEFT JOIN oh_types ot ON o.type_id = ot.id
    LEFT JOIN oh_modules om ON ot.module_id = om.id
    ${where}
    ORDER BY m.score DESC
    LIMIT ? OFFSET ?
  `, [...binds, pageSize, offset]);

  return { items, total, page, page_size: pageSize, total_pages: Math.ceil(total / pageSize) };
}

// --- Search ---
export async function searchApis(paramsStr: string): Promise<SearchResponse> {
  const db = await getDb();
  const params = new URLSearchParams(paramsStr);
  const q = params.get('q') || '';
  const limit = Number(params.get('limit') || '100');

  const results: SearchResultItem[] = [];

  // Search Android APIs
  const androidResults = queryAll(db, `
    SELECT a.id, a.name, a.signature, a.kind, t.name as type_name,
      p.name as package_or_module, p.subsystem, m.score
    FROM android_apis a
    JOIN android_types t ON a.type_id = t.id
    JOIN android_packages p ON t.package_id = p.id
    LEFT JOIN api_mappings m ON m.android_api_id = a.id
    WHERE a.name LIKE ?
    LIMIT ?
  `, [`%${q}%`, limit]);
  androidResults.forEach((r: any) => results.push({ ...r, platform: 'android' }));

  // Search OH APIs
  const ohResults = queryAll(db, `
    SELECT o.id, o.name, o.signature, o.kind, t.name as type_name,
      m.name as package_or_module
    FROM oh_apis o
    JOIN oh_types t ON o.type_id = t.id
    JOIN oh_modules m ON t.module_id = m.id
    WHERE o.name LIKE ?
    LIMIT ?
  `, [`%${q}%`, limit]);
  ohResults.forEach((r: any) => results.push({ ...r, platform: 'openharmony' }));

  return { results: results.slice(0, limit), query: q, total: results.length };
}

// --- Subsystems ---
export async function getSubsystems(): Promise<any[]> {
  const db = await getDb();
  return queryAll(db, `
    SELECT DISTINCT p.subsystem as name, COUNT(a.id) as api_count
    FROM android_packages p
    JOIN android_types t ON t.package_id = p.id
    JOIN android_apis a ON a.type_id = t.id
    GROUP BY p.subsystem ORDER BY api_count DESC
  `);
}

export async function getSubsystem(name: string): Promise<SubsystemDetail> {
  const db = await getDb();

  const apiCount = queryOne(db, `
    SELECT COUNT(a.id) as c FROM android_apis a
    JOIN android_types t ON a.type_id = t.id
    JOIN android_packages p ON t.package_id = p.id
    WHERE p.subsystem = ?
  `, [name])!.c;

  const avgRow = queryOne(db, `
    SELECT AVG(m.score) as avg_score FROM api_mappings m
    JOIN android_apis a ON m.android_api_id = a.id
    JOIN android_types t ON a.type_id = t.id
    JOIN android_packages p ON t.package_id = p.id
    WHERE p.subsystem = ?
  `, [name]);

  const overall = avgRow?.avg_score || 0;
  const coverage = queryOne(db, `
    SELECT ROUND(100.0 * SUM(CASE WHEN m.score >= 5 THEN 1 ELSE 0 END) / COUNT(*), 1) as pct
    FROM android_apis a
    JOIN android_types t ON a.type_id = t.id
    JOIN android_packages p ON t.package_id = p.id
    LEFT JOIN api_mappings m ON m.android_api_id = a.id
    WHERE p.subsystem = ?
  `, [name])?.pct || 0;

  const scoreDist = queryAll(db, `
    SELECT CASE
      WHEN m.score >= 9 THEN 'Direct (9-10)'
      WHEN m.score >= 7 THEN 'Good (7-8)'
      WHEN m.score >= 5 THEN 'Partial (5-6)'
      WHEN m.score >= 3 THEN 'Hard (3-4)'
      ELSE 'Gap (1-2)' END as bucket,
      COUNT(*) as count
    FROM api_mappings m
    JOIN android_apis a ON m.android_api_id = a.id
    JOIN android_types t ON a.type_id = t.id
    JOIN android_packages p ON t.package_id = p.id
    WHERE p.subsystem = ?
    GROUP BY bucket
  `, [name]);

  const effortDist = queryAll(db, `
    SELECT effort_level, COUNT(*) as count FROM api_mappings m
    JOIN android_apis a ON m.android_api_id = a.id
    JOIN android_types t ON a.type_id = t.id
    JOIN android_packages p ON t.package_id = p.id
    WHERE p.subsystem = ?
    GROUP BY effort_level ORDER BY count DESC
  `, [name]);

  const types = queryAll(db, `
    SELECT t.id, t.name, t.kind, p.name as package_name,
      COUNT(a.id) as api_count, ROUND(AVG(m.score), 1) as avg_score
    FROM android_types t
    JOIN android_packages p ON t.package_id = p.id
    JOIN android_apis a ON a.type_id = t.id
    LEFT JOIN api_mappings m ON m.android_api_id = a.id
    WHERE p.subsystem = ?
    GROUP BY t.id ORDER BY api_count DESC LIMIT 50
  `, [name]);

  const topGaps = queryAll(db, `
    SELECT a.id, a.name, a.signature, m.score as compat_score,
      t.name as type_name, p.name as package_name
    FROM android_apis a
    JOIN android_types t ON a.type_id = t.id
    JOIN android_packages p ON t.package_id = p.id
    LEFT JOIN api_mappings m ON m.android_api_id = a.id
    WHERE p.subsystem = ?
    ORDER BY COALESCE(m.score, 0) ASC LIMIT 20
  `, [name]);

  return {
    id: 0, name, api_count_android: apiCount,
    overall_score: overall, coverage_pct: coverage,
    score_distribution: scoreDist, effort_distribution: effortDist,
    types, top_gaps: topGaps,
  };
}

export async function getGaps(): Promise<any[]> {
  const db = await getDb();
  return queryAll(db, `
    SELECT p.subsystem, COUNT(*) as gap_count
    FROM android_apis a
    JOIN android_types t ON a.type_id = t.id
    JOIN android_packages p ON t.package_id = p.id
    LEFT JOIN api_mappings m ON m.android_api_id = a.id
    WHERE m.score IS NULL OR m.score < 3
    GROUP BY p.subsystem ORDER BY gap_count DESC
  `);
}

export function autocomplete(q: string): Promise<any[]> {
  return searchApis(`q=${encodeURIComponent(q)}&limit=10`).then(r => r.results);
}
