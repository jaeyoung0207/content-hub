import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import tailwindcss from '@tailwindcss/vite';
import { visualizer } from 'rollup-plugin-visualizer';

// https://vite.dev/config/
export default defineConfig({
  plugins: [react(), tailwindcss(), visualizer()],
  server: {
    // develope only
    host: true,
    port: 3000,
  },
  resolve: {
    alias: [
      { find: '@', replacement: '/src' },
      { find: '@api', replacement: '/src/api' },
      { find: '@assets', replacement: '/src/assets' },
      { find: '@components', replacement: '/src/components' },
    ],
  },
  build: {
    rollupOptions: {
      output: {
        manualChunks(id) {
          if (id.includes('node_modules')) {
            if (id.includes('react-dom')) return 'react-dom-vendor';
            if (id.includes('react-router')) return 'react-router-vendor';
            if (id.includes('@tanstack/query-core')) return 'query-core-vendor';
            if (id.includes('axios')) return 'axios-vendor';
            if (id.includes('react-toastify')) return 'toastify-vendor';
            if (id.includes('zod')) return 'zod-vendor';
            if (id.includes('i18next')) return 'i18next-vendor';
            if (id.includes('dompurify')) return 'dompurify-vendor';
            if (id.includes('react-icons')) return 'icons-vendor';
            if (id.includes('react-hook-form')) return 'react-hook-form-vendor';
            if (id.includes('react')) return 'react-vendor';
            return 'vendor';
          }
        },
      },
    },
  },
});
