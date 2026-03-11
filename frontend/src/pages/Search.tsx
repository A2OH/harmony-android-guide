import { useEffect, useState } from 'react';
import { Link, useSearchParams } from 'react-router-dom';
import { searchApis } from '../api/client';
import type { SearchResponse } from '../api/client';
import { useLang } from '../i18n/LanguageContext';

export default function Search() {
  const [searchParams] = useSearchParams();
  const query = searchParams.get('q') || '';
  const [results, setResults] = useState<SearchResponse | null>(null);
  const [loading, setLoading] = useState(false);
  const { t } = useLang();

  useEffect(() => {
    if (!query) return;
    setLoading(true);
    const params = new URLSearchParams();
    params.set('q', query);
    params.set('limit', '100');
    searchApis(params.toString())
      .then(setResults)
      .finally(() => setLoading(false));
  }, [query]);

  if (!query) {
    return (
      <div className="max-w-5xl mx-auto px-4 py-12 text-center text-gray-500">
        {t('search.emptyPrompt')}
      </div>
    );
  }

  return (
    <div className="max-w-5xl mx-auto px-4 py-6 space-y-4">
      <div>
        <h1 className="text-2xl font-bold">{t('search.title')}</h1>
        <p className="text-sm text-gray-400 mt-1">
          {loading ? t('search.searching') : results ? t('search.resultsFor', { count: results.total, query: results.query }) : ''}
        </p>
      </div>

      {results && results.results.length === 0 && (
        <div className="bg-gray-900 border border-gray-800 rounded-xl p-8 text-center text-gray-500">
          {t('search.noResults', { query })}
        </div>
      )}

      {results && results.results.length > 0 && (
        <div className="space-y-1">
          {results.results.map((item) => (
            <Link
              key={`${item.platform}-${item.id}`}
              to={item.platform === 'android' ? `/api/${item.id}` : '#'}
              className="block bg-gray-900 border border-gray-800 rounded-lg px-4 py-2 hover:border-gray-600 transition"
            >
              <div className="flex items-center gap-3">
                <span className={`text-xs font-bold px-2 py-0.5 rounded ${
                  item.platform === 'android' ? 'bg-green-900 text-green-300' : 'bg-blue-900 text-blue-300'
                }`}>
                  {item.platform === 'android' ? 'ANDROID' : 'OH'}
                </span>
                <span className="text-xs text-gray-500 bg-gray-800 px-1.5 py-0.5 rounded">{item.kind}</span>
                <span className="font-mono text-sm text-blue-300">{item.name}</span>
                {item.type_name && <span className="text-xs text-gray-600">{item.type_name}</span>}
                <span className="text-xs text-gray-600 ml-auto">{item.package_or_module}</span>
                {item.score != null && (
                  <span className={`text-xs font-bold px-2 py-0.5 rounded ${
                    item.score >= 7 ? 'bg-green-900 text-green-300' :
                    item.score >= 5 ? 'bg-yellow-900 text-yellow-300' :
                    item.score >= 3 ? 'bg-orange-900 text-orange-300' :
                    'bg-red-900 text-red-300'
                  }`}>
                    {item.score.toFixed(1)}
                  </span>
                )}
              </div>
              <div className="text-xs text-gray-500 font-mono mt-1 truncate">{item.signature}</div>
            </Link>
          ))}
        </div>
      )}
    </div>
  );
}
