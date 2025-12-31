import { useTranslation } from 'react-i18next';
import homeIcon from '@assets/icons/home.png';
import { SearchTextUi } from '@/components/ui/SearchTextUi';
import { useHeader } from './hooks/useHeader';
import { CheckBoxUi } from '@/components/ui/CheckBoxUi';
import { BsFilterSquare, BsFilterSquareFill } from 'react-icons/bs';
import { FieldValues, Path } from 'react-hook-form';
import {
  OMISSION_TEXT,
  SEARCH_TYPE,
  SELECT_TYPE,
  TOOLTIP_CLOSE_STATE,
} from '@/components/common/constants/constants';
import { memo, RefObject } from 'react';
import { commonErrorHandler } from '@/components/common/utils/errorUtil';
import { settings } from '@/components/common/config/settings';
import { FaHeart } from 'react-icons/fa';
import { HeaderType } from './hooks/useHeaderForm';
import { useTooltipStore } from '@/components/common/store/globalStateStore';
import TooltipUi from '@/components/ui/common/TooltipUi';
import {
  RadioButtonGroupUi,
  RadioButtonProps,
} from '@/components/ui/RadioButtonGroupUi';
import { myCommentsUrlQuery } from '@/components/common/utils/urlUtil';
import { useNavigate } from 'react-router-dom';
import { TfiMenu } from 'react-icons/tfi';
import { isMobileOnly } from 'react-device-detect';

/**
 * 자동완성 박스 컴포넌트 props 타입
 *
 */
type AutoCompletePropsType = {
  autoCompleteList?: string[]; // 자동완성 리스트
  handleKeywordListOnClick: (item: string) => void; // 자동완성 리스트 아이템 클릭 핸들러
  currentIndex: number; // 현재 인덱스
  selectRef: RefObject<HTMLLIElement | null>; // 자동완성 검색어 선택 참조
  handleRemoveSearchHistory: (index: number) => void; // 검색 기록 삭제 핸들러
  searchHistoryisOpen: boolean; // 검색 기록 열림 여부
  handleSetCurrentIndex: (index: number) => void; // 현재 인덱스 설정 핸들러
  savedKeyword: string; // 저장된 키워드
};

/**
 * 헤더 컴포넌트
 * 헤더는 검색창, 필터 아이콘, 로그인/로그아웃 버튼 등을 포함
 * 헤더는 페이지 상단에 고정되어 있으며, 사용자가 사이트를 탐색할 때 항상 표시됨
 */
export const Header = () => {
  // i18n 번역 함수
  const { t } = useTranslation();

  // navigate 훅
  const navigate = useNavigate();

  // 툴팁 상태 저장 훅
  const { isTooltipOpen, setIsTooltipOpen } = useTooltipStore();

  const {
    control,
    handleHomeOnClick,
    handleSearchOnClick,
    handleLoginOnClick,
    handleLogoutOnClick,
    isFilterOpen,
    handleFilterIconOnClick,
    selectType,
    aniFlg,
    comicsFlg,
    dramaFlg,
    movieFlg,
    documentaryFlg,
    kidsFlg,
    newsFlg,
    varietyFlg,
    adultFlg,
    isFocusedRef,
    filterRef,
    autoCompleteRef,
    autoCompleteList,
    handleKeywordOnKeyDown,
    handleKeywordListOnClick,
    currentIndex,
    handleKeywordOnKeyDownEvent,
    selectRef,
    handleRemoveSearchHistory,
    searchHistoryisOpen,
    handleSetCurrentIndex,
    handleDeleteKeyword,
    savedKeyword,
    user,
    handleWishlistOnClick,
    userOptionIsOpen,
    handleUserOptionToggle,
    userOptionRef,
    setUserOptionIsOpen,
    handleOnClickSelectTypeRadioButton,
    handleOnClickSearchTypeRadioButton,
    isMenuOpen,
    menuRef,
    handleMenuIconOnClick,
  } = useHeader();

  // 체크박스용 인자 타입 정의
  type SearchTypeProps<T extends FieldValues> = {
    label: string;
    name: Path<T>;
    state: boolean;
    value: string;
  };

  // 검색 종류 체크박스용 인자값 리스트
  const searchTypeList: SearchTypeProps<HeaderType>[] = [
    {
      label: 'info.animation',
      name: 'aniFlg',
      state: aniFlg!,
      value: SEARCH_TYPE.ANI,
    },
    {
      label: 'info.drama',
      name: 'dramaFlg',
      state: dramaFlg!,
      value: SEARCH_TYPE.DRAMA,
    },
    {
      label: 'info.movie',
      name: 'movieFlg',
      state: movieFlg!,
      value: SEARCH_TYPE.MOVIE,
    },
    {
      label: 'info.documentary',
      name: 'documentaryFlg',
      state: documentaryFlg!,
      value: SEARCH_TYPE.DOCUMENTARY,
    },
    {
      label: 'info.kids',
      name: 'kidsFlg',
      state: kidsFlg!,
      value: SEARCH_TYPE.KIDS,
    },
    {
      label: 'info.news',
      name: 'newsFlg',
      state: newsFlg!,
      value: SEARCH_TYPE.NEWS,
    },
    {
      label: 'info.variety',
      name: 'varietyFlg',
      state: varietyFlg!,
      value: SEARCH_TYPE.VARIETY,
    },
    {
      label: 'info.comics',
      name: 'comicsFlg',
      state: comicsFlg!,
      value: SEARCH_TYPE.COMICS,
    },
  ];

  // 검색 종류 선택 방식 라디오 버튼용 인자값 리스트
  const radioButtonList: RadioButtonProps[] = [
    {
      label: t('info.multiple'),
      value: SELECT_TYPE.MULTIPLE,
    },
    {
      label: t('info.single'),
      value: SELECT_TYPE.SINGLE,
    },
  ];

  // 유저 닉네임
  const userNickname = user?.nickname ?? '';
  // 닉네임 길이 제한
  const DISPLAY_LENGTH = 9;
  // 닉네임이 길이 제한을 초과하는지 여부
  const isHideNickname = userNickname.length > DISPLAY_LENGTH;
  // 화면에 표시할 닉네임
  const displayUserNickname = isHideNickname
    ? userNickname.slice(0, DISPLAY_LENGTH) + OMISSION_TEXT
    : userNickname;
  // 닉네임 툴팁 상태 상수
  const NICKNAME_TOOLTIP_OPEN_STATE = 1;
  // 홈 툴팁 상태 상수
  const HOME_TOOLTIP_OPEN_STATE = 2;
  // 위시리스트 툴팁 상태 상수
  const WISHLIST_TOOLTIP_OPEN_STATE = 3;

  return (
    <header className="fixed inset-x-0 top-0 z-50 border-b border-black/10 bg-white">
      <div className="container mx-auto px-4 md:px-6 lg:px-8">
        <div className="flex h-14 items-center gap-3 sm:h-16">
          {isMobileOnly ? (
            <div>
              {/* 모바일 화면일 때 메뉴 아이콘 */}
              <button
                type="button"
                className="absolute cursor-pointer items-center justify-center w-8 h-8"
                aria-label="메뉴 아이콘"
                onClick={commonErrorHandler(handleMenuIconOnClick)}
              />
              <div className="cursor-pointer" ref={menuRef}>
                {isMenuOpen ? (
                  <div>
                    <TfiMenu className="h-8 w-8" aria-label="메뉴아이콘 열림" />
                    <div className="flex justify-center">
                      {/* 메뉴 열림 상태일 때 표시할 내용 */}
                      <div
                        className={`absolute z-50 mt-2 w-28 gap-y-10 rounded border bg-white p-1 shadow-2xl left-1`}
                      >
                        {/* 홈 */}
                        <div className="py-1 text-sm text-gray-700 hover:bg-gray-200">
                          <button
                            type="button"
                            className="w-full cursor-pointer"
                            onClick={() => { 
                              commonErrorHandler(handleHomeOnClick)();
                              handleMenuIconOnClick();
                            }}
                          >
                            {t('info.home')}
                          </button>
                        </div>
                        {/* 위시리스트 */}
                        <div className="py-1 text-sm text-gray-700 hover:bg-gray-200">
                          <button
                            type="button"
                            className="w-full cursor-pointer"
                            onClick={() => { 
                              commonErrorHandler(handleWishlistOnClick)();
                              handleMenuIconOnClick();
                            }}
                          >
                            {t('info.wishlist')}
                          </button>
                        </div>
                      </div>
                    </div>
                  </div>
                ) : (
                  <TfiMenu className="h-8 w-8" aria-label="메뉴아이콘 닫힘" />
                )}
              </div>
            </div>
          ) : (
            // 데스크탑 화면일 때 아이콘들
            <>
              {/* 좌측: 아이콘들 */}
              <div className="flex items-center gap-4 sm:gap-6">
                {/* 홈 아이콘 */}
                <button
                  type="button"
                  aria-label={t('info.home') || 'Home'}
                  className="relative inline-flex cursor-pointer items-center justify-center"
                  onClick={commonErrorHandler(handleHomeOnClick)}
                  onMouseEnter={() => setIsTooltipOpen(HOME_TOOLTIP_OPEN_STATE)}
                  onMouseLeave={() => setIsTooltipOpen(TOOLTIP_CLOSE_STATE)}
                >
                  <img
                    src={homeIcon}
                    className="h-8 w-8 sm:h-10 sm:w-10 lg:h-12 lg:w-12"
                    alt=""
                  />
                  {/* 홈 툴팁 */}
                  {isTooltipOpen === HOME_TOOLTIP_OPEN_STATE && (
                    <TooltipUi text={t('info.home')} />
                  )}
                </button>
                {/* 위시리스트 아이콘 */}
                <button
                  type="button"
                  aria-label={t('info.wishlist') || 'Wishlist'}
                  className="relative inline-flex cursor-pointer items-center justify-center"
                  onClick={commonErrorHandler(handleWishlistOnClick)}
                  onMouseEnter={() =>
                    setIsTooltipOpen(WISHLIST_TOOLTIP_OPEN_STATE)
                  }
                  onMouseLeave={() => setIsTooltipOpen(TOOLTIP_CLOSE_STATE)}
                >
                  <FaHeart className="h-8 w-8 sm:h-10 sm:w-10 lg:h-12 lg:w-12" />
                  {/* 위시리스트 툴팁 */}
                  {isTooltipOpen === WISHLIST_TOOLTIP_OPEN_STATE && (
                    <TooltipUi text={t('info.wishlist')} className="w-22" />
                  )}
                </button>
              </div>
            </>
          )}

          {/* 우측: 검색창 및 필터 아이콘 */}
          <div className="ml-auto flex min-w-0 items-center gap-2 sm:gap-3">
            <div
              className="relative w-52 sm:w-60 md:w-80 lg:w-96"
              ref={autoCompleteRef}
            >
              <div>
                {/* 검색창 */}
                <SearchTextUi
                  control={control}
                  name="keyword"
                  onClick={commonErrorHandler(handleSearchOnClick)}
                  onMouseDown={handleKeywordOnKeyDown}
                  onKeyDown={handleKeywordOnKeyDownEvent}
                  isFocusedRef={isFocusedRef}
                  deleteValue={handleDeleteKeyword}
                />
              </div>
              {/* 자동완성창 */}
              <AutoCompleteBox
                autoCompleteList={autoCompleteList}
                handleKeywordListOnClick={commonErrorHandler(
                  handleKeywordListOnClick
                )}
                currentIndex={currentIndex}
                selectRef={selectRef}
                handleRemoveSearchHistory={handleRemoveSearchHistory}
                searchHistoryisOpen={searchHistoryisOpen}
                handleSetCurrentIndex={handleSetCurrentIndex}
                savedKeyword={savedKeyword}
              />
            </div>
            {/* 필터 아이콘 */}
            <div className="relative" ref={filterRef}>
              {isFilterOpen ? (
                <>
                  {/* 검은색 필터 아이콘 */}
                  <BsFilterSquareFill
                    className="h-7 w-7 cursor-pointer sm:h-8 sm:w-8 lg:h-9 lg:w-9"
                    onClick={commonErrorHandler(handleFilterIconOnClick)}
                  />
                  {/* 필터 팝업 */}
                  <div className="absolute right-0 z-50 mt-2 w-52 rounded border bg-white p-4 shadow-2xl md:w-60">
                    {/* 검색 종류 */}
                    <div className="text-lg font-bold sm:text-xl">
                      {t('info.searchType')}
                    </div>
                    <div className="mt-3">
                      {/* 검색 종류 선택 방식 라디오 버튼 그룹 */}
                      <RadioButtonGroupUi
                        name="selectType"
                        control={control}
                        radioButtonList={radioButtonList}
                        displayStyle="flex"
                        onClickRadioButton={commonErrorHandler(
                          handleOnClickSelectTypeRadioButton
                        )}
                      />
                    </div>
                    <div className="mt-3">
                      {selectType === radioButtonList[0].value ? (
                        // 검색 종류 체크박스
                        <div className="mb-2">
                          {searchTypeList.map((items) => {
                            return (
                              <div key={items.value}>
                                <CheckBoxUi
                                  label={t(items.label)}
                                  name={items.name}
                                  control={control}
                                  defaultChecked={items.state}
                                />
                              </div>
                            );
                          })}
                        </div>
                      ) : (
                        // 단일 선택시 검색 종류 라디오 버튼 그룹
                        <RadioButtonGroupUi
                          name="searchType"
                          control={control}
                          radioButtonList={searchTypeList.map((item) => ({
                            label: t(item.label),
                            value: item.value,
                          }))}
                          displayStyle="block"
                          onClickRadioButton={commonErrorHandler(
                            handleOnClickSearchTypeRadioButton
                          )}
                        />
                      )}
                    </div>
                    {/* 성인물 포함 체크박스 */}
                    <div className="mt-4">
                      <div className="text-xl font-bold">
                        {t('info.searchAdultContent')}
                      </div>
                      <div className="mt-2">
                        <CheckBoxUi
                          label={t('info.include')}
                          name={'adultFlg'}
                          control={control}
                          defaultChecked={adultFlg}
                          disabled={settings.isBlockingAdultContent}
                        />
                      </div>
                    </div>
                  </div>
                </>
              ) : (
                // 하얀색 필터 아이콘
                <BsFilterSquare
                  className="h-7 w-7 cursor-pointer sm:h-8 sm:w-8 lg:h-9 lg:w-9"
                  onClick={commonErrorHandler(handleFilterIconOnClick)}
                />
              )}
            </div>
          </div>

          {/* 유저/로그인 */}
          <div className="max-w-16 md:max-w-24">
            {user ? (
              <div className="relative" ref={userOptionRef}>
                {/* 유저 닉네임 */}
                <button
                  type="button"
                  className="flex cursor-pointer items-center text-sm break-words text-yellow-600 md:text-base"
                  onClick={handleUserOptionToggle}
                  onMouseEnter={() =>
                    isHideNickname &&
                    setIsTooltipOpen(NICKNAME_TOOLTIP_OPEN_STATE)
                  }
                  onMouseLeave={() =>
                    isHideNickname && setIsTooltipOpen(TOOLTIP_CLOSE_STATE)
                  }
                >
                  {displayUserNickname}
                  {/* 유저 닉네임 툴팁 */}
                  {isTooltipOpen === NICKNAME_TOOLTIP_OPEN_STATE && (
                    <TooltipUi
                      text={userNickname}
                      align="end"
                      className="top-full right-0 mt-2"
                    />
                  )}
                </button>
                {/* 유저 옵션 팝업 */}
                {userOptionIsOpen && (
                  <div className="absolute right-0 z-50 mt-3 w-32 rounded border bg-white p-1 shadow-2xl">
                    {/* 마이페이지(코멘트 관리) */}
                    <div className="py-1 text-sm text-gray-700 hover:bg-gray-200">
                      <button
                        className="w-full cursor-pointer"
                        onClick={commonErrorHandler(() => {
                          const myCommentsUrl = myCommentsUrlQuery({
                            userId: user.userId,
                          });
                          setUserOptionIsOpen(false);
                          navigate(myCommentsUrl);
                        })}
                      >
                        {t('info.myComments')}
                      </button>
                    </div>
                    {/* 로그아웃 */}
                    <div className="py-1 text-sm text-gray-700 hover:bg-gray-200">
                      <button
                        className="w-full cursor-pointer"
                        onClick={commonErrorHandler(handleLogoutOnClick)}
                      >
                        {t('info.logout')}
                      </button>
                    </div>
                  </div>
                )}
              </div>
            ) : (
              // 로그인 버튼
              <button
                className="text-foreground cursor-pointer text-base hover:opacity-80 sm:text-lg lg:text-2xl"
                onClick={commonErrorHandler(() => handleLoginOnClick())}
              >
                {t('info.login')}
              </button>
            )}
          </div>
        </div>
      </div>
    </header>
  );
};

/**
 * 자동완성 박스 컴포넌트
 * @param AutoCompletePropsType
 * @returns 자동완성 박스
 */
const AutoCompleteBox = memo(
  ({
    autoCompleteList,
    handleKeywordListOnClick,
    currentIndex,
    selectRef,
    handleRemoveSearchHistory,
    searchHistoryisOpen,
    handleSetCurrentIndex,
    savedKeyword,
  }: AutoCompletePropsType) => {
    // i18n 훅
    const { t } = useTranslation();
    // 강조 표시 스타일
    const highLightStyle = 'font-black text-blue-600';
    // 탭 문자
    const tab = '\t';

    return (
      <>
        {autoCompleteList && autoCompleteList.length !== 0 && (
          <div
            className={`absolute inset-x-0 z-50 mt-2 h-auto w-full overflow-auto rounded border bg-white p-2 shadow-2xl`}
          >
            {autoCompleteList.map((item, index) => {
              // 자동완성 리스트에서 검색어를 기준으로 배열화
              const keywordArray =
                item.search(new RegExp(savedKeyword, 'gi')) === -1
                  ? [item]
                  : item
                    .replaceAll(
                      new RegExp(savedKeyword, 'gi'),
                      tab.concat(savedKeyword).concat(tab)
                    )
                    .split(tab);

              // 현재 인덱스와 비교하여 활성화 상태 결정
              const isActive = index === currentIndex;

              return (
                <ul
                  key={index + '-' + item}
                  className={`mb-1 ${isActive ? 'bg-gray-200 hover:bg-gray-200' : ''} ${searchHistoryisOpen ? 'flex justify-between' : ''}`}
                  onMouseEnter={() => handleSetCurrentIndex(index)} // 현재 인덱스 설정
                  onMouseLeave={() => handleSetCurrentIndex(-1)} // 현재 인덱스 초기화
                >
                  <li
                    className={`${searchHistoryisOpen ? 'w-[85%]' : ''} cursor-pointer text-xs md:text-base`}
                    ref={isActive ? selectRef : null}
                  >
                    <button
                      className="w-full cursor-pointer text-left"
                      onClick={commonErrorHandler(() =>
                        handleKeywordListOnClick(item)
                      )}
                    >
                      {
                        // 검색 기록이 표시된 상태에서는 전체 아이템 표시
                        searchHistoryisOpen ? (
                          item
                        ) : (
                          // 검색 기록이 표시되지 않은 상태에서 검색어가 포함된 부분을 강조 표시
                          <>
                            {keywordArray.map((text, textIndex) => {
                              return (
                                <span
                                  key={textIndex + '-' + text}
                                  className={`${text === savedKeyword ? highLightStyle : ''}`}
                                >
                                  {text === savedKeyword ? savedKeyword : text}
                                </span>
                              );
                            })}
                          </>
                        )
                      }
                    </button>
                  </li>
                  {
                    // 검색 기록이 표시된 상태에서 삭제 버튼 표시
                    searchHistoryisOpen && (
                      <li className="flex w-[15%] justify-end">
                        <button
                          className="cursor-pointer text-xs text-gray-400 underline"
                          onClick={commonErrorHandler(() =>
                            handleRemoveSearchHistory(index)
                          )}
                        >
                          {t('info.delete')}
                        </button>
                      </li>
                    )
                  }
                </ul>
              );
            })}
          </div>
        )}
      </>
    );
  }
);
