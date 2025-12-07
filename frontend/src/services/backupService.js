const API_BASE = '/api/backup';

export const backupService = {
  downloadBackup: async () => {
    const response = await fetch(`${API_BASE}`);
    if (!response.ok) throw new Error('Error downloading backup');
    return response.json();
  },

  restoreBackup: async (backupData) => {
    const response = await fetch(`${API_BASE}/restore`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(backupData),
    });
    if (!response.ok) throw new Error('Error restoring backup');
  }
};
