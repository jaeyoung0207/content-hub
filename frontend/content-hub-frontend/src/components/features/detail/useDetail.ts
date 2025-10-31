import {
  Dispatch,
  SetStateAction,
  useCallback,
  useEffect,
  useMemo,
  useState,
} from 'react';
import { DetailInformationApi } from '@/api/DetailInformationApi';
import { DetailCommentsApi } from '@/api/DetailCommentsApi';
import { useQuery, useQueryClient } from '@tanstack/react-query';
import {
  DetailTvResponseDto,
  DetailMovieResponseDto,
  DetailComicsResponseDto,
  LoginUserInfoDto,
} from '@/api/data-contracts';
import { detailQueryKeys } from './queryKeys/detailQueryKeys';
import { useTranslation } from 'react-i18next';
import { useUserStore } from '@/components/common/store/globalStateStore';
import { getContentMediaType } from '@/components/common/utils/convertUtil';
import { settings } from '@/components/common/config/settings';
import { freshOnMountOptions } from '@/components/common/config/queryOptions';

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
  user: LoginUserInfoDto | null; // 유저 정보
  setCastObserveTarget: Dispatch<SetStateAction<HTMLDivElement | null>>; // cast 관찰 대상 ref 설정 함수
  setCrewObserveTarget: Dispatch<SetStateAction<HTMLDivElement | null>>; // crew 관찰 대상 ref 설정 함수
  castDisplayCount: number; // cast 항목 수
  crewDisplayCount: number; // crew 항목 수
};

/**
 * 상세 화면 커스텀 훅
 * @param contentMediaType 컨텐츠 미디어 타입
 * @param apiId API ID
 * @param tabNo 탭 번호
 * @returns
 */
export const useDetail = (
  contentMediaType: string,
  apiId: string,
  tabNo: number
): useDetailReturnType => {
  // ================================================================================================== custom

  // 한 페이지당 항목 수
  const creditsPerPage = settings.detailCreditsPerPage;

  // ================================================================================================== react hook

  // i18n 훅
  const { t } = useTranslation();
  // 탭 인덱스 상태
  const [tabIndex, setTabIndex] = useState(tabNo);
  // 유저 평균 평점 상태
  const [userStarRating, setUserStarRating] = useState<string>();
  // cast 관찰 대상 ref 상태
  const [castObserveTarget, setCastObserveTarget] =
    useState<HTMLDivElement | null>(null);
  // crew 관찰 대상 ref 상태
  const [crewObserveTarget, setCrewObserveTarget] =
    useState<HTMLDivElement | null>(null);
  // cast 표시할 항목 수 상태
  const [castDisplayCount, setCastDisplayCount] =
    useState<number>(creditsPerPage);
  // crew 표시할 항목 수 상태
  const [crewDisplayCount, setCrewDisplayCount] =
    useState<number>(creditsPerPage);

  // ================================================================================================== zustand

  // 유저 정보 전역 상태 저장 훅
  const { user } = useUserStore();

  // ================================================================================================== react query

  // react query 클라이언트 훅
  const queryClient = useQueryClient();
  // 상세 API 인스턴스 생성
  const detailInformationApi = useMemo(() => new DetailInformationApi(), []);
  const detailCommentsApi = useMemo(() => new DetailCommentsApi(), []);
  // 리퀘스트 파라미터용 콘텐츠ID
  const apiIdParam = Number(apiId);

  /**
   * 상세 정보를 가져오는 API 호출 함수
   * @returns 상세 정보 데이터
   */
  const getDetailApi = async () => {
    // 컨텐츠 미디어 타입이 ANI 또는 DRAMA인 경우
    if (
      contentMediaType === getContentMediaType().aniCode ||
      contentMediaType === getContentMediaType().dramaCode ||
      contentMediaType === getContentMediaType().documentaryCode ||
      contentMediaType === getContentMediaType().kidsCode ||
      contentMediaType === getContentMediaType().newsCode ||
      contentMediaType === getContentMediaType().varietyCode
    ) {
      // TV 상세 정보를 가져오는 API 호출
      return (
        await detailInformationApi.getTvDetail({
          series_id: apiIdParam,
          content_media_type: contentMediaType,
          user_id: user?.userId,
        })
      ).data;
    }
    // 컨텐츠 미디어 타입이 MOVIE인 경우
    else if (contentMediaType === getContentMediaType().movieCode) {
      // MOVIE 상세 정보를 가져오는 API 호출
      return (
        await detailInformationApi.getMovieDetail({
          movie_id: apiIdParam,
          content_media_type: contentMediaType,
          user_id: user?.userId,
        })
      ).data;
    }
    // 컨텐츠 미디어 타입이 COMICS인 경우
    else if (contentMediaType === getContentMediaType().comicsCode) {
      // COMICS 상세 정보를 가져오는 API 호출
      return (
        await detailInformationApi.getComicsDetail({
          comics_id: apiIdParam,
          content_media_type: contentMediaType,
          user_id: user?.userId,
        })
      ).data;
    }
  };

  /**
   * 상세 정보를 가져오는 쿼리
   */
  const { data, isLoading, isError } = useQuery({
    queryKey: detailQueryKeys.detail.getDetail(
      contentMediaType,
      apiId,
      user?.userId
    ),
    queryFn: async () => {
      // 상세 정보를 가져오는 API 호출
      return await getDetailApi();
    },
    enabled: !!contentMediaType, // contentMediaType이 존재할 때만 쿼리 실행
    ...freshOnMountOptions, // 쿼리 공통 옵션 적용
  });

  // ================================================================================================== function

  /**
   * IntersectionObserver 콜백 함수
   * castObserveTarget이 화면에 나타날 때마다 실행
   */
  const castObserverCallback = useCallback<IntersectionObserverCallback>(
    (entries) => {
      entries.forEach((entry) => {
        // castObserveTarget이 화면에 나타나면 표시할 항목 수 증가
        if (entry.isIntersecting) {
          setCastDisplayCount((prev) => prev + creditsPerPage);
        }
      });
    },
    [setCastDisplayCount, creditsPerPage]
  );

  /**
   * IntersectionObserver 콜백 함수
   * crewObserveTarget이 화면에 나타날 때마다 실행
   */
  const crewObserverCallback = useCallback<IntersectionObserverCallback>(
    (entries) => {
      entries.forEach((entry) => {
        // crewObserveTarget이 화면에 나타나면 표시할 항목 수 증가
        if (entry.isIntersecting) {
          setCrewDisplayCount((prev) => prev + creditsPerPage);
        }
      });
    },
    [setCrewDisplayCount, creditsPerPage]
  );

  // ================================================================================================== useEffect

  /**
   * 별점 평균을 가져오는 useEffect
   */
  useEffect(() => {
    queryClient.fetchQuery({
      queryKey: detailQueryKeys.detail.getStarRatingAverage(
        contentMediaType,
        apiId
      ),
      queryFn: async () => {
        // 유저 평균 평점 취득
        const response = (
          await detailCommentsApi.getStarRatingAverage({
            content_media_type: contentMediaType,
            api_id: apiId,
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
  }, [tabIndex, apiId, detailCommentsApi, contentMediaType, queryClient, t]);

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
   * IntersectionObserver를 설정하는 useEffect
   * castObserveTarget이 변경될 때마다 실행
   */
  useEffect(() => {
    // castObserveTarget이 null인 경우에는 관찰을 중지
    if (!castObserveTarget) {
      return;
    }

    // 새로운 IntersectionObserver를 생성
    const observer = new IntersectionObserver(castObserverCallback, {
      threshold: 0.1,
    });

    // castObserveTarget이 화면에 보이면 관찰을 시작
    observer.observe(castObserveTarget);

    // castObserveTarget이 변경되면 이전에 관찰하던 타겟은 관찰을 중지
    return () => {
      observer.disconnect();
    };
  }, [castObserveTarget, castObserverCallback]);

  /**
   * IntersectionObserver를 설정하는 useEffect
   * crewObserveTarget이 변경될 때마다 실행
   */
  useEffect(() => {
    // crewObserveTarget이 null인 경우에는 관찰을 중지
    if (!crewObserveTarget) {
      return;
    }

    // 새로운 IntersectionObserver를 생성
    const observer = new IntersectionObserver(crewObserverCallback, {
      threshold: 0.1,
    });

    // crewObserveTarget이 화면에 보이면 관찰을 시작
    observer.observe(crewObserveTarget);

    // crewObserveTarget이 변경되면 이전에 관찰하던 타겟은 관찰을 중지
    return () => {
      observer.disconnect();
    };
  }, [crewObserveTarget, crewObserverCallback]);

  /**
   * 표시할 항목 수 초기화 useEffect
   */
  useEffect(() => {
    setCastDisplayCount(creditsPerPage);
    setCrewDisplayCount(creditsPerPage);
  }, [contentMediaType, apiId, creditsPerPage]);

  // ================================================================================================== return

  return {
    isLoading: isLoading,
    tabIndex: tabIndex,
    setTabIndex: setTabIndex,
    data: data,
    isError: isError,
    userStarRating: userStarRating,
    user: user,
    setCastObserveTarget: setCastObserveTarget,
    setCrewObserveTarget: setCrewObserveTarget,
    castDisplayCount: castDisplayCount,
    crewDisplayCount: crewDisplayCount,
  };
};
