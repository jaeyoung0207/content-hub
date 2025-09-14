// 상세 화면 메인 쿼리 키
export const detailMainKey = 'detail';

/**
 * 상세 화면의 검색 결과를 가져오기 위한 쿼리 키를 정의하고 관리
 * 각 쿼리 키는 React Query에서 데이터를 캐싱하고 관리하는 데 사용됨
 */
export const detailQueryKeys = {
  detail: {
    getDetail: (originalMediaType: string, apiId: string, userId?: number) =>
      [detailMainKey, originalMediaType, apiId, userId] as const,
    getStarRatingAverage: (originalMediaType: string, apiId: string) =>
      [
        detailMainKey,
        'getStarRatingAverage',
        originalMediaType,
        apiId,
      ] as const,
    contentInformation: {
      characterList: (originalMediaType: string, apiId: string) =>
        [
          detailMainKey,
          'getCharacterInformation',
          originalMediaType,
          apiId,
        ] as const,
      staffList: (originalMediaType: string, apiId: string) =>
        [
          detailMainKey,
          'getStaffInformation',
          originalMediaType,
          apiId,
        ] as const,
    },
    contentComment: {
      list: (originalMediaType: string, apiId: string) =>
        [detailMainKey, 'getCommentList', originalMediaType, apiId] as const,
      save: (originalMediaType: string, apiId: string) =>
        [detailMainKey, 'saveComment', originalMediaType, apiId] as const,
      update: (originalMediaType: string, apiId: string) =>
        [detailMainKey, 'updateComment', originalMediaType, apiId] as const,
      delete: (originalMediaType: string, apiId: string) =>
        [detailMainKey, 'deleteComment', originalMediaType, apiId] as const,
    },
    recommendationContent: {
      list: (originalMediaType: string, apiId: string) =>
        [detailMainKey, 'getRecommendation', originalMediaType, apiId] as const,
    },
  },
};
