import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { pjService } from '../services/pjService';
import { useMode } from '../context/ModeContext';

function PjListPage() {
  const [pjs, setPjs] = useState([]);
  const [status, setStatus] = useState('loading');
  const { isMaster } = useMode();

  useEffect(() => {
    loadPjs();
  }, []);

  const loadPjs = async () => {
    try {
      const data = await pjService.getAll();
      setPjs(data);
      setStatus('success');
    } catch (error) {
      console.error(error);
      setStatus('error');
    }
  };

  const handleDelete = async (e, pjId) => {
    e.preventDefault(); // Prevent navigation
    if (window.confirm('¿Eliminar personaje?')) {
        try {
            await pjService.delete(pjId);
            loadPjs(); // Reload list
        } catch (error) {
            console.error("Error al eliminar", error);
        }
    }
  };

  return (
    <div className="min-h-screen bg-neutral-900 text-white p-8 font-sans">
      <div className="max-w-6xl mx-auto">
        <header className="flex justify-between items-center mb-8">
          <div className="flex items-center gap-4">
            <Link to="/" className="bg-neutral-800 hover:bg-neutral-700 text-neutral-400 hover:text-white p-2 rounded-lg transition-colors border border-white/5">
                <svg xmlns="http://www.w3.org/2000/svg" className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6" />
                </svg>
            </Link>
            <h2 className="text-3xl font-bold text-transparent bg-clip-text bg-gradient-to-r from-jade-400 to-teal-200">
                Personajes
            </h2>
          </div>

          <Link 
            to="/create-pj" 
            className="bg-jade-600 hover:bg-jade-500 text-white font-bold py-2 px-6 rounded-lg transition-colors shadow-lg shadow-jade-900/20"
          >
            + Crear Nuevo
          </Link>
        </header>

        {status === 'loading' && (
          <div className="text-center py-12 text-neutral-400 animate-pulse">
            Cargando personajes...
          </div>
        )}

        {status === 'error' && (
          <div className="p-4 bg-red-900/50 border border-red-500/50 text-red-200 rounded-lg text-center">
            Error al cargar los personajes.
          </div>
        )}

        {status === 'success' && pjs.length === 0 && (
          <div className="text-center py-12 text-neutral-500 border border-neutral-800 rounded-xl bg-neutral-800/20">
            <p className="text-lg">No hay personajes registrados.</p>
            <p className="text-sm mt-2">¡Crea el primero para comenzar tu aventura!</p>
          </div>
        )}

        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
          {pjs.map((pj) => (
            <Link 
              key={pj.pj_id} 
              to={`/pjs/${pj.pj_id}`}
              className="bg-neutral-800 rounded-xl overflow-hidden border border-white/5 hover:border-jade-500/30 transition-all hover:transform hover:-translate-y-1 shadow-lg group block"
            >
              <div className="h-48 overflow-hidden bg-neutral-900 relative">
                 {pj.imagen_url ? (
                    <img 
                      src={pj.imagen_url} 
                      alt={pj.nombre_display} 
                      className="w-full h-full object-cover opacity-80 group-hover:opacity-100 transition-opacity"
                      onError={(e) => {
                          e.target.onerror = null; 
                          e.target.src = 'https://via.placeholder.com/400x200?text=Sin+Imagen';
                      }}
                    />
                 ) : (
                    <div className="w-full h-full flex items-center justify-center text-neutral-600 bg-neutral-900">
                        <span className="text-4xl text-jade-800">?</span>
                    </div>
                 )}
                 <div className="absolute inset-0 bg-gradient-to-t from-neutral-900 via-transparent to-transparent"></div>
              </div>
              
              <div className="p-6 relative">
                 <div className="flex justify-between items-start mb-2">
                    <h3 className="text-xl font-bold text-white mb-0">{pj.nombre_display}</h3>
                    {isMaster && (
                        <button 
                            onClick={(e) => handleDelete(e, pj.pj_id)}
                            className="text-red-400 hover:text-red-300 p-1 hover:bg-red-900/30 rounded z-10 relative"
                            title="Eliminar"
                        >
                            <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                            </svg>
                        </button>
                    )}
                 </div>
                 <p className="text-neutral-400 text-sm line-clamp-3">
                    {pj.nota_opcional || "Sin descripción."}
                 </p>
              </div>
            </Link>
          ))}
        </div>
      </div>
    </div>
  );
}

export default PjListPage;
