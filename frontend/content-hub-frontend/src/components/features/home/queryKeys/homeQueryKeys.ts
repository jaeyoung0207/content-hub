const homeMainKey = 'home';

/**
 * 홈 화면의 검색 결과를 가져오기 위한 쿼리 키를 정의하고 관리
 * 각 쿼리 키는 React Query에서 데이터를 캐싱하고 관리하는 데 사용됨
 */
export const homeQueryKeys = {
  // 홈 화면의 콘텐츠 랭킹 조회 쿼리 키
  getContentRankings: [homeMainKey, 'getContentRankings'] as const,
};
