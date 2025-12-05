import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { pjService } from '../services/pjService';

function CreatePjPage() {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    nombre_display: '',
    nota_opcional: '',
    imagen_url: ''
  });
  const [status, setStatus] = useState('idle'); // idle, loading, success, error

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setStatus('loading');
    try {
      await pjService.create(formData);
      setStatus('success');
      navigate('/pjs');
    } catch (error) {
      console.error(error);
      setStatus('error');
    }
  };

  return (
    <div className="min-h-screen bg-neutral-900 text-white p-8 font-sans">
      <div className="max-w-md mx-auto bg-neutral-800 rounded-xl p-8 border border-white/5 shadow-xl">
        <div className="mb-6 flex justify-between items-center">
            <h2 className="text-3xl font-bold text-transparent bg-clip-text bg-gradient-to-r from-jade-400 to-teal-200">
            Crear Nuevo PJ
            </h2>
            <Link to="/" className="text-neutral-400 hover:text-white transition-colors">
                ✕
            </Link>
        </div>
        
        {status === 'success' && (
          <div className="mb-4 p-4 bg-jade-900/50 border border-jade-500/50 text-jade-200 rounded-lg">
            ¡PJ creado correctamente!
          </div>
        )}

        {status === 'error' && (
          <div className="mb-4 p-4 bg-red-900/50 border border-red-500/50 text-red-200 rounded-lg">
            Error al crear el PJ. Inténtalo de nuevo.
          </div>
        )}

        <form onSubmit={handleSubmit} className="space-y-6">
          <div>
            <label className="block text-sm font-medium text-neutral-400 mb-1">Nombre</label>
            <input
              type="text"
              name="nombre_display"
              value={formData.nombre_display}
              onChange={handleChange}
              required
              className="w-full bg-neutral-900 border border-neutral-700 rounded-lg px-4 py-2 focus:ring-2 focus:ring-jade-500 focus:border-transparent outline-none transition-all"
              placeholder="Ej: Ameiko Kaijitsu"
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-neutral-400 mb-1">Nota Opcional</label>
            <textarea
              name="nota_opcional"
              value={formData.nota_opcional}
              onChange={handleChange}
              rows="3"
              className="w-full bg-neutral-900 border border-neutral-700 rounded-lg px-4 py-2 focus:ring-2 focus:ring-jade-500 focus:border-transparent outline-none transition-all"
              placeholder="Descripción breve..."
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-neutral-400 mb-1">URL Imagen (Local)</label>
            <input
              type="text"
              name="imagen_url"
              value={formData.imagen_url}
              onChange={handleChange}
              className="w-full bg-neutral-900 border border-neutral-700 rounded-lg px-4 py-2 focus:ring-2 focus:ring-jade-500 focus:border-transparent outline-none transition-all"
              placeholder="/data/images/pj/ameiko.jpg"
            />
          </div>

          <button
            type="submit"
            disabled={status === 'loading'}
            className="w-full bg-jade-600 hover:bg-jade-500 disabled:opacity-50 disabled:cursor-not-allowed text-white font-bold py-3 rounded-lg transition-colors shadow-lg shadow-jade-900/20"
          >
            {status === 'loading' ? 'Creando...' : 'Crear Personaje'}
          </button>
        </form>
      </div>
    </div>
  );
}

export default CreatePjPage;
