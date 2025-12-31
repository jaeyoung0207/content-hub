import { useEffect, useRef, useState } from 'react';
import { WishlistUiPropsType } from '../WishlistUi';
import { WishlistApi } from '@/api/WishlistApi';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { wishlistQueryKeys } from '@/components/features/wishlist/queryKeys/wishlistQueryKeys';
import { toast } from 'react-toastify';
import { throttle } from 'lodash-es';
import { useNavigate } from 'react-router-dom';
import { loginConfirmDialog } from '@/components/common/utils/loginUtil';
import { useTranslation } from 'react-i18next';
import {
  getDisplayMediaTypeName,
  mappingToMediaType,
} from '@/components/common/utils/convertUtil';
import { MEDIA_TYPE_KIND } from '@/components/common/constants/constants';
import { useConfirmDialogStore } from '@/components/common/store/globalStateStore';
import { searchQueryKeys } from '@/components/features/search/queryKeys/searchQueryKeys';
import { detailQueryKeys } from '@/components/features/detail/queryKeys/detailQueryKeys';
import { homeQueryKeys } from '@/components/features/home/queryKeys/homeQueryKeys';

/**
 * 위시리스트 훅 반환 타입
 */
type UseWishlistReturnType = {
  handleOnClickHeart: () => void;
  isExecuting: boolean;
};

/**
 * 위시리스트 훅
 */
export const useWishlistUi = ({
  isWishlisted,
  userId,
  contentMediaType,
  apiId,
  title,
  thumbnailImageUrl,
  genreIds,
  displayMediaType,
}: WishlistUiPropsType): UseWishlistReturnType => {
  // ================================================================================================== react hook

  // queryClient 훅
  const queryClient = useQueryClient();

  // navigate 훅
  const navigate = useNavigate();

  // i18n 훅
  const { t } = useTranslation();

  // Wishlist API 인스턴스
  const wishlistApi = new WishlistApi();

  // 최신 isWishlisted를 읽기 위한 ref (throttle 클로저 최신값 보장)
  const isWishlistedRef = useRef<boolean>(isWishlisted);

  // 실행 중 상태
  const [isExecuting, setIsExecuting] = useState(false);

  // ================================================================================================== zustand

  // 확인 다이얼로그 상태 관리 훅
  const {
    setIsConfirmDialogOpen,
    setConfirmMsg,
    setOnOk,
    setTitle,
    setOnCancel,
  } = useConfirmDialogStore();

  // ================================================================================================== mutation

  /**
   * 위시리스트 추가 뮤테이션
   */
  const addWishlistMutation = useMutation({
    mutationKey: wishlistQueryKeys.wishlist.add(userId!),
    mutationFn: async () =>
      (
        await wishlistApi.saveWishlist({
          userId: userId!,
          contentMediaType: contentMediaType,
          apiId: String(apiId),
          title: title,
          thumbnailImageUrl: thumbnailImageUrl,
          genreIds: genreIds ?? [],
          displayMediaType: displayMediaType,
        })
      ).data,
    onSuccess: (res) => {
      if (res) {
        toast.success(t('info.addedToWishlist', { title: title }), {
          autoClose: 1000,
          style: { whiteSpace: 'pre-line' },
        });
        return;
      }
      toast.warning(t('info.existsInWishlist', { title: title }), {
        autoClose: 1000,
        style: { whiteSpace: 'pre-line' },
      });
    },
    onError: () => {
      toast.error(t('error.failedToAddWishlist', { title: title }), {
        toastId: 'wishlist_add_error_' + title,
      });
    },
    onSettled: () => {
      // 실행 중 상태 해제
      setIsExecuting(false);
      // 관련 쿼리 키 무효화
      invalidateQueries();
    },
  });

  /**
   * 위시리스트 삭제 뮤테이션
   */
  const deleteWishlistMutation = useMutation({
    mutationKey: wishlistQueryKeys.wishlist.delete(userId!),
    mutationFn: async () =>
      (
        await wishlistApi.deleteWishlist({
          userId: userId!,
          contentMediaType: contentMediaType,
          apiId: String(apiId),
        })
      ).data,
    onSuccess: (res) => {
      if (res) {
        toast.success(t('info.removedFromWishlist', { title: title }), {
          autoClose: 1000,
          style: { whiteSpace: 'pre-line' },
        });
        return;
      }
      toast.warning(t('info.notExistsInWishlist', { title: title }), {
        autoClose: 1000,
        style: { whiteSpace: 'pre-line' },
      });
    },
    onError: () => {
      toast.error(t('error.failedToRemoveWishlist', { title: title }), {
        toastId: 'wishlist_remove_error_' + title,
      });
    },
    onSettled: () => {
      // 실행 중 상태 해제
      setIsExecuting(false);
      // 관련 쿼리 키 무효화
      invalidateQueries();
    },
  });

  /**
   * 모든 위시리스트 관련 화면 쿼리 무효화 함수
   */
  const invalidateQueries = () => {
    // 위시리스트 관련 쿼리 무효화
    queryClient.invalidateQueries({
      queryKey: wishlistQueryKeys.all,
    });
    // 홈 화면 관련 쿼리 무효화
    queryClient.invalidateQueries({
      queryKey: homeQueryKeys.all,
    });
    // 검색 화면 관련 쿼리 무효화
    queryClient.invalidateQueries({
      queryKey: searchQueryKeys.all,
    });
    // 상세 화면 관련 쿼리 무효화
    queryClient.invalidateQueries({
      queryKey: detailQueryKeys.all,
    });
  };

  /**
   * 위시리스트 존재 여부 확인 함수
   */
  const checkWishlist = async () => {
    return await queryClient.fetchQuery({
      queryKey: wishlistQueryKeys.wishlist.exists(
        userId!,
        contentMediaType,
        apiId
      ),
      queryFn: async () => {
        return (
          await wishlistApi.checkWishlist({
            user_id: userId!,
            api_id: String(apiId),
            content_media_type: contentMediaType,
          })
        ).data;
      },
    });
  };

  // ================================================================================================== function

  /**
   * 하트 아이콘 클릭 핸들러
   * 스로틀을 적용하여 중복 클릭 방지
   * - 표시 상태는 상위 isWishlisted만 사용
   * - 최신 isWishlisted는 ref에서 읽음(스로틀 클로저 신선도 보장)
   */
  const handleOnClickHeart = throttle(() => {
    if (!userId) {
      // 로그인 확인 다이얼로그 표시
      loginConfirmDialog('info.loginConfirmMsg2', navigate);
      return;
    }
    // 이미 실행 중인 경우 처리 중지
    if (isExecuting) {
      return;
    }
    // 실행 중 상태 설정
    setIsExecuting(true);
    // 위시리스트 참조값에 따른 처리
    if (isWishlistedRef.current) {
      deleteWishlistMutation.mutate();
    } else {
      checkWishlist().then((res) => {
        // 위시리스트 등록 가능 항목 수 초과
        if (res.maxWishlistCount) {
          toast.warn(
            t('warn.tooManyWishlistEntries', {
              maxEntries: res.maxWishlistCount,
            })
          );
        }
        // 이미 위시리스트에 존재하는지 확인
        else if (res.wishlists && res.wishlists.length > 0) {
          const displayMediaTypeNames: string[] = [];
          res.wishlists.forEach((items) => {
            if (items.displayMediaType) {
              displayMediaTypeNames.push(
                t(getDisplayMediaTypeName(items.displayMediaType)!) ?? ''
              );
            }
          });
          // 이미 등록된 화면 표시용 미디어 타입이 존재하는 경우 확인 다이얼로그 표시
          const dialogMessage =
            displayMediaTypeNames.join(', ') +
            '에 등록되어 있는 작품입니다. \r\n' +
            t(
              getDisplayMediaTypeName(
                mappingToMediaType(
                  contentMediaType,
                  MEDIA_TYPE_KIND.DISPLAY_MEDIA_TYPE
                )!
              )!
            ) +
            '에도 등록하시겠습니까?';
          // 확인 다이얼로그 표시
          setIsConfirmDialogOpen(true);
          // 확인 다이얼로그 타이틀 설정
          setTitle(t('info.wishlistAddConfirmTitle'));
          // 확인 다이얼로그 메시지 설정
          setConfirmMsg(dialogMessage);
          // 확인 다이얼로그 확인 버튼 핸들러 설정
          setOnOk(() => {
            setIsConfirmDialogOpen(false);
            addWishlistMutation.mutate();
          });
          // 확인 다이얼로그 취소 버튼 핸들러 설정
          setOnCancel(() => {
            setIsConfirmDialogOpen(false);
            setIsExecuting(false);
          });
        }
        // 위시리스트에 없는 경우 바로 추가
        else {
          addWishlistMutation.mutate();
        }
      });
    }
  }, 500);

  // ================================================================================================== useEffect

  /**
   * isWishlisted prop이 변경될 때 참조값을 동기화
   */
  useEffect(() => {
    isWishlistedRef.current = isWishlisted;
  }, [isWishlisted]);

  /**
   * 컴포넌트 언마운트 시에 handleOnClickHeart 잔여 작업을 정리
   * - 스로틀 타이머 클리어(타이머/지연 호출이 누적되는 문제 방지)
   */
  useEffect(() => {
    return () => {
      handleOnClickHeart.cancel();
    };
  }, [handleOnClickHeart]);

  // ================================================================================================== return

  return {
    handleOnClickHeart: handleOnClickHeart,
    isExecuting: isExecuting,
  };
};
