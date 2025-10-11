import { defineConfig, loadEnv } from 'vite';
import react from '@vitejs/plugin-react';
import tailwindcss from '@tailwindcss/vite';
import { visualizer } from 'rollup-plugin-visualizer';
import { sentryVitePlugin } from '@sentry/vite-plugin';

// https://vite.dev/config/
export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd(), '');
  return {
    plugins: [
      react(),
      tailwindcss(),
      visualizer(),
      sentryVitePlugin({
        org: 'cjy-37',
        project: 'javascript-react',
        authToken: env.VITE_SENTRY_AUTH_TOKEN,
      }),
    ],
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
              if (id.includes('@tanstack/query-core'))
                return 'query-core-vendor';
              if (id.includes('axios')) return 'axios-vendor';
              if (id.includes('react-toastify')) return 'toastify-vendor';
              if (id.includes('zod')) return 'zod-vendor';
              if (id.includes('i18next')) return 'i18next-vendor';
              if (id.includes('dompurify')) return 'dompurify-vendor';
              if (id.includes('react-icons')) return 'icons-vendor';
              if (id.includes('react-hook-form'))
                return 'react-hook-form-vendor';
              if (id.includes('react')) return 'react-vendor';
              if (id.includes('@sentry-internal'))
                return 'sentry-internal-vendor';
              if (id.includes('@sentry')) return 'sentry-vendor';
              return 'vendor';
            }
          },
        },
      },
      sourcemap: true,
    },
  };
});
