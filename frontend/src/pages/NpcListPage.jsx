import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { npcService } from '../services/npcService';

import { useMode } from '../context/ModeContext';

function NpcListPage() {
  const [npcs, setNpcs] = useState([]);
  const [status, setStatus] = useState('loading');
  const { isMaster } = useMode();

  useEffect(() => {
    loadNpcs();
  }, []);

  const loadNpcs = async () => {
    try {
      const data = await npcService.getAll();
      // Ordenar por nivel_maximo descendente, y luego alfabéticamente por nombre
      const sortedData = data.sort((a, b) => {
        if (b.nivel_maximo !== a.nivel_maximo) {
          return b.nivel_maximo - a.nivel_maximo;
        }
        return a.nombre.localeCompare(b.nombre);
      });
      setNpcs(sortedData);
      setStatus('success');
    } catch (error) {
      console.error(error);
      setStatus('error');
    }
  };

  return (
    <div className="min-h-screen bg-neutral-900 text-white p-8 font-sans">
      <div className="max-w-6xl mx-auto">
        <header className="flex justify-between items-center mb-8">
          <div className="flex items-center gap-4">
            <Link to={isMaster ? "/dashboard" : "/"} className="bg-neutral-800 hover:bg-neutral-700 text-neutral-400 hover:text-white p-2 rounded-lg transition-colors border border-white/5">
                <svg xmlns="http://www.w3.org/2000/svg" className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6" />
                </svg>
            </Link>
            <h2 className="text-3xl font-bold text-transparent bg-clip-text bg-gradient-to-r from-purple-400 to-pink-200">
                NPCs
            </h2>
          </div>
        </header>

        {status === 'loading' && (
          <div className="text-center py-12 text-neutral-400 animate-pulse">
            Cargando NPCs...
          </div>
        )}

        {status === 'error' && (
          <div className="p-4 bg-red-900/50 border border-red-500/50 text-red-200 rounded-lg text-center">
            Error al cargar los NPCs.
          </div>
        )}

        {status === 'success' && npcs.length === 0 && (
          <div className="text-center py-12 text-neutral-500 border border-neutral-800 rounded-xl bg-neutral-800/20">
            <p className="text-lg">No hay NPCs registrados.</p>
            <p className="text-sm mt-2">Pide al Máster que importe algunos.</p>
          </div>
        )}

        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
          {npcs.map((npc) => (
            <Link key={npc.npc_id} to={`/npcs/${npc.npc_id}`}>
              <div className="bg-neutral-800 rounded-xl overflow-hidden border border-white/5 hover:border-purple-500/30 transition-all hover:transform hover:-translate-y-1 shadow-lg group h-full">
                <div className="h-48 overflow-hidden bg-neutral-900 relative">
                  {npc.imagen_url ? (
                      <img 
                        src={npc.imagen_url} 
                        alt={npc.nombre} 
                        className="w-full h-full object-cover opacity-80 group-hover:opacity-100 transition-opacity"
                        onError={(e) => {
                            e.target.onerror = null; 
                            e.target.src = 'https://via.placeholder.com/400x200?text=Sin+Imagen';
                        }}
                      />
                  ) : (
                      <div className="w-full h-full flex items-center justify-center text-neutral-600 bg-neutral-900">
                          <span className="text-4xl text-purple-800">?</span>
                      </div>
                  )}
                  <div className="absolute inset-0 bg-gradient-to-t from-neutral-900 via-transparent to-transparent"></div>
                </div>
                
                <div className="p-6 relative">
                  <h3 className="text-xl font-bold text-white mb-1">{npc.nombre}</h3>
                  <span className="text-xs font-semibold px-2 py-1 bg-purple-900/40 text-purple-300 rounded mb-3 inline-block">Nivel Max {npc.nivel_maximo}</span>
                  <p className="text-neutral-400 text-sm line-clamp-3">
                      {npc.descripcion_larga || "Sin descripción."}
                  </p>
                </div>
              </div>
            </Link>
          ))}
        </div>
      </div>
    </div>
  );
}

export default NpcListPage;
