import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { getAndroidApis } from '../api/client';
import type { AndroidApi, PaginatedResponse } from '../api/client';
import { EffortBadge, ScoreBadge } from '../components/ScoreBadge';

export default function Gaps() {
  const [data, setData] = useState<PaginatedResponse<AndroidApi> | null>(null);
  const [page, setPage] = useState(1);
  const [kindFilter, setKindFilter] = useState('');
  const [search, setSearch] = useState('');

  useEffect(() => {
    const params = new URLSearchParams();
    params.set('score_max', '3');
    if (search) params.set('search', search);
    if (kindFilter) params.set('kind', kindFilter);
    params.set('page', String(page));
    params.set('page_size', '50');
    getAndroidApis(params.toString()).then(setData);
  }, [page, search, kindFilter]);

  return (
    <div className="max-w-7xl mx-auto px-4 py-6 space-y-4">
      <div>
        <h1 className="text-2xl font-bold">API Gaps</h1>
        <p className="text-sm text-gray-400 mt-1">Android APIs with compatibility score ≤ 3 — these require significant effort or have no OpenHarmony equivalent.</p>
      </div>

      {/* Filters */}
      <div className="bg-gray-900 border border-gray-800 rounded-xl p-3 flex flex-wrap gap-3 items-center">
        <input
          type="text"
          placeholder="Filter gaps..."
          value={search}
          onChange={(e) => { setSearch(e.target.value); setPage(1); }}
          className="px-3 py-1.5 bg-gray-800 border border-gray-700 rounded text-sm text-gray-200 focus:outline-none focus:border-blue-500"
        />
        <select
          value={kindFilter}
          onChange={(e) => { setKindFilter(e.target.value); setPage(1); }}
          className="px-2 py-1.5 bg-gray-800 border border-gray-700 rounded text-sm text-gray-200"
        >
          <option value="">All Types</option>
          <option value="method">Methods</option>
          <option value="field">Fields</option>
          <option value="constructor">Constructors</option>
          <option value="enum_constant">Enum Constants</option>
        </select>
        {data && <span className="text-xs text-gray-500 ml-auto">{data.total.toLocaleString()} gaps</span>}
      </div>

      {/* Gap list */}
      {data && (
        <div className="space-y-1">
          {data.items.map((api) => (
            <Link
              key={api.id}
              to={`/api/${api.id}`}
              className="block bg-gray-900 border border-gray-800 rounded-lg px-4 py-2 hover:border-gray-600 transition"
            >
              <div className="flex items-center gap-3">
                <ScoreBadge score={api.compat_score} />
                <span className="text-xs text-gray-500 bg-gray-800 px-1.5 py-0.5 rounded">{api.kind}</span>
                <span className="font-mono text-sm text-red-300">{api.type_name}.{api.name}</span>
                <span className="text-xs text-gray-600">{api.package_name}</span>
                <div className="ml-auto">
                  <EffortBadge effort={api.effort_level} />
                </div>
              </div>
              <div className="text-xs text-gray-500 font-mono mt-1 truncate">{api.signature}</div>
            </Link>
          ))}

          {data.total_pages > 1 && (
            <div className="flex justify-center gap-2 mt-4">
              <button
                disabled={page <= 1}
                onClick={() => setPage(p => p - 1)}
                className="px-3 py-1 bg-gray-800 rounded text-sm disabled:opacity-30"
              >Prev</button>
              <span className="px-3 py-1 text-sm text-gray-400">
                Page {page} of {data.total_pages}
              </span>
              <button
                disabled={page >= data.total_pages}
                onClick={() => setPage(p => p + 1)}
                className="px-3 py-1 bg-gray-800 rounded text-sm disabled:opacity-30"
              >Next</button>
            </div>
          )}
        </div>
      )}
    </div>
  );
}
