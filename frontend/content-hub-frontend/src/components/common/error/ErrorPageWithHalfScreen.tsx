import { useTranslation } from "react-i18next";
import { useSearchParams } from "react-router-dom";

/**
 * 에러 페이지 컴포넌트(헤더 제외)
 * @returns 에러 페이지
 */
export const ErrorPageWithHalfScreen = () => {

    const { t } = useTranslation();
    const [searchParams] = useSearchParams();

    const status = searchParams.get("status") ?? "404";
    const message = searchParams.get("message") ?? t("error.notFound");

    return (
        <div className="min-h-screen flex flex-col justify-center items-center bg-white px-4">
            <div className="text-center">
                <h1 className="text-6xl font-bold text-red-600 mb-4">{status}</h1>
                <p className="text-xl text-gray-700 mb-6">{message}</p>
            </div>
        </div>
    );
};