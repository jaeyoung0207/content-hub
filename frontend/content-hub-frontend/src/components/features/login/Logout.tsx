import { Login } from "@/api/Login";
import { settings } from "@/components/common/config/settings";
import { LOGIN_PROVIDER } from "@/components/common/constants/constants";
import { useProviderStore } from "@/components/common/store/globalStateStore";
import { afterLoginRedirect, clearUserData } from "@/components/common/utils/commonUtil";
import { useEffect } from "react"
import { useNavigate } from "react-router-dom";

/**
 * 로그아웃 컴포넌트
 */
export const Logout = () => {
    
    // 네비게이션 훅
    const navigate = useNavigate();
    // provider 전역 상태 저장 훅
    const { provider } = useProviderStore();
    // 로그인 API 인스턴스 생성
    const loginApi = new Login();

    /**
     * 로그아웃 처리
     * 각 로그인 제공자를 통해 로그인한 경우, 해당 API를 호출하여 로그아웃 처리
     * 이후 유저 정보를 클리어하고 홈 화면으로 리다이렉트
     * 실행 조건: 컴포넌트가 마운트될 때
     */
    useEffect(() => {
        const logout = async () => {
            try {
                // 접근토큰 취득
                const accessToken = sessionStorage.getItem("accessToken");
                // 접근토큰이나 provider가 존재하지 않는 경우는 처리 종료
                if (!accessToken || !provider) {
                    return;
                }
                // 네이버로 로그인한 경우
                if (provider === LOGIN_PROVIDER.NAVER) {
                    // 로그아웃 처리
                    await loginApi.deleteNaverToken({ access_token: accessToken });
                }
                // 카카오로 로그인한 경우
                else if (provider === LOGIN_PROVIDER.KAKAO) {
                    // 로그아웃 처리
                    await loginApi.deleteKakaoToken({ access_token: accessToken, target_id: settings.kakaoClientId });
                }
            } finally {
                // 유저정보 클리어
                clearUserData();
                // 리다이렉트 처리
                afterLoginRedirect(navigate);
            }
        }
        logout();
    }, []);


    return (
        <></>
    )
}