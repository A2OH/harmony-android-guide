import { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import { getSubsystem } from '../api/client';
import type { SubsystemDetail as SubsystemDetailType } from '../api/client';
import { ScoreBadge } from '../components/ScoreBadge';
import { scoreBarColor } from '../utils/colors';
import { BarChart, Bar, XAxis, YAxis, Tooltip, ResponsiveContainer, Cell } from 'recharts';

const EFFORT_COLORS: Record<string, string> = {
  trivial: '#059669', easy: '#16a34a', moderate: '#ca8a04',
  hard: '#ea580c', rewrite: '#dc2626', impossible: '#7f1d1d',
};

export default function SubsystemDetail() {
  const { name } = useParams();
  const [data, setData] = useState<SubsystemDetailType | null>(null);

  useEffect(() => {
    if (name) getSubsystem(name).then(setData);
  }, [name]);

  if (!data) return <div className="p-8 text-center text-gray-500">Loading...</div>;

  return (
    <div className="max-w-7xl mx-auto px-4 py-6 space-y-6">
      {/* Breadcrumb */}
      <div className="text-sm text-gray-500">
        <Link to="/" className="hover:text-blue-400">Dashboard</Link>
        {' → '}
        <span className="text-white">{data.name}</span>
      </div>

      {/* Header */}
      <div className="bg-gray-900 border border-gray-800 rounded-xl p-6">
        <h1 className="text-2xl font-bold">{data.name}</h1>
        <div className="flex items-center gap-6 mt-3 text-sm">
          <div>
            <span className="text-gray-500">APIs: </span>
            <span className="font-bold">{data.api_count_android.toLocaleString()}</span>
          </div>
          <div>
            <span className="text-gray-500">Avg Score: </span>
            <span className="font-bold">{data.overall_score.toFixed(1)}/10</span>
          </div>
          <div>
            <span className="text-gray-500">Coverage: </span>
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
            <h2 className="text-lg font-semibold mb-3">Score Distribution</h2>
            <ResponsiveContainer width="100%" height={200}>
              <BarChart data={data.score_distribution}>
                <XAxis dataKey="bucket" stroke="#9ca3af" fontSize={11} />
                <YAxis stroke="#9ca3af" fontSize={11} />
                <Tooltip contentStyle={{ background: '#1f2937', border: '1px solid #374151', borderRadius: '8px' }} />
                <Bar dataKey="count" radius={[4, 4, 0, 0]} fill="#3b82f6" />
              </BarChart>
            </ResponsiveContainer>
          </div>
        )}

        {/* Effort Distribution */}
        {data.effort_distribution && data.effort_distribution.length > 0 && (
          <div className="bg-gray-900 rounded-xl p-4 border border-gray-800">
            <h2 className="text-lg font-semibold mb-3">Effort Distribution</h2>
            <ResponsiveContainer width="100%" height={200}>
              <BarChart data={data.effort_distribution} layout="vertical">
                <XAxis type="number" stroke="#9ca3af" fontSize={11} />
                <YAxis type="category" dataKey="effort_level" stroke="#9ca3af" fontSize={11} width={80} />
                <Tooltip contentStyle={{ background: '#1f2937', border: '1px solid #374151', borderRadius: '8px' }} />
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
          <h2 className="font-semibold mb-3">Types ({data.types.length})</h2>
          <div className="overflow-x-auto">
            <table className="w-full text-sm">
              <thead>
                <tr className="text-gray-400 border-b border-gray-800">
                  <th className="text-left py-2 px-3">Type</th>
                  <th className="text-left py-2 px-3">Kind</th>
                  <th className="text-left py-2 px-3">Package</th>
                  <th className="text-right py-2 px-3">APIs</th>
                  <th className="text-right py-2 px-3">Avg Score</th>
                </tr>
              </thead>
              <tbody>
                {data.types.map((t) => (
                  <tr key={t.id} className="border-b border-gray-800/50 hover:bg-gray-800/30">
                    <td className="py-2 px-3 font-mono text-blue-300">{t.name}</td>
                    <td className="py-2 px-3 text-gray-400">{t.kind}</td>
                    <td className="py-2 px-3 text-gray-500 text-xs">{t.package_name}</td>
                    <td className="text-right py-2 px-3">{t.api_count}</td>
                    <td className="text-right py-2 px-3"><ScoreBadge score={t.avg_score} /></td>
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
          <h2 className="font-semibold mb-3">Top Gaps (Lowest Scores)</h2>
          <div className="space-y-1">
            {data.top_gaps.map((gap) => (
              <Link
                key={gap.id}
                to={`/api/${gap.id}`}
                className="block bg-gray-800 rounded-lg px-4 py-2 hover:bg-gray-700 transition"
              >
                <div className="flex items-center gap-3">
                  <ScoreBadge score={gap.compat_score} />
                  <span className="font-mono text-sm text-red-300">{gap.type_name}.{gap.name}</span>
                  <span className="text-xs text-gray-600 ml-auto">{gap.package_name}</span>
                </div>
                <div className="text-xs text-gray-500 font-mono mt-1 truncate">{gap.signature}</div>
              </Link>
            ))}
          </div>
        </div>
      )}
    </div>
  );
}
