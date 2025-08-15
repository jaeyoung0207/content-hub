import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import tailwindcss from '@tailwindcss/vite';

// https://vite.dev/config/
export default defineConfig({
  plugins: [react(), tailwindcss()],
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
});
