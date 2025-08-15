import { Dispatch, SetStateAction, useEffect, useMemo, useState } from 'react';
import { Detail } from '@/api/Detail';
import { useQuery, useQueryClient } from '@tanstack/react-query';
import {
  DetailTvResponseDto,
  DetailMovieResponseDto,
  DetailComicsResponseDto,
} from '@/api/data-contracts';
import { MEDIA_TYPE } from '@/components/common/constants/constants';
import { detailQueryKeys } from './queryKeys/detailQueryKeys';
import { useTranslation } from 'react-i18next';

/**
 * 상세 정보 결과 타입
 */
export type DetailResponseType =
  | DetailTvResponseDto
  | DetailMovieResponseDto
  | DetailComicsResponseDto;

/**
 * useDetail 훅 반환 타입
 */
type useDetailReturnType = {
  tabIndex: number; // 현재 탭 인덱스
  setTabIndex: Dispatch<SetStateAction<number>>; // 탭 인덱스 설정 함수
  data?: DetailResponseType; // 상세 정보 데이터
  isLoading: boolean; // 로딩 중 여부
  isError: boolean; // 에러 여부
  userStarRating?: string; // 유저 평균 평점
};

/**
 * 상세 화면 커스텀 훅
 * @param originalMediaType 원본 미디어 타입
 * @param contentId 컨텐츠 ID
 * @param tabNo 탭 번호
 * @returns
 */
export const useDetail = (
  originalMediaType: string,
  contentId: string,
  tabNo: number
): useDetailReturnType => {
  // ================================================================================================== react hook

  // i18n 훅
  const { t } = useTranslation();
  // 탭 인덱스 상태
  const [tabIndex, setTabIndex] = useState(tabNo);
  // 유저 평균 평점 상태
  const [userStarRating, setUserStarRating] = useState<string>();

  // ================================================================================================== react query

  // react query 클라이언트 훅
  const queryClient = useQueryClient();
  // 상세 API 인스턴스 생성
  const detailApi = useMemo(() => new Detail(), []);
  // 리퀘스트 파라미터용 컨텐츠ID
  const contentIdParam = Number(contentId);

  /**
   * 상세 정보를 가져오는 API 호출 함수
   * @returns 상세 정보 데이터
   */
  const getDetailApi = async () => {
    // 원본 미디어 타입이 ANI 또는 DRAMA인 경우
    if (
      originalMediaType === MEDIA_TYPE.ANI ||
      originalMediaType === MEDIA_TYPE.DRAMA
    ) {
      // TV 상세 정보를 가져오는 API 호출
      return (await detailApi.getTvDetail({ series_id: contentIdParam })).data;
    }
    // 원본 미디어 타입이 MOVIE인 경우
    else if (originalMediaType === MEDIA_TYPE.MOVIE) {
      // MOVIE 상세 정보를 가져오는 API 호출
      return (await detailApi.getMovieDetail({ movie_id: contentIdParam }))
        .data;
    }
    // 원본 미디어 타입이 COMICS인 경우
    else if (originalMediaType === MEDIA_TYPE.COMICS) {
      // COMICS 상세 정보를 가져오는 API 호출
      return (await detailApi.getComicsDetail({ comics_id: contentIdParam }))
        .data;
    }
  };

  /**
   * 상세 정보를 가져오는 쿼리
   */
  const { data, isLoading, isError } = useQuery({
    queryKey: detailQueryKeys.detail.getDetail(originalMediaType, contentId),
    queryFn: async () => {
      // 상세 정보를 가져오는 API 호출
      return await getDetailApi();
    },
    enabled: !!originalMediaType, // originalMediaType이 존재할 때만 쿼리 실행
  });

  // ================================================================================================== function

  // ================================================================================================== useEffect

  /**
   * 별점 평균을 가져오는 useEffect
   */
  useEffect(() => {
    queryClient.fetchQuery({
      queryKey: detailQueryKeys.detail.getStarRatingAverage(
        originalMediaType,
        contentId
      ),
      queryFn: async () => {
        // 유저 평균 평점 취득
        const response = (
          await detailApi.getStarRatingAverage({
            originalMediaType: originalMediaType,
            apiId: contentId,
          })
        ).data;
        // 유저 평균 평점 설정
        const convertResponse = response
          ? response.toFixed(1) // 소수점 한자리까지 표시(ex: 4 -> 4.0)
          : t('info.notExist');
        setUserStarRating(convertResponse);
        return convertResponse;
      },
    });
  }, [tabIndex, contentId, detailApi, originalMediaType, queryClient, t]);

  /**
   * 탭 번호가 변경될 때마다 실행되는 useEffect
   */
  useEffect(() => {
    // 화면 진입시에 탭 인덱스 상태설정
    if (tabNo !== tabIndex) {
      setTabIndex(tabNo);
    }
  }, [tabNo, tabIndex]);

  /**
   * 컴포넌트 마운트/언마운트 시 실행되는 useEffect
   */
  useEffect(() => {
    // 언마운트시 실행
    return () => {
      // 리다이렉트 주소 삭제
      sessionStorage.removeItem('redirectUrl');
    };
  }, []);

  // ================================================================================================== return

  return {
    isLoading: isLoading,
    tabIndex: tabIndex,
    setTabIndex: setTabIndex,
    data: data,
    isError: isError,
    userStarRating: userStarRating,
  };
};
