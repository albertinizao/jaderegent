import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    host: '0.0.0.0',  // Listen on all network interfaces for LAN access
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://192.168.1.201:8080',
        changeOrigin: true,
        secure: false,
      },
      '/data': {
        target: 'http://192.168.1.201:8080',
        changeOrigin: true,
        secure: false,
      }
    }
  }
})
