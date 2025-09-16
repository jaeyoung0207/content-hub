import { useSearchTypeStore } from '@/components/common/store/globalStateStore';
import { useEffect } from 'react';
import { Control, useForm, UseFormSetValue, useWatch } from 'react-hook-form';

/**
 * 헤더 컴포넌트의 폼 필드 타입 정의
 */
export type HeaderType = Partial<{
  // Partial로 감싸서 객체를 각 필드로 풀어서 정의
  keyword: string;
  aniFlg?: boolean;
  dramaFlg?: boolean;
  movieFlg?: boolean;
  comicsFlg?: boolean;
  varietyFlg?: boolean;
  adultFlg?: boolean;
}>;

/**
 * 헤더 form 훅 반환 타입
 */
export type UseHeaderFormReturnType = {
  control: Control<HeaderType>; // react-hook-form의 control 객체
  setValue: UseFormSetValue<HeaderType>; // react-hook-form의 setValue 함수
  setFocus: (name: keyof HeaderType) => void; // react-hook-form의 setFocus 함수
  reset: (values?: HeaderType) => void; // react-hook-form의 reset 함수
  keyword?: string; // 현재 입력된 검색어
  aniFlg?: boolean; // 애니메이션 검색 여부
  comicsFlg?: boolean; // 만화 검색 여부
  movieFlg?: boolean; // 영화 검색 여부
  dramaFlg?: boolean; // 드라마 검색 여부
  varietyFlg?: boolean; // 예능 검색 여부
  adultFlg?: boolean; // 성인물 검색 여부
};

/**
 * 헤더 컴포넌트의 폼 상태와 동작을 관리하는 훅
 */
export const useHeaderForm = (): UseHeaderFormReturnType => {
  // ================================================================================================== zustand

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
    varietyFlg: true,
    adultFlg: false,
  };

  // react-hook-form 훅
  const { control, setValue, setFocus, reset } = useForm<HeaderType>({
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
  const varietyFlg = useWatch({
    control,
    name: 'varietyFlg',
  });
  const adultFlg = useWatch({
    control,
    name: 'adultFlg',
  });

  // ================================================================================================== useEffect
  /**
   * 검색 종류 제어
   * 검색 종류의 체크 상태를 검색 종류 전역 상태에 설정(검색 훅에서 사용하기 위함)
   */
  useEffect(() => {
    setSearchTypeState(aniFlg!, dramaFlg!, movieFlg!, comicsFlg!, varietyFlg!);
  }, [aniFlg, dramaFlg, movieFlg, comicsFlg, varietyFlg, setSearchTypeState]);

  // ================================================================================================== return

  return {
    control: control,
    setValue: setValue,
    setFocus: setFocus,
    reset: reset,
    keyword: keyword,
    aniFlg: aniFlg,
    dramaFlg: dramaFlg,
    movieFlg: movieFlg,
    comicsFlg: comicsFlg,
    varietyFlg: varietyFlg,
    adultFlg: adultFlg,
  };
};
