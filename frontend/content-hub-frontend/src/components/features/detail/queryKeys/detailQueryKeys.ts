
// 상세 화면 메인 쿼리 키
export const detailMainKey = "detail";

/**
 * 상세 화면의 검색 결과를 가져오기 위한 쿼리 키를 정의하고 관리
 * 각 쿼리 키는 React Query에서 데이터를 캐싱하고 관리하는 데 사용됨
 */
export const detailQueryKeys = {
    detail: {
        getDetail: (originalMediaType: string, contentId: string) => [detailMainKey, originalMediaType, contentId] as const,
        getStarRatingAverage: (originalMediaType: string, contentId: string) => [detailMainKey, "getStarRatingAverage", originalMediaType, contentId] as const,
        contentComment: {
            list: (originalMediaType: string, contentId: string) => [detailMainKey, "getCommentList", originalMediaType, contentId] as const,
            save: (originalMediaType: string, contentId: string) => [detailMainKey, "saveComment", originalMediaType, contentId] as const,
            update: (originalMediaType: string, contentId: string) => [detailMainKey, "updateComment", originalMediaType, contentId] as const,
            delete: (originalMediaType: string, contentId: string) => [detailMainKey, "deleteComment", originalMediaType, contentId] as const,
        },
        recommendationContent: {
            list: (originalMediaType: string, contentId: string) => [detailMainKey, "getRecommendation", originalMediaType, contentId] as const,
        }
    },
}