import { useUserStore } from '@/components/common/store/globalStateStore';
import { Home } from '@/api/Home';
import { useQuery } from '@tanstack/react-query';
import {
  HomeRankingListResponseDto,
  LoginUserInfoDto,
} from '@/api/data-contracts';
import { homeQueryKeys } from './queryKeys/homeQueryKeys';
import { freshOnMountOptions } from '@/components/common/config/queryOptions';

/**
 * 홈 화면 훅 반환 타입
 */
type useHomeReturnType = {
  data: HomeRankingListResponseDto | undefined;
  isLoading: boolean;
  user: LoginUserInfoDto | null;
};

/**
 * 홈 화면 훅
 * @returns 홈 화면 훅 반환값
 */
export const useHome = (): useHomeReturnType => {
  // ================================================================================================== react hook

  // ================================================================================================== zustand

  // 유저 정보 전역 상태 저장 훅
  const { user } = useUserStore();

  // ================================================================================================== react query

  // 홈 API 인스턴스
  const homeApi = new Home();

  // 콘텐츠 랭킹 조회 API 호출
  const { data, isLoading } = useQuery({
    queryKey: homeQueryKeys.getContentRankings(user?.userId),
    queryFn: async () =>
      (await homeApi.getContentRankings({ user_id: user?.userId })).data,
    ...freshOnMountOptions // 쿼리 공통 옵션 적용
  });

  // ================================================================================================== function

  // ================================================================================================== return

  return {
    data: data,
    isLoading: isLoading,
    user: user,
  };
};
