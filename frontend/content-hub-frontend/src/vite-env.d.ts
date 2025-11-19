/// <reference types="vite/client" />

/**
 * 'true' 또는 'false' 문자열 타입
 */
type BooleanString = 'true' | 'false';

/**
 * Vite 환경변수 타입 정의
 */
interface ImportMetaEnv {
  readonly VITE_APP_BACKEND_URL: string;
  readonly VITE_BLOCKING_ADULT_CONTENT: BooleanString;
  readonly VITE_MAINTENANCE_MODE: BooleanString;
  readonly VITE_MAINTENANCE_START: string;
  readonly VITE_MAINTENANCE_END: string;
  readonly VITE_NAVER_REDIRECT_URI: string;
  readonly VITE_NAVER_CLIENT_ID: string;
  readonly VITE_KAKAO_REDIRECT_URI: string;
  readonly VITE_KAKAO_CLIENT_ID: string;
  readonly VITE_SENTRY_DSN: string;
  readonly VITE_SENTRY_ENABLE: BooleanString;
  readonly VITE_VIDEO_CREDITS_COUNT: number;
  readonly VITE_COMICS_CREDITS_COUNT: number;
  readonly VITE_CREDITS_PER_PAGE: number;
  readonly VITE_COMMENT_MAX_LENGTH: number;
  readonly VITE_TMDB_SEARCH_KEYWORD_MAX_LENGTH: number;
  readonly VITE_SAVE_KEYWORD_HISTORY_COUNT: number;
  readonly VITE_PAGE_RANGE_DISPLAYED: number;
  readonly VITE_MARGIN_PAGES_DISPLAYED: number;
  readonly VITE_COMMENT_LF_OMISSION_LENGTH: number;
  readonly VITE_COMMENT_LENGTH_OMISSION_LENGTH: number;
  readonly VITE_WISHLIST_VIDEO_OMISSION_PC_LENGTH: number;
  readonly VITE_WISHLIST_COMICS_OMISSION_PC_LENGTH: number;
  readonly VITE_WISHLIST_VIDEO_OMISSION_TABLET_LENGTH: number;
  readonly VITE_WISHLIST_COMICS_OMISSION_TABLET_LENGTH: number;
  readonly VITE_WISHLIST_VIDEO_OMISSION_MOBILE_LENGTH: number;
  readonly VITE_WISHLIST_COMICS_OMISSION_MOBILE_LENGTH: number;
}

/**
 * ImportMeta 인터페이스 확장
 */
interface ImportMeta {
  readonly env: ImportMetaEnv;
}
