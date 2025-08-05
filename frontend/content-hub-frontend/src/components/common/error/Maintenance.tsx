import { settings } from "@components/common/config/settings";
import { Trans, useTranslation } from "react-i18next";

/**
 * 유지보수 페이지 컴포넌트
 * 유지보수 중임을 알리고, 새로고침 버튼을 포함
 * @returns 유지보수 페이지
 */
export const Maintenance = () => {

  const { t } = useTranslation();

  const handleReload = () => {
    window.location.href = "/";
  };

  return (
    <div className="flex flex-col items-center justify-center min-h-screen bg-gray-50 px-4 text-center">
      <h1 className="text-5xl font-bold text-gray-800 mb-6">{t("info.maintenanceTitle")}</h1>
      <p className="text-lg text-gray-600 mb-6">
        <Trans i18nKey="info.maintenanceMessage" />
      </p>
      <div className="flex flex-col items-center text-sm text-gray-500 mb-8">
        <div>{settings.maintenanceStart} ~ {settings.maintenanceEnd}</div>
      </div>
      <button
        onClick={handleReload}
        className="px-6 py-3 bg-blue-600 hover:bg-blue-700 text-white rounded-xl shadow-md transition cursor-pointer"
      >
        {t("info.refresh")}
      </button>
    </div>
  );
};