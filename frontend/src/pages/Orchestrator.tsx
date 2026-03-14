import { useEffect, useState, useCallback, useRef } from 'react';

interface Issue {
  number: number;
  title: string;
  state: string;
  labels: { name: string; color: string }[];
  created_at: string;
  updated_at: string;
  assignee?: { login: string };
  body?: string;
  html_url?: string;
}

interface Stats {
  todo: number;
  inProgress: number;
  done: number;
  failed: number;
  total: number;
  tierA: number;
  tierB: number;
  tierC: number;
  tierD: number;
}

const REPO = 'A2OH/harmony-android-guide';
const API_BASE = 'https://api.github.com/repos/' + REPO;

// ─── Tier class definitions (loaded from tier-classes.json) ─────────────────
interface TierClassEntry {
  fqcn: string;
  short: string;
  package: string;
  apis: number;
  skill: string;
}

interface TierData {
  generated: string;
  tiers: Record<string, TierClassEntry[]>;
  counts: { A_classes: number; B_classes: number; C_classes: number; D_classes: number; total_apis: number };
}

const TIER_LABELS: Record<string, { label: string; color: string }> = {
  A: { label: 'Tier A — Pure Java', color: 'text-blue-400' },
  B: { label: 'Tier B — I/O with Java Fallback', color: 'text-yellow-400' },
  C: { label: 'Tier C — System Services (OHBridge)', color: 'text-orange-400' },
  D: { label: 'Tier D — UI Components (ArkUI)', color: 'text-red-400' },
};

// ─── Skill lookup helper ────────────────────────────────────────────────────

function lookupSkill(pkg: string, tierData: TierData | null): string {
  if (!tierData) return 'A2OH-JAVA-TO-ARKTS';
  for (const tier of Object.values(tierData.tiers)) {
    for (const c of tier) {
      if (c.package === pkg && c.skill) return c.skill;
    }
  }
  return 'A2OH-JAVA-TO-ARKTS';
}

// ─── API helpers ─────────────────────────────────────────────────────────────

async function fetchAllShimIssues(token?: string): Promise<Issue[]> {
  let page = 1;
  const maxPages = 20;
  let all: Issue[] = [];
  const headers: Record<string, string> = {
    Accept: 'application/vnd.github.v3+json',
  };
  if (token) {
    headers.Authorization = `token ${token}`;
  }
  while (page <= maxPages) {
    const params = new URLSearchParams({
      per_page: '100',
      state: 'all',
      labels: 'shim',
      page: String(page),
    });
    const res = await fetch(`${API_BASE}/issues?${params}`, { headers });
    if (!res.ok) break;
    const batch: Issue[] = await res.json();
    if (batch.length === 0) break;
    all = all.concat(batch);
    if (batch.length < 100) break;
    page++;
  }
  return all;
}

async function ghApiCall(
  endpoint: string,
  method: string,
  token: string,
  body?: Record<string, unknown>,
) {
  const res = await fetch(`${API_BASE}${endpoint}`, {
    method,
    headers: {
      Authorization: `token ${token}`,
      Accept: 'application/vnd.github.v3+json',
      'Content-Type': 'application/json',
    },
    body: body ? JSON.stringify(body) : undefined,
  });
  if (!res.ok) {
    const text = await res.text();
    throw new Error(`GitHub API ${res.status}: ${text}`);
  }
  return res.json();
}

function hasLabel(issue: Issue, name: string): boolean {
  return issue.labels.some(l => l.name === name);
}

function getStatus(issue: Issue): string {
  if (hasLabel(issue, 'done') || issue.state === 'closed') return 'done';
  if (hasLabel(issue, 'failed')) return 'failed';
  if (hasLabel(issue, 'in-progress')) return 'in-progress';
  return 'todo';
}

function getTier(issue: Issue): string {
  if (hasLabel(issue, 'tier-a')) return 'A';
  if (hasLabel(issue, 'tier-b')) return 'B';
  if (hasLabel(issue, 'tier-c')) return 'C';
  if (hasLabel(issue, 'tier-d')) return 'D';
  return '?';
}

function timeSince(dateStr: string): string {
  const seconds = Math.floor((Date.now() - new Date(dateStr).getTime()) / 1000);
  if (seconds < 60) return `${seconds}s ago`;
  if (seconds < 3600) return `${Math.floor(seconds / 60)}m ago`;
  if (seconds < 86400) return `${Math.floor(seconds / 3600)}h ago`;
  return `${Math.floor(seconds / 86400)}d ago`;
}

function extractClassName(title: string): string {
  return title.replace('[SHIM] Implement ', '');
}

// Convert fqcn to shim file path, handling inner classes
// e.g. "android.bluetooth.BluetoothClass.Device" → "shim/java/android/bluetooth/BluetoothClass.java"
// Inner classes (uppercase after class-level dot) live in the outer class file
function fqcnToPath(fqcn: string): string {
  const parts = fqcn.split('.');
  // Find where the class name starts: first part that begins with uppercase
  let classIdx = parts.findIndex(p => p[0] && p[0] === p[0].toUpperCase() && p[0] !== p[0].toLowerCase());
  if (classIdx < 0) classIdx = parts.length - 1;
  // Package is everything before, outer class is the first uppercase part
  const pkg = parts.slice(0, classIdx).join('/');
  const outerClass = parts[classIdx];
  return `shim/java/${pkg}/${outerClass}.java`;
}

const STATUS_COLORS: Record<string, string> = {
  'todo': 'bg-gray-700 text-gray-300',
  'in-progress': 'bg-yellow-900/50 text-yellow-300 border border-yellow-700/50',
  'done': 'bg-green-900/50 text-green-300 border border-green-700/50',
  'failed': 'bg-red-900/50 text-red-300 border border-red-700/50',
};

const STATUS_DOT: Record<string, string> = {
  'todo': 'bg-gray-500',
  'in-progress': 'bg-yellow-400 animate-pulse',
  'done': 'bg-green-400',
  'failed': 'bg-red-400',
};

const TIER_COLORS: Record<string, string> = {
  'A': 'bg-blue-900/50 text-blue-300',
  'B': 'bg-yellow-900/50 text-yellow-300',
  'C': 'bg-orange-900/50 text-orange-300',
  'D': 'bg-red-900/50 text-red-300',
  '?': 'bg-gray-800 text-gray-400',
};

// ─── Main component ─────────────────────────────────────────────────────────

export default function Orchestrator() {
  const [issues, setIssues] = useState<Issue[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [filter, setFilter] = useState<string>('all');
  const [tierFilter, setTierFilter] = useState<string>('all');
  const [lastRefresh, setLastRefresh] = useState<Date>(new Date());
  const [autoRefresh, setAutoRefresh] = useState(true);

  // Issue management
  const [token, setToken] = useState<string>(() => localStorage.getItem('gh_token') || '');
  const [showTokenInput, setShowTokenInput] = useState(false);
  const [actionLoading, setActionLoading] = useState<number | null>(null);
  const [actionMsg, setActionMsg] = useState<string | null>(null);

  // Tier data from JSON
  const [tierData, setTierData] = useState<TierData | null>(null);
  const [tierDataLoading, setTierDataLoading] = useState(false);
  const tierDataLoaded = useRef(false);

  // Batch creation
  const [showCreatePanel, setShowCreatePanel] = useState(false);
  const [createTier, setCreateTier] = useState<string>('A');
  const [selectedClasses, setSelectedClasses] = useState<Set<string>>(new Set());
  const [creating, setCreating] = useState(false);
  const [createLog, setCreateLog] = useState<string[]>([]);
  const [tierSearch, setTierSearch] = useState('');

  // Auto-fill: auto-create issues when todo queue runs low (persisted)
  const [autoFill, setAutoFill] = useState(() => localStorage.getItem('af_on') === '1');
  const [autoFillTier, setAutoFillTier] = useState(() => localStorage.getItem('af_tier') || 'A');
  const [autoFillThreshold, setAutoFillThreshold] = useState(() => Number(localStorage.getItem('af_thresh')) || 10);
  const [autoFillBatch, setAutoFillBatch] = useState(() => Number(localStorage.getItem('af_batch')) || 20);
  const [autoFillLog, setAutoFillLog] = useState<string[]>([]);
  const autoFillRunning = useRef(false);

  // Persist auto-fill settings
  useEffect(() => { localStorage.setItem('af_on', autoFill ? '1' : '0'); }, [autoFill]);
  useEffect(() => { localStorage.setItem('af_tier', autoFillTier); }, [autoFillTier]);
  useEffect(() => { localStorage.setItem('af_thresh', String(autoFillThreshold)); }, [autoFillThreshold]);
  useEffect(() => { localStorage.setItem('af_batch', String(autoFillBatch)); }, [autoFillBatch]);

  // Single issue creation
  const [showSingleCreate, setShowSingleCreate] = useState(false);
  const [singleClassName, setSingleClassName] = useState('');
  const [singleTier, setSingleTier] = useState('a');
  const [singleDesc, setSingleDesc] = useState('');
  const [singleCreating, setSingleCreating] = useState(false);

  const saveToken = (t: string) => {
    setToken(t);
    localStorage.setItem('gh_token', t);
    setShowTokenInput(false);
  };

  const refresh = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await fetchAllShimIssues(token || undefined);
      setIssues(data);
      setLastRefresh(new Date());
    } catch (e) {
      setError('Failed to fetch issues. GitHub API rate limit? Set a token for 5,000 req/hr.');
    }
    setLoading(false);
  }, [token]);

  useEffect(() => { refresh(); }, [refresh]);

  useEffect(() => {
    if (!autoRefresh) return;
    const interval = setInterval(refresh, 30000);
    return () => clearInterval(interval);
  }, [autoRefresh, refresh]);

  const stats: Stats = issues.reduce((s, i) => {
    const status = getStatus(i);
    if (status === 'todo') s.todo++;
    else if (status === 'in-progress') s.inProgress++;
    else if (status === 'done') s.done++;
    else if (status === 'failed') s.failed++;
    if (hasLabel(i, 'tier-a')) s.tierA++;
    else if (hasLabel(i, 'tier-b')) s.tierB++;
    else if (hasLabel(i, 'tier-c')) s.tierC++;
    else if (hasLabel(i, 'tier-d')) s.tierD++;
    s.total++;
    return s;
  }, { todo: 0, inProgress: 0, done: 0, failed: 0, total: 0, tierA: 0, tierB: 0, tierC: 0, tierD: 0 });

  // Auto-fill: when todo drops below threshold, inject more issues from tier-classes.json
  useEffect(() => {
    if (!autoFill || !token || !tierData || autoFillRunning.current) return;
    if (stats.todo >= autoFillThreshold) return;

    const existingNames = new Set(issues.map(i => extractClassName(i.title)));
    const candidates = (tierData.tiers[autoFillTier] || [])
      .filter(c => !existingNames.has(c.fqcn));

    if (candidates.length === 0) {
      setAutoFillLog(prev => [...prev.slice(-19), `[${new Date().toLocaleTimeString()}] No more ${autoFillTier} classes to create`]);
      return;
    }

    const batch = candidates.slice(0, autoFillBatch);
    autoFillRunning.current = true;
    setAutoFillLog(prev => [...prev.slice(-19), `[${new Date().toLocaleTimeString()}] Todo=${stats.todo} < ${autoFillThreshold}, creating ${batch.length} Tier ${autoFillTier} issues...`]);

    (async () => {
      let created = 0;
      for (const c of batch) {
        const title = `[SHIM] Implement ${c.fqcn}`;
        const relpath = fqcnToPath(c.fqcn);
        const skill = c.skill || lookupSkill(c.package, tierData);
        const tierInfo = TIER_LABELS[autoFillTier];
        const body = `## Android Class\n\`${c.fqcn}\`\n\n## Tier\n${autoFillTier} (${tierInfo?.label.split(' — ')[1] || ''})\n\n## APIs to implement\n${c.apis} method(s)/field(s) classified as Tier ${autoFillTier}\n\n## Package\n\`${c.package}\`\n\n## Skill file\n${skill || 'N/A'}\n\n## Instructions\n1. Read the existing shim at \`${relpath}\`\n2. Implement all Tier ${autoFillTier} methods with real Java logic\n3. Write a self-validating test in the test harness\n4. Verify 497/502 baseline holds`;

        try {
          await ghApiCall('/issues', 'POST', token, {
            title,
            body,
            labels: [`tier-${autoFillTier.toLowerCase()}`, 'todo', 'non-ui', 'shim'],
          });
          created++;
        } catch (e: any) {
          setAutoFillLog(prev => [...prev.slice(-19), `  FAILED ${c.fqcn}: ${e.message}`]);
          if (e.message.includes('403')) await new Promise(r => setTimeout(r, 5000));
        }
      }
      setAutoFillLog(prev => [...prev.slice(-19), `  Created ${created}/${batch.length} issues`]);
      autoFillRunning.current = false;
      refresh();
    })();
  }, [autoFill, token, tierData, stats.todo, autoFillThreshold, autoFillBatch, autoFillTier, issues, refresh]);

  const filtered = issues.filter(i => {
    if (filter !== 'all' && getStatus(i) !== filter) return false;
    if (tierFilter !== 'all' && getTier(i) !== tierFilter) return false;
    return true;
  }).sort((a, b) => {
    const order: Record<string, number> = { 'in-progress': 0, 'todo': 1, 'failed': 2, 'done': 3 };
    const diff = (order[getStatus(a)] ?? 4) - (order[getStatus(b)] ?? 4);
    if (diff !== 0) return diff;
    return a.number - b.number;
  });

  const completionPct = stats.total > 0 ? Math.round((stats.done / stats.total) * 100) : 0;

  // ─── Issue actions ──────────────────────────────────────────────────────

  const requireToken = (): string | null => {
    if (!token) {
      setShowTokenInput(true);
      setActionMsg('Please enter a GitHub token first');
      return null;
    }
    return token;
  };

  const changeIssueStatus = async (issue: Issue, newStatus: string) => {
    const t = requireToken();
    if (!t) return;
    setActionLoading(issue.number);
    setActionMsg(null);
    try {
      const removeLabels = ['todo', 'in-progress', 'done', 'failed'].filter(l => l !== newStatus);

      // Remove old status labels
      for (const label of removeLabels) {
        if (hasLabel(issue, label)) {
          try {
            await ghApiCall(`/issues/${issue.number}/labels/${label}`, 'DELETE', t);
          } catch { /* label may not exist */ }
        }
      }

      // Add new status label (except 'done' which uses close)
      if (newStatus === 'done') {
        await ghApiCall(`/issues/${issue.number}`, 'PATCH', t, { state: 'closed' });
      } else {
        if (issue.state === 'closed') {
          await ghApiCall(`/issues/${issue.number}`, 'PATCH', t, { state: 'open' });
        }
        await ghApiCall(`/issues/${issue.number}/labels`, 'POST', t, { labels: [newStatus] });
      }

      setActionMsg(`#${issue.number} -> ${newStatus}`);
      refresh();
    } catch (e: any) {
      setActionMsg(`Error: ${e.message}`);
    }
    setActionLoading(null);
  };

  const closeIssue = async (issue: Issue) => {
    const t = requireToken();
    if (!t) return;
    setActionLoading(issue.number);
    try {
      // Remove other status labels
      for (const label of ['todo', 'in-progress', 'failed']) {
        if (hasLabel(issue, label)) {
          try { await ghApiCall(`/issues/${issue.number}/labels/${label}`, 'DELETE', t); } catch { /* ok */ }
        }
      }
      await ghApiCall(`/issues/${issue.number}/labels`, 'POST', t, { labels: ['done'] });
      await ghApiCall(`/issues/${issue.number}`, 'PATCH', t, { state: 'closed' });
      setActionMsg(`Closed #${issue.number}`);
      refresh();
    } catch (e: any) {
      setActionMsg(`Error: ${e.message}`);
    }
    setActionLoading(null);
  };

  const reopenIssue = async (issue: Issue) => {
    const t = requireToken();
    if (!t) return;
    setActionLoading(issue.number);
    try {
      // Remove other status labels
      for (const label of ['done', 'in-progress', 'failed']) {
        if (hasLabel(issue, label)) {
          try { await ghApiCall(`/issues/${issue.number}/labels/${label}`, 'DELETE', t); } catch { /* ok */ }
        }
      }
      await ghApiCall(`/issues/${issue.number}`, 'PATCH', t, { state: 'open' });
      await ghApiCall(`/issues/${issue.number}/labels`, 'POST', t, { labels: ['todo'] });
      setActionMsg(`Reopened #${issue.number}`);
      refresh();
    } catch (e: any) {
      setActionMsg(`Error: ${e.message}`);
    }
    setActionLoading(null);
  };

  // ─── Batch issue creation ───────────────────────────────────────────────

  const existingClassNames = new Set(issues.map(i => extractClassName(i.title)));

  // Load tier data (on mount for skill lookup, and when batch panel opens)
  const loadTierData = useCallback(async () => {
    if (tierDataLoaded.current) return;
    tierDataLoaded.current = true;
    setTierDataLoading(true);
    try {
      const res = await fetch(`${import.meta.env.BASE_URL}tier-classes.json`);
      if (res.ok) setTierData(await res.json());
    } catch { /* ignore */ }
    setTierDataLoading(false);
  }, []);

  useEffect(() => { loadTierData(); }, [loadTierData]);

  const toggleClass = (fqn: string) => {
    setSelectedClasses(prev => {
      const next = new Set(prev);
      if (next.has(fqn)) next.delete(fqn); else next.add(fqn);
      return next;
    });
  };

  const selectAllInTier = () => {
    const classes = tierData?.tiers[createTier];
    if (!classes) return;
    const all = new Set(selectedClasses);
    const filtered = tierSearch
      ? classes.filter(c => c.fqcn.toLowerCase().includes(tierSearch.toLowerCase()))
      : classes;
    filtered.forEach(c => {
      if (!existingClassNames.has(c.fqcn)) all.add(c.fqcn);
    });
    setSelectedClasses(all);
  };

  const deselectAll = () => setSelectedClasses(new Set());

  const createSelectedIssues = async () => {
    const t = requireToken();
    if (!t || selectedClasses.size === 0 || !tierData) return;
    setCreating(true);
    setCreateLog([]);
    const classes = tierData.tiers[createTier] || [];
    const tierInfo = TIER_LABELS[createTier];

    for (const c of classes) {
      if (!selectedClasses.has(c.fqcn)) continue;

      const title = `[SHIM] Implement ${c.fqcn}`;
      const relpath = fqcnToPath(c.fqcn);
      const skill = c.skill || lookupSkill(c.package, tierData);
      const body = `## Android Class
\`${c.fqcn}\`

## Tier
${createTier} (${tierInfo?.label.split(' — ')[1] || ''})

## APIs to implement
${c.apis} method(s)/field(s) classified as Tier ${createTier}

## Package
\`${c.package}\`

## Skill file
${skill || 'N/A'}

## Instructions
1. Read the existing shim at \`${relpath}\`
2. Implement all Tier ${createTier} methods with real Java logic
3. Write a self-validating test in the test harness
4. Verify 497/502 baseline holds`;

      try {
        await ghApiCall('/issues', 'POST', t, {
          title,
          body,
          labels: [`tier-${createTier.toLowerCase()}`, 'todo', 'non-ui', 'shim'],
        });
        setCreateLog(prev => [...prev, `Created: ${c.fqcn}`]);
      } catch (e: any) {
        setCreateLog(prev => [...prev, `FAILED ${c.fqcn}: ${e.message}`]);
        // If rate limited, wait and continue
        if (e.message.includes('403')) {
          await new Promise(r => setTimeout(r, 5000));
        }
      }
    }

    setCreating(false);
    setSelectedClasses(new Set());
    refresh();
  };

  // ─── Single issue creation ──────────────────────────────────────────────

  const createSingleIssue = async () => {
    const t = requireToken();
    if (!t || !singleClassName.trim()) return;
    setSingleCreating(true);
    setActionMsg(null);

    const fqn = singleClassName.trim();
    const parts = fqn.split('.');
    const cls = parts[parts.length - 1];
    const pkg = parts.slice(0, -1).join('.');
    const relpath = fqcnToPath(fqn);
    const skill = lookupSkill(pkg, tierData);
    const tierLabel = singleTier.toUpperCase();
    const tierDesc = { a: 'Pure Java', b: 'I/O with Java Fallback', c: 'System Services (needs OHBridge)', d: 'UI Components (needs ArkUI)' }[singleTier] || '';

    const title = `[SHIM] Implement ${fqn}`;
    const body = `## Class: \`${fqn}\`

**Tier:** ${tierLabel} (${tierDesc})
**Description:** ${singleDesc || cls}
**File:** \`${relpath}\`
**Skill:** \`skills/${skill}.md\`

## Task

Replace stub implementations (\`return null\`, \`return 0\`, \`return false\`) with **real Java logic**.

### Conversion Skill
Read \`skills/${skill}.md\` for Android-to-OpenHarmony conversion rules for this package.

Query the DB for API details:
\`\`\`bash
sqlite3 database/api_compat.db "SELECT a.name, a.signature, m.score, m.mapping_type, m.effort_level FROM api_mappings m JOIN android_apis a ON m.android_api_id = a.id JOIN android_types t ON a.type_id = t.id WHERE t.full_name = '${cls}' ORDER BY m.score DESC"
\`\`\`

## Verification

\`\`\`bash
cd test-apps && ./run-local-tests.sh headless
# Baseline: 497/502 pass. Must not regress.
\`\`\`
`;

    try {
      await ghApiCall('/issues', 'POST', t, {
        title,
        body,
        labels: [`tier-${singleTier}`, 'todo', 'non-ui', 'shim'],
      });
      setActionMsg(`Created: ${title}`);
      setSingleClassName('');
      setSingleDesc('');
      refresh();
    } catch (e: any) {
      setActionMsg(`Error: ${e.message}`);
    }
    setSingleCreating(false);
  };

  return (
    <div className="max-w-7xl mx-auto px-4 py-6 space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between flex-wrap gap-3">
        <h1 className="text-2xl font-bold">Shim Orchestrator</h1>
        <div className="flex items-center gap-3 flex-wrap">
          {/* Token management */}
          {!token ? (
            <button
              onClick={() => setShowTokenInput(true)}
              className="px-3 py-1.5 bg-blue-700 text-white rounded-lg text-sm hover:bg-blue-600"
            >
              Set GitHub Token
            </button>
          ) : (
            <span className="text-xs text-green-500 flex items-center gap-1">
              <span className="w-1.5 h-1.5 bg-green-500 rounded-full" />
              Token set
              <button onClick={() => { setToken(''); localStorage.removeItem('gh_token'); }} className="text-gray-500 hover:text-red-400 ml-1">[clear]</button>
            </span>
          )}
          <label className="flex items-center gap-1.5 text-sm text-gray-400">
            <input type="checkbox" checked={autoRefresh} onChange={e => setAutoRefresh(e.target.checked)} className="rounded" />
            Auto-refresh
          </label>
          <button onClick={refresh} disabled={loading}
            className="px-3 py-1.5 bg-gray-800 border border-gray-700 rounded-lg text-sm hover:bg-gray-700 disabled:opacity-50">
            {loading ? 'Refreshing...' : 'Refresh'}
          </button>
          <span className="text-xs text-gray-600">Updated {timeSince(lastRefresh.toISOString())}</span>
        </div>
      </div>

      {/* Token input modal */}
      {showTokenInput && (
        <div className="fixed inset-0 bg-black/60 z-50 flex items-center justify-center" onClick={() => setShowTokenInput(false)}>
          <div className="bg-gray-900 border border-gray-700 rounded-xl p-6 max-w-md w-full mx-4" onClick={e => e.stopPropagation()}>
            <h3 className="text-lg font-semibold mb-3">GitHub Personal Access Token</h3>
            <p className="text-sm text-gray-400 mb-4">
              Required for issue management (create, close, label). Needs <code className="bg-gray-800 px-1 rounded">repo</code> scope.
              Stored in browser localStorage only.
            </p>
            <TokenInput onSave={saveToken} onCancel={() => setShowTokenInput(false)} />
          </div>
        </div>
      )}

      {/* Action messages */}
      {actionMsg && (
        <div className={`rounded-lg p-3 text-sm ${actionMsg.startsWith('Error') ? 'bg-red-900/30 border border-red-700/50 text-red-300' : 'bg-green-900/30 border border-green-700/50 text-green-300'}`}>
          {actionMsg}
          <button onClick={() => setActionMsg(null)} className="ml-3 text-gray-500 hover:text-white">[x]</button>
        </div>
      )}

      {error && (
        <div className="bg-red-900/30 border border-red-700/50 rounded-lg p-3 text-red-300 text-sm">{error}</div>
      )}

      {/* Progress bar */}
      <div className="bg-gray-900 rounded-xl p-5 border border-gray-800">
        <div className="flex items-center justify-between mb-3">
          <span className="text-lg font-semibold">Overall Progress</span>
          <span className="text-2xl font-bold text-green-400">{completionPct}%</span>
        </div>
        <div className="h-4 bg-gray-800 rounded-full overflow-hidden flex">
          {stats.done > 0 && <div className="h-full bg-green-500 transition-all duration-500" style={{ width: `${(stats.done / stats.total) * 100}%` }} title={`${stats.done} done`} />}
          {stats.inProgress > 0 && <div className="h-full bg-yellow-500 transition-all duration-500" style={{ width: `${(stats.inProgress / stats.total) * 100}%` }} title={`${stats.inProgress} in progress`} />}
          {stats.failed > 0 && <div className="h-full bg-red-500 transition-all duration-500" style={{ width: `${(stats.failed / stats.total) * 100}%` }} title={`${stats.failed} failed`} />}
        </div>
        <div className="flex gap-6 mt-3 text-sm flex-wrap">
          <span className="flex items-center gap-1.5"><span className="w-2.5 h-2.5 rounded-full bg-green-500" /> {stats.done} done</span>
          <span className="flex items-center gap-1.5"><span className="w-2.5 h-2.5 rounded-full bg-yellow-500 animate-pulse" /> {stats.inProgress} in progress</span>
          <span className="flex items-center gap-1.5"><span className="w-2.5 h-2.5 rounded-full bg-gray-500" /> {stats.todo} todo</span>
          <span className="flex items-center gap-1.5"><span className="w-2.5 h-2.5 rounded-full bg-red-500" /> {stats.failed} failed</span>
        </div>
      </div>

      {/* Auto-fill controls */}
      <div className="bg-gray-900 rounded-xl p-4 border border-gray-800">
        <div className="flex items-center justify-between flex-wrap gap-3">
          <div className="flex items-center gap-3">
            <label className="flex items-center gap-1.5 text-sm">
              <input type="checkbox" checked={autoFill} onChange={e => setAutoFill(e.target.checked)}
                className="rounded" disabled={!token} />
              <span className={autoFill ? 'text-green-400' : 'text-gray-400'}>Auto-fill queue</span>
            </label>
            {autoFill && (
              <>
                <span className="text-gray-600">|</span>
                <label className="flex items-center gap-1 text-xs text-gray-400">
                  Tier
                  <select value={autoFillTier} onChange={e => setAutoFillTier(e.target.value)}
                    className="px-2 py-0.5 bg-gray-800 border border-gray-700 rounded text-xs text-gray-200 w-14">
                    {['A', 'B', 'C', 'D'].map(t => <option key={t} value={t}>{t}</option>)}
                  </select>
                </label>
                <label className="flex items-center gap-1 text-xs text-gray-400">
                  When todo &lt;
                  <input type="number" value={autoFillThreshold} onChange={e => setAutoFillThreshold(Number(e.target.value))}
                    min={1} max={100}
                    className="px-2 py-0.5 bg-gray-800 border border-gray-700 rounded text-xs text-gray-200 w-14" />
                </label>
                <label className="flex items-center gap-1 text-xs text-gray-400">
                  Add
                  <input type="number" value={autoFillBatch} onChange={e => setAutoFillBatch(Number(e.target.value))}
                    min={1} max={100}
                    className="px-2 py-0.5 bg-gray-800 border border-gray-700 rounded text-xs text-gray-200 w-14" />
                  issues
                </label>
              </>
            )}
          </div>
          {!token && autoFill === false && (
            <span className="text-xs text-gray-500">Requires GitHub token</span>
          )}
        </div>
        {autoFillLog.length > 0 && (
          <div className="mt-3 bg-gray-800 rounded-lg p-2 max-h-32 overflow-y-auto">
            {autoFillLog.map((log, i) => (
              <div key={i} className={`text-xs font-mono ${log.includes('FAILED') ? 'text-red-400' : log.includes('Created') ? 'text-green-400' : 'text-gray-400'}`}>{log}</div>
            ))}
          </div>
        )}
      </div>

      {/* Stat cards */}
      <div className="grid grid-cols-2 md:grid-cols-4 lg:grid-cols-8 gap-3">
        <StatCard label="Issues" value={stats.total} />
        <StatCard label="Completed" value={stats.done} color="text-green-400" />
        <StatCard label="Active" value={stats.inProgress} color="text-yellow-400" />
        <StatCard label="Failed" value={stats.failed} color="text-red-400" />
        <StatCard label="Tier A" value={stats.tierA} color="text-blue-400" sub={tierData ? `/ ${tierData.counts.A_classes}` : undefined} />
        <StatCard label="Tier B" value={stats.tierB} color="text-yellow-400" sub={tierData ? `/ ${tierData.counts.B_classes}` : undefined} />
        <StatCard label="Tier C" value={stats.tierC} color="text-orange-400" sub={tierData ? `/ ${tierData.counts.C_classes}` : undefined} />
        <StatCard label="Tier D" value={stats.tierD} color="text-red-400" sub={tierData ? `/ ${tierData.counts.D_classes}` : undefined} />
      </div>

      {/* Action bar: filters + create button */}
      <div className="flex items-center justify-between flex-wrap gap-3">
        <div className="flex gap-2 flex-wrap">
          {/* Status filters */}
          {['all', 'todo', 'in-progress', 'done', 'failed'].map(f => (
            <button key={f} onClick={() => setFilter(f)}
              className={`px-3 py-1.5 rounded-lg text-sm ${filter === f ? 'bg-blue-600 text-white' : 'bg-gray-800 text-gray-400 hover:text-white'}`}>
              {f === 'all' ? `All (${stats.total})` : f === 'todo' ? `Todo (${stats.todo})` : f === 'in-progress' ? `Active (${stats.inProgress})` : f === 'done' ? `Done (${stats.done})` : `Failed (${stats.failed})`}
            </button>
          ))}
          <span className="text-gray-700">|</span>
          {/* Tier filters */}
          {['all', 'A', 'B', 'C', 'D'].map(t => (
            <button key={t} onClick={() => setTierFilter(t)}
              className={`px-3 py-1.5 rounded-lg text-sm ${tierFilter === t ? 'bg-purple-600 text-white' : 'bg-gray-800 text-gray-400 hover:text-white'}`}>
              {t === 'all' ? 'All Tiers' : `Tier ${t}`}
            </button>
          ))}
        </div>
        <div className="flex gap-2">
          <button
            onClick={() => { setShowSingleCreate(!showSingleCreate); setShowCreatePanel(false); }}
            className="px-4 py-2 bg-blue-700 text-white rounded-lg text-sm hover:bg-blue-600 font-medium"
          >
            {showSingleCreate ? 'Close' : '+ Create Issue'}
          </button>
          <button
            onClick={() => { const next = !showCreatePanel; setShowCreatePanel(next); setShowSingleCreate(false); if (next) loadTierData(); }}
            className="px-4 py-2 bg-green-700 text-white rounded-lg text-sm hover:bg-green-600 font-medium"
          >
            {showCreatePanel ? 'Close' : '+ Batch Add'}
          </button>
        </div>
      </div>

      {/* Single issue create */}
      {showSingleCreate && (
        <div className="bg-gray-900 rounded-xl border border-gray-800 p-5 space-y-4">
          <h2 className="text-lg font-semibold">Create Single Issue</h2>
          <p className="text-sm text-gray-400">
            Enter any Android class name. The skill file is auto-detected from the package.
          </p>
          <div className="grid grid-cols-1 md:grid-cols-4 gap-3">
            <div className="md:col-span-2">
              <label className="block text-xs text-gray-500 mb-1">Class (fully qualified)</label>
              <input type="text" value={singleClassName} onChange={e => setSingleClassName(e.target.value)}
                placeholder="android.graphics.Bitmap"
                className="w-full px-3 py-2 bg-gray-800 border border-gray-700 rounded-lg text-sm text-gray-200 placeholder-gray-500 focus:outline-none focus:border-blue-500" />
            </div>
            <div>
              <label className="block text-xs text-gray-500 mb-1">Tier</label>
              <select value={singleTier} onChange={e => setSingleTier(e.target.value)}
                className="w-full px-3 py-2 bg-gray-800 border border-gray-700 rounded-lg text-sm text-gray-200">
                <option value="a">A — Pure Java</option>
                <option value="b">B — I/O Fallback</option>
                <option value="c">C — System (OHBridge)</option>
                <option value="d">D — UI (ArkUI)</option>
              </select>
            </div>
            <div>
              <label className="block text-xs text-gray-500 mb-1">Description (optional)</label>
              <input type="text" value={singleDesc} onChange={e => setSingleDesc(e.target.value)}
                placeholder="Short description"
                className="w-full px-3 py-2 bg-gray-800 border border-gray-700 rounded-lg text-sm text-gray-200 placeholder-gray-500 focus:outline-none focus:border-blue-500" />
            </div>
          </div>
          {singleClassName && (
            <div className="text-xs text-gray-500">
              Skill: <code className="bg-gray-800 px-1.5 py-0.5 rounded text-gray-300">
                {lookupSkill(singleClassName.split('.').slice(0, -1).join('.'), tierData)}
              </code>
              {' | '}File: <code className="bg-gray-800 px-1.5 py-0.5 rounded text-gray-300">
                {fqcnToPath(singleClassName)}
              </code>
              {existingClassNames.has(singleClassName) && (
                <span className="ml-2 text-yellow-400">Issue already exists</span>
              )}
            </div>
          )}
          <div className="flex justify-end">
            {!token ? (
              <button onClick={() => setShowTokenInput(true)}
                className="px-4 py-2 bg-blue-700 text-white rounded-lg text-sm hover:bg-blue-600">Set Token First</button>
            ) : (
              <button onClick={createSingleIssue}
                disabled={!singleClassName.trim() || singleCreating || existingClassNames.has(singleClassName)}
                className="px-4 py-2 bg-green-700 text-white rounded-lg text-sm hover:bg-green-600 disabled:opacity-50 font-medium">
                {singleCreating ? 'Creating...' : 'Create Issue'}
              </button>
            )}
          </div>
        </div>
      )}

      {/* Batch create panel */}
      {showCreatePanel && (
        <div className="bg-gray-900 rounded-xl border border-gray-800 p-5 space-y-4">
          <div className="flex items-center justify-between">
            <h2 className="text-lg font-semibold">Add Issues by Tier</h2>
            <div className="flex gap-2">
              {['A', 'B', 'C', 'D'].map(t => (
                <button key={t} onClick={() => { setCreateTier(t); deselectAll(); setTierSearch(''); }}
                  className={`px-3 py-1.5 rounded-lg text-sm ${createTier === t ? 'bg-purple-600 text-white' : 'bg-gray-800 text-gray-400 hover:text-white'}`}>
                  {t}
                </button>
              ))}
            </div>
          </div>

          {tierDataLoading ? (
            <div className="text-center text-gray-500 py-8">Loading tier data...</div>
          ) : !tierData ? (
            <div className="text-center text-gray-500 py-8">Failed to load tier-classes.json</div>
          ) : (() => {
            const classes = tierData.tiers[createTier] || [];
            const info = TIER_LABELS[createTier];
            const filtered = tierSearch
              ? classes.filter(c => c.fqcn.toLowerCase().includes(tierSearch.toLowerCase()) || c.package.toLowerCase().includes(tierSearch.toLowerCase()))
              : classes;
            const newCount = classes.filter(c => !existingClassNames.has(c.fqcn)).length;
            return (
              <>
                <div className="flex items-center justify-between text-sm flex-wrap gap-2">
                  <span className={info?.color || 'text-gray-400'}>
                    {info?.label} — {classes.length} classes ({newCount} new)
                  </span>
                  <div className="flex gap-2 items-center">
                    <input type="text" value={tierSearch} onChange={e => setTierSearch(e.target.value)}
                      placeholder="Filter classes..."
                      className="px-3 py-1 bg-gray-800 border border-gray-700 rounded-lg text-xs text-gray-200 placeholder-gray-500 focus:outline-none focus:border-blue-500 w-48" />
                    <button onClick={selectAllInTier} className="text-xs text-blue-400 hover:text-blue-300">Select all new{tierSearch ? ' (filtered)' : ''}</button>
                    <button onClick={deselectAll} className="text-xs text-gray-500 hover:text-gray-300">Deselect all</button>
                  </div>
                </div>

                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-1.5 max-h-96 overflow-y-auto">
                  {filtered.slice(0, 300).map(c => {
                    const exists = existingClassNames.has(c.fqcn);
                    const selected = selectedClasses.has(c.fqcn);
                    return (
                      <label key={c.fqcn}
                        className={`flex items-start gap-2 p-2 rounded-lg text-sm cursor-pointer ${exists ? 'opacity-40 cursor-not-allowed' : selected ? 'bg-blue-900/30 border border-blue-700/50' : 'bg-gray-800/50 hover:bg-gray-800'}`}>
                        <input type="checkbox" checked={selected} disabled={exists}
                          onChange={() => !exists && toggleClass(c.fqcn)} className="mt-0.5 rounded" />
                        <div className="min-w-0">
                          <div className={`font-mono text-xs truncate ${exists ? 'text-gray-500 line-through' : 'text-gray-200'}`}>{c.fqcn}</div>
                          <div className="text-xs text-gray-500">{c.apis} APIs {c.skill && `· ${c.skill}`} {exists && '(exists)'}</div>
                        </div>
                      </label>
                    );
                  })}
                  {filtered.length > 300 && (
                    <div className="col-span-full text-center text-xs text-gray-500 py-2">
                      Showing 300 of {filtered.length} — use filter to narrow down
                    </div>
                  )}
                </div>

                <div className="flex items-center justify-between pt-2 border-t border-gray-800">
                  <span className="text-sm text-gray-400">{selectedClasses.size} selected</span>
                  {!token ? (
                    <button onClick={() => setShowTokenInput(true)}
                      className="px-4 py-2 bg-blue-700 text-white rounded-lg text-sm hover:bg-blue-600">
                      Set Token to Create
                    </button>
                  ) : (
                    <button onClick={createSelectedIssues} disabled={selectedClasses.size === 0 || creating}
                      className="px-4 py-2 bg-green-700 text-white rounded-lg text-sm hover:bg-green-600 disabled:opacity-50 font-medium">
                      {creating ? 'Creating...' : `Create ${selectedClasses.size} Issues`}
                    </button>
                  )}
                </div>
              </>
            );
          })()}

          {createLog.length > 0 && (
            <div className="bg-gray-800 rounded-lg p-3 max-h-40 overflow-y-auto">
              {createLog.map((log, i) => (
                <div key={i} className={`text-xs font-mono ${log.startsWith('FAILED') ? 'text-red-400' : 'text-green-400'}`}>{log}</div>
              ))}
            </div>
          )}
        </div>
      )}

      {/* Issue table */}
      <div className="bg-gray-900 rounded-xl border border-gray-800 overflow-hidden">
        <table className="w-full text-sm">
          <thead>
            <tr className="text-gray-400 border-b border-gray-800 text-left">
              <th className="py-2 px-3 w-12">#</th>
              <th className="py-2 px-3">Class</th>
              <th className="py-2 px-3 w-16">Tier</th>
              <th className="py-2 px-3 w-28">Status</th>
              <th className="py-2 px-3 w-24">Updated</th>
              <th className="py-2 px-3 w-28">Worker</th>
              <th className="py-2 px-3 w-40 text-right">Actions</th>
            </tr>
          </thead>
          <tbody>
            {filtered.map(issue => {
              const status = getStatus(issue);
              const tier = getTier(issue);
              const isActioning = actionLoading === issue.number;
              return (
                <tr key={issue.number} className="border-b border-gray-800/50 hover:bg-gray-800/30 group">
                  <td className="py-2 px-3 text-gray-500 font-mono">{issue.number}</td>
                  <td className="py-2 px-3">
                    <a href={`https://github.com/${REPO}/issues/${issue.number}`} target="_blank" rel="noopener noreferrer"
                      className="text-blue-400 hover:underline">
                      {extractClassName(issue.title)}
                    </a>
                  </td>
                  <td className="py-2 px-3">
                    <span className={`text-xs px-2 py-0.5 rounded ${TIER_COLORS[tier]}`}>{tier}</span>
                  </td>
                  <td className="py-2 px-3">
                    <span className={`inline-flex items-center gap-1.5 text-xs px-2 py-0.5 rounded ${STATUS_COLORS[status]}`}>
                      <span className={`w-1.5 h-1.5 rounded-full ${STATUS_DOT[status]}`} />
                      {status}
                    </span>
                  </td>
                  <td className="py-2 px-3 text-gray-500 text-xs">{timeSince(issue.updated_at)}</td>
                  <td className="py-2 px-3 text-gray-500 text-xs">
                    {issue.assignee?.login || (status === 'in-progress' ? 'claiming...' : '-')}
                  </td>
                  <td className="py-2 px-3 text-right">
                    {isActioning ? (
                      <span className="text-xs text-gray-500 animate-pulse">Working...</span>
                    ) : (
                      <div className="flex gap-1 justify-end opacity-0 group-hover:opacity-100 transition-opacity">
                        {status === 'todo' && (
                          <ActionBtn label="Claim" color="bg-yellow-700" onClick={() => changeIssueStatus(issue, 'in-progress')} />
                        )}
                        {status === 'in-progress' && (
                          <>
                            <ActionBtn label="Done" color="bg-green-700" onClick={() => closeIssue(issue)} />
                            <ActionBtn label="Fail" color="bg-red-700" onClick={() => changeIssueStatus(issue, 'failed')} />
                            <ActionBtn label="Release" color="bg-gray-600" onClick={() => changeIssueStatus(issue, 'todo')} />
                          </>
                        )}
                        {status === 'failed' && (
                          <>
                            <ActionBtn label="Retry" color="bg-yellow-700" onClick={() => changeIssueStatus(issue, 'todo')} />
                            <ActionBtn label="Close" color="bg-gray-600" onClick={() => closeIssue(issue)} />
                          </>
                        )}
                        {status === 'done' && (
                          <ActionBtn label="Reopen" color="bg-gray-600" onClick={() => reopenIssue(issue)} />
                        )}
                      </div>
                    )}
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
        {filtered.length === 0 && (
          <div className="p-8 text-center text-gray-500">
            {loading ? 'Loading issues...' : 'No issues match filters'}
          </div>
        )}
      </div>

      {/* How to contribute */}
      <div className="bg-gray-900 rounded-xl p-5 border border-gray-800">
        <h2 className="text-lg font-semibold mb-3">How to Contribute (CC Worker)</h2>
        <div className="bg-gray-800 rounded-lg p-4 font-mono text-xs text-gray-300 overflow-x-auto whitespace-pre">{`# 1. Clone and setup
git clone https://github.com/${REPO}.git && cd harmony-android-guide

# 2. Pick a task
gh issue list --repo ${REPO} --label todo --label tier-a --limit 5

# 3. Claim it
gh issue edit <NUMBER> --repo ${REPO} --remove-label todo --add-label in-progress

# 4. Implement the shim + write tests (see CLAUDE.md for details)

# 5. Verify
cd test-apps && ./run-local-tests.sh headless

# 6. Push and close
git checkout -b shim/<class-name> && git push origin shim/<class-name>
gh issue close <NUMBER> --comment "Done" --repo ${REPO}`}</div>
      </div>
    </div>
  );
}

// ─── Sub-components ─────────────────────────────────────────────────────────

function StatCard({ label, value, color = 'text-white', sub }: { label: string; value: number; color?: string; sub?: string }) {
  return (
    <div className="bg-gray-900 border border-gray-800 rounded-xl p-3">
      <div className="text-xs text-gray-500 uppercase tracking-wide">{label}</div>
      <div className={`text-xl font-bold mt-1 ${color}`}>
        {value}
        {sub && <span className="text-xs font-normal text-gray-500 ml-1">{sub}</span>}
      </div>
    </div>
  );
}

function ActionBtn({ label, color, onClick }: { label: string; color: string; onClick: () => void }) {
  return (
    <button onClick={onClick} className={`px-2 py-0.5 ${color} text-white text-xs rounded hover:opacity-80`}>
      {label}
    </button>
  );
}

function TokenInput({ onSave, onCancel }: { onSave: (t: string) => void; onCancel: () => void }) {
  const [val, setVal] = useState('');
  return (
    <div className="flex gap-2">
      <input type="password" value={val} onChange={e => setVal(e.target.value)} placeholder="ghp_..."
        className="flex-1 px-3 py-2 bg-gray-800 border border-gray-700 rounded-lg text-sm text-gray-200 placeholder-gray-500 focus:outline-none focus:border-blue-500" />
      <button onClick={() => onSave(val)} disabled={!val}
        className="px-4 py-2 bg-blue-600 text-white rounded-lg text-sm hover:bg-blue-500 disabled:opacity-50">Save</button>
      <button onClick={onCancel}
        className="px-4 py-2 bg-gray-700 text-gray-300 rounded-lg text-sm hover:bg-gray-600">Cancel</button>
    </div>
  );
}

