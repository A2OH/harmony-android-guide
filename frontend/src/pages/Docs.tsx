import { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import ReactMarkdown from 'react-markdown';
import remarkGfm from 'remark-gfm';
import { useLang } from '../i18n/LanguageContext';

const ANDROID_REVIEWS = [
  { slug: '01-app-framework-review', title: 'App Framework (Activity, Service, Application)', titleZh: '应用框架（Activity、Service、Application）' },
  { slug: '02-content-data-review', title: 'Content & Data (Context, Intent, ContentProvider)', titleZh: '内容与数据（Context、Intent、ContentProvider）' },
  { slug: '03-os-ipc-review', title: 'OS & IPC (Binder, Handler, Looper)', titleZh: '操作系统与 IPC（Binder、Handler、Looper）' },
  { slug: '04-view-ui-framework-review', title: 'View & UI Framework', titleZh: '视图与 UI 框架' },
  { slug: '05-networking-media-review', title: 'Networking & Media', titleZh: '网络与媒体' },
  { slug: '06-system-services-review', title: 'System Services', titleZh: '系统服务' },
  { slug: '07-security-permissions-review', title: 'Security & Permissions', titleZh: '安全与权限' },
  { slug: '08-package-installer-review', title: 'Package & Installer', titleZh: '包管理与安装器' },
  { slug: '09-art-runtime-review', title: 'ART Runtime', titleZh: 'ART 运行时' },
  { slug: '10-bionic-ndk-review', title: 'Bionic & NDK', titleZh: 'Bionic 与 NDK' },
  { slug: '11-builtin-apps-review', title: 'Built-in Apps', titleZh: '内置应用' },
  { slug: '12-build-system-review', title: 'Build System', titleZh: '构建系统' },
];

const ANDROID_API_ENUM = [
  { slug: '00-INDEX', title: 'API Index (Master)', titleZh: 'API 索引（总览）' },
  { slug: 'A-android-core', title: 'A — Android Core', titleZh: 'A — Android 核心' },
  { slug: 'B-android-app', title: 'B — Android App', titleZh: 'B — Android 应用' },
  { slug: 'C-android-content', title: 'C — Android Content', titleZh: 'C — Android 内容' },
  { slug: 'D-android-graphics', title: 'D — Android Graphics', titleZh: 'D — Android 图形' },
  { slug: 'E-android-hardware', title: 'E — Android Hardware', titleZh: 'E — Android 硬件' },
  { slug: 'F-android-media', title: 'F — Android Media', titleZh: 'F — Android 媒体' },
  { slug: 'G-android-net', title: 'G — Android Net', titleZh: 'G — Android 网络' },
  { slug: 'H-android-os-system', title: 'H — Android OS & System', titleZh: 'H — Android 操作系统' },
  { slug: 'I-android-provider-service', title: 'I — Android Provider & Service', titleZh: 'I — Android 提供者与服务' },
  { slug: 'J-android-telephony-telecom', title: 'J — Android Telephony & Telecom', titleZh: 'J — Android 电话与通信' },
  { slug: 'K-android-text-util', title: 'K — Android Text & Util', titleZh: 'K — Android 文本与工具' },
  { slug: 'L-android-view', title: 'L — Android View', titleZh: 'L — Android 视图' },
  { slug: 'M-android-widget', title: 'M — Android Widget', titleZh: 'M — Android 组件' },
  { slug: 'N-android-other', title: 'N — Android Other', titleZh: 'N — Android 其他' },
  { slug: 'O-java-standard', title: 'O — Java Standard Library', titleZh: 'O — Java 标准库' },
  { slug: 'P-javax-org-other', title: 'P — Javax, Org & Other', titleZh: 'P — Javax、Org 与其他' },
];

const OH_REVIEWS = [
  { slug: '00-master-summary', title: 'Master Summary', titleZh: '总体概要' },
  { slug: '01-sdk-js-api-review', title: 'SDK JS API', titleZh: 'SDK JS API' },
  { slug: '02-sdk-c-ndk-review', title: 'SDK C/NDK API', titleZh: 'SDK C/NDK API' },
  { slug: '03-arkui-framework-review', title: 'ArkUI Framework', titleZh: 'ArkUI 框架' },
  { slug: '04-ability-bundle-review', title: 'Ability & Bundle', titleZh: 'Ability 与 Bundle' },
  { slug: '05-security-review', title: 'Security', titleZh: '安全' },
  { slug: '06-communication-review', title: 'Communication', titleZh: '通信' },
  { slug: '07-multimedia-review', title: 'Multimedia', titleZh: '多媒体' },
  { slug: '08-kernel-drivers-review', title: 'Kernel & Drivers', titleZh: '内核与驱动' },
  { slug: '09-distributed-data-review', title: 'Distributed Data', titleZh: '分布式数据' },
  { slug: '10-graphics-window-review', title: 'Graphics & Window', titleZh: '图形与窗口' },
  { slug: '11-arkcompiler-runtime-review', title: 'ArkCompiler Runtime', titleZh: 'ArkCompiler 运行时' },
  { slug: '12-system-services-review', title: 'System Services', titleZh: '系统服务' },
  { slug: '13-builtin-apps-review', title: 'Built-in Apps', titleZh: '内置应用' },
  { slug: '14-build-system-review', title: 'Build System', titleZh: '构建系统' },
];

const OH_API_ENUM = [
  { slug: 'api-count-report', title: 'API Count Report', titleZh: 'API 数量报告' },
  { slug: 'api-enumeration-js-part1', title: 'JS API Enumeration (Part 1)', titleZh: 'JS API 枚举（第一部分）' },
  { slug: 'api-enumeration-js-part2', title: 'JS API Enumeration (Part 2)', titleZh: 'JS API 枚举（第二部分）' },
  { slug: 'api-enumeration-js-part3', title: 'JS API Enumeration (Part 3)', titleZh: 'JS API 枚举（第三部分）' },
  { slug: 'api-enumeration-js-part4-subdirs', title: 'JS API Enumeration (Part 4 — Subdirs)', titleZh: 'JS API 枚举（第四部分 — 子目录）' },
  { slug: 'api-enumeration-ndk-part1', title: 'NDK API Enumeration (Part 1)', titleZh: 'NDK API 枚举（第一部分）' },
  { slug: 'api-enumeration-ndk-part2', title: 'NDK API Enumeration (Part 2)', titleZh: 'NDK API 枚举（第二部分）' },
];

function DocIndex() {
  const { lang, t } = useLang();
  const getTitle = (d: { title: string; titleZh: string }) => lang === 'zh' ? d.titleZh : d.title;

  return (
    <div className="max-w-5xl mx-auto px-4 py-6 space-y-8">
      <h1 className="text-2xl font-bold">{t('docs.title')}</h1>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Android Reviews */}
        <div className="bg-gray-900 border border-gray-800 rounded-xl p-4">
          <h2 className="text-lg font-semibold mb-3 flex items-center gap-2">
            <span className="text-xs font-bold bg-green-900 text-green-300 px-2 py-0.5 rounded">ANDROID</span>
            {t('docs.codeReviews')}
          </h2>
          <div className="space-y-1">
            {ANDROID_REVIEWS.map(d => (
              <Link key={d.slug} to={`/docs/android/${d.slug}`}
                className="block text-sm text-blue-400 hover:text-blue-300 py-1 px-2 rounded hover:bg-gray-800">
                {getTitle(d)}
              </Link>
            ))}
          </div>
        </div>

        {/* OpenHarmony Reviews */}
        <div className="bg-gray-900 border border-gray-800 rounded-xl p-4">
          <h2 className="text-lg font-semibold mb-3 flex items-center gap-2">
            <span className="text-xs font-bold bg-blue-900 text-blue-300 px-2 py-0.5 rounded">OPENHARMONY</span>
            {t('docs.codeReviews')}
          </h2>
          <div className="space-y-1">
            {OH_REVIEWS.map(d => (
              <Link key={d.slug} to={`/docs/openharmony/${d.slug}`}
                className="block text-sm text-blue-400 hover:text-blue-300 py-1 px-2 rounded hover:bg-gray-800">
                {getTitle(d)}
              </Link>
            ))}
          </div>
        </div>
      </div>

      {/* API Enumeration */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div className="bg-gray-900 border border-gray-800 rounded-xl p-4">
          <h2 className="text-lg font-semibold mb-3 flex items-center gap-2">
            <span className="text-xs font-bold bg-green-900 text-green-300 px-2 py-0.5 rounded">ANDROID</span>
            {t('docs.androidApiEnum')}
          </h2>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-1">
            {ANDROID_API_ENUM.map(d => (
              <Link key={d.slug} to={`/docs/android/api-enumeration/${d.slug}`}
                className="block text-sm text-blue-400 hover:text-blue-300 py-1 px-2 rounded hover:bg-gray-800">
                {getTitle(d)}
              </Link>
            ))}
          </div>
        </div>

        <div className="bg-gray-900 border border-gray-800 rounded-xl p-4">
          <h2 className="text-lg font-semibold mb-3 flex items-center gap-2">
            <span className="text-xs font-bold bg-blue-900 text-blue-300 px-2 py-0.5 rounded">OPENHARMONY</span>
            {t('docs.ohApiEnum')}
          </h2>
          <div className="space-y-1">
            {OH_API_ENUM.map(d => (
              <Link key={d.slug} to={`/docs/openharmony/${d.slug}`}
                className="block text-sm text-blue-400 hover:text-blue-300 py-1 px-2 rounded hover:bg-gray-800">
                {getTitle(d)}
              </Link>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}

function DocViewer() {
  const { '*': path } = useParams();
  const [content, setContent] = useState<string | null>(null);
  const [error, setError] = useState(false);
  const { lang, t } = useLang();

  useEffect(() => {
    if (!path) return;
    setContent(null);
    setError(false);
    const base = import.meta.env.BASE_URL || '/';

    if (lang === 'zh') {
      // Try Chinese doc first, fall back to English
      fetch(`${base}docs/zh/${path}.md`)
        .then(r => { if (!r.ok) throw new Error('Not found'); return r.text(); })
        .then(setContent)
        .catch(() => {
          fetch(`${base}docs/${path}.md`)
            .then(r => { if (!r.ok) throw new Error('Not found'); return r.text(); })
            .then(setContent)
            .catch(() => setError(true));
        });
    } else {
      fetch(`${base}docs/${path}.md`)
        .then(r => { if (!r.ok) throw new Error('Not found'); return r.text(); })
        .then(setContent)
        .catch(() => setError(true));
    }
  }, [path, lang]);

  if (error) return <div className="p-8 text-center text-red-400">{t('docs.notFound')}</div>;
  if (content === null) return <div className="p-8 text-center text-gray-500">{t('loading')}</div>;

  return (
    <div className="max-w-5xl mx-auto px-4 py-6">
      <div className="mb-4">
        <Link to="/docs" className="text-sm text-blue-400 hover:underline">{t('docs.backToDocumentation')}</Link>
      </div>
      <article className="prose prose-invert prose-sm max-w-none bg-gray-900 border border-gray-800 rounded-xl p-6 overflow-x-auto">
        <ReactMarkdown remarkPlugins={[remarkGfm]}>{content}</ReactMarkdown>
      </article>
    </div>
  );
}

export default function Docs() {
  const { '*': path } = useParams();
  return path ? <DocViewer /> : <DocIndex />;
}
