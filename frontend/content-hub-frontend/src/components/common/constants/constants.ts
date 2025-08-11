/**
 * 공통 상수 정의
 */

// 이미지 없음 썸네일 파일
import NoImageThumbnailFile from "@assets/images/no_image_available_thumbnail.png";
import i18n from "@/i18n";

// 모바일 사이즈 판단
export const IS_MOBILE = window.innerWidth < 768;

// TMDB API 이미지 불러오기용 URL ("https://image.tmdb.org/t/p/<이미지 사이즈>/<이미지 파일명>" 형식으로 사용)
// 이미지 사이즈 예시 : w300, w500, original 등
export const TMDB_API_IMAGE_DOMAIN = "https://image.tmdb.org/t/p/";

// width 185 이미지 사이즈
export const WIDTH_185 = "w185";

// width 300 이미지 사이즈
export const WIDTH_300 = "w300";

// width 500 이미지 사이즈
export const WIDTH_500 = "w500";

// width 500 이미지 사이즈
export const WIDTH_780 = "w780";

// width original 이미지 사이즈
export const WIDTH_ORIGINAL = "original";

// 엔터 키
export const ENTER_KEY = "Enter";

// ESC 키
export const ESC_KEY = "Escape";

// 아래 화살표 키
export const ARROW_DOWN_KEY = "ArrowDown";

// 위 화살표 키
export const ARROW_UP_KEY = "ArrowUp";

// 미디어 타입
export enum MEDIA_TYPE {
    "ANI" = "1",
    "DRAMA" = "2",
    "MOVIE" = "3",
    "COMICS" = "4",
    "NOVEL" = "5",
}

// /**
//  * 미디어 타입에 따라 전체보기 타입 문자열을 반환
//  * @param mediaType 미디어 타입
//  * @returns 전체보기 타입 문자열
//  */
// export const VIEW_MORE_TYPE = (mediaType: string) => {
//     if (MEDIA_TYPE.ANI === mediaType) {
//         return "ani";
//     } else if (MEDIA_TYPE.DRAMA === mediaType) {
//         return "drama";
//     } else if (MEDIA_TYPE.MOVIE === mediaType) {
//         return "movie";
//     } else if (MEDIA_TYPE.COMICS === mediaType) {
//         return "comics";
//     } else if (MEDIA_TYPE.NOVEL === mediaType) {
//         return "novel";
//     } else {
//         return null;
//     }
// }

// 로그인 제공자
export enum LOGIN_PROVIDER {
    "NAVER" = "NAVER",
    "KAKAO" = "KAKAO",
    "GOOGLE" = "GOOGLE",
}

// 공통 이미지
export const COMMON_IMAGES = {
    // No Image Thumbnail 파일
    NO_IMAGE : NoImageThumbnailFile,
}

export const ERROR_CODE = {
    // API 응답 에러 코드
    BAD_REQUEST : {
        name: "400",
        message: "Bad Request",
    },
    UNAUTHORIZED : {
        name: "401",
        message: "Unauthorized",
    },
    FORBIDDEN : {
        name: "403",
        message: "Forbidden",
    },
    NOT_FOUND : {
        name: "404",
        message: "Not Found",
    },
    INTERNAL_SERVER_ERROR : {
        name: "500",
        message: "Internal Server Error",
    },
    SERVICE_UNAVAILABLE : {
        name: "503",
        message: "Service Unavailable",
    },
}

/**
 * 에러 메세지 정의
 */
export const ERROR_MESSAGE = {
    // API 응답 에러 메시지
    // 네트워크 에러
    NETWORK_ERROR : {
        name: "Network Error",
        message: i18n.t("error.networkError"),
    },
    // 인증/권한 에러
    AUTHORIZATION_ERROR : {
        name: "Authentication/Authorization Error",
        message: i18n.t("error.authError"),
    },
    // 유효성 검사 에러
    VALIDATION_ERROR : {
        name: "Validation Error",
        message: i18n.t("error.validationError"),
    },
    // API 응답 에러
    API_RESPONSE_ERROR : {
        name: "API Response Error",
        message: i18n.t("error.apiResponseError"),
    },
    // 비즈니스 에러
    BUSINESS_ERROR : {
        name: "Business Error",
        message: i18n.t("error.businessError"),
    },
    // 서버 에러
    SERVER_ERROR : {
        name: "Server Error",
        message: i18n.t("error.serverError"),
    },
    // 예기치 못한 에러
    UNEXPECTED_ERROR : {
        name: "Unexpected Error",
        message: i18n.t("error.unexpectedError"),
    },
    BAD_REQUEST_ERROR : {
        name: "Bad Request Error",
        message: i18n.t("error.badRequestError"),
    },
    // 시스템 에러
    SYSTEM_ERROR : {
        name: "System Error",
        message: i18n.t("error.systemError"),
    },
}
