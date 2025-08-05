import { useSearchParams } from "react-router-dom";
import { commonErrorHandler } from "../utils/commonUtil";
import { useTranslation } from "react-i18next";

/**
 * 전체 화면 에러 페이지 컴포넌트
 * 에러 상태 코드와 메시지를 표시하며, 홈으로 돌아가는 버튼을 포함
 * @returns 전체 화면 에러 페이지
 */
export const ErrorPageWithFullScreen = () => {

    const { t } = useTranslation();
    const [searchParams] = useSearchParams();

    const status = searchParams.get("status") ?? "500";
    const message = searchParams.get("message") ?? t("error.serverError");

    const handleReload = () => {
        window.location.href = "/";
    };

    return (
        <div className="min-h-screen flex flex-col justify-center items-center bg-white px-4">
            <div className="text-center">
                <h1 className="text-6xl font-bold text-red-600 mb-4">{status}</h1>
                <p className="text-xl text-gray-700 mb-6">{message}</p>
                <button
                    className="mt-4 px-6 py-2 rounded-xl bg-black text-white hover:bg-gray-800 transition cursor-pointer"
                    onClick={commonErrorHandler(handleReload)}
                >
                    {t("info.toHome")}
                </button>
            </div>
        </div>
    );
};