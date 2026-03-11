import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { getMappings } from '../api/client';
import type { MappingItem, PaginatedResponse } from '../api/client';
import { ScoreBadge, EffortBadge } from '../components/ScoreBadge';

export default function Mappings() {
  const [data, setData] = useState<PaginatedResponse<MappingItem> | null>(null);
  const [page, setPage] = useState(1);
  const [scoreFilter, setScoreFilter] = useState('');
  const [effortFilter, setEffortFilter] = useState('');
  const [typeFilter, setTypeFilter] = useState('');
  const [search, setSearch] = useState('');

  useEffect(() => {
    const params = new URLSearchParams();
    if (search) params.set('search', search);
    if (scoreFilter) {
      const [min, max] = scoreFilter.split('-');
      if (min) params.set('score_min', min);
      if (max) params.set('score_max', max);
    }
    if (effortFilter) params.set('effort', effortFilter);
    if (typeFilter) params.set('mapping_type', typeFilter);
    params.set('page', String(page));
    params.set('page_size', '50');
    getMappings(params.toString()).then(setData);
  }, [page, search, scoreFilter, effortFilter, typeFilter]);

  return (
    <div className="max-w-7xl mx-auto px-4 py-6 space-y-4">
      <h1 className="text-2xl font-bold">API Mappings</h1>

      {/* Filters */}
      <div className="bg-gray-900 border border-gray-800 rounded-xl p-3 flex flex-wrap gap-3 items-center">
        <input
          type="text"
          placeholder="Search mappings..."
          value={search}
          onChange={(e) => { setSearch(e.target.value); setPage(1); }}
          className="px-3 py-1.5 bg-gray-800 border border-gray-700 rounded text-sm text-gray-200 focus:outline-none focus:border-blue-500"
        />
        <select
          value={scoreFilter}
          onChange={(e) => { setScoreFilter(e.target.value); setPage(1); }}
          className="px-2 py-1.5 bg-gray-800 border border-gray-700 rounded text-sm text-gray-200"
        >
          <option value="">All Scores</option>
          <option value="8-10">8-10 (Direct)</option>
          <option value="5-7">5-7 (Partial)</option>
          <option value="3-4">3-4 (Hard)</option>
          <option value="1-2">1-2 (Gap)</option>
        </select>
        <select
          value={effortFilter}
          onChange={(e) => { setEffortFilter(e.target.value); setPage(1); }}
          className="px-2 py-1.5 bg-gray-800 border border-gray-700 rounded text-sm text-gray-200"
        >
          <option value="">All Effort</option>
          <option value="trivial">Trivial</option>
          <option value="easy">Easy</option>
          <option value="moderate">Moderate</option>
          <option value="hard">Hard</option>
          <option value="rewrite">Rewrite</option>
          <option value="impossible">Impossible</option>
        </select>
        <select
          value={typeFilter}
          onChange={(e) => { setTypeFilter(e.target.value); setPage(1); }}
          className="px-2 py-1.5 bg-gray-800 border border-gray-700 rounded text-sm text-gray-200"
        >
          <option value="">All Types</option>
          <option value="direct">Direct</option>
          <option value="near">Near</option>
          <option value="partial">Partial</option>
          <option value="composite">Composite</option>
          <option value="none">None</option>
        </select>
        {data && <span className="text-xs text-gray-500 ml-auto">{data.total.toLocaleString()} mappings</span>}
      </div>

      {/* Mapping list */}
      {data && (
        <div className="space-y-1">
          <div className="overflow-x-auto">
            <table className="w-full text-sm">
              <thead>
                <tr className="text-gray-400 border-b border-gray-800 text-left">
                  <th className="py-2 px-3">Score</th>
                  <th className="py-2 px-3">Android API</th>
                  <th className="py-2 px-3">OH API</th>
                  <th className="py-2 px-3">Type</th>
                  <th className="py-2 px-3">Effort</th>
                  <th className="py-2 px-3">Flags</th>
                </tr>
              </thead>
              <tbody>
                {data.items.map((m) => (
                  <tr key={m.id} className="border-b border-gray-800/50 hover:bg-gray-800/30">
                    <td className="py-2 px-3"><ScoreBadge score={m.score} /></td>
                    <td className="py-2 px-3">
                      <Link to={`/api/${m.android_api_id}`} className="text-blue-400 hover:underline font-mono text-xs">
                        {m.android_type_name}.{m.android_api_name}
                      </Link>
                      <div className="text-xs text-gray-600">{m.android_package}</div>
                    </td>
                    <td className="py-2 px-3">
                      {m.oh_api_name ? (
                        <>
                          <span className="font-mono text-xs text-blue-300">{m.oh_type_name ? `${m.oh_type_name}.` : ''}{m.oh_api_name}</span>
                          <div className="text-xs text-gray-600">{m.oh_module}</div>
                        </>
                      ) : (
                        <span className="text-gray-500 italic text-xs">No mapping</span>
                      )}
                    </td>
                    <td className="py-2 px-3">
                      <span className="text-xs text-gray-400">{m.mapping_type}</span>
                    </td>
                    <td className="py-2 px-3"><EffortBadge effort={m.effort_level} /></td>
                    <td className="py-2 px-3">
                      <div className="flex gap-1">
                        {m.paradigm_shift ? <span className="text-xs bg-purple-900 text-purple-300 px-1.5 py-0.5 rounded">paradigm</span> : null}
                        {m.needs_ui_rewrite ? <span className="text-xs bg-red-900 text-red-300 px-1.5 py-0.5 rounded">UI</span> : null}
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>

          {/* Pagination */}
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
