import React, { useState } from 'react';
import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom';
import CreatePjPage from './pages/CreatePjPage';

import { useMode } from './context/ModeContext';



import RoleSelectionPage from './pages/RoleSelectionPage';
import DashboardPage from './pages/DashboardPage';

import PjListPage from './pages/PjListPage';

import { ModeProvider } from './context/ModeContext';
import MasterToggle from './components/MasterToggle';

import ImportNpcPage from './pages/ImportNpcPage';

import NpcListPage from './pages/NpcListPage';
import NpcDetailPage from './pages/NpcDetailPage';
import NpcAdvantagesTreePage from './pages/NpcAdvantagesTreePage';
import PjDetailPage from './pages/PjDetailPage';
import RelationsMatrixPage from './pages/RelationsMatrixPage';

function App() {
  return (
    <ModeProvider>
      <Router>
        <div className="relative min-h-screen bg-neutral-900">
          <MasterToggle />
          <Routes>
            <Route path="/" element={<RoleSelectionPage />} />
            <Route path="/dashboard" element={<DashboardPage />} />
            <Route path="/create-pj" element={<CreatePjPage />} />
            <Route path="/pjs" element={<PjListPage />} />
            <Route path="/pjs/:id" element={<PjDetailPage />} />
            <Route path="/npcs" element={<NpcListPage />} />
            <Route path="/npcs/:id" element={<NpcDetailPage />} />
            <Route path="/npcs/:id/advantages-tree" element={<NpcAdvantagesTreePage />} />
            <Route path="/import-npc" element={<ImportNpcPage />} />
            <Route path="/relations-matrix" element={<RelationsMatrixPage />} />
          </Routes>
        </div>
      </Router>
    </ModeProvider>
  );
}

export default App;
