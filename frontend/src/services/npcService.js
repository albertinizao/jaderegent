import axios from 'axios';

const API_URL = '/api/npc';

export const npcService = {
  importNpc: async (file) => {
    const formData = new FormData();
    formData.append('file', file);
    return axios.post(`${API_URL}/import`, formData, {
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    });
  },
  getAll: async () => {
    return axios.get(API_URL).then(res => res.data);
  },
  getById: async (id) => {
    return axios.get(`${API_URL}/${id}`).then(res => res.data);
  },
  delete: async (id) => {
    return axios.delete(`${API_URL}/${id}`);
  }
};
