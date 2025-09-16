import { useQueryClient } from '@tanstack/react-query';
import { useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import { HeaderType } from './useHeaderForm';

/**
 * 헤더 컴포넌트의 홈 버튼 클릭 처리 훅 매개변수 타입
 */
type HeaderHomePropsType = {
  reset: (values?: HeaderType) => void;
  setFocus: (name: keyof HeaderType) => void;
  clearAdultFlg: () => void;
  setAutoCompleteList: (list?: string[]) => void;
  setCurrentIndex: (index: number) => void;
};

/**
 * 헤더 컴포넌트의 홈 버튼 클릭 처리 훅 반환 타입
 */
export type UseHeaderHomeReturnType = {
  handleHomeOnClick: () => void;
};

/**
 * 헤더 컴포넌트의 홈 버튼 클릭 처리 훅
 */
export const useHeaderHome = ({
  reset,
  setFocus,
  clearAdultFlg,
  setAutoCompleteList,
  setCurrentIndex,
}: HeaderHomePropsType): UseHeaderHomeReturnType => {
  // ================================================================================================== react hook

  // navigate 훅
  const navigate = useNavigate();

  // ================================================================================================== react query

  // react query 클라이언트 훅
  const queryClient = useQueryClient();

  // ================================================================================================== function

  /**
   * 초기화 처리
   */
  const resetAll = useCallback(() => {
    // 성인물 검색 플래그 초기화
    clearAdultFlg();
    // 각 필드값 초기화
    reset();
    // 자동완성 리스트 초기화
    setAutoCompleteList(undefined);
    // 자동완성 박스 포커스 인덱스 초기화
    setCurrentIndex(-1);
    // 캐시에서 모든 쿼리 제거
    queryClient.removeQueries();
    // // 처음 로드 참조를 true로 설정
    // firstLoadRef.current = true;
  }, [clearAdultFlg, reset, setAutoCompleteList, setCurrentIndex, queryClient]);

  /**
   * 홈 버튼 클릭시 처리
   */
  const handleHomeOnClick = useCallback(() => {
    // 초기화 처리
    resetAll();
    // 홈으로 이동
    navigate('/');
    // 포커스 설정
    setFocus('keyword');
  }, [resetAll, navigate, setFocus]);

  // ================================================================================================== return

  return {
    handleHomeOnClick: handleHomeOnClick,
  };
};
