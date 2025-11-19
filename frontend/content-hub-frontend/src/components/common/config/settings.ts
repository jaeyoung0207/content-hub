/**
 * 필수 환경변수 체크 함수
 * @param value 환경변수 값
 * @param key 환경변수 키
 * @returns 환경변수 값 또는 빈 문자열
 */
const requiredCheck = (value: string | undefined, key: string): string => {
  if (!value) {
    // 운영 환경에서는 실패하도록 처리
    if (import.meta.env.MODE === 'production') {
      throw new Error(`[ENV] Missing required key: ${key}`);
    }
    console.error(`[ENV] Missing key: ${key}, using empty fallback.`);
    return '';
  }
  return value;
};

/**
 * .env에서 설정한 환경변수를 가지고 내부에서 사용할 수 있도록 설정
 */
export const settings = {
  appBackendUrl: requiredCheck(
    import.meta.env.VITE_APP_BACKEND_URL,
    'VITE_APP_BACKEND_URL'
  ),
  isBlockingAdultContent: import.meta.env.VITE_BLOCKING_ADULT_CONTENT
    ? import.meta.env.VITE_BLOCKING_ADULT_CONTENT === 'true'
    : false,
  isMaintenanceMode: import.meta.env.VITE_MAINTENANCE_MODE
    ? import.meta.env.VITE_MAINTENANCE_MODE === 'true'
    : false,
  maintenanceStart:
    import.meta.env.VITE_MAINTENANCE_START || '2025-07-17 04:00',
  maintenanceEnd: import.meta.env.VITE_MAINTENANCE_END || '2025-07-17 10:00',
  naverRedirectUri:
    import.meta.env.VITE_NAVER_REDIRECT_URI ||
    'http://localhost:3000/login/naver',
  naverClientId:
    import.meta.env.VITE_NAVER_CLIENT_ID || 'naver_client_id_placeholder',
  kakaoRedirectUri:
    import.meta.env.VITE_KAKAO_REDIRECT_URI ||
    'http://localhost:3000/login/kakao',
  kakaoClientId:
    import.meta.env.VITE_KAKAO_CLIENT_ID || 'kakao_client_id_placeholder',
  sentryDsn: import.meta.env.VITE_SENTRY_DSN || 'sentry_dsn_placeholder',
  isSentryEnabled: import.meta.env.VITE_SENTRY_ENABLE
    ? import.meta.env.VITE_SENTRY_ENABLE === 'true'
    : false,
  detailVideoCount: Number(import.meta.env.VITE_VIDEO_CREDITS_COUNT || 6),
  detailComicsCount: Number(import.meta.env.VITE_COMICS_CREDITS_COUNT || 10),
  detailCreditsPerPage: Number(import.meta.env.VITE_CREDITS_PER_PAGE || 9),
  commentMaxLength: Number(import.meta.env.VITE_COMMENT_MAX_LENGTH || 500),
  tmdbSearchKeywordMaxLength: Number(
    import.meta.env.VITE_TMDB_SEARCH_KEYWORD_MAX_LENGTH || 500
  ),
  saveKeywordHistoryCount: Number(
    import.meta.env.VITE_SAVE_KEYWORD_HISTORY_COUNT || 10
  ),
  pageRangeDisplayed: Number(import.meta.env.VITE_PAGE_RANGE_DISPLAYED || 10),
  marginPagesDisplayed: Number(
    import.meta.env.VITE_MARGIN_PAGES_DISPLAYED || 4
  ),
  commentLfOmissionLength: Number(
    import.meta.env.VITE_COMMENT_LF_OMISSION_LENGTH || 4
  ),
  commentLengthOmissionLength: Number(
    import.meta.env.VITE_COMMENT_LENGTH_OMISSION_LENGTH || 200
  ),
  wishlistVideoOmissionPcLength: Number(
    import.meta.env.VITE_WISHLIST_VIDEO_OMISSION_PC_LENGTH || 10
  ),
  wishlistComicsOmissionPcLength: Number(
    import.meta.env.VITE_WISHLIST_COMICS_OMISSION_PC_LENGTH || 14
  ),
  wishlistVideoOmissionTabletLength: Number(
    import.meta.env.VITE_WISHLIST_VIDEO_OMISSION_TABLET_LENGTH || 8
  ),
  wishlistComicsOmissionTabletLength: Number(
    import.meta.env.VITE_WISHLIST_COMICS_OMISSION_TABLET_LENGTH || 10
  ),
  wishlistVideoOmissionMobileLength: Number(
    import.meta.env.VITE_WISHLIST_VIDEO_OMISSION_MOBILE_LENGTH || 6
  ),
  wishlistComicsOmissionMobileLength: Number(
    import.meta.env.VITE_WISHLIST_COMICS_OMISSION_MOBILE_LENGTH || 6
  ),
};
