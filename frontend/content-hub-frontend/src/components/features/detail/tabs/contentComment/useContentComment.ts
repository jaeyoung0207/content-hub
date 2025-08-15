import {
  DetailCommentGetDataDto,
  DetailCommentSaveRequestDto,
  DetailCommentUpdateRequestDto,
} from '@/api/data-contracts';
import {
  ContentCommentSchema,
  useContentCommentSchema,
} from './useContentComment.validation';
import { Control, SubmitHandler, useForm, useWatch } from 'react-hook-form';
import {
  Dispatch,
  RefObject,
  SetStateAction,
  useCallback,
  useEffect,
  useRef,
  useState,
} from 'react';
import {
  useProviderStore,
  useUserStore,
} from '@/components/common/store/globalStateStore';
import { Detail } from '@/api/Detail';
import {
  useInfiniteQuery,
  useMutation,
  useQueryClient,
} from '@tanstack/react-query';
import { zodResolver } from '@hookform/resolvers/zod';
import { AxiosError } from 'axios';
import { useNavigate } from 'react-router-dom';
import { DetailResponseType } from '../../useDetail';
import { handleUnExceptedError } from '@/components/common/utils/commonUtil';
import { detailQueryKeys } from '../../queryKeys/detailQueryKeys';

/**
 * 콘텐츠 코멘트 훅의 결과 타입
 */
type useContentCommentReturnType = {
  control: Control<ContentCommentSchema>; // react-hook-form의 control 객체
  handleCommentOnClick: () => void; // 코멘트 작성 버튼 클릭 시 처리 함수
  handleSaveComment: () => void; // 코멘트 저장 함수
  handleEditComment: (commentDataList: DetailCommentGetDataDto) => void; // 코멘트 수정 가능 상태 설정 함수
  handleUpdateComment: () => void; // 코멘트 수정 함수
  isLoading: boolean; // 로딩 상태
  setObserveTarget: Dispatch<SetStateAction<HTMLDivElement | null>>; // 무한스크롤 div태그 관찰용 상태 설정 함수
  data: CommentUseInfiniteQueryResultType | undefined; // useInfiniteQuery의 데이터
  hasNextPage: boolean; // 다음 페이지가 있는지 여부
  isFetchingNextPage: boolean; // 다음 페이지를 가져오는 중인지 여부
  totalElements: number; // 코멘트 총 개수
  isLoginConfirmOpen: boolean; // 로그인 확인 모달 열림 여부
  handleLoginConfirmOk: () => void; // 로그인 확인 모달에서 확인 버튼 클릭 시 처리 함수
  handleLoginConfirmCancel: () => void; // 로그인 확인 모달에서 취소 버튼 클릭 시 처리 함수
  isDeleteConfirmOpen: boolean; // 삭제 확인 모달 열림 여부
  handleDeleteConfirmOk: () => void; // 삭제 확인 모달에서 확인 버튼 클릭 시 처리 함수
  handleDeleteConfirmCancel: () => void; // 삭제 확인 모달에서 취소 버튼 클릭 시 처리 함수
  handleDeleteOnClick: (commentNo: number) => void; // 삭제 버튼 클릭 시 처리 함수
  textAreaRef: RefObject<HTMLTextAreaElement | null>; // 코멘트 작성 시 포커스를 주기 위한 ref
  isMyComment: boolean | null; // 로그인한 유저의 코멘트 여부 상태
  comment: string; // 코멘트 작성 시 입력된 값
  isCommentEditable: boolean; // 코멘트 수정 가능 상태
  starRatingErrorMsg?: string; // 별점 선택 시 유효성 검사 에러 메시지
};

/**
 * 무한스크롤 결과 타입
 */
export type CommentUseInfiniteQueryResultType = {
  pages: (DetailCommentGetDataDto[] | undefined)[];
  pageParams: (number | undefined)[];
};

/**
 * 콘텐츠 코멘트 훅
 * 콘텐츠 상세 정보에 대한 코멘트을 관리하는 훅
 * 코멘트 작성, 수정, 삭제 기능을 제공하며, 코멘트 목록을 무한 스크롤로 가져옴
 * 로그인 상태 확인 및 로그인 확인 모달 처리
 * @param detailResult 콘텐츠 상세 정보 결과
 * @param originalMediaType 원본 미디어 타입
 * @returns CommentUseInfiniteQueryResultType
 */
export const useContentComment = (
  detailResult: DetailResponseType,
  originalMediaType: string
): useContentCommentReturnType => {
  // ================================================================================================== react hook

  // 네비게이션 훅
  const navigate = useNavigate();
  // 로딩 상태
  const [isLoading, setIsLoading] = useState(false);
  // 코멘트 번호 상태
  const [commentNo, setCommentNo] = useState<number>();
  // 코멘트 총 개수 상태
  const [totalElements, setTotalElements] = useState<number>(0);
  // 무한스크롤 div태그 관찰용 상태
  const [observeTarget, setObserveTarget] = useState<HTMLDivElement | null>(
    null
  );
  // 코멘트 수정 가능 상태
  const [isCommentEditable, setIsCommentEditable] = useState(false);
  // 로그인 확인 모달 상태
  const [isLoginConfirmOpen, setIsLoginConfirmOpen] = useState(false);
  // 삭제 확인 모달 상태
  const [isDeleteConfirmOpen, setIsDeleteConfirmOpen] = useState(false);
  // 로그인한 유저의 코멘트 여부 상태
  const [isMyComment, setIsMyComment] = useState<boolean | null>(null);
  // 코멘트 작성 시 포커스를 주기 위한 ref
  const textAreaRef = useRef<HTMLTextAreaElement>(null);
  // 코멘트 수정 시 해당 코멘트 번호를 저장하기 위한 ref
  const commentNoRef = useRef<number>(null);
  // 등록 중 상태
  const [isSaving, setIsSaving] = useState(false);
  // 수정 중 상태
  const [isUpdating, setIsUpdating] = useState(false);
  // 삭제 중 상태
  const [isDeleting, setIsDeleting] = useState(false);

  // ================================================================================================== zustand

  // 유저 정보 전역 상태 저장 훅
  const { user } = useUserStore();
  // 프로바이더 정보 전역 상태 저장 훅
  const { provider } = useProviderStore();

  // ================================================================================================== react hook form

  // 초기값 설정
  const defaultValues = {
    starRating: 0,
    comment: '',
  };

  // useForm 훅을 사용하여 폼 상태를 관리하고, 초기값과 유효성 검사 스키마를 설정
  const {
    control,
    setValue,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<ContentCommentSchema>({
    resolver: zodResolver(useContentCommentSchema),
    defaultValues: defaultValues,
  });

  // 코멘트 작성 시 입력된 값을 가져오기 위한 useWatch 훅
  const comment = useWatch({
    control,
    name: 'comment',
  });

  // ================================================================================================== react query

  // 쿼리 클라이언트 훅
  const queryClient = useQueryClient();
  // API 인스턴스 생성
  const detailApi = new Detail();

  // useInfiniteQuery의 query key
  // 콘텐츠 ID
  const contentId = detailResult.id!.toString();

  // useInfiniteQuery를 사용하여 코멘트 목록을 가져옴
  // 페이지 매개변수는 페이지가 변경될 때마다 업데이트되며, 페이지가 변경될 때마다 새로운 데이터를 가져옴
  const {
    data, // 서버에 요청해서 받아온 데이터
    fetchNextPage, // 다음페이지 호출
    isFetchingNextPage, // 다음페이지 로딩중 여부
    hasNextPage, // 가져올 다음페이지가 있는지 여부를 나타냄(boolean). getNextPageParam옵션을 통해 확인가능
    hasPreviousPage, // 이전 페이지가 있는지 여부를 나타냄(boolean). getPreviousPageParam옵션을 통해 확인가능
  } = useInfiniteQuery<
    DetailCommentGetDataDto[] | undefined, // queryFn이 반환하는 원본 데이터
    AxiosError, // 에러 타입 (보통 AxiosError)
    CommentUseInfiniteQueryResultType, // 반환할 최종 데이터 형태 (select로 가공한 경우)
    [string, string, string, string], // query key의 타입 (예: [string, string] -> [루트 키, 서브 키])
    number | undefined // pageParam 타입 (보통 number | undefined)
  >({
    // useInfiniteQuery의 키 지정
    queryKey: detailQueryKeys.detail.contentComment.list(
      originalMediaType,
      contentId
    ) as [string, string, string, string],
    // 쿼리가 데이터를 요청하는 데 사용할 함수/API 지정
    queryFn: async ({ pageParam = 0 }) => {
      const response = (
        await detailApi.getCommentList({
          apiId: contentId,
          originalMediaType: originalMediaType,
          page: pageParam,
          userId: user?.id,
        })
      ).data;
      setTotalElements(response.totalElements!);
      return response.responseList;
    },
    // lastPageData : 가장 최근에 불러온 캐싱된 데이터 / allPages: 지금까지 불러온 데이터
    // 새 데이터를 받아올 때 마지막 페이지와 전체 페이지 배열을 함께 받아옴
    // 더 불러올 데이터가 있는지 여부를 결정하는데 사용
    // 반환값이 다음 API호출할때의 pageParam으로 들어감
    getNextPageParam: (lastPageData, allPages) => {
      return lastPageData!.length === 0 ? undefined : allPages.length;
    },
    select: (data) => ({
      // pages는 각 페이지의 데이터를 배열로 묶음
      pages: data.pages,
      // pageParams는 페이지가 변경될 때마다 업데이트되며, 새로운 데이터를 가져옴
      pageParams: data.pageParams,
    }),
    // 초기 페이지 매개변수를 지정
    initialPageParam: 0,
    // enabled: !!tabIndex, // useInfiniteQuery가 실행되는 조건 지정
  });

  /**
   * 등록/수정/삭제 성공 후 최신데이터 조회
   */
  const mutationOnSuccess = () => {
    // 최신 코멘트 목록 조회
    queryClient.refetchQueries({
      queryKey: detailQueryKeys.detail.contentComment.list(
        originalMediaType,
        contentId
      ),
    });
    // 최신 별점 평균 조회
    queryClient.refetchQueries({
      queryKey: detailQueryKeys.detail.getStarRatingAverage(
        originalMediaType,
        contentId
      ),
    });
  };

  /**
   * 코멘트 저장 API 호출 처리
   * 코멘트 작성 시 호출되며, 코멘트 데이터를 서버에 저장
   * 코멘트 수정 후 최신 코멘트 목록 및 별점 데이터를 조회
   * @param data 코멘트 데이터
   */
  const saveComentMutation = useMutation({
    mutationKey: detailQueryKeys.detail.contentComment.save(
      originalMediaType,
      contentId
    ),
    mutationFn: async (data: ContentCommentSchema) => {
      // 코멘트 저장 중 상태 설정
      setIsSaving(true);
      // 코멘트 저장 요청 데이터 생성
      const requestData = {
        originalMediaType: originalMediaType,
        apiId: contentId,
        provider: provider,
        userId: user?.id,
        nickname: user?.nickname,
        comment: data.comment,
        starRating: data.starRating,
      } as DetailCommentSaveRequestDto;
      // 코멘트 저장 API 호출
      return (await detailApi.saveComent(requestData)).data;
    },
    // 성공 후 처리
    onSuccess: () => {
      // 코멘트 저장 후 최신 데이터 조회
      mutationOnSuccess();
    },
    // 성공/실패 여부와 관계없이 실행되는 콜백 함수
    onSettled: () => {
      // 코멘트 저장 중 상태 해제
      setIsSaving(false);
    },
  });

  /**
   * 코멘트 수정 API 호출 처리
   * 코멘트 수정 시 호출되며, 수정된 코멘트 데이터를 서버에 저장
   * 코멘트 수정 후 최신 코멘트 목록 및 별점 데이터를 조회
   * @param data 코멘트 데이터
   */
  const updateCommentMutation = useMutation({
    mutationKey: detailQueryKeys.detail.contentComment.update(
      originalMediaType,
      contentId
    ),
    mutationFn: async (data: ContentCommentSchema) => {
      // 코멘트 갱신 중 상태 설정
      setIsUpdating(true);
      // 코멘트 갱신 요청 데이터 생성
      const requestData = {
        commentNo: commentNo,
        originalMediaType: originalMediaType,
        apiId: contentId,
        userId: user?.id,
        nickname: user?.nickname,
        comment: data.comment,
        starRating: data.starRating,
      } as DetailCommentUpdateRequestDto;
      // 코멘트 수정 API 호출
      return (await detailApi.updateComent(requestData)).data;
    },
    // 성공 후 처리
    onSuccess: () => {
      // 코멘트 수정 후 최신 데이터 조회
      mutationOnSuccess();
    },
    // 성공/실패 여부와 관계없이 실행되는 콜백 함수
    onSettled: () => {
      // 코멘트 수정 중 상태 해제
      setIsUpdating(false);
    },
  });

  /**
   * 코멘트 삭제 API 호출 처리
   * 코멘트 삭제 시 호출되며, 해당 코멘트를 서버에서 삭제
   * 코멘트 수정 후 최신 코멘트 목록 및 별점 데이터를 조회
   * @param commentNo 삭제할 코멘트 번호
   */
  const deleteCommentMutation = useMutation({
    mutationKey: detailQueryKeys.detail.contentComment.delete(
      originalMediaType,
      contentId
    ),
    mutationFn: async (commentNo: number) => {
      // 코멘트 삭제 중 상태 설정
      setIsDeleting(true);
      // 코멘트 삭제 API 호출
      return (await detailApi.deleteComment({ commentNo })).data;
    },
    // 성공 후 처리
    onSuccess: () => {
      // 코멘트 삭제 후 최신 데이터 조회
      mutationOnSuccess();
    },
    // 성공/실패 여부와 관계없이 실행되는 콜백 함수
    onSettled: () => {
      // 코멘트 삭제 중 상태 해제
      setIsDeleting(false);
    },
  });

  // ================================================================================================== function

  /**
   * 코멘트 저장 함수
   * @param data 코멘트 데이터
   * @returns Promise<void>
   */
  const saveComment: SubmitHandler<ContentCommentSchema> = async (
    data
  ): Promise<void> => {
    // 이미 저장, 수정, 삭제 중인 경우 함수 종료
    if (isSaving || isUpdating || isDeleting) {
      return;
    }
    setIsLoading(true);
    try {
      // 코멘트 저장 mutation 호출
      await saveComentMutation.mutateAsync(data);
      // 코멘트 작성 후 폼 초기화
      reset();
    } finally {
      setIsLoading(false);
    }
  };

  /**
   * 코멘트 수정 함수
   * @param data 코멘트 데이터
   * @returns Promise<void>
   */
  const updateComment: SubmitHandler<ContentCommentSchema> = async (
    data
  ): Promise<void> => {
    // 이미 저장, 수정, 삭제 중인 경우 함수 종료
    if (isSaving || isUpdating || isDeleting) {
      return;
    }
    setIsLoading(true);
    try {
      // 코멘트 수정 mutation 호출
      await updateCommentMutation.mutateAsync(data);
      // 코멘트 수정 후 폼 초기화
      reset();
    } finally {
      setIsLoading(false);
    }
  };

  /**
   * 코멘트 삭제 함수
   * @param commentNo 코멘트 번호
   * @returns Promise<void>
   */
  const deleteComment = async (commentNo: number): Promise<void> => {
    // 이미 저장, 수정, 삭제 중인 경우 함수 종료
    if (isSaving || isUpdating || isDeleting) {
      return;
    }
    setIsLoading(true);
    try {
      // 코멘트 삭제 mutation 호출
      await deleteCommentMutation.mutateAsync(commentNo);
    } finally {
      setIsLoading(false);
    }
  };

  /**
   * 코멘트 작성 버튼 클릭 시 로그인 확인 모달을 열도록 설정
   */
  const handleCommentOnClick = () => {
    // 유저가 로그인하지 않은 경우
    if (!user) {
      // 코멘트 작성란의 포커스 해제
      textAreaRef.current?.blur();
      // 로그인 확인 모달 열기
      setIsLoginConfirmOpen(true);
    }
  };

  /**
   * 로그인 확인 모달에서 확인 버튼 클릭 시 로그인 페이지로 이동
   */
  const handleLoginConfirmOk = () => {
    // 로그인 확인 모달 닫기
    setIsLoginConfirmOpen(false);
    // 로그인 페이지로 이동
    navigate('/login');
  };

  /**
   * 로그인 확인 모달에서 취소 버튼 클릭 시 모달 닫기
   */
  const handleLoginConfirmCancel = () => {
    // 로그인 확인 모달 닫기
    setIsLoginConfirmOpen(false);
  };

  /**
   * 삭제 버튼 클릭 시 처리
   */
  const handleDeleteOnClick = (commentNo: number) => {
    // 유저가 로그인하지 않은 경우
    if (!user) {
      // 로그인 확인 모달 열기
      setIsLoginConfirmOpen(true);
    } else {
      // 코멘트 번호를 Ref에저장
      commentNoRef.current = commentNo;
      // 삭제 확인 모달 열기
      setIsDeleteConfirmOpen(true);
    }
  };

  /**
   * 삭제 확인 모달에서 확인 버튼 클릭 시 코멘트 삭제
   */
  const handleDeleteConfirmOk = () => {
    //  삭제 확인 모달 닫기
    setIsDeleteConfirmOpen(false);
    // Ref에 저장된 코멘트 번호를 가져옴
    const commentNo = commentNoRef.current!;
    // 코멘트 삭제 함수 호출
    handleDeleteComment(commentNo);
  };

  /**
   * 삭제 확인 모달에서 취소 버튼 클릭 시 모달 닫기
   */
  const handleDeleteConfirmCancel = () => {
    // 삭제 확인 모달 닫기
    setIsDeleteConfirmOpen(false);
    // 코멘트 번호Ref 초기화
    commentNoRef.current = null;
  };

  /**
   * 코멘트 작성 버튼 클릭 시 처리
   */
  const handleSaveComment = () => {
    // 로딩 중이면 함수 종료
    if (isLoading) {
      return;
    }
    // 유저가 로그인하지 않은 경우
    if (!user) {
      // 로그인 확인 모달 열기
      setIsLoginConfirmOpen(true);
    } else {
      // 코멘트 저장 API 호출
      handleSubmit(async (data) => {
        await saveComment(data);
      })();
    }
  };

  /**
   * 코멘트 작성란의 수정 버튼 클릭 시 처리
   */
  const handleUpdateComment = () => {
    // 로딩 중이면 함수 종료
    if (isLoading) {
      return;
    }
    // 유저가 로그인하지 않은 경우
    if (!user) {
      // 로그인 확인 모달 열기
      setIsLoginConfirmOpen(true);
    } else {
      // 코멘트 수정 API 호출
      handleSubmit(async (data) => {
        await updateComment(data);
      })();
      // 코멘트 수정 가능 상태를 false로 변경
      setIsCommentEditable(false);
    }
  };

  /**
   * 코멘트 목록의 각 코멘트에 대한 수정 버튼 클릭 시 처리
   * @param commentData
   */
  const handleEditComment = (commentData: DetailCommentGetDataDto) => {
    // 유저가 로그인하지 않은 경우
    if (!user) {
      // 로그인 확인 모달 열기
      setIsLoginConfirmOpen(true);
    } else {
      // 아직 isCommentEditable가 안바뀌었으므로 false로 판정
      // 해당 코멘트관련 데이터 셋팅
      if (!isCommentEditable) {
        setValue('comment', commentData.comment!);
        setValue('starRating', commentData.starRating!);
        setCommentNo(commentData.commentNo!);
      }
      // 아직 isCommentEditable가 안바뀌었으므로 true로 판정
      // 셋팅 되었던 코멘트관련 데이터 삭제
      else {
        setValue('comment', '');
        setValue('starRating', 0);
        setCommentNo(undefined);
      }
      // 코멘트 수정 가능 상태 변경
      setIsCommentEditable(!isCommentEditable);
    }
  };

  /**
   * 코멘트 삭제 처리
   * @param commentNo 삭제할 코멘트 번호
   */
  const handleDeleteComment = async (commentNo: number) => {
    // 로딩 중이면 함수 종료
    if (isLoading) {
      return;
    }
    // 유저가 로그인하지 않은 경우
    if (!user) {
      // 로그인 확인 모달 열기
      setIsLoginConfirmOpen(true);
    } else {
      // 코멘트 삭제 API 호출
      await deleteComment(commentNo);
      // 로그인한 유저의 코멘트 여부 상태를 false로 변경
      setIsMyComment(false);
      // 코멘트 수정 가능 상태를 false로 변경
      setIsCommentEditable(false);
    }
  };

  /**
   * 무한 스크롤 기능을 구현하기 위한 IntersectionObserver 콜백 함수
   * observeTarget가 화면에 나타나면 observerCallback이 호출되어 fetchNextPage를 호출
   * @param entries 관찰 대상의 교차 상태를 나타내는 IntersectionObserverEntry 배열
   */
  const observerCallback = useCallback<IntersectionObserverCallback>(
    (entries) => {
      entries.forEach((entry) => {
        // observeTarget이 화면에 나타나고, 다음 페이지가 있고, 현재 페이지를 가져오고 있지 않고, 로딩중이 아닌 경우
        if (
          entry.isIntersecting &&
          hasNextPage &&
          !isFetchingNextPage &&
          !isLoading
        ) {
          console.log('★★★fetchNextPage실행!!!!!!!!!★★★');
          // fetchNextPage를 호출
          fetchNextPage();
        }
      });
    },
    [hasNextPage, isFetchingNextPage, fetchNextPage, isLoading]
  );

  // ================================================================================================== useEffect

  /**
   * 무한 스크롤 기능을 구현하기 위한 useEffect
   * observeTarget이 화면에 나타나면 observerCallback이 호출되어 fetchNextPage를 호출하여 무한 스크롤 기능을 구현
   */
  useEffect(() => {
    // observeTarget이 null이거나 hasNextPage가 false이거나 isFetchingNextPage가 true인 경우에는 관찰을 중지
    if (!observeTarget || !hasNextPage || isFetchingNextPage) {
      return;
    }
    // 새로운 IntersectionObserver를 생성
    // observerCallback을 사용하여 observeTarget이 화면에 나타날 때 fetchNextPage를 호출
    const observer = new IntersectionObserver(observerCallback, {
      threshold: 0.1,
    });

    // observeTarget이 화면에 보이면 관찰을 시작
    observer.observe(observeTarget);

    // observeTarget이 변경되면 이전에 관찰하던 타겟은 관찰을 중지
    return () => {
      observer.unobserve(observeTarget);
    };
  }, [observeTarget, hasNextPage, isFetchingNextPage, observerCallback]);

  /**
   * 컴포넌트가 마운트되면, 유저가 로그인한 경우에만 코멘트 작성란에 포커스를 주도록 설정
   * 컴포넌트가 렌더링된 직후에 포커스를 주면 브라우저가 아직 DOM을 완전히 렌더링하지 않았기 때문에,
   * 포커스가 제대로 적용되지 않을 수 있어, setTimeout을 사용하여 포커스를 주는 타이밍을 조정하도록 처리
   * 유저가 로그인한 상태에서 코멘트 작성란에 포커스를 주어 코멘트를 작성할 수 있도록 유도
   */
  useEffect(() => {
    // 유저가 로그인한 경우
    if (user) {
      try {
        const timeout = setTimeout(
          () =>
            // 코멘트 작성란에 포커스를 주도록 설정
            textAreaRef.current?.focus(),
          50
        );
        return () => clearTimeout(timeout);
      } catch (error) {
        handleUnExceptedError(error);
      }
    }
  }, [user]);

  /**
   * useInfiniteQuery의 data가 변경될 때마다 로그인한 유저의 코멘트 여부 상태를 업데이트
   * 이전 페이지가 없는 경우(첫번째 페이지)에는 코멘트가 존재하는지 여부를 확인하여 로그인한 유저의 코멘트 여부 상태를 설정
   */
  useEffect(() => {
    // 유저정보가 없는 경우
    if (!user) {
      // 로그인한 유저의 코멘트 여부 상태를 false로 변경
      setIsMyComment(false);
    }
    // 유저정보 && API 코멘트 데이터 && 이전 페이지가 없는 경우(첫번째 페이지)
    if (user && data && !hasPreviousPage) {
      // 로그인한 유저의 코멘트 여부 상태를 업데이트
      const isCommentExist = data?.pages
        .flat()
        .find((items) => items?.userId === user?.id);
      setIsMyComment(!!isCommentExist);
    }
  }, [data, hasPreviousPage, user]);

  // ================================================================================================== return

  return {
    control: control,
    handleCommentOnClick: handleCommentOnClick,
    handleSaveComment: handleSaveComment,
    handleEditComment: handleEditComment,
    handleUpdateComment: handleUpdateComment,
    data: data,
    isLoading: isLoading,
    hasNextPage: hasNextPage,
    isFetchingNextPage: isFetchingNextPage,
    setObserveTarget: setObserveTarget,
    totalElements: totalElements,
    isLoginConfirmOpen: isLoginConfirmOpen,
    handleLoginConfirmOk: handleLoginConfirmOk,
    handleLoginConfirmCancel: handleLoginConfirmCancel,
    textAreaRef: textAreaRef,
    isMyComment: isMyComment,
    comment: comment,
    isCommentEditable: isCommentEditable,
    isDeleteConfirmOpen: isDeleteConfirmOpen,
    handleDeleteOnClick: handleDeleteOnClick,
    handleDeleteConfirmOk: handleDeleteConfirmOk,
    handleDeleteConfirmCancel: handleDeleteConfirmCancel,
    starRatingErrorMsg: errors.starRating?.message,
  };
};
