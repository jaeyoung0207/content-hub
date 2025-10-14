import { keepPreviousData, UseInfiniteQueryOptions, UseQueryOptions } from '@tanstack/react-query';
import { ONE_MINUTE } from '../constants/constants';

/**
 * 마운트 시 항상 재조회 + 깜빡임 최소화 프리셋
 * - 실시간성이 중요한 화면에 사용
 * - queryKey/queryFn은 제외
 */
export const freshOnMountOptions = {
    staleTime: 0, // 항상 stale
    gcTime: ONE_MINUTE * 5, // 캐시는 5분 보존(뒤로가기 UX 개선)
    refetchOnMount: 'always', // 마운트 때는 항상 재조회
    refetchOnWindowFocus: false, // window focus 시 재조회 안함
    placeholderData: keepPreviousData, // 이전 데이터 유지로 깜빡임 최소화
  } satisfies Omit<UseQueryOptions<any, any, any, any>, 'queryKey' | 'queryFn'>;

/**
 * 일반 리스트/상세에 무난한 캐시형 프리셋
 * - 실시간성이 덜 중요한 화면에 사용
 * - queryKey/queryFn은 제외
 */
export const cachedListOptions = {
  staleTime: ONE_MINUTE,
  gcTime: ONE_MINUTE * 5,
  refetchOnMount: false,
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData
} satisfies Omit<UseQueryOptions<any, any, any, any>, 'queryKey' | 'queryFn'>;

/**
 * 무한 스크롤용 프리셋
 * - queryKey/queryFn/getNextPageParam/initialPageParam은 제외
 */
export const freshOnMountInfiniteOptions = {
    staleTime: 0,
    gcTime: ONE_MINUTE * 5,
    refetchOnWindowFocus: false,
  } satisfies Omit<
    UseInfiniteQueryOptions<any, any, any, any, any>,
    'queryKey' | 'queryFn' | 'getNextPageParam' | 'initialPageParam'
  >;