import { Link, useNavigate } from 'react-router-dom';
import { useState } from 'react';
import { useLang } from '../i18n/LanguageContext';

export default function Header() {
  const [query, setQuery] = useState('');
  const navigate = useNavigate();
  const { lang, setLang, t } = useLang();

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault();
    if (query.trim()) {
      navigate(`/search?q=${encodeURIComponent(query.trim())}`);
    }
  };

  return (
    <header className="bg-gray-900 border-b border-gray-800 sticky top-0 z-50">
      <div className="max-w-7xl mx-auto px-4 py-3 flex items-center gap-6">
        <Link to="/" className="text-lg font-bold text-white whitespace-nowrap">
          {t('siteName')}
        </Link>
        <nav className="flex gap-4 text-sm">
          <Link to="/" className="text-gray-400 hover:text-white">{t('nav.dashboard')}</Link>
          <Link to="/browse" className="text-gray-400 hover:text-white">{t('nav.browse')}</Link>
          <Link to="/mappings" className="text-gray-400 hover:text-white">{t('nav.mappings')}</Link>
          <Link to="/gaps" className="text-gray-400 hover:text-white">{t('nav.gaps')}</Link>
          <Link to="/docs" className="text-gray-400 hover:text-white">{t('nav.docs')}</Link>
        </nav>
        <form onSubmit={handleSearch} className="flex-1 max-w-md ml-auto">
          <input
            type="text"
            value={query}
            onChange={(e) => setQuery(e.target.value)}
            placeholder={t('search.placeholder')}
            className="w-full px-3 py-1.5 bg-gray-800 border border-gray-700 rounded-lg text-sm text-gray-200 placeholder-gray-500 focus:outline-none focus:border-blue-500"
          />
        </form>
        <button
          onClick={() => setLang(lang === 'en' ? 'zh' : 'en')}
          className="shrink-0 text-sm text-gray-400 hover:text-white border border-gray-700 px-3 py-1 rounded whitespace-nowrap"
        >
          {lang === 'en' ? '中文' : 'EN'}
        </button>
      </div>
    </header>
  );
}
