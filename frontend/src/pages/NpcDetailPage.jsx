import React, { useEffect, useState } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import { npcService } from '../services/npcService';
import { useMode } from '../context/ModeContext';

function NpcDetailPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { isMaster } = useMode();
  const [npcData, setNpcData] = useState(null);
  const [status, setStatus] = useState('loading');
  const [selectedVentaja, setSelectedVentaja] = useState(null);

  useEffect(() => {
    loadNpcDetail();
  }, [id]);

  const openVentajaModal = (ventajaId) => {
    const ventaja = npcData.ventajas.find(v => v.ventajaId === ventajaId);
    if (ventaja) {
      setSelectedVentaja(ventaja);
    }
  };

  const closeVentajaModal = () => {
    setSelectedVentaja(null);
  };

  const loadNpcDetail = async () => {
    try {
      const data = await npcService.getById(id);
      setNpcData(data);
      setStatus('success');
    } catch (error) {
      console.error(error);
      setStatus('error');
    }
  };

  const handleDelete = async () => {
    if (window.confirm('¿Estás seguro de que quieres eliminar este NPC? Esta acción no se puede deshacer.')) {
      try {
        await npcService.delete(id);
        navigate('/npcs');
      } catch (error) {
        console.error("Error al eliminar NPC", error);
        alert("Error al eliminar NPC");
      }
    }
  };

  if (status === 'loading') {
    return <div className="min-h-screen bg-neutral-900 text-white flex items-center justify-center">Cargando...</div>;
  }

  if (status === 'error' || !npcData) {
    return (
        <div className="min-h-screen bg-neutral-900 text-white flex flex-col items-center justify-center p-4">
            <h2 className="text-2xl font-bold text-red-500 mb-4">Error</h2>
            <p className="text-neutral-400 mb-6">No se pudo cargar el NPC.</p>
            <Link to="/npcs" className="text-purple-400 hover:text-purple-300">Volver a la lista</Link>
        </div>
    );
  }

  const { npc, ventajas } = npcData;

  return (
    <div className="min-h-screen bg-neutral-900 text-white p-4 md:p-8 font-sans">
      <div className="max-w-5xl mx-auto">
        <header className="mb-8 flex justify-between items-center">
            <Link to="/npcs" className="inline-flex items-center text-neutral-400 hover:text-white transition-colors">
                <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M10 19l-7-7m0 0l7-7m-7 7h18" />
                </svg>
                Volver
            </Link>
            {isMaster && (
              <button 
                onClick={handleDelete}
                className="bg-red-500/20 hover:bg-red-500/40 text-red-300 border border-red-500/50 px-4 py-2 rounded-lg transition-colors flex items-center gap-2"
              >
                <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                </svg>
                Eliminar NPC
              </button>
            )}
        </header>

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
            {/* NPC Card / Basic Info */}
            <div className="lg:col-span-1">
                <div className="bg-neutral-800 rounded-2xl overflow-hidden border border-white/5 sticky top-8">
                    <div className="h-64 sm:h-80 bg-neutral-900 relative">
                        {npc.imagenUrl ? (
                            <img 
                                src={npc.imagenUrl} 
                                alt={npc.nombre} 
                                className="w-full h-full object-cover"
                                onError={(e) => { e.target.src = 'https://via.placeholder.com/400x500?text=Sin+Imagen'; }}
                            />
                        ) : (
                            <div className="w-full h-full flex items-center justify-center text-neutral-600">
                                <span className="text-6xl text-purple-800">?</span>
                            </div>
                        )}
                         <div className="absolute inset-x-0 bottom-0 bg-gradient-to-t from-neutral-900 to-transparent h-24"></div>
                         <div className="absolute bottom-4 left-4 right-4">
                             <h1 className="text-3xl font-bold text-white mb-1">{npc.nombre}</h1>
                             <div className="inline-block px-3 py-1 bg-purple-600 text-white text-xs font-bold rounded-full uppercase tracking-wider">
                                Nivel Máximo: {npc.nivelMaximo}
                             </div>
                         </div>
                    </div>
                    <div className="p-6">
                        <h3 className="text-neutral-400 text-sm font-bold uppercase tracking-wider mb-2">Descripción</h3>
                        <p className="text-neutral-300 leading-relaxed text-sm">
                            {npc.descripcionLarga}
                        </p>
                    </div>
                </div>
            </div>

            {/* Advantages List */}
            <div className="lg:col-span-2">
                <h2 className="text-2xl font-bold mb-6 flex items-center">
                    <span className="bg-clip-text text-transparent bg-gradient-to-r from-purple-400 to-pink-300">
                        Árbol de Ventajas
                    </span>
                    <span className="ml-3 px-2 py-1 bg-neutral-800 text-xs text-neutral-400 rounded-full border border-white/10">
                        {ventajas.length}
                    </span>
                </h2>

                <div className="space-y-4">
                    {ventajas.map((v) => (
                        <div key={v.ventajaId} className="bg-neutral-800/60 rounded-xl p-6 border border-white/5 hover:border-purple-500/20 transition-all">
                            <div className="flex justify-between items-start mb-2">
                                <h3 className="text-lg font-bold text-white">{v.nombre}</h3>
                                <div className="flex flex-col items-end">
                                    <span className={`text-xs font-bold px-2 py-1 rounded ${
                                        v.minNivelRelacion <= 3 ? 'bg-green-900/40 text-green-300' : 
                                        v.minNivelRelacion <= 6 ? 'bg-yellow-900/40 text-yellow-300' : 'bg-red-900/40 text-red-300'
                                    }`}>
                                        Nivel {v.minNivelRelacion}
                                    </span>
                                </div>
                            </div>
                            <p className="text-neutral-400 text-sm mb-4">
                                {v.descripcionLarga}
                            </p>
                            {v.prerequisitos && v.prerequisitos.length > 0 && (
                                <div className="mt-4 pt-3 border-t border-white/5">
                                    <p className="text-xs text-neutral-500 mb-2 uppercase tracking-wide">
                                        Prerrequisitos {(v.prerequisitosOperator || 'AND') === 'OR' ? 
                                            <span className="text-amber-400 font-bold">(cualquiera)</span> : 
                                            <span className="text-purple-400 font-bold">(todos)</span>
                                        }
                                    </p>
                                    <div className="flex flex-wrap gap-2">
                                        {v.prerequisitos.map(pre => {
                                            const prereqVentaja = ventajas.find(ventaja => ventaja.ventajaId === pre);
                                            const displayName = prereqVentaja ? prereqVentaja.nombre : pre;
                                            const isOrLogic = (v.prerequisitosOperator || 'AND') === 'OR';
                                            return (
                                                <button
                                                    key={pre}
                                                    onClick={() => openVentajaModal(pre)}
                                                    className={`text-xs px-2 py-1 rounded border hover:scale-105 transition-all cursor-pointer ${
                                                        isOrLogic 
                                                            ? 'text-amber-300 bg-amber-900/20 border-amber-500/30 hover:bg-amber-900/40' 
                                                            : 'text-purple-300 bg-purple-900/20 border-purple-500/30 hover:bg-purple-900/40'
                                                    }`}
                                                >
                                                    {displayName}
                                                </button>
                                            );
                                        })}
                                    </div>
                                </div>
                            )}
                        </div>
                    ))}
                    
                    {ventajas.length === 0 && (
                        <div className="text-center py-12 border-2 border-dashed border-neutral-800 rounded-xl">
                            <p className="text-neutral-500">Este NPC no tiene ventajas registradas.</p>
                        </div>
                    )}
                </div>
            </div>
        </div>

        {/* Modal for Ventaja Details */}
        {selectedVentaja && (
            <div className="fixed inset-0 bg-black/80 flex items-center justify-center z-50 p-4" onClick={closeVentajaModal}>
                <div className="bg-neutral-800 rounded-2xl max-w-2xl w-full border border-white/10 shadow-2xl" onClick={(e) => e.stopPropagation()}>
                    <div className="p-6 border-b border-white/10 flex justify-between items-start">
                        <div>
                            <h3 className="text-2xl font-bold text-white mb-2">{selectedVentaja.nombre}</h3>
                            <span className={`text-xs font-bold px-3 py-1 rounded ${
                                selectedVentaja.minNivelRelacion <= 3 ? 'bg-green-900/40 text-green-300' : 
                                selectedVentaja.minNivelRelacion <= 6 ? 'bg-yellow-900/40 text-yellow-300' : 'bg-red-900/40 text-red-300'
                            }`}>
                                Nivel {selectedVentaja.minNivelRelacion}
                            </span>
                        </div>
                        <button 
                            onClick={closeVentajaModal}
                            className="text-neutral-400 hover:text-white transition-colors p-2 hover:bg-neutral-700 rounded-lg"
                        >
                            <svg xmlns="http://www.w3.org/2000/svg" className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                            </svg>
                        </button>
                    </div>
                    
                    <div className="p-6">
                        <div className="mb-6">
                            <h4 className="text-sm font-semibold text-neutral-400 uppercase tracking-wide mb-2">Descripción</h4>
                            <p className="text-neutral-300">{selectedVentaja.descripcionLarga}</p>
                        </div>
                        
                        {selectedVentaja.prerequisitos && selectedVentaja.prerequisitos.length > 0 && (
                            <div>
                                <h4 className="text-sm font-semibold text-neutral-400 uppercase tracking-wide mb-2">
                                    Prerrequisitos {(selectedVentaja.prerequisitosOperator || 'AND') === 'OR' ? 
                                        <span className="text-amber-400 font-bold">(cualquiera)</span> : 
                                        <span className="text-purple-400 font-bold">(todos)</span>
                                    }
                                </h4>
                                <div className="flex flex-wrap gap-2">
                                    {selectedVentaja.prerequisitos.map(prereqId => {
                                        const prereqVentaja = ventajas.find(v => v.ventajaId === prereqId);
                                        const displayName = prereqVentaja ? prereqVentaja.nombre : prereqId;
                                        const isOrLogic = (selectedVentaja.prerequisitosOperator || 'AND') === 'OR';
                                        
                                        return (
                                            <button
                                                key={prereqId}
                                                onClick={() => openVentajaModal(prereqId)}
                                                className={`text-xs px-2 py-1 rounded border cursor-pointer hover:scale-105 transition-transform ${
                                                    isOrLogic 
                                                        ? 'text-amber-300 bg-amber-900/20 border-amber-500/30 hover:bg-amber-900/40' 
                                                        : 'text-purple-300 bg-purple-900/20 border-purple-500/30 hover:bg-purple-900/40'
                                                }`}
                                            >
                                                {displayName}
                                            </button>
                                        );
                                    })}
                                </div>
                            </div>
                        )}
                    </div>
                </div>
            </div>
        )}
      </div>
    </div>
  );
}

export default NpcDetailPage;
