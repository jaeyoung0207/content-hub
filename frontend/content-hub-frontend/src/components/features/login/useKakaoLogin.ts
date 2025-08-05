import { Login } from "@/api/Login";
import { settings } from "@/components/common/config/settings";
import { LOGIN_PROVIDER } from "@/components/common/constants/constants";
import { useProviderStore, useUserStore } from "@/components/common/store/globalStateStore";
import { afterLoginRedirect } from "@/components/common/utils/commonUtil";
import { useEffect } from "react"
import { useNavigate, useSearchParams } from "react-router-dom";
import { toast } from "react-toastify";

/**
 * Kakao 로그인 훅
 * 카카오 로그인을 처리하며, 로그인 성공 시 유저 정보를 전역 상태에 저장하고, 세션 스토리지에 토큰을 저장
 * 로그인 실패 시 에러 메시지를 표시하고 로그인 페이지로 리다이렉트
 * 로그인 후 유저 정보를 가져오는 함수도 포함되어 있어, 액세스 토큰과 만료 시간을 받아 유저 정보를 갱신
 */
export const useKakaoLogin = () => {

    // 네비게이션 훅
    const navigate = useNavigate();
    // URL 쿼리 파라미터 훅
    const [searchParams] = useSearchParams();
    // 유저 정보 전역 상태 저장 훅
    const { setUser } = useUserStore();
    // provider 전역 상태 저장 훅
    const { setProvider } = useProviderStore();
    // 로그인 API 인스턴스 생성
    const loginApi = new Login();

    /**
     * 카카오 로그인 인증 및 유저 정보 조회 API 요청
     * @param code 인증 코드
     * @returns 로그인 유저 정보
     */
    const getKakaoLoginInfo = async (code: string) => {
        // 카카오 로그인 API 호출
        const response = await loginApi.getKakaoLoginInfo({
            client_id: settings.kakaoClientId,
            redirect_uri: settings.kakaoRedirectUri,
            code: code
        });
        const loginInfo = response.data;
        if (loginInfo && loginInfo.userInfo) {
            // 유저정보를 전역상태저장
            setUser(loginInfo.userInfo!);
            // provider 전역상태저장
            setProvider(LOGIN_PROVIDER.KAKAO);
            // 액세스 토큰을 sessionStorage에 저장
            sessionStorage.setItem("accessToken", loginInfo.accessToken!);
            // JWT를 sessionStorage에 저장
            sessionStorage.setItem("jwt", loginInfo.jwt!);
            // 만료시각을 sessionStorage에 저장
            sessionStorage.setItem("expireDate", loginInfo.expireDate!);
        }
        return loginInfo;
    }

    // const getKakaoUserInfo = async (accessToken: string, expiresIn: number) => {
    //     const response = await loginApi.getKakaoUserInfo({
    //         accessToken: accessToken,
    //         expiresIn: expiresIn
    //     });
    //     const loginInfo = response.data;
    //     if (loginInfo && loginInfo.response) {
    //         // 유저정보를 전역상태저장
    //         setUser(loginInfo.response!);
    //         // provider 전역상태저장
    //         setProvider(LOGIN_PROVIDER.KAKAO);
    //         // 액세스 토큰을 sessionStorage에 저장
    //         sessionStorage.setItem("accessToken", loginInfo.accessToken!);
    //         // JWT를 sessionStorage에 저장
    //         sessionStorage.setItem("jwt", loginInfo.jwt!);
    //         // 만료시각을 sessionStorage에 저장
    //         sessionStorage.setItem("expireDate", loginInfo.expireDate!);
    //     }
    //     return loginInfo;
    // }

    useEffect(() => {
        // URL에서 인증 코드를 가져옴
        const code = searchParams.get("code")!;
        // 카카오 로그인 인증 및 유저 정보 조회 API 호출
        getKakaoLoginInfo(code)
            .catch((err) => {
                console.error("카카오 로그인 실패", err);
                toast.error("로그인에 실패했습니다. 다시 시도해주세요.", { toastId: "kakaoLoginError" });
                navigate("/login");
            });
        // 리다이렉트 URL이 있다면 해당 URL로 이동
        afterLoginRedirect(navigate);
    }, []);
}