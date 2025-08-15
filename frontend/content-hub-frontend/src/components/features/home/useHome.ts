import { useNavigate } from 'react-router-dom';
import { useConfirmDialogStore } from '@/components/common/store/globalStateStore';

/**
 * 홈 화면 훅 반환 타입
 */
type useHomeReturnType = {
  // control: Control<HomeSchema>,
  isConfirmDialogOpen: boolean;
  handleConfirmOk: () => void;
  handleConfirmCancle: () => void;
};

/**
 * 홈 화면 훅
 * @returns 홈 화면 훅 반환값
 */
export const useHome = (): useHomeReturnType => {
  // navigate 훅
  const navigate = useNavigate();
  // confirm dialog 상태 훅
  const { isConfirmDialogOpen, setIsConfirmDialogOpen } =
    useConfirmDialogStore();

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

  // const defaultValue = {
  //     keyword: ""
  // }

  // const {
  //     control,
  //     getValues,
  // } = useForm<HomeSchema>({
  //     resolver: zodResolver(useHomeSchema()),
  //     defaultValues: defaultValue,
  // });

  return {
    // control: control,
    isConfirmDialogOpen: isConfirmDialogOpen,
    handleConfirmOk: handleConfirmOk,
    handleConfirmCancle: handleConfirmCancle,
  };
};
