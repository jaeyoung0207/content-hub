import { AppBrowserRouter } from './components/common/router/AppRouter';
import { ToastContainer } from 'react-toastify';
import 'react-toastify/ReactToastify.css';
import { CookiesProvider } from 'react-cookie';

/**
 * 메인 애플리케이션 컴포넌트
 */
function App() {
  return (
    <>
      <div className="sm:w-sm md:w-md lg:w-7xl m-auto">
        {/* 애플리케이션 전역에서 알림을 표시하는 ToastContainer는 설정 */}
        <ToastContainer
          position="bottom-center" // 알림 위치 설정
        />
        {/* 쿠키 프로바이더는 애플리케이션 전체에서 쿠키를 관리 */}
        <CookiesProvider>
          {/* AppBrowserRouter 컴포넌트는 애플리케이션의 라우팅을 관리하고, URL 경로에 따라 적절한 컴포넌트를 렌더링 함 */}
          <AppBrowserRouter />
        </CookiesProvider>
      </div>
    </>
  );
}

export default App;
