import { BrowserRouter, Routes, Route } from 'react-router-dom';
import Header from './components/Header';
import Dashboard from './pages/Dashboard';
import Browse from './pages/Browse';
import ApiDetail from './pages/ApiDetail';
import Mappings from './pages/Mappings';
import Gaps from './pages/Gaps';
import Search from './pages/Search';
import SubsystemDetail from './pages/SubsystemDetail';
import Docs from './pages/Docs';

export default function App() {
  return (
    <BrowserRouter>
      <div className="min-h-screen bg-black text-white">
        <Header />
        <Routes>
          <Route path="/" element={<Dashboard />} />
          <Route path="/browse" element={<Browse />} />
          <Route path="/api/:id" element={<ApiDetail />} />
          <Route path="/mappings" element={<Mappings />} />
          <Route path="/gaps" element={<Gaps />} />
          <Route path="/search" element={<Search />} />
          <Route path="/subsystem/:name" element={<SubsystemDetail />} />
          <Route path="/docs" element={<Docs />} />
          <Route path="/docs/*" element={<Docs />} />
        </Routes>
      </div>
    </BrowserRouter>
  );
}
