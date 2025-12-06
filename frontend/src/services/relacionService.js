const API_BASE = '/api/relaciones';

export const relacionService = {
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
  }
};
