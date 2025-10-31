import { settings } from '@components/common/config/settings';
import { Trans, useTranslation } from 'react-i18next';

/**
 * 유지보수 페이지 컴포넌트
 * 유지보수 중임을 알리고, 새로고침 버튼을 포함
 * @returns 유지보수 페이지
 */
export const Maintenance = () => {
  const { t } = useTranslation();

  const handleReload = () => {
    window.location.href = '/';
  };

  return (
    <div className="flex min-h-screen flex-col items-center justify-center bg-gray-50 px-4 text-center">
      <h1 className="mb-6 text-5xl font-bold text-gray-800">
        {t('info.maintenanceTitle')}
      </h1>
      <p className="mb-6 text-lg text-gray-600">
        <Trans i18nKey="info.maintenanceMessage" />
      </p>
      <div className="mb-8 flex flex-col items-center text-sm text-gray-500">
        <div>
          {settings.maintenanceStart} ~ {settings.maintenanceEnd}
        </div>
      </div>
      <button
        onClick={handleReload}
        className="cursor-pointer rounded-xl bg-blue-600 px-6 py-3 text-white shadow-md transition hover:bg-blue-700"
      >
        {t('info.refresh')}
      </button>
    </div>
  );
};

export default Maintenance;
