import { useTranslation } from 'react-i18next';
import { useSearchParams } from 'react-router-dom';

/**
 * 에러 페이지 컴포넌트(헤더 제외)
 */
export const ErrorPageWithHalfScreen = () => {
  // i18n 번역 훅
  const { t } = useTranslation();
  // URL 쿼리스트링 제어
  const [searchParams] = useSearchParams();

  // 상태 코드와 메시지 쿼리 파라미터에서 추출, 없으면 기본값 설정
  const status = searchParams.get('status') ?? '404';
  const message = searchParams.get('message') ?? t('error.notFound');

  // 콘솔에 에러 메시지 출력
  console.error(`Error ${status}: ${message}`);

  return (
    <div className="min-h-screen flex flex-col justify-center items-center bg-white px-4">
      <div className="text-center">
        {/* 에러 상태 코드 */}
        <h1 className="text-6xl font-bold text-red-600 mb-4">{status}</h1>
        {/* 에러 메시지 */}
        <p className="text-xl text-gray-700 mb-6">{message}</p>
      </div>
    </div>
  );
};

export default ErrorPageWithHalfScreen;
