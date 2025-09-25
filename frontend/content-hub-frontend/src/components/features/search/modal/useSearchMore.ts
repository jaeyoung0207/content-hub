import {
  Dispatch,
  SetStateAction,
  useCallback,
  useEffect,
  useRef,
  useState,
} from 'react';
import { Search } from '@/api/Search';
import {
  ESC_KEY,
  INFINITE_SCROLL_THROTTLE_DELAY,
  MEDIA_TYPE_KIND,
} from '@/components/common/constants/constants';
import { useInfiniteQuery, useQueryClient } from '@tanstack/react-query';
import { AxiosError } from 'axios';
import { SearchCommonResultType } from '../useSearch';
import { useSearchParams } from 'react-router-dom';
import { searchQueryKeys } from '../queryKeys/searchQueryKeys';
import throttle from 'lodash/throttle';
import { useUserStore } from '@/components/common/store/globalStateStore';
import {
  getDisplayMediaType,
  mappingToMediaType,
} from '@/components/common/utils/convertUtil';

/**
 * 전체보기 모달화면 훅 결과 타입
 */
type UseSearchConentModalReturnType = {
  data: UseInfiniteQueryResultType | undefined;
  hasNextPage: boolean;
  isFetchingNextPage: boolean;
  setObserveTarget: Dispatch<SetStateAction<HTMLDivElement | null>>;
  handleModalClose: () => void;
};

/**
 * 무한스크롤 결과 타입
 */
export type UseInfiniteQueryResultType = {
  pages: SearchCommonResultType[][];
  pageParams: (number | undefined)[];
};

/**
 * 전체보기 모달화면 훅
 * @param keyword 검색어
 * @param displayMediaType 미디어 타입
 * @returns UseSearchConentModalReturnType
 */
export const useSearchMore = (
  keyword: string,
  isAdult: boolean,
  displayMediaType: string
): UseSearchConentModalReturnType => {
  // ================================================================================================== URL query string

  // URL 쿼리스트링 제어
  const [searchParams, setSearchParams] = useSearchParams();

  // ================================================================================================== react hook

  // 무한스크롤 div태그 관찰용 state
  const [observeTarget, setObserveTarget] = useState<HTMLDivElement | null>(
    null
  );
  // 전체 페이지 수를 저장하는 ref
  const totalPagesRef = useRef<number | undefined>(0);

  // ================================================================================================== zustand

  // 유저 정보 전역 상태 저장 훅
  const { user } = useUserStore();

  // ================================================================================================== react query

  // React Query 클라이언트 인스턴스
  const queryClient = useQueryClient();

  // 검색 API 인스턴스 생성
  const searchApi = new Search();

  // 전체보기 검색결과를 가져오기 위한 API 호출 함수
  const judgeExecApi = async (pageParam: number) => {
    if (displayMediaType == getDisplayMediaType().aniCode) {
      // 애니메이션 검색 API 호출
      const response = await await searchApi.searchAni(
        { keyword: keyword, page: pageParam, user_id: user?.userId },
        {}
      );
      // 전체 페이지 수 저장
      if (response.data.page === 1) {
        totalPagesRef.current = response.data.totalPages;
      }
      return response.data.aniResults;
    } else if (
      displayMediaType == getDisplayMediaType().dramaCode ||
      displayMediaType == getDisplayMediaType().varietyCode ||
      displayMediaType == getDisplayMediaType().documentaryCode ||
      displayMediaType == getDisplayMediaType().kidsCode ||
      displayMediaType == getDisplayMediaType().newsCode
    ) {
      // 컨텐츠 미디어 타입 코드 가져오기
      const contentMediaType = mappingToMediaType(
        displayMediaType,
        MEDIA_TYPE_KIND.CONTENT_MEDIA_TYPE
      )!;
      // 드라마 검색 API 호출
      const response = await await searchApi.searchTvExceptAni(
        {
          keyword: keyword,
          content_media_type: contentMediaType,
          page: pageParam,
          user_id: user?.userId,
        },
        {}
      );
      // 전체 페이지 수 저장
      if (response.data.page === 1) {
        totalPagesRef.current = response.data.totalPages;
      }
      return response.data.dramaResults;
    } else if (displayMediaType == getDisplayMediaType().movieCode) {
      // 영화 검색 API 호출
      const response = await await searchApi.searchMovie(
        { keyword: keyword, page: pageParam, user_id: user?.userId },
        {}
      );
      // 전체 페이지 수 저장
      if (response.data.page === 1) {
        totalPagesRef.current = response.data.totalPages;
      }
      return response.data.movieResults;
    } else if (displayMediaType == getDisplayMediaType().comicsCode) {
      // 만화 검색 API 호출
      const response = await await searchApi.searchComics(
        {
          keyword: keyword,
          page: pageParam,
          is_main_page: false,
          user_id: user?.userId,
        },
        {}
      );
      // 전체 페이지 수 저장
      if (response.data.page === 1) {
        totalPagesRef.current = response.data.totalPages;
      }
      return response.data.comicsResults;
    } else {
      return null;
    }
  };

  // useInfiniteQuery 정의
  const {
    data, // 서버에 요청해서 받아온 데이터
    fetchNextPage, // 다음페이지 호출
    isFetchingNextPage, // 다음페이지 로딩중 여부
    hasNextPage, // 가져올 다음페이지가 있는지 여부를 나타냄(boolean). getNextPageParam옵션을 통해 확인가능
  } = useInfiniteQuery<
    SearchCommonResultType[], // queryFn이 반환하는 원본 데이터
    AxiosError, // 에러 타입 (보통 AxiosError)
    UseInfiniteQueryResultType, // 반환할 최종 데이터 형태 (select로 가공한 경우)
    [string, string, string, boolean, string, number | undefined], // query key의 타입 (예: [string, string] -> [루트 키, 서브 키])
    number | undefined // pageParam 타입 (보통 number | undefined)
  >({
    // useInfiniteQuery의 키 지정
    queryKey: searchQueryKeys.searchMore.searchMore(
      keyword,
      isAdult,
      displayMediaType,
      user?.userId
    ) as [string, string, string, boolean, string, number | undefined],
    // 쿼리가 데이터를 요청하는 데 사용할 함수/API 지정
    queryFn: async ({ pageParam = 1 }) => {
      const responseDataResults = await judgeExecApi(pageParam);
      return responseDataResults ?? []; // 제네릭 1번째 인자가 배열이므로 반드시 배열 반환
    },
    // lastPageData : 가장 최근에 불러온 캐싱된 데이터 / allPages: 지금까지 불러온 데이터
    // 새 데이터를 받아올 때 마지막페이지와 전체페이지 배열을 함께 받아옴
    // 더 불러올 데이터가 있는지 여부를 결정하는데 사용
    // 반환값이 다음 API호출할때의 pageParam으로 들어감
    getNextPageParam: (_, allPages) => {
      const currentPage = allPages.length;
      const totalPages = totalPagesRef.current ?? 1;
      return currentPage < totalPages ? currentPage + 1 : undefined;
    },
    select: (data) => ({
      pages: data.pages,
      pageParams: data.pageParams,
    }),
    initialPageParam: 1, // 초기 페이지 매개변수를 지정
    enabled: !!keyword && !!displayMediaType, // useInfiniteQuery가 실행되는 조건 지정
    staleTime: 0, // 데이터를 바로 stale로 간주
    gcTime: 0, // 캐시된 데이터를 바로 제거
  });

  // ================================================================================================== function

  /**
   * 다음 페이지를 가져오는 함수를 스로틀하여 호출 빈도를 조절
   */
  const throttledFetchNextPage = throttle(() => {
    fetchNextPage();
  }, INFINITE_SCROLL_THROTTLE_DELAY);

  /**
   * 무한 스크롤 기능을 구현하기 위한 IntersectionObserver 콜백 함수
   * observeTarget가 화면에 나타나면 observerCallback이 호출되어 fetchNextPage를 호출
   * @param entries 관찰 대상의 교차 상태를 나타내는 IntersectionObserverEntry 배열
   */
  const observerCallback = useCallback<IntersectionObserverCallback>(
    (entries) => {
      entries.forEach((entry) => {
        // observeTarget이 화면에 나타나고, 다음 페이지가 있고, 현재 페이지를 가져오고 있지 않은 경우
        if (entry.isIntersecting && hasNextPage && !isFetchingNextPage) {
          console.log('★★★fetchNextPage실행!!!!!!!!!★★★');
          // fetchNextPage를 호출
          throttledFetchNextPage();
        }
      });
    },
    [hasNextPage, isFetchingNextPage, throttledFetchNextPage]
  );

  /**
   * 모달 닫을 시 처리
   */
  const handleModalClose = useCallback(() => {
    // URL 쿼리스트링 제거
    searchParams.delete('viewMore');
    setSearchParams(searchParams); //  URL이 바뀌면 React Router가 감지해서 리렌더링 발생
  }, [searchParams, setSearchParams]);

  // ================================================================================================== useEffect

  /**
   * 무한 스크롤 기능을 구현하기 위한 useEffect
   * observeTarget이 화면에 나타나면 observerCallback이 호출되어 fetchNextPage를 호출하여 무한 스크롤 기능을 구현
   */
  useEffect(() => {
    // observeTarget이 null이거나 hasNextPage가 false이거나 isFetchingNextPage가 true인 경우에는 관찰을 중지
    if (!observeTarget || !hasNextPage || isFetchingNextPage) {
      return;
    }

    // 새로운 IntersectionObserver를 생성
    // observerCallback을 사용하여 observeTarget이 화면에 나타날 때 fetchNextPage를 호출
    const observer = new IntersectionObserver(observerCallback, {
      threshold: 0.1,
    });

    // observeTarget이 화면에 보이면 관찰을 시작
    observer.observe(observeTarget);

    // observeTarget이 변경되면 이전에 관찰하던 타겟은 관찰을 중지
    return () => {
      observer.unobserve(observeTarget);
    };
  }, [observeTarget, hasNextPage, isFetchingNextPage, observerCallback]);

  /**
   * 이벤트 리스너를 설정하는 useEffect
   * 배경 스크롤을 막고, ESC 키를 눌렀을 때 모달을 닫는 이벤트 리스너를 등록
   * 이벤트 리스너는 컴포넌트가 언마운트될 때 제거
   */
  useEffect(() => {
    // ESC키 눌렀을 시 모달 종료
    const handleOnEscKey = (e: KeyboardEvent) =>
      e.key === ESC_KEY && handleModalClose();
    // 배경 스크롤 막기
    document.body.style.overflow = 'hidden';
    // ESC 키다운 이벤트리스너 등록
    document.addEventListener('keydown', handleOnEscKey);

    return () => {
      // 배경 스크롤 복원
      document.body.style.removeProperty('overflow');
      // ESC 키다운 이벤트리스너 제거
      document.removeEventListener('keydown', handleOnEscKey);
    };
  }, [handleModalClose]);

  /**
   * 컴포넌트 언마운트 시 검색결과 캐시 제거
   * 모달을 닫고 검색페이지로 돌아왔을 때 기존 검색결과(+ 위시리스트)가 남아있는 현상 방지
   * 의존성 배열에 넣은 값이 바뀔때마다 실행되지 않도록 의존성 배열을 빈 배열로 설정
   */
  /* eslint-disable react-hooks/exhaustive-deps */
  useEffect(() => {
    return () => {
      queryClient.removeQueries({
        queryKey: searchQueryKeys.search.search(
          keyword,
          String(isAdult),
          user?.userId
        ),
        exact: true, // 완전히 일치하는 쿼리만 제거
      });
    };
  }, []);

  // ================================================================================================== return

  return {
    setObserveTarget: setObserveTarget,
    data: data,
    hasNextPage: hasNextPage,
    isFetchingNextPage: isFetchingNextPage,
    handleModalClose: handleModalClose,
  };
};
