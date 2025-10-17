import { SearchApi } from '@api/SearchApi';
import { SearchVideoResponseDto } from '@api/data-contracts';
import { useQuery } from '@tanstack/react-query';
import { searchQueryKeys } from './queryKeys/searchQueryKeys';
import { useUserStore } from '@/components/common/store/globalStateStore';
import { freshOnMountOptions } from '@/components/common/config/queryOptions';

/**
 * 비디오 검색 훅 반환 타입
 */
type UseSearchReturnType = {
  isVideoLoading?: boolean; // 로딩 중 여부
  videoData?: SearchVideoResponseDto | undefined; // 비디오 검색 결과
};

/**
 * 검색 컴포넌트에서 사용하는 비디오 검색 훅
 * @param keyword 검색어
 * @param isAdult 성인물 포함 여부
 */
export const useVideoSearch = (
  keyword: string,
  isAdult: string
): UseSearchReturnType => {
  // ================================================================================================== zustand

  // 유저 정보 전역 상태 저장 훅
  const { user } = useUserStore();

  // ================================================================================================== react query

  // 검색 API 인스턴스 생성
  const searchApi = new SearchApi();

  /**
   * 검색 결과를 가져오기 위한 react-query 훅
   */
  const {
    data, // 검색 결과 데이터
    isLoading, // 로딩 중 여부
  } = useQuery({
    queryKey: searchQueryKeys.search.videoSearch(
      keyword,
      isAdult,
      user?.userId
    ),
    queryFn: async () => {
      // 비디오 검색 결과를 가져오는 API 호출
      return (
        await searchApi.searchVideo({ keyword: keyword, user_id: user?.userId })
      ).data;
    },
    enabled: !!keyword,
    ...freshOnMountOptions, // 쿼리 공통 옵션 적용
  });

  // ================================================================================================== return

  return {
    isVideoLoading: isLoading,
    videoData: data,
  };
};
