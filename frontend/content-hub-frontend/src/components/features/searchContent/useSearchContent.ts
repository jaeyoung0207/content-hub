import { SearchContent } from "@api/SearchContent";
import { SearchContentComicsResponseDto, SearchContentComicsMediaResultDto, SearchContentVideoResponseDto, TmdbSearchMovieResultsDto, TmdbSearchTvResultsDto } from "@api/data-contracts";
import { useQuery } from "@tanstack/react-query";
import { searchContentQueryKeys } from "./queryKeys/searchContentQueryKeys";

// 공통 검색 결과 타입
export type SearchContentCommonResultType = TmdbSearchTvResultsDto | TmdbSearchMovieResultsDto | SearchContentComicsMediaResultDto;
// 공통 검색 결과 리스트 타입
export type SearchContentCommonResultListType = TmdbSearchTvResultsDto[] | TmdbSearchMovieResultsDto[] | SearchContentComicsMediaResultDto[];

/**
 * 검색 콘텐츠 훅 반환 타입
 */
type UseSearchContentReturnType = {
    isLoading?: boolean, // 로딩 중 여부
    data?: {
        videoResult: SearchContentVideoResponseDto | undefined; // 비디오 검색 결과
        comicsResult: SearchContentComicsResponseDto | undefined; // 만화 검색 결과
    },
}

/**
 * 검색 콘텐츠 컴포넌트에서 사용하는 훅
 * @param keyword 검색어
 * @param isAdult 성인물 포함 여부
 */
export const useSearchContent = (keyword: string, isAdult: string): UseSearchContentReturnType => {
    
    // ================================================================================================== react query

    // 검색 콘텐츠 API 인스턴스 생성
    const searchContentApi = new SearchContent();

    /**
     * 비디오 및 만화 검색 결과를 가져오는 API 호출 함수
     * @returns 검색 결과 데이터
     */
    const getSearchResult = async () => {
        const [videoResult, comicsResult] = await Promise.allSettled([
            searchVideoApi(),
            searchComicsApi(),
        ]);
        return {
            videoResult: videoResult.status === "fulfilled" ? videoResult.value : undefined,
            comicsResult: comicsResult.status === "fulfilled" ? comicsResult.value : undefined,
        };
    }

    /**
     * 비디오(애니/드라마/영화) 검색 API 호출
     * @returns 비디오 검색 결과
     */
    const searchVideoApi = async () => {
        return (await searchContentApi.searchVideo({ query: keyword }, {})).data;
    }

    /**
     * 만화 검색 API 호출
     * @returns 만화 검색 결과
     */
    const searchComicsApi = async () => {
        return (await searchContentApi.searchComics({ query: keyword, isMainPage: true }, {})).data;
    }

    /**
     * 검색 콘텐츠를 가져오기 위한 react-query 훅
     */
    const {
        data, // 검색 결과 데이터
        isLoading, // 로딩 중 여부
    } = useQuery({
        queryKey: searchContentQueryKeys.searchContent.search(keyword, isAdult),
        queryFn: async () => {
            // 비디오 및 만화 검색 결과를 가져오는 API 호출
            return await getSearchResult();
        },
        enabled: !!keyword,
    });

    // ================================================================================================== return

    return ({
        isLoading: isLoading,
        data: data,
    })

}