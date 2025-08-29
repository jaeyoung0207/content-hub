import { Search } from '@api/Search';
import {
  SearchComicsResponseDto,
  SearchComicsMediaResultDto,
  SearchVideoResponseDto,
  TmdbSearchMovieResultsDto,
  TmdbSearchTvResultsDto,
} from '@api/data-contracts';
import { useQuery } from '@tanstack/react-query';
import { searchQueryKeys } from './queryKeys/searchQueryKeys';
import { AxiosError, isAxiosError } from 'axios';
import { toast } from 'react-toastify';
import {
  changeConsoleColor,
  formattingErrorMsg,
  formattingApiErrorMsg,
} from '@/components/common/utils/errorUtil';
import { ERROR_MESSAGE } from '@/components/common/constants/constants';
import { AxiosErrorType } from '@/components/common/config/queryClientConfig';

// 공통 검색 결과 타입
export type SearchCommonResultType =
  | TmdbSearchTvResultsDto
  | TmdbSearchMovieResultsDto
  | SearchComicsMediaResultDto;

/**
 * 검색 훅 반환 타입
 */
type UseSearchReturnType = {
  isLoading?: boolean; // 로딩 중 여부
  data?: {
    videoResult: SearchVideoResponseDto | undefined; // 비디오 검색 결과
    comicsResult: SearchComicsResponseDto | undefined; // 만화 검색 결과
  };
};

/**
 * 검색 컴포넌트에서 사용하는 훅
 * @param keyword 검색어
 * @param isAdult 성인물 포함 여부
 */
export const useSearch = (
  keyword: string,
  isAdult: string
): UseSearchReturnType => {
  // ================================================================================================== react query

  // 검색 API 인스턴스 생성
  const searchApi = new Search();

  /**
   * 비디오 및 만화 검색 결과를 가져오는 API 호출 함수
   * @returns 검색 결과 데이터
   */
  const getSearchResult = async () => {
    const [videoResult, comicsResult] = await Promise.allSettled([
      searchVideoApi(),
      searchComicsApi(),
    ]);
    // 에러인 경우
    if (
      videoResult.status === 'rejected' ||
      comicsResult.status === 'rejected'
    ) {
      const videoError: AxiosError<AxiosErrorType> | undefined =
        videoResult.status === 'rejected' && videoResult.reason
          ? videoResult.reason
          : undefined;
      const comicsError: AxiosError<AxiosErrorType> | undefined =
        comicsResult.status === 'rejected' && comicsResult.reason
          ? comicsResult.reason
          : undefined;
      if (videoError) {
        if (!isAxiosError(videoError) || !videoError.response) {
          throw videoError;
        } else {
          searchErrorMessage(videoError.response.data);
        }
      }
      if (comicsError) {
        if (!isAxiosError(comicsError) || !comicsError.response) {
          throw comicsError;
        } else {
          searchErrorMessage(comicsError.response.data);
        }
      }
    }
    // 정상인 경우
    return {
      videoResult:
        videoResult.status === 'fulfilled' ? videoResult.value : undefined,
      comicsResult:
        comicsResult.status === 'fulfilled' ? comicsResult.value : undefined,
    };
  };

  /**
   * 비디오(애니/드라마/영화) 검색 API 호출
   * @returns 비디오 검색 결과
   */
  const searchVideoApi = async () => {
    return (await searchApi.searchVideo({ query: keyword }, {})).data;
  };

  /**
   * 만화 검색 API 호출
   * @returns 만화 검색 결과
   */
  const searchComicsApi = async () => {
    return (
      await searchApi.searchComics({ query: keyword, isMainPage: true }, {})
    ).data;
  };

  /**
   * 검색 결과를 가져오기 위한 react-query 훅
   */
  const {
    data, // 검색 결과 데이터
    isLoading, // 로딩 중 여부
  } = useQuery({
    queryKey: searchQueryKeys.search.search(keyword, isAdult),
    queryFn: async () => {
      // 비디오 및 만화 검색 결과를 가져오는 API 호출
      return await getSearchResult();
    },
    enabled: !!keyword,
  });

  // ================================================================================================== return

  return {
    isLoading: isLoading,
    data: data,
  };
};

/**
 * 검색 에러 메시지 출력 함수
 * @param errorData 에러 데이터
 */
const searchErrorMessage = (errorData: AxiosErrorType) => {
  const name = errorData.name;
  const path = errorData.path;
  const status = errorData.status;
  const message = errorData.message;
  const body = errorData.body;
  changeConsoleColor(
    formattingApiErrorMsg({
      name: name,
      path: path,
      status: status,
      message: message,
      body: body,
    }) || ERROR_MESSAGE.API_RESPONSE_ERROR.message
  );
  toast.error(
    formattingErrorMsg(
      ERROR_MESSAGE.API_RESPONSE_ERROR.name,
      ERROR_MESSAGE.API_RESPONSE_ERROR.message
    ),
    {
      toastId: 'apiResponseError', // 중복 토스트 방지
    }
  );
};
