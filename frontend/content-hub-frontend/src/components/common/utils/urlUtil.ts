/**
 * 검색 URL 쿼리 생성 함수 타입
 */
type SearchUrlQueryPropsType = {
  keyword?: string; // 검색어
  isAdult?: string; // 성인물 포함 여부
  displayMediaType?: string; // 미디어 타입
  contentMediaType?: string; // 컨텐츠 미디어 타입
  apiId?: string; // API ID
  tabNo?: number; // 탭 번호
  personId?: number; // 인물 ID
  creditsId?: number; // 캐릭터 ID
  comicsCreditsType?: string; // 만화 크레딧 타입
  userId?: number; // 유저 ID
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
 * @param displayMediaType 화면 표시용 미디어 타입
 * @returns 전체보기 URL 쿼리 문자열
 */
export const viewMoreUrlQuery = ({
  keyword,
  isAdult,
  displayMediaType,
}: SearchUrlQueryPropsType) => {
  return `/search?keyword=${keyword}&isAdult=${isAdult}&viewMore=${displayMediaType}`;
};

/**
 * 상세 화면 URL 쿼리 생성 함수
 * @param contentMediaType 컨텐츠 미디어 타입
 * @param apiId API ID
 * @param tabNo 탭 번호
 * @returns 상세화면 URL 쿼리 문자열
 */
export const detailUrlQuery = ({
  contentMediaType,
  apiId,
  tabNo,
}: SearchUrlQueryPropsType) => {
  return `/detail/${contentMediaType}/${apiId}?tabNo=${tabNo}`;
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

/**
 * 위시리스트 화면 URL 쿼리 생성 함수
 * @param userId 유저 ID
 * @returns 위시리스트 화면 URL 쿼리 문자열
 */
export const wishlistUrlQuery = ({ userId }: SearchUrlQueryPropsType) => {
  return `/wishlist/${userId}`;
};

/**
 * 마이 코멘트 화면 URL 쿼리 생성 함수
 * @param userId 유저 ID
 * @returns 마이 코멘트 화면 URL 쿼리 문자열
 */
export const myCommentsUrlQuery = ({ userId }: SearchUrlQueryPropsType) => {
  return `/my/comments/${userId}`;
};
