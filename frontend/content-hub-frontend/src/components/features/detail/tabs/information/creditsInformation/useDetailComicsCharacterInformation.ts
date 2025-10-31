import { DetailInformationApi } from '@/api/DetailInformationApi';
import {
  Dispatch,
  SetStateAction,
  useCallback,
  useEffect,
  useMemo,
  useRef,
  useState,
} from 'react';
import { DetailResponseType } from '../../../useDetail';
import { useInfiniteQuery } from '@tanstack/react-query';
import {
  AniListCharactersEdgesDto,
  AniListStaffEdgesDto,
} from '@/api/data-contracts';
import { AxiosError } from 'axios';
import { detailQueryKeys } from '../../../queryKeys/detailQueryKeys';
import {
  COMICS_CREDITS_TYPE,
  INFINITE_SCROLL_THROTTLE_DELAY,
} from '@/components/common/constants/constants';
import { throttle } from 'lodash-es';
import { cachedListOptions } from '@/components/common/config/queryOptions';

/**
 * 만화 캐릭터/제작진 정보 무한스크롤 쿼리 결과 타입
 */
type InformationUseInfiniteQueryResultType = {
  pages: (AniListCharactersEdgesDto[] | AniListStaffEdgesDto[] | undefined)[];
  pageParams: (number | undefined)[];
};

/**
 * 만화 캐릭터/제작진 정보 컴포넌트 훅 반환 타입
 */
type UseDetailComicsCharacterInformationReturnType = {
  data: InformationUseInfiniteQueryResultType | undefined; // 서버에 요청해서 받아온 데이터
  isFetchingNextPage: boolean; // 다음 페이지 로딩중 여부
  hasNextPage: boolean; // 가져올 다음 페이지가 있는지 여부
  setObserveTarget: Dispatch<SetStateAction<HTMLDivElement | null>> | undefined;
};

/**
 * 만화 캐릭터/제작진 정보 컴포넌트 훅
 */
export const useDetailComicsCharacterInformation = (
  detailResult: DetailResponseType,
  contentMediaType: string,
  creditsType: string
): UseDetailComicsCharacterInformationReturnType => {
  // ================================================================================================== react hook

  // 무한스크롤 div태그 관찰용 state
  const [observeTarget, setObserveTarget] = useState<HTMLDivElement | null>(
    null
  );
  // IntersectionObserver를 ref로 관리
  const observerRef = useRef<IntersectionObserver | null>(null);

  // ================================================================================================== react query

  // Detail API 인스턴스 생성
  const detailInformationApi = new DetailInformationApi();

  /**
   * useInfiniteQuery 훅을 사용하여 캐릭터 정보를 무한 스크롤로 가져오는 쿼리
   */
  const { data, fetchNextPage, isFetchingNextPage, hasNextPage } =
    useInfiniteQuery<
      AniListCharactersEdgesDto[] | AniListStaffEdgesDto[] | undefined,
      AxiosError,
      InformationUseInfiniteQueryResultType,
      [string, string, string, string],
      number | undefined
    >({
      queryKey:
        creditsType === COMICS_CREDITS_TYPE.CHARACTER
          ? (detailQueryKeys.detail.contentInformation.characterList(
              contentMediaType,
              detailResult.id!.toString()
            ) as [string, string, string, string])
          : (detailQueryKeys.detail.contentInformation.staffList(
              contentMediaType,
              detailResult.id!.toString()
            ) as [string, string, string, string]),
      queryFn: async ({ pageParam = 1 }) => {
        const response =
          creditsType === COMICS_CREDITS_TYPE.CHARACTER
            ? await detailInformationApi.getComicsCharacterList({
                comics_id: detailResult.id!,
                page: pageParam,
              })
            : await detailInformationApi.getComicsStaffList({
                comics_id: detailResult.id!,
                page: pageParam,
              });
        return response.data.edges ? response.data.edges : [];
      },
      getNextPageParam: (lastPageData, allPages) => {
        return !lastPageData || lastPageData.length === 0
          ? undefined
          : allPages.length + 1;
      },
      select: (data) => ({
        pages: data.pages,
        pageParams: data.pageParams,
      }),
      initialPageParam: 1,
      ...cachedListOptions, // 쿼리 공통 캐시형 프리셋
    });

  /**
   * 다음 페이지를 가져오는 함수를 스로틀하여 호출 빈도를 조절
   */
  const throttledFetchNextPage = useMemo(() => {
    return throttle(() => {
      fetchNextPage();
    }, INFINITE_SCROLL_THROTTLE_DELAY);
  }, [fetchNextPage]);

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
          // fetchNextPage를 호출
          throttledFetchNextPage();
        }
      });
    },
    [hasNextPage, isFetchingNextPage, throttledFetchNextPage]
  );

  // ================================================================================================== useEffect

  /**
   * 무한 스크롤 기능을 구현하기 위한 useEffect
   * observeTarget이 화면에 나타나면 observerCallback이 호출되어 fetchNextPage를 호출하여 무한 스크롤 기능을 구현
   */
  useEffect(() => {
    if (!observeTarget || !hasNextPage || isFetchingNextPage) {
      return;
    }

    // 기존 옵저버 정리 후 새로 생성
    if (observerRef.current) {
      observerRef.current.disconnect();
      observerRef.current = null;
    }

    // 새로운 IntersectionObserver를 생성
    // observerCallback을 사용하여 observeTarget이 화면에 나타날 때 fetchNextPage를 호출
    observerRef.current = new IntersectionObserver(observerCallback, {
      threshold: 0.1,
    });

    // observeTarget이 화면에 보이면 관찰을 시작
    observerRef.current.observe(observeTarget);

    // observeTarget이 변경되면 이전에 관찰하던 타겟은 관찰을 중지하고 옵저버를 정리
    return () => {
      observerRef.current?.disconnect();
      observerRef.current = null;
    };
  }, [observeTarget, hasNextPage, isFetchingNextPage, observerCallback]);

  /**
   * 컴포넌트 언마운트 시에 throttledFetchNextPage의 잔여 작업을 정리
   */
  useEffect(() => {
    return () => {
      throttledFetchNextPage.cancel();
    };
  }, [throttledFetchNextPage]);

  // ================================================================================================== return

  return {
    data: data,
    isFetchingNextPage: isFetchingNextPage,
    hasNextPage: hasNextPage,
    setObserveTarget: setObserveTarget,
  };
};
