
import React, { useEffect, useState } from 'react';
import { useParams, Link, useLocation, useNavigate } from 'react-router-dom';
import { pjService } from '../services/pjService';
import { relacionService } from '../services/relacionService';
import { npcService } from '../services/npcService';
import { useMode } from '../context/ModeContext';

function PjDetailPage() {
  const { id } = useParams();
  const location = useLocation();
  const navigate = useNavigate();
  const { isMaster } = useMode();
  const [pjData, setPjData] = useState(null);
  const [status, setStatus] = useState('loading');
  const [isEditing, setIsEditing] = useState(false);
  const [editData, setEditData] = useState({
      nombre_display: '',
      nota_opcional: '',
      imagen_url: ''
  });
  const [showAddRelacionModal, setShowAddRelacionModal] = useState(false);
  const [availableNpcs, setAvailableNpcs] = useState([]);
  const [selectedNpcId, setSelectedNpcId] = useState('');

  const handleEditClick = () => {
    setEditData({
        nombre_display: pjData.pj.nombre_display || '',
        nota_opcional: pjData.pj.nota_opcional || '',
        imagen_url: pjData.pj.imagen_url || ''
    });
    setIsEditing(true);
  };

  const handleCancelClick = () => {
    setIsEditing(false);
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setEditData(prev => ({ ...prev, [name]: value }));
  };

  const handleSaveClick = async () => {
    try {
      const updatedPj = await pjService.update(id, editData);
      setPjData(prev => ({
          ...prev,
          pj: {
              ...prev.pj,
              ...updatedPj
          }
      }));
      setIsEditing(false);
    } catch (error) {
      console.error("Error updating PJ", error);
      alert("Error al guardar los cambios");
    }
  };

  const handleDelete = async () => {
    if (window.confirm('¿Estás seguro de que quieres eliminar este personaje? Esta acción no se puede deshacer.')) {
        try {
            await pjService.delete(id);
            navigate('/pjs');
        } catch (error) {
            console.error("Error al eliminar PJ", error);
            alert("Error al eliminar el personaje");
        }
    }
  };
  
  const handleInteraction = async (e, relId, tipo) => {
    e.preventDefault(); // Stop link navigation
    e.stopPropagation();
    
    try {
        await relacionService.addInteraccion(relId, tipo);
        
        // Update local state deeply
        setPjData(prev => {
            if (!prev) return null;
            
            const change = tipo === 'POSITIVA' ? 1 : -1;
            
            const updatedRelaciones = prev.relaciones.map(r => {
                if (r.relacion_id === relId) {
                    return { ...r, contador_interacciones: (r.contador_interacciones || 0) + change };
                }
                return r;
            });
            
            return { ...prev, relaciones: updatedRelaciones };
        });
        
    } catch (error) {
        console.error("Error adding interaction", error);
        alert("Error al registrar interacción");
    }
  };

  const handleOpenAddRelacion = async () => {
    try {
        const allNpcs = await npcService.getAll();
        // Filter NPCs that already have a relationship
        const existingNpcIds = new Set(pjData.relaciones.map(r => r.npc_id));
        const filtered = allNpcs.filter(npc => !existingNpcIds.has(npc.npc_id));
        
        setAvailableNpcs(filtered);
        setShowAddRelacionModal(true);
    } catch (error) {
        console.error("Error loading NPCs", error);
        alert("Error al cargar lista de NPCs");
    }
  };

  const handleAddRelacion = async (e) => {
    e.preventDefault();
    if (!selectedNpcId) return;
    
    try {
        await relacionService.create(id, selectedNpcId);
        setShowAddRelacionModal(false);
        setSelectedNpcId('');
        loadPjDetail(); // Refresh data
    } catch(error) {
        console.error("Error creating relationship", error);
        alert("Error al crear relación");
    }
  };

  const handleLevelUpdate = async (e, relId, increment) => {
    e.preventDefault();
    e.stopPropagation();
    
    try {
        await relacionService.updateLevel(relId, increment);
        loadPjDetail(); // Refresh data to see new level and status
    } catch (error) {
        console.error("Error updating level", error);
        alert("Error al actualizar nivel");
    }
  };

  const handleDeleteRelacion = async (e, relId, npcNombre) => {
    e.preventDefault();
    e.stopPropagation();
    
    if (!window.confirm(`¿Estás seguro de que quieres eliminar la relación con ${npcNombre}?`)) {
        return;
    }
    
    try {
        await relacionService.delete(relId);
        loadPjDetail(); // Refresh data
    } catch (error) {
        console.error("Error deleting relacion", error);
        alert("Error al eliminar la relación: " + error.message);
    }
  };

  useEffect(() => {
    loadPjDetail();
  }, [id]);

  const loadPjDetail = async () => {
    try {
      const data = await pjService.getById(id);
      setPjData(data);
      setStatus('success');
    } catch (error) {
      console.error(error);
      setStatus('error');
    }
  };

  if (status === 'loading') {
    return <div className="min-h-screen bg-neutral-900 text-white flex items-center justify-center">Cargando...</div>;
  }

  if (status === 'error' || !pjData) {
    return (
        <div className="min-h-screen bg-neutral-900 text-white flex flex-col items-center justify-center p-4">
            <h2 className="text-2xl font-bold text-red-500 mb-4">Error</h2>
            <p className="text-neutral-400 mb-6">No se pudo cargar el personaje.</p>
            <Link to="/pjs" className="text-purple-400 hover:text-purple-300">Volver a la lista</Link>
        </div>
    );
  }

  const { pj, relaciones: relacionesRaw } = pjData;
  
  // Filtrar relaciones de nivel 0 para jugadores (solo máster las ve)
  const relacionesFiltradas = isMaster 
    ? relacionesRaw 
    : relacionesRaw.filter(r => r.nivel_actual > 0);
  
  // Ordenar relaciones: 1) nivel_maximo desc, 2) nivel_actual desc, 3) alfabético
  const relaciones = [...relacionesFiltradas].sort((a, b) => {
    // Primero por nivel_maximo descendente
    if (b.nivel_maximo !== a.nivel_maximo) {
      return b.nivel_maximo - a.nivel_maximo;
    }
    // Luego por nivel_actual descendente
    if (b.nivel_actual !== a.nivel_actual) {
      return b.nivel_actual - a.nivel_actual;
    }
    // Finalmente alfabéticamente por nombre del NPC
    return a.npc_nombre.localeCompare(b.npc_nombre);
  });

  return (
    <div className="min-h-screen bg-neutral-900 text-white p-4 md:p-8 font-sans">
      <div className="max-w-6xl mx-auto">
        <header className="mb-8 flex justify-between items-center">
            {(!isMaster && localStorage.getItem('jade_regent_player_pj_id')) ? (
                 <button 
                    onClick={() => {
                        localStorage.removeItem('jade_regent_player_pj_id');
                        navigate('/');
                    }}
                    className="inline-flex items-center text-red-400 hover:text-red-300 transition-colors font-bold"
                 >
                    <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
                    </svg>
                    Salir
                 </button>
            ) : (
                <Link to="/pjs" className="inline-flex items-center text-neutral-400 hover:text-white transition-colors">
                    <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M10 19l-7-7m0 0l7-7m-7 7h18" />
                    </svg>
                    Volver a Personajes
                </Link>
            )}
            <div className="flex gap-3">
                {isMaster && (
                    <Link
                        to="/dashboard"
                        className="bg-neutral-800 hover:bg-neutral-700 text-neutral-400 hover:text-white p-2 rounded-lg transition-colors border border-neutral-700"
                        title="Ir al Dashboard"
                    >
                        <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6" />
                        </svg>
                    </Link>
                )}
                {isMaster && (
                    <Link
                        to="/relations-matrix"
                        className="bg-pink-900/30 hover:bg-pink-900/50 text-pink-400 hover:text-pink-300 p-2 rounded-lg transition-colors border border-pink-500/30"
                        title="Matriz de Relaciones"
                    >
                        <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 10h18M3 14h18m-9-4v8m-7-9l-2 2 2 2m0-8l-2 2 2 2m12-8l2 2-2 2m0-8l2 2-2 2" />
                        </svg>
                    </Link>
                )}
                {isMaster && (
                    <button
                        onClick={handleOpenAddRelacion}
                        className="bg-green-600 hover:bg-green-500 text-white font-bold px-4 py-2 rounded-lg transition-colors flex items-center gap-2 shadow-lg hover:shadow-green-500/20"
                    >
                         <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4v16m8-8H4" />
                        </svg>
                        Añadir Relación
                    </button>
                )}
                <button 
                    onClick={handleEditClick}
                    className="bg-blue-500/20 hover:bg-blue-500/40 text-blue-300 border border-blue-500/50 px-4 py-2 rounded-lg transition-colors flex items-center gap-2"
                >
                    <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15.232 5.232l3.536 3.536m-2.036-5.036a2.5 2.5 0 113.536 3.536L6.5 21.036H3v-3.572L16.732 3.732z" />
                    </svg>
                    Editar Personaje
                </button>
                {isMaster && (
                    <button 
                        onClick={handleDelete}
                        className="bg-red-500/20 hover:bg-red-500/40 text-red-300 border border-red-500/50 px-4 py-2 rounded-lg transition-colors flex items-center gap-2"
                    >
                        <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                        </svg>
                        Eliminar Personaje
                    </button>
                )}
            </div>
        </header>

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
            {/* PJ Card */}
            <div className="lg:col-span-1">
                <div className="bg-neutral-800 rounded-2xl overflow-hidden border border-white/5 sticky top-8">
                    {!isEditing ? (
                        <>
                            <div className="h-64 sm:h-80 bg-neutral-900 relative">
                                {pj.imagen_url ? (
                                    <img 
                                        src={pj.imagen_url} 
                                        alt={pj.nombre_display} 
                                        className="w-full h-full object-cover"
                                        onError={(e) => { e.target.src = 'https://via.placeholder.com/400x500?text=Sin+Imagen'; }}
                                    />
                                ) : (
                                    <div className="w-full h-full flex items-center justify-center text-neutral-600">
                                        <svg xmlns="http://www.w3.org/2000/svg" className="h-32 w-32" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
                                        </svg>
                                    </div>
                                )}
                                 <div className="absolute inset-x-0 bottom-0 bg-gradient-to-t from-neutral-900 to-transparent h-24"></div>
                                 <div className="absolute bottom-4 left-4 right-4">
                                     <h1 className="text-3xl font-bold text-white mb-1">{pj.nombre_display}</h1>
                                 </div>
                            </div>
                            <div className="p-6">
                                <h3 className="text-neutral-400 text-sm font-bold uppercase tracking-wider mb-2">Descripción</h3>
                                <p className="text-neutral-300 leading-relaxed text-sm whitespace-pre-wrap">
                                    {pj.nota_opcional || 'Sin descripción.'}
                                </p>
                            </div>
                        </>
                    ) : (
                        <div className="p-6 space-y-4">
                            <h2 className="text-xl font-bold text-white mb-4">Editar Personaje</h2>
                            
                            <div>
                                <label className="block text-sm font-bold text-neutral-400 mb-1">Nombre</label>
                                <input 
                                    type="text"
                                    name="nombre_display"
                                    value={editData.nombre_display}
                                    onChange={handleInputChange}
                                    className="w-full bg-neutral-900 border border-neutral-700 rounded-lg p-2 text-white focus:ring-2 focus:ring-purple-500 outline-none"
                                />
                            </div>

                            <div>
                                <label className="block text-sm font-bold text-neutral-400 mb-1">URL Imagen</label>
                                <input 
                                    type="text"
                                    name="imagen_url"
                                    value={editData.imagen_url}
                                    onChange={handleInputChange}
                                    className="w-full bg-neutral-900 border border-neutral-700 rounded-lg p-2 text-white focus:ring-2 focus:ring-purple-500 outline-none"
                                />
                            </div>

                            <div>
                                <label className="block text-sm font-bold text-neutral-400 mb-1">Descripción / Notas</label>
                                <textarea 
                                    name="nota_opcional"
                                    value={editData.nota_opcional}
                                    onChange={handleInputChange}
                                    rows="6"
                                    className="w-full bg-neutral-900 border border-neutral-700 rounded-lg p-2 text-white focus:ring-2 focus:ring-purple-500 outline-none resize-none"
                                />
                            </div>

                            <div className="flex gap-2 pt-2">
                                <button 
                                    onClick={handleSaveClick}
                                    className="flex-1 bg-purple-600 hover:bg-purple-700 text-white font-bold py-2 px-4 rounded-lg transition-colors"
                                >
                                    Guardar
                                </button>
                                <button 
                                    onClick={handleCancelClick}
                                    className="flex-1 bg-neutral-700 hover:bg-neutral-600 text-white font-bold py-2 px-4 rounded-lg transition-colors"
                                >
                                    Cancelar
                                </button>
                            </div>
                        </div>
                    )}
                </div>
            </div>

            {/* Right Column: Relations & Advantages */}
            <div className="lg:col-span-2 space-y-8">
                
                {/* Relaciones List */}
                <div>
                    <h2 className="text-2xl font-bold mb-6 flex items-center">
                        <span className="bg-clip-text text-transparent bg-gradient-to-r from-jade-400 to-teal-200">
                            Relaciones con NPCs
                        </span>
                        <span className="ml-3 px-2 py-1 bg-neutral-800 text-xs text-neutral-400 rounded-full border border-white/10">
                            {relaciones.length}
                        </span>
                    </h2>

                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                        {relaciones.map((rel) => (
                            <div key={rel.relacion_id} className="relative group">
                                <Link 
                                    to={`/npcs/${rel.npc_id}`}
                                    state={{ 
                                        from: location.pathname, 
                                        fromLabel: `Volver a ${pj.nombre_display}`,
                                        relacionContext: {
                                            relacionId: rel.relacion_id,
                                            nivelActual: rel.nivel_actual,
                                            pendienteEleccion: rel.pendiente_eleccion,
                                            ventajasObtenidasIds: rel.ventajas_obtenidas_ids || []
                                        }
                                    }}
                                    className="block bg-neutral-800/60 rounded-xl p-4 border border-white/5 hover:border-jade-500/30 transition-all hover:scale-[1.02] cursor-pointer"
                                >
                                    <div className="flex items-center gap-4 mb-3">
                                        {rel.npc_imagen_url ? (
                                            <img 
                                                src={rel.npc_imagen_url} 
                                                alt={rel.npc_nombre}
                                                className="w-16 h-16 rounded-lg object-cover"
                                                onError={(e) => { e.target.style.display = 'none'; }}
                                            />
                                        ) : (
                                            <div className="w-16 h-16 rounded-lg bg-neutral-700 flex items-center justify-center">
                                                <span className="text-2xl text-neutral-500">?</span>
                                            </div>
                                        )}
                                        <div className={`flex-1 ${isMaster ? 'pr-24' : ''}`}>
                                            <h3 className="text-lg font-bold text-white flex justify-between">
                                                {rel.npc_nombre}
                                                {isMaster && rel.contador_interacciones !== 0 && (
                                                    <span className={`text-xs ml-2 px-2 py-0.5 rounded-full ${rel.contador_interacciones > 0 ? 'bg-green-900/50 text-green-300' : 'bg-red-900/50 text-red-300'}`}>
                                                        {rel.contador_interacciones > 0 ? '+' : ''}{rel.contador_interacciones}
                                                    </span>
                                                )}
                                            </h3>
                                            <p className="text-sm text-neutral-400">
                                                Nivel {rel.nivel_actual} / {rel.nivel_maximo}
                                            </p>
                                        </div>
                                    </div>

                                    {/* Progress Bar & Level Controls */}
                                    <div className="mb-2 flex items-center gap-2">
                                        <div className="flex-1 bg-neutral-700 rounded-full h-2">
                                            <div 
                                                className="bg-gradient-to-r from-jade-600 to-teal-500 h-2 rounded-full transition-all"
                                                style={{ width: `${(rel.nivel_actual / rel.nivel_maximo) * 100}%` }}
                                            ></div>
                                        </div>
                                        {isMaster && (
                                            <div className="flex gap-1 z-20">
                                                <button
                                                    onClick={(e) => handleLevelUpdate(e, rel.relacion_id, false)}
                                                    className="w-6 h-6 rounded bg-neutral-700 hover:bg-neutral-600 text-white flex items-center justify-center text-xs ml-1 disabled:opacity-50"
                                                    disabled={rel.nivel_actual <= 0}
                                                    title="-1 Nivel"
                                                >
                                                    -
                                                </button>
                                                <button
                                                    onClick={(e) => handleLevelUpdate(e, rel.relacion_id, true)}
                                                    className="w-6 h-6 rounded bg-purple-600 hover:bg-purple-500 text-white flex items-center justify-center text-xs disabled:opacity-50"
                                                    disabled={rel.nivel_actual >= rel.nivel_maximo}
                                                    title="+1 Nivel"
                                                >
                                                    +
                                                </button>
                                            </div>
                                        )}
                                    </div>

                                    {/* Status Indicators */}
                                    <div className="flex gap-2 flex-wrap">
                                        {rel.pendiente_eleccion && (
                                            <span className="text-xs bg-amber-900/20 text-amber-300 px-2 py-1 rounded border border-amber-500/10">
                                                ⚠️ Pendiente elección
                                            </span>
                                        )}
                                        {!rel.consistente && (
                                            <span className="text-xs bg-red-900/20 text-red-300 px-2 py-1 rounded border border-red-500/10">
                                                ❌ Inconsistente
                                            </span>
                                        )}
                                    </div>
                                </Link>
                                
                                {/* Master Controls for Interactions */}
                                {isMaster && (
                                    <div className="absolute top-4 right-4 flex gap-1 z-10">
                                        {/* Delete button - only for level 0 and no interactions */}
                                        {rel.nivel_actual === 0 && rel.contador_interacciones === 0 && (
                                            <button
                                                onClick={(e) => handleDeleteRelacion(e, rel.relacion_id, rel.npc_nombre)}
                                                className="w-8 h-8 rounded-full bg-neutral-700/80 hover:bg-red-600 text-neutral-400 hover:text-white flex items-center justify-center border border-neutral-600 hover:border-red-500 transition-colors"
                                                title="Eliminar relación (solo nivel 0 sin interacciones)"
                                            >
                                                <svg xmlns="http://www.w3.org/2000/svg" className="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                                                </svg>
                                            </button>
                                        )}
                                        <button
                                            onClick={(e) => handleInteraction(e, rel.relacion_id, 'NEGATIVA')}
                                            className="w-8 h-8 rounded-full bg-red-500/20 hover:bg-red-500/40 text-red-300 flex items-center justify-center border border-red-500/40 transition-colors"
                                            title="Registrar interacción negativa"
                                        >
                                            <svg xmlns="http://www.w3.org/2000/svg" className="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M20 12H4" />
                                            </svg>
                                        </button>
                                        <button
                                            onClick={(e) => handleInteraction(e, rel.relacion_id, 'POSITIVA')}
                                            className="w-8 h-8 rounded-full bg-green-500/20 hover:bg-green-500/40 text-green-300 flex items-center justify-center border border-green-500/40 transition-colors"
                                            title="Registrar interacción positiva"
                                        >
                                            <svg xmlns="http://www.w3.org/2000/svg" className="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4v16m8-8H4" />
                                            </svg>
                                        </button>
                                    </div>
                                )}
                            </div>
                        ))}
                    </div>

                    {relaciones.length === 0 && (
                        <div className="text-center py-12 border-2 border-dashed border-neutral-800 rounded-xl">
                            <p className="text-neutral-500">Este personaje no tiene relaciones con NPCs.</p>
                        </div>
                    )}
                </div>

                {/* Selected Advantages Block */}
                <div>
                    <div className="bg-neutral-800 rounded-2xl p-6 border border-white/5">
                        <h2 className="text-xl font-bold mb-4 flex items-center">
                            <span className="bg-clip-text text-transparent bg-gradient-to-r from-purple-400 to-pink-300">
                                Ventajas Seleccionadas
                            </span>
                        </h2>
                        
                        {pjData.relaciones.some(r => r.selecciones && r.selecciones.length > 0) ? (
                            <div className="space-y-6">
                                {pjData.relaciones
                                    .filter(r => r.selecciones && r.selecciones.length > 0)
                                    .map(rel => (
                                        <div key={rel.npc_id}>
                                            <h3 className="text-sm font-bold text-neutral-400 uppercase tracking-wider mb-2 flex items-center gap-2">
                                                {rel.npc_imagen_url && (
                                                    <img src={rel.npc_imagen_url} className="w-5 h-5 rounded-full object-cover" alt="" />
                                                )}
                                                {rel.npc_nombre}
                                            </h3>
                                            <div className="space-y-2">
                                                {rel.selecciones
                                                    .sort((a, b) => a.nivel_adquisicion - b.nivel_adquisicion)
                                                    .map(sel => (
                                                    <div key={sel.ventaja_id} className="bg-neutral-700/30 rounded-lg p-3 border border-white/5 hover:bg-neutral-700/50 transition-colors">
                                                        <div className="flex justify-between items-start">
                                                            <span className="font-bold text-jade-300">{sel.nombre}</span>
                                                            <div className="flex flex-col items-end">
                                                                {(() => {
                                                                    const level = sel.nivel_ventaja || 1;
                                                                    // Calculate hue: 120 (Green) -> 60 (Yellow) -> 0 (Red)
                                                                    // Map level 1..10 to hue 120..0
                                                                    const hue = Math.max(0, 120 - ((level - 1) * (120 / 9)));
                                                                    
                                                                    const dynamicStyle = {
                                                                        color: `hsl(${hue}, 90%, 75%)`,
                                                                        borderColor: `hsla(${hue}, 80%, 50%, 0.4)`,
                                                                        backgroundColor: `hsla(${hue}, 80%, 50%, 0.1)`
                                                                    };

                                                                    return (
                                                                        <span 
                                                                            className="text-xs font-bold px-2 py-0.5 rounded mb-1 border"
                                                                            style={dynamicStyle}
                                                                            title="Nivel de la ventaja"
                                                                        >
                                                                            Nvl {sel.nivel_ventaja || '?'}
                                                                        </span>
                                                                    );
                                                                })()}
                                                                <span className="text-[10px] text-neutral-500" title="Nivel al que se adquirió">
                                                                    (Adq. {sel.nivel_adquisicion})
                                                                </span>
                                                            </div>
                                                        </div>
                                                        {sel.descripcion && (
                                                            <p className="text-xs text-neutral-400 mt-1 line-clamp-2" title={sel.descripcion}>
                                                                {sel.descripcion}
                                                            </p>
                                                        )}
                                                    </div>
                                                ))}
                                            </div>
                                        </div>
                                    ))
                                }
                            </div>
                        ) : (
                            <p className="text-neutral-500 text-sm italic py-4 text-center">
                                No hay ventajas seleccionadas.
                            </p>
                        )}
                    </div>
                </div>
                </div>

            </div>
        </div>

       {/* Add Relacion Modal */}
       {showAddRelacionModal && (
        <div className="fixed inset-0 bg-black/80 flex items-center justify-center z-50 p-4" onClick={() => setShowAddRelacionModal(false)}>
            <div className="bg-neutral-800 rounded-2xl max-w-md w-full border border-white/10 shadow-2xl" onClick={(e) => e.stopPropagation()}>
                <div className="p-6 border-b border-white/10 flex justify-between items-start">
                    <h3 className="text-xl font-bold text-white">Añadir Nueva Relación</h3>
                    <button 
                        onClick={() => setShowAddRelacionModal(false)}
                        className="text-neutral-400 hover:text-white"
                    >
                        <svg xmlns="http://www.w3.org/2000/svg" className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                        </svg>
                    </button>
                </div>
                <form onSubmit={handleAddRelacion} className="p-6">
                    <div className="mb-6">
                        <label className="block text-sm font-semibold text-neutral-400 mb-2">Seleccionar NPC</label>
                        {availableNpcs.length > 0 ? (
                            <select
                                value={selectedNpcId}
                                onChange={(e) => setSelectedNpcId(e.target.value)}
                                className="w-full bg-neutral-700 text-white px-4 py-2 rounded-lg border border-neutral-600 focus:border-green-500 focus:outline-none"
                                required
                            >
                                <option value="">-- Selecciona un NPC --</option>
                                {availableNpcs.map(npc => (
                                    <option key={npc.npc_id} value={npc.npc_id}>
                                        {npc.nombre}
                                    </option>
                                ))}
                            </select>
                        ) : (
                            <p className="text-yellow-400 text-sm">No hay NPCs disponibles para añadir (todos tienen relación ya).</p>
                        )}
                    </div>
                    
                    <button 
                        type="submit"
                        disabled={!selectedNpcId}
                        className="w-full bg-green-600 hover:bg-green-700 disabled:bg-neutral-600 disabled:text-neutral-400 text-white font-bold py-2 px-4 rounded-lg transition-colors"
                    >
                        Crear Relación (Nivel 0)
                    </button>
                </form>
            </div>
        </div>
      )}

    </div>
  );
}

export default PjDetailPage;
