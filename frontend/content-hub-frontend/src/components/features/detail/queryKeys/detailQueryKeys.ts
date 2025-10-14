// 상세 화면 메인 쿼리 키
export const detailMainKey = 'detail';

/**
 * 상세 화면의 검색 결과를 가져오기 위한 쿼리 키를 정의하고 관리
 * 각 쿼리 키는 React Query에서 데이터를 캐싱하고 관리하는 데 사용됨
 */
export const detailQueryKeys = {
  all: [detailMainKey] as const,
  detail: {
    getDetail: (contentMediaType: string, apiId: string, userId?: number) =>
      [detailMainKey, contentMediaType, apiId, userId] as const,
    getStarRatingAverage: (contentMediaType: string, apiId: string) =>
      [detailMainKey, 'getStarRatingAverage', contentMediaType, apiId] as const,
    contentInformation: {
      characterList: (contentMediaType: string, apiId: string) =>
        [
          detailMainKey,
          'getCharacterInformation',
          contentMediaType,
          apiId,
        ] as const,
      staffList: (contentMediaType: string, apiId: string) =>
        [
          detailMainKey,
          'getStaffInformation',
          contentMediaType,
          apiId,
        ] as const,
    },
    contentComment: {
      list: (contentMediaType: string, apiId: string) =>
        [detailMainKey, 'getCommentList', contentMediaType, apiId] as const,
      save: (contentMediaType: string, apiId: string) =>
        [detailMainKey, 'saveComment', contentMediaType, apiId] as const,
      update: (contentMediaType: string, apiId: string) =>
        [detailMainKey, 'updateComment', contentMediaType, apiId] as const,
      delete: (contentMediaType: string, apiId: string) =>
        [detailMainKey, 'deleteComment', contentMediaType, apiId] as const,
    },
    recommendationContent: {
      list: (contentMediaType: string, apiId: string) =>
        [detailMainKey, 'getRecommendation', contentMediaType, apiId] as const,
    },
  },
};
