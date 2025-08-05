import { useNaverLogin } from "./useNaverLogin";

/**
 * 네이버 로그인 컴포넌트
 * 이 컴포넌트는 네이버 로그인 훅을 사용하여 로그인 로직을 실행하며, UI 요소는 포함하지 않음
 */
export const NaverLogin = () => {
    // 네이버 로그인 훅을 사용하여 로그인 로직 실행
    useNaverLogin();
    // 컴포넌트는 렌더링할 내용이 없으므로 빈 Fragment 반환
    return (
        <></>
    )

}