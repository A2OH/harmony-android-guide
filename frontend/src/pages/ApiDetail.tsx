import { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import { getAndroidApi } from '../api/client';
import { ScoreBadge, EffortBadge, ScoreBar } from '../components/ScoreBadge';
import { useLang } from '../i18n/LanguageContext';

export default function ApiDetail() {
  const { id } = useParams();
  const [data, setData] = useState<any>(null);
  const { t } = useLang();

  useEffect(() => {
    if (id) getAndroidApi(Number(id)).then(setData);
  }, [id]);

  if (!data) return <div className="p-8 text-center text-gray-500">{t('loading')}</div>;

  const mapping = data.mappings?.[0];

  return (
    <div className="max-w-5xl mx-auto px-4 py-6 space-y-6">
      {/* Breadcrumb */}
      <div className="text-sm text-gray-500">
        <Link to="/browse" className="hover:text-blue-400">{t('nav.browse')}</Link>
        {' → '}
        <Link to={`/browse?package=${data.package_name}`} className="hover:text-blue-400">{data.package_name}</Link>
        {' → '}
        <span className="text-gray-300">{data.type_name}</span>
        {' → '}
        <span className="text-white">{data.name}</span>
      </div>

      {/* Header */}
      <div className="bg-gray-900 border border-gray-800 rounded-xl p-6">
        <div className="flex items-start justify-between">
          <div>
            <h1 className="text-xl font-bold font-mono">{data.type_name}.{data.name}</h1>
            <div className="flex items-center gap-3 mt-2">
              <span className="text-xs bg-gray-800 text-gray-400 px-2 py-0.5 rounded">{data.kind}</span>
              <span className="text-xs text-gray-500">{data.package_name}</span>
              {data.subsystem && <span className="text-xs bg-gray-800 text-gray-400 px-2 py-0.5 rounded">{data.subsystem}</span>}
              {data.is_deprecated && <span className="text-xs bg-yellow-900 text-yellow-300 px-2 py-0.5 rounded">{t('apiDetail.deprecated')}</span>}
            </div>
          </div>
          <div className="text-right">
            <ScoreBar score={data.compat_score} />
            <div className="mt-1"><EffortBadge effort={data.effort_level} /></div>
          </div>
        </div>
        <div className="mt-4 bg-gray-800 rounded-lg p-3 font-mono text-sm text-green-300 overflow-x-auto">
          {data.signature}
        </div>
      </div>

      {/* Side-by-side comparison */}
      {mapping && (
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          {/* Android side */}
          <div className="bg-gray-900 border border-gray-800 rounded-xl p-4">
            <div className="flex items-center gap-2 mb-3">
              <span className="text-xs font-bold bg-green-900 text-green-300 px-2 py-0.5 rounded">ANDROID</span>
              <span className="text-sm text-gray-400">{data.package_name}</span>
            </div>
            <div className="space-y-2">
              <div>
                <div className="text-xs text-gray-500">{t('apiDetail.class')}</div>
                <div className="text-sm">{data.type_name}</div>
              </div>
              <div>
                <div className="text-xs text-gray-500">{t('apiDetail.signature')}</div>
                <div className="font-mono text-xs text-green-300 bg-gray-800 p-2 rounded overflow-x-auto">
                  {data.signature}
                </div>
              </div>
              {data.return_type && (
                <div>
                  <div className="text-xs text-gray-500">{t('apiDetail.returns')}</div>
                  <div className="text-sm font-mono">{data.return_type}</div>
                </div>
              )}
            </div>
          </div>

          {/* OH side */}
          <div className="bg-gray-900 border border-gray-800 rounded-xl p-4">
            <div className="flex items-center gap-2 mb-3">
              <span className="text-xs font-bold bg-blue-900 text-blue-300 px-2 py-0.5 rounded">OPENHARMONY</span>
              {mapping.oh_module && <span className="text-sm text-gray-400">{mapping.oh_module}</span>}
            </div>
            {mapping.oh_api_name ? (
              <div className="space-y-2">
                {mapping.oh_type_name && (
                  <div>
                    <div className="text-xs text-gray-500">{t('apiDetail.type')}</div>
                    <div className="text-sm">{mapping.oh_type_name}</div>
                  </div>
                )}
                <div>
                  <div className="text-xs text-gray-500">{t('apiDetail.signature')}</div>
                  <div className="font-mono text-xs text-blue-300 bg-gray-800 p-2 rounded overflow-x-auto">
                    {mapping.oh_signature}
                  </div>
                </div>
              </div>
            ) : (
              <div className="text-gray-500 text-sm italic">{t('apiDetail.noEquivalent')}</div>
            )}
          </div>
        </div>
      )}

      {/* Mapping details */}
      {mapping && (
        <div className="bg-gray-900 border border-gray-800 rounded-xl p-4 space-y-3">
          <h2 className="font-semibold">{t('apiDetail.mappingDetails')}</h2>
          <div className="grid grid-cols-2 md:grid-cols-4 gap-4 text-sm">
            <div>
              <div className="text-xs text-gray-500">{t('apiDetail.score')}</div>
              <ScoreBadge score={mapping.score} />
            </div>
            <div>
              <div className="text-xs text-gray-500">{t('apiDetail.mappingType')}</div>
              <div>{mapping.mapping_type}</div>
            </div>
            <div>
              <div className="text-xs text-gray-500">{t('apiDetail.effort')}</div>
              <EffortBadge effort={mapping.effort_level} />
            </div>
            <div>
              <div className="text-xs text-gray-500">{t('apiDetail.flags')}</div>
              <div className="flex gap-1">
                {mapping.paradigm_shift ? <span className="text-xs bg-purple-900 text-purple-300 px-1.5 py-0.5 rounded">{t('apiDetail.paradigmShift')}</span> : null}
                {mapping.needs_ui_rewrite ? <span className="text-xs bg-red-900 text-red-300 px-1.5 py-0.5 rounded">{t('apiDetail.uiRewrite')}</span> : null}
              </div>
            </div>
          </div>
          {mapping.gap_description && (
            <div>
              <div className="text-xs text-gray-500 mb-1">{t('apiDetail.gapAnalysis')}</div>
              <div className="text-sm text-gray-300 bg-gray-800 p-3 rounded">{mapping.gap_description}</div>
            </div>
          )}
          {mapping.migration_guide && (
            <div>
              <div className="text-xs text-gray-500 mb-1">{t('apiDetail.migrationGuide')}</div>
              <div className="text-sm text-gray-300 bg-gray-800 p-3 rounded whitespace-pre-wrap">{mapping.migration_guide}</div>
            </div>
          )}
        </div>
      )}

      {/* All mappings for this API */}
      {data.mappings?.length > 1 && (
        <div className="bg-gray-900 border border-gray-800 rounded-xl p-4">
          <h2 className="font-semibold mb-3">{t('apiDetail.alternativeMappings')} ({data.mappings.length})</h2>
          <div className="space-y-2">
            {data.mappings.map((m: any, i: number) => (
              <div key={i} className="flex items-center gap-3 text-sm bg-gray-800 rounded px-3 py-2">
                <ScoreBadge score={m.score} />
                <span className="font-mono text-blue-300">{m.oh_api_name || '—'}</span>
                <span className="text-gray-500">{m.oh_module}</span>
                <EffortBadge effort={m.effort_level} />
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
}
