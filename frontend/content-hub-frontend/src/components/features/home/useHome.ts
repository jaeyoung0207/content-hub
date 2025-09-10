import { useNavigate } from 'react-router-dom';
import {
  useConfirmDialogStore,
  useUserStore,
} from '@/components/common/store/globalStateStore';
import { Home } from '@/api/Home';
import { useQuery } from '@tanstack/react-query';
import { HomeRankingListResponseDto } from '@/api/data-contracts';
import { homeQueryKeys } from './queryKeys/homeQueryKeys';

/**
 * 홈 화면 훅 반환 타입
 */
type useHomeReturnType = {
  isConfirmDialogOpen: boolean;
  handleConfirmOk: () => void;
  handleConfirmCancle: () => void;
  data: HomeRankingListResponseDto | undefined;
  isLoading: boolean;
};

/**
 * 홈 화면 훅
 * @returns 홈 화면 훅 반환값
 */
export const useHome = (): useHomeReturnType => {
  // ================================================================================================== react hook
  // navigate 훅
  const navigate = useNavigate();
  // confirm dialog 상태 훅
  const { isConfirmDialogOpen, setIsConfirmDialogOpen } =
    useConfirmDialogStore();

  // ================================================================================================== zustand

  // 유저 정보 전역 상태 저장 훅
  const { user } = useUserStore();

  // ================================================================================================== react query

  // 홈 API 인스턴스
  const homeApi = new Home();

  // 콘텐츠 랭킹 조회 API 호출
  const { data, isLoading } = useQuery({
    queryKey: homeQueryKeys.getContentRankings,
    queryFn: async () =>
      (await homeApi.getContentRankings({ user_id: user?.userId })).data,
  });

  // ================================================================================================== function
  /**
   * 로그인 확인 다이얼로그에서 OK 버튼 클릭 시
   */
  const handleConfirmOk = () => {
    setIsConfirmDialogOpen();
    navigate('/login');
  };

  /**
   * 로그인 확인 다이얼로그에서 Cancel 버튼 클릭 시
   */
  const handleConfirmCancle = () => {
    setIsConfirmDialogOpen();
  };

  // ================================================================================================== return

  return {
    // control: control,
    isConfirmDialogOpen: isConfirmDialogOpen,
    handleConfirmOk: handleConfirmOk,
    handleConfirmCancle: handleConfirmCancle,
    data: data,
    isLoading: isLoading,
  };
};
