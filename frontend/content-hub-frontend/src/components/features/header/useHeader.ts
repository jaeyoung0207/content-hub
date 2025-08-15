import { Control, useForm, useWatch } from 'react-hook-form';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import {
  useProviderStore,
  useSearchTypeStore,
  useUserStore,
} from '@/components/common/store/globalStateStore';
import {
  KeyboardEvent,
  RefObject,
  useCallback,
  useEffect,
  useRef,
  useState,
} from 'react';
import { Login } from '@/api/Login';
import { Common } from '@/api/Common';
import { Search } from '@/api/Search';
import { useDebounce } from '@/components/common/hooks/useDebounce';
import {
  ARROW_DOWN_KEY,
  ARROW_UP_KEY,
  ENTER_KEY,
  ESC_KEY,
  LOGIN_PROVIDER,
} from '@/components/common/constants/constants';
import {
  clearUserData,
  searchUrlQuery,
} from '@/components/common/utils/commonUtil';
import { useCookies } from 'react-cookie';
import { settings } from '@/components/common/config/settings';
import { headerQueryKeys } from './queryKeys/headerQueryKeys';

/**
 * 헤더 컴포넌트의 폼 필드 타입 정의
 */
export type HeaderType = Partial<{
  // Partial로 감싸서 객체를 각 필드로 풀어서 정의
  keyword: string;
  aniFlg?: boolean;
  comicsFlg?: boolean;
  movieFlg?: boolean;
  dramaFlg?: boolean;
  novelFlg?: boolean;
  adultFlg?: boolean;
}>;

/**
 * 헤더 훅 반환 타입
 */
type useHeaderReturnType = {
  control: Control<HeaderType>; // react-hook-form의 control 객체
  handleHomeOnClick: () => void; // 홈 버튼 클릭 처리 함수
  handleSearchOnClick: () => void; // 검색 버튼 클릭 처리 함수
  handleLoginOnClick: () => void; // 로그인 버튼 클릭 처리 함수
  handleLogoutOnClick: () => void; // 로그아웃 버튼 클릭 처리 함수
  isFilterOpen: boolean; // 필터 박스 오픈 여부
  handleFilterIconOnClick: () => void; // 필터 아이콘 클릭 처리 함수
  keyword?: string; // 현재 입력된 검색어
  aniFlg?: boolean; // 애니메이션 검색 여부
  comicsFlg?: boolean; // 만화 검색 여부
  movieFlg?: boolean; // 영화 검색 여부
  dramaFlg?: boolean; // 드라마 검색 여부
  novelFlg?: boolean; // 소설 검색 여부
  adultFlg?: boolean; // 성인물 검색 여부
  isFocusedRef: RefObject<boolean | null>; // 검색창 포커스 상태 참조
  filterRef: RefObject<HTMLDivElement | null>; // 필터 박스 참조
  autoCompleteRef: RefObject<HTMLDivElement | null>; // 자동완성 박스 참조
  selectRef: RefObject<HTMLLIElement | null>; // 자동완성 검색어 선택 참조
  autoCompleteList?: string[]; // 자동완성 리스트
  handleKeywordOnKeyDown: () => void; // 검색어 입력창에서 키다운 이벤트 처리 함수
  handleKeywordListOnClick: (item: string) => void; // 자동완성 리스트 아이템 클릭 처리 함수
  currentIndex: number; // 자동완성 박스 포커스 인덱스
  handleKeywordOnKeyDownEvent: (e: KeyboardEvent) => void; // 자동완성 박스 키보드 이벤트 처리 함수
  handleRemoveSearchHistory: (index: number) => void; // 검색 이력 삭제 처리 함수
  searchHistoryisOpen: boolean; // 검색 이력 표시 여부
  handleSetCurrentIndex: (index: number) => void; // 자동완성 박스 인덱스 설정 함수
  handleDeleteKeyword: () => void; // 검색창 클리어 함수
  savedKeyword: string; // 저장된 검색어
};

/**
 * 헤더 컴포넌트의 상태와 동작을 관리하는 훅
 */
export const useHeader = (): useHeaderReturnType => {
  // ================================================================================================== react hook

  // URL query string 값을 가져오기 위한 useSearchParams 훅
  const [searchParams] = useSearchParams();
  // 검색어 파라미터
  const keywordParam = searchParams.get('keyword');
  // 성인물 포함 여부 파라미터
  const isAdultParam = searchParams.get('isAdult');
  // 전체보기 여부 파라미터
  const viewMore = searchParams.get('viewMore');
  // 컨텐츠 ID 파라미터
  const contentId = searchParams.get('contentId');

  // navigate 훅
  const navigate = useNavigate();

  // 쿠키 훅: 리프레시 토큰
  const [refreshTokenCookie] = useCookies<string>(['refreshToken']);
  // 쿠키 훅: provider 정보
  const [providerCookie] = useCookies<string>(['provider']);

  // 필터 박스 오픈 판단
  const [isFilterOpen, setIsFilterOpen] = useState<boolean>(false);
  // 자동완성리스트 설정
  const [autoCompleteList, setAutoCompleteList] = useState<string[]>();
  // 자동완성박스 포커스 인덱스
  const [currentIndex, setCurrentIndex] = useState<number>(-1);
  // 검색 이력 상태 저장
  const [searchHistoryisOpen, setSearchHistoryisOpen] = useState<boolean>(true);

  // 검색어 입력값 참조
  const savedKeyword = useRef<string>('');
  // 포커스 상태 참조
  const isFocusedRef = useRef<boolean>(false);
  // 필터 박스 참조
  const filterRef = useRef<HTMLDivElement>(null);
  // 자동완성 박스 참조
  const autoCompleteRef = useRef<HTMLDivElement>(null);
  // 검색 실행여부 참조
  const searchExecuteRef = useRef<boolean>(false);
  // 자동완성 검색어 선택 참조
  const selectRef = useRef<HTMLLIElement>(null);
  // 처음 로드 참조
  const firstLoadRef = useRef<boolean>(false);

  // ================================================================================================== zustand

  // 유저 정보 전역 상태 저장용 훅
  const { setUser } = useUserStore();
  // provider 정보 전역 상태 저장용 훅
  const { setProvider } = useProviderStore();
  // 검색 종류 전역 상태 저장용 훅
  const { setSearchTypeState } = useSearchTypeStore();

  // ================================================================================================== react hook form

  // 초기값 설정
  const defaultValue = {
    keyword: '',
    aniFlg: true,
    comicsFlg: true,
    movieFlg: true,
    dramaFlg: true,
    novelFlg: true,
    adultFlg: false,
  };

  // react-hook-form 훅
  const { control, setValue, setFocus, reset } = useForm<HeaderType>({
    // } = useForm<HeaderSchema>({
    // resolver: zodResolver(useHeaderSchema()),
    defaultValues: defaultValue,
  });

  // react-hook-form의 useWatch 훅을 사용하여 폼 필드 값 감시
  // 각 필드의 값을 감시하여 상태를 업데이트
  const keyword = useWatch({
    control,
    name: 'keyword',
  });
  const aniFlg = useWatch({
    control,
    name: 'aniFlg',
  });
  const dramaFlg = useWatch({
    control,
    name: 'dramaFlg',
  });
  const movieFlg = useWatch({
    control,
    name: 'movieFlg',
  });
  const comicsFlg = useWatch({
    control,
    name: 'comicsFlg',
  });
  const novelFlg = useWatch({
    control,
    name: 'novelFlg',
  });

  const adultFlg = useWatch({
    control,
    name: 'adultFlg',
  });

  // ================================================================================================== custom hook

  // 검색어 입력지연 디바운스
  const debouncedKeyword = useDebounce(keyword, 300);

  // ================================================================================================== react query

  // react query 클라이언트 훅
  const queryClient = useQueryClient();
  // 공통 API 인스턴스 생성
  const commonApi = new Common();
  // 검색 API 인스턴스 생성
  const searchApi = new Search();
  // 로그인 API 인스턴스 생성
  const loginApi = new Login();

  /**
   * 성인물 검색 플래그 설정 API 호출
   */
  const setAdultFlgMutation = useMutation({
    mutationKey: headerQueryKeys.setAdultFlg(),
    mutationFn: async () =>
      await commonApi.setAdultFlg({ adult_flg: adultFlg! }),
  });

  /**
   * 성인물 검색 플래그 해제 API 호출
   */
  const clearAdultFlgMutation = useMutation({
    mutationKey: headerQueryKeys.clearAdultFlg(),
    mutationFn: async () => await commonApi.clearAdultFlg(),
  });

  // ================================================================================================== function

  /**
   * 초기화 처리
   */
  const resetAll = () => {
    // 성인물 검색 플래그 초기화
    clearAdultFlg();
    // 각 필드값 초기화
    reset();
    // 자동완성 리스트 초기화
    setAutoCompleteList(undefined);
    // 자동완성 박스 포커스 인덱스 초기화
    setCurrentIndex(-1);
    // 캐시에서 모든 쿼리 제거
    queryClient.removeQueries();
    // 처음 로드 참조를 true로 설정
    firstLoadRef.current = true;
  };

  /**
   * 홈 버튼 클릭시 처리
   */
  const handleHomeOnClick = useCallback(() => {
    // 초기화 처리
    resetAll();
    // 홈으로 이동
    navigate('/');
    // 포커스 설정
    setFocus('keyword');
  }, [resetAll, navigate, setFocus]);

  /**
   * 성인물 검색 플래그 설정
   */
  const setAdultFlg = async () => {
    // 성인물 검색 플래그 설정 API 호출
    await setAdultFlgMutation.mutate();
    // 재검색
    handleSearchOnClick();
  };

  /**
   * 성인물 검색 플래그 해제
   */
  const clearAdultFlg = async () => {
    // 성인물 검색 플래그 해제 API 호출
    await clearAdultFlgMutation.mutate();
  };

  /**
   * 검색창 클리어
   */
  const handleDeleteKeyword = useCallback(() => {
    // 검색어 초기화
    setValue('keyword', '');
    // 검색 이력 표시
    setFocus('keyword');
  }, [setValue, setFocus]);

  /**
   * 자동완성 검색어 리스트 API 호출
   * @returns Promise<string[]>
   */
  const getKeywordList = () => {
    return queryClient.fetchQuery({
      queryKey: headerQueryKeys.searchKeyword(keyword!),
      queryFn: async () => {
        return (await searchApi.searchKeyword({ query: keyword! })).data;
      },
    });
  };

  /**
   * 검색창 클릭시 처리
   */
  const handleKeywordOnKeyDown = async () => {
    // 검색어가 비어있지 않은 경우
    if (keyword) {
      // 검색어 입력값 저장
      savedKeyword.current = keyword;
      // 검색 이력 상태 false로 설정
      setSearchHistoryisOpen(false);
      // 자동완성 검색어 리스트 API 호출
      getKeywordList().then((res) => setAutoCompleteList(res));
    } else {
      // 검색어 입력값 초기화
      savedKeyword.current = '';
      // 검색 이력이 있는 경우, 검색 이력 표시
      const keywordList = localStorage.getItem('keywordList');
      if (keywordList) {
        // 검색 이력 상태 true로 설정
        setSearchHistoryisOpen(true);
        // 검색 이력 리스트 설정
        setAutoCompleteList(keywordList.split('\t'));
      } else {
        // 검색 이력 상태 false로 설정
        setSearchHistoryisOpen(false);
        // 자동완성 리스트 초기화
        setAutoCompleteList(undefined);
      }
    }
  };

  /**
   * 검색 이력에 검색어 저장 처리
   * @param selectedKeyword 선택된 자동완성 검색어
   */
  const saveSearchHistory = (selectedKeyword: string) => {
    // 검색 이력 로컬스토리지에서 취득
    const keywords = localStorage.getItem('keywordList') ?? '';
    // 검색어 리스트로 변환
    let keywordArray = keywords ? keywords.split('\t') : [];
    // 중복 제거
    keywordArray = keywordArray.filter((k) => k !== selectedKeyword);
    // 검색 이력 최대 10개 유지
    if (keywordArray.length >= 9) {
      // 오래된 검색이력부터 제거
      keywordArray.pop();
    }
    // 최신 검색어를 맨 앞에 추가
    keywordArray.unshift(selectedKeyword);
    // 검색 이력 로컬스토리지에 저장
    localStorage.setItem('keywordList', keywordArray.join('\t'));
  };

  /**
   * 자동완성 검색어 클릭시 처리
   * @param selectedKeyword 선택된 자동완성 검색어
   */
  const handleKeywordListOnClick = useCallback(
    (selectedKeyword: string) => {
      // 검색어 입력값 설정
      setValue('keyword', selectedKeyword);
      // 자동완성 리스트 닫기
      setAutoCompleteList(undefined);
      // 자동완성박스 인덱스 초기화
      setCurrentIndex(-1);
      // 검색 실행 여부 참조 true로 설정
      searchExecuteRef.current = true;
      // 검색 이력에 검색어 저장
      saveSearchHistory(selectedKeyword);
      // 검색 실행
      navigate(
        searchUrlQuery({ keyword: selectedKeyword, isAdult: String(adultFlg) }),
        { replace: true }
      );
    },
    [
      setValue,
      setAutoCompleteList,
      setCurrentIndex,
      saveSearchHistory,
      navigate,
      adultFlg,
    ]
  );

  /**
   * 검색 처리
   */
  const handleSearchOnClick = useCallback(() => {
    // 현재 검색어 설정
    const currentKeyword = keyword ? keyword : keywordParam;
    // 검색어가 비어있지 않은 경우
    if (currentKeyword) {
      // 자동완성박스 이동용 인덱스 초기화
      setCurrentIndex(-1);
      // 검색실행ref = true
      searchExecuteRef.current = true;
      // 검색 이력에 검색어 저장
      saveSearchHistory(currentKeyword);
      // 검색 실행
      navigate(
        searchUrlQuery({ keyword: currentKeyword, isAdult: String(adultFlg) }),
        { replace: true }
      );
    }
  }, [
    keyword,
    keywordParam,
    adultFlg,
    setCurrentIndex,
    saveSearchHistory,
    navigate,
  ]);

  /**
   * 검색 이력 삭제 처리
   * @param index 해당 검색 이력 인덱스
   */
  const handleRemoveSearchHistory = useCallback(
    (index: number) => {
      // 검색 이력 로컬스토리지에서 취득
      const keywords = localStorage.getItem('keywordList')!;
      // 검색어 리스트로 변환
      const keywordArray = keywords?.split('\t');
      // 해당 인덱스의 검색어 삭제
      keywordArray?.splice(index, 1);
      // 검색 이력 로컬스토리지에 저장
      localStorage.setItem('keywordList', keywordArray?.join('\t'));
      // 자동완성 리스트 업데이트
      setAutoCompleteList(keywordArray);
      // 포커스 설정
      setFocus('keyword');
    },
    [setAutoCompleteList, setFocus]
  );

  /**
   * 로그인 클릭시 처리
   */
  const handleLoginOnClick = useCallback(() => {
    // 로그인 페이지로 이동
    navigate('/login');
  }, [navigate]);

  /**
   * 로그아웃 클릭시 처리
   */
  const handleLogoutOnClick = useCallback(() => {
    // 검색 화면 URL 생성
    const searchUrl = searchUrlQuery({
      keyword: keyword!,
      isAdult: String(adultFlg),
    });
    // 검색 화면 URL 저장
    sessionStorage.setItem('redirectUrl', searchUrl);
    // 로그아웃 페이지로 이동
    navigate('/logout');
  }, [keyword, adultFlg, navigate]);

  /**
   * 필터 아이콘 클릭시 처리
   */
  const handleFilterIconOnClick = useCallback(() => {
    // 필터 박스 오픈 상태 토글
    setIsFilterOpen(!isFilterOpen);
  }, [isFilterOpen]);

  /**
   * 자동완성박스 인덱스 설정
   * @param index
   */
  const handleSetCurrentIndex = useCallback((index: number) => {
    // 자동완성박스 인덱스 설정
    setCurrentIndex(index);
  }, []);

  /**
   * 자동완성박스 키보드 이벤트
   * @param e 키보드 이벤트
   * @returns void
   */
  const handleKeywordOnKeyDownEvent = (e: KeyboardEvent) => {
    // 엔터키 입력시
    if (e.key === ENTER_KEY) {
      // 검색 실행 함수 호출
      handleSearchOnClick();
      // 자동완성 리스트 초기화
      setAutoCompleteList(undefined);
      // 자동완성박스 인덱스 초기화
      setCurrentIndex(-1);
      return;
    }
    // 자동완성 리스트가 존재하지 않는 경우
    if (!autoCompleteList || autoCompleteList.length === 0) {
      // 아래 화살표 키 입력시
      if (e.key === ARROW_DOWN_KEY) {
        // 자동완성박스 인덱스 초기화
        setCurrentIndex(-1);
        // 검색창 클릭시 처리
        handleKeywordOnKeyDown();
      }
      return;
    }

    // 아래 화살표 키 입력시
    if (e.key === ARROW_DOWN_KEY) {
      // 자동완성 박스 인덱스와 자동완성 리스트 길이를 비교하여 다음 인덱스 계산
      // 현재 인덱스 + 1 >= 자동완성 리스트 길이인 경우, 다음 인덱스를 -1(검색창)로 설정
      // -1이외의 경우, 다음 인덱스를 현재 인덱스 + 1로 설정
      const nextIndex =
        currentIndex + 1 >= autoCompleteList.length ? -1 : currentIndex + 1;
      setCurrentIndex(nextIndex);
      // 다음 인덱스가 -1인 경우, 검색어를 저장된 검색어(검색창에 입력된 검색어)로 설정
      // 다음 인덱스가 -1이 아니면, 검색어를 자동완성 리스트의 해당 인덱스의 검색어로 설정
      const newKeyword =
        nextIndex === -1 ? savedKeyword.current : autoCompleteList[nextIndex];
      setValue('keyword', newKeyword);
    }
    // 위 화살표 키 입력시
    else if (e.key === ARROW_UP_KEY) {
      // 자동완성 박스 인덱스와 자동완성 리스트 길이를 비교하여 이전 인덱스 계산
      // 현재 인덱스가 -1인 경우, 이전 인덱스를 자동완성 리스트의 마지막 인덱스로 설정
      // 현재 인덱스 - 1 < -1인 경우, 이전 인덱스를 -1(검색창)로 설정
      // 그 외의 경우, 이전 인덱스를 현재 인덱스 - 1로 설정
      const nextIndex =
        currentIndex === -1
          ? autoCompleteList.length - 1
          : currentIndex - 1 < -1
            ? -1
            : currentIndex - 1;
      setCurrentIndex(nextIndex);
      // 현재 인덱스가 -1인 경우, 검색어를 저장된 검색어(검색창에 입력된 검색어)로 설정
      // 현재 인덱스가 -1이 아니면, 검색어를 자동완성 리스트의 해당 인덱스의 검색어로 설정
      const newKeyword =
        nextIndex === -1 ? savedKeyword.current : autoCompleteList[nextIndex];
      setValue('keyword', newKeyword);
    }
    // 그 밖에 다른 키 입력시
    else {
      // 자동완성박스 인덱스 초기화
      setCurrentIndex(-1);
    }
  };

  // ================================================================================================== useEffect

  /**
   * 화면 첫 로드 시 처리
   */
  /* eslint-disable react-hooks/exhaustive-deps */
  // 최초 한번만 실행돼야 하므로 의존성 배열 미지정
  useEffect(() => {
    // 맨 처음 접속시에는 유저정보 초기화
    clearUserData();
    // 처음 로드시 true
    firstLoadRef.current = true;
    // 재로그인 처리
    if (refreshTokenCookie.refreshToken) {
      // 쿠키의 provider가 NAVER인 경우
      if (providerCookie.provider === LOGIN_PROVIDER.NAVER) {
        queryClient.fetchQuery({
          queryKey: headerQueryKeys.login(LOGIN_PROVIDER.NAVER),
          queryFn: async () => {
            // 네이버 로그인 정보 업데이트 API 호출
            const updateResponse = (await loginApi.updateNaverLoginInfo()).data;
            if (updateResponse && updateResponse.userInfo) {
              // 유저정보 저장
              setUser(updateResponse.userInfo!);
              // provider 저장
              setProvider(LOGIN_PROVIDER.NAVER);
              // 액세스 토큰을 sessionStorage에 저장
              sessionStorage.setItem(
                'accessToken',
                updateResponse.accessToken!
              );
              // JWT를 localStorage에 저장
              sessionStorage.setItem('jwt', updateResponse.jwt!);
              // 만료시각을 sessionStorage에 저장
              // const expireDate = dayjs().add(updateResponse.expiresIn!, "seconds").format('YYYYMMDDHHmmss');
              sessionStorage.setItem('expireDate', updateResponse.expireDate!);
            } else {
              clearUserData();
            }
            return updateResponse;
          },
        });
      }
      // 쿠키의 provider가 KAKAO인 경우
      else if (providerCookie.provider === LOGIN_PROVIDER.KAKAO) {
        queryClient.fetchQuery({
          queryKey: headerQueryKeys.login(LOGIN_PROVIDER.KAKAO),
          queryFn: async () => {
            // 카카오 로그인 정보 업데이트 API 호출
            const updateResponse = (
              await loginApi.updateKakaoLoginInfo({
                client_id: settings.kakaoClientId,
              })
            ).data;
            if (updateResponse && updateResponse.userInfo) {
              // 유저정보 저장
              setUser(updateResponse.userInfo!);
              // provider 저장
              setProvider(LOGIN_PROVIDER.KAKAO);
              // 액세스 토큰을 sessionStorage에 저장
              sessionStorage.setItem(
                'accessToken',
                updateResponse.accessToken!
              );
              // JWT를 localStorage에 저장
              sessionStorage.setItem('jwt', updateResponse.jwt!);
              // 만료시각을 sessionStorage에 저장
              // const expireDate = dayjs().add(updateResponse.expiresIn!, "seconds").format('YYYYMMDDHHmmss');
              sessionStorage.setItem('expireDate', updateResponse.expireDate!);
            } else {
              clearUserData();
            }
            return updateResponse;
          },
        });
      }
    }
    // 초기화
    resetAll();
    // keyword 쿼리스트링이 있는 경우 설정(URL직접 입력 고려)
    if (keywordParam) {
      setValue('keyword', keywordParam);
    }
    // 전체보기, 상세화면용 URL쿼리스트링이 없을 경우에만 처리(URL직접 입력 고려)
    if (!viewMore && !contentId) {
      // 초기 포커스
      setFocus('keyword');
    }
    // 성인물 쿼리스트링이 true인 경우에 adultFlg에 true 설정(URL직접 입력 고려)
    // 재로그인 처리보다 뒤에 처리(요청 인터셉터에서 정상적인 접근토큰,만료시각,유저정보로 처리하기 위함)
    if (isAdultParam && isAdultParam === 'true') {
      setValue('adultFlg', true);
    }
  }, []);

  /**
   * 성인물 체크시 성인물 플래그 설정
   */
  useEffect(() => {
    // 처음 로드시에만 처리 중지
    if (firstLoadRef.current) {
      return;
    }
    // 전체보기, 상세화면용 URL쿼리스트링이 없을 경우에만 처리(URL직접 입력 고려)
    if (!viewMore && !contentId) {
      // 성인물 체크시 처리
      setAdultFlg();
    }
  }, [adultFlg]);

  /**
   * 자동완성 debounce 처리
   */
  useEffect(() => {
    // 처음 로드시에만 처리 중지
    if (firstLoadRef.current) {
      firstLoadRef.current = false;
      return;
    }
    // currentIndex 가 초기화 되어있고, 포커스가 검색창에 가있을때만 처리
    if (currentIndex === -1 && isFocusedRef.current) {
      // 검색버튼 누른 직후에는 처리 중지
      if (searchExecuteRef.current) {
        searchExecuteRef.current = false;
        return;
      }
      // 검색창 클릭시 처리
      handleKeywordOnKeyDown();
    }
  }, [debouncedKeyword]);

  /**
   * 검색 종류 제어
   * 검색 종류의 체크 상태를 검색 종류 전역 상태에 설정(검색 훅에서 사용하기 위함)
   */
  useEffect(() => {
    setSearchTypeState(aniFlg!, dramaFlg!, movieFlg!, comicsFlg!, novelFlg!);
  }, [aniFlg, dramaFlg, movieFlg, comicsFlg, novelFlg]);

  /**
   * 마우스 클릭/키보드 키다운 이벤트
   */
  useEffect(() => {
    // 필터 및 자동완성박스 바깥 영역 클릭 이벤트
    const handleOnClickOutside = (e: MouseEvent) => {
      // 필터 바깥영역 클릭시
      if (filterRef.current && !filterRef.current.contains(e.target as Node)) {
        setIsFilterOpen(false);
      }
      // 자동완성박스 바깥영역 클릭시
      if (
        autoCompleteRef.current &&
        !autoCompleteRef.current.contains(e.target as Node)
      ) {
        setAutoCompleteList(undefined);
        setCurrentIndex(-1);
      }
    };
    // 필터 및 자동완성박스 esc 키다운 이벤트
    const handleOnKeyDown = (e: globalThis.KeyboardEvent) => {
      if (e.key === ESC_KEY) {
        // 필터 박스 닫기
        setIsFilterOpen(false);
        // 자동완성 박스 닫기
        setAutoCompleteList(undefined);
        // 자동완성박스 인덱스 초기화
        setCurrentIndex(-1);
        // 포커스가 검색창에 있을경우, 검색어 초기화
        if (document.activeElement?.getAttribute('name') === 'keyword') {
          setValue('keyword', '');
        }
      }
    };

    // 각 이벤트 리스너 추가
    document.addEventListener('mousedown', handleOnClickOutside);
    document.addEventListener('keydown', handleOnKeyDown);

    return () => {
      // 각 이벤트 리스너 제거
      document.removeEventListener('mousedown', handleOnClickOutside);
      document.removeEventListener('keydown', handleOnKeyDown);
    };
  }, []);

  // 자동완성 스크롤 조정
  useEffect(() => {
    // 자동완성 검색어 li태그에 ref를 참조
    // scrollIntoView() 메서드를 호출하여 해당 요소가 화면에 보이도록 스크롤(부드럽게 움직임, 대상 요소가 수직 스크롤 가운데 오도록)
    selectRef.current?.scrollIntoView({ behavior: 'smooth', block: 'center' });
  }, [currentIndex]);

  // ================================================================================================== return

  return {
    control: control,
    handleHomeOnClick: handleHomeOnClick,
    handleSearchOnClick: handleSearchOnClick,
    handleLoginOnClick: handleLoginOnClick,
    handleLogoutOnClick: handleLogoutOnClick,
    isFilterOpen: isFilterOpen,
    handleFilterIconOnClick: handleFilterIconOnClick,
    keyword: keyword,
    aniFlg: aniFlg,
    comicsFlg: comicsFlg,
    movieFlg: movieFlg,
    dramaFlg: dramaFlg,
    novelFlg: novelFlg,
    adultFlg: adultFlg,
    isFocusedRef: isFocusedRef,
    filterRef: filterRef,
    autoCompleteRef: autoCompleteRef,
    selectRef: selectRef,
    autoCompleteList: autoCompleteList,
    handleKeywordOnKeyDown: handleKeywordOnKeyDown,
    handleKeywordListOnClick: handleKeywordListOnClick,
    currentIndex: currentIndex,
    handleKeywordOnKeyDownEvent: handleKeywordOnKeyDownEvent,
    handleRemoveSearchHistory: handleRemoveSearchHistory,
    searchHistoryisOpen: searchHistoryisOpen,
    handleSetCurrentIndex: handleSetCurrentIndex,
    handleDeleteKeyword: handleDeleteKeyword,
    savedKeyword: savedKeyword.current,
  };
};
