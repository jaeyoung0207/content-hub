import { ERROR_MESSAGE, MEDIA_TYPE, VIEW_MORE_TYPE } from "../constants/constants";
import { isAxiosError } from "axios";
import { toast } from "react-toastify";
import i18n from "i18next";
import { useProviderStore, useUserStore } from "../store/globalStateStore";
import { SearchContentCommonResultType } from "@/components/features/searchContent/useSearchContent";
import { DetailComicsResponseDto, DetailMovieResponseDto, DetailTvResponseDto, SearchContentComicsMediaResultDto, TmdbRecommendationsMovieResultsDto, TmdbRecommendationsTvResultsDto, TmdbSearchMovieResultsDto, TmdbSearchTvResultsDto } from "@/api/data-contracts";
import { RecommendContentResultType } from "@/components/features/detail/tabs/recommendContent/useRecommendContent";
import { DetailResponseType } from "@/components/features/detail/useDetail";
import { NavigateFunction } from "react-router-dom";

/**
 * 검색 URL 쿼리 생성 함수 타입
 */
type SearchUrlQueryPropsType = {
    keyword: string, // 검색어
    isAdult: string, // 성인물 포함 여부
    viewMore?: string, // 전체보기 여부
    mediaType?: string, // 미디어 타입
    originalMediaType?: string, // 원본 미디어 타입
    contentId?: string, // 콘텐츠 ID
    tabNo?: string, // 탭 번호
}

/**
 * 검색 URL 쿼리 생성 함수
 * @param keyword 검색어
 * @param isAdult 성인물 포함 여부
 * @returns 검색 URL 쿼리 문자열
 */
export const searchUrlQuery = ({ keyword, isAdult }: SearchUrlQueryPropsType) => {
    return `/search?keyword=${keyword}&isAdult=${isAdult}`;
}

/**
 * 상세 화면 URL 쿼리 생성 함수
 * @param keyword 검색어
 * @param isAdult 성인물 포함 여부
 * @param originalMediaType 원본 미디어 타입
 * @param contentId 콘텐츠 ID
 * @param tabNo 탭 번호
 * @returns 상세화면 URL 쿼리 문자열
 */
export const detailUrlQuery = ({ keyword, isAdult, originalMediaType, contentId, tabNo }: SearchUrlQueryPropsType) => {
    return `/search?keyword=${keyword}&isAdult=${isAdult}&originalMediaType=${originalMediaType}&contentId=${contentId}&tabNo=${tabNo}`;
}

/**
 * 전체보기를 통해 상세 화면으로 이동하는 URL 쿼리 생성 함수
 * @param keyword 검색어
 * @param isAdult 성인물 포함 여부
 * @param mediaType 미디어 타입
 * @param originalMediaType 원본 미디어 타입
 * @param contentId 콘텐츠 ID
 * @param tabNo 탭 번호
 * @returns 전체보기를 통한 상세화면 URL 쿼리 문자열
 */
export const detailInViewMoreUrlQuery = ({ keyword, isAdult, mediaType, originalMediaType, contentId, tabNo }: SearchUrlQueryPropsType) => {
    return `/search?keyword=${keyword}&isAdult=${isAdult}&viewMore=${VIEW_MORE_TYPE(mediaType!)}&mediaType=${mediaType}&originalMediaType=${originalMediaType}&contentId=${contentId}&tabNo=${tabNo}`;
}

/**
 * 전체보기 URL 쿼리 생성 함수
 * @param keyword 검색어
 * @param isAdult 성인물 포함 여부
 * @param mediaType 미디어 타입
 * @returns 전체보기 URL 쿼리 문자열
 */
export const viewMoreUrlQuery = ({ keyword, isAdult, mediaType }: SearchUrlQueryPropsType) => {
    return `/search?keyword=${keyword}&isAdult=${isAdult}&viewMore=${VIEW_MORE_TYPE(mediaType!)}&mediaType=${mediaType}`;
}

/**
 * 로그인 후 리다이렉트 처리
 */
export const afterLoginRedirect = (navigate: NavigateFunction) => {
    // 리다이렉트 URL이 있다면 해당 URL로 이동
    const redirectUrl = sessionStorage.getItem("redirectUrl");
    if (redirectUrl) {
        // 상세 화면으로 리다이렉트하는 경우, tabNo가 0인 경우 1로 변경
        if (redirectUrl.includes("tabNo=0")) {
            navigate(redirectUrl.replace("tabNo=0", "tabNo=1"));
        }
        // 그 이외의 경우 
        else {
            navigate(redirectUrl);
        }
        // 리다이렉트 URL 삭제
        sessionStorage.removeItem("redirectUrl");
    } else {
        // 리다이렉트 URL이 없다면 홈으로 이동
        navigate("/");
    }
}

/**
 * 콘솔 출력시 글자색을 붉은색으로 출력
 * @param error 에러 객체
 * @param message 에러 메시지
 */
export const handleUnExceptedError = (error: unknown, message?: string) => {
    // axios에러가 아닌경우에만 에러 표시
    if (!isAxiosError(error)) {
        changConsoleColor("[ERROR]: " + error); // 콘솔 출력시 글자색을 붉은색으로 출력
        toast.error(message ?? i18n.t("error.problemsOccurred"));
    }
}

/**
 * 이벤트 처리 공통 에러 핸들러
 * @param fn 이벤트 처리 함수
 */
export const commonErrorHandler = <T extends any[]>(fn: (...args: T) => void) => (...args: T) => {
    try {
        fn(...args);
    } catch (err) {
        handleUnExceptedError(err, "이벤트 처리 중 문제가 발생했습니다.");
    }
};

/**
 * 콘솔 출력시 글자색을 붉은색으로 출력
 * @param text 콘솔 출력 텍스트
 */
export const changConsoleColor = (text: string) => {
    console.error("%c" + text, "color:red");
}

/**
 * 에러 메세지 포맷팅
 * @param errorName 에러 이름
 * @param message 에러 메세지
 * @returns 포맷팅된 에러 메세지
 */
export const formattingErrorMessage = (errorName: string, message: string): string => {
    return `[${errorName}]: ${message}`;
}

/**
 * 에러 메시지 가져오기 함수
 * @param errorName 에러 이름
 * @returns 에러 메시지
 */
export const getErrorMessage = (errorName: string): string => {
    switch (errorName) {
        case ERROR_MESSAGE.NETWORK_ERROR.name:
            return ERROR_MESSAGE.NETWORK_ERROR.message;
        case ERROR_MESSAGE.AUTHORIZATION_ERROR.name:
            return ERROR_MESSAGE.AUTHORIZATION_ERROR.message;
        case ERROR_MESSAGE.VALIDATION_ERROR.name:
            return ERROR_MESSAGE.VALIDATION_ERROR.message;
        case ERROR_MESSAGE.API_RESPONSE_ERROR.name:
            return ERROR_MESSAGE.API_RESPONSE_ERROR.message;
        case ERROR_MESSAGE.BUSINESS_ERROR.name:
            return ERROR_MESSAGE.BUSINESS_ERROR.message;
        case ERROR_MESSAGE.SERVER_ERROR.name:
            return ERROR_MESSAGE.SERVER_ERROR.message;
        case ERROR_MESSAGE.BAD_REQUEST_ERROR.name:
            return ERROR_MESSAGE.BAD_REQUEST_ERROR.message;
        case ERROR_MESSAGE.SYSTEM_ERROR.name:
            return ERROR_MESSAGE.SYSTEM_ERROR.message;
        case ERROR_MESSAGE.UNEXPECTED_ERROR.name:
            return ERROR_MESSAGE.UNEXPECTED_ERROR.message;
        default:
            return i18n.t("error.unexpectedError");
    }
}

/**
 * 유저정보, provider정보, 세션스토리지 클리어
 */
export const clearUserData = () => {
    // 유저정보
    const { clearUser } = useUserStore.getState();
    // provider정보
    const { clearProvider } = useProviderStore.getState();
    // 유저정보 클리어
    clearUser();
    // provider정보 클리어
    clearProvider();
    // sessionStorage클리어
    sessionStorage.removeItem("accessToken");
    sessionStorage.removeItem("jwt");
    sessionStorage.removeItem("expireDate");
}

/**
 * 검색 결과의 타입이 TV 타입인지 확인하는 함수
 * @param results 검색 결과
 * @param mediaType 미디어 타입
 * @returns TV 타입 여부
 */
export const isSearchTvType = (results: SearchContentCommonResultType, mediaType: string): results is TmdbSearchTvResultsDto => {
    return mediaType === MEDIA_TYPE.ANI || mediaType === MEDIA_TYPE.DRAMA;
}

/**
 * 검색 결과의 타입이 MOVIE 타입인지 확인하는 함수
 * @param results 검색 결과
 * @param mediaType 미디어 타입
 * @returns MOVIE 타입 여부
 */
export const isSearchMovieType = (results: SearchContentCommonResultType, mediaType: string): results is TmdbSearchMovieResultsDto => {
    return mediaType === MEDIA_TYPE.MOVIE;
}

/**
 * 검색 결과의 타입이 COMICS 타입인지 확인하는 함수
 * @param results 검색 결과
 * @param mediaType 미디어 타입
 * @returns COMICS 타입 여부
 */
export const isSearchComicsType = (results: SearchContentCommonResultType, mediaType: string): results is SearchContentComicsMediaResultDto => {
    return mediaType === MEDIA_TYPE.COMICS;
}

/**
 * 추천 콘텐츠 결과의 타입이 TV 타입인지 확인하는 함수
 * @param results 추천 콘텐츠 결과
 * @param originalMediaType 원본 미디어 타입
 * @returns TV 타입 여부
 */
export const isRecommendationsTvType = (results: RecommendContentResultType, originalMediaType: string): results is TmdbRecommendationsTvResultsDto => {
    return originalMediaType === MEDIA_TYPE.ANI || originalMediaType === MEDIA_TYPE.DRAMA;
}

/**
 * 추천 콘텐츠 결과의 타입이 MOVIE 타입인지 확인하는 함수
 * @param results 추천 콘텐츠 결과
 * @param originalMediaType 원본 미디어 타입
 * @returns MOVIE 타입 여부
 */
export const isRecommendationsMovieType = (results: RecommendContentResultType, originalMediaType: string): results is TmdbRecommendationsMovieResultsDto => {
    return originalMediaType === MEDIA_TYPE.MOVIE;
}

/**
 * 추천 콘텐츠 결과의 타입이 COMICS 타입인지 확인하는 함수
 * @param results 추천 콘텐츠 결과
 * @param originalMediaType 원본 미디어 타입
 * @returns COMICS 타입 여부
 */
export const isRecommendationsComicsType = (results: RecommendContentResultType, originalMediaType: string): results is DetailComicsResponseDto => {
    return originalMediaType === MEDIA_TYPE.COMICS;
}

/**
 * 상세 정보 결과의 타입이 TV 타입인지 확인하는 함수
 * @param detailResult 상세 정보 결과
 * @param originalMediaType 원본 미디어 타입
 * @returns TV 타입 여부
 */
export const isDetailTvType = (detailResult: DetailResponseType, originalMediaType: string): detailResult is DetailTvResponseDto => {
    return originalMediaType === MEDIA_TYPE.ANI || originalMediaType === MEDIA_TYPE.DRAMA;
}

/**
 * 상세 정보 결과의 타입이 MOVIE 타입인지 확인하는 함수
 * @param detailResult 상세 정보 결과
 * @param originalMediaType 원본 미디어 타입
 * @returns MOVIE 타입 여부
 */
export const isDetailMovieType = (detailResult: DetailResponseType, originalMediaType: string): detailResult is DetailMovieResponseDto => {
    return originalMediaType === MEDIA_TYPE.MOVIE;
}

/**
 * 상세 정보 결과의 타입이 COMICS 타입인지 확인하는 함수
 * @param detailResult 상세 정보 결과
 * @param originalMediaType 원본 미디어 타입
 * @returns COMICS 타입 여부
 */
export const isDetailComicsType = (detailResult: DetailResponseType, originalMediaType: string): detailResult is DetailComicsResponseDto => {
    return originalMediaType === MEDIA_TYPE.COMICS;
}
