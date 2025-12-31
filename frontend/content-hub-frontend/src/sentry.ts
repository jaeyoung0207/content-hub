import { settings } from './components/common/config/settings';

/**
 * Sentry 초기화 함수
 * 앱 시작 시 한 번만 호출
 */
export const sentryInit = async () => {
  // 운영환경이면서 Sentry가 활성화된 경우에만 Sentry 초기화
  if (import.meta.env.PROD && settings.isSentryEnabled && settings.sentryDsn) {
    // production에서만 Sentry를 동적으로 불러와 초기화
    import('@sentry/react').then((Sentry) => {
      // 앱 전체에서 처리되지 않은 모든 Promise 에러를 전역적으로 감지하여 Sentry로 전송
      globalThis.addEventListener('unhandledrejection', (event) => {
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
        tracesSampleRate: 1,
        replaysSessionSampleRate: 0.1,
        replaysOnErrorSampleRate: 0.1,
      });
    });
  }
};
