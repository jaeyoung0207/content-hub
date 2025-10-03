import { MyCommentsDataResponseDto } from '@/api/data-contracts';
import { useUserStore } from '@/components/common/store/globalStateStore';
import { useQuery } from '@tanstack/react-query';
import { myCommentsQueryKeys } from './queryKeys/myCommentsQueryKeys';
import { RefObject, useCallback, useEffect, useRef, useState } from 'react';
import { My } from '@/api/My';
import { useNavigate } from 'react-router-dom';
import { Control, useForm } from 'react-hook-form';

/**
 * 나의 코멘트 폼 타입
 */
type MyCommentsFormType = Partial<{
  starRating: number;
}>;

/**
 * 나의 코멘트 훅 반환 타입
 */
type UseMyCommentsReturnType = {
  data: MyCommentsDataResponseDto[] | undefined;
  isLoading: boolean;
  currentPage: number;
  totalPages: number;
  totalElements: number;
  handlePageOnClick: (page: number) => void;
  isOmitComment: boolean[];
  handleOnClickOmitComment: (index: number) => void;
  perPageCountRef: RefObject<number>;
  control: Control<MyCommentsFormType>;
};

/**
 * 나의 코멘트 훅
 */
export const useMyComments = (): UseMyCommentsReturnType => {
  // ================================================================================================== react hook

  // 리액트 라우터 네비게이트 훅
  const navigate = useNavigate();

  // 현재 페이지 번호 (0부터 시작)
  const [currentPage, setCurrentPage] = useState<number>(0);
  // 전체 페이지 수
  const [totalPages, setTotalPages] = useState<number>(0);
  // 전체 코멘트 수
  const [totalElements, setTotalElements] = useState<number>(0);
  // 코멘트 생략 처리용
  const [isOmitComment, setIsOmitComment] = useState<boolean[]>([]);

  // 페이지 당 코멘트 수
  const perPageCountRef = useRef<number>(0);

  // ================================================================================================== zustand

  // 유저 정보 전역 상태 저장 훅
  const { user } = useUserStore();

  // =================================================================================================== react hook form

  // 폼 기본값
  const defaultValues = {
    starRating: 0,
  };

  // react-hook-form 훅
  const { control } = useForm<MyCommentsFormType>({
    defaultValues,
  });

  // ================================================================================================== react query

  // 나의 코멘트 API 인스턴스
  const myApi = new My();

  // 나의 코멘트 목록 조회
  const { data, isLoading } = useQuery({
    queryKey: myCommentsQueryKeys.page(user!.userId!, currentPage),
    queryFn: async () => {
      const response = await myApi.getMyCommentList({
        user_id: user!.userId!,
        page_no: currentPage,
      });
      setTotalPages(response.data.totalPages!);
      setTotalElements(response.data.totalElements!);
      return response.data.myCommentList;
    },
    maxPages: totalPages,
    enabled: !!user?.userId,
    staleTime: 0, // 데이터가 바로 만료되도록 설정
    gcTime: 0, // 가비지 컬렉션 시간 설정
  });

  /**
   * 페이지 클릭 핸들러
   */
  const handlePageOnClick = useCallback(
    (page: number) => {
      setCurrentPage(page);
    },
    [setCurrentPage]
  );

  // =================================================================================================== function

  /**
   * 코멘트 생략 처리 함수
   * @param index 코멘트 인덱스
   */
  const handleOnClickOmitComment = (index: number) => {
    setIsOmitComment((prevState) => {
      const newState = [...prevState];
      newState[index] = !newState[index];
      return newState;
    });
  };

  // =================================================================================================== useEffect

  /**
   * 페이지 당 코멘트 수 설정
   */
  useEffect(() => {
    if (data && data.length > 0 && !perPageCountRef.current) {
      perPageCountRef.current = data.length;
    }
  }, [data, perPageCountRef]);

  /**
   * user정보가 없으면 홈으로 이동
   */
  useEffect(() => {
    if (!user) {
      navigate('/');
      return;
    }
  }, [user, navigate]);

  // ================================================================================================== return

  return {
    data: data,
    isLoading: isLoading,
    currentPage: currentPage,
    totalPages: totalPages,
    totalElements: totalElements,
    handlePageOnClick: handlePageOnClick,
    isOmitComment: isOmitComment,
    handleOnClickOmitComment: handleOnClickOmitComment,
    perPageCountRef: perPageCountRef,
    control: control,
  };
};
