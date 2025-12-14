import React, { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import { pjService } from '../services/pjService';

const PrintAdvantagesPage = () => {
    const { id } = useParams();
    const [data, setData] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const loadData = async () => {
            try {
                const result = await pjService.getPrintableAdvantages(id);
                setData(result);
            } catch (err) {
                console.error(err);
                setError('Error al cargar datos');
            } finally {
                setLoading(false);
            }
        };
        loadData();
    }, [id]);

    if (loading) return <div className="p-8 text-center text-neutral-500">Cargando hoja de ventajas...</div>;
    if (error) return <div className="p-8 text-center text-red-600 font-bold">{error}</div>;
    if (!data) return <div className="p-8 text-center">No se encontraron datos para este personaje.</div>;

    const handlePrint = () => {
        window.print();
    };

    return (
        <div className="min-h-screen bg-white text-black p-8 font-serif leading-relaxed print:p-0">
            {/* Header / Actions - Hidden on print */}
            <div className="print:hidden mb-8 flex justify-between items-center border-b pb-4 bg-neutral-100 p-4 rounded-lg shadow-sm">
                <Link to={`/pjs/${id}`} className="text-blue-600 hover:text-blue-800 font-sans flex items-center gap-2">
                    <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
                        <path fillRule="evenodd" d="M9.707 16.707a1 1 0 01-1.414 0l-6-6a1 1 0 010-1.414l6-6a1 1 0 011.414 1.414L5.414 9H17a1 1 0 110 2H5.414l4.293 4.293a1 1 0 010 1.414z" clipRule="evenodd" />
                    </svg>
                    Volver a la ficha
                </Link>
                <div className="flex items-center gap-4">
                     <span className="text-sm text-neutral-500 font-sans hidden md:inline">
                        Consejo: Activa "Gráficos de fondo" en las opciones de impresión si deseas ver imágenes.
                    </span>
                   <button 
                        onClick={handlePrint}
                        className="bg-neutral-900 text-white px-6 py-2 rounded hover:bg-neutral-700 transition font-sans font-bold flex items-center gap-2 shadow-lg"
                    >
                        <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17 17h2a2 2 0 002-2v-4a2 2 0 00-2-2H5a2 2 0 00-2 2v4a2 2 0 002 2h2m2 4h6a2 2 0 002-2v-4a2 2 0 00-2-2H9a2 2 0 00-2 2v4a2 2 0 002 2zm8-12V5a2 2 0 00-2-2H9a2 2 0 00-2 2v4h10z" />
                        </svg>
                        Imprimir
                    </button>
                </div>
            </div>

            {/* Printable Content */}
            <div className="max-w-4xl mx-auto print:w-full">
                <header className="mb-4 flex items-end justify-between">
                    <h1 className="text-3xl font-bold mb-0">{data.pj_nombre}</h1>
                    {/* Optional: Add image if needed, but keeping it text-heavy for print is safer */}
                </header>

                {data.npcs.length === 0 ? (
                    <div className="text-center py-12 border border-dashed border-neutral-300 rounded-lg">
                        <p className="italic text-neutral-500 text-xl">Este personaje aún no ha obtenido ventajas de sus relaciones.</p>
                    </div>
                ) : (
                    <div className="space-y-4">
                        {data.npcs.map((npc, idx) => (
                            <div key={idx} className="break-inside-avoid">
                                <div className="flex items-center gap-2 mb-1">
                                    <h3 className="text-xl font-bold">
                                        {npc.npc_nombre}
                                    </h3>
                                    <span className="text-sm bg-neutral-200 px-2 py-0.5 rounded-full print:border print:border-neutral-400">NPC</span>
                                </div>
                                
                                <div className="grid grid-cols-1 gap-1 ml-4">
                                    {npc.ventajas.map((ventaja, vIdx) => (
                                        <div key={vIdx} className="relative pl-3 border-l-2 border-neutral-300">
                                            <div className="font-bold text-base leading-tight">{ventaja.nombre}</div>
                                            <div className="text-neutral-800 text-sm whitespace-pre-wrap leading-tight text-justify">
                                                {ventaja.descripcion}
                                            </div>
                                        </div>
                                    ))}
                                </div>
                            </div>
                        ))}
                    </div>
                )}
                
                <footer className="mt-12 pt-4 border-t text-xs text-center text-neutral-400 print:block hidden break-before-avoid">
                    Generado el {new Date().toLocaleDateString()} - Sistema de Gestión Jade Regent
                </footer>
            </div>
            
            {/* Print Styles Injection for extra safety */}
            <style>{`
                @media print {
                    @page { margin: 2cm; }
                    body { -webkit-print-color-adjust: exact; }
                }
            `}</style>
        </div>
    );
};

export default PrintAdvantagesPage;
