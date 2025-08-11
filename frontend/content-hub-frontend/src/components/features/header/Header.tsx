import { useTranslation } from "react-i18next"
import homeIcon from "@assets/icons/home.png"
import { useUserStore } from "@/components/common/store/globalStateStore";
import { SearchTextUi } from "@/components/ui/SearchTextUi";
import { HeaderType, useHeader } from "./useHeader";
import { CheckBoxUi } from "@/components/ui/CheckBoxUi";
import { BsFilterSquare, BsFilterSquareFill } from "react-icons/bs";
import { FieldValues, Path } from "react-hook-form";
import { FormFieldProps } from "@/components/ui/common/FormFieldProps";
import { IS_MOBILE } from "@/components/common/constants/constants";
import { CheckBoxUiM } from "@/components/ui/CheckBoxUiM";
import { memo, RefObject } from "react";
import { commonErrorHandler } from "@/components/common/utils/commonUtil";
import { settings } from "@/components/common/config/settings";

/**
 * 자동완성 박스 컴포넌트 props 타입
 */
type autoCompletePropsType = {
    autoCompleteList?: string[],
    autoCompoleteRef?: RefObject<HTMLDivElement | null>,
    handleKeywordListOnClick: (item: string) => void,
    currentIndex: number,
    selectRef: RefObject<HTMLLIElement | null>,
    handleRemoveSearchHistory: (index: number) => void,
    searchHistoryisOpen: boolean,
    handleSetCurrentIndex: (index: number) => void,
    savedKeyword: string,
}

/**
 * 헤더 컴포넌트
 * 헤더는 검색창, 필터 아이콘, 로그인/로그아웃 버튼 등을 포함
 * 헤더는 페이지 상단에 고정되어 있으며, 사용자가 사이트를 탐색할 때 항상 표시됨
 */
export const Header = () => {
    // 유저 정보 전역 상태 훅
    const { user } = useUserStore();
    // i18n 번역 함수
    const { t } = useTranslation();

    const {
        control,
        handleHomeOnClick,
        handleSearchOnClick,
        handleLoginOnClick,
        handleLogoutOnClick,
        isFilterOpen,
        handleFilterIconOnClick,
        aniFlg,
        comicsFlg,
        dramaFlg,
        movieFlg,
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
    } = useHeader();

    // 체크박스용 인자 타입 정의
    type CheckBoxProps<T extends FieldValues> = {
        label: string,
        name: Path<T>,
        state: boolean,
    }

    // name의 타입이 Path<T>이기 때문에, T가 무엇인지 정의
    type CheckboxType = {
        aniFlg: boolean;
        dramaFlg: boolean;
        movieFlg: boolean;
        comicsFlg: boolean;
        novelFlg: boolean;
    };

    // 검색 종류 체크박스용 인자값 리스트
    const checkBoxList: CheckBoxProps<CheckboxType>[] = [
        {
            label: "info.ani",
            name: "aniFlg",
            state: aniFlg!,
        },
        {
            label: "info.drama",
            name: "dramaFlg",
            state: dramaFlg!,
        },
        {
            label: "info.movie",
            name: "movieFlg",
            state: movieFlg!,
        },
        {
            label: "info.comics",
            name: "comicsFlg",
            state: comicsFlg!,
        },
    ]

    return (
        <div className="flex justify-between z-50 shadow-md">
            <div className="fixed bg-white">
                <div className="flex justify-between w-sm lg:w-7xl lg:mt-5 px-3 ">
                    {/* 홈 아이콘 */}
                    <img src={homeIcon} className="w-12 h-12 cursor-pointer" alt="Home" onClick={commonErrorHandler(() => {
                        handleHomeOnClick();
                    })} />
                    <div className="flex items-center">
                        <div className="mr-5 flex items-center">
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
                                    handleKeywordListOnClick={commonErrorHandler(handleKeywordListOnClick)}
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
                                    {
                                        isFilterOpen
                                            ? <>
                                                {/* 검은색 필터 아이콘 */}
                                                <BsFilterSquareFill className="w-9 h-9 mr-1 cursor-pointer" onClick={commonErrorHandler(handleFilterIconOnClick)} />
                                                {/* 필터 팝업 */}
                                                <div className="absolute right-0 mt-2 w-64 bg-white border rounded shadow-2xl z-50 p-4 space-y-4">
                                                    {/* 체크박스 */}
                                                    <div className="text-xl font-bold mb-2">
                                                        {t("info.mediaType")}
                                                    </div>
                                                    <div className="block mb-2">
                                                        {
                                                            // 검색 종류 체크박스
                                                            // 체크박스 리스트를 map을 통해 순회하며 체크박스와 라벨 컴포넌트를 생성
                                                            checkBoxList.map((items, index) => {
                                                                return (
                                                                    <div key={index} className="mb-2">
                                                                        <CheckBoxAndLabel<HeaderType> label={items.label} name={items.name} control={control} defaultChecked={items.state} />
                                                                    </div>
                                                                )
                                                            })
                                                        }
                                                    </div>
                                                    {/* 성인물 포함 체크박스 */}
                                                    <div>
                                                        <div className="text-xl font-bold mb-2">
                                                            {t("info.searchAdultContent")}
                                                        </div>
                                                        <CheckBoxUi label={t("info.include")} name={"adultFlg"} control={control} defaultChecked={adultFlg} disabled={settings.isBlockingAdultContent} />
                                                    </div>
                                                </div>
                                            </>
                                            :
                                            // 하얀색 필터 아이콘
                                            <BsFilterSquare className="w-9 h-9 mr-1 cursor-pointer" onClick={commonErrorHandler(handleFilterIconOnClick)} />
                                    }
                                </div>
                            </div>
                        </div>
                        {
                            user ?
                                <div className="block w-24">
                                    {/* 유저 닉네임 */}
                                    <div className="text-[16px] text-yellow-600">
                                        {user.nickname}
                                    </div>
                                    {/* 로그아웃 버튼 */}
                                    <div className="text-[16px] cursor-pointer" onClick={commonErrorHandler(handleLogoutOnClick)}>
                                        {t('info.logout')}
                                    </div>
                                </div>
                                :
                                // 로그인 버튼
                                <div className="right-full text-black text-2xl font-normal font-['Inter'] cursor-pointer" onClick={commonErrorHandler(() =>
                                    handleLoginOnClick()
                                )}>
                                    {
                                        t('info.login')
                                    }
                                </div>
                        }
                    </div>
                </div>
                <div className="mt-4 border-b border-gray-300" />
            </div>
        </div>
    )
}

/**
 * 체크박스와 라벨 컴포넌트
 * @param label 라벨 텍스트
 * @param name 체크박스 이름
 * @param control react-hook-form의 control 객체
 * @param defaultChecked 기본 체크 상태
 */
const CheckBoxAndLabelInner = <T extends FieldValues>({ label, name, control, defaultChecked }: FormFieldProps<T>) => {
    return (
        <>
            {
                // IS_MOBILE이 true이면 CheckBoxUiM 컴포넌트 사용, false이면 CheckBoxUi 컴포넌트 사용
                IS_MOBILE
                    ? <CheckBoxUiM label={label ?? ""} name={name} control={control} />
                    : <CheckBoxUi label={label ?? ""} name={name} control={control} defaultChecked={defaultChecked} />
            }
        </>
    )
}

/**
 * 체크박스와 라벨 컴포넌트
 * 제네릭 타입 보존하면서 memo 적용
 */
export const CheckBoxAndLabel = memo(CheckBoxAndLabelInner) as typeof CheckBoxAndLabelInner;

/**
 * 자동완성 박스 컴포넌트
 * @param autoCompletePropsType
 * @returns 자동완성 박스
 */
const AutoCompleteBox = memo(({ autoCompleteList, handleKeywordListOnClick, currentIndex, selectRef, handleRemoveSearchHistory, searchHistoryisOpen, handleSetCurrentIndex, savedKeyword }: autoCompletePropsType) => {
    // i18n 훅
    const { t } = useTranslation();

    const boxHeight = autoCompleteList?.length;
    const highLightStyle = "font-black text-blue-600";
    const tab = "\\t";
    return (
        <>
            {
                autoCompleteList &&
                autoCompleteList.length !== 0 &&
                // <div className={`absolute lg:w-sm w-xs max-h-64 overflow-auto right-0 mt-1 bg-white border rounded shadow-2xl z-50 p-2 `}>
                <div className={`absolute lg:w-sm w-xs h-[${boxHeight}] right-0 mt-1 bg-white border rounded shadow-2xl z-50 p-2 `}>
                    {
                        autoCompleteList.map((item, index) => {
                            // 자동완성 리스트에서 검색어를 기준으로 배열화
                            const keywordArray = item.search(new RegExp(savedKeyword, "gi")) !== -1
                                ? item.replace(new RegExp(savedKeyword, "gi"), tab.concat(savedKeyword).concat(tab)).split(tab)
                                : [item];
                            return (
                                <ul
                                    key={index}
                                    className={`mb-1 ${index === currentIndex ? "bg-gray-200 hover:bg-gray-200" : ""} ${searchHistoryisOpen ? "flex justify-between" : ""}`}
                                    onMouseEnter={() => handleSetCurrentIndex(index)} // 현재 인덱스 설정
                                    onMouseLeave={() => handleSetCurrentIndex(-1)} // 현재 인덱스 초기화
                                >
                                    <li
                                        className="w-[92%] cursor-pointer"
                                        ref={index === currentIndex ? selectRef : null}
                                        onClick={commonErrorHandler(() => handleKeywordListOnClick(item))}
                                    >
                                        {
                                            // 검색 기록이 표시되지 않은 상태에서 검색어가 포함된 부분을 강조 표시
                                            !searchHistoryisOpen
                                                ? <>
                                                    {
                                                        keywordArray.map((text, textIndex) => {
                                                            return (
                                                                <span key={textIndex} className={`${text === savedKeyword ? highLightStyle : ""}`}>
                                                                    {
                                                                        text === savedKeyword
                                                                            ? savedKeyword
                                                                            : text
                                                                    }
                                                                </span>
                                                            )
                                                        })
                                                    }
                                                </>
                                                : item
                                        }
                                    </li>
                                    {
                                        // 검색 기록이 표시된 상태에서 삭제 버튼 표시
                                        searchHistoryisOpen &&
                                        <li className="w-[8%] flex justify-end">
                                            <button className="text-xs underline text-gray-400 cursor-pointer" onClick={commonErrorHandler(() => handleRemoveSearchHistory(index))}>{t("info.delete")}</button>
                                        </li>
                                    }
                                </ul>
                            )
                        })
                    }
                </div>
            }
        </>
    )
});