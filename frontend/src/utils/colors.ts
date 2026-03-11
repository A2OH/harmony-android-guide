export function scoreColor(score: number | null | undefined): string {
  if (score == null) return 'bg-gray-700 text-gray-300';
  if (score >= 9) return 'bg-emerald-600 text-white';
  if (score >= 7) return 'bg-green-600 text-white';
  if (score >= 5) return 'bg-yellow-600 text-white';
  if (score >= 3) return 'bg-orange-600 text-white';
  return 'bg-red-600 text-white';
}

export function scoreBarColor(score: number | null | undefined): string {
  if (score == null) return '#6b7280';
  if (score >= 9) return '#059669';
  if (score >= 7) return '#16a34a';
  if (score >= 5) return '#ca8a04';
  if (score >= 3) return '#ea580c';
  return '#dc2626';
}

export function effortColor(effort: string | null | undefined): string {
  switch (effort) {
    case 'trivial': return 'bg-emerald-700 text-emerald-100';
    case 'easy': return 'bg-green-700 text-green-100';
    case 'moderate': return 'bg-yellow-700 text-yellow-100';
    case 'hard': return 'bg-orange-700 text-orange-100';
    case 'rewrite': return 'bg-red-700 text-red-100';
    case 'impossible': return 'bg-red-900 text-red-200';
    default: return 'bg-gray-700 text-gray-300';
  }
}
