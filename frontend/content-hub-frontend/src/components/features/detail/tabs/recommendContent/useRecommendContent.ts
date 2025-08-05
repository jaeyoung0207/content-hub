import { DetailComicsResponseDto, TmdbRecommendationsMovieResultsDto, TmdbRecommendationsTvResultsDto } from "@/api/data-contracts";
import { Detail } from "@/api/Detail";
import { useInfiniteQuery } from "@tanstack/react-query";
import { AxiosError } from "axios";
import { DetailResponseType } from "../../useDetail";
import { Dispatch, SetStateAction, useCallback, useEffect, useState } from "react";
import { MEDIA_TYPE } from "@/components/common/constants/constants";
import { detailQueryKeys } from "../../queryKeys/detailQueryKeys";

/**
 * 추천 콘텐츠 무한 스크롤 결과 타입
 */
export type RecommendUseInfiniteQueryResultType = {
    pages: (RecommendContentResultType[] | undefined)[],
    // pages: RecommendContentResultListType[],
    pageParams: (number | undefined)[],
}

/**
 * 추천 콘텐츠 훅 반환 타입
 */
type useRecommendContentReturnType = {
    data: RecommendUseInfiniteQueryResultType | undefined, // 서버에 요청해서 받아온 데이터
    isFetchingNextPage: boolean, // 다음 페이지 로딩중 여부
    hasNextPage: boolean, // 가져올 다음 페이지가 있는지 여부
    setObserveTarget: Dispatch<SetStateAction<HTMLDivElement | null>>, // 무한스크롤 div태그 관찰용 state
}

// 추천 콘텐츠 결과 타입
export type RecommendContentResultType = TmdbRecommendationsTvResultsDto | TmdbRecommendationsMovieResultsDto | DetailComicsResponseDto;
// 추천 콘텐츠 결과 리스트 타입
export type RecommendContentResultListType = TmdbRecommendationsTvResultsDto[] | TmdbRecommendationsMovieResultsDto[] | DetailComicsResponseDto[];

/**
 * 추천 콘텐츠 정보를 가져오기 위한 훅
 * @param detailResult 추천 콘텐츠를 가져오기 위한 상세 정보 결과
 * @param originalMediaType 원본 미디어 타입
 * @returns useRecommendContent 훅 반환값
 */
export const useRecommendContent = (detailResult: DetailResponseType, originalMediaType: string): useRecommendContentReturnType => {

    // ================================================================================================== useState

    // 무한스크롤 div태그 관찰용 state
    const [observeTarget, setObserveTarget] = useState<HTMLDivElement | null>(null);

    // ================================================================================================== react query

    /**
     * 추천 콘텐츠를 가져오기 위한 API 호출 함수
     * @param pageParam 다음 페이지를 가져오기 위한 페이지 번호
     * @returns 추천 콘텐츠 결과 배열
     */
    const judgeExecApi = async (pageParam: number) => {
        // Detail API 인스턴스 생성
        const detailApi = new Detail();
        // 원본 미디어 타입이 ANI 또는 DRAMA인 경우
        if (originalMediaType == MEDIA_TYPE.ANI || originalMediaType == MEDIA_TYPE.DRAMA) {
            return (await detailApi.getTvRecommendations({
                series_id: detailResult.id!,
                page: pageParam,
            })).data.results;
        }
        // 원본 미디어 타입이 MOVIE인 경우
        else if (originalMediaType == MEDIA_TYPE.MOVIE) {
            return (await detailApi.getMovieRecommendations({
                movie_id: detailResult.id!,
                page: pageParam,
            })).data.results;
        }
        // 원본 미디어 타입이 COMICS인 경우
        else if (originalMediaType == MEDIA_TYPE.COMICS) {
            return (await detailApi.getComicsRecommendations({
                mediaId: detailResult.id!,
                page: pageParam,
            })).data.results;
        }
        // 그 외의 경우는 undefined 반환
        else {
            return undefined;
        }
    }

    /**
     * useInfiniteQuery 훅을 사용하여 추천 콘텐츠를 가져오는 쿼리
     */
    const {
        data, // 서버에 요청해서 받아온 데이터
        fetchNextPage, // 다음페이지 호출
        isFetchingNextPage, // 다음페이지 로딩중 여부
        hasNextPage, // 가져올 다음페이지가 있는지 여부를 나타냄(boolean). getNextPageParam옵션을 통해 확인가능
    } = useInfiniteQuery<
        RecommendContentResultType[] | undefined,  // queryFn이 반환하는 원본 데이터
        AxiosError, // 에러 타입 (보통 AxiosError)
        RecommendUseInfiniteQueryResultType, // 반환할 최종 데이터 형태 (select로 가공한 경우)
        [string, string, string, string], // query key의 타입 (예: [string, string] -> [루트 키, 서브 키])
        number | undefined // pageParam 타입 (보통 number | undefined)
    >({
        // useInfiniteQuery의 키 지정
        queryKey: detailQueryKeys.detail.recommendationContent.list(originalMediaType, detailResult.id!.toString()),
        // 쿼리가 데이터를 요청하는 데 사용할 함수/API 지정
        queryFn: async ({ pageParam = 1 }) => {
            const response = await judgeExecApi(pageParam);
            return response ?? [] // 제네릭 1번째 인자가 배열이므로 반드시 배열 반환
        },
        // lastPageData : 가장 최근에 불러온 캐싱된 데이터 / allPages: 지금까지 불러온 데이터
        // 새 데이터를 받아올 때 마지막페이지와 전체페이지 배열을 함께 받아옴
        // 더 불러올 데이터가 있는지 여부를 결정하는데 사용
        // 반환값이 다음 API호출할때의 pageParam으로 들어감
        getNextPageParam: (lastPageData, allPages) => {
            return (!lastPageData || lastPageData.length === 0) ? undefined : allPages.length + 1;
        },
        select: data => ({
            // useInfiniteQuery의 반환값을 가공하여 반환
            // pages: 현재 페이지의 데이터와 이전 페이지의 데이터를 합쳐서 반환
            pages: data.pages,
            // pageParams: 현재 페이지의 매개변수와 이전 페이지의 매개변수를 합쳐서 반환
            pageParams: data.pageParams,
        }),
        // 초기 페이지 매개변수를 지정
        initialPageParam: 1,
        // enabled: !!tabIndex, // useInfiniteQuery가 실행되는 조건 지정
    });

    // ================================================================================================== function

    /**
     * 무한 스크롤 기능을 구현하기 위한 IntersectionObserver 콜백 함수
     * observeTarget가 화면에 나타나면 observerCallback이 호출되어 fetchNextPage를 호출
     * @param entries 관찰 대상의 교차 상태를 나타내는 IntersectionObserverEntry 배열
     */
    const observerCallback = useCallback<IntersectionObserverCallback>((entries) => {
        entries.forEach((entry) => {
            // observeTarget이 화면에 나타나고, 다음 페이지가 있고, 현재 페이지를 가져오고 있지 않은 경우
            if (entry.isIntersecting && hasNextPage && !isFetchingNextPage) {
                console.log("★★★fetchNextPage실행!!!!!!!!!★★★");
                // fetchNextPage를 호출
                fetchNextPage();
            }
        })
    }, [hasNextPage, isFetchingNextPage, fetchNextPage]);

    // ================================================================================================== useEffect

    /**
     * 무한 스크롤 기능을 구현하기 위한 useEffect
     * observeTarget가 null이 아니고, hasNextPage가 true이며, 
     * isFetchingNextPage가 false일 때만 IntersectionObserver를 설정
     * observeTarget이 화면에 나타나면 observerCallback이 호출되어 fetchNextPage를 호출하여 무한 스크롤 기능을 구현
     * 실행조건: observeTarget, hasNextPage, isFetchingNextPage가 변경될 때
     */
    useEffect(() => {
        console.log("★★★useEffect★★★");
        // observeTarget이 null이거나 hasNextPage가 false이거나 isFetchingNextPage가 true인 경우에는 관찰을 중지
        if (!observeTarget || !hasNextPage || isFetchingNextPage) {
            console.log("useEffect Return################################");
            return
        }

        // 새로운 IntersectionObserver를 생성
        // observerCallback을 사용하여 observeTarget이 화면에 나타날 때 fetchNextPage를 호출
        const observer = new IntersectionObserver(observerCallback, {
            threshold: 0.1,
        });

        // observeTarget이 화면에 보이면 관찰을 시작
        console.log("✅ 관찰 시작:", observeTarget);
        observer.observe(observeTarget);

        // observeTarget이 변경되면 이전에 관찰하던 타겟은 관찰을 중지
        return () => {
            observer.unobserve(observeTarget);
            console.log("🛑 관찰 종료");
        }

    }, [observeTarget, hasNextPage, isFetchingNextPage, observerCallback]);

    // ================================================================================================== return

    return {
        data: data,
        isFetchingNextPage: isFetchingNextPage,
        hasNextPage: hasNextPage,
        setObserveTarget: setObserveTarget,
    }
}