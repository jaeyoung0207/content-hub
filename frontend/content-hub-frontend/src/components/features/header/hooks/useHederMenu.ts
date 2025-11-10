import { ESC_KEY } from '@/components/common/constants/constants';
import { RefObject, useCallback, useEffect, useRef, useState } from 'react';

/**
 * 헤더 컴포넌트의 메뉴 아이콘 클릭 처리 훅 반환 타입
 */
export type UseHeaderMenuReturnType = {
  isMenuOpen: boolean;
  menuRef: RefObject<HTMLDivElement | null>;
  handleMenuIconOnClick: () => void;
};

/**
 * 헤더 컴포넌트의 메뉴 아이콘 클릭 처리 훅
 */
export const useHeaderMenu = (): UseHeaderMenuReturnType => {
  // ================================================================================================== react hook

  // 메뉴 박스 오픈 상태
  const [isMenuOpen, setIsMenuOpen] = useState<boolean>(false);

  // 메뉴 박스 참조
  const menuRef = useRef<HTMLDivElement>(null);

  // ================================================================================================== function
  /**
   * 메뉴 아이콘 클릭시 처리
   */
  const handleMenuIconOnClick = useCallback(() => {
    setIsMenuOpen((prev) => !prev);
  }, [setIsMenuOpen]);

  // ================================================================================================== useEffect

  /**
   * 마우스 클릭/키보드 키다운 이벤트
   */
  useEffect(() => {
    // 메뉴 바깥 영역 클릭 이벤트
    const handleOnClickOutside = (e: MouseEvent) => {
      // 메뉴 바깥영역 클릭시
      if (menuRef.current && !menuRef.current.contains(e.target as Node)) {
        setIsMenuOpen(false);
      }
    };
    // 메뉴 esc 키다운 이벤트
    const handleOnKeyDown = (e: globalThis.KeyboardEvent) => {
      if (e.key === ESC_KEY) {
        // 메뉴 닫기
        setIsMenuOpen(false);
      }
    };

    // 각 이벤트 리스너 추가
    document.addEventListener('mousedown', handleOnClickOutside);
    document.addEventListener('keydown', handleOnKeyDown);

    return () => {
      // 각 이벤트 리스너 제거
      document.removeEventListener('mousedown', handleOnClickOutside);
      document.removeEventListener('keydown', handleOnKeyDown);
    };
  }, []);

  // ================================================================================================== return

  return {
    isMenuOpen: isMenuOpen,
    menuRef: menuRef,
    handleMenuIconOnClick: handleMenuIconOnClick,
  };
};
