const API_BASE = '/api/relaciones';

export const relacionService = {
  create: async (pjId, npcId) => {
    const response = await fetch(`${API_BASE}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ pj_id: pjId, npc_id: npcId }),
    });

    if (!response.ok) {
        throw new Error(`Error creating Relacion: ${response.statusText}`);
    }

    return response.json();
  },

  selectVentaja: async (relacionId, ventajaId) => {
    const response = await fetch(`${API_BASE}/${relacionId}/ventajas/${ventajaId}`, {
         method: 'POST',
         headers: {
             'Content-Type': 'application/json',
         }
     });

     if (!response.ok) {
         throw new Error(`Error selecting Ventaja: ${response.statusText}`);
     }

     return response.json();
  },

  addInteraccion: async (relacionId, tipo, nota = '') => {
    const response = await fetch(`${API_BASE}/${relacionId}/interacciones`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ tipo, nota }),
    });

    if (!response.ok) {
        // Try to read text if json fails or just status
       throw new Error(`Error registering interaction: ${response.statusText}`);
    }

    return response.json();
  },

  updateLevel: async (relacionId, increment) => {
    const response = await fetch(`${API_BASE}/${relacionId}/nivel?increment=${increment}`, {
        method: 'PATCH',
        headers: {
            'Content-Type': 'application/json',
        }
    });

    if (!response.ok) {
        throw new Error(`Error updating Level: ${response.statusText}`);
    }

    return response.json();
  }
};
