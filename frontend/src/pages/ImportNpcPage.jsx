import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { npcService } from '../services/npcService';
import { useMode } from '../context/ModeContext';

function ImportNpcPage() {
  const { isMaster } = useMode();
  const navigate = useNavigate();
  const [file, setFile] = useState(null);
  const [status, setStatus] = useState('idle'); // idle, uploading, success, error
  const [message, setMessage] = useState('');

  if (!isMaster) {
      return (
        <div className="min-h-screen bg-neutral-900 text-white flex flex-col items-center justify-center p-4">
            <h2 className="text-2xl font-bold text-red-500 mb-4">Acceso Denegado</h2>
            <p className="text-neutral-400 mb-6">Esta funcionalidad es solo para el Máster.</p>
            <Link to="/" className="text-jade-400 hover:text-jade-300">Volver al Inicio</Link>
        </div>
      );
  }

  const handleFileChange = (e) => {
    setFile(e.target.files[0]);
    setStatus('idle');
    setMessage('');
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!file) {
        setMessage('Por favor, selecciona un archivo JSON.');
        setStatus('error');
        return;
    }

    setStatus('uploading');
    try {
      const response = await npcService.importNpc(file);
      setStatus('success');
      setMessage(response.data || 'NPC importado correctamente.');
    } catch (error) {
      console.error(error);
      setStatus('error');
      setMessage('Error al importar el NPC. Verifica el formato del JSON.');
    }
  };

  return (
    <div className="min-h-screen bg-neutral-900 text-white p-8 font-sans">
      <div className="max-w-md mx-auto bg-neutral-800 rounded-xl p-8 border border-white/5 shadow-xl">
        <div className="mb-6 flex justify-between items-center">
            <h2 className="text-3xl font-bold text-transparent bg-clip-text bg-gradient-to-r from-purple-400 to-pink-200">
            Importar NPC
            </h2>
            <Link to="/" className="text-neutral-400 hover:text-white transition-colors">
                ✕
            </Link>
        </div>

        {status === 'success' && (
          <div className="mb-4 p-4 bg-green-900/50 border border-green-500/50 text-green-200 rounded-lg">
            {message}
          </div>
        )}

        {status === 'error' && (
          <div className="mb-4 p-4 bg-red-900/50 border border-red-500/50 text-red-200 rounded-lg">
            {message}
          </div>
        )}

        <form onSubmit={handleSubmit} className="space-y-6">
          <div className="border-2 border-dashed border-neutral-700 rounded-lg p-8 text-center hover:border-purple-500 transition-colors cursor-pointer relative">
             <input
               type="file"
               accept=".json"
               onChange={handleFileChange}
               className="absolute inset-0 w-full h-full opacity-0 cursor-pointer"
             />
             <div className="pointer-events-none">
                <svg xmlns="http://www.w3.org/2000/svg" className="h-12 w-12 mx-auto text-neutral-500 mb-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-8l-4-4m0 0L8 8m4-4v12" />
                </svg>
                <p className="text-neutral-400">
                    {file ? file.name : "Arrastra un archivo JSON o haz click para seleccionar"}
                </p>
             </div>
          </div>

          <button
            type="submit"
            disabled={status === 'uploading' || !file}
            className="w-full bg-purple-600 hover:bg-purple-500 disabled:opacity-50 disabled:cursor-not-allowed text-white font-bold py-3 rounded-lg transition-colors shadow-lg shadow-purple-900/20"
          >
            {status === 'uploading' ? 'Importando...' : 'Importar NPC'}
          </button>
        </form>
      </div>
    </div>
  );
}

export default ImportNpcPage;
