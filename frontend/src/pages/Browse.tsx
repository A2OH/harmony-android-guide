import { useEffect, useState } from 'react';
import { Link, useSearchParams } from 'react-router-dom';
import { getAndroidPackages, getAndroidApis } from '../api/client';
import type { AndroidPackage, AndroidApi, PaginatedResponse } from '../api/client';
import { ScoreBadge, EffortBadge } from '../components/ScoreBadge';
import { useLang } from '../i18n/LanguageContext';

export default function Browse() {
  const [searchParams] = useSearchParams();
  const [packages, setPackages] = useState<AndroidPackage[]>([]);
  const [selectedPkg, setSelectedPkg] = useState(searchParams.get('package') || '');
  const [apis, setApis] = useState<PaginatedResponse<AndroidApi> | null>(null);
  const [page, setPage] = useState(1);
  const [search, setSearch] = useState('');
  const [scoreFilter, setScoreFilter] = useState('');
  const [effortFilter, setEffortFilter] = useState('');
  const [kindFilter, setKindFilter] = useState('');
  const { t } = useLang();

  useEffect(() => {
    getAndroidPackages().then(setPackages);
  }, []);

  useEffect(() => {
    const params = new URLSearchParams();
    if (selectedPkg) params.set('package', selectedPkg);
    if (search) params.set('search', search);
    if (scoreFilter) {
      const [min, max] = scoreFilter.split('-');
      if (min) params.set('score_min', min);
      if (max) params.set('score_max', max);
    }
    if (effortFilter) params.set('effort', effortFilter);
    if (kindFilter) params.set('kind', kindFilter);
    params.set('page', String(page));
    params.set('page_size', '50');
    getAndroidApis(params.toString()).then(setApis);
  }, [selectedPkg, page, search, scoreFilter, effortFilter, kindFilter]);

  // Group packages by subsystem
  const grouped = packages.reduce((acc, pkg) => {
    if (!acc[pkg.subsystem]) acc[pkg.subsystem] = [];
    acc[pkg.subsystem].push(pkg);
    return acc;
  }, {} as Record<string, AndroidPackage[]>);

  return (
    <div className="max-w-7xl mx-auto px-4 py-6 flex gap-4">
      {/* Sidebar */}
      <div className="w-64 shrink-0">
        <div className="bg-gray-900 border border-gray-800 rounded-xl p-3 max-h-[calc(100vh-100px)] overflow-y-auto sticky top-20">
          <h3 className="text-sm font-semibold text-gray-400 mb-2">{t('browse.androidPackages')}</h3>
          <button
            onClick={() => { setSelectedPkg(''); setPage(1); }}
            className={`w-full text-left text-xs px-2 py-1 rounded ${!selectedPkg ? 'bg-blue-600 text-white' : 'text-gray-400 hover:bg-gray-800'}`}
          >
            {t('browse.all')} ({packages.reduce((s, p) => s + (p.api_count || 0), 0).toLocaleString()})
          </button>
          {Object.entries(grouped).sort().map(([sub, pkgs]) => (
            <div key={sub} className="mt-2">
              <div className="text-xs text-gray-500 font-semibold px-2">{sub}</div>
              {pkgs.map((pkg) => (
                <button
                  key={pkg.name}
                  onClick={() => { setSelectedPkg(pkg.name); setPage(1); }}
                  className={`w-full text-left text-xs px-2 py-0.5 rounded truncate ${selectedPkg === pkg.name ? 'bg-blue-600 text-white' : 'text-gray-400 hover:bg-gray-800'}`}
                >
                  {pkg.name.replace('android.', '')} <span className="text-gray-600">({pkg.api_count})</span>
                </button>
              ))}
            </div>
          ))}
        </div>
      </div>

      {/* Main content */}
      <div className="flex-1 min-w-0">
        {/* Filters */}
        <div className="bg-gray-900 border border-gray-800 rounded-xl p-3 mb-4 flex flex-wrap gap-3 items-center">
          <input
            type="text"
            placeholder={t('browse.filterByName')}
            value={search}
            onChange={(e) => { setSearch(e.target.value); setPage(1); }}
            className="px-3 py-1.5 bg-gray-800 border border-gray-700 rounded text-sm text-gray-200 focus:outline-none focus:border-blue-500"
          />
          <select
            value={scoreFilter}
            onChange={(e) => { setScoreFilter(e.target.value); setPage(1); }}
            className="px-2 py-1.5 bg-gray-800 border border-gray-700 rounded text-sm text-gray-200"
          >
            <option value="">{t('browse.allScores')}</option>
            <option value="8-10">{t('score.8-10')}</option>
            <option value="5-7">{t('score.5-7')}</option>
            <option value="3-4">{t('score.3-4')}</option>
            <option value="1-2">{t('score.1-2')}</option>
          </select>
          <select
            value={effortFilter}
            onChange={(e) => { setEffortFilter(e.target.value); setPage(1); }}
            className="px-2 py-1.5 bg-gray-800 border border-gray-700 rounded text-sm text-gray-200"
          >
            <option value="">{t('browse.allEffort')}</option>
            <option value="trivial">{t('effort.trivial')}</option>
            <option value="easy">{t('effort.easy')}</option>
            <option value="moderate">{t('effort.moderate')}</option>
            <option value="hard">{t('effort.hard')}</option>
            <option value="rewrite">{t('effort.rewrite')}</option>
            <option value="impossible">{t('effort.impossible')}</option>
          </select>
          <select
            value={kindFilter}
            onChange={(e) => { setKindFilter(e.target.value); setPage(1); }}
            className="px-2 py-1.5 bg-gray-800 border border-gray-700 rounded text-sm text-gray-200"
          >
            <option value="">{t('browse.allTypes')}</option>
            <option value="method">{t('type.methods')}</option>
            <option value="field">{t('type.fields')}</option>
            <option value="constructor">{t('type.constructors')}</option>
            <option value="enum_constant">{t('type.enumConstants')}</option>
          </select>
          {apis && <span className="text-xs text-gray-500 ml-auto">{t('browse.results', { count: apis.total.toLocaleString() })}</span>}
        </div>

        {/* API list */}
        {apis && (
          <div className="space-y-1">
            {apis.items.map((api) => (
              <Link
                key={api.id}
                to={`/api/${api.id}`}
                className="block bg-gray-900 border border-gray-800 rounded-lg px-4 py-2 hover:border-gray-600 transition"
              >
                <div className="flex items-center gap-3">
                  <span className="text-xs text-gray-500 bg-gray-800 px-1.5 py-0.5 rounded">{api.kind}</span>
                  <span className="font-mono text-sm text-blue-300">{api.name}</span>
                  <span className="text-xs text-gray-600">{api.type_name}</span>
                  <div className="ml-auto flex items-center gap-2">
                    <EffortBadge effort={api.effort_level} />
                    <ScoreBadge score={api.compat_score} />
                  </div>
                </div>
                <div className="text-xs text-gray-500 font-mono mt-1 truncate">{api.signature}</div>
              </Link>
            ))}

            {/* Pagination */}
            {apis.total_pages > 1 && (
              <div className="flex justify-center gap-2 mt-4">
                <button
                  disabled={page <= 1}
                  onClick={() => setPage(p => p - 1)}
                  className="px-3 py-1 bg-gray-800 rounded text-sm disabled:opacity-30"
                >
                  {t('pagination.prev')}
                </button>
                <span className="px-3 py-1 text-sm text-gray-400">
                  {t('pagination.pageOf', { page, total: apis.total_pages })}
                </span>
                <button
                  disabled={page >= apis.total_pages}
                  onClick={() => setPage(p => p + 1)}
                  className="px-3 py-1 bg-gray-800 rounded text-sm disabled:opacity-30"
                >
                  {t('pagination.next')}
                </button>
              </div>
            )}
          </div>
        )}
      </div>
    </div>
  );
}
