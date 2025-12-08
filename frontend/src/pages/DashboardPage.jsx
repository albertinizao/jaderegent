import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import NotificationModal from '../components/NotificationModal';
import { backupService } from '../services/backupService';
import { useMode } from '../context/ModeContext';
import ConfirmationModal from '../components/ConfirmationModal';

function DashboardPage() {
  const { isMaster } = useMode();
  const [count, setCount] = useState(0);
  const [pendingBackupFile, setPendingBackupFile] = useState(null);
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

  const [confirmModal, setConfirmModal] = useState({ 
      isOpen: false, 
      title: '', 
      message: '', 
      onConfirm: () => {},
      isDanger: false
  });

  const closeConfirm = () => {
      setConfirmModal(prev => ({ ...prev, isOpen: false }));
  };

  const handleDownloadBackup = async () => {
    try {
      const data = await backupService.downloadBackup();
      const blob = new Blob([JSON.stringify(data, null, 2)], { type: 'application/json' });
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `backup_jaderegent_${new Date().toISOString().split('T')[0]}.json`;
      document.body.appendChild(a);
      a.click();
      window.URL.revokeObjectURL(url);
      document.body.removeChild(a);
      showNotification("Éxito", "Backup descargado correctamente", 'success');
    } catch (error) {
      console.error(error);
      showNotification("Error", "Error al descargar el backup", 'error');
    }
  };

  const handleRestoreBackup = async (event) => {
    const file = event.target.files[0];
    if (!file) return;

    setPendingBackupFile(file);
    setConfirmModal({
        isOpen: true,
        title: 'Confirmar Restauración',
        message: '¿Estás seguro de que quieres restaurar este backup? Esto sobrescribirá cualquier dato existente que entre en conflicto.',
        isDanger: true,
        onConfirm: async () => {
            await performRestore(file);
            setPendingBackupFile(null);
        }
    });
    
    // Reset file input
    event.target.value = '';
  };

  const performRestore = async (file) => {
    
    try {
      const text = await file.text();
      const json = JSON.parse(text);
      await backupService.restoreBackup(json);
      showNotification("Éxito", "Backup restaurado correctamente", 'success');
    } catch (error) {
      console.error(error);
      showNotification("Error", "Error al restaurar el backup: " + error.message, 'error');
    }
  };

  return (
    <div className="min-h-screen bg-neutral-900 text-white flex flex-col items-center justify-center p-4 font-sans selection:bg-jade-500 selection:text-white overflow-hidden relative">
      <div className="absolute inset-0 bg-[radial-gradient(ellipse_at_center,_var(--tw-gradient-stops))] from-jade-900/20 via-neutral-900 to-neutral-900 z-0 pointer-events-none"></div>
      
      <main className="z-10 text-center space-y-8 max-w-2xl w-full">
        <div className="relative group cursor-default">
          <div className="absolute -inset-1 bg-gradient-to-r from-jade-600 to-teal-600 rounded-lg blur opacity-25 group-hover:opacity-75 transition duration-1000 group-hover:duration-200"></div>
          <div className="relative bg-neutral-900 ring-1 ring-white/10 rounded-lg p-8 leading-none flex items-center justify-center space-x-6">
             <h1 className="text-5xl md:text-7xl font-bold bg-clip-text text-transparent bg-gradient-to-r from-jade-400 to-teal-200 tracking-tight">
              Jade Regent
            </h1>
          </div>
        </div>

        <p className="text-xl md:text-2xl text-neutral-400 font-light max-w-lg mx-auto leading-relaxed">
          Sistema de Relaciones para la campaña <span className="text-jade-400 font-medium">El Regente de Jade</span>.
        </p>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-4 pt-8">
           <Link to="/create-pj" className="p-6 rounded-2xl bg-neutral-800/50 hover:bg-neutral-800/80 transition-colors border border-white/5 backdrop-blur-sm group block">
              <h3 className="text-lg font-semibold text-jade-300 mb-2 group-hover:translate-x-1 transition-transform">Crear Personaje</h3>
              <p className="text-neutral-400 text-sm">Comienza tu aventura registrando un nuevo PJ.</p>
           </Link>
           <Link to="/pjs" className="p-6 rounded-2xl bg-neutral-800/50 hover:bg-neutral-800/80 transition-colors border border-white/5 backdrop-blur-sm group block">
              <h3 className="text-lg font-semibold text-teal-300 mb-2 group-hover:translate-x-1 transition-transform">Ver Personajes</h3>
              <p className="text-neutral-400 text-sm">Gestiona tus relaciones con los NPCs clave de la historia.</p>
           </Link>
           <Link to="/npcs" className="p-6 rounded-2xl bg-neutral-800/50 hover:bg-neutral-800/80 transition-colors border border-white/5 backdrop-blur-sm group block">
              <h3 className="text-lg font-semibold text-purple-300 mb-2 group-hover:translate-x-1 transition-transform">Ver NPCs</h3>
              <p className="text-neutral-400 text-sm">Consulta la lista de NPCs disponibles y sus detalles.</p>
           </Link>
           {isMaster && (
               <Link to="/import-npc" className="p-6 rounded-2xl bg-neutral-800/50 hover:bg-neutral-800/80 transition-colors border-2 border-purple-500/20 hover:border-purple-500/50 backdrop-blur-sm group block">
                  <h3 className="text-lg font-semibold text-purple-300 mb-2 group-hover:translate-x-1 transition-transform">Importar NPC</h3>
                  <p className="text-neutral-400 text-sm">Carga nuevos personajes desde archivos JSON.</p>
               </Link>
           )}
           {isMaster && (
               <Link to="/relations-matrix" className="p-6 rounded-2xl bg-neutral-800/50 hover:bg-neutral-800/80 transition-colors border-2 border-pink-500/20 hover:border-pink-500/50 backdrop-blur-sm group block">
                  <h3 className="text-lg font-semibold text-pink-300 mb-2 group-hover:translate-x-1 transition-transform">Tabla de Relaciones</h3>
                  <p className="text-neutral-400 text-sm">Vista matricial de todas las relaciones PJ-NPC.</p>
               </Link>
           )}
        </div>
      </main>

      <footer className="absolute bottom-8 text-neutral-600 text-sm">
        &copy; 2025 Jade Regent Campaign Manager
      </footer>
      
      {isMaster && (
        <div className="fixed bottom-28 right-8 flex flex-row gap-2 z-50">
            <label className="bg-orange-600/90 hover:bg-orange-500 text-white p-3 rounded-full shadow-lg transition-all hover:scale-110 cursor-pointer" title="Restaurar Backup">
                <input type="file" accept=".json" onChange={handleRestoreBackup} className="hidden" />
                <svg xmlns="http://www.w3.org/2000/svg" className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M7 16a4 4 0 01-.88-7.903A5 5 0 1115.9 6L16 6a5 5 0 011 9.9M15 13l-3-3m0 0l-3 3m3-3v12" />
                </svg>
            </label>
            <button 
                onClick={handleDownloadBackup}
                className="bg-blue-600/90 hover:bg-blue-500 text-white p-3 rounded-full shadow-lg transition-all hover:scale-110"
                title="Descargar Backup"
            >
                <svg xmlns="http://www.w3.org/2000/svg" className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4" />
                </svg>
            </button>
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
  );
}

export default DashboardPage;
