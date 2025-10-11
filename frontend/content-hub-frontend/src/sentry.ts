import * as Sentry from '@sentry/react';
import { settings } from './components/common/config/settings';

/**
 * Sentry 초기화 함수
 * 앱 시작 시 한 번만 호출
 */
export const sentryInit = () => {
  // 앱 전체에서 처리되지 않은 모든 Promise 에러를 전역적으로 감지하여 Sentry로 전송
  window.addEventListener('unhandledrejection', (event) => {
    Sentry.captureException(event.reason);
  });
  // Sentry 초기화
  Sentry.init({
    // Sentry에서 제공하는 DSN
    dsn: settings.sentryDsn,
    integrations: [
      // Sentry가 오류 및 성능 데이터를 추적하는 데 도움이 되는 통합 기능
      Sentry.browserTracingIntegration(),
      Sentry.replayIntegration({
        maskAllText: false,
        blockAllMedia: false,
      }),
    ],
    // 개발 환경에서는 Sentry가 이벤트를 전송하지 않도록 설정
    environment: import.meta.env.MODE,
    // 성능 추적을 위한 샘플링 비율을 설정
    tracesSampleRate: 1.0,
    // Replay 녹화 비율을 설정
    replaysSessionSampleRate: 0.1,
    replaysOnErrorSampleRate: 1.0,
  });
};

export default Sentry;
