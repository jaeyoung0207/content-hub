
// 상세 화면 메인 쿼리 키
export const detailMainKey = "detail";

/**
 * 상세화면의 검색 결과를 가져오기 위한 쿼리 키를 정의하고 관리
 * 각 쿼리 키는 React Query에서 데이터를 캐싱하고 관리하는 데 사용됨
 */
export const detailQueryKeys = {
    detail: {
        getDetail: (originalMediaType: string, contentId: string) => [detailMainKey, originalMediaType, contentId],
        getStarRatingAverage: (originalMediaType: string, contentId: string) => [detailMainKey, "getStarRatingAverage", originalMediaType, contentId],
        contentComment: {
            list: (originalMediaType: string, contentId: string) => [detailMainKey, "getCommentList", originalMediaType, contentId] as [string, string, string, string],
            save: (originalMediaType: string, contentId: string) => [detailMainKey, "saveComment", originalMediaType, contentId],
            update: (originalMediaType: string, contentId: string) => [detailMainKey, "updateComment", originalMediaType, contentId],
            delete: (originalMediaType: string, contentId: string) => [detailMainKey, "deleteComment", originalMediaType, contentId],
        },
        recommendationContent: {
            list: (originalMediaType: string, contentId: string) => [detailMainKey, "getRecommendation", originalMediaType, contentId] as [string, string, string, string],
        }
    },
}