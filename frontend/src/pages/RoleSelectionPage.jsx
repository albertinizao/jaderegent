import React from 'react';
import { useNavigate } from 'react-router-dom';
import { useMode } from '../context/ModeContext';

function RoleSelectionPage() {
  const navigate = useNavigate();
  const { setIsMaster } = useMode();

  const handleSelectMaster = () => {
    localStorage.removeItem('jade_regent_player_pj_id');
    setIsMaster(true);
    navigate('/dashboard');
  };

  const handleSelectPlayer = () => {
    localStorage.removeItem('jade_regent_player_pj_id');
    setIsMaster(false);
    navigate('/pjs');
  };

  return (
    <div className="min-h-screen bg-neutral-900 text-white flex flex-col items-center justify-center p-4 font-sans relative overflow-hidden">
      <div className="absolute inset-0 bg-[radial-gradient(ellipse_at_center,_var(--tw-gradient-stops))] from-jade-900/20 via-neutral-900 to-neutral-900 z-0 pointer-events-none"></div>
      
      <div className="z-10 text-center space-y-12 max-w-4xl w-full">
        <div>
            <h1 className="text-5xl md:text-7xl font-bold bg-clip-text text-transparent bg-gradient-to-r from-jade-400 to-teal-200 tracking-tight mb-4">
              Jade Regent
            </h1>
            <p className="text-xl text-neutral-400 font-light">
              Selecciona tu rol para comenzar
            </p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-8 max-w-2xl mx-auto">
          <button 
            onClick={handleSelectMaster}
            className="group relative bg-neutral-800/50 hover:bg-neutral-800 border border-white/5 hover:border-purple-500/50 rounded-2xl p-8 transition-all hover:-translate-y-1 hover:shadow-2xl hover:shadow-purple-900/20"
          >
             <div className="absolute inset-0 bg-gradient-to-br from-purple-500/10 to-transparent opacity-0 group-hover:opacity-100 rounded-2xl transition-opacity"></div>
             <div className="relative z-10">
                <div className="w-16 h-16 mx-auto mb-6 bg-purple-900/30 rounded-xl flex items-center justify-center group-hover:scale-110 transition-transform">
                    <svg xmlns="http://www.w3.org/2000/svg" className="h-8 w-8 text-purple-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19.428 15.428a2 2 0 00-1.022-.547l-2.384-.477a6 6 0 00-3.86.517l-.318.158a6 6 0 01-3.86.517L6.05 15.21a2 2 0 00-1.806.547M8 4h8l-1 1v5.172a2 2 0 00.586 1.414l5 5c1.26 1.26.367 3.414-1.415 3.414H4.828c-1.782 0-2.674-2.154-1.414-3.414l5-5A2 2 0 009 10.172V5L8 4z" />
                    </svg>
                </div>
                <h2 className="text-2xl font-bold text-white mb-2 group-hover:text-purple-300 transition-colors">Soy Máster</h2>
                <p className="text-neutral-400 text-sm">Gestiona la campaña, NPCs y relaciones.</p>
             </div>
          </button>

          <button 
            onClick={handleSelectPlayer}
            className="group relative bg-neutral-800/50 hover:bg-neutral-800 border border-white/5 hover:border-jade-500/50 rounded-2xl p-8 transition-all hover:-translate-y-1 hover:shadow-2xl hover:shadow-jade-900/20"
          >
             <div className="absolute inset-0 bg-gradient-to-br from-jade-500/10 to-transparent opacity-0 group-hover:opacity-100 rounded-2xl transition-opacity"></div>
             <div className="relative z-10">
                <div className="w-16 h-16 mx-auto mb-6 bg-jade-900/30 rounded-xl flex items-center justify-center group-hover:scale-110 transition-transform">
                    <svg xmlns="http://www.w3.org/2000/svg" className="h-8 w-8 text-jade-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
                    </svg>
                </div>
                <h2 className="text-2xl font-bold text-white mb-2 group-hover:text-jade-300 transition-colors">Soy Jugador</h2>
                <p className="text-neutral-400 text-sm">Consulta tu personaje y sus relaciones.</p>
             </div>
          </button>
        </div>
      </div>
      
      <footer className="absolute bottom-8 text-neutral-600 text-sm">
        &copy; 2025 Jade Regent Campaign Manager
      </footer>
    </div>
  );
}

export default RoleSelectionPage;
