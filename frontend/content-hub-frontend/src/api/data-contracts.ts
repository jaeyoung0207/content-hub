/* eslint-disable */
/* tslint:disable */
// @ts-nocheck
/*
 * ---------------------------------------------------------------
 * ## THIS FILE WAS GENERATED VIA SWAGGER-TYPESCRIPT-API        ##
 * ##                                                           ##
 * ## AUTHOR: acacode                                           ##
 * ## SOURCE: https://github.com/acacode/swagger-typescript-api ##
 * ---------------------------------------------------------------
 */

export interface DetailCommentUpdateRequestDto {
  /** @format int64 */
  commentNo: number;
  originalMediaType: string;
  apiId: string;
  userId: string;
  nickname: string;
  starRating: number;
  comment: string;
  /** @format int64 */
  good?: number;
  /** @format int64 */
  bad?: number;
}

export interface DetailCommentSaveRequestDto {
  originalMediaType: string;
  apiId: string;
  provider: string;
  userId: string;
  nickname: string;
  starRating?: number;
  comment: string;
  /** @format int64 */
  good?: number;
  /** @format int64 */
  bad?: number;
}

export interface SearchVideoResponseDto {
  aniResults?: TmdbSearchTvResultsDto[];
  dramaResults?: TmdbSearchTvResultsDto[];
  movieResults?: TmdbSearchMovieResultsDto[];
  /** @format int32 */
  page?: number;
  /** @format int32 */
  totalPages?: number;
  /** @format int32 */
  totalResults?: number;
  aniViewMore?: boolean;
  dramaViewMore?: boolean;
  movieViewMore?: boolean;
}

export interface TmdbSearchMovieResultsDto {
  adult?: boolean;
  backdropPath?: string;
  genreIds?: number[];
  /** @format int32 */
  id?: number;
  originalLanguage?: string;
  originalName?: string;
  overview?: string;
  popularity?: number;
  posterPath?: string;
  voteAverage?: number;
  /** @format int32 */
  voteCount?: number;
  genreNames?: string[];
  originalMediaType?: string;
  originalTitle?: string;
  releaseDate?: string;
  title?: string;
  video?: boolean;
}

export interface TmdbSearchTvResultsDto {
  adult?: boolean;
  backdropPath?: string;
  genreIds?: number[];
  /** @format int32 */
  id?: number;
  originalLanguage?: string;
  originalName?: string;
  overview?: string;
  popularity?: number;
  posterPath?: string;
  voteAverage?: number;
  /** @format int32 */
  voteCount?: number;
  genreNames?: string[];
  originalMediaType?: string;
  originCountry?: string[];
  firstAirDate?: string;
  name?: string;
}

export interface TmdbSearchMovieDto {
  /** @format int32 */
  page?: number;
  results?: TmdbSearchMovieResultsDto[];
  /** @format int32 */
  totalPages?: number;
  /** @format int32 */
  totalResults?: number;
}

export interface TmdbSearchTvDto {
  /** @format int32 */
  page?: number;
  results?: TmdbSearchTvResultsDto[];
  /** @format int32 */
  totalPages?: number;
  /** @format int32 */
  totalResults?: number;
}

export interface SearchComicsMediaResultDto {
  adult?: boolean;
  backdropPath?: string;
  genreIds?: number[];
  /** @format int32 */
  id?: number;
  originalLanguage?: string;
  originalName?: string;
  overview?: string;
  popularity?: number;
  posterPath?: string;
  voteAverage?: number;
  /** @format int32 */
  voteCount?: number;
  genreNames?: string[];
  originalMediaType?: string;
  title?: string;
}

export interface SearchComicsResponseDto {
  comicsResults?: SearchComicsMediaResultDto[];
  /** @format int32 */
  page?: number;
  /** @format int32 */
  totalPages?: number;
  /** @format int32 */
  totalResults?: number;
  comicsViewMore?: boolean;
}

export interface PersonCreditsCastDto {
  adult?: boolean;
  backdropPath?: string;
  genreIds?: number[];
  /** @format int32 */
  id?: number;
  originalLanguage?: string;
  overview?: string;
  popularity?: number;
  posterPath?: string;
  voteAverage?: number;
  /** @format int32 */
  voteCount?: number;
  character?: string;
  creditId?: string;
  originalTitle?: string;
  releaseDate?: string;
  releaseYear?: string;
  title?: string;
  /** @format int32 */
  episodeCount?: number;
  mediaType?: string;
}

export interface PersonCreditsCrewDto {
  adult?: boolean;
  backdropPath?: string;
  genreIds?: number[];
  /** @format int32 */
  id?: number;
  originalLanguage?: string;
  overview?: string;
  popularity?: number;
  posterPath?: string;
  voteAverage?: number;
  /** @format int32 */
  voteCount?: number;
  creditId?: string;
  department?: string;
  job?: string;
  originalTitle?: string;
  releaseDate?: string;
  releaseYear?: string;
  title?: string;
  /** @format int32 */
  episodeCount?: number;
  mediaType?: string;
}

export interface PersonResponseDto {
  adult?: boolean;
  alsoKnownAs?: string[];
  biography?: string;
  birthday?: string;
  deathday?: string;
  /** @format int32 */
  gender?: number;
  homepage?: string;
  /** @format int32 */
  id?: number;
  imdbId?: string;
  knownForDepartment?: string;
  name?: string;
  placeOfBirth?: string;
  popularity?: number;
  profilePath?: string;
  genderValue?: string;
  /** @format int32 */
  castCount?: number;
  /** @format int32 */
  crewCount?: number;
  cast?: PersonCreditsCastDto[];
  crew?: PersonCreditsCrewDto[];
}

export interface LoginUserInfoDto {
  id?: string;
  nickname?: string;
  name?: string;
  email?: string;
  gender?: string;
  age?: string;
  birthday?: string;
  profileImage?: string;
  birthyear?: string;
  mobile?: string;
}

export interface LoginUserResponseDto {
  resultcode?: string;
  message?: string;
  userInfo?: LoginUserInfoDto;
  accessToken?: string;
  jwt?: string;
  /** @format int32 */
  expiresIn?: number;
  expireDate?: string;
}

export interface NaverDeleteTokenDto {
  accessToken?: string;
  result?: string;
  /** @format int32 */
  expiresIn?: number;
  error?: string;
  errorDescription?: string;
}

export interface KakaoAccountDto {
  profileNeedsAgreement?: boolean;
  profileNicknameNeedsAgreement?: boolean;
  profileImageNeedsAgreement?: boolean;
  profile?: KakaoProfileDto;
  nameNeedsAgreement?: boolean;
  name?: string;
  emailNeedsAgreement?: boolean;
  email?: string;
  ageRangeNeedsAgreement?: boolean;
  ageRange?: string;
  birthyearNeedsAgreement?: boolean;
  birthyear?: string;
  birthdayNeedsAgreement?: boolean;
  birthday?: string;
  birthdayType?: string;
  genderNeedsAgreement?: boolean;
  gender?: string;
  phoneNumberNeedsAgreement?: boolean;
  phoneNumber?: string;
  ciNeedsAgreement?: boolean;
  ci?: string;
  /** @format date-time */
  ciAuthenticatedAt?: string;
  emailVerified?: boolean;
  emailValid?: boolean;
  leapMonth?: boolean;
}

export interface KakaoPartnerDto {
  uuid?: string;
}

export interface KakaoProfileDto {
  nickname?: string;
  thumbnailImageUrl?: string;
  profileImageUrl?: string;
  defaultNickname?: boolean;
  defaultImage?: boolean;
}

export interface KakaoUserInfoDto {
  /** @format int64 */
  id?: number;
  hasSignedUp?: boolean;
  /** @format date-time */
  connectedAt?: string;
  /** @format date-time */
  synchedAt?: string;
  properties?: Record<string, string>;
  kakaoAccount?: KakaoAccountDto;
  forPartner?: KakaoPartnerDto;
}

export interface TmdbRecommendationsTvDto {
  /** @format int32 */
  page?: number;
  results?: TmdbRecommendationsTvResultsDto[];
  /** @format int32 */
  totalPages?: number;
  /** @format int32 */
  totalResults?: number;
}

export interface TmdbRecommendationsTvResultsDto {
  adult?: boolean;
  backdropPath?: string;
  genreIds?: number[];
  /** @format int32 */
  id?: number;
  originalLanguage?: string;
  overview?: string;
  popularity?: number;
  posterPath?: string;
  voteAverage?: number;
  /** @format int32 */
  voteCount?: number;
  originCountry?: string[];
  originalName?: string;
  firstAirDate?: string;
  name?: string;
}

export interface DetailTvResponseDto {
  adult?: boolean;
  backdropPath?: string;
  homepage?: string;
  originalLanguage?: string;
  /** @format int32 */
  id?: number;
  overview?: string;
  posterPath?: string;
  status?: string;
  genres?: TmdbGenreDto[];
  episodeRunTime?: number[];
  firstAirDate?: string;
  languages?: string[];
  lastAirDate?: string;
  name?: string;
  numberOfEpisodes?: string;
  numberOfSeasons?: string;
  originCountry?: string[];
  originalName?: string;
  seasons?: TmdbTvSeasonDto[];
  type?: string;
  credits?: TmdbVideoCreditsDto;
  link?: string;
  starRatingAverage?: number;
}

export interface TmdbGenreDto {
  /** @format int32 */
  id?: number;
  name?: string;
}

export interface TmdbTvSeasonDto {
  airDate?: string;
  /** @format int32 */
  episodeCount?: number;
  /** @format int32 */
  id?: number;
  name?: string;
  overview?: string;
  posterPath?: string;
  seasonNumber?: string;
  voteAverage?: number;
}

export interface TmdbVideoCreditsCastDto {
  adult?: boolean;
  /** @format int32 */
  gender?: number;
  /** @format int32 */
  id?: number;
  knownForDepartment?: string;
  name?: string;
  originalName?: string;
  popularity?: number;
  profilePath?: string;
  /** @format int32 */
  castId?: number;
  character?: string;
  creditId?: string;
  /** @format int32 */
  order?: number;
}

export interface TmdbVideoCreditsCrewDto {
  adult?: boolean;
  /** @format int32 */
  gender?: number;
  /** @format int32 */
  id?: number;
  knownForDepartment?: string;
  name?: string;
  originalName?: string;
  popularity?: number;
  profilePath?: string;
  creditId?: string;
  department?: string;
  job?: string;
}

export interface TmdbVideoCreditsDto {
  cast?: TmdbVideoCreditsCastDto[];
  crew?: TmdbVideoCreditsCrewDto[];
}

export interface TmdbRecommendationsMovieDto {
  /** @format int32 */
  page?: number;
  results?: TmdbRecommendationsMovieResultsDto[];
  /** @format int32 */
  totalPages?: number;
  /** @format int32 */
  totalResults?: number;
}

export interface TmdbRecommendationsMovieResultsDto {
  adult?: boolean;
  backdropPath?: string;
  genreIds?: number[];
  /** @format int32 */
  id?: number;
  originalLanguage?: string;
  overview?: string;
  popularity?: number;
  posterPath?: string;
  voteAverage?: number;
  /** @format int32 */
  voteCount?: number;
  originalTitle?: string;
  releaseDate?: string;
  title?: string;
  video?: boolean;
}

export interface DetailMovieResponseDto {
  adult?: boolean;
  backdropPath?: string;
  homepage?: string;
  originalLanguage?: string;
  /** @format int32 */
  id?: number;
  overview?: string;
  posterPath?: string;
  status?: string;
  genres?: TmdbGenreDto[];
  imdbId?: string;
  originalTitle?: string;
  releaseDate?: string;
  /** @format int32 */
  runtime?: number;
  title?: string;
  credits?: TmdbVideoCreditsDto;
  link?: string;
  starRatingAverage?: number;
}

export interface DetailCommentGetDataDto {
  /** @format int64 */
  commentNo?: number;
  originalMediaType?: string;
  apiId?: string;
  userId?: string;
  nickname?: string;
  starRating?: number;
  comment?: string;
  /** @format int64 */
  good?: number;
  /** @format int64 */
  bad?: number;
  createTime?: string;
}

export interface DetailCommentGetResponseDto {
  /** @format int64 */
  totalElements?: number;
  responseList?: DetailCommentGetDataDto[];
}

export interface DetailComicsRecommendationsResponseDto {
  results?: DetailComicsRecommendationsResultDto[];
}

export interface DetailComicsRecommendationsResultDto {
  adult?: boolean;
  backdropPath?: string;
  genreIds?: number[];
  /** @format int32 */
  id?: number;
  originalLanguage?: string;
  overview?: string;
  popularity?: number;
  posterPath?: string;
  voteAverage?: number;
  /** @format int32 */
  voteCount?: number;
  title?: string;
}

export interface AniListCharactersDto {
  nodes?: AniListCharactersNodesDto[];
}

export interface AniListCharactersImageDto {
  large?: string;
  medium?: string;
}

export interface AniListCharactersNameDto {
  full?: string;
  userPreferred?: string;
  native?: string;
}

export interface AniListCharactersNodesDto {
  /** @format int32 */
  id?: number;
  image?: AniListCharactersImageDto;
  name?: AniListCharactersNameDto;
  age?: string;
  gender?: string;
  description?: string;
  bloodType?: string;
  dateOfBirth?: AniListDateDto;
  /** @format int32 */
  favourites?: number;
  siteUrl?: string;
  favouriteBlocked?: boolean;
  favourite?: boolean;
}

export interface AniListDateDto {
  /** @format int32 */
  year?: number;
  /** @format int32 */
  month?: number;
  /** @format int32 */
  day?: number;
}

export interface DetailComicsResponseDto {
  adult?: boolean;
  backdropPath?: string;
  homepage?: string;
  originalLanguage?: string;
  /** @format int32 */
  id?: number;
  overview?: string;
  posterPath?: string;
  status?: string;
  title?: string;
  comicsGenres?: string[];
  characters?: AniListCharactersDto;
  /** @format int32 */
  volumes?: number;
  /** @format int32 */
  chapters?: number;
  startDate?: string;
}
