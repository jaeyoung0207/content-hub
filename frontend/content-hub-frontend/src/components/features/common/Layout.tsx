import { Outlet } from 'react-router-dom';
import { Header } from '@components/features/header/Header';

/**
 * 레이아웃 컴포넌트
 * 헤더를 포함하고, Outlet을 통해 하위 라우트를 렌더링
 * 이 컴포넌트는 전체 페이지 레이아웃을 구성하며, 헤더와 메인 콘텐츠 영역을 포함
 * 헤더는 페이지 상단에 고정되어 있으며, Outlet은 현재 라우트에 해당하는 컴포넌트를 렌더링
 * 이 레이아웃은 모든 페이지에서 공통적으로 사용되며, 페이지 간 일관된 디자인을 유지하도록 해 줌
 */
export const Layout = () => {
  return (
    <>
      <Header />
      <main>
        <Outlet />
      </main>
    </>
  );
};
