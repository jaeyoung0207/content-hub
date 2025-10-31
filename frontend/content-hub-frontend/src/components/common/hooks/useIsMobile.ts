import { useEffect, useState } from 'react';
import { MOBILE_WIDTH } from '../constants/constants';

/**
 * 모바일 사이즈 여부를 반환하는 커스텀 훅
 * @returns {boolean} 모바일 사이즈 여부
 */
export const useIsMobile = () => {
  // 모바일 사이즈 상태 관리
  const [isMobile, setIsMobile] = useState<boolean>(false);

  /**
   * 윈도우 리사이즈 이벤트 핸들러 등록
   * 윈도우 크기가 변경될 때마다 모바일 사이즈 여부를 업데이트
   */
  useEffect(() => {
    // 리사이즈 이벤트 핸들러
    const handleResize = () => {
      setIsMobile(window.innerWidth < MOBILE_WIDTH);
    };
    // 등록 및 초기 실행
    window.addEventListener('resize', handleResize);
    handleResize();
    // 클린업 함수에서 이벤트 리스너 제거
    return () => {
      window.removeEventListener('resize', handleResize);
    };
  }, []);

  return isMobile;
};
