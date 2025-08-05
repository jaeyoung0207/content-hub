import { useHome } from "./useHome";
import { ConfirmModalUi } from "@/components/ui/ConfirmModalUi";
import { useTranslation } from "react-i18next";

/**
 * 홈 화면 컴포넌트
 */
export const Home = () => {
    // i18n 번역 훅
    const { t } = useTranslation();

    // 홈 화면 훅 호출
    const {
        isConfirmDialogOpen,
        handleConfirmOk,
        handleConfirmCancle,
    } = useHome();

    return (
        <>
            <div className="w-sm lg:w-7xl">
                <div className="mt-20 flex justify-center items-center">
                    {
                        isConfirmDialogOpen &&
                        <div className="mt-40">
                            <ConfirmModalUi isOpen={isConfirmDialogOpen} onOk={handleConfirmOk} onCancel={handleConfirmCancle} confirmMsg={t("info.loginConfirmMsg1")} />
                        </div>
                    }
                </div>
                <div className="mt-20 text-center text-2xl font-bold">
                    대문 화면입니다.
                </div>
            </div>
        </>
    )
}