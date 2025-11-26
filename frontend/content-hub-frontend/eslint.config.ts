import js from '@eslint/js';
import globals from 'globals';
import tseslint from 'typescript-eslint';
import pluginReact from 'eslint-plugin-react';
import hooksPlugin from 'eslint-plugin-react-hooks';

/**
 * ESLint 설정
 */
export default [
  {
    // ESLint가 무시할 파일/디렉토리 설정
    ignores: [
      '**/dist/**',
      '**/node_modules/**',
      '**/coverage/**',
      '**/src/api/**/*', // swagger-typescript-api 자동 생성 파일
    ],
  },
  {
    // 모든 JS/TS 파일에 적용
    files: ['**/*.{js,mjs,cjs,ts,mts,cts,jsx,tsx}'],
    plugins: {
      react: pluginReact,
      'react-hooks': hooksPlugin,
    },
    // 환경 설정
    languageOptions: {
      // 브라우저 환경 전역 변수 활성화
      globals: globals.browser,
    },
  },
  // JavaScript ESLint 권장 설정 병합
  js.configs.recommended,
  // TypeScript ESLint 권장 설정 병합
  ...tseslint.configs.recommended,
  // React ESLint 권장 설정 병합
  pluginReact.configs.flat.recommended,
  {
    // 특정 룰 오버라이드 설정
    rules: {
      ...hooksPlugin.configs.recommended.rules, // react-hooks 플러그인 권장 룰 병합
      'react/react-in-jsx-scope': 'off', // React 17+에서는 import React 불필요
      'react/prop-types': 'off', // TypeScript 사용 시 PropTypes 불필요
      'react/display-name': 'off', // 익명 함수형 컴포넌트에 displayName 경고 비활성화
      '@typescript-eslint/no-unused-vars': [
        'warn',
        {
          argsIgnorePattern: '^_', // 사용하지 않는 매개변수 이름이 _로 시작하면 경고 무시
          varsIgnorePattern: '^_', // 사용하지 않는 변수 이름이 _로 시작하면 경고 무시
        },
      ],
    },
    // React 버전 자동 감지 설정
    settings: {
      react: {
        version: 'detect',
      },
    },
  },
];
