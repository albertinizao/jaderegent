import React, { useEffect, useState } from 'react';
import { useParams, Link, useLocation, useNavigate } from 'react-router-dom';
import { pjService } from '../services/pjService';
import { relacionService } from '../services/relacionService';
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

  const { pj, relaciones } = pjData;

  return (
    <div className="min-h-screen bg-neutral-900 text-white p-4 md:p-8 font-sans">
      <div className="max-w-6xl mx-auto">
        <header className="mb-8 flex justify-between items-center">
            <Link to="/pjs" className="inline-flex items-center text-neutral-400 hover:text-white transition-colors">
                <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M10 19l-7-7m0 0l7-7m-7 7h18" />
                </svg>
                Volver a Personajes
            </Link>
            <div className="flex gap-3">
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

            {/* Relaciones List */}
            <div className="lg:col-span-2">
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
                                    fromLabel: `Volver a ${pj.nombre_display}` 
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
                                    <div className="flex-1">
                                        <h3 className="text-lg font-bold text-white flex justify-between">
                                            {rel.npc_nombre}
                                            {rel.contador_interacciones !== 0 && (
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

                                {/* Progress Bar */}
                                <div className="mb-2">
                                    <div className="w-full bg-neutral-700 rounded-full h-2">
                                        <div 
                                            className="bg-gradient-to-r from-jade-600 to-teal-500 h-2 rounded-full transition-all"
                                            style={{ width: `${(rel.nivel_actual / rel.nivel_maximo) * 100}%` }}
                                        ></div>
                                    </div>
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
        </div>
      </div>
    </div>
  );
}

export default PjDetailPage;
