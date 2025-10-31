/**
 * 공통 상수 정의
 */

// 이미지 없음 썸네일 파일
import NoImageThumbnailFile from '@assets/images/no_image_available.svg';
import i18n from '@/i18n';

// 모바일 화면 너비 기준
export const MOBILE_WIDTH = 768;

// 모바일 사이즈 판단
// export const IS_MOBILE = window.innerWidth < 768;

// TMDB API 이미지 불러오기용 URL ("https://image.tmdb.org/t/p/<이미지 사이즈>/<이미지 파일명>" 형식으로 사용)
// 이미지 사이즈 예시 : w300, w500, original 등
export const TMDB_API_IMAGE_DOMAIN = 'https://image.tmdb.org/t/p/';

// width 45 이미지 사이즈
export const WIDTH_45 = 'w45';

// width 185 이미지 사이즈
export const WIDTH_185 = 'w185';

// width 300 이미지 사이즈
export const WIDTH_300 = 'w300';

// width 500 이미지 사이즈
export const WIDTH_500 = 'w500';

// width 500 이미지 사이즈
export const WIDTH_780 = 'w780';

// width original 이미지 사이즈
export const WIDTH_ORIGINAL = 'original';

// 엔터 키
export const ENTER_KEY = 'Enter';

// ESC 키
export const ESC_KEY = 'Escape';

// 아래 화살표 키
export const ARROW_DOWN_KEY = 'ArrowDown';

// 위 화살표 키
export const ARROW_UP_KEY = 'ArrowUp';

// 왼쪽 화살표 키
export const ARROW_LEFT_KEY = 'ArrowLeft';

// 오른쪽 화살표 키
export const ARROW_RIGHT_KEY = 'ArrowRight';

// 홈 키
export const HOME_KEY = 'Home';

// 엔드 키
export const END_KEY = 'End';

// 구분자 슬래시
export const SEPERATE_SLASH = ' / ';

// 생략 텍스트
export const OMISSION_TEXT = '...';

// 리다이렉트 URL
export const REDIRECT_URL = 'redirectUrl';

// 1분
export const ONE_MINUTE = 1000 * 60;

// 무한스크롤 스로틀 딜레이
export const INFINITE_SCROLL_THROTTLE_DELAY = 1000;

// 툴팁 닫힘 상태
export const TOOLTIP_CLOSE_STATE = 0;

// 미디어 타입 이름(화면 표시용)
export const MEDIA_TYPE_NAME = {
  ANI: 'info.animation',
  DRAMA: 'info.drama',
  MOVIE: 'info.movie',
  DOCUMENTARY: 'info.documentary',
  KIDS: 'info.kids',
  NEWS: 'info.news',
  VARIETY: 'info.variety',
  COMICS: 'info.comics',
  PERSON: 'info.person',
};

// 검색 선택 타입
export const SELECT_TYPE = {
  MULTIPLE: '1',
  SINGLE: '2',
};

// 검색 미디어 타입
export const SEARCH_TYPE = {
  ANI: '1',
  DRAMA: '2',
  MOVIE: '3',
  DOCUMENTARY: '4',
  KIDS: '5',
  NEWS: '6',
  VARIETY: '7',
  COMICS: '8',
  PERSON: '9',
};

export const MEDIA_TYPE_KIND = {
  CONTENT_MEDIA_TYPE: '1',
  DISPLAY_MEDIA_TYPE: '2',
};

// 탭 ID 매핑
export const DETAIL_TAB_ID = {
  mediaInfo: 0,
  cast: 1,
  crew: 2,
  review: 3,
  recommendation: 4,
};

/**
 * 비디오 크레딧 타입
 */
export const VIDEO_CREDITS_TYPE = {
  CAST: '1',
  CREW: '2',
};

/**
 * 만화 크레딧 타입
 */
export const COMICS_CREDITS_TYPE = {
  CHARACTER: '1',
  STAFF: '2',
};

/**
 * 검색 화면 타입
 */
export const SEARCH_SCREEN_TYPE = {
  MAIN: '1',
  VIEW_MORE: '2',
  RECOMMENDATION: '3',
};

// 로그인 제공자
export enum LOGIN_PROVIDER {
  'NAVER' = 'NAVER',
  'KAKAO' = 'KAKAO',
  'GOOGLE' = 'GOOGLE',
}

// 공통 이미지
export const COMMON_IMAGES = {
  // No Image Thumbnail 파일
  NO_IMAGE: NoImageThumbnailFile,
};

// TMDB TV 방영 상태
export const TV_RELEASE_STATUS: Record<string, string> = {
  'Returning Series': '방영 중',
  Planned: '방영 예정',
  'In Production': '제작 중',
  Ended: '방영 종료',
  Canceled: '방영 취소',
  Pilot: '파일럿',
};

// TMDB 영화 개봉 상태
export const MOVIE_RELEASE_STATUS: Record<string, string> = {
  Rumored: '루머',
  Planned: '제작 예정',
  'In Production': '제작 중',
  'Post Production': '후반 작업 중',
  Released: '개봉',
  Canceled: '제작 취소',
};

// AniList 만화 출간 상태
export const COMICS_RELEASE_STATUS: Record<string, string> = {
  FINISHED: '완결',
  RELEASING: '연재 중',
  NOT_YET_RELEASED: '미출간',
  CANCELED: '연재 중단',
  HIATUS: '휴재',
};

// API 응답 에러 코드
export const ERROR_CODE = {
  BAD_REQUEST: {
    status: 400,
    message: 'Bad Request',
  },
  UNAUTHORIZED: {
    status: 401,
    message: 'Unauthorized',
  },
  FORBIDDEN: {
    status: 403,
    message: 'Forbidden',
  },
  NOT_FOUND: {
    status: 404,
    message: 'Not Found',
  },
  TOO_MANY_REQUESTS: {
    status: 429,
    message: 'Too Many Requests',
  },
  INTERNAL_SERVER_ERROR: {
    status: 500,
    message: 'Internal Server Error',
  },
  SERVICE_UNAVAILABLE: {
    status: 503,
    message: 'Service Unavailable',
  },
  GATEWAY_TIMEOUT: {
    status: 504,
    message: 'Gateway Timeout',
  },
};

// API 응답 에러 메시지
export const ERROR_MESSAGE = {
  // API 응답 에러 메시지
  // 네트워크 에러
  NETWORK_ERROR: {
    name: 'Network Error',
    message: i18n.t('error.networkError'),
  },
  // 인증/권한 에러
  AUTHORIZATION_ERROR: {
    name: 'Authentication/Authorization Error',
    message: i18n.t('error.authError'),
  },
  // 유효성 검사 에러
  VALIDATION_ERROR: {
    name: 'Validation Error',
    message: i18n.t('error.validationError'),
  },
  // API 응답 에러
  API_RESPONSE_ERROR: {
    name: 'API Response Error',
    message: i18n.t('error.apiResponseError'),
  },
  // 비즈니스 에러
  BUSINESS_ERROR: {
    name: 'Business Error',
    message: i18n.t('error.businessError'),
  },
  // 서버 에러
  SERVER_ERROR: {
    name: 'Server Error',
    message: i18n.t('error.serverError'),
  },
  // 예기치 못한 에러
  UNEXPECTED_ERROR: {
    name: 'Unexpected Error',
    message: i18n.t('error.unexpectedError'),
  },
  // 잘못된 요청 에러
  BAD_REQUEST_ERROR: {
    name: 'Bad Request Error',
    message: i18n.t('error.badRequestError'),
  },
  // 시스템 에러
  SYSTEM_ERROR: {
    name: 'System Error',
    message: i18n.t('error.systemError'),
  },
  // 타임아웃 에러
  TIMEOUT_ERROR: {
    name: 'Timeout Error',
    message: i18n.t('error.timeoutError'),
  },
  API_RATE_LIMIT_EXCEEDED_ERROR: {
    name: 'API Rate Limit Exceeded Error',
    message: i18n.t('error.apiRateLimitExceededError'),
  },
};
