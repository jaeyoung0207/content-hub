import { useHeaderForm, UseHeaderFormReturnType } from './useHeaderForm';
import { useHeaderHome, UseHeaderHomeReturnType } from './useHeaderHome';
import { useHeaderInitialize } from './useHeaderInitialize';
import { useHeaderLogin, UseHeaderLoginReturnType } from './useHeaderLogin';
import { useHeaderSearch, UseHeaderSearchReturnType } from './useHeaderSearch';
import {
  useHeaderWishlist,
  UseHeaderWishlistReturnType,
} from './useHeaderWishlist';

/**
 * 헤더 훅 반환 타입
 */
export type UseHeaderReturnType = UseHeaderFormReturnType &
  UseHeaderLoginReturnType &
  UseHeaderSearchReturnType &
  UseHeaderHomeReturnType &
  UseHeaderWishlistReturnType;

/**
 * 헤더 컴포넌트의 상태와 동작을 관리하는 훅
 */
export const useHeader = (): UseHeaderReturnType => {
  // ================================================================================================== custom hook

  // 헤더 초기화 훅
  useHeaderInitialize();

  // 헤더 form 훅
  const {
    control,
    setValue,
    setFocus,
    reset,
    keyword,
    selectType,
    searchType,
    aniFlg,
    dramaFlg,
    movieFlg,
    documentaryFlg,
    kidsFlg,
    newsFlg,
    varietyFlg,
    comicsFlg,
    adultFlg,
  } = useHeaderForm();

  // 헤더 로그인 훅
  const {
    user,
    handleLogoutOnClick,
    handleLoginOnClick,
    userOptionIsOpen,
    handleUserOptionToggle,
    userOptionRef,
  } = useHeaderLogin();

  // 헤더 검색 훅
  const {
    autoCompleteRef,
    selectRef,
    autoCompleteList,
    setAutoCompleteList,
    currentIndex,
    setCurrentIndex,
    savedKeyword,
    searchHistoryisOpen,
    isFocusedRef,
    isFilterOpen,
    filterRef,
    firstLoadRef,
    clearAdultFlg,
    handleSearchOnClick,
    handleKeywordOnKeyDown,
    handleKeywordListOnClick,
    handleKeywordOnKeyDownEvent,
    handleRemoveSearchHistory,
    handleSetCurrentIndex,
    handleDeleteKeyword,
    handleFilterIconOnClick,
    handleOnClickSelectTypeRadioButton,
    handleOnClickSearchTypeRadioButton,
    setLastSelectedSearchType,
  } = useHeaderSearch({
    keyword: keyword,
    adultFlg: adultFlg,
    setValue: setValue,
    setFocus: setFocus,
  });

  // 헤더 홈 훅
  const { handleHomeOnClick } = useHeaderHome({
    reset: reset,
    setFocus: setFocus,
    clearAdultFlg: clearAdultFlg,
    setAutoCompleteList: setAutoCompleteList,
    setCurrentIndex: setCurrentIndex,
    setLastSelectedSearchType: setLastSelectedSearchType,
    firstLoadRef: firstLoadRef,
  });

  // 헤더 위시리스트 훅
  const { handleWishlistOnClick } = useHeaderWishlist(user?.userId);

  // ================================================================================================== return

  return {
    control: control,
    setValue: setValue,
    setFocus: setFocus,
    reset: reset,
    keyword: keyword,
    selectType: selectType,
    searchType: searchType,
    aniFlg: aniFlg,
    dramaFlg: dramaFlg,
    movieFlg: movieFlg,
    documentaryFlg: documentaryFlg,
    kidsFlg: kidsFlg,
    newsFlg: newsFlg,
    varietyFlg: varietyFlg,
    comicsFlg: comicsFlg,
    adultFlg: adultFlg,
    user: user,
    handleLogoutOnClick: handleLogoutOnClick,
    handleLoginOnClick: handleLoginOnClick,
    autoCompleteRef: autoCompleteRef,
    selectRef: selectRef,
    autoCompleteList: autoCompleteList,
    setAutoCompleteList: setAutoCompleteList,
    currentIndex: currentIndex,
    setCurrentIndex: setCurrentIndex,
    savedKeyword: savedKeyword,
    searchHistoryisOpen: searchHistoryisOpen,
    isFocusedRef: isFocusedRef,
    isFilterOpen: isFilterOpen,
    filterRef: filterRef,
    firstLoadRef: firstLoadRef,
    clearAdultFlg: clearAdultFlg,
    handleSearchOnClick: handleSearchOnClick,
    handleKeywordOnKeyDown: handleKeywordOnKeyDown,
    handleKeywordListOnClick: handleKeywordListOnClick,
    handleKeywordOnKeyDownEvent: handleKeywordOnKeyDownEvent,
    handleRemoveSearchHistory: handleRemoveSearchHistory,
    handleSetCurrentIndex: handleSetCurrentIndex,
    handleDeleteKeyword: handleDeleteKeyword,
    handleFilterIconOnClick: handleFilterIconOnClick,
    handleHomeOnClick: handleHomeOnClick,
    handleWishlistOnClick: handleWishlistOnClick,
    userOptionIsOpen: userOptionIsOpen,
    handleUserOptionToggle: handleUserOptionToggle,
    userOptionRef: userOptionRef,
    handleOnClickSelectTypeRadioButton: handleOnClickSelectTypeRadioButton,
    handleOnClickSearchTypeRadioButton: handleOnClickSearchTypeRadioButton,
    setLastSelectedSearchType: setLastSelectedSearchType,
  };
};
