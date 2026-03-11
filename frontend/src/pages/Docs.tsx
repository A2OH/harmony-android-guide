import { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import ReactMarkdown from 'react-markdown';
import remarkGfm from 'remark-gfm';

const ANDROID_REVIEWS = [
  { slug: '01-app-framework-review', title: 'App Framework (Activity, Service, Application)' },
  { slug: '02-content-data-review', title: 'Content & Data (Context, Intent, ContentProvider)' },
  { slug: '03-os-ipc-review', title: 'OS & IPC (Binder, Handler, Looper)' },
  { slug: '04-view-ui-framework-review', title: 'View & UI Framework' },
  { slug: '05-networking-media-review', title: 'Networking & Media' },
  { slug: '06-system-services-review', title: 'System Services' },
  { slug: '07-security-permissions-review', title: 'Security & Permissions' },
  { slug: '08-package-installer-review', title: 'Package & Installer' },
  { slug: '09-art-runtime-review', title: 'ART Runtime' },
  { slug: '10-bionic-ndk-review', title: 'Bionic & NDK' },
  { slug: '11-builtin-apps-review', title: 'Built-in Apps' },
  { slug: '12-build-system-review', title: 'Build System' },
];

const ANDROID_API_ENUM = [
  { slug: '00-INDEX', title: 'API Index (Master)' },
  { slug: 'A-android-core', title: 'A — Android Core' },
  { slug: 'B-android-app', title: 'B — Android App' },
  { slug: 'C-android-content', title: 'C — Android Content' },
  { slug: 'D-android-graphics', title: 'D — Android Graphics' },
  { slug: 'E-android-hardware', title: 'E — Android Hardware' },
  { slug: 'F-android-media', title: 'F — Android Media' },
  { slug: 'G-android-net', title: 'G — Android Net' },
  { slug: 'H-android-os-system', title: 'H — Android OS & System' },
  { slug: 'I-android-provider-service', title: 'I — Android Provider & Service' },
  { slug: 'J-android-telephony-telecom', title: 'J — Android Telephony & Telecom' },
  { slug: 'K-android-text-util', title: 'K — Android Text & Util' },
  { slug: 'L-android-view', title: 'L — Android View' },
  { slug: 'M-android-widget', title: 'M — Android Widget' },
  { slug: 'N-android-other', title: 'N — Android Other' },
  { slug: 'O-java-standard', title: 'O — Java Standard Library' },
  { slug: 'P-javax-org-other', title: 'P — Javax, Org & Other' },
];

const OH_REVIEWS = [
  { slug: '00-master-summary', title: 'Master Summary' },
  { slug: '01-sdk-js-api-review', title: 'SDK JS API' },
  { slug: '02-sdk-c-ndk-review', title: 'SDK C/NDK API' },
  { slug: '03-arkui-framework-review', title: 'ArkUI Framework' },
  { slug: '04-ability-bundle-review', title: 'Ability & Bundle' },
  { slug: '05-security-review', title: 'Security' },
  { slug: '06-communication-review', title: 'Communication' },
  { slug: '07-multimedia-review', title: 'Multimedia' },
  { slug: '08-kernel-drivers-review', title: 'Kernel & Drivers' },
  { slug: '09-distributed-data-review', title: 'Distributed Data' },
  { slug: '10-graphics-window-review', title: 'Graphics & Window' },
  { slug: '11-arkcompiler-runtime-review', title: 'ArkCompiler Runtime' },
  { slug: '12-system-services-review', title: 'System Services' },
  { slug: '13-builtin-apps-review', title: 'Built-in Apps' },
  { slug: '14-build-system-review', title: 'Build System' },
];

const OH_API_ENUM = [
  { slug: 'api-count-report', title: 'API Count Report' },
  { slug: 'api-enumeration-js-part1', title: 'JS API Enumeration (Part 1)' },
  { slug: 'api-enumeration-js-part2', title: 'JS API Enumeration (Part 2)' },
  { slug: 'api-enumeration-js-part3', title: 'JS API Enumeration (Part 3)' },
  { slug: 'api-enumeration-js-part4-subdirs', title: 'JS API Enumeration (Part 4 — Subdirs)' },
  { slug: 'api-enumeration-ndk-part1', title: 'NDK API Enumeration (Part 1)' },
  { slug: 'api-enumeration-ndk-part2', title: 'NDK API Enumeration (Part 2)' },
];

function DocIndex() {
  return (
    <div className="max-w-5xl mx-auto px-4 py-6 space-y-8">
      <h1 className="text-2xl font-bold">Documentation</h1>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Android Reviews */}
        <div className="bg-gray-900 border border-gray-800 rounded-xl p-4">
          <h2 className="text-lg font-semibold mb-3 flex items-center gap-2">
            <span className="text-xs font-bold bg-green-900 text-green-300 px-2 py-0.5 rounded">ANDROID</span>
            Code Reviews
          </h2>
          <div className="space-y-1">
            {ANDROID_REVIEWS.map(d => (
              <Link key={d.slug} to={`/docs/android/${d.slug}`}
                className="block text-sm text-blue-400 hover:text-blue-300 py-1 px-2 rounded hover:bg-gray-800">
                {d.title}
              </Link>
            ))}
          </div>
        </div>

        {/* OpenHarmony Reviews */}
        <div className="bg-gray-900 border border-gray-800 rounded-xl p-4">
          <h2 className="text-lg font-semibold mb-3 flex items-center gap-2">
            <span className="text-xs font-bold bg-blue-900 text-blue-300 px-2 py-0.5 rounded">OPENHARMONY</span>
            Code Reviews
          </h2>
          <div className="space-y-1">
            {OH_REVIEWS.map(d => (
              <Link key={d.slug} to={`/docs/openharmony/${d.slug}`}
                className="block text-sm text-blue-400 hover:text-blue-300 py-1 px-2 rounded hover:bg-gray-800">
                {d.title}
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
            API Enumeration (56,387 APIs)
          </h2>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-1">
            {ANDROID_API_ENUM.map(d => (
              <Link key={d.slug} to={`/docs/android/api-enumeration/${d.slug}`}
                className="block text-sm text-blue-400 hover:text-blue-300 py-1 px-2 rounded hover:bg-gray-800">
                {d.title}
              </Link>
            ))}
          </div>
        </div>

        <div className="bg-gray-900 border border-gray-800 rounded-xl p-4">
          <h2 className="text-lg font-semibold mb-3 flex items-center gap-2">
            <span className="text-xs font-bold bg-blue-900 text-blue-300 px-2 py-0.5 rounded">OPENHARMONY</span>
            API Enumeration
          </h2>
          <div className="space-y-1">
            {OH_API_ENUM.map(d => (
              <Link key={d.slug} to={`/docs/openharmony/${d.slug}`}
                className="block text-sm text-blue-400 hover:text-blue-300 py-1 px-2 rounded hover:bg-gray-800">
                {d.title}
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

  useEffect(() => {
    if (!path) return;
    setContent(null);
    setError(false);
    const base = import.meta.env.BASE_URL || '/';
    fetch(`${base}docs/${path}.md`)
      .then(r => { if (!r.ok) throw new Error('Not found'); return r.text(); })
      .then(setContent)
      .catch(() => setError(true));
  }, [path]);

  if (error) return <div className="p-8 text-center text-red-400">Document not found.</div>;
  if (content === null) return <div className="p-8 text-center text-gray-500">Loading...</div>;

  return (
    <div className="max-w-5xl mx-auto px-4 py-6">
      <div className="mb-4">
        <Link to="/docs" className="text-sm text-blue-400 hover:underline">← Back to Documentation</Link>
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
