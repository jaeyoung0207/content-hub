import { useTranslation } from 'react-i18next';
import homeIcon from '@assets/icons/home.png';
import { SearchTextUi } from '@/components/ui/SearchTextUi';
import { useHeader } from './hooks/useHeader';
import { CheckBoxUi } from '@/components/ui/CheckBoxUi';
import { BsFilterSquare, BsFilterSquareFill } from 'react-icons/bs';
import { FieldValues, Path } from 'react-hook-form';
import { FormFieldProps } from '@/components/ui/common/FormFieldProps';
import {
  IS_MOBILE,
  OMMIT_TEXT,
  SEARCH_TYPE,
  SELECT_TYPE,
  TOOLTIP_CLOSE_STATE,
} from '@/components/common/constants/constants';
import { CheckBoxUiM } from '@/components/ui/CheckBoxUiM';
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

/**
 * 자동완성 박스 컴포넌트 props 타입
 */
type autoCompletePropsType = {
  autoCompleteList?: string[];
  autoCompoleteRef?: RefObject<HTMLDivElement | null>;
  handleKeywordListOnClick: (item: string) => void;
  currentIndex: number;
  selectRef: RefObject<HTMLLIElement | null>;
  handleRemoveSearchHistory: (index: number) => void;
  searchHistoryisOpen: boolean;
  handleSetCurrentIndex: (index: number) => void;
  savedKeyword: string;
};

/**
 * 헤더 컴포넌트
 * 헤더는 검색창, 필터 아이콘, 로그인/로그아웃 버튼 등을 포함
 * 헤더는 페이지 상단에 고정되어 있으며, 사용자가 사이트를 탐색할 때 항상 표시됨
 */
export const Header = () => {
  // i18n 번역 함수
  const { t } = useTranslation();

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
    handleOnClickSelectTypeRadioButton,
    handleOnClickSearchTypeRadioButton,
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
  const isHideNickname =
    userNickname && userNickname.length > DISPLAY_LENGTH ? true : false;
  // 닉네임 툴팁 상태 상수
  const NICKNAME_TOOLTIP_OPEN_STATE = 1;
  // 홈 툴팁 상태 상수
  const HOME_TOOLTIP_OPEN_STATE = 2;
  // 위시리스트 툴팁 상태 상수
  const WISHLIST_TOOLTIP_OPEN_STATE = 3;

  return (
    <div className="relative z-50 flex">
      <div className="fixed bg-white">
        <div className="flex w-sm lg:w-7xl lg:mt-5 px-3 ">
          <div className="w-[58%] flex items-center mr-5">
            {/* 홈 아이콘 */}
            <div className="mr-8">
              <img
                src={homeIcon}
                className="w-12 h-12 cursor-pointer"
                alt="Home"
                onClick={commonErrorHandler(() => {
                  handleHomeOnClick();
                })}
                onMouseEnter={() => setIsTooltipOpen(HOME_TOOLTIP_OPEN_STATE)}
                onMouseLeave={() => setIsTooltipOpen(TOOLTIP_CLOSE_STATE)}
              />
              {/* 홈 툴팁 */}
              {isTooltipOpen === HOME_TOOLTIP_OPEN_STATE && (
                <TooltipUi text={t('info.home')} style={'left-4 mt-2 w-10'} />
              )}
            </div>
            {/* 위시리스트 아이콘 */}
            <div className="mr-8">
              <FaHeart
                className="w-12 h-12 cursor-pointer"
                onClick={commonErrorHandler(handleWishlistOnClick)}
                onMouseEnter={() =>
                  setIsTooltipOpen(WISHLIST_TOOLTIP_OPEN_STATE)
                }
                onMouseLeave={() => setIsTooltipOpen(TOOLTIP_CLOSE_STATE)}
              />
              {/* 위시리스트 툴팁 */}
              {isTooltipOpen === WISHLIST_TOOLTIP_OPEN_STATE && (
                <TooltipUi
                  text={t('info.wishlist')}
                  style={'left-18 mt-2 w-22'}
                />
              )}
            </div>
          </div>
          <div className="w-[42%] flex items-center">
            <div className="relative block" ref={autoCompleteRef}>
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
            <div className="ml-3">
              <div className="relative" ref={filterRef}>
                {isFilterOpen ? (
                  <>
                    {/* 검은색 필터 아이콘 */}
                    <BsFilterSquareFill
                      className="w-9 h-9 mr-1 cursor-pointer"
                      onClick={commonErrorHandler(handleFilterIconOnClick)}
                    />
                    {/* 필터 팝업 */}
                    <div className="absolute right-0 mt-2 w-64 bg-white border rounded shadow-2xl z-50 p-4 space-y-4">
                      {/* 검색 종류 */}
                      <div className="text-xl font-bold mb-2">
                        {t('info.searchType')}
                      </div>
                      <div className="flex mb-2">
                        {/* 검색 종류 선택 방식 라디오 버튼 그룹 */}
                        <RadioButtonGroupUi
                          name="selectType"
                          control={control}
                          radioButtonList={radioButtonList}
                          displayStyle="flex"
                          onClickRadioButton={
                            commonErrorHandler(handleOnClickSelectTypeRadioButton)
                          }
                        />
                      </div>
                      <div className="block mb-2">
                        {selectType === radioButtonList[0].value ? (
                          // 검색 종류 체크박스
                          searchTypeList.map((items, index) => {
                            return (
                              <div key={index} className="mb-1">
                                <CheckBoxAndLabel<HeaderType>
                                  label={t(items.label)}
                                  name={items.name}
                                  control={control}
                                  defaultChecked={items.state}
                                />
                              </div>
                            );
                          })
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
                            onClickRadioButton={
                              commonErrorHandler(handleOnClickSearchTypeRadioButton)
                            }
                          />
                        )}
                      </div>
                      {/* 성인물 포함 체크박스 */}
                      <div>
                        <div className="text-xl font-bold mb-2">
                          {t('info.searchAdultContent')}
                        </div>
                        <CheckBoxUi
                          label={t('info.include')}
                          name={'adultFlg'}
                          control={control}
                          defaultChecked={adultFlg}
                          disabled={settings.isBlockingAdultContent}
                        />
                      </div>
                    </div>
                  </>
                ) : (
                  // 하얀색 필터 아이콘
                  <BsFilterSquare
                    className="w-9 h-9 mr-1 cursor-pointer"
                    onClick={commonErrorHandler(handleFilterIconOnClick)}
                  />
                )}
              </div>
            </div>
            <div className="ml-3">
              {user ? (
                <div className="block w-24" ref={userOptionRef}>
                  {/* 유저 닉네임 */}
                  <div
                    className="text-lg text-yellow-600 cursor-pointer"
                    onClick={handleUserOptionToggle}
                    onMouseEnter={() =>
                      isHideNickname &&
                      setIsTooltipOpen(NICKNAME_TOOLTIP_OPEN_STATE)
                    }
                    onMouseLeave={() =>
                      isHideNickname && setIsTooltipOpen(TOOLTIP_CLOSE_STATE)
                    }
                  >
                    {isHideNickname
                      ? userNickname.slice(0, DISPLAY_LENGTH) + OMMIT_TEXT
                      : userNickname}
                  </div>
                  {/* 유저 닉네임 툴팁 */}
                  {isTooltipOpen === NICKNAME_TOOLTIP_OPEN_STATE && (
                    <TooltipUi
                      text={userNickname}
                      style={'right-0 mt-2 w-32'}
                    />
                  )}
                  {/* 유저 옵션 팝업 */}
                  {userOptionIsOpen && (
                    <div className="absolute flex justify-center right-0 mt-2 w-30 bg-white border rounded shadow-2xl z-50 p-1">
                      {/* 로그아웃 */}
                      <div
                        className="px-4 py-1 text-sm text-gray-700 hover:bg-gray-200 cursor-pointer"
                        onClick={commonErrorHandler(handleLogoutOnClick)}
                      >
                        {t('info.logout')}
                      </div>
                    </div>
                  )}
                </div>
              ) : (
                // 로그인 버튼
                <div
                  className="right-full text-black text-2xl font-normal font-['Inter'] hover:text-shadow-md cursor-pointer"
                  onClick={commonErrorHandler(() => handleLoginOnClick())}
                >
                  {t('info.login')}
                </div>
              )}
            </div>
          </div>
        </div>
        <div className="mt-4 border-b border-gray-300" />
      </div>
    </div>
  );
};

/**
 * 체크박스와 라벨 컴포넌트
 * @param label 라벨 텍스트
 * @param name 체크박스 이름
 * @param control react-hook-form의 control 객체
 * @param defaultChecked 기본 체크 상태
 */
const CheckBoxAndLabelInner = <T extends FieldValues>({
  label,
  name,
  control,
  defaultChecked,
}: FormFieldProps<T>) => {
  return (
    <>
      {
        // IS_MOBILE이 true이면 CheckBoxUiM 컴포넌트 사용, false이면 CheckBoxUi 컴포넌트 사용
        IS_MOBILE ? (
          <CheckBoxUiM label={label ?? ''} name={name} control={control} />
        ) : (
          <CheckBoxUi
            label={label ?? ''}
            name={name}
            control={control}
            defaultChecked={defaultChecked}
          />
        )
      }
    </>
  );
};

/**
 * 체크박스와 라벨 컴포넌트
 * 제네릭 타입 보존하면서 memo 적용
 */
export const CheckBoxAndLabel = memo(
  CheckBoxAndLabelInner
) as typeof CheckBoxAndLabelInner;

/**
 * 자동완성 박스 컴포넌트
 * @param autoCompletePropsType
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
  }: autoCompletePropsType) => {
    // i18n 훅
    const { t } = useTranslation();

    const boxHeight = autoCompleteList?.length;
    const highLightStyle = 'font-black text-blue-600';
    const tab = '\\t';
    return (
      <>
        {autoCompleteList && autoCompleteList.length !== 0 && (
          <div
            className={`absolute lg:w-sm w-xs h-[${boxHeight}] right-0 mt-1 bg-white border rounded shadow-2xl z-50 p-2 `}
          >
            {autoCompleteList.map((item, index) => {
              // 자동완성 리스트에서 검색어를 기준으로 배열화
              const keywordArray =
                item.search(new RegExp(savedKeyword, 'gi')) !== -1
                  ? item
                      .replace(
                        new RegExp(savedKeyword, 'gi'),
                        tab.concat(savedKeyword).concat(tab)
                      )
                      .split(tab)
                  : [item];
              return (
                <ul
                  key={index}
                  className={`mb-1 ${index === currentIndex ? 'bg-gray-200 hover:bg-gray-200' : ''} ${searchHistoryisOpen ? 'flex justify-between' : ''}`}
                  onMouseEnter={() => handleSetCurrentIndex(index)} // 현재 인덱스 설정
                  onMouseLeave={() => handleSetCurrentIndex(-1)} // 현재 인덱스 초기화
                >
                  <li
                    className="w-[92%] cursor-pointer"
                    ref={index === currentIndex ? selectRef : null}
                    onClick={commonErrorHandler(() =>
                      handleKeywordListOnClick(item)
                    )}
                  >
                    {
                      // 검색 기록이 표시되지 않은 상태에서 검색어가 포함된 부분을 강조 표시
                      !searchHistoryisOpen ? (
                        <>
                          {keywordArray.map((text, textIndex) => {
                            return (
                              <span
                                key={textIndex}
                                className={`${text === savedKeyword ? highLightStyle : ''}`}
                              >
                                {text === savedKeyword ? savedKeyword : text}
                              </span>
                            );
                          })}
                        </>
                      ) : (
                        item
                      )
                    }
                  </li>
                  {
                    // 검색 기록이 표시된 상태에서 삭제 버튼 표시
                    searchHistoryisOpen && (
                      <li className="w-[8%] flex justify-end">
                        <button
                          className="text-xs underline text-gray-400 cursor-pointer"
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
