import { BrowserRouter, Route, Routes } from "react-router-dom"
import { Login } from "../../features/login/Login"
import { NaverLogin } from "@/components/features/login/NaverLogin"
import { Logout } from "@/components/features/login/Logout"
import { Home } from "@/components/features/home/Home"
import { SearchContentPage } from "@/components/features/searchContent/SearchContentPage"
import { Layout } from "@/components/features/common/Layout"
import { ErrorPageWithHalfScreen } from "@/components/common/error/ErrorPageWithHalfScreen"
import { ErrorPageWithFullScreen } from "@/components/common/error/ErrorPageWithFullScreen"
import { ErrorBoundary } from "react-error-boundary"
import { QueryClientProvider } from "@tanstack/react-query"
import { queryClientConfig } from "../config/queryClientConfig"
import { Maintenance } from "../error/Maintenance"
import { settings } from "../config/settings"
import { KakaoLogin } from "@/components/features/login/KakaoLogin"

/**
 * AppBrowserRouter 컴포넌트 
 * 애플리케이션의 라우팅을 관리하는 컴포넌트
 * react-router-dom의 BrowserRouter를 사용하여 라우팅을 설정
 */
export const AppBrowserRouter = () => {
    return (
        <BrowserRouter>
            <AppRouter />
        </BrowserRouter>
    )
}

/**
 * 각 URL에 대한 컴포넌트를 매핑하는 AppRouter 컴포넌트
 * 유지보수 모드일 경우 유지보수 페이지로 리다이렉트
 * QueryClientProvider를 사용하여 React Query의 QueryClient를 제공
 * 라우트 설정은 중첩 라우트를 사용하여 레이아웃을 적용
 * 각 URL 경로에 해당하는 컴포넌트를 지정
 * 전체화면 에러 페이지와 헤더 제외 에러 페이지를 포함
 */
const AppRouter = () => {

    // 점검중일 경우 점검페이지 표시
    if (settings.isMaintenanceMode) {
        if (!window.location.pathname.startsWith("/maintenance")) {
            window.location.href = "/maintenance";
        }
        return <Maintenance />;
    }
    return (
        <>
            <ErrorBoundary FallbackComponent={({ error }) => {
                // 전체화면 에러페이지로 이동
                return <ErrorPageWithFullScreen />;
            }}>
                <QueryClientProvider client={queryClientConfig}>
                    <Routes>
                        <Route element={<Layout />} >
                            <Route path='/' element={<Home />} />
                            <Route path='/search' element={<SearchContentPage />} />
                            <Route path='/login' element={<Login />} />
                            <Route path='/login/naver' element={<NaverLogin />} />
                            <Route path='/login/kakao' element={<KakaoLogin />} />
                            <Route path='/logout' element={<Logout />} />
                            <Route path='/error' element={<ErrorPageWithHalfScreen />} />
                            <Route path='*' element={<ErrorPageWithHalfScreen />} />
                        </Route>
                        <Route path="/maintenance" element={<Maintenance />} />
                    </Routes>
                </QueryClientProvider>
            </ErrorBoundary>
        </>
    )
}