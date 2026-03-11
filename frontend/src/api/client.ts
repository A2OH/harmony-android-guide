import * as staticClient from './static-client';

const BASE = '/api';

// Auto-detect: if backend is available use it, otherwise use client-side sql.js
let _useStatic: boolean | null = null;
async function isStaticMode(): Promise<boolean> {
  if (_useStatic !== null) return _useStatic;
  // Skip health check on known static hosts (GitHub Pages, custom domain)
  const host = window.location.hostname;
  if (host.endsWith('.github.io') || host === 'harmony.moxin.app') {
    _useStatic = true;
    return true;
  }
  try {
    const controller = new AbortController();
    const timer = setTimeout(() => controller.abort(), 1500);
    const res = await fetch(`${BASE}/health`, { signal: controller.signal });
    clearTimeout(timer);
    _useStatic = !res.ok;
  } catch {
    _useStatic = true;
  }
  return _useStatic;
}

async function fetchJson<T>(url: string): Promise<T> {
  const res = await fetch(`${BASE}${url}`);
  if (!res.ok) throw new Error(`API error: ${res.status}`);
  return res.json();
}

export interface StatsOverview {
  total_android_apis: number;
  total_android_entries?: number;
  total_android_constants?: number;
  total_android_types: number;
  total_android_packages: number;
  total_oh_apis: number;
  total_oh_entries?: number;
  total_oh_constants?: number;
  total_oh_types: number;
  total_oh_modules: number;
  total_mappings: number;
  mapped_count: number;
  unmapped_count: number;
  avg_score: number;
  score_distribution: Record<string, number>;
  effort_distribution: Record<string, number>;
}

export interface SubsystemCoverage {
  subsystem: string;
  total_apis: number;
  well_mapped: number;
  partially_mapped: number;
  gaps: number;
  avg_score: number;
  coverage_pct: number;
}

export interface EffortItem {
  effort_level: string;
  count: number;
  percentage: number;
}

export interface ScoreDistItem {
  bucket: string;
  count: number;
  percentage: number;
}

export interface PaginatedResponse<T> {
  items: T[];
  total: number;
  page: number;
  page_size: number;
  total_pages: number;
}

export interface AndroidApi {
  id: number;
  type_id: number;
  type_name?: string;
  package_name?: string;
  kind: string;
  name: string;
  signature: string;
  return_type?: string;
  is_deprecated: boolean;
  subsystem?: string;
  compat_score?: number;
  effort_level?: string;
}

export interface MappingItem {
  id: number;
  score: number;
  mapping_type: string;
  effort_level: string;
  paradigm_shift: number;
  needs_ui_rewrite: number;
  android_api_id: number;
  android_api_name: string;
  android_signature: string;
  android_kind: string;
  subsystem: string;
  android_type_name: string;
  android_package: string;
  oh_api_id?: number;
  oh_api_name?: string;
  oh_signature?: string;
  oh_kind?: string;
  oh_type_name?: string;
  oh_module?: string;
}

export interface SearchResultItem {
  platform: string;
  id: number;
  name: string;
  signature: string;
  kind: string;
  type_name?: string;
  package_or_module?: string;
  subsystem?: string;
  score?: number;
}

export interface SearchResponse {
  results: SearchResultItem[];
  query: string;
  total: number;
}

export interface AndroidPackage {
  id: number;
  name: string;
  subsystem: string;
  type_count: number;
  api_count: number;
}

export interface SubsystemDetail {
  id: number;
  name: string;
  api_count_android: number;
  overall_score: number;
  coverage_pct: number;
  score_distribution: { bucket: string; count: number }[];
  effort_distribution: { effort_level: string; count: number }[];
  types: { id: number; name: string; kind: string; package_name: string; api_count: number; avg_score: number }[];
  top_gaps: { id: number; name: string; signature: string; compat_score: number; type_name: string; package_name: string }[];
}

export interface MappingDetail {
  id: number;
  android_api_id: number;
  oh_api_id?: number;
  score: number;
  mapping_type: string;
  effort_level: string;
  android_description?: string;
  oh_description?: string;
  gap_description?: string;
  migration_guide?: string;
  code_example_android?: string;
  code_example_oh?: string;
  paradigm_shift: number;
  needs_ui_rewrite: number;
  android_api_name: string;
  android_signature: string;
  android_kind: string;
  android_return_type?: string;
  android_type_name: string;
  android_type_kind?: string;
  android_package: string;
  android_subsystem: string;
  oh_api_name?: string;
  oh_signature?: string;
  oh_kind?: string;
  oh_return_type?: string;
  oh_type_name?: string;
  oh_type_kind?: string;
  oh_module?: string;
  oh_sdk_type?: string;
  oh_kit?: string;
  oh_syscap?: string;
}

// --- Auto-switching API functions ---

// Stats
export const getStatsOverview = async () =>
  (await isStaticMode()) ? staticClient.getStatsOverview() : fetchJson<StatsOverview>('/stats/overview');
export const getCoverageBySubsystem = async () =>
  (await isStaticMode()) ? staticClient.getCoverageBySubsystem() : fetchJson<SubsystemCoverage[]>('/stats/coverage-by-subsystem');
export const getEffortBreakdown = async () =>
  (await isStaticMode()) ? staticClient.getEffortBreakdown() : fetchJson<EffortItem[]>('/stats/effort-breakdown');
export const getScoreDistribution = async () =>
  (await isStaticMode()) ? staticClient.getScoreDistribution() : fetchJson<ScoreDistItem[]>('/stats/score-distribution');
export const getGaps = async () =>
  (await isStaticMode()) ? staticClient.getGaps() : fetchJson<any[]>('/stats/gaps');

// Android
export const getAndroidPackages = async (sub?: string) =>
  (await isStaticMode()) ? staticClient.getAndroidPackages(sub) : fetchJson<AndroidPackage[]>(`/android/packages${sub ? `?subsystem=${encodeURIComponent(sub)}` : ''}`);
export const getAndroidApis = async (params: string) =>
  (await isStaticMode()) ? staticClient.getAndroidApis(params) : fetchJson<PaginatedResponse<AndroidApi>>(`/android/apis?${params}`);
export const getAndroidApi = async (id: number) =>
  (await isStaticMode()) ? staticClient.getAndroidApi(id) : fetchJson<any>(`/android/apis/${id}`);

// Mappings
export const getMappings = async (params: string) =>
  (await isStaticMode()) ? staticClient.getMappings(params) : fetchJson<PaginatedResponse<MappingItem>>(`/mappings?${params}`);
export const getMapping = async (id: number) =>
  (await isStaticMode()) ? fetchJson<MappingDetail>(`/mappings/${id}`) : fetchJson<MappingDetail>(`/mappings/${id}`);

// Search
export const searchApis = async (params: string) =>
  (await isStaticMode()) ? staticClient.searchApis(params) : fetchJson<SearchResponse>(`/search?${params}`);
export const autocomplete = async (q: string) =>
  (await isStaticMode()) ? staticClient.autocomplete(q) : fetchJson<any[]>(`/search/autocomplete?q=${encodeURIComponent(q)}`);

// Subsystems
export const getSubsystems = async () =>
  (await isStaticMode()) ? staticClient.getSubsystems() : fetchJson<any[]>('/subsystems');
export const getSubsystem = async (name: string) =>
  (await isStaticMode()) ? staticClient.getSubsystem(name) : fetchJson<SubsystemDetail>(`/subsystems/${encodeURIComponent(name)}`);
