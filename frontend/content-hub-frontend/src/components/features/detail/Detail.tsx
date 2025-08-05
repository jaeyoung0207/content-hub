import { MEDIA_TYPE, IS_MOBILE, TMDB_API_IMAGE_DOMAIN, WIDTH_300 } from "@/components/common/constants/constants";
import { CloseButtonUi } from "@/components/ui/common/CloseButtonUi";
import { memo } from "react";
import { useTranslation } from "react-i18next";
import { ContentInfomation } from "./tabs/contentInformation/ContentInfomation";
import NoImageDetailFile from "@assets/images/no_image_available_detail.png";
import { useDetail } from "./useDetail";
import { ContentComment } from "./tabs/contentComment/ContentComment";
import { useSearchParams } from "react-router-dom";
import { SearchContentPropsType } from "../searchContent/SearchContentPage";
import { LoadingUi } from "@/components/ui/LoadingUi";
import { RecommendContent } from "./tabs/recommendContent/RecommendContent";
import { commonErrorHandler, isDetailComicsType, isDetailMovieType, isDetailTvType } from "@/components/common/utils/commonUtil";

/**
 * 상세 화면 컴포넌트
 * @param originalMediaType 원본 미디어 타입
 * @param contentId 컨텐츠 ID
 * @param tabNo 탭 번호
 */
export const Detail = memo(({ originalMediaType, contentId, tabNo }: SearchContentPropsType) => {

    // URL query string 값을 가져오기 위한 useSearchParams 훅
    const [searchParams, setSearchParams] = useSearchParams();

    // 컨텐츠ID가 존재하지 않는 경우, 렌더링 중지
    if (!contentId) return null;

    // useDetail 훅을 사용하여 상세 정보 조회
    const {
        tabIndex,
        setTabIndex,
        handleModalClose,
        data,
        isLoading,
        isError,
        userStarRating,
    } = useDetail(originalMediaType!, contentId!, tabNo!);

    // i18n 훅
    const { t } = useTranslation();

    // 탭 정보 배열
    const tabInfo = [
        {
            id: 0,
            tabTitle: t("info.mediaInfo"),
        },
        {
            id: 1,
            tabTitle: t("info.review"),
        },
        {
            id: 2,
            tabTitle: t("info.recommend"),
        },
    ]

    // 작품 타입에 따라 조건부 렌더링을 위한 변수 설정
    const isTvType = data && originalMediaType && isDetailTvType(data, originalMediaType);
    const isMovieType = data && originalMediaType && isDetailMovieType(data, originalMediaType);
    const isComicsType = data && originalMediaType && isDetailComicsType(data, originalMediaType);

    return (
        <>
            <div className="flex justify-center items-center fixed top-0 left-0 w-full h-full bg-black/30 z-50">
                <div className="w-full max-w-md md:max-w-4xl lg:max-w-7xl h-11/12 bg-white rounded-xl overflow-auto mx-auto mt-10">
                    <div className="mt-10 mb-10">
                        {/* 닫기 버튼 */}
                        <CloseButtonUi modalClose={handleModalClose} />

                        {/* 탭 버튼 */}
                        <div className="flex justify-center items-center border-b border-gray-300">
                            {
                                // 탭 정보 배열 수만큼 반복하여 탭 버튼 생성
                                tabInfo.map((tabInfo, index) => {
                                    const isActive = tabIndex === tabInfo.id;
                                    return (
                                        <div key={index}
                                            className={`text-sm sm:text-lg md:text-xl px-4 py-2 mx-2 transition duration-200
                                            ${isActive
                                                    ? "font-bold border-b-4 border-blue-500 text-blue-600"
                                                    : "text-gray-500 hover:text-blue-500 hover:border-b-4 hover:border-blue-300"
                                                }`}
                                        >
                                            <button
                                                role="tab"
                                                className={"text-2xl cursor-pointer " + (tabIndex === tabInfo.id && "font-bold")}
                                                onClick={commonErrorHandler(() => {
                                                    // URL query string에 tabNo를 설정
                                                    searchParams.set("tabNo", String(tabInfo.id));
                                                    setSearchParams(searchParams);
                                                    // 탭 인덱스 상태 설정
                                                    setTabIndex(tabInfo.id);
                                                })}>
                                                {tabInfo.tabTitle}
                                            </button>
                                        </div>
                                    )
                                })
                            }
                        </div>

                        {/* 작품 공통 정보 */}
                        <div className="flex justify-center m-5">
                            {
                                // 로딩 중이면 로딩 UI 표시, 에러가 발생하면 에러 메시지 표시
                                isLoading
                                    ?
                                    <LoadingUi />
                                    :
                                    isError &&
                                    <div className="mt-60 text-3xl">
                                        {t("warn.noData")}
                                    </div>
                            }
                            {
                                data && originalMediaType &&
                                <>
                                    {/* 작품 이미지 */}
                                    <div className="flex justify-center items-center mb-4 w-[30%]">
                                        <img
                                            // loading="lazy"
                                            src={
                                                originalMediaType === MEDIA_TYPE.COMICS
                                                    ? data?.posterPath
                                                    : TMDB_API_IMAGE_DOMAIN + (IS_MOBILE ? WIDTH_300 : WIDTH_300) + data?.posterPath // TODO
                                            }
                                            className={(IS_MOBILE ? "w-[200px]" : "w-[300px]") + " h-full"}
                                            onError={(e) => {
                                                e.currentTarget.src = NoImageDetailFile;
                                            }}
                                        />
                                    </div>
                                    {/* 제목 */}
                                    <div className="w-[70%] block">
                                        <div className="text-2xl mb-3 mr-3">
                                            {
                                                isTvType && data.name ||
                                                isMovieType && data.title ||
                                                isComicsType && data.title
                                            }
                                        </div>
                                        <div className="flex">
                                            <div className="w-[100%]">
                                                <ul className="mt-5 mb-5">
                                                    {/* 장르 */}
                                                    <li className="mb-2 flex">
                                                        <div className="mr-2">
                                                            {t("info.genre") + " : "}
                                                        </div>
                                                        <div>
                                                            {
                                                                (isTvType || isMovieType) &&
                                                                data.genres?.map((genre, index) => genre.name + (index + 1 !== data.genres?.length ? ", " : ""))
                                                            }
                                                            {
                                                                isComicsType &&
                                                                data.comicsGenres?.map((genre, index) => genre + (index + 1 !== data.comicsGenres?.length ? ", " : ""))
                                                            }
                                                        </div>
                                                    </li>
                                                    {/* 연령 제한 */}
                                                    {
                                                        data.adult &&
                                                        <li className="mb-2 flex">
                                                            <div className="mr-2">
                                                                {t("info.movieRating") + " : "}
                                                            </div>
                                                            <div>
                                                                {t("info.adultContent")}
                                                            </div>
                                                        </li>
                                                    }
                                                    {/* 방영 시간 OR 총 권 수 */}
                                                    <li className="mb-2 flex">
                                                        <div className="mr-2">
                                                            {
                                                                (
                                                                    (isTvType && t("info.tvRunningTime")) ||
                                                                    (isMovieType && t("info.movieRunningTime")) ||
                                                                    (isComicsType && t("info.volumes"))
                                                                ) + " : "
                                                            }
                                                        </div>
                                                        <div>
                                                            {
                                                                (isTvType && data.episodeRunTime + t("info.minutes")) ||
                                                                (isMovieType && data.runtime + t("info.minutes")) ||
                                                                (isComicsType && (data.status === "RELEASING" ? t("info.notEndedYet") : data.volumes + t("info.volume")))
                                                            }
                                                        </div>
                                                    </li>
                                                    {/* 방영시작일 OR 연재시작일 */}
                                                    <li className="mb-2 flex">
                                                        <div className="mr-2">
                                                            {
                                                                (
                                                                    (isTvType && t("info.tvReleaseDate")) ||
                                                                    (isMovieType && t("info.movieReleaseDate")) ||
                                                                    (isComicsType && t("info.serializeDate"))
                                                                ) + " : "
                                                            }
                                                        </div>
                                                        <div>
                                                            {
                                                                isTvType && data.firstAirDate ||
                                                                isMovieType && data.releaseDate ||
                                                                isComicsType && data.startDate
                                                            }
                                                        </div>
                                                    </li>
                                                    {/* 시즌 수 */}
                                                    {
                                                        isTvType && data.seasons &&
                                                        <li className="mb-2 flex">
                                                            <div className="mr-2">
                                                                {t("info.seasonNumbers") + " : "}
                                                            </div>
                                                            <div>
                                                                {data.seasons?.length + t("info.season")}
                                                            </div>
                                                        </li>
                                                    }
                                                    {/* 방영 상태 OR 연재 상태 */}
                                                    <li className="mb-2 flex">
                                                        <div className="mr-2">
                                                            {
                                                                (
                                                                    (isTvType && t("info.tvReleaseStatus")) ||
                                                                    (isMovieType && t("info.movieReleaseStatus")) ||
                                                                    (isComicsType && t("info.serializeStatus"))
                                                                ) + " : "
                                                            }
                                                        </div>
                                                        <div>
                                                            {
                                                                (isTvType && (data.status === "Ended" ? t("info.ended") : t("info.onAir"))) ||
                                                                (isMovieType && (data.status === "Released" ? t("info.released") : t("info.planned"))) ||
                                                                (isComicsType && (data.status === "FINISHED" ? t("info.finished") : t("info.releasing")))
                                                            }
                                                        </div>
                                                    </li>
                                                    {/* 홈페이지 */}
                                                    {
                                                        data.homepage &&
                                                        <li className="mb-2 flex">
                                                            <div className="mr-2">
                                                                {t("info.homePage") + " : "}
                                                            </div>
                                                            <div>
                                                                <a className="text-blue-600" href={data.homepage} target="_blank">{data.homepage}</a>
                                                            </div>
                                                        </li>
                                                    }
                                                    {/* 유저 평점 */}
                                                    <li className="mb-2 flex">
                                                        <div className="mr-2">
                                                            {t("info.userStarRating") + " : "}
                                                        </div>
                                                        <div>
                                                            {userStarRating ? userStarRating : "없음"}
                                                        </div>
                                                    </li>
                                                    {/* 볼 수 있는 곳 */}
                                                    {
                                                        isTvType || isMovieType &&
                                                        <li className="mb-2 flex">
                                                            <div className="mr-2">
                                                                {t("info.ableToWatching") + " : "}
                                                            </div>
                                                            <div>
                                                                {
                                                                    data.link
                                                                        ? <a className="text-blue-600" href={data.link} target="_blank">{data.link}</a>
                                                                        : "알 수 없음"
                                                                }
                                                            </div>
                                                        </li>
                                                    }
                                                </ul>
                                            </div>
                                            {/* <div className="w-[30%] mt-5 mb-5">
                                            </div> */}
                                        </div>
                                    </div>
                                </>
                            }
                        </div>

                        {/* 탭 내용 */}
                        <div className="mt-5 p-4">
                            {
                                data && originalMediaType &&
                                <>
                                    {
                                        tabIndex === 0 &&
                                        <div>
                                            {/* 작품 정보 */}
                                            <ContentInfomation detailResult={data} originalMediaType={originalMediaType} />
                                        </div>
                                    }
                                    {
                                        tabIndex === 1 &&
                                        <div>
                                            {/* 평가&리뷰 */}
                                            <ContentComment detailResult={data} originalMediaType={originalMediaType} />
                                        </div>
                                    }
                                    {
                                        tabIndex === 2 &&
                                        <div>
                                            {/* 비슷한 작품 */}
                                            <RecommendContent detailResult={data} originalMediaType={originalMediaType} />
                                        </div>
                                    }
                                </>
                            }
                        </div>
                    </div>
                </div>
            </div>
        </>
    )
});
