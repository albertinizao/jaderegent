const API_BASE = '/api';

export const pjService = {
  create: async (data) => {
    const response = await fetch(`${API_BASE}/pj`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(data),
    });

    if (!response.ok) {
      throw new Error(`Error creating PJ: ${response.statusText}`);
    }

    return response.json();
  },

  getAll: async () => {
    const response = await fetch(`${API_BASE}/pj`);
    if (!response.ok) {
      throw new Error(`Error fetching PJs: ${response.statusText}`);
    }
    return response.json();
  }
};
