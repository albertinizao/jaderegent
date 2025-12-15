import React from 'react';
import { useMode } from '../context/ModeContext';

const MasterToggle = () => {
  const { isMaster, toggleMode, hasMasterPrivileges } = useMode();

  if (!hasMasterPrivileges) return null;

  return (
    <button
      onClick={toggleMode}
      className={`print:hidden fixed bottom-8 right-8 z-50 px-6 py-3 rounded-full font-bold shadow-lg transition-all transform hover:scale-105 ${
        isMaster
          ? 'bg-purple-600 hover:bg-purple-500 text-white shadow-purple-900/50 ring-2 ring-purple-400'
          : 'bg-neutral-800 hover:bg-neutral-700 text-neutral-400 hover:text-white border border-white/10'
      }`}
    >
      {isMaster ? 'Modo Máster Activo' : 'Modo Jugador'}
    </button>
  );
};

export default MasterToggle;
