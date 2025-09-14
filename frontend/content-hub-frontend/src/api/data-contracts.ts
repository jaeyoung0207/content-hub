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
  commentId: number;
  originalMediaType: string;
  apiId: string;
  providerId: string;
  nickname: string;
  starRating: number;
  comment: string;
  /** @format int64 */
  good?: number;
  /** @format int64 */
  bad?: number;
}

export interface WishlistRequestDto {
  /** @format int64 */
  userId: number;
  originalMediaType: string;
  apiId: string;
  genreIds?: number[];
  mediaType?: string;
  title?: string;
  thumbnailImageUrl?: string;
}

export interface WishlistListResponseDto {
  aniWishlist?: WishlistResponseDto[];
  dramaWishlist?: WishlistResponseDto[];
  movieWishlist?: WishlistResponseDto[];
  comicsWishlist?: WishlistResponseDto[];
}

export interface WishlistResponseDto {
  /** @format int64 */
  userId?: number;
  originalMediaType?: string;
  apiId?: string;
  title?: string;
  thumbnailImageUrl?: string;
}

export interface DetailCommentSaveRequestDto {
  originalMediaType: string;
  apiId: string;
  genreIds: number[];
  title: string;
  thumbnailImageUrl?: string;
  provider: string;
  providerId: string;
  nickname: string;
  starRating?: number;
  comment: string;
  /** @format int64 */
  good?: number;
  /** @format int64 */
  bad?: number;
}

export interface SearchMovieResultsDto {
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
  originalTitle?: string;
  releaseDate?: string;
  title?: string;
  video?: boolean;
  originalMediaType?: string;
  genreNames?: string[];
  wishlisted?: boolean;
}

export interface SearchTvResultsDto {
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
  originCountry?: string[];
  firstAirDate?: string;
  name?: string;
  originalMediaType?: string;
  genreNames?: string[];
  wishlisted?: boolean;
}

export interface SearchVideoResponseDto {
  aniResults?: SearchTvResultsDto[];
  dramaResults?: SearchTvResultsDto[];
  movieResults?: SearchMovieResultsDto[];
  /** @format int32 */
  page?: number;
  /** @format int32 */
  totalPages?: number;
  /** @format int32 */
  totalResults?: number;
  isAniViewMore?: boolean;
  isDramaViewMore?: boolean;
  isMovieViewMore?: boolean;
}

export interface SearchMovieResponseDto {
  movieResults?: SearchMovieResultsDto[];
  /** @format int32 */
  page?: number;
  /** @format int32 */
  totalPages?: number;
  /** @format int32 */
  totalResults?: number;
}

export interface SearchTvResponseDto {
  aniResults?: SearchTvResultsDto[];
  dramaResults?: SearchTvResultsDto[];
  /** @format int32 */
  page?: number;
  /** @format int32 */
  totalPages?: number;
  /** @format int32 */
  totalResults?: number;
}

export interface SearchComicsResponseDto {
  comicsResults?: SearchComicsResultDto[];
  /** @format int32 */
  page?: number;
  /** @format int32 */
  totalPages?: number;
  /** @format int32 */
  totalResults?: number;
  isComicsViewMore?: boolean;
}

export interface SearchComicsResultDto {
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
  title?: string;
  originalMediaType?: string;
  genreNames?: string[];
  wishlisted?: boolean;
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
  /** @format int64 */
  userId?: number;
  provider?: string;
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
  status?: string;
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
  emailValid?: boolean;
  emailVerified?: boolean;
  leapMonth?: boolean;
}

export interface KakaoPartnerDto {
  uuid?: string;
}

export interface KakaoProfileDto {
  nickname?: string;
  thumbnailImageUrl?: string;
  profileImageUrl?: string;
  defaultImage?: boolean;
  defaultNickname?: boolean;
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

export interface HomeRankingListResponseDto {
  aniRankingList?: HomeRankingReponseDto[];
  dramaRankingList?: HomeRankingReponseDto[];
  movieRankingList?: HomeRankingReponseDto[];
  comicsRankingList?: HomeRankingReponseDto[];
}

export interface HomeRankingReponseDto {
  /** @format int64 */
  contentId?: number;
  /** @format int64 */
  rowNum?: number;
  originalMediaType?: string;
  mediaType?: string;
  apiId?: string;
  starRatingAverage?: number;
  /** @format int64 */
  starRatingCount?: number;
  title?: string;
  thumbnailImageUrl?: string;
  wishlisted?: boolean;
}

export interface DetailRecommendationsTvDto {
  /** @format int32 */
  page?: number;
  results?: DetailRecommendationsTvResultsDto[];
  /** @format int32 */
  totalPages?: number;
  /** @format int32 */
  totalResults?: number;
}

export interface DetailRecommendationsTvResultsDto {
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
  originalMediaType?: string;
  wishlisted?: boolean;
}

export interface DetailRecommendationsMovieDto {
  /** @format int32 */
  page?: number;
  results?: DetailRecommendationsMovieResultsDto[];
  /** @format int32 */
  totalPages?: number;
  /** @format int32 */
  totalResults?: number;
}

export interface DetailRecommendationsMovieResultsDto {
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
  originalMediaType?: string;
  wishlisted?: boolean;
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
  originalMediaType?: string;
  wishlisted?: boolean;
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
  aggregateCredits?: TmdbVideoCreditsDto;
  genreIds?: number[];
  link?: string;
  starRatingAverage?: number;
  wishlisted?: boolean;
}

export interface TmdbGenreDto {
  /** @format int32 */
  id?: number;
  name?: string;
}

export interface TmdbJobDto {
  creditId?: string;
  job?: string;
  episodeCount?: string;
}

export interface TmdbRoleDto {
  creditId?: string;
  character?: string;
  episodeCount?: string;
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
  roles?: TmdbRoleDto[];
  /** @format int32 */
  totalEpisodeCount?: number;
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
  jobs?: TmdbJobDto[];
  department?: string;
  job?: string;
  /** @format int32 */
  totalEpisodeCount?: number;
}

export interface TmdbVideoCreditsDto {
  cast?: TmdbVideoCreditsCastDto[];
  crew?: TmdbVideoCreditsCrewDto[];
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
  genreIds?: number[];
  link?: string;
  starRatingAverage?: number;
  wishlisted?: boolean;
}

export interface AniListCoverImageDto {
  color?: string;
  medium?: string;
  large?: string;
  extraLarge?: string;
}

export interface AniListDateDto {
  /** @format int32 */
  year?: number;
  /** @format int32 */
  month?: number;
  /** @format int32 */
  day?: number;
}

export interface AniListNameDto {
  full?: string;
  userPreferred?: string;
  native?: string;
}

export interface AniListPageInfoDto {
  /** @format int32 */
  total?: number;
  /** @format int32 */
  lastPage?: number;
  /** @format int32 */
  currentPage?: number;
  hasNextPage?: boolean;
  perPage?: boolean;
}

export interface AniListStaffDto {
  pageInfo?: AniListPageInfoDto;
  edges?: AniListStaffEdgesDto[];
}

export interface AniListStaffEdgesDto {
  /** @format int32 */
  id?: number;
  role?: string;
  node?: AniListStaffNodeDto;
}

export interface AniListStaffNodeDto {
  /** @format int32 */
  id?: number;
  /** @format int32 */
  age?: number;
  gender?: string;
  bloodType?: string;
  dateOfBirth?: AniListDateDto;
  dateOfDeath?: AniListDateDto;
  homeTown?: string;
  name?: AniListNameDto;
  image?: AniListCoverImageDto;
  siteUrl?: string;
  description?: string;
  primaryOccupations?: string[];
  yearsActive?: number[];
}

export interface AniListCharactersDto {
  pageInfo?: AniListPageInfoDto;
  edges?: AniListCharactersEdgesDto[];
}

export interface AniListCharactersEdgesDto {
  /** @format int32 */
  id?: number;
  role?: string;
  node?: AniListCharactersNodeDto;
}

export interface AniListCharactersImageDto {
  large?: string;
  medium?: string;
}

export interface AniListCharactersNodeDto {
  /** @format int32 */
  id?: number;
  image?: AniListCharactersImageDto;
  name?: AniListNameDto;
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
  genreIds?: number[];
  characters?: AniListCharactersDto;
  staff?: AniListStaffDto;
  /** @format int32 */
  volumes?: number;
  /** @format int32 */
  chapters?: number;
  startDate?: string;
  wishlisted?: boolean;
}

export interface DetailCommentGetDataDto {
  /** @format int64 */
  commentId?: number;
  originalMediaType?: string;
  apiId?: string;
  providerId?: string;
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

export interface CsrfToken {
  token?: string;
  parameterName?: string;
  headerName?: string;
}
