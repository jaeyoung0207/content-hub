import { MEDIA_TYPE } from '../constants/constants';
import { SearchCommonResultType } from '@/components/features/search/useSearch';
import {
  AniListCharactersNodeDto,
  AniListStaffNodeDto,
  DetailComicsResponseDto,
  DetailMovieResponseDto,
  DetailTvResponseDto,
  PersonCreditsCastDto,
  PersonCreditsCrewDto,
  SearchComicsMediaResultDto,
  TmdbRecommendationsMovieResultsDto,
  TmdbRecommendationsTvResultsDto,
  TmdbSearchMovieResultsDto,
  TmdbSearchTvResultsDto,
  TmdbVideoCreditsCastDto,
  TmdbVideoCreditsCrewDto,
} from '@/api/data-contracts';
import { RecommendationContentResultType } from '@/components/features/detail/tabs/recommendationContent/useRecommendationContent';
import { DetailResponseType } from '@/components/features/detail/useDetail';
import { PersonCredits } from '@/components/features/person/Person';

/**
 * 검색 결과의 타입이 TV 타입인지 확인하는 함수
 * @param results 검색 결과
 * @param mediaType 미디어 타입
 * @returns TV 타입 여부
 */
export const isSearchTvType = (
  results: SearchCommonResultType,
  mediaType: string
): results is TmdbSearchTvResultsDto => {
  return (
    results && (mediaType === MEDIA_TYPE.ANI || mediaType === MEDIA_TYPE.DRAMA)
  );
};

/**
 * 검색 결과의 타입이 MOVIE 타입인지 확인하는 함수
 * @param results 검색 결과
 * @param mediaType 미디어 타입
 * @returns MOVIE 타입 여부
 */
export const isSearchMovieType = (
  results: SearchCommonResultType,
  mediaType: string
): results is TmdbSearchMovieResultsDto => {
  return results && mediaType === MEDIA_TYPE.MOVIE;
};

/**
 * 검색 결과의 타입이 COMICS 타입인지 확인하는 함수
 * @param results 검색 결과
 * @param mediaType 미디어 타입
 * @returns COMICS 타입 여부
 */
export const isSearchComicsType = (
  results: SearchCommonResultType,
  mediaType: string
): results is SearchComicsMediaResultDto => {
  return results && mediaType === MEDIA_TYPE.COMICS;
};

/**
 * 추천 콘텐츠 결과의 타입이 TV 타입인지 확인하는 함수
 * @param results 추천 콘텐츠 결과
 * @param originalMediaType 원본 미디어 타입
 * @returns TV 타입 여부
 */
export const isRecommendationsTvType = (
  results: RecommendationContentResultType,
  originalMediaType: string
): results is TmdbRecommendationsTvResultsDto => {
  return (
    results &&
    (originalMediaType === MEDIA_TYPE.ANI ||
      originalMediaType === MEDIA_TYPE.DRAMA)
  );
};

/**
 * 추천 콘텐츠 결과의 타입이 MOVIE 타입인지 확인하는 함수
 * @param results 추천 콘텐츠 결과
 * @param originalMediaType 원본 미디어 타입
 * @returns MOVIE 타입 여부
 */
export const isRecommendationsMovieType = (
  results: RecommendationContentResultType,
  originalMediaType: string
): results is TmdbRecommendationsMovieResultsDto => {
  return results && originalMediaType === MEDIA_TYPE.MOVIE;
};

/**
 * 추천 콘텐츠 결과의 타입이 COMICS 타입인지 확인하는 함수
 * @param results 추천 콘텐츠 결과
 * @param originalMediaType 원본 미디어 타입
 * @returns COMICS 타입 여부
 */
export const isRecommendationsComicsType = (
  results: RecommendationContentResultType,
  originalMediaType: string
): results is DetailComicsResponseDto => {
  return results && originalMediaType === MEDIA_TYPE.COMICS;
};

/**
 * 상세 정보 결과의 타입이 TV 타입인지 확인하는 함수
 * @param detailResult 상세 정보 결과
 * @param originalMediaType 원본 미디어 타입
 * @returns TV 타입 여부
 */
export const isDetailTvType = (
  detailResult: DetailResponseType,
  originalMediaType: string
): detailResult is DetailTvResponseDto => {
  return (
    detailResult &&
    (originalMediaType === MEDIA_TYPE.ANI ||
      originalMediaType === MEDIA_TYPE.DRAMA)
  );
};

/**
 * 상세 정보 결과의 타입이 MOVIE 타입인지 확인하는 함수
 * @param detailResult 상세 정보 결과
 * @param originalMediaType 원본 미디어 타입
 * @returns MOVIE 타입 여부
 */
export const isDetailMovieType = (
  detailResult: DetailResponseType,
  originalMediaType: string
): detailResult is DetailMovieResponseDto => {
  return detailResult && originalMediaType === MEDIA_TYPE.MOVIE;
};

/**
 * 상세 정보 결과의 타입이 COMICS 타입인지 확인하는 함수
 * @param detailResult 상세 정보 결과
 * @param originalMediaType 원본 미디어 타입
 * @returns COMICS 타입 여부
 */
export const isDetailComicsType = (
  detailResult: DetailResponseType,
  originalMediaType: string
): detailResult is DetailComicsResponseDto => {
  return detailResult && originalMediaType === MEDIA_TYPE.COMICS;
};

/**
 * 상세 화면 크레딧이 cast 타입인지 확인하는 함수
 * @param credits 크레딧
 * @returns cast 타입 여부
 */
export const isDetailCreditsCastType = (
  credits: TmdbVideoCreditsCastDto | TmdbVideoCreditsCrewDto
): credits is TmdbVideoCreditsCastDto => {
  return 'character' in credits;
};

/**
 * 상세 화면 크레딧이 crew 타입인지 확인하는 함수
 * @param credits 크레딧
 * @returns crew 타입 여부
 */
export const isDetailCreditsCrewType = (
  credits: TmdbVideoCreditsCastDto | TmdbVideoCreditsCrewDto
): credits is TmdbVideoCreditsCrewDto => {
  return 'job' in credits;
};

/**
 * 인물 화면 크레딧이 cast 타입인지 확인하는 함수
 * @param credits 크레딧
 * @returns cast 타입 여부
 */
export const isPersonCreditsCastType = (
  credits: PersonCredits
): credits is PersonCreditsCastDto => {
  return 'character' in credits;
};

/**
 * 인물 화면 크레딧이 crew 타입인지 확인하는 함수
 * @param credits 크레딧
 * @returns crew 타입 여부
 */
export const isPersonCreditsCrewType = (
  credits: PersonCredits
): credits is PersonCreditsCrewDto => {
  return 'job' in credits;
};

/**
 * 캐릭터 타입인지 확인하는 함수
 * @param credits 데이터
 * @returns 캐릭터 타입 여부
 */
export const isCharacterType = (
  credits: AniListCharactersNodeDto | AniListStaffNodeDto
): credits is AniListCharactersNodeDto => {
  return !('homeTown' in credits);
};

/**
 * 스태프 타입인지 확인하는 함수
 * @param credits 데이터
 * @returns 스태프 타입 여부
 */
export const isStaffType = (
  credits: AniListCharactersNodeDto | AniListStaffNodeDto
): credits is AniListStaffNodeDto => {
  return 'homeTown' in credits;
};
