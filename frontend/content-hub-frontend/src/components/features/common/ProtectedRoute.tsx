import { REDIRECT_URL } from "@/components/common/constants/constants";
import { useUserStore } from "@/components/common/store/globalStateStore";
import { ReactNode } from "react";
import { Navigate, useLocation } from "react-router-dom";

/**
 * ProtectedRouteProps 타입 정의
 */
type ProtectedRoutePropsType = { 
    children: ReactNode 
};

/**
 * ProtectedRoute 컴포넌트
 * 인증된 사용자만 접근할 수 있는 라우트를 보호하는 역할
 * 사용자가 인증되지 않은 경우 리다이렉트
 */
export const ProtectedRoute = ({ children }: ProtectedRoutePropsType) => {
  const { user } = useUserStore();
  const location = useLocation();

  if (!user) {
    // 현재 URL 저장 후 로그인 페이지로 이동
    sessionStorage.setItem(REDIRECT_URL, location.pathname + location.search);
    return <Navigate to="/login" replace />;
  }

  return <>{children}</>;
};