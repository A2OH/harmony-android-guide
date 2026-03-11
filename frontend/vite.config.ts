import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import { resolve } from 'path'
import { copyFileSync } from 'fs'

const copy404Plugin = () => ({
  name: 'copy-404',
  closeBundle() {
    // Copy index.html to 404.html for GitHub Pages SPA routing
    copyFileSync(resolve('dist/index.html'), resolve('dist/404.html'));
  },
});

export default defineConfig({
  plugins: [react(), copy404Plugin()],
  server: {
    proxy: {
      '/api': 'http://localhost:8080',
    },
  },
  optimizeDeps: {
    exclude: ['sql.js'],
  },
  build: {
    target: 'esnext',
  },
})
