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
import { ESC_KEY, SEARCH_TYPE } from '@/components/common/constants/constants';
import { useTranslation } from 'react-i18next';

/**
 * useWishlist 훅 반환 타입
 */
type UseWishlistReturnType = {
  data: WishlistListResponseDto | undefined;
  isLoading: boolean;
  handleWishlistDeleteOnClick: (
    apiId: number,
    contentMediaType: string,
    title: string
  ) => void;
  isExecuting: boolean;
  wishlistOptionIsOpen: boolean;
  handleWishlistOptionOnClick: (
    contentMediaType: string,
    index: number
  ) => void;
  aniWishlistOptionRef: RefObject<HTMLDivElement[] | null[]>;
  dramaWishlistOptionRef: RefObject<HTMLDivElement[] | null[]>;
  documentaryWishlistOptionRef: RefObject<HTMLDivElement[] | null[]>;
  kidsWishlistOptionRef: RefObject<HTMLDivElement[] | null[]>;
  newsWishlistOptionRef: RefObject<HTMLDivElement[] | null[]>;
  varietyWishlistOptionRef: RefObject<HTMLDivElement[] | null[]>;
  movieWishlistOptionRef: RefObject<HTMLDivElement[] | null[]>;
  comicsWishlistOptionRef: RefObject<HTMLDivElement[] | null[]>;
  wishlistContentMediaType: string;
  wishlistOptionIndex: number;
  isOmitAniList: boolean;
  isOmitDramaList: boolean;
  isOmitDocumentaryList: boolean;
  isOmitKidsList: boolean;
  isOmitNewsList: boolean;
  isOmitVarietyList: boolean;
  isOmitMovieList: boolean;
  isOmitComicsList: boolean;
  handleOnClickOmitWishlist: (searchType: string) => void;
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
  // 타겟 contentMediaType
  const [wishlistContentMediaType, setWishlistContentMediaType] =
    useState<string>('');
  // 옵션 인덱스
  const [wishlistOptionIndex, setWishlistOptionIndex] = useState<number>(-1);

  const [isOmitAniList, setIsOmitAniList] = useState<boolean>(true);
  const [isOmitDramaList, setIsOmitDramaList] = useState<boolean>(true);
  const [isOmitDocumentaryList, setIsOmitDocumentaryList] =
    useState<boolean>(true);
  const [isOmitKidsList, setIsOmitKidsList] = useState<boolean>(true);
  const [isOmitNewsList, setIsOmitNewsList] = useState<boolean>(true);
  const [isOmitVarietyList, setIsOmitVarietyList] = useState<boolean>(true);
  const [isOmitMovieList, setIsOmitMovieList] = useState<boolean>(true);
  const [isOmitComicsList, setIsOmitComicsList] = useState<boolean>(true);

  // 옵션 참조
  const aniWishlistOptionRef = useRef<HTMLDivElement[] | null[]>([]);
  const dramaWishlistOptionRef = useRef<HTMLDivElement[] | null[]>([]);
  const documentaryWishlistOptionRef = useRef<HTMLDivElement[] | null[]>([]);
  const kidsWishlistOptionRef = useRef<HTMLDivElement[] | null[]>([]);
  const newsWishlistOptionRef = useRef<HTMLDivElement[] | null[]>([]);
  const varietyWishlistOptionRef = useRef<HTMLDivElement[] | null[]>([]);
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
      contentMediaType,
      userId,
    }: WishlistRequestDto & { title: string }) =>
      (
        await wishlistApi.deleteWishlist({
          userId: userId,
          contentMediaType: contentMediaType,
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
   * @param contentMediaType 컨텐츠 미디어 타입
   * @param title 제목
   */
  const handleWishlistDeleteOnClick = (
    apiId: number,
    contentMediaType: string,
    title: string
  ) => {
    setIsExecuting(true);
    deleteWishlistMutation.mutate({
      apiId: String(apiId),
      contentMediaType: contentMediaType,
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
    contentMediaType: string,
    index: number
  ) => {
    setWishlistContentMediaType(contentMediaType);
    setWishlistOptionIndex(index);
    setWishlistOptionIsOpen((prev) => !prev);
  };

  /**
   * 생략 처리 토글
   * @param searchType 미디어 타입
   */
  const handleOnClickOmitWishlist = (searchType: string) => {
    switch (searchType) {
      case SEARCH_TYPE.ANI:
        setIsOmitAniList((prev) => !prev);
        break;
      case SEARCH_TYPE.DRAMA:
        setIsOmitDramaList((prev) => !prev);
        break;
      case SEARCH_TYPE.MOVIE:
        setIsOmitMovieList((prev) => !prev);
        break;
      case SEARCH_TYPE.DOCUMENTARY:
        setIsOmitDocumentaryList((prev) => !prev);
        break;
      case SEARCH_TYPE.KIDS:
        setIsOmitKidsList((prev) => !prev);
        break;
      case SEARCH_TYPE.NEWS:
        setIsOmitNewsList((prev) => !prev);
        break;
      case SEARCH_TYPE.VARIETY:
        setIsOmitVarietyList((prev) => !prev);
        break;
      case SEARCH_TYPE.COMICS:
        setIsOmitComicsList((prev) => !prev);
        break;
      default:
        break;
    }
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
      const isDocumentaryWishlistOptionRef =
        documentaryWishlistOptionRef.current &&
        documentaryWishlistOptionRef.current[wishlistOptionIndex]?.contains(
          e.target as Node
        );
      const isKidsWishlistOptionRef =
        kidsWishlistOptionRef.current &&
        kidsWishlistOptionRef.current[wishlistOptionIndex]?.contains(
          e.target as Node
        );
      const isNewsWishlistOptionRef =
        newsWishlistOptionRef.current &&
        newsWishlistOptionRef.current[wishlistOptionIndex]?.contains(
          e.target as Node
        );
      const isVarietyWishlistOptionRef =
        varietyWishlistOptionRef.current &&
        varietyWishlistOptionRef.current[wishlistOptionIndex]?.contains(
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
        !isDocumentaryWishlistOptionRef &&
        !isKidsWishlistOptionRef &&
        !isNewsWishlistOptionRef &&
        !isVarietyWishlistOptionRef &&
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
    documentaryWishlistOptionRef,
    kidsWishlistOptionRef,
    newsWishlistOptionRef,
    varietyWishlistOptionRef,
    movieWishlistOptionRef,
    comicsWishlistOptionRef,
  ]);

  /**
   * data 변경시 옵션 ref 초기화
   */
  useEffect(() => {
    aniWishlistOptionRef.current = [];
    dramaWishlistOptionRef.current = [];
    documentaryWishlistOptionRef.current = [];
    kidsWishlistOptionRef.current = [];
    newsWishlistOptionRef.current = [];
    varietyWishlistOptionRef.current = [];
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
    documentaryWishlistOptionRef: documentaryWishlistOptionRef,
    kidsWishlistOptionRef: kidsWishlistOptionRef,
    newsWishlistOptionRef: newsWishlistOptionRef,
    varietyWishlistOptionRef: varietyWishlistOptionRef,
    movieWishlistOptionRef: movieWishlistOptionRef,
    comicsWishlistOptionRef: comicsWishlistOptionRef,
    wishlistContentMediaType: wishlistContentMediaType,
    wishlistOptionIndex: wishlistOptionIndex,
    isOmitAniList: isOmitAniList,
    isOmitDramaList: isOmitDramaList,
    isOmitDocumentaryList: isOmitDocumentaryList,
    isOmitKidsList: isOmitKidsList,
    isOmitNewsList: isOmitNewsList,
    isOmitVarietyList: isOmitVarietyList,
    isOmitMovieList: isOmitMovieList,
    isOmitComicsList: isOmitComicsList,
    handleOnClickOmitWishlist: handleOnClickOmitWishlist,
  };
};
