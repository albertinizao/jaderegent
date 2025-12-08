import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { relacionService } from '../services/relacionService';

function RelationsMatrixPage() {
  const [matrixData, setMatrixData] = useState(null);
  const [status, setStatus] = useState('loading');

  useEffect(() => {
    loadMatrix();
  }, []);

  const loadMatrix = async () => {
    try {
      const data = await relacionService.getMatrix();
      setMatrixData(data);
      setStatus('success');
    } catch (error) {
      console.error('Error loading matrix:', error);
      setStatus('error');
    }
  };

  const handleInteraction = async (relacionId, isPositive) => {
    try {
      await relacionService.registerInteraction(relacionId, isPositive);
      loadMatrix(); // Reload to get updated  data
    } catch (error) {
      console.error('Error registering interaction:', error);
      alert(`Error al registrar interacción: ${error.message}`);
    }
  };

  const getCell = (pjId, npcId) => {
    const key = `${pjId}:${npcId}`;
    return matrixData.matrix[key];
  };

  if (status === 'loading') {
    return (
      <div className="min-h-screen bg-neutral-900 text-white flex items-center justify-center">
        Cargando matriz de relaciones...
      </div>
    );
  }

  if (status === 'error' || !matrixData) {
    return (
      <div className="min-h-screen bg-neutral-900 text-white flex flex-col items-center justify-center p-4">
        <h2 className="text-2xl font-bold text-red-500 mb-4">Error</h2>
        <p className="text-neutral-400 mb-6">No se pudo cargar la matriz de relaciones.</p>
        <Link to="/dashboard" className="text-purple-400 hover:text-purple-300">
          Volver al Dashboard
        </Link>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-neutral-900 text-white p-4 md:p-8 font-sans">
      <div className="max-w-full mx-auto">
        <header className="mb-6 flex justify-between items-center">
          <div>
            <Link
              to="/dashboard"
              className="inline-flex items-center text-neutral-400 hover:text-white transition-colors mb-4"
            >
              <svg
                xmlns="http://www.w3.org/2000/svg"
                className="h-5 w-5 mr-2"
                fill="none"
                viewBox="0 0 24 24"
                stroke="currentColor"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={2}
                  d="M10 19l-7-7m0 0l7-7m-7 7h18"
                />
              </svg>
              Volver al Dashboard
            </Link>
            <h1 className="text-3xl font-bold bg-clip-text text-transparent bg-gradient-to-r from-purple-400 to-pink-300">
              Matriz de Relaciones
            </h1>
            <p className="text-neutral-400 text-sm mt-2">
              {matrixData.pjs.length} Personajes × {matrixData.npcs.length} NPCs
            </p>
          </div>
        </header>

        {/* Scrollable Container */}
        <div className="overflow-auto border border-neutral-800 rounded-lg">
          <table className="w-full border-collapse">
            <thead>
              <tr className="bg-neutral-800 sticky top-0 z-10">
                <th className="p-3 text-left font-semibold text-neutral-400 uppercase text-xs tracking-wide border-b border-r border-neutral-700 sticky left-0 bg-neutral-800 z-20 min-w-[200px]">
                  NPC / PJ
                </th>
                {matrixData.pjs.map((pj) => (
                  <th
                    key={pj.pj_id}
                    className="p-3 text-center font-semibold border-b border-r border-neutral-700 min-w-[140px]"
                  >
                    <Link
                      to={`/pjs/${pj.pj_id}`}
                      className="flex flex-col items-center gap-2 hover:opacity-80 transition-opacity"
                    >
                      {pj.imagen_url ? (
                        <img
                          src={pj.imagen_url}
                          alt={pj.nombre_display}
                          className="w-10 h-10 rounded-full object-cover border-2 border-neutral-600 hover:border-jade-400 transition-colors"
                        />
                      ) : (
                        <div className="w-10 h-10 rounded-full bg-neutral-700 flex items-center justify-center text-white font-bold text-sm border-2 border-neutral-600 hover:border-jade-400 transition-colors">
                          {pj.nombre_display?.substring(0, 2).toUpperCase()}
                        </div>
                      )}
                      <span className="text-xs text-white font-medium hover:text-jade-300 transition-colors">
                        {pj.nombre_display}
                      </span>
                    </Link>
                  </th>
                ))}
              </tr>
            </thead>
            <tbody>
              {matrixData.npcs.map((npc) => (
                <tr key={npc.npc_id} className="hover:bg-neutral-800/50">
                  <td className="p-3 font-semibold border-b border-r border-neutral-700 sticky left-0 bg-neutral-900 z-10">
                    <Link
                      to={`/npcs/${npc.npc_id}`}
                      className="flex items-center gap-3 hover:opacity-80 transition-opacity"
                    >
                      {npc.imagen_url ? (
                        <img
                          src={npc.imagen_url}
                          alt={npc.nombre}
                          className="w-10 h-10 rounded-full object-cover border-2 border-neutral-600 hover:border-purple-400 transition-colors"
                        />
                      ) : (
                        <div className="w-10 h-10 rounded-full bg-neutral-700 flex items-center justify-center text-white font-bold text-sm border-2 border-neutral-600 hover:border-purple-400 transition-colors">
                          {npc.nombre?.substring(0, 2).toUpperCase()}
                        </div>
                      )}
                      <div>
                        <div className="text-white text-sm hover:text-purple-300 transition-colors">{npc.nombre}</div>
                        <div className="text-xs text-neutral-500">
                          Nivel máx: {npc.nivel_maximo}
                        </div>
                      </div>
                    </Link>
                  </td>
                  {matrixData.pjs.map((pj) => {
                    const cell = getCell(pj.pj_id, npc.npc_id);
                    
                    if (!cell || !cell.existe_relacion) {
                      return (
                        <td
                          key={`${pj.pj_id}-${npc.npc_id}`}
                          className="p-3 text-center border-b border-r border-neutral-700 bg-neutral-900/30"
                        >
                          <span className="text-neutral-600 text-xs">—</span>
                        </td>
                      );
                    }

                    const nivelColor = 
                      cell.nivel_actual >= 7 ? 'text-emerald-400' :
                      cell.nivel_actual >= 4 ? 'text-yellow-400' :
                      cell.nivel_actual >= 1 ? 'text-blue-400' :
                      'text-neutral-500';

                    return (
                      <td
                        key={`${pj.pj_id}-${npc.npc_id}`}
                        className="p-2 text-center border-b border-r border-neutral-700"
                      >
                        <div className="flex flex-col gap-1 items-center">
                          {/* Level */}
                          <div className={`text-lg font-bold ${nivelColor}`}>
                            {cell.nivel_actual}
                            <span className="text-neutral-600 text-xs font-normal">
                              /{cell.nivel_maximo}
                            </span>
                          </div>
                          
                          {/* Interactions count */}
                          <div className="text-xs text-neutral-400">
                            {cell.contador_interacciones} inter.
                          </div>
                          
                          {/* Action buttons */}
                          <div className="flex gap-1">
                            <button
                              onClick={() => handleInteraction(cell.relacion_id, false)}
                              className="w-6 h-6 flex items-center justify-center bg-red-900/30 hover:bg-red-900/50 text-red-400 rounded border border-red-500/30 hover:border-red-500/60 transition-colors text-xs font-bold"
                              title="Interacción negativa"
                            >
                              −
                            </button>
                            <button
                              onClick={() => handleInteraction(cell.relacion_id, true)}
                              className="w-6 h-6 flex items-center justify-center bg-green-900/30 hover:bg-green-900/50 text-green-400 rounded border border-green-500/30 hover:border-green-500/60 transition-colors text-xs font-bold"
                              title="Interacción positiva"
                            >
                              +
                            </button>
                          </div>
                        </div>
                      </td>
                    );
                  })}
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        {/* Legend */}
        <div className="mt-6 p-4 bg-neutral-800 rounded-lg border border-neutral-700">
          <h3 className="text-sm font-bold text-neutral-300 mb-3">Leyenda</h3>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-3 text-xs">
            <div className="flex items-center gap-2">
              <div className="w-4 h-4 bg-neutral-900/30 border border-neutral-700 rounded"></div>
              <span className="text-neutral-400">Sin relación</span>
            </div>
            <div className="flex items-center gap-2">
              <div className="font-bold text-neutral-500">0-3</div>
              <span className="text-neutral-400">Nivel bajo</span>
            </div>
            <div className="flex items-center gap-2">
              <div className="font-bold text-blue-400">4-6</div>
              <span className="text-neutral-400">Nivel medio</span>
            </div>
            <div className="flex items-center gap-2">
              <div className="font-bold text-yellow-400">7-9</div>
              <span className="text-neutral-400">Nivel avanzado</span>
            </div>
            <div className="flex items-center gap-2">
              <div className="font-bold text-emerald-400">10</div>
              <span className="text-neutral-400">Nivel máximo</span>
            </div>
            <div className="flex items-center gap-2">
              <div className="flex gap-1">
                <div className="w-4 h-4 bg-red-900/30 border border-red-500/30 rounded flex items-center justify-center text-red-400">−</div>
                <div className="w-4 h-4 bg-green-900/30 border border-green-500/30 rounded flex items-center justify-center text-green-400">+</div>
              </div>
              <span className="text-neutral-400">Registrar interacción</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default RelationsMatrixPage;
