import { Search } from '@api/Search';
import { SearchComicsResponseDto } from '@api/data-contracts';
import { useQuery } from '@tanstack/react-query';
import { searchQueryKeys } from './queryKeys/searchQueryKeys';
import { useUserStore } from '@/components/common/store/globalStateStore';
import { freshOnMountOptions } from '@/components/common/config/queryOptions';

/**
 * 만화 검색 훅 반환 타입
 */
type UseComicsSearchReturnType = {
  isComicsLoading?: boolean; // 로딩 중 여부
  comicsData?: SearchComicsResponseDto | undefined; // 검색 결과 데이터
  comicsStatus?: number; // HTTP 상태 코드
  isComicsError?: boolean; // 에러 발생 여부
};

/**
 * 검색 컴포넌트에서 사용하는 만화 검색 훅
 * @param keyword 검색어
 * @param isAdult 성인물 포함 여부
 */
export const useComicsSearch = (
  keyword: string,
  isAdult: string
): UseComicsSearchReturnType => {
  // ================================================================================================== zustand

  // 유저 정보 전역 상태 저장 훅
  const { user } = useUserStore();

  // ================================================================================================== react query

  // 검색 API 인스턴스 생성
  const searchApi = new Search();

  /**
   * 검색 결과를 가져오기 위한 react-query 훅
   */
  const {
    data, // 검색 결과 데이터
    isLoading, // 로딩 중 여부
  } = useQuery({
    queryKey: searchQueryKeys.search.comicsSearch(
      keyword,
      isAdult,
      user?.userId
    ),
    queryFn: async () => {
      // 만화 검색 결과를 가져오는 API 호출
      return (
        await searchApi.searchComics({
          keyword: keyword,
          is_main_page: true,
          user_id: user?.userId,
        })
      ).data;
    },
    enabled: !!keyword,
    ...freshOnMountOptions // 쿼리 공통 옵션 적용
  });

  // ================================================================================================== return

  return {
    isComicsLoading: isLoading,
    comicsData: data,
  };
};
