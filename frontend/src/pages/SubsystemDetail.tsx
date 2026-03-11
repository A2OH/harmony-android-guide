import { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import { getSubsystem } from '../api/client';
import type { SubsystemDetail as SubsystemDetailType } from '../api/client';
import { ScoreBadge } from '../components/ScoreBadge';
import { scoreBarColor } from '../utils/colors';
import { BarChart, Bar, XAxis, YAxis, Tooltip, ResponsiveContainer, Cell } from 'recharts';
import { useLang } from '../i18n/LanguageContext';

const EFFORT_COLORS: Record<string, string> = {
  trivial: '#059669', easy: '#16a34a', moderate: '#ca8a04',
  hard: '#ea580c', rewrite: '#dc2626', impossible: '#7f1d1d',
};

type TypeSortKey = 'name' | 'kind' | 'package_name' | 'api_count' | 'avg_score';
type GapSortKey = 'name' | 'type_name' | 'package_name' | 'compat_score';

export default function SubsystemDetail() {
  const { name } = useParams();
  const [data, setData] = useState<SubsystemDetailType | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [typeSortKey, setTypeSortKey] = useState<TypeSortKey>('api_count');
  const [typeSortDir, setTypeSortDir] = useState<'asc' | 'desc'>('desc');
  const [gapSortKey, setGapSortKey] = useState<GapSortKey>('compat_score');
  const [gapSortDir, setGapSortDir] = useState<'asc' | 'desc'>('asc');
  const { t } = useLang();

  const toggleTypeSort = (key: TypeSortKey) => {
    if (typeSortKey === key) setTypeSortDir(d => d === 'asc' ? 'desc' : 'asc');
    else { setTypeSortKey(key); setTypeSortDir('desc'); }
  };
  const toggleGapSort = (key: GapSortKey) => {
    if (gapSortKey === key) setGapSortDir(d => d === 'asc' ? 'desc' : 'asc');
    else { setGapSortKey(key); setGapSortDir('asc'); }
  };
  const sortIcon = (active: boolean, dir: 'asc' | 'desc') => active ? (dir === 'asc' ? ' ▲' : ' ▼') : '';

  useEffect(() => {
    if (name) {
      setData(null);
      setError(null);
      getSubsystem(name).then(setData).catch(e => setError(e.message || 'Failed to load'));
    }
  }, [name]);

  if (error) return <div className="p-8 text-center text-red-400">Error: {error}</div>;
  if (!data) return <div className="p-8 text-center text-gray-500">{t('loading')}</div>;

  return (
    <div className="max-w-7xl mx-auto px-4 py-6 space-y-6">
      {/* Breadcrumb */}
      <div className="text-sm text-gray-500">
        <Link to="/" className="hover:text-blue-400">{t('nav.dashboard')}</Link>
        {' → '}
        <span className="text-white">{data.name}</span>
      </div>

      {/* Header */}
      <div className="bg-gray-900 border border-gray-800 rounded-xl p-6">
        <h1 className="text-2xl font-bold">{data.name}</h1>
        <div className="flex items-center gap-6 mt-3 text-sm">
          <div>
            <span className="text-gray-500">{t('subsystem.apis')}: </span>
            <span className="font-bold">{data.api_count_android.toLocaleString()}</span>
          </div>
          <div>
            <span className="text-gray-500">{t('subsystem.avgScore')}: </span>
            <span className="font-bold">{data.overall_score.toFixed(1)}/10</span>
          </div>
          <div>
            <span className="text-gray-500">{t('subsystem.coverage')}: </span>
            <span className="font-bold">{data.coverage_pct}%</span>
          </div>
        </div>
        <div className="mt-3 w-full h-3 bg-gray-800 rounded-full overflow-hidden">
          <div
            className="h-full rounded-full"
            style={{ width: `${data.coverage_pct}%`, backgroundColor: scoreBarColor(data.overall_score) }}
          />
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Score Distribution */}
        {data.score_distribution && data.score_distribution.length > 0 && (
          <div className="bg-gray-900 rounded-xl p-4 border border-gray-800">
            <h2 className="text-lg font-semibold mb-3">{t('subsystem.scoreDistribution')}</h2>
            <ResponsiveContainer width="100%" height={200}>
              <BarChart data={data.score_distribution}>
                <XAxis dataKey="bucket" stroke="#9ca3af" fontSize={11} />
                <YAxis stroke="#9ca3af" fontSize={11} />
                <Tooltip contentStyle={{ background: '#1f2937', border: '1px solid #374151', borderRadius: '8px', color: '#e5e7eb' }} labelStyle={{ color: '#e5e7eb' }} itemStyle={{ color: '#e5e7eb' }} />
                <Bar dataKey="count" radius={[4, 4, 0, 0]} fill="#3b82f6" />
              </BarChart>
            </ResponsiveContainer>
          </div>
        )}

        {/* Effort Distribution */}
        {data.effort_distribution && data.effort_distribution.length > 0 && (
          <div className="bg-gray-900 rounded-xl p-4 border border-gray-800">
            <h2 className="text-lg font-semibold mb-3">{t('subsystem.effortDistribution')}</h2>
            <ResponsiveContainer width="100%" height={200}>
              <BarChart data={data.effort_distribution} layout="vertical">
                <XAxis type="number" stroke="#9ca3af" fontSize={11} />
                <YAxis type="category" dataKey="effort_level" stroke="#9ca3af" fontSize={11} width={80} />
                <Tooltip contentStyle={{ background: '#1f2937', border: '1px solid #374151', borderRadius: '8px', color: '#e5e7eb' }} labelStyle={{ color: '#e5e7eb' }} itemStyle={{ color: '#e5e7eb' }} />
                <Bar dataKey="count" radius={[0, 4, 4, 0]}>
                  {data.effort_distribution.map((e) => (
                    <Cell key={e.effort_level} fill={EFFORT_COLORS[e.effort_level] || '#6b7280'} />
                  ))}
                </Bar>
              </BarChart>
            </ResponsiveContainer>
          </div>
        )}
      </div>

      {/* Types in this subsystem */}
      {data.types && data.types.length > 0 && (
        <div className="bg-gray-900 border border-gray-800 rounded-xl p-4">
          <h2 className="font-semibold mb-3">{t('subsystem.types')} ({data.types.length})</h2>
          <div className="overflow-x-auto">
            <table className="w-full text-sm">
              <thead>
                <tr className="text-gray-400 border-b border-gray-800">
                  <th className="text-left py-2 px-3 cursor-pointer hover:text-white select-none" onClick={() => toggleTypeSort('name')}>{t('apiDetail.type')}{sortIcon(typeSortKey === 'name', typeSortDir)}</th>
                  <th className="text-left py-2 px-3 cursor-pointer hover:text-white select-none" onClick={() => toggleTypeSort('kind')}>{t('subsystem.kind')}{sortIcon(typeSortKey === 'kind', typeSortDir)}</th>
                  <th className="text-left py-2 px-3 cursor-pointer hover:text-white select-none" onClick={() => toggleTypeSort('package_name')}>{t('subsystem.package')}{sortIcon(typeSortKey === 'package_name', typeSortDir)}</th>
                  <th className="text-right py-2 px-3 cursor-pointer hover:text-white select-none" onClick={() => toggleTypeSort('api_count')}>{t('subsystem.apis')}{sortIcon(typeSortKey === 'api_count', typeSortDir)}</th>
                  <th className="text-right py-2 px-3 cursor-pointer hover:text-white select-none" onClick={() => toggleTypeSort('avg_score')}>{t('subsystem.avgScore')}{sortIcon(typeSortKey === 'avg_score', typeSortDir)}</th>
                </tr>
              </thead>
              <tbody>
                {[...data.types].sort((a, b) => {
                  const av = a[typeSortKey], bv = b[typeSortKey];
                  const cmp = typeof av === 'string' ? av.localeCompare(bv as string) : (av as number) - (bv as number);
                  return typeSortDir === 'asc' ? cmp : -cmp;
                }).map((tp) => (
                  <tr key={tp.id} className="border-b border-gray-800/50 hover:bg-gray-800/30">
                    <td className="py-2 px-3 font-mono text-blue-300">{tp.name}</td>
                    <td className="py-2 px-3 text-gray-400">{tp.kind}</td>
                    <td className="py-2 px-3 text-gray-500 text-xs">{tp.package_name}</td>
                    <td className="text-right py-2 px-3">{tp.api_count}</td>
                    <td className="text-right py-2 px-3"><ScoreBadge score={tp.avg_score} /></td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* Top Gaps */}
      {data.top_gaps && data.top_gaps.length > 0 && (
        <div className="bg-gray-900 border border-gray-800 rounded-xl p-4">
          <h2 className="font-semibold mb-3">{t('subsystem.topGaps')}</h2>
          <div className="overflow-x-auto">
            <table className="w-full text-sm">
              <thead>
                <tr className="text-gray-400 border-b border-gray-800">
                  <th className="text-right py-2 px-3 cursor-pointer hover:text-white select-none" onClick={() => toggleGapSort('compat_score')}>{t('subsystem.avgScore')}{sortIcon(gapSortKey === 'compat_score', gapSortDir)}</th>
                  <th className="text-left py-2 px-3 cursor-pointer hover:text-white select-none" onClick={() => toggleGapSort('name')}>{t('browse.name')}{sortIcon(gapSortKey === 'name', gapSortDir)}</th>
                  <th className="text-left py-2 px-3 cursor-pointer hover:text-white select-none" onClick={() => toggleGapSort('type_name')}>{t('apiDetail.type')}{sortIcon(gapSortKey === 'type_name', gapSortDir)}</th>
                  <th className="text-left py-2 px-3 cursor-pointer hover:text-white select-none" onClick={() => toggleGapSort('package_name')}>{t('subsystem.package')}{sortIcon(gapSortKey === 'package_name', gapSortDir)}</th>
                </tr>
              </thead>
              <tbody>
                {[...data.top_gaps].sort((a, b) => {
                  const av = a[gapSortKey] ?? 0, bv = b[gapSortKey] ?? 0;
                  const cmp = typeof av === 'string' ? av.localeCompare(bv as string) : (av as number) - (bv as number);
                  return gapSortDir === 'asc' ? cmp : -cmp;
                }).map((gap) => (
                  <tr key={gap.id} className="border-b border-gray-800/50 hover:bg-gray-800/30">
                    <td className="text-right py-2 px-3"><ScoreBadge score={gap.compat_score} /></td>
                    <td className="py-2 px-3">
                      <Link to={`/api/${gap.id}`} className="font-mono text-red-300 hover:underline">{gap.name}</Link>
                    </td>
                    <td className="py-2 px-3 font-mono text-blue-300">{gap.type_name}</td>
                    <td className="py-2 px-3 text-gray-500 text-xs">{gap.package_name}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}
    </div>
  );
}
