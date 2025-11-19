import { defineConfig, loadEnv } from 'vite';
import react from '@vitejs/plugin-react';
import tailwindcss from '@tailwindcss/vite';
import { visualizer } from 'rollup-plugin-visualizer';
import { sentryVitePlugin } from '@sentry/vite-plugin';

// https://vite.dev/config/
export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd(), '');
  const isProduction = mode === 'production';
  const enableSentry = env.VITE_SENTRY_ENABLE === 'true' && isProduction; 
  return {
    plugins: [
      react(),
      tailwindcss(),
      visualizer(),
      enableSentry &&
        sentryVitePlugin({
          org: 'cjy-37',
          project: 'javascript-react',
          authToken: process.env.SENTRY_AUTH_TOKEN, // process.env.SENTRY_AUTH_TOKEN은 빌드 시점에만 접근 가능, CI/CD 환경변수에서 설정
        }),
    ],
    server: {
      host: true, // 0.0.0.0 으로 바인딩
      port: 3000,
      strictPort: false,
      proxy: {
        '/api': {
          target: 'http://backend:8080',
          changeOrigin: true,
          secure: false,
        },
      },
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
      // 운영환경 + Sentry 활성화 시에만 sourcemap 생성
      sourcemap: enableSentry,
      chunkSizeWarningLimit: 600, // KB / 필요 시 조정
      target: 'es2022', // 최종 번들 자바스크립트 코드의 문법 수준을 ES2022(ECMAScript 2022)로 맞춰서 출력 → 불필요한 폴리필 줄여서 번들 작아짐
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
            }
          },
        },
      },
    },
  };
});
