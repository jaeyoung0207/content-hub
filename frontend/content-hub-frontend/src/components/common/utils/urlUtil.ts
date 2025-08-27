/**
 * 검색 URL 쿼리 생성 함수 타입
 */
type SearchUrlQueryPropsType = {
  keyword?: string; // 검색어
  isAdult?: string; // 성인물 포함 여부
  // viewMore?: string, // 전체보기 여부
  mediaType?: string; // 미디어 타입
  originalMediaType?: string; // 원본 미디어 타입
  contentId?: string; // 콘텐츠 ID
  tabNo?: number; // 탭 번호
  personId?: number; // 인물 ID
  creditsId?: number; // 캐릭터 ID
  comicsCreditsType?: string; // 만화 크레딧 타입
};

/**
 * 검색 URL 쿼리 생성 함수
 * @param keyword 검색어
 * @param isAdult 성인물 포함 여부
 * @returns 검색 URL 쿼리 문자열
 */
export const searchUrlQuery = ({
  keyword,
  isAdult,
}: SearchUrlQueryPropsType) => {
  return `/search?keyword=${keyword}&isAdult=${isAdult}`;
};

/**
 * 전체보기 URL 쿼리 생성 함수
 * @param keyword 검색어
 * @param isAdult 성인물 포함 여부
 * @param mediaType 미디어 타입
 * @returns 전체보기 URL 쿼리 문자열
 */
export const viewMoreUrlQuery = ({
  keyword,
  isAdult,
  mediaType,
}: SearchUrlQueryPropsType) => {
  return `/search?keyword=${keyword}&isAdult=${isAdult}&viewMore=${mediaType}`;
};

/**
 * 상세 화면 URL 쿼리 생성 함수
 * @param originalMediaType 원본 미디어 타입
 * @param contentId 콘텐츠 ID
 * @param tabNo 탭 번호
 * @returns 상세화면 URL 쿼리 문자열
 */
export const detailUrlQuery = ({
  originalMediaType,
  contentId,
  tabNo,
}: SearchUrlQueryPropsType) => {
  return `/detail/${originalMediaType}/${contentId}?tabNo=${tabNo}`;
};

/**
 * 인물 화면 URL 쿼리 생성 함수
 * @param personId 인물 ID
 * @returns 인물 화면 URL 쿼리 문자열
 */
export const personUrlQuery = ({ personId }: SearchUrlQueryPropsType) => {
  return `/person/${personId}`;
};

/**
 * 캐릭터 화면 URL 쿼리 생성 함수
 * @param comicsCreditsType 만화 크레딧 타입
 * @param characterId 캐릭터 ID
 * @returns 캐릭터 화면 URL 쿼리 문자열
 */
export const characterUrlQuery = ({
  comicsCreditsType,
  creditsId,
}: SearchUrlQueryPropsType) => {
  return `/character/${comicsCreditsType}/${creditsId}`;
};
