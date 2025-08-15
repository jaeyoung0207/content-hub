import { Detail } from '@/api/Detail';
import {
  Dispatch,
  SetStateAction,
  useCallback,
  useEffect,
  useState,
} from 'react';
import { DetailResponseType } from '../../useDetail';
import { useInfiniteQuery } from '@tanstack/react-query';
import { AniListCharactersNodesDto } from '@/api/data-contracts';
import { AxiosError } from 'axios';
import { detailQueryKeys } from '../../queryKeys/detailQueryKeys';

/**
 * 만화 정보 무한스크롤 쿼리 결과 타입
 */
type InformationUseInfiniteQueryResultType = {
  pages: (AniListCharactersNodesDto[] | undefined)[];
  pageParams: (number | undefined)[];
};

/**
 * 만화 정보 컴포넌트 훅 반환 타입
 */
type UseComicsInformationReturnType = {
  data: InformationUseInfiniteQueryResultType | undefined; // 서버에 요청해서 받아온 데이터
  isFetchingNextPage: boolean; // 다음 페이지 로딩중 여부
  hasNextPage: boolean; // 가져올 다음 페이지가 있는지 여부
  setObserveTarget: Dispatch<SetStateAction<HTMLDivElement | null>> | undefined;
};

/**
 * 만화 정보 컴포넌트 훅
 */
export const useComicsInformation = (
  detailResult: DetailResponseType,
  originalMediaType: string
): UseComicsInformationReturnType => {
  // ================================================================================================== react hook

  // 무한스크롤 div태그 관찰용 state
  const [observeTarget, setObserveTarget] = useState<HTMLDivElement | null>(
    null
  );

  // ================================================================================================== react query

  // Detail API 인스턴스 생성
  const detailApi = new Detail();

  /**
   * useInfiniteQuery 훅을 사용하여 캐릭터 정보를 무한 스크롤로 가져오는 쿼리
   */
  const { data, fetchNextPage, isFetchingNextPage, hasNextPage } =
    useInfiniteQuery<
      AniListCharactersNodesDto[] | undefined,
      AxiosError,
      InformationUseInfiniteQueryResultType,
      [string, string, string, string],
      number | undefined
    >({
      queryKey: detailQueryKeys.detail.contentInformation.list(
        originalMediaType,
        detailResult.id!.toString()
      ) as [string, string, string, string],
      queryFn: async ({ pageParam = 1 }) => {
        const response = await detailApi.getComicsDetail({
          comics_id: detailResult.id!,
          page: pageParam,
        });
        return response.data.characters?.nodes;
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
    });

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
          fetchNextPage();
        }
      });
    },
    [hasNextPage, isFetchingNextPage, fetchNextPage]
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

  // ================================================================================================== return

  return {
    data: data,
    isFetchingNextPage: isFetchingNextPage,
    hasNextPage: hasNextPage,
    setObserveTarget: setObserveTarget,
  };
};
