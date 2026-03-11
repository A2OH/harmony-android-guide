import { scoreColor, effortColor } from '../utils/colors';

export function ScoreBadge({ score }: { score: number | null | undefined }) {
  const display = score != null ? score.toFixed(1) : '—';
  return (
    <span className={`inline-block px-2 py-0.5 rounded text-xs font-bold ${scoreColor(score)}`}>
      {display}
    </span>
  );
}

export function EffortBadge({ effort }: { effort: string | null | undefined }) {
  return (
    <span className={`inline-block px-2 py-0.5 rounded text-xs font-medium ${effortColor(effort)}`}>
      {effort || 'unknown'}
    </span>
  );
}

export function ScoreBar({ score }: { score: number | null | undefined }) {
  const pct = ((score || 0) / 10) * 100;
  return (
    <div className="flex items-center gap-2">
      <div className="w-20 h-2 bg-gray-800 rounded-full overflow-hidden">
        <div
          className={`h-full rounded-full ${score != null && score >= 7 ? 'bg-green-500' : score != null && score >= 5 ? 'bg-yellow-500' : score != null && score >= 3 ? 'bg-orange-500' : 'bg-red-500'}`}
          style={{ width: `${pct}%` }}
        />
      </div>
      <ScoreBadge score={score} />
    </div>
  );
}
