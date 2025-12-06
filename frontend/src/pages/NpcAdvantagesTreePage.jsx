import React, { useEffect, useState, useCallback } from 'react';
import { useParams, Link } from 'react-router-dom';
import ReactFlow, {
  MiniMap,
  Controls,
  Background,
  useNodesState,
  useEdgesState,
} from 'reactflow';
import dagre from 'dagre';
import 'reactflow/dist/style.css';
import { npcService } from '../services/npcService';

const dagreGraph = new dagre.graphlib.Graph();
dagreGraph.setDefaultEdgeLabel(() => ({}));

const nodeWidth = 280;
const nodeHeight = 80;

const getLayoutedElements = (nodes, edges, direction = 'TB') => {
  const isHorizontal = direction === 'LR';
  dagreGraph.setGraph({ 
    rankdir: direction, 
    nodesep: isHorizontal ? 100 : 80, 
    ranksep: isHorizontal ? 150 : 120,
    edgesep: 50,
  });

  nodes.forEach((node) => {
    dagreGraph.setNode(node.id, { width: nodeWidth, height: nodeHeight });
  });

  edges.forEach((edge) => {
    dagreGraph.setEdge(edge.source, edge.target);
  });

  dagre.layout(dagreGraph);

  nodes.forEach((node) => {
    const nodeWithPosition = dagreGraph.node(node.id);
    node.targetPosition = isHorizontal ? 'left' : 'top';
    node.sourcePosition = isHorizontal ? 'right' : 'bottom';
    node.position = {
      x: nodeWithPosition.x - nodeWidth / 2,
      y: nodeWithPosition.y - nodeHeight / 2,
    };
    return node;
  });

  return { nodes, edges };
};

const getLevelColor = (level) => {
  const colors = {
    1: 'from-emerald-600 to-green-700',
    2: 'from-cyan-600 to-blue-700',
    3: 'from-blue-600 to-indigo-700',
    4: 'from-indigo-600 to-purple-700',
    5: 'from-purple-600 to-pink-700',
    6: 'from-pink-600 to-rose-700',
    7: 'from-rose-600 to-red-700',
    8: 'from-red-600 to-orange-700',
    9: 'from-orange-600 to-amber-700',
    10: 'from-amber-600 to-yellow-700',
  };
  return colors[level] || 'from-neutral-600 to-neutral-700';
};

function NpcAdvantagesTreePage() {
  const { id } = useParams();
  const [npcData, setNpcData] = useState(null);
  const [nodes, setNodes, onNodesChange] = useNodesState([]);
  const [edges, setEdges, onEdgesChange] = useEdgesState([]);
  const [status, setStatus] = useState('loading');
  const [selectedVentaja, setSelectedVentaja] = useState(null);

  useEffect(() => {
    loadNpcData();
  }, [id]);

  const openVentajaModal = (ventajaId) => {
    const ventaja = npcData.ventajas.find(v => (v.ventaja_id || v.ventajaId) === ventajaId);
    if (ventaja) {
      setSelectedVentaja(ventaja);
    }
  };
  
  const closeVentajaModal = () => {
    setSelectedVentaja(null);
  };

  const loadNpcData = async () => {
    try {
      const data = await npcService.getById(id);
      setNpcData(data);
      
      // Sort ventajas by level and then by name for better grouping
      const sortedVentajas = [...data.ventajas].sort((a, b) => {
        const levelA = a.min_nivel_relacion || a.minNivelRelacion;
        const levelB = b.min_nivel_relacion || b.minNivelRelacion;
        
        // First sort by level
        if (levelA !== levelB) {
          return levelA - levelB;
        }
        
        // Then sort by name to group similar advantages
        return a.nombre.localeCompare(b.nombre);
      });
      
      // Create nodes from advantages
      const advantageNodes = sortedVentajas.map((ventaja) => ({
        id: ventaja.ventaja_id || ventaja.ventajaId,
        data: { 
          label: (
            <div className="text-center w-full">
              <div className="font-semibold text-sm">{ventaja.nombre}</div>
              <div className="text-xs text-white/70 mt-1">Nivel {ventaja.min_nivel_relacion || ventaja.minNivelRelacion}</div>
            </div>
          ),
          level: ventaja.min_nivel_relacion || ventaja.minNivelRelacion
        },
        position: { x: 0, y: 0 },
        style: {
          background: `linear-gradient(135deg, var(--tw-gradient-stops))`,
          border: '2px solid rgba(255, 255, 255, 0.2)',
          borderRadius: '8px',
          padding: '10px',
          width: nodeWidth,
          height: nodeHeight,
          color: 'white',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          cursor: 'pointer',
        },
        className: `bg-gradient-to-br ${getLevelColor(ventaja.min_nivel_relacion || ventaja.minNivelRelacion)}`,
      }));

      // Create edges from prerequisites
      const advantageEdges = [];
      data.ventajas.forEach((ventaja) => {
        if (ventaja.prerequisitos && ventaja.prerequisitos.length > 0) {
          const isOrLogic = (ventaja.prerequisitos_operator || ventaja.prerequisitosOperator || 'AND').toUpperCase() === 'OR';
          
          ventaja.prerequisitos.forEach((prereqId) => {
            advantageEdges.push({
              id: `${prereqId}-${ventaja.ventaja_id || ventaja.ventajaId}`,
              source: prereqId,
              target: ventaja.ventaja_id || ventaja.ventajaId,
              type: 'default',
              animated: false,
              style: { 
                stroke: isOrLogic ? '#f59e0b' : '#818cf8',  // Orange for OR, Indigo for AND
                strokeWidth: 2.5,
                strokeDasharray: isOrLogic ? '5,5' : 'none', // Dashed for OR, solid for AND
              },
              markerEnd: {
                type: 'arrowclosed',
                color: isOrLogic ? '#f59e0b' : '#818cf8',
                width: 20,
                height: 20,
              },
            });
          });
        }
      });

      const { nodes: layoutedNodes, edges: layoutedEdges } = getLayoutedElements(
        advantageNodes,
        advantageEdges
      );

      setNodes(layoutedNodes);
      setEdges(layoutedEdges);
      setStatus('success');
    } catch (error) {
      console.error(error);
      setStatus('error');
    }
  };

  const onLayout = useCallback(
    (direction) => {
      const { nodes: layoutedNodes, edges: layoutedEdges } = getLayoutedElements(
        nodes,
        edges,
        direction
      );

      setNodes([...layoutedNodes]);
      setEdges([...layoutedEdges]);
    },
    [nodes, edges]
  );

  if (status === 'loading') {
    return (
      <div className="min-h-screen bg-neutral-900 text-white flex items-center justify-center">
        <div className="text-center animate-pulse">
          <div className="text-2xl mb-2">Cargando árbol de ventajas...</div>
        </div>
      </div>
    );
  }

  if (status === 'error') {
    return (
      <div className="min-h-screen bg-neutral-900 text-white flex items-center justify-center">
        <div className="text-center">
          <div className="text-2xl text-red-400 mb-4">Error al cargar el árbol</div>
          <Link to={`/npcs/${id}`} className="text-purple-400 hover:text-purple-300">
            Volver a la ficha del NPC
          </Link>
        </div>
      </div>
    );
  }

  return (
    <div className="h-screen bg-neutral-900 text-white flex flex-col">
      <header className="bg-neutral-800 border-b border-white/10 p-4 flex justify-between items-center">
        <div className="flex items-center gap-4">
          <Link 
            to={`/npcs/${id}`} 
            className="bg-neutral-700 hover:bg-neutral-600 text-neutral-300 hover:text-white p-2 rounded-lg transition-colors"
          >
            <svg xmlns="http://www.w3.org/2000/svg" className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M10 19l-7-7m0 0l7-7m-7 7h18" />
            </svg>
          </Link>
          <div>
            <h1 className="text-2xl font-bold text-transparent bg-clip-text bg-gradient-to-r from-purple-400 to-pink-200">
              Árbol de Ventajas
            </h1>
            <p className="text-neutral-400 text-sm">{npcData?.npc?.nombre}</p>
          </div>
        </div>
        <div className="flex gap-2">
          <button
            onClick={() => onLayout('TB')}
            className="px-4 py-2 bg-purple-900/40 hover:bg-purple-800/60 text-purple-300 rounded-lg transition-colors text-sm"
          >
            Vertical
          </button>
          <button
            onClick={() => onLayout('LR')}
            className="px-4 py-2 bg-purple-900/40 hover:bg-purple-800/60 text-purple-300 rounded-lg transition-colors text-sm"
          >
            Horizontal
          </button>
        </div>
      </header>

      <div className="flex-1">
        <ReactFlow
          nodes={nodes}
          edges={edges}
          onNodesChange={onNodesChange}
          onEdgesChange={onEdgesChange}
          onNodeClick={(event, node) => openVentajaModal(node.id)}
          fitView
          attributionPosition="bottom-left"
        >
          <Controls />
          <MiniMap 
            nodeColor={(node) => {
              const level = node.data.level;
              return level <= 3 ? '#10b981' : level <= 6 ? '#3b82f6' : level <= 9 ? '#8b5cf6' : '#f59e0b';
            }}
            maskColor="rgba(0, 0, 0, 0.6)"
          />
          <Background color="#525252" gap={16} />
        </ReactFlow>
      </div>
      
      {/* Modal for Ventaja Details */}
      {selectedVentaja && npcData && (
          <div className="fixed inset-0 bg-black/80 flex items-center justify-center z-50 p-4" onClick={closeVentajaModal}>
              <div className="bg-neutral-800 rounded-2xl max-w-2xl w-full border border-white/10 shadow-2xl" onClick={(e) => e.stopPropagation()}>
                  <div className="p-6 border-b border-white/10 flex justify-between items-start">
                      <div>
                          <h3 className="text-2xl font-bold text-white mb-2">{selectedVentaja.nombre}</h3>
                          <span className={`text-xs font-bold px-3 py-1 rounded ${
                              (selectedVentaja.min_nivel_relacion || selectedVentaja.minNivelRelacion) <= 3 ? 'bg-green-900/40 text-green-300' : 
                              (selectedVentaja.min_nivel_relacion || selectedVentaja.minNivelRelacion) <= 6 ? 'bg-yellow-900/40 text-yellow-300' : 'bg-red-900/40 text-red-300'
                          }`}>
                              Nivel {selectedVentaja.min_nivel_relacion || selectedVentaja.minNivelRelacion}
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
                          <p className="text-neutral-300">{selectedVentaja.descripcion_larga || selectedVentaja.descripcionLarga}</p>
                      </div>
                      
                      {selectedVentaja.prerequisitos && selectedVentaja.prerequisitos.length > 0 && (
                          <div>
                              <h4 className="text-sm font-semibold text-neutral-400 uppercase tracking-wide mb-2">
                                  Prerrequisitos {(selectedVentaja.prerequisitos_operator || selectedVentaja.prerequisitosOperator) === 'OR' ? '(cualquiera)' : '(todos)'}
                              </h4>
                              <div className="flex flex-wrap gap-2">
                                  {selectedVentaja.prerequisitos.map(prereqId => {
                                      const prereqVentaja = npcData.ventajas.find(v => (v.ventaja_id || v.ventajaId) === prereqId);
                                      const displayName = prereqVentaja ? prereqVentaja.nombre : prereqId;
                                      const isOrLogic = (selectedVentaja.prerequisitos_operator || selectedVentaja.prerequisitosOperator) === 'OR';
                                      
                                      return (
                                          <button
                                              key={prereqId}
                                              onClick={() => openVentajaModal(prereqId)}
                                              className={`text-xs px-2 py-1 rounded border cursor-pointer hover:scale-105 transition-transform ${
                                                  isOrLogic 
                                                      ? 'text-amber-300 bg-amber-900/20 border-amber-500/10 hover:bg-amber-900/40' 
                                                      : 'text-purple-300 bg-purple-900/20 border-purple-500/10 hover:bg-purple-900/40'
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
  );
}

export default NpcAdvantagesTreePage;
