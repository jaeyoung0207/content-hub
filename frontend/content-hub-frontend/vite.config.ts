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
    base: '/', // 배포 기본 경로
    plugins: [
      react(),
      tailwindcss(),
      visualizer(),
      enableSentry &&
        sentryVitePlugin({
          org: 'cjy-37',
          project: 'javascript-react',
          authToken: process.env.SENTRY_AUTH_TOKEN, // process.env.SENTRY_AUTH_TOKEN은 빌드 시점에만 접근 가능, CI/CD 환경변수에서 설정
          sourcemaps: {
            assets: ['./dist/assets/**'], // sourcemap 업로드할 파일 경로
            filesToDeleteAfterUpload: ['./dist/assets/**/*.map'], // 업로드 후 삭제할 sourcemap 파일 경로
          },
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
    },
    test: {
      globals: true, // describe, it, test, expect 같은 Vitest의 핵심 API를 전역 변수로 사용할 수 있게 해줌
      environment: 'jsdom', // 테스트를 실행할 환경으로 jsdom을 사용하도록 지정
      setupFiles: './src/test/setup.ts', // 테스트 실행 전에 설정 파일을 불러옴
      css: true, // CSS 관련 테스트 지원 활성화
    },
  };
});
