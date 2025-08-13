import { Search } from "@api/Search";
import { SearchComicsResponseDto, SearchComicsMediaResultDto, SearchVideoResponseDto, TmdbSearchMovieResultsDto, TmdbSearchTvResultsDto } from "@api/data-contracts";
import { useQuery } from "@tanstack/react-query";
import { searchQueryKeys } from "./queryKeys/searchQueryKeys";

// 공통 검색 결과 타입
export type SearchCommonResultType = TmdbSearchTvResultsDto | TmdbSearchMovieResultsDto | SearchComicsMediaResultDto;
// 공통 검색 결과 리스트 타입
export type SearchCommonResultListType = TmdbSearchTvResultsDto[] | TmdbSearchMovieResultsDto[] | SearchComicsMediaResultDto[];

/**
 * 검색 훅 반환 타입
 */
type UseSearchReturnType = {
    isLoading?: boolean, // 로딩 중 여부
    data?: {
        videoResult: SearchVideoResponseDto | undefined; // 비디오 검색 결과
        comicsResult: SearchComicsResponseDto | undefined; // 만화 검색 결과
    },
}

/**
 * 검색 컴포넌트에서 사용하는 훅
 * @param keyword 검색어
 * @param isAdult 성인물 포함 여부
 */
export const useSearch = (keyword: string, isAdult: string): UseSearchReturnType => {
    
    // ================================================================================================== react query

    // 검색 API 인스턴스 생성
    const searchApi = new Search();

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
        return (await searchApi.searchVideo({ query: keyword }, {})).data;
    }

    /**
     * 만화 검색 API 호출
     * @returns 만화 검색 결과
     */
    const searchComicsApi = async () => {
        return (await searchApi.searchComics({ query: keyword, isMainPage: true }, {})).data;
    }

    /**
     * 검색 결과를 가져오기 위한 react-query 훅
     */
    const {
        data, // 검색 결과 데이터
        isLoading, // 로딩 중 여부
    } = useQuery({
        queryKey: searchQueryKeys.search.search(keyword, isAdult),
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