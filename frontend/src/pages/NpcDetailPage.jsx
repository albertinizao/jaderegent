import React, { useEffect, useState } from 'react';
import { useParams, Link, useNavigate, useLocation } from 'react-router-dom';
import { npcService } from '../services/npcService';
import { useMode } from '../context/ModeContext';
import ConfirmationModal from '../components/ConfirmationModal';
import NotificationModal from '../components/NotificationModal';

function NpcDetailPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const location = useLocation();
  const { isMaster } = useMode();
  const [npcData, setNpcData] = useState(null);
  const [status, setStatus] = useState('loading');
  const [selectedVentaja, setSelectedVentaja] = useState(null);
  const [showEditModal, setShowEditModal] = useState(false);
  const [showReimportModal, setShowReimportModal] = useState(false);
  const [editForm, setEditForm] = useState({ nombre: '', descripcion_larga: '', imagen_url: '' });
  const [reimportFile, setReimportFile] = useState(null);

  // Relationship Context from PjDetailPage
  const [relacionContext, setRelacionContext] = useState(location.state?.relacionContext || null);

  // Modal States
  const [confirmModal, setConfirmModal] = useState({ 
      isOpen: false, 
      title: '', 
      message: '', 
      onConfirm: () => {},
      isDanger: false
  });
  
  const [notifyModal, setNotifyModal] = useState({ 
      isOpen: false, 
      title: '', 
      message: '', 
      type: 'info' 
  });

  const showNotification = (title, message, type = 'info') => {
      setNotifyModal({ isOpen: true, title, message, type });
  };
   
  const closeNotification = () => {
      setNotifyModal(prev => ({ ...prev, isOpen: false }));
  };

  const closeConfirm = () => {
      setConfirmModal(prev => ({ ...prev, isOpen: false }));
  };

  useEffect(() => {
    loadNpcDetail();
  }, [id]);

  // Update local context if location state changes (e.g. navigation)
  useEffect(() => {
      if (location.state?.relacionContext) {
          setRelacionContext(location.state.relacionContext);
      }
  }, [location.state]);

  const openVentajaModal = (ventajaId) => {
    const ventaja = npcData.ventajas.find(v => v.ventaja_id === ventajaId);
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
  
  const handleSelectVentaja = (ventaja) => {
      if (!relacionContext || !relacionContext.pendienteEleccion) return;
      
      setConfirmModal({
          isOpen: true,
          title: 'Seleccionar Ventaja',
          message: `¿Quieres seleccionar la ventaja "${ventaja.nombre}"?`,
          onConfirm: async () => {
              try {
                  await import('../services/relacionService').then(m => m.relacionService.selectVentaja(relacionContext.relacionId, ventaja.ventaja_id));
                  
                  // Update local state to reflect selection
                  setRelacionContext(prev => ({
                      ...prev,
                      pendienteEleccion: false,
                      ventajasObtenidasIds: [...prev.ventajasObtenidasIds, ventaja.ventaja_id]
                  }));
                  showNotification("Éxito", "Ventaja seleccionada correctamente", 'success');
              } catch (error) {
                  console.error("Error al seleccionar ventaja", error);
                  showNotification("Error", "Error al seleccionar la ventaja", 'error');
              }
          }
      });
  };

  const checkEligibility = (ventaja) => {
      if (!relacionContext) return { eligible: false, reason: 'No hay contexto de relación' };
      
      // Check if already obtained
      if (relacionContext.ventajasObtenidasIds.includes(ventaja.ventaja_id)) {
          return { eligible: false, obtained: true };
      }

      // Check level
      if (relacionContext.nivelActual < ventaja.min_nivel_relacion) {
          return { eligible: false, reason: 'Nivel insuficiente' };
      }

      // Check Prerequisites
      if (ventaja.prerequisitos && ventaja.prerequisitos.length > 0) {
          const isOr = (ventaja.prerequisitos_operator || 'AND') === 'OR';
          const obtained = new Set(relacionContext.ventajasObtenidasIds);
          
          if (isOr) {
              const anyMet = ventaja.prerequisitos.some(req => obtained.has(req));
              if (!anyMet) return { eligible: false, reason: 'Falta prerequisito (cualquiera)' };
          } else {
              const allMet = ventaja.prerequisitos.every(req => obtained.has(req));
              if (!allMet) return { eligible: false, reason: 'Faltan prerequisitos' };
          }
      }

      return { eligible: true };
  };

  const handleDelete = () => {
    setConfirmModal({
        isOpen: true,
        title: 'Eliminar NPC',
        message: '¿Estás seguro de que quieres eliminar este NPC? Esta acción no se puede deshacer.',
        isDanger: true,
        onConfirm: async () => {
            try {
                await npcService.delete(id);
                navigate('/npcs');
            } catch (error) {
                console.error("Error al eliminar NPC", error);
                showNotification("Error", "Error al eliminar el NPC", 'error');
            }
        }
    });
  };

  const openEditModal = () => {
    setEditForm({
      nombre: npcData.npc.nombre,
      descripcion_larga: npcData.npc.descripcion_larga,
      imagen_url: npcData.npc.imagen_url || ''
    });
    setShowEditModal(true);
  };

  const handleEdit = async (e) => {
    e.preventDefault();
    try {
      await npcService.update(id, editForm);
      setShowEditModal(false);
      loadNpcDetail(); // Refresh data
      showNotification("Éxito", "NPC actualizado correctamente", 'success');
    } catch (error) {
      console.error("Error al actualizar NPC", error);
      showNotification("Error", "Error al actualizar el NPC", 'error');
    }
  };

  const handleReimport = async (e) => {
    e.preventDefault();
    if (!reimportFile) {
      showNotification("Atención", "Por favor selecciona un archivo", 'info');
      return;
    }
    
    // We can use confirmation here too if we want to be extra safe about overwriting
    setConfirmModal({
        isOpen: true,
        title: 'Confirmar Reimportación',
        message: 'Esto sobrescribirá todos los datos del NPC. ¿Deseas continuar?',
        isDanger: true,
        onConfirm: async () => {
             try {
              await npcService.importNpc(reimportFile);
              setShowReimportModal(false);
              setReimportFile(null);
              loadNpcDetail(); // Refresh data
              showNotification("Éxito", "NPC reimportado correctamente", 'success');
            } catch (error) {
              console.error("Error al reimportar NPC", error);
              showNotification("Error", "Error al reimportar el NPC", 'error');
            }
        }
    });
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
  const backPath = location.state?.from || '/npcs';
  const backLabel = location.state?.fromLabel || 'Volver';

  return (
    <div className="min-h-screen bg-neutral-900 text-white p-4 md:p-8 font-sans">
      <div className="max-w-5xl mx-auto">
        <header className="mb-8 flex justify-between items-center">
            <Link to={backPath} className="inline-flex items-center text-neutral-400 hover:text-white transition-colors">
                <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M10 19l-7-7m0 0l7-7m-7 7h18" />
                </svg>
                {backLabel}
            </Link>
            {isMaster && (
              <div className="flex gap-2">
                <button 
                  onClick={openEditModal}
                  className="bg-blue-500/20 hover:bg-blue-500/40 text-blue-300 border border-blue-500/50 px-4 py-2 rounded-lg transition-colors flex items-center gap-2"
                >
                  <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                  </svg>
                  Editar
                </button>
                <button 
                  onClick={() => setShowReimportModal(true)}
                  className="bg-purple-500/20 hover:bg-purple-500/40 text-purple-300 border border-purple-500/50 px-4 py-2 rounded-lg transition-colors flex items-center gap-2"
                >
                  <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-8l-4-4m0 0L8 8m4-4v12" />
                  </svg>
                  Reimportar
                </button>
                <button 
                  onClick={handleDelete}
                  className="bg-red-500/20 hover:bg-red-500/40 text-red-300 border border-red-500/50 px-4 py-2 rounded-lg transition-colors flex items-center gap-2"
                >
                  <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                  </svg>
                  Eliminar
                </button>
              </div>
            )}
        </header>

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
            {/* Relationships List */}
            <div className="lg:col-span-1 space-y-8">
                <div className="bg-neutral-800 rounded-2xl overflow-hidden border border-white/5 sticky top-8">
                    <div className="h-64 sm:h-80 bg-neutral-900 relative">
                        {npc.imagen_url ? (
                            <img 
                                src={npc.imagen_url} 
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
                                Nivel Máximo: {npc.nivel_maximo}
                             </div>
                         </div>
                    </div>
                    <div className="p-6">
                        <h3 className="text-neutral-400 text-sm font-bold uppercase tracking-wider mb-2">Descripción</h3>
                        <p className="text-neutral-300 leading-relaxed text-sm">
                            {npc.descripcion_larga}
                        </p>
                    </div>
                </div>

                {/* Relationships Section */}
                {isMaster && (
                <div className="bg-neutral-800 rounded-2xl p-6 border border-white/5">
                     <h3 className="text-xl font-bold mb-4 flex items-center">
                        <span className="bg-clip-text text-transparent bg-gradient-to-r from-blue-400 to-purple-300">
                            Relaciones
                        </span>
                    </h3>
                    
                    {npcData.relaciones && npcData.relaciones.length > 0 ? (
                        <div className="space-y-4">
                            {npcData.relaciones.map(rel => (
                                <div key={rel.relacion_id} className="bg-neutral-700/30 rounded-lg p-4 border border-white/5">
                                    <div className="flex items-center gap-3 mb-3">
                                        {rel.pj_imagen_url ? (
                                             <img src={rel.pj_imagen_url} className="w-10 h-10 rounded-full object-cover border border-white/10" alt={rel.pj_nombre} />
                                        ) : (
                                            <div className="w-10 h-10 rounded-full bg-neutral-600 flex items-center justify-center text-white font-bold text-xs">
                                                {rel.pj_nombre?.substring(0,2).toUpperCase()}
                                            </div>
                                        )}
                                        <div>
                                            <div className="font-bold text-white">{rel.pj_nombre}</div>
                                            <div className="text-xs text-neutral-400">Nivel {rel.nivel_actual}</div>
                                        </div>
                                    </div>
                                    
                                    {isMaster && (
                                        <div className="flex flex-col gap-2 border-t border-white/5 pt-3 mt-2">
                                             <div className="flex justify-between items-center text-xs">
                                                <span className="text-neutral-500 uppercase tracking-wider">Interacciones ({rel.contador_interacciones || 0})</span>
                                                <div className="flex gap-1">
                                                     <button 
                                                        onClick={async () => {
                                                            try {
                                                                const { relacionService } = await import('../services/relacionService');
                                                                await relacionService.registerInteraction(rel.relacion_id, false);
                                                                loadNpcDetail(); 
                                                                showNotification("Registrado", "Interacción negativa registrada", 'success');
                                                            } catch (e) { 
                                                                console.error("Interaction Error:", e);
                                                                showNotification("Error", "Error al registrar interacción: " + e.message, 'error');
                                                            }
                                                        }}
                                                        className="w-6 h-6 flex items-center justify-center bg-red-900/30 hover:bg-red-900/50 text-red-400 rounded border border-red-500/20"
                                                     >-</button>
                                                     <button 
                                                       onClick={async () => {
                                                            try {
                                                                const { relacionService } = await import('../services/relacionService');
                                                                await relacionService.registerInteraction(rel.relacion_id, true);
                                                                loadNpcDetail(); 
                                                                showNotification("Registrado", "Interacción positiva registrada", 'success');
                                                            } catch (e) { 
                                                                console.error("Interaction Error:", e);
                                                                showNotification("Error", "Error al registrar interacción: " + e.message, 'error');
                                                            }
                                                        }}
                                                        className="w-6 h-6 flex items-center justify-center bg-green-900/30 hover:bg-green-900/50 text-green-400 rounded border border-green-500/20"
                                                     >+</button>
                                                </div>
                                            </div>
                                        </div>
                                    )}
                                </div>
                            ))}
                        </div>
                    ) : (
                         <p className="text-neutral-500 text-sm italic">No hay relaciones activas.</p>
                    )}
                </div>
                )}
            </div>

            {/* Advantages List */}
            <div className="lg:col-span-2">
                <div className="flex justify-between items-center mb-6">
                    <div className="flex items-center">
                        <span className="bg-clip-text text-transparent bg-gradient-to-r from-purple-400 to-pink-300 text-2xl font-bold">
                            Árbol de Ventajas
                        </span>
                        <span className="ml-3 px-2 py-1 bg-neutral-800 text-xs text-neutral-400 rounded-full border border-white/10">
                            {ventajas.length}
                        </span>
                    </div>
                    <Link 
                        to={`/npcs/${id}/advantages-tree`}
                        className="bg-gradient-to-r from-purple-600 to-pink-600 hover:from-purple-700 hover:to-pink-700 text-white px-4 py-2 rounded-lg transition-all flex items-center gap-2 shadow-lg hover:shadow-purple-500/50"
                    >
                        <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2V6zM14 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2V6zM4 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2v-2zM14 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2v-2z" />
                        </svg>
                        Ver Árbol Interactivo
                    </Link>
                </div>

                <div className="space-y-4">
                    {ventajas.map((v) => {
                        const { eligible, obtained, reason } = checkEligibility(v);
                        
                        return (
                        <div key={v.ventaja_id} className={`bg-neutral-800/60 rounded-xl p-6 border transition-all ${
                            obtained ? 'border-green-500/30 bg-green-900/10' : 
                            eligible && relacionContext?.pendienteEleccion ? 'border-amber-500/50 bg-amber-900/10 hover:border-amber-400' :
                            'border-white/5'
                        }`}>
                            <div className="flex justify-between items-start mb-2">
                                <h3 className="text-lg font-bold text-white">{v.nombre}</h3>
                                <div className="flex flex-col items-end gap-2">
                                    <span className={`text-xs font-bold px-2 py-1 rounded ${
                                        v.min_nivel_relacion <= 3 ? 'bg-green-900/40 text-green-300' : 
                                        v.min_nivel_relacion <= 6 ? 'bg-yellow-900/40 text-yellow-300' : 'bg-red-900/40 text-red-300'
                                    }`}>
                                        Nivel {v.min_nivel_relacion}
                                    </span>
                                    {obtained && (
                                        <span className="text-xs font-bold px-2 py-1 rounded bg-green-500 text-white flex items-center gap-1">
                                            <svg xmlns="http://www.w3.org/2000/svg" className="h-3 w-3" viewBox="0 0 20 20" fill="currentColor">
                                                <path fillRule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clipRule="evenodd" />
                                            </svg>
                                            Obtenida
                                        </span>
                                    )}
                                </div>
                            </div>
                            <p className="text-neutral-400 text-sm mb-4">
                                {v.descripcion_larga}
                            </p>
                            {v.prerequisitos && v.prerequisitos.length > 0 && (
                                <div className="mt-4 pt-3 border-t border-white/5">
                                    <p className="text-xs text-neutral-500 mb-2 uppercase tracking-wide">
                                        Prerrequisitos {(v.prerequisitos_operator || 'AND') === 'OR' ? 
                                            <span className="text-amber-400 font-bold">(cualquiera)</span> : 
                                            <span className="text-purple-400 font-bold">(todos)</span>
                                        }
                                    </p>
                                    <div className="flex flex-wrap gap-2">
                                        {v.prerequisitos.map(pre => {
                                            const prereqVentaja = ventajas.find(ventaja => ventaja.ventaja_id === pre);
                                            const displayName = prereqVentaja ? prereqVentaja.nombre : pre;
                                            const isOrLogic = (v.prerequisitos_operator || 'AND') === 'OR';
                                            const isMet = relacionContext?.ventajasObtenidasIds?.includes(pre);

                                            return (
                                                <button
                                                    key={pre}
                                                    onClick={() => openVentajaModal(pre)}
                                                    className={`text-xs px-2 py-1 rounded border hover:scale-105 transition-all cursor-pointer ${
                                                        isMet ? 'bg-green-900/30 border-green-500/50 text-green-300' :
                                                        isOrLogic 
                                                            ? 'text-amber-300 bg-amber-900/20 border-amber-500/30 hover:bg-amber-900/40' 
                                                            : 'text-purple-300 bg-purple-900/20 border-purple-500/30 hover:bg-purple-900/40'
                                                    }`}
                                                >
                                                    {displayName} {isMet && '✓'}
                                                </button>
                                            );
                                        })}
                                    </div>
                                </div>
                            )}

                            {/* Select Button */}
                            {eligible && relacionContext?.pendienteEleccion && (
                                <button
                                    onClick={() => handleSelectVentaja(v)}
                                    className="mt-4 w-full bg-amber-600 hover:bg-amber-700 text-white font-bold py-2 px-4 rounded-lg transition-colors flex items-center justify-center gap-2 shadow-lg shadow-amber-900/20 animate-pulse"
                                >
                                    <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
                                    </svg>
                                    Seleccionar Ventaja
                                </button>
                            )}
                            {!eligible && !obtained && relacionContext?.pendienteEleccion && (
                                <div className="mt-4 text-xs text-center text-neutral-500 italic">
                                    No disponible: {reason}
                                </div>
                            )}
                        </div>
                    );
                    })}
                    
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
                                selectedVentaja.min_nivel_relacion <= 3 ? 'bg-green-900/40 text-green-300' : 
                                selectedVentaja.min_nivel_relacion <= 6 ? 'bg-yellow-900/40 text-yellow-300' : 'bg-red-900/40 text-red-300'
                            }`}>
                                Nivel {selectedVentaja.min_nivel_relacion}
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
                            <p className="text-neutral-300">{selectedVentaja.descripcion_larga}</p>
                        </div>
                        
                        {selectedVentaja.prerequisitos && selectedVentaja.prerequisitos.length > 0 && (
                            <div>
                                <h4 className="text-sm font-semibold text-neutral-400 uppercase tracking-wide mb-2">
                                    Prerrequisitos {(selectedVentaja.prerequisitos_operator || 'AND') === 'OR' ? 
                                        <span className="text-amber-400 font-bold">(cualquiera)</span> : 
                                        <span className="text-purple-400 font-bold">(todos)</span>
                                    }
                                </h4>
                                <div className="flex flex-wrap gap-2">
                                    {selectedVentaja.prerequisitos.map(prereqId => {
                                        const prereqVentaja = ventajas.find(v => v.ventaja_id === prereqId);
                                        const displayName = prereqVentaja ? prereqVentaja.nombre : prereqId;
                                        const isOrLogic = (selectedVentaja.prerequisitos_operator || 'AND') === 'OR';
                                        
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

        {/* Edit Modal */}
        {showEditModal && (
            <div className="fixed inset-0 bg-black/80 flex items-center justify-center z-50 p-4" onClick={() => setShowEditModal(false)}>
                <div className="bg-neutral-800 rounded-2xl max-w-2xl w-full border border-white/10 shadow-2xl" onClick={(e) => e.stopPropagation()}>
                    <div className="p-6 border-b border-white/10 flex justify-between items-start">
                        <h3 className="text-2xl font-bold text-white">Editar NPC</h3>
                        <button 
                            onClick={() => setShowEditModal(false)}
                            className="text-neutral-400 hover:text-white transition-colors p-2 hover:bg-neutral-700 rounded-lg"
                        >
                            <svg xmlns="http://www.w3.org/2000/svg" className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                            </svg>
                        </button>
                    </div>
                    
                    <form onSubmit={handleEdit} className="p-6">
                        <div className="space-y-4">
                            <div>
                                <label className="block text-sm font-semibold text-neutral-400 mb-2">Nombre</label>
                                <input 
                                    type="text" 
                                    value={editForm.nombre} 
                                    onChange={(e) => setEditForm({...editForm, nombre: e.target.value})}
                                    className="w-full bg-neutral-700 text-white px-4 py-2 rounded-lg border border-neutral-600 focus:border-blue-500 focus:outline-none"
                                    required
                                />
                            </div>
                            <div>
                                <label className="block text-sm font-semibold text-neutral-400 mb-2">Descripción</label>
                                <textarea 
                                    value={editForm.descripcion_larga} 
                                    onChange={(e) => setEditForm({...editForm, descripcion_larga: e.target.value})}
                                    className="w-full bg-neutral-700 text-white px-4 py-2 rounded-lg border border-neutral-600 focus:border-blue-500 focus:outline-none h-32"
                                    required
                                />
                            </div>
                            <div>
                                <label className="block text-sm font-semibold text-neutral-400 mb-2">URL de la Imagen</label>
                                <input 
                                    type="text" 
                                    value={editForm.imagen_url} 
                                    onChange={(e) => setEditForm({...editForm, imagen_url: e.target.value})}
                                    className="w-full bg-neutral-700 text-white px-4 py-2 rounded-lg border border-neutral-600 focus:border-blue-500 focus:outline-none"
                                />
                            </div>
                        </div>
                        <div className="flex gap-2 mt-6">

                            <button 
                                type="submit"
                                className="flex-1 bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg transition-colors font-semibold"
                            >
                                Guardar
                            </button>
                            <button 
                                type="button"
                                onClick={() => setShowEditModal(false)}
                                className="flex-1 bg-neutral-700 hover:bg-neutral-600 text-neutral-300 px-4 py-2 rounded-lg transition-colors"
                            >
                                Cancelar
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        )}

        {/* Reimport Modal */}
        {showReimportModal && (
            <div className="fixed inset-0 bg-black/80 flex items-center justify-center z-50 p-4" onClick={() => setShowReimportModal(false)}>
                <div className="bg-neutral-800 rounded-2xl max-w-2xl w-full border border-white/10 shadow-2xl" onClick={(e) => e.stopPropagation()}>
                    <div className="p-6 border-b border-white/10 flex justify-between items-start">
                        <h3 className="text-2xl font-bold text-white">Reimportar NPC</h3>
                        <button 
                            onClick={() => setShowReimportModal(false)}
                            className="text-neutral-400 hover:text-white transition-colors p-2 hover:bg-neutral-700 rounded-lg"
                        >
                            <svg xmlns="http://www.w3.org/2000/svg" className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                            </svg>
                        </button>
                    </div>
                    
                    <form onSubmit={handleReimport} className="p-6">
                        <div className="mb-4">
                            <label className="block text-sm font-semibold text-neutral-400 mb-2">Archivo JSON</label>
                            <input 
                                type="file" 
                                accept=".json"
                                onChange={(e) => setReimportFile(e.target.files[0])}
                                className="w-full bg-neutral-700 text-white px-4 py-2 rounded-lg border border-neutral-600 focus:border-purple-500 focus:outline-none file:mr-4 file:py-2 file:px-4 file:rounded-lg file:border-0 file:bg-purple-600 file:text-white hover:file:bg-purple-700 file:cursor-pointer"
                                required
                            />
                            <p className="text-xs text-neutral-500 mt-2">Esto sobrescribirá todos los datos del NPC, incluyendo las ventajas.</p>
                        </div>
                        <div className="flex gap-2 mt-6">
                            <button 
                                type="submit"
                                className="flex-1 bg-purple-600 hover:bg-purple-700 text-white px-4 py-2 rounded-lg transition-colors font-semibold"
                            >
                                Importar
                            </button>
                            <button 
                                type="button"
                                onClick={() => setShowReimportModal(false)}
                                className="flex-1 bg-neutral-700 hover:bg-neutral-600 text-neutral-300 px-4 py-2 rounded-lg transition-colors"
                            >
                                Cancelar
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        )}
      
            <ConfirmationModal 
                isOpen={confirmModal.isOpen}
                onClose={closeConfirm}
                onConfirm={confirmModal.onConfirm}
                title={confirmModal.title}
                message={confirmModal.message}
                isDanger={confirmModal.isDanger}
            />

            <NotificationModal
                isOpen={notifyModal.isOpen}
                onClose={closeNotification}
                title={notifyModal.title}
                message={notifyModal.message}
                type={notifyModal.type}
            />
      </div>
    </div>
  );
}

export default NpcDetailPage;
