import { useQueryClient } from '@tanstack/react-query';
import { useEffect } from 'react';
import { headerQueryKeys } from '../queryKeys/headerQueryKeys';
import { CommonApi } from '@/api/CommonApi';
import {
  useContentMediaTypeMapStore,
  useDisplayMediaTypeMapStore,
} from '@/components/common/store/globalStateStore';

export const useHeaderInitialize = () => {
  // ================================================================================================== zustand

  // 컨텐츠 미디어 타입 맵 상태관리
  const { setContentMediaType } = useContentMediaTypeMapStore();
  // 화면 표시용 미디어 타입 맵 상태관리
  const { setDisplayMediaType } = useDisplayMediaTypeMapStore();

  // ================================================================================================== react query

  // react query 클라이언트 훅
  const queryClient = useQueryClient();

  // 공통 API 인스턴스 생성
  const commonApi = new CommonApi();

  /**
   * CSRF 토큰 초기화 함수
   */
  const getCsrfToken = async () => {
    // csrf token 초기화 API 호출
    queryClient.fetchQuery({
      queryKey: headerQueryKeys.getCsrfToken(),
      queryFn: async () => {
        return await commonApi.getCsrfToken();
      },
    });
  };

  /**
   * 미디어 타입 초기화 함수
   */
  const getMediaTypes = async () => {
    queryClient.fetchQuery({
      queryKey: headerQueryKeys.getMediaTypes(),
      queryFn: async () => {
        const mediaTypes = (await commonApi.getMediaTypes()).data;
        setContentMediaType(mediaTypes.contentMediaTypeDto, true);
        setDisplayMediaType(mediaTypes.displayMediaTypeDto, true);
        return mediaTypes;
      },
    });
  };

  // ================================================================================================== function

  /**
   * 컴포넌트 마운트 시 초기화 작업 수행
   */
  /* eslint-disable react-hooks/exhaustive-deps */
  useEffect(() => {
    getCsrfToken();
    getMediaTypes();
  }, []);
};
