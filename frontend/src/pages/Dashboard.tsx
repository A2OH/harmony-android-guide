import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { BarChart, Bar, XAxis, YAxis, Tooltip, ResponsiveContainer, Cell } from 'recharts';
import { getStatsOverview, getCoverageBySubsystem, getEffortBreakdown, getScoreDistribution } from '../api/client';
import type { StatsOverview, SubsystemCoverage, EffortItem, ScoreDistItem } from '../api/client';
import { scoreBarColor } from '../utils/colors';
import { useLang } from '../i18n/LanguageContext';

const EFFORT_COLORS: Record<string, string> = {
  trivial: '#059669', easy: '#16a34a', moderate: '#ca8a04',
  hard: '#ea580c', rewrite: '#dc2626', impossible: '#7f1d1d',
};

export default function Dashboard() {
  const [stats, setStats] = useState<StatsOverview | null>(null);
  const [coverage, setCoverage] = useState<SubsystemCoverage[]>([]);
  const [effort, setEffort] = useState<EffortItem[]>([]);
  const [scoreDist, setScoreDist] = useState<ScoreDistItem[]>([]);
  const [sortKey, setSortKey] = useState<keyof SubsystemCoverage>('total_apis');
  const [sortDir, setSortDir] = useState<'asc' | 'desc'>('desc');
  const { t } = useLang();

  const toggleSort = (key: keyof SubsystemCoverage) => {
    if (sortKey === key) setSortDir(d => d === 'asc' ? 'desc' : 'asc');
    else { setSortKey(key); setSortDir('desc'); }
  };

  const sortedCoverage = [...coverage].sort((a, b) => {
    const av = a[sortKey], bv = b[sortKey];
    const cmp = typeof av === 'string' ? av.localeCompare(bv as string) : (av as number) - (bv as number);
    return sortDir === 'asc' ? cmp : -cmp;
  });

  const sortIcon = (key: keyof SubsystemCoverage) =>
    sortKey === key ? (sortDir === 'asc' ? ' ▲' : ' ▼') : '';

  useEffect(() => {
    getStatsOverview().then(setStats);
    getCoverageBySubsystem().then(setCoverage);
    getEffortBreakdown().then(setEffort);
    getScoreDistribution().then(setScoreDist);
  }, []);

  if (!stats) return <div className="p-8 text-center text-gray-500">{t('loading')}</div>;

  const scoreData = Object.entries(stats.score_distribution)
    .map(([k, v]) => ({ score: Number(k), count: v }))
    .sort((a, b) => a.score - b.score);

  return (
    <div className="max-w-7xl mx-auto px-4 py-6 space-y-6">
      <h1 className="text-2xl font-bold">{t('dashboard.title')}</h1>

      {/* Stat cards */}
      <div className="grid grid-cols-2 md:grid-cols-4 lg:grid-cols-6 gap-4">
        <StatCard label={t('stat.androidApis')} value={stats.total_android_apis.toLocaleString()} />
        <StatCard label={t('stat.ohApis')} value={stats.total_oh_apis.toLocaleString()} />
        <StatCard label={t('stat.mapped')} value={stats.mapped_count.toLocaleString()} color="text-green-400" />
        <StatCard label={t('stat.unmapped')} value={stats.unmapped_count.toLocaleString()} color="text-red-400" />
        <StatCard label={t('stat.avgScore')} value={stats.avg_score.toFixed(1) + '/10'} />
        <StatCard label={t('stat.totalMappings')} value={stats.total_mappings.toLocaleString()} />
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Score Distribution */}
        <div className="bg-gray-900 rounded-xl p-4 border border-gray-800">
          <h2 className="text-lg font-semibold mb-3">{t('chart.scoreDistribution')}</h2>
          <ResponsiveContainer width="100%" height={250}>
            <BarChart data={scoreData}>
              <XAxis dataKey="score" stroke="#9ca3af" fontSize={12} />
              <YAxis stroke="#9ca3af" fontSize={12} />
              <Tooltip contentStyle={{ background: '#1f2937', border: '1px solid #374151', borderRadius: '8px', color: '#e5e7eb' }} labelStyle={{ color: '#e5e7eb' }} itemStyle={{ color: '#e5e7eb' }} />
              <Bar dataKey="count" radius={[4, 4, 0, 0]}>
                {scoreData.map((entry) => (
                  <Cell key={entry.score} fill={scoreBarColor(entry.score)} />
                ))}
              </Bar>
            </BarChart>
          </ResponsiveContainer>
        </div>

        {/* Effort Breakdown */}
        <div className="bg-gray-900 rounded-xl p-4 border border-gray-800">
          <h2 className="text-lg font-semibold mb-3">{t('chart.migrationEffort')}</h2>
          <ResponsiveContainer width="100%" height={250}>
            <BarChart data={effort} layout="vertical">
              <XAxis type="number" stroke="#9ca3af" fontSize={12} />
              <YAxis type="category" dataKey="effort_level" stroke="#9ca3af" fontSize={12} width={80} />
              <Tooltip contentStyle={{ background: '#1f2937', border: '1px solid #374151', borderRadius: '8px', color: '#e5e7eb' }} labelStyle={{ color: '#e5e7eb' }} itemStyle={{ color: '#e5e7eb' }} />
              <Bar dataKey="count" radius={[0, 4, 4, 0]}>
                {effort.map((e) => (
                  <Cell key={e.effort_level} fill={EFFORT_COLORS[e.effort_level] || '#6b7280'} />
                ))}
              </Bar>
            </BarChart>
          </ResponsiveContainer>
        </div>
      </div>

      {/* Coverage by Subsystem */}
      <div className="bg-gray-900 rounded-xl p-4 border border-gray-800">
        <h2 className="text-lg font-semibold mb-3">{t('table.coverageBySubsystem')}</h2>
        <div className="overflow-x-auto">
          <table className="w-full text-sm">
            <thead>
              <tr className="text-gray-400 border-b border-gray-800">
                <th className="text-left py-2 px-3 cursor-pointer hover:text-white select-none" onClick={() => toggleSort('subsystem')}>{t('table.subsystem')}{sortIcon('subsystem')}</th>
                <th className="text-right py-2 px-3 cursor-pointer hover:text-white select-none" onClick={() => toggleSort('total_apis')}>{t('table.apis')}{sortIcon('total_apis')}</th>
                <th className="text-right py-2 px-3 cursor-pointer hover:text-white select-none" onClick={() => toggleSort('well_mapped')}>{t('table.wellMapped')}{sortIcon('well_mapped')}</th>
                <th className="text-right py-2 px-3 cursor-pointer hover:text-white select-none" onClick={() => toggleSort('partially_mapped')}>{t('table.partial')}{sortIcon('partially_mapped')}</th>
                <th className="text-right py-2 px-3 cursor-pointer hover:text-white select-none" onClick={() => toggleSort('gaps')}>{t('table.gaps')}{sortIcon('gaps')}</th>
                <th className="text-right py-2 px-3 cursor-pointer hover:text-white select-none" onClick={() => toggleSort('avg_score')}>{t('table.avgScore')}{sortIcon('avg_score')}</th>
                <th className="text-left py-2 px-3 w-48 cursor-pointer hover:text-white select-none" onClick={() => toggleSort('coverage_pct')}>{t('table.coverage')}{sortIcon('coverage_pct')}</th>
              </tr>
            </thead>
            <tbody>
              {sortedCoverage.map((row) => (
                <tr key={row.subsystem} className="border-b border-gray-800/50 hover:bg-gray-800/30">
                  <td className="py-2 px-3">
                    <Link to={`/subsystem/${encodeURIComponent(row.subsystem)}`} className="text-blue-400 hover:underline">
                      {row.subsystem}
                    </Link>
                  </td>
                  <td className="text-right py-2 px-3">{row.total_apis.toLocaleString()}</td>
                  <td className="text-right py-2 px-3 text-green-400">{row.well_mapped}</td>
                  <td className="text-right py-2 px-3 text-yellow-400">{row.partially_mapped}</td>
                  <td className="text-right py-2 px-3 text-red-400">{row.gaps}</td>
                  <td className="text-right py-2 px-3 font-mono">{row.avg_score}</td>
                  <td className="py-2 px-3">
                    <div className="flex items-center gap-2">
                      <div className="flex-1 h-2 bg-gray-800 rounded-full overflow-hidden">
                        <div
                          className="h-full rounded-full"
                          style={{
                            width: `${row.coverage_pct}%`,
                            backgroundColor: scoreBarColor(row.avg_score),
                          }}
                        />
                      </div>
                      <span className="text-xs text-gray-400 w-10 text-right">{row.coverage_pct}%</span>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {/* Score bucket summary */}
      <div className="bg-gray-900 rounded-xl p-4 border border-gray-800">
        <h2 className="text-lg font-semibold mb-3">{t('dashboard.compatSummary')}</h2>
        <div className="grid grid-cols-1 md:grid-cols-5 gap-3">
          {scoreDist.map((d) => (
            <div key={d.bucket} className="bg-gray-800 rounded-lg p-3 text-center">
              <div className="text-xs text-gray-400 mb-1">{d.bucket}</div>
              <div className="text-xl font-bold">{d.count.toLocaleString()}</div>
              <div className="text-xs text-gray-500">{d.percentage}%</div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}

function StatCard({ label, value, color = 'text-white' }: { label: string; value: string; color?: string }) {
  return (
    <div className="bg-gray-900 border border-gray-800 rounded-xl p-4">
      <div className="text-xs text-gray-500 uppercase tracking-wide">{label}</div>
      <div className={`text-2xl font-bold mt-1 ${color}`}>{value}</div>
    </div>
  );
}
