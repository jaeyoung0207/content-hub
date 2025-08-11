import { useNavigate, useSearchParams } from "react-router-dom";
import { DetailResponseType } from "../../useDetail";
import { RecommendUseInfiniteQueryResultType, useRecommendContent } from "./useRecommendContent"
import { COMMON_IMAGES, MEDIA_TYPE, TMDB_API_IMAGE_DOMAIN, WIDTH_300 } from "@/components/common/constants/constants";
import { LoadingUi } from "@/components/ui/LoadingUi";
import { NodataMessageUi } from "@/components/ui/common/NodataMessageUi";
import { useTranslation } from "react-i18next";
import { commonErrorHandler, detailUrlQuery, isRecommendationsTvType } from "@/components/common/utils/commonUtil";

/**
 * 추천 콘텐츠 컴포넌트 props 타입
 */
type RecommendContentPropsType = {
    detailResult: DetailResponseType,
    originalMediaType: string,
}

/**
 * 추천 콘텐츠 결과 표시 컴포넌트 props 타입
 */
type DisplayRecommendResultsPropsType = {
    data: RecommendUseInfiniteQueryResultType
    originalMediaType: string,
}

/**
 * 추천 콘텐츠 컴포넌트
 * @param detailResult 상세 정보 결과
 * @param originalMediaType 원본 미디어 타입
 * @returns 추천 콘텐츠 컴포넌트
 */
export const RecommendContent = ({ detailResult, originalMediaType }: RecommendContentPropsType) => {
    // i18n 번역 훅
    const { t } = useTranslation();
    
    // 추천 콘텐츠를 가져오기 위한 커스텀 훅 호출
    const {
        data,
        isFetchingNextPage,
        hasNextPage,
        setObserveTarget,
    } = useRecommendContent(detailResult, originalMediaType);

    return (
        <>
            {
                data
                    ? <div>
                        {/* 추천 콘텐츠 결과 표시 */}
                        <DisplayRecommendResults data={data} originalMediaType={originalMediaType} />
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
            {
                // 추천 콘텐츠 데이터가 없을 때 표시할 메시지
                data && data.pages[0]?.length === 0 &&
                <NodataMessageUi message={t("info.noSearchData")} />
            }
        </>
    )
}

/**
 * 추천 콘텐츠 결과 표시 컴포넌트
 * @param data 추천 콘텐츠 데이터
 * @param originalMediaType 원본 미디어 타입
 */
const DisplayRecommendResults = ({ data, originalMediaType }: DisplayRecommendResultsPropsType) => {

    // navigate 훅
    const navigate = useNavigate();
    // 상세보기 모달에서 사용할 썸네일 이미지 경로
    const thumbnailImagePath = TMDB_API_IMAGE_DOMAIN + WIDTH_300;

    return (
        <div className="w-full">
            <div className="flex flex-wrap items-start mt-8">
                {
                    data.pages.length !== 0 &&
                    data.pages.flat().map((items, index) => {
                        const title = items && (isRecommendationsTvType(items, originalMediaType) ? items.name : items.title);
                        return (
                            <div key={index}>
                                {
                                    items &&
                                    <ul
                                        key={"frame" + index}
                                        className={"ml-1 mr-1 block hover:font-bold cursor-pointer " + (originalMediaType === MEDIA_TYPE.COMICS ? "w-[190px]" : "w-[300px]")}
                                        onClick={commonErrorHandler(() => {
                                            // 상세화면 URL 생성
                                            const detailUrl = detailUrlQuery({ originalMediaType: originalMediaType, contentId: String(items.id), tabNo: 0 });
                                            // 리다이렉트용 데이터 저장
                                            sessionStorage.setItem("redirectUrl", detailUrl);
                                            // 상세보기 모달 오픈
                                            navigate(detailUrl);
                                        })}
                                    >
                                        {/* 썸네일 */}
                                        <li key={"poster_path" + index} className="flex justify-center items-center w-full">
                                            <img
                                                src={
                                                    originalMediaType === MEDIA_TYPE.COMICS
                                                        ? items.backdropPath
                                                        : (items.backdropPath ? thumbnailImagePath + items.backdropPath : thumbnailImagePath + items.posterPath)
                                                }
                                                onError={(e) => {
                                                    e.currentTarget.src = COMMON_IMAGES.NO_IMAGE;
                                                }}
                                                alt={"Thumbnail Image"}
                                                className={(originalMediaType === MEDIA_TYPE.COMICS ? "w-[190px] h-[270px]" : "w-full h-[180px]") + " object-scale-down"}
                                            />
                                        </li>
                                        {/* 제목 */}
                                        <li key={"title" + index} className="ml-1 mr-1 mb-4 text-xl">
                                            {title}
                                        </li>
                                    </ul>
                                }
                            </div>
                        )
                    })
                }
            </div>
        </div>
    )
}