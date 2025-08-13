
/**
 * 검색 화면의 검색 결과를 가져오기 위한 쿼리 키를 정의하고 관리
 * 각 쿼리 키는 React Query에서 데이터를 캐싱하고 관리하는 데 사용됨
 */
export const searchQueryKeys = {
    // 검색 화면에서 검색 결과를 가져오기 위한 쿼리 키
    search: {
        search: (keyword: string, isAdult: string) => ["search", keyword, isAdult] as const,
    },
    // 전체보기 화면의 검색 결과를 가져오기 위한 쿼리 키
    searchMore: {
        searchMore: (keyword: string, mediaType: string) => ["searchMore", keyword, mediaType] as const,
    },

}