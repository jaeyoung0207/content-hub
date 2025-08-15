// 헤더 메인 쿼리 키
const headerMainKey = 'header';

/**
 * 헤더의 검색 결과를 가져오기 위한 쿼리 키를 정의하고 관리
 * 각 쿼리 키는 React Query에서 데이터를 캐싱하고 관리하는 데 사용됨
 */
export const headerQueryKeys = {
  // 헤더에서 키워드 리스트를 가져오기 위한 쿼리 키
  searchKeyword: (keyword: string) =>
    [headerMainKey, 'searchKeyword', keyword] as const,
  // 헤더에서 로그인 정보를 업데이트하기 위한 쿼리 키
  login: (provider: string) => [headerMainKey, 'login', provider] as const,
  // 헤더에서 성인물 검색 플래그를 설정하기 위한 쿼리 키
  setAdultFlg: () => [headerMainKey, 'setAdultFlg'] as const,
  // 헤더에서 성인물 검색 플래그를 해제하기 위한 쿼리 키
  clearAdultFlg: () => [headerMainKey, 'clearAdultFlg'] as const,
};
