import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { Wishlist } from '@/api/Wishlist';
import {
  WishlistListResponseDto,
  WishlistRequestDto,
} from '@/api/data-contracts';
import { wishlistQueryKeys } from './queryKeys/wishlistQueryKeys';
import { toast } from 'react-toastify';
import { RefObject, useEffect, useRef, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useUserStore } from '@/components/common/store/globalStateStore';
import { ESC_KEY } from '@/components/common/constants/constants';
import { useTranslation } from 'react-i18next';

/**
 * useWishlist 훅 반환 타입
 */
type UseWishlistReturnType = {
  data: WishlistListResponseDto | undefined;
  isLoading: boolean;
  handleWishlistDeleteOnClick: (
    apiId: number,
    originalMediaType: string,
    title: string
  ) => void;
  isExecuting: boolean;
  wishlistOptionIsOpen: boolean;
  handleWishlistOptionOnClick: (
    originalMediaType: string,
    index: number
  ) => void;
  aniWishlistOptionRef: RefObject<HTMLDivElement[] | null[]>;
  dramaWishlistOptionRef: RefObject<HTMLDivElement[] | null[]>;
  movieWishlistOptionRef: RefObject<HTMLDivElement[] | null[]>;
  comicsWishlistOptionRef: RefObject<HTMLDivElement[] | null[]>;
  wishlistOriginalMediaType: string;
  wishlistOptionIndex: number;
};

/**
 * 위시리스트 조회 훅
 * @param userId 유저 id
 */
export const useWishlist = (userId: number): UseWishlistReturnType => {
  // ================================================================================================== react hook

  // navigate 훅
  const navigate = useNavigate();

  // i18n 훅
  const { t } = useTranslation();

  // 실행 중 상태
  const [isExecuting, setIsExecuting] = useState(false);
  // 옵션 열림 상태
  const [wishlistOptionIsOpen, setWishlistOptionIsOpen] =
    useState<boolean>(false);
  // 타겟 originalMediaType
  const [wishlistOriginalMediaType, setWishlistOriginalMediaType] =
    useState<string>('');
  // 옵션 인덱스
  const [wishlistOptionIndex, setWishlistOptionIndex] = useState<number>(-1);

  // 옵션 참조
  const aniWishlistOptionRef = useRef<HTMLDivElement[] | null[]>([]);
  const dramaWishlistOptionRef = useRef<HTMLDivElement[] | null[]>([]);
  const movieWishlistOptionRef = useRef<HTMLDivElement[] | null[]>([]);
  const comicsWishlistOptionRef = useRef<HTMLDivElement[] | null[]>([]);

  // ================================================================================================== zustand

  // user 전역 상태 훅
  const { user } = useUserStore();

  // ================================================================================================== react query

  // queryClient 훅
  const queryClient = useQueryClient();

  // 위시리스트 API 인스턴스
  const wishlistApi = new Wishlist();

  // 위시리스트 조회
  const { data, isLoading } = useQuery<WishlistListResponseDto>({
    queryKey: wishlistQueryKeys.wishlist.list(userId),
    queryFn: async () => {
      return (await wishlistApi.getWishlist({ userId })).data;
    },
    enabled: !!userId,
  });

  /**
   * 위시리스트 삭제 뮤테이션
   */
  const deleteWishlistMutation = useMutation({
    mutationKey: wishlistQueryKeys.wishlist.delete(userId),
    mutationFn: async ({
      apiId,
      originalMediaType,
      userId,
    }: WishlistRequestDto & { title: string }) =>
      (
        await wishlistApi.deleteWishlist({
          userId: userId,
          originalMediaType: originalMediaType,
          apiId: apiId,
        })
      ).data,
    onSuccess: (res, { title }) => {
      if (res) {
        toast.success(t('info.removedFromWishlist', { title: title }), {
          autoClose: 1000,
          style: { whiteSpace: 'pre-line' },
        });
      } else {
        toast.warning(t('info.notExistsInWishlist', { title: title }), {
          autoClose: 1000,
          style: { whiteSpace: 'pre-line' },
        });
      }
    },
    onError: (_err, { title }) => {
      toast.error(t('error.failedToRemoveWishlist', { title: title }), {
        toastId: 'wishlist_remove_error_' + title,
      });
    },
    onSettled: () => {
      setIsExecuting(false);
      queryClient.invalidateQueries({
        queryKey: wishlistQueryKeys.wishlist.all,
      });
    },
  });

  // ================================================================================================== function

  /**
   * 위시리스트 삭제 버튼 클릭시 처리
   * @param apiId Api ID
   * @param originalMediaType 원본 미디어 타입
   * @param title 제목
   */
  const handleWishlistDeleteOnClick = (
    apiId: number,
    originalMediaType: string,
    title: string
  ) => {
    setIsExecuting(true);
    deleteWishlistMutation.mutate({
      apiId: String(apiId),
      originalMediaType: originalMediaType,
      userId: userId,
      title: title,
    });
    // 옵션 닫기
    setWishlistOptionIsOpen(false);
  };

  /**
   * 옵션 버튼 클릭시 처리
   */
  const handleWishlistOptionOnClick = (
    originalMediaType: string,
    index: number
  ) => {
    setWishlistOriginalMediaType(originalMediaType);
    setWishlistOptionIndex(index);
    setWishlistOptionIsOpen((prev) => !prev);
  };

  // ================================================================================================== useEffect

  /**
   * user정보가 없으면 홈으로 이동
   */
  useEffect(() => {
    if (!user) {
      navigate('/');
      return;
    }
  }, [user, navigate]);

  /**
   * 마우스 클릭/키보드 키다운 이벤트
   */
  useEffect(() => {
    // 위시리스트 옵션 바깥 영역 클릭 이벤트
    const handleOnClickOutside = (e: MouseEvent) => {
      // 옵션이 열려있지 않으면 무시
      if (!wishlistOptionIsOpen) {
        return;
      }

      // 각 ref에 현재 인덱스가 존재하고, 클릭한 타겟이 해당 ref 내부에 있는지 확인
      const isAniWishlistOptionRef =
        aniWishlistOptionRef.current &&
        aniWishlistOptionRef.current[wishlistOptionIndex]?.contains(
          e.target as Node
        );
      const isDramaWishlistOptionRef =
        dramaWishlistOptionRef.current &&
        dramaWishlistOptionRef.current[wishlistOptionIndex]?.contains(
          e.target as Node
        );
      const isMovieWishlistOptionRef =
        movieWishlistOptionRef.current &&
        movieWishlistOptionRef.current[wishlistOptionIndex]?.contains(
          e.target as Node
        );
      const isComicsWishlistOptionRef =
        comicsWishlistOptionRef.current &&
        comicsWishlistOptionRef.current[wishlistOptionIndex]?.contains(
          e.target as Node
        );

      // 어느 ref에도 해당하지 않으면 옵션 닫기
      if (
        !isAniWishlistOptionRef &&
        !isDramaWishlistOptionRef &&
        !isMovieWishlistOptionRef &&
        !isComicsWishlistOptionRef
      ) {
        setWishlistOptionIsOpen(false);
      }
    };
    // 필터 및 자동완성박스 esc 키다운 이벤트
    const handleOnKeyDown = (e: globalThis.KeyboardEvent) => {
      if (e.key === ESC_KEY) {
        // 위시리스트 옵션 닫기
        setWishlistOptionIsOpen(false);
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
  }, [
    wishlistOptionIsOpen,
    wishlistOptionIndex,
    aniWishlistOptionRef,
    dramaWishlistOptionRef,
    movieWishlistOptionRef,
    comicsWishlistOptionRef,
  ]);

  /**
   * data 변경시 옵션 ref 초기화
   */
  useEffect(() => {
    aniWishlistOptionRef.current = [];
    dramaWishlistOptionRef.current = [];
    movieWishlistOptionRef.current = [];
    comicsWishlistOptionRef.current = [];
  }, [data]);
  // ================================================================================================== return

  return {
    data: data,
    isLoading: isLoading,
    handleWishlistDeleteOnClick: handleWishlistDeleteOnClick,
    isExecuting: isExecuting,
    wishlistOptionIsOpen: wishlistOptionIsOpen,
    handleWishlistOptionOnClick: handleWishlistOptionOnClick,
    aniWishlistOptionRef: aniWishlistOptionRef,
    dramaWishlistOptionRef: dramaWishlistOptionRef,
    movieWishlistOptionRef: movieWishlistOptionRef,
    comicsWishlistOptionRef: comicsWishlistOptionRef,
    wishlistOriginalMediaType: wishlistOriginalMediaType,
    wishlistOptionIndex: wishlistOptionIndex,
  };
};
