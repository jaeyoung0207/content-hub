import { SearchCommonResultType } from '@/components/features/search/Search';
import {
  AniListCharactersNodeDto,
  AniListStaffNodeDto,
  DetailComicsResponseDto,
  DetailMovieResponseDto,
  DetailTvResponseDto,
  PersonCreditsCastDto,
  PersonCreditsCrewDto,
  SearchComicsResultDto,
  DetailRecommendationsMovieResultsDto,
  DetailRecommendationsTvResultsDto,
  SearchMovieResultsDto,
  SearchTvResultsDto,
  TmdbVideoCreditsCastDto,
  TmdbVideoCreditsCrewDto,
} from '@/api/data-contracts';
import { RecommendationContentResultType } from '@/components/features/detail/tabs/recommendation/useDetailRecommendation';
import { DetailResponseType } from '@/components/features/detail/useDetail';
import { PersonCredits } from '@/components/features/person/Person';
import { getContentMediaType, getDisplayMediaType } from './convertUtil';

/**
 * 검색 결과의 타입이 TV 타입인지 확인하는 함수
 * @param results 검색 결과
 * @param displayMediaType 미디어 타입
 * @returns TV 타입 여부
 */
export const isSearchTvType = (
  results: SearchCommonResultType,
  displayMediaType: string
): results is SearchTvResultsDto => {
  return (
    results &&
    (displayMediaType === getDisplayMediaType().aniCode ||
      displayMediaType === getDisplayMediaType().dramaCode ||
      displayMediaType === getDisplayMediaType().documentaryCode ||
      displayMediaType === getDisplayMediaType().kidsCode ||
      displayMediaType === getDisplayMediaType().newsCode ||
      displayMediaType === getDisplayMediaType().varietyCode)
  );
};

/**
 * 검색 결과의 타입이 MOVIE 타입인지 확인하는 함수
 * @param results 검색 결과
 * @param displayMediaType 미디어 타입
 * @returns MOVIE 타입 여부
 */
export const isSearchMovieType = (
  results: SearchCommonResultType,
  displayMediaType: string
): results is SearchMovieResultsDto => {
  return results && displayMediaType === getDisplayMediaType().movieCode;
};

/**
 * 검색 결과의 타입이 COMICS 타입인지 확인하는 함수
 * @param results 검색 결과
 * @param displayMediaType 화면 표시용 미디어 타입
 * @returns COMICS 타입 여부
 */
export const isSearchComicsType = (
  results: SearchCommonResultType,
  displayMediaType: string
): results is SearchComicsResultDto => {
  return results && displayMediaType === getDisplayMediaType().comicsCode;
};

/**
 * 추천 콘텐츠 결과의 타입이 TV 타입인지 확인하는 함수
 * @param results 추천 콘텐츠 결과
 * @param displayMediaType 화면 표시용 미디어 타입
 * @returns TV 타입 여부
 */
export const isRecommendationsTvType = (
  results: RecommendationContentResultType,
  displayMediaType: string
): results is DetailRecommendationsTvResultsDto => { // NOSONAR
  return (
    results &&
    (displayMediaType === getDisplayMediaType().aniCode ||
      displayMediaType === getDisplayMediaType().dramaCode ||
      displayMediaType === getDisplayMediaType().documentaryCode ||
      displayMediaType === getDisplayMediaType().kidsCode ||
      displayMediaType === getDisplayMediaType().newsCode ||
      displayMediaType === getDisplayMediaType().varietyCode)
  );
};

/**
 * 추천 콘텐츠 결과의 타입이 MOVIE 타입인지 확인하는 함수
 * @param results 추천 콘텐츠 결과
 * @param displayMediaType 화면 표시용 미디어 타입
 * @returns MOVIE 타입 여부
 */
export const isRecommendationsMovieType = (
  results: RecommendationContentResultType,
  displayMediaType: string
): results is DetailRecommendationsMovieResultsDto => {
  return results && displayMediaType === getDisplayMediaType().movieCode;
};

/**
 * 추천 콘텐츠 결과의 타입이 COMICS 타입인지 확인하는 함수
 * @param results 추천 콘텐츠 결과
 * @param displayMediaType 화면 표시용 미디어 타입
 * @returns COMICS 타입 여부
 */
export const isRecommendationsComicsType = (
  results: RecommendationContentResultType,
  displayMediaType: string
): results is DetailComicsResponseDto => {
  return results && displayMediaType === getDisplayMediaType().comicsCode;
};

/**
 * 상세 정보 결과의 타입이 TV 타입인지 확인하는 함수
 * @param detailResult 상세 정보 결과
 * @param contentMediaType 컨텐츠 미디어 타입
 * @returns TV 타입 여부
 */
export const isDetailTvType = (
  detailResult: DetailResponseType,
  contentMediaType: string
): detailResult is DetailTvResponseDto => {
  return (
    detailResult &&
    (contentMediaType === getContentMediaType().aniCode ||
      contentMediaType === getContentMediaType().dramaCode ||
      contentMediaType === getContentMediaType().documentaryCode ||
      contentMediaType === getContentMediaType().kidsCode ||
      contentMediaType === getContentMediaType().newsCode ||
      contentMediaType === getContentMediaType().varietyCode)
  );
};

/**
 * 상세 정보 결과의 타입이 MOVIE 타입인지 확인하는 함수
 * @param detailResult 상세 정보 결과
 * @param contentMediaType 컨텐츠 미디어 타입
 * @returns MOVIE 타입 여부
 */
export const isDetailMovieType = (
  detailResult: DetailResponseType,
  contentMediaType: string
): detailResult is DetailMovieResponseDto => {
  return detailResult && contentMediaType === getContentMediaType().movieCode;
};

/**
 * 상세 정보 결과의 타입이 COMICS 타입인지 확인하는 함수
 * @param detailResult 상세 정보 결과
 * @param contentMediaType 컨텐츠 미디어 타입
 * @returns COMICS 타입 여부
 */
export const isDetailComicsType = (
  detailResult: DetailResponseType,
  contentMediaType: string
): detailResult is DetailComicsResponseDto => {
  return detailResult && contentMediaType === getContentMediaType().comicsCode;
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
