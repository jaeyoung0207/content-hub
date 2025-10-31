import { useSearchParams } from 'react-router-dom';
import { commonErrorHandler } from '../utils/errorUtil';
import { useTranslation } from 'react-i18next';

/**
 * 전체 화면 에러 페이지 컴포넌트
 */
export const ErrorPageWithFullScreen = () => {
  // i18n 번역 훅
  const { t } = useTranslation();
  // URL 쿼리스트링 제어
  const [searchParams] = useSearchParams();

  // 상태 코드와 메시지 쿼리 파라미터에서 추출, 없으면 기본값 설정
  const status = searchParams.get('status') ?? '500';
  const message = searchParams.get('message') ?? t('error.serverError');

  // 콘솔에 에러 메시지 출력
  console.error(`Error ${status}: ${message}`);

  /**
   * 페이지를 새로고침하여 홈으로 이동
   */
  const handleReload = () => {
    window.location.href = '/';
  };

  return (
    <div className="flex min-h-screen flex-col items-center justify-center bg-white px-4">
      <div className="text-center">
        {/* 에러 상태 코드 */}
        <h1 className="mb-4 text-6xl font-bold text-red-600">{status}</h1>
        {/* 에러 메시지 */}
        <p className="mb-6 text-xl text-gray-700">{message}</p>
        {/* 홈으로 돌아가기 버튼 */}
        <button
          className="mt-4 cursor-pointer rounded-xl bg-black px-6 py-2 text-white transition hover:bg-gray-800"
          onClick={commonErrorHandler(handleReload)}
        >
          {t('info.toHome')}
        </button>
      </div>
    </div>
  );
};
