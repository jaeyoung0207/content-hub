import { Dispatch, SetStateAction, useCallback, useEffect, useState } from "react";
import { SearchContent } from "@/api/SearchContent";
import { ESC_KEY, MEDIA_TYPE } from "@/components/common/constants/constants";
import { useInfiniteQuery } from "@tanstack/react-query";
import { AxiosError } from "axios";
import { SearchContentCommonResultListType } from "../useSearchContent";
import { useSearchParams } from "react-router-dom";
import { searchContentQueryKeys } from "../queryKeys/searchContentQueryKeys";

/**
 * 전체보기 모달화면 훅 결과 타입
 */
type UseSearchConentModalReturnType = {
    data: UseInfiniteQueryResultType | undefined,
    hasNextPage: boolean,
    isFetchingNextPage: boolean,
    setObserveTarget: Dispatch<SetStateAction<HTMLDivElement | null>>,
    handleModalClose: () => void,
}

/**
 * 무한스크롤 결과 타입
 */
export type UseInfiniteQueryResultType = {
    pages: SearchContentCommonResultListType[],
    pageParams: (number | undefined)[],
}

/**
 * 전체보기 모달화면 훅
 * @param keyword 검색어
 * @param mediaType 미디어 타입 
 * @returns UseSearchConentModalReturnType
 */
export const useSearchContentMore = (
    keyword: string,
    mediaType: string,
): UseSearchConentModalReturnType => {

    // ================================================================================================== URL query string

    // URL 쿼리스트링 제어
    const [searchParams, setSearchParams] = useSearchParams();

    // ================================================================================================== react hook

    // 무한스크롤 div태그 관찰용 state
    const [observeTarget, setObserveTarget] = useState<HTMLDivElement | null>(null);

    // ================================================================================================== react query

    // 검색 API 인스턴스 생성
    const searchContentApi = new SearchContent();

    // 전체보기 검색결과를 가져오기 위한 API 호출 함수
    const judgeExecApi = async (pageParam: number) => {        
        if (mediaType == MEDIA_TYPE.ANI) {
            // 애니메이션 검색 API 호출
            return (await searchContentApi.searchAni({ query: keyword, page: pageParam }, {})).data.results
        } else if (mediaType == MEDIA_TYPE.DRAMA) {
            // 드라마 검색 API 호출
            return (await searchContentApi.searchDrama({ query: keyword, page: pageParam }, {})).data.results
        } else if (mediaType == MEDIA_TYPE.MOVIE) {
            // 영화 검색 API 호출
            return (await searchContentApi.searchMovie({ query: keyword, page: pageParam }, {})).data.results
        } else if (mediaType == MEDIA_TYPE.COMICS) {
            // 만화 검색 API 호출
            return (await searchContentApi.searchComics({ query: keyword, page: pageParam, isMainPage: false }, {})).data.comicsResults
        } else {
            return null;
        }
    }

    // useInfiniteQuery 정의
    const {
        data, // 서버에 요청해서 받아온 데이터
        fetchNextPage, // 다음페이지 호출
        isFetchingNextPage, // 다음페이지 로딩중 여부
        hasNextPage, // 가져올 다음페이지가 있는지 여부를 나타냄(boolean). getNextPageParam옵션을 통해 확인가능
        isSuccess, // useInfiniteQuery의 실행 성공여부
    } = useInfiniteQuery<
        SearchContentCommonResultListType,  // queryFn이 반환하는 원본 데이터
        AxiosError, // 에러 타입 (보통 AxiosError)
        UseInfiniteQueryResultType, // 반환할 최종 데이터 형태 (select로 가공한 경우)
        [string, string, string], // query key의 타입 (예: [string, string] -> [루트 키, 서브 키])
        number | undefined // pageParam 타입 (보통 number | undefined)
    >({
        // useInfiniteQuery의 키 지정
        queryKey: searchContentQueryKeys.searchContentMore.searchMore(keyword, mediaType) as [string, string, string],
        // 쿼리가 데이터를 요청하는 데 사용할 함수/API 지정
        queryFn: async ({ pageParam = 1 }) => {
            console.log("queryFn★★★★★");
            const responseDataResults = await judgeExecApi(pageParam);
            return responseDataResults ?? []; // 제네릭 1번째 인자가 배열이므로 반드시 배열 반환
        },
        // lastPageData : 가장 최근에 불러온 캐싱된 데이터 / allPages: 지금까지 불러온 데이터
        // 새 데이터를 받아올 때 마지막페이지와 전체페이지 배열을 함께 받아옴
        // 더 불러올 데이터가 있는지 여부를 결정하는데 사용
        // 반환값이 다음 API호출할때의 pageParam으로 들어감
        getNextPageParam: (lastPageData, allPages) => {
            return lastPageData.length === 0 ? undefined : allPages.length + 1;
        },
        select: data => ({
            pages: data.pages,
            pageParams: data.pageParams,
        }),
        initialPageParam: 1, // 초기 페이지 매개변수를 지정
        enabled: !!keyword && !!mediaType, // useInfiniteQuery가 실행되는 조건 지정
    })

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

    /**
     * 모달 닫을 시 처리
     */
    const handleModalClose = () => {
        // 리다이렉트 주소 삭제
        sessionStorage.removeItem("redirectUrl");
        // URL 쿼리스트링 제거
        searchParams.delete("viewMore");
        setSearchParams(searchParams); //  URL이 바뀌면 React Router가 감지해서 리렌더링 발생
    }

    // ================================================================================================== useEffect

    /**
     * 무한 스크롤 기능을 구현하기 위한 useEffect
     * observeTarget가 null이 아니고, hasNextPage가 true이며, 
     * isFetchingNextPage가 false일 때만 IntersectionObserver를 설정
     * observeTarget이 화면에 나타나면 observerCallback이 호출되어 fetchNextPage를 호출하여 무한 스크롤 기능을 구현
     * 실행조건: observeTarget, hasNextPage, isFetchingNextPage, observerCallback이 변경될 때
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

    /**
     * 이벤트 리스너를 설정하는 useEffect
     * 배경 스크롤을 막고, ESC 키를 눌렀을 때 모달을 닫는 이벤트 리스너를 등록
     * 이벤트 리스너는 컴포넌트가 언마운트될 때 제거
     * 실행조건: 컴포넌트 마운트/언마운트 시
     */
    useEffect(() => {
        // ESC키 눌렀을 시 모달 종료
        const handleOnEscKey = (e: KeyboardEvent) => {
            e.key === ESC_KEY && handleModalClose();
        }
        // 배경 스크롤 막기
        document.body.style.overflow = "hidden";
        // ESC 키다운 이벤트리스너 등록
        document.addEventListener("keydown", handleOnEscKey);

        return (() => {
            // 배경 스크롤 복원
            document.body.style.removeProperty("overflow");
            // ESC 키다운 이벤트리스너 제거
            document.removeEventListener("keydown", handleOnEscKey);
        });
    }, []);

    // ================================================================================================== return

    return ({
        setObserveTarget: setObserveTarget,
        data: data,
        hasNextPage: hasNextPage,
        isFetchingNextPage: isFetchingNextPage,
        handleModalClose: handleModalClose,
    })

}