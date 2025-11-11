import { AppBrowserRouter } from './components/common/router/AppRouter';
import { toast, ToastContainer } from 'react-toastify';
import 'react-toastify/ReactToastify.css';
import { CookiesProvider } from 'react-cookie';
import { ConfirmModalUi } from './components/ui/ConfirmModalUi';
import { useConfirmDialogStore } from './components/common/store/globalStateStore';
import { PageContainer } from './components/layout/PageContainer';
import { useEffect } from 'react';

/**
 * 메인 애플리케이션 컴포넌트
 */
function App() {
  // confirm dialog 상태 훅
  const { isConfirmDialogOpen, onOk, onCancel, title, confirmMsg } =
    useConfirmDialogStore();

  /**
   * 전역 콘솔 및 토스트 메세지 처리
   */
  useEffect(() => {
    // 전역 콘솔 메세지가 있는 경우 출력
    const consoleMsg = sessionStorage.getItem('consoleMessage');
    if (consoleMsg) {
      console.error(consoleMsg);
    }
    // 전역 토스트 메세지가 있는 경우 출력
    const toastMsg = sessionStorage.getItem('toastMessage');
    if (toastMsg) {
      toast.error(<div className="whitespace-pre-line">{toastMsg}</div>, {
        toastId: 'globalMessage',
      });
    }
    // 세션 스토리지에서 메세지 제거
    sessionStorage.removeItem('consoleMessage');
    sessionStorage.removeItem('toastMessage');
  }, []);

  return (
    <>
      {/* 애플리케이션 전역에서 알림을 표시하는 ToastContainer 설정 */}
      <ToastContainer
        position="bottom-center" // 알림 위치 설정
      />
      {/* 쿠키 프로바이더는 애플리케이션 전체에서 쿠키를 관리 */}
      <CookiesProvider>
        {/* 페이지 컨테이너로 전체 페이지 레이아웃을 감싸고, 공통 스타일을 적용 */}
        <PageContainer>
          {/* AppBrowserRouter 컴포넌트는 애플리케이션의 라우팅을 관리하고, URL 경로에 따라 적절한 컴포넌트를 렌더링 함 */}
          <AppBrowserRouter />
        </PageContainer>
      </CookiesProvider>
      {/* 확인 다이얼로그 */}
      <div className="pointer-events-none fixed inset-0 z-50 grid place-items-start pt-40">
        <div className="pointer-events-auto">
          <ConfirmModalUi
            isOpen={isConfirmDialogOpen}
            onOk={onOk!}
            onCancel={onCancel!}
            title={title}
            confirmMsg={confirmMsg!}
          />
        </div>
      </div>
    </>
  );
}

export default App;
