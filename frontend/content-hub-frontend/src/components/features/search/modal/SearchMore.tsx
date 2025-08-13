import { MEDIA_TYPE, TMDB_API_IMAGE_DOMAIN, WIDTH_300, COMMON_IMAGES } from "@/components/common/constants/constants";
import { CloseButtonUi } from "@/components/ui/common/CloseButtonUi";
import { UseInfiniteQueryResultType, useSearchMore } from "./useSearchMore";
import { useTranslation } from "react-i18next";
import { SearchCommonResultType } from "../useSearch";
import { memo } from "react";
import { LoadingUi } from "@/components/ui/LoadingUi";
import { useNavigate } from "react-router-dom";
import { SearchPropsType } from "../SearchPage";
import { commonErrorHandler, detailUrlQuery, isSearchTvType } from "@/components/common/utils/commonUtil";

/**
 * 전체보기 모달화면 컴포넌트 props 타입
 */
export type SearchModalPropsType = {
    keyword: string | null | undefined,
    mediaType: string,
    getDatailData: (params: SearchCommonResultType, mediaType: string) => void,
    detailResult?: SearchCommonResultType | undefined,
}

/**
 * 각 미디어 검색결과 컴포넌트 props 타입
 */
type SearchResultsModalPropsType = {
    media: string,
    results: UseInfiniteQueryResultType,
    keyword: string,
    isViewMore?: boolean,
    setCurrentContentType?: (mediaType: string) => void,
    mediaType: string,
    modalSearchAllOpen?: () => void,
    isAdult: string,
}

/**
 * 전체보기 모달화면 컴포넌트
 * @param keyword 검색어
 * @param isAdult 성인 콘텐츠 포함 여부
 * @param mediaType 미디어 타입
 */
export const SearchMore = memo(({ keyword, isAdult, mediaType }: SearchPropsType) => {
    // i18n 번역 함수
    const { t } = useTranslation();
    // 검색 결과를 가져오는 커스텀 훅
    const {
        setObserveTarget,
        data,
        hasNextPage,
        isFetchingNextPage,
        handleModalClose,
    } = useSearchMore(keyword, mediaType!);

    // 각 미디어 이름을 가져오는 함수
    const getMediaName = () => {
        if (mediaType === MEDIA_TYPE.ANI) {
            return "info.ani";
        } else if (mediaType === MEDIA_TYPE.DRAMA) {
            return "info.drama";
        } else if (mediaType === MEDIA_TYPE.MOVIE) {
            return "info.movie";
        } else if (mediaType === MEDIA_TYPE.COMICS) {
            return "info.comics";
        } else {
            return "info.novel";
        }
    }

    return (
        <>
            {
                <div className="flex justify-center items-center fixed top-0 left-0 w-full h-full bg-black/30 z-50">
                    <div className="w-full max-w-md md:max-w-4xl lg:max-w-7xl h-11/12 bg-white rounded-xl overflow-auto mx-auto mt-10">
                        <div className="mb-5 p-4">
                            {/* 닫기 버튼 */}
                            <CloseButtonUi modalClose={handleModalClose} />
                            {
                                data
                                    ? <div>
                                        {/* 각 미디어 검색결과 컴포넌트 */}
                                        <DisplayAllResults media={t(getMediaName())} results={data} mediaType={mediaType!} keyword={keyword} isAdult={isAdult!} />
                                        {
                                            // 다음 페이지 로딩 중인 경우 로딩 UI 표시
                                            isFetchingNextPage &&
                                            <LoadingUi />
                                        }
                                        {
                                            // 다음 페이지가 있는 경우 무한 스크롤을 위한 div 태그
                                            hasNextPage &&
                                            <div ref={(el) => setObserveTarget(el)} ></div> // ref를 함수 형태로 지정 -> DOM이 생기거나 없어질 때마다 실행되면서 setObserveTarget을 호출
                                        }
                                    </div>
                                    : <LoadingUi />
                            }
                        </div>
                    </div>
                </div>
            }
        </>
    )
})

/**
 * 각 미디어 검색결과 컴포넌트
 * 전체보기 모달화면에서 검색 결과를 렌더링하는 컴포넌트
 * @param media 미디어 이름
 * @param results 검색 결과 데이터
 * @param mediaType 미디어 타입
 * @param keyword 검색어
 * @param isAdult 성인 콘텐츠 포함 여부
 */
const DisplayAllResults = memo(({ media, results, mediaType, keyword, isAdult }: SearchResultsModalPropsType) => {

    // navigate 훅
    const navigate = useNavigate();
    // i18n 번역 함수
    const {t} = useTranslation();
    // 상세보기 모달에서 사용할 썸네일 이미지 경로
    const thumbnailImagePath = TMDB_API_IMAGE_DOMAIN + WIDTH_300;

    return (
        <div className="w-full">
            {/* 키워드 미디어 검색 결과 */}
            <div className="text-3xl font-bold ml-5">
                "{keyword}" {media} {t("info.searchResults")}
            </div>
            {/* 검색 결과 */}
            <div className="flex flex-wrap items-start mt-6">
                {
                    results.pages.length !== 0 &&
                    results.pages.flat().map((items, index) => { // flat()을 이용하여 다차원 배열을 평탄화하여 1차원 배열로 만듦
                        return (
                            <ul
                                key={"frame" + index}
                                className={"ml-1 mr-1 block hover:font-bold cursor-pointer " + (mediaType === MEDIA_TYPE.COMICS ? "w-[190px]" : "w-[300px]")}
                                onClick={commonErrorHandler(() => {
                                    // const detailUrl = `/search?keyword=${keyword}&isAdult=${isAdult}&viewMore=${VIEW_MORE_TYPE(mediaType)}&mediaType=${mediaType}&originalMediaType=${items.originalMediaType}&contentId=${items.id}&tabNo=${0}`;
                                    const detailUrl = detailUrlQuery({ originalMediaType: items.originalMediaType!, contentId: String(items.id), tabNo: 0 });
                                    // 리다이렉트용 데이터 저장
                                    sessionStorage.setItem("redirectUrl", detailUrl);
                                    // 상세보기 모달 오픈
                                    navigate(detailUrl);
                                })}>
                                {/* 썸네일 */}
                                <li key={"poster_path" + index} className="flex justify-center items-center w-full">
                                    <img
                                        src={
                                            mediaType === MEDIA_TYPE.COMICS
                                                ? items.backdropPath
                                                : (items.backdropPath ? thumbnailImagePath + items.backdropPath : thumbnailImagePath + items.posterPath)
                                        }
                                        onError={(e) => {
                                            e.currentTarget.src = COMMON_IMAGES.NO_IMAGE;
                                        }}
                                        // 만화인 경우와 그 이외의 경우에 따라 이미지 크기를 다르게 설정
                                        className={(mediaType === MEDIA_TYPE.COMICS ? "w-[190px] h-[270px]" : "w-full h-[180px]") + " object-scale-down"}
                                        alt={"Thumbnail Image"}
                                    />
                                </li>
                                {/* 제목 */}
                                <li key={"title" + index} className="ml-1 mr-1 mb-4 text-xl">
                                    {
                                        isSearchTvType(items, mediaType) ? items.name : items.title
                                    }
                                </li>
                            </ul>
                        )
                    })
                }
            </div>
        </div>
    )
})