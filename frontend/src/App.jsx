import React, { useState } from 'react';
import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom';
import CreatePjPage from './pages/CreatePjPage';

import { useMode } from './context/ModeContext';

function Home() {
  const { isMaster } = useMode();
  const [count, setCount] = useState(0);

  return (
    <div className="min-h-screen bg-neutral-900 text-white flex flex-col items-center justify-center p-4 font-sans selection:bg-jade-500 selection:text-white overflow-hidden relative">
      <div className="absolute inset-0 bg-[radial-gradient(ellipse_at_center,_var(--tw-gradient-stops))] from-jade-900/20 via-neutral-900 to-neutral-900 z-0 pointer-events-none"></div>
      
      <main className="z-10 text-center space-y-8 max-w-2xl w-full">
        <div className="relative group cursor-default">
          <div className="absolute -inset-1 bg-gradient-to-r from-jade-600 to-teal-600 rounded-lg blur opacity-25 group-hover:opacity-75 transition duration-1000 group-hover:duration-200"></div>
          <div className="relative bg-neutral-900 ring-1 ring-white/10 rounded-lg p-8 leading-none flex items-center justify-center space-x-6">
             <h1 className="text-5xl md:text-7xl font-bold bg-clip-text text-transparent bg-gradient-to-r from-jade-400 to-teal-200 tracking-tight">
              Jade Regent
            </h1>
          </div>
        </div>

        <p className="text-xl md:text-2xl text-neutral-400 font-light max-w-lg mx-auto leading-relaxed">
          Sistema de Relaciones para la campaña <span className="text-jade-400 font-medium">El Regente de Jade</span>.
        </p>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-4 pt-8">
           <Link to="/create-pj" className="p-6 rounded-2xl bg-neutral-800/50 hover:bg-neutral-800/80 transition-colors border border-white/5 backdrop-blur-sm group block">
              <h3 className="text-lg font-semibold text-jade-300 mb-2 group-hover:translate-x-1 transition-transform">Crear Personaje</h3>
              <p className="text-neutral-400 text-sm">Comienza tu aventura registrando un nuevo PJ.</p>
           </Link>
           <Link to="/pjs" className="p-6 rounded-2xl bg-neutral-800/50 hover:bg-neutral-800/80 transition-colors border border-white/5 backdrop-blur-sm group block">
              <h3 className="text-lg font-semibold text-teal-300 mb-2 group-hover:translate-x-1 transition-transform">Ver Personajes</h3>
              <p className="text-neutral-400 text-sm">Gestiona tus relaciones con los NPCs clave de la historia.</p>
           </Link>
           <Link to="/npcs" className="p-6 rounded-2xl bg-neutral-800/50 hover:bg-neutral-800/80 transition-colors border border-white/5 backdrop-blur-sm group block">
              <h3 className="text-lg font-semibold text-purple-300 mb-2 group-hover:translate-x-1 transition-transform">Ver NPCs</h3>
              <p className="text-neutral-400 text-sm">Consulta la lista de NPCs disponibles y sus detalles.</p>
           </Link>
           {isMaster && (
               <Link to="/import-npc" className="p-6 rounded-2xl bg-neutral-800/50 hover:bg-neutral-800/80 transition-colors border-2 border-purple-500/20 hover:border-purple-500/50 backdrop-blur-sm group block">
                  <h3 className="text-lg font-semibold text-purple-300 mb-2 group-hover:translate-x-1 transition-transform">Importar NPC</h3>
                  <p className="text-neutral-400 text-sm">Carga nuevos personajes desde archivos JSON.</p>
               </Link>
           )}
        </div>

        <div className="pt-12">
            <button 
              onClick={() => setCount((count) => count + 1)}
              className="px-8 py-3 rounded-full bg-jade-600 hover:bg-jade-500 text-white font-semibold transition-all shadow-[0_0_20px_-5px_rgba(34,197,94,0.3)] hover:shadow-[0_0_30px_-5px_rgba(34,197,94,0.5)] active:scale-95 translate-y-0"
            >
              Explorar (Clicks: {count})
            </button>
        </div>
      </main>

      <footer className="absolute bottom-8 text-neutral-600 text-sm">
        &copy; 2025 Jade Regent Campaign Manager
      </footer>
    </div>
  );
}

import PjListPage from './pages/PjListPage';

import { ModeProvider } from './context/ModeContext';
import MasterToggle from './components/MasterToggle';

import ImportNpcPage from './pages/ImportNpcPage';

import NpcListPage from './pages/NpcListPage';
import NpcDetailPage from './pages/NpcDetailPage';
import NpcAdvantagesTreePage from './pages/NpcAdvantagesTreePage';
import PjDetailPage from './pages/PjDetailPage';

function App() {
  return (
    <ModeProvider>
      <Router>
        <div className="relative min-h-screen bg-neutral-900">
          <MasterToggle />
          <Routes>
            <Route path="/" element={<Home />} />
            <Route path="/create-pj" element={<CreatePjPage />} />
            <Route path="/pjs" element={<PjListPage />} />
            <Route path="/pjs/:id" element={<PjDetailPage />} />
            <Route path="/npcs" element={<NpcListPage />} />
            <Route path="/npcs/:id" element={<NpcDetailPage />} />
            <Route path="/npcs/:id/advantages-tree" element={<NpcAdvantagesTreePage />} />
            <Route path="/import-npc" element={<ImportNpcPage />} />
          </Routes>
        </div>
      </Router>
    </ModeProvider>
  );
}

export default App;
