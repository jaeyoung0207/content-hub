import { act, renderHook, waitFor } from '@testing-library/react';
import { useSearchMore } from './useSearchMore';
import { beforeEach, afterEach, describe, expect, it, vi } from 'vitest';
import { isSearchTvType } from '@/components/common/utils/typeGuardUtil';
import { MemoryRouter } from 'react-router-dom';

import { SearchApi } from '@/api/SearchApi';
import { AxiosHeaders, InternalAxiosRequestConfig } from 'axios';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import {
  AppContentMediaTypeDto,
  AppDisplayMediaTypeDto,
  SearchComicsResultDto,
  SearchMovieResultsDto,
  SearchTvResultsDto,
} from '@/api/data-contracts';

// 테스트용 QueryClient 생성 함수
const queryClientMock = () =>
  new QueryClient({
    defaultOptions: {
      queries: {
        retry: false,
      },
    },
  });

// SearchApi의 searchAni 메서드를 mock 처리
vi.mock('@/api/SearchApi', () => {
  return {
    SearchApi: class MockSearchApi {
      searchAni() {}
      searchTvExceptAni() {}
      searchMovie() {}
      searchComics() {}
    },
  };
});

// 화면 표시용 미디어 타입
const displayMediaType = {
  aniCode: '1',
  dramaCode: '2',
  documentaryCode: '3',
  kidsCode: '4',
  newsCode: '5',
  varietyCode: '6',
  movieCode: '7',
  personCode: '8',
  comicsCode: '9',
} as AppDisplayMediaTypeDto;

// 컨텐츠 미디어 타입
const contentMediaType = {
  aniCode: '1101',
  dramaCode: '1102',
  documentaryCode: '1103',
  kidsCode: '1104',
  newsCode: '1105',
  varietyCode: '1106',
  movieCode: '1201',
  personCode: '1301',
  comicsCode: '2101',
} as AppContentMediaTypeDto;

// 화면 표시용 미디어 타입을 미디어 이름으로 매핑하는 함수
const displayMediaTypeMap = (mediaType: string) => {
  const map: Record<string, string> = {
    [displayMediaType.aniCode]: '애니:진격의 거인',
    [displayMediaType.dramaCode]: '드라마:왕좌의 게임',
    [displayMediaType.documentaryCode]: '다큐멘터리:정글 동물 구조대',
    [displayMediaType.kidsCode]: '키즈:뽀로로',
    [displayMediaType.newsCode]: '뉴스:MBC 뉴스데스크',
    [displayMediaType.varietyCode]: '예능:런닝맨',
    [displayMediaType.movieCode]: '영화:어벤져스',
    [displayMediaType.personCode]: '인물:이제훈',
    [displayMediaType.comicsCode]: '만화:귀멸의 칼날',
  };
  return map[mediaType] || '알수없음';
};

// 화면용 미디어 타입 <-> 컨텐츠 미디어 타입 매핑 함수
const mediaTypeMap = (mediaType: string) => {
  const map: Record<string, string> = {
    [displayMediaType.aniCode]: contentMediaType.aniCode,
    [displayMediaType.dramaCode]: contentMediaType.dramaCode,
    [displayMediaType.documentaryCode]: contentMediaType.documentaryCode,
    [displayMediaType.kidsCode]: contentMediaType.kidsCode,
    [displayMediaType.newsCode]: contentMediaType.newsCode,
    [displayMediaType.varietyCode]: contentMediaType.varietyCode,
    [displayMediaType.movieCode]: contentMediaType.movieCode,
    [displayMediaType.personCode]: contentMediaType.personCode,
    [displayMediaType.comicsCode]: contentMediaType.comicsCode,

    [contentMediaType.aniCode]: displayMediaType.aniCode,
    [contentMediaType.dramaCode]: displayMediaType.dramaCode,
    [contentMediaType.documentaryCode]: displayMediaType.documentaryCode,
    [contentMediaType.kidsCode]: displayMediaType.kidsCode,
    [contentMediaType.newsCode]: displayMediaType.newsCode,
    [contentMediaType.varietyCode]: displayMediaType.varietyCode,
    [contentMediaType.movieCode]: displayMediaType.movieCode,
    [contentMediaType.personCode]: displayMediaType.personCode,
    [contentMediaType.comicsCode]: displayMediaType.comicsCode,
  };
  return map[mediaType] || mediaType;
};

const setSearchParamsMock = vi.fn();
vi.mock('react-router-dom', async (importOriginal) => {
  const actual = await importOriginal<typeof import('react-router-dom')>();
  return {
    ...actual, // 기존 모듈의 모든 내보내기 유지
    useSearchParams: () => [
      new URLSearchParams('viewMore=1'),
      setSearchParamsMock,
    ],
  };
});

// convertUtil 모듈의 함수들을 mock 처리
vi.mock('@/components/common/utils/convertUtil', async (importOriginal) => {
  const actual =
    await importOriginal<
      typeof import('@/components/common/utils/convertUtil')
    >();
  return {
    ...actual, // 기존 모듈의 모든 내보내기 유지
    getDisplayMediaType: vi.fn().mockImplementation(() => displayMediaType),
    getContentMediaType: vi.fn().mockImplementation(() => contentMediaType),
    mappingToMediaType: vi.fn().mockImplementation((mediaType: string) => {
      return mediaTypeMap(mediaType);
    }),
  };
});

// IntersectionObserver 모킹을 위한 전역 변수
let observerInstance: IntersectionObserverMock[] = [];

// IntersectionObserver 모킹 클래스
class IntersectionObserverMock {
  callback: IntersectionObserverCallback;
  observe: ReturnType<typeof vi.fn>;
  unobserve: ReturnType<typeof vi.fn>;
  disconnect: ReturnType<typeof vi.fn>;

  constructor(callback: IntersectionObserverCallback) {
    this.callback = callback;
    this.observe = vi.fn();
    this.unobserve = vi.fn();
    this.disconnect = vi.fn();
    // 생성된 인스턴스를 추적하기 위해 배열에 추가
    observerInstance.push(this);
  }

  // 테스트에서 교차 상태를 트리거하는 메서드
  triggerIntersect(isIntersecting: boolean) {
    this.callback(
      [{ isIntersecting } as IntersectionObserverEntry],
      this as unknown as IntersectionObserver
    );
  }
}
// 전역 IntersectionObserver를 모킹 클래스로 대체
vi.stubGlobal('IntersectionObserver', IntersectionObserverMock);

// 각 테스트 전에 실행되는 설정
// let queryClient: QueryClient;
beforeEach(() => {
  observerInstance = [];
  // 새로운 QueryClient 인스턴스 생성
  queryClientMock();
  // 모킹 초기화
  vi.clearAllMocks();
});

// 각 테스트 후에 실행되는 정리
afterEach(() => {
  document.body.style.removeProperty('overflow');
});

// 커스텀 훅을 렌더링하기 위한 래퍼 컴포넌트
// react-router 훅을 쓰는 모든 컴포넌트/훅 테스트는 반드시 Router context로 감싸야 함
const wrapper = ({ children }: { children: React.ReactNode }) => (
  <QueryClientProvider client={queryClientMock()}>
    <MemoryRouter>{children}</MemoryRouter>
  </QueryClientProvider>
);

// useSearchMore 훅 테스트
describe('useSearchMore', () => {
  describe('test_judgeExecApi_differentMediaTypes', () => {
    it('애니메이션 검색 API 호출 시 결과를 반환', async () => {
      // 애니메이션 이름
      const aniName = '진격의 거인';
      // 메서드 스파이 설정
      const searchApispy = vi.spyOn(SearchApi.prototype, 'searchAni');
      searchApispy.mockResolvedValue({
        data: {
          page: 1,
          totalPages: 2,
          aniResults: [{ id: 1, name: aniName } as SearchTvResultsDto],
        },
        headers: new AxiosHeaders(),
        status: 200,
        statusText: 'OK',
        config: {} as InternalAxiosRequestConfig,
      });

      // useSearchMore 훅 렌더링
      const { result } = renderHook(
        () => useSearchMore(aniName, false, displayMediaType.aniCode),
        { wrapper } // 래퍼 컴포넌트 지정
      );

      // 비동기 작업 완료 대기
      await waitFor(() => {
        expect(result.current.data).toBeDefined();
        expect(result.current.data?.pages).toHaveLength(1);
      });
      // judgeExecApi는 내부에서 호출되므로, data가 정상적으로 들어왔는지
      const aniData = result.current.data?.pages[0][0];
      expect(aniData).toBeDefined();
      // 반환된 데이터의 이름이 기대한 값과 일치하는지 확인
      if (isSearchTvType(aniData!, displayMediaType.aniCode) && aniData.name) {
        expect(aniData.name).toBe(aniName);
      }
      // searchAni API가 한 번 호출되었는지 확인
      expect(searchApispy).toHaveBeenCalledTimes(1);
      // searchAni API가 올바른 매개변수로 호출되었는지 확인
      expect(searchApispy).toHaveBeenCalledWith(
        {
          page: 1,
          keyword: aniName,
          user_id: undefined,
        },
        {}
      );
    });

    Object.values(displayMediaType).map((mediaType) => {
      if (
        mediaType === displayMediaType.aniCode ||
        mediaType === displayMediaType.movieCode ||
        // || mediaType === displayMediaType.personCode
        mediaType === displayMediaType.comicsCode
      )
        return;

      const displayMediaTypeValues = displayMediaTypeMap(mediaType).split(':');
      const media = displayMediaTypeValues[0];
      const title = displayMediaTypeValues[1];

      it(
        '애니메이션 이외 TV 시리즈 검색 API 호출 시 결과를 반환 (' +
          media +
          ')',
        async () => {
          // 메서드 스파이 설정
          const searchApispy = vi.spyOn(
            SearchApi.prototype,
            'searchTvExceptAni'
          );
          searchApispy.mockResolvedValue({
            data: {
              page: 1,
              totalPages: 1,
              dramaResults: [
                { id: Number(mediaType), name: title } as SearchTvResultsDto,
              ],
              documentaryResults: [
                { id: Number(mediaType), name: title } as SearchTvResultsDto,
              ],
              kidsResults: [
                { id: Number(mediaType), name: title } as SearchTvResultsDto,
              ],
              newsResults: [
                { id: Number(mediaType), name: title } as SearchTvResultsDto,
              ],
              varietyResults: [
                { id: Number(mediaType), name: title } as SearchTvResultsDto,
              ],
            },
            headers: new AxiosHeaders(),
            status: 200,
            statusText: 'OK',
            config: {} as InternalAxiosRequestConfig,
          });
          // useSearchMore 훅 렌더링
          const { result } = renderHook(
            () => useSearchMore(title, false, mediaType),
            { wrapper } // 래퍼 컴포넌트 지정
          );

          // 인물인 경우 null 반환 확인 후 종료
          if (mediaType === displayMediaType.personCode) {
            expect(result.current.data).toBeUndefined();
            return;
          }

          // 비동기 작업 완료 대기
          await waitFor(() => {
            expect(result.current.data).toBeDefined();
            expect(result.current.data?.pages).toHaveLength(1);
          });
          // judgeExecApi는 내부에서 호출되므로, data가 정상적으로 들어왔는지
          const contentData = result.current.data?.pages[0][0];
          expect(contentData).toBeDefined();
          // 반환된 데이터의 이름이 기대한 값과 일치하는지 확인
          if (isSearchTvType(contentData!, mediaType) && contentData.name) {
            expect(contentData.name).toBe(title);
          }
          // searchTvExceptAni API가 한 번 호출되었는지 확인
          expect(searchApispy).toHaveBeenCalledTimes(1);
          // searchTvExceptAni API가 올바른 매개변수로 호출되었는지 확인
          expect(searchApispy).toHaveBeenCalledWith(
            {
              keyword: title,
              content_media_type: mediaTypeMap(mediaType),
              page: 1,
              user_id: undefined,
            },
            {}
          );
        }
      );
    });

    it('영화 검색 API 호출 시 결과를 반환', async () => {
      // 영화 이름
      const movieName = '어벤져스';
      // 메서드 스파이 설정
      const searchApispy = vi.spyOn(SearchApi.prototype, 'searchMovie');
      searchApispy.mockResolvedValue({
        data: {
          page: 1,
          totalPages: 1,
          movieResults: [{ id: 7, name: movieName } as SearchMovieResultsDto],
        },
        headers: new AxiosHeaders(),
        status: 200,
        statusText: 'OK',
        config: {} as InternalAxiosRequestConfig,
      });

      // useSearchMore 훅 렌더링
      const { result } = renderHook(
        () => useSearchMore(movieName, false, displayMediaType.movieCode),
        { wrapper } // 래퍼 컴포넌트 지정
      );

      // 비동기 작업 완료 대기
      await waitFor(() => {
        expect(result.current.data).toBeDefined();
        expect(result.current.data?.pages).toHaveLength(1);
      });
      // judgeExecApi는 내부에서 호출되므로, data가 정상적으로 들어왔는지
      const movieData = result.current.data?.pages[0][0];
      expect(movieData).toBeDefined();
      // 반환된 데이터의 이름이 기대한 값과 일치하는지 확인
      if (
        isSearchTvType(movieData!, displayMediaType.movieCode) &&
        movieData.name
      ) {
        expect(movieData.name).toBe(movieName);
      }
      // searchMovie API가 한 번 호출되었는지 확인
      expect(searchApispy).toHaveBeenCalledTimes(1);
      // searchMovie API가 올바른 매개변수로 호출되었는지 확인
      expect(searchApispy).toHaveBeenCalledWith(
        {
          page: 1,
          keyword: movieName,
          user_id: undefined,
        },
        {}
      );
    });

    it('만화 검색 API 호출 시 결과를 반환', async () => {
      // 만화 이름
      const comicsName = '귀멸의 칼날';
      // 메서드 스파이 설정
      const searchApispy = vi.spyOn(SearchApi.prototype, 'searchComics');
      searchApispy.mockResolvedValue({
        data: {
          page: 1,
          totalPages: 1,
          comicsResults: [{ id: 9, name: comicsName } as SearchComicsResultDto],
        },
        headers: new AxiosHeaders(),
        status: 200,
        statusText: 'OK',
        config: {} as InternalAxiosRequestConfig,
      });

      // useSearchMore 훅 렌더링
      const { result } = renderHook(
        () => useSearchMore(comicsName, false, displayMediaType.comicsCode),
        { wrapper } // 래퍼 컴포넌트 지정
      );

      // 비동기 작업 완료 대기
      await waitFor(() => {
        expect(result.current.data).toBeDefined();
        expect(result.current.data?.pages).toHaveLength(1);
      });
      // judgeExecApi는 내부에서 호출되므로, data가 정상적으로 들어왔는지
      const comicsData = result.current.data?.pages[0][0];
      expect(comicsData).toBeDefined();
      // 반환된 데이터의 이름이 기대한 값과 일치하는지 확인
      if (
        isSearchTvType(comicsData!, displayMediaType.comicsCode) &&
        comicsData.name
      ) {
        expect(comicsData.name).toBe(comicsName);
      }
      // searchComics API가 한 번 호출되었는지 확인
      expect(searchApispy).toHaveBeenCalledTimes(1);
      // searchComics API가 올바른 매개변수로 호출되었는지 확인
      expect(searchApispy).toHaveBeenCalledWith(
        {
          page: 1,
          keyword: comicsName,
          is_main_page: false,
          user_id: undefined,
        },
        {}
      );
    });

    it('존재하지 않는 화면용 미디어 타입', async () => {
      // useSearchMore 훅 렌더링
      const { result } = renderHook(
        () => useSearchMore('작품명', false, '99'), // 존재하지 않는 미디어 타입
        { wrapper } // 래퍼 컴포넌트 지정
      );
      // 비동기 작업 완료 대기
      await waitFor(() => {
        expect(result.current.data).toBeUndefined();
      });
      // data가 undefined인지 확인
      expect(result.current.data).toBeUndefined();
    });
  });

  describe('test_useEffect_infiniteScroll', () => {
    it('무한 스크롤로 다음 페이지 로드 시 데이터가 추가로 로드됨', async () => {
      const aniName = '귀멸의 칼날';
      // 메서드 스파이 설정
      const searchApispy = vi.spyOn(SearchApi.prototype, 'searchAni');
      // 1페이지 응답 모킹
      searchApispy
        .mockResolvedValueOnce({
          data: {
            page: 1,
            totalPages: 2,
            aniResults: [
              { id: 1, name: aniName + ' 무한열차편' } as SearchTvResultsDto,
            ],
          },
          headers: new AxiosHeaders(),
          status: 200,
          statusText: 'OK',
          config: {} as InternalAxiosRequestConfig,
        })
        // 2페이지 응답 모킹
        .mockResolvedValueOnce({
          data: {
            page: 2,
            totalPages: 2,
            aniResults: [
              { id: 2, name: aniName + ' 무한성편' } as SearchTvResultsDto,
            ],
          },
          headers: new AxiosHeaders(),
          status: 200,
          statusText: 'OK',
          config: {} as InternalAxiosRequestConfig,
        });

      // useSearchMore 훅 렌더링
      const { result } = renderHook(
        () => useSearchMore(aniName, false, displayMediaType.aniCode),
        { wrapper } // 래퍼 컴포넌트 지정
      );
      // 1페이지 로드 대기
      await waitFor(() => {
        expect(result.current.data).toBeDefined();
        expect(result.current.data?.pages).toHaveLength(1);
      });
      // 관찰 대상 설정
      const observerTarget = document.createElement('div');
      act(() => {
        result.current.setObserveTarget(observerTarget);
      });

      // observe가 호출될 때까지 대기
      await waitFor(() => {
        expect(observerInstance.length).toBeGreaterThan(0);
        expect(
          observerInstance[observerInstance.length - 1].observe
        ).toHaveBeenCalled();
      });
      // 마지막으로 생성된 IntersectionObserver 인스턴스 가져오기
      const lastObserver = observerInstance[observerInstance.length - 1];

      // 교차 상태 트리거하여 다음 페이지 로드 유발
      act(() => {
        lastObserver.triggerIntersect(true);
      });
      // 2페이지 로드 확인
      await waitFor(() => {
        expect(result.current.data?.pages).toHaveLength(2);
      });

      // 전체 데이터 개수가 2개인지 확인
      const allData = result.current.data?.pages.flat();
      expect(allData).toHaveLength(2);
      if (
        allData &&
        isSearchTvType(allData[0]!, displayMediaType.aniCode) &&
        allData[0].name
      ) {
        expect(allData[0].name).toBe(aniName + ' 무한열차편');
      }
      if (
        allData &&
        isSearchTvType(allData[1]!, displayMediaType.aniCode) &&
        allData[1].name
      ) {
        expect(allData[1].name).toBe(aniName + ' 무한성편');
      }
      // searchAni API 호출 횟수 확인
      expect(searchApispy).toHaveBeenCalledTimes(2);
    });
  });

  describe('test_handleModalClose_overlayClickAndEscKey', () => {
    it('onOverlayClick이 호출될 때 handleModalClose이 호출됨', () => {
      const aniName = '진격의 거인';
      const { result } = renderHook(
        () => useSearchMore(aniName, false, displayMediaType.aniCode),
        { wrapper } // 래퍼 컴포넌트 지정
      );
      // 모달 닫기 핸들러 호출을 위한 가짜 이벤트 객체 생성
      const mockTarget = document.createElement('div');
      const event = {
        currentTarget: mockTarget,
        target: mockTarget,
      } as unknown as React.MouseEvent<HTMLDivElement>;

      // 오버레이(모달 바깥 영역) 클릭 시 모달 닫기 함수 호출
      act(() => {
        result.current.onOverlayClick(event);
      });
      // setSearchParams가 viewMore 파라미터를 제거하는지 확인
      expect(setSearchParamsMock).toHaveBeenCalled();
      // 호출된 setSearchParams의 인자를 확인
      const calledParams = setSearchParamsMock.mock.calls[0][0];
      expect(calledParams.has('viewMore')).toBe(false);
    });

    it('ESC키 입력 시 handleModalClose이 호출됨', () => {
      const aniName = '진격의 거인';
      renderHook(
        () => useSearchMore(aniName, false, displayMediaType.aniCode),
        { wrapper } // 래퍼 컴포넌트 지정
      );
      // 키다운 이벤트 생성
      const escEvent = new KeyboardEvent('keydown', { key: 'Escape' });
      // 문서에 이벤트 디스패치
      act(() => {
        document.dispatchEvent(escEvent);
      });
      // setSearchParams가 viewMore 파라미터를 제거하는지 확인
      expect(setSearchParamsMock).toHaveBeenCalled();
      // 호출된 setSearchParams의 인자를 확인
      const calledParams = setSearchParamsMock.mock.calls[0][0];
      expect(calledParams.has('viewMore')).toBe(false);
    });
  });
});
