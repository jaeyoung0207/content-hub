import { useTranslation } from "react-i18next";
import DOMPurify from 'dompurify';
import { MEDIA_TYPE, IS_MOBILE, TMDB_API_IMAGE_DOMAIN, WIDTH_185, COMMON_IMAGES } from "@/components/common/constants/constants";
import { DetailResponseType } from "../../useDetail";
import { characterUrlQuery, isDetailComicsType, isDetailMovieType, isDetailTvType, personUrlQuery } from "@/components/common/utils/commonUtil";
import { Link } from "react-router-dom";
import { toast } from "react-toastify";

/**
 * 콘텐츠 정보 컴포넌트 props 타입
 */
type ContentInfomationPropsType = {
    detailResult: DetailResponseType,
    originalMediaType: string,
}

/**
 * 콘텐츠 정보 컴포넌트
 * @param detailResult 상세 정보 결과
 * @param originalMediaType 원본 미디어 타입
 */
export const ContentInfomation = ({ detailResult, originalMediaType }: ContentInfomationPropsType) => {
    // i18n 번역 훅
    const { t } = useTranslation();

    // 개요 변수 선언
    // DOMPurify를 사용하여 XSS 공격을 방지하며, comics 타입의 개요는 HTML로 처리
    // 그 외 타입의 개요는 문자열로 처리
    const overview = detailResult.overview
        && (originalMediaType === MEDIA_TYPE.COMICS
            ? DOMPurify.sanitize(detailResult.overview)
            : detailResult.overview);

    // 썸네일 이미지 경로
    const thumbnailImagePath = TMDB_API_IMAGE_DOMAIN + WIDTH_185;

    return (
        <div className="ml-5 mr-5">
            {
                overview &&
                <>
                    {/* 개요 */}
                    <div className="text-3xl font-bold mb-5">
                        {t("info.overview")}
                    </div>
                    <div className="mb-10">
                        {
                            (originalMediaType === MEDIA_TYPE.COMICS
                                ? <div dangerouslySetInnerHTML={{ __html: overview }} ></div>
                                : overview)
                        }
                    </div>
                </>
            }

            {
                // 상세 정보 결과의 타입이 TV 또는 MOVIE인 경우
                (isDetailTvType(detailResult, originalMediaType) || isDetailMovieType(detailResult, originalMediaType)) ?
                    <>
                        {
                            detailResult.credits &&
                            detailResult.credits.cast &&
                            detailResult.credits.cast.length !== 0 &&
                            <>
                                {/* 출연진 */}
                                <div className="text-3xl font-bold mt-5 mb-5">
                                    {t("info.cast")}
                                </div>
                                <div className="flex flex-wrap items-start mt-5">
                                    {
                                        detailResult.credits.cast.map((items, index) => {
                                            return (
                                                <div
                                                    key={index}
                                                    className="ml-1 mr-1 w-[190px]"
                                                    onClick={() => {
                                                        if (!items.id) {
                                                            toast.warn(t("warn.noPersonId"), { toastId: "noPersonId" });
                                                        }
                                                    }}>
                                                    <Link to={items.id ? personUrlQuery({ personId: items.id }) : "#"}>
                                                        <ul className="block hover:font-bold">
                                                            <li className="flex justify-center items-center w-full h-[285px]">
                                                                <img
                                                                    src={thumbnailImagePath + items.profilePath}
                                                                    onError={(e) => {
                                                                        e.currentTarget.src = COMMON_IMAGES.NO_IMAGE;
                                                                    }}
                                                                    alt={items.name}
                                                                />
                                                            </li>
                                                            <li className="ml-1 mr-1 mb-4 text-lg">
                                                                {
                                                                    items.name! + (items.character && "(" + items.character + t("info.role") + ")")
                                                                }
                                                            </li>
                                                        </ul>
                                                    </Link>
                                                </div>
                                            )
                                        })
                                    }
                                </div>
                            </>
                        }
                        {
                            detailResult.credits &&
                            detailResult.credits.crew &&
                            detailResult.credits.crew.length !== 0 &&
                            <>
                                {/* 제작진 */}
                                <div className="text-3xl font-bold mt-5 mb-5">
                                    {t("info.crew")}
                                </div>
                                <div className="relative flex flex-wrap items-start mt-5">
                                    {
                                        detailResult.credits.crew.map((items, index) => {
                                            return (
                                                <div
                                                    key={index}
                                                    className="ml-1 mr-1 w-[190px]"
                                                    onClick={() => {
                                                        if (!items.id) {
                                                            toast.warn(t("warn.noPersonId"), { toastId: "noPersonId" });
                                                        }
                                                    }}
                                                >
                                                    <Link to={items.id ? personUrlQuery({ personId: items.id }) : "#"}>
                                                        <ul className="block hover:font-bold">
                                                            <li className="flex justify-center items-center w-full h-[285px]">
                                                                <img
                                                                    src={thumbnailImagePath + items.profilePath}
                                                                    onError={(e) => {
                                                                        e.currentTarget.src = COMMON_IMAGES.NO_IMAGE;
                                                                    }}
                                                                    alt={items.name}
                                                                />
                                                            </li>
                                                            <li className="ml-1 mr-1 mb-4 text-lg">
                                                                {
                                                                    items.name! + (items.job && "(" + items.job + ")")
                                                                }
                                                            </li>
                                                        </ul>
                                                    </Link>
                                                </div>
                                            )
                                        })
                                    }
                                </div>
                            </>
                        }
                    </>
                    // 상세 정보 결과의 타입이 COMICS인 경우
                    : isDetailComicsType(detailResult, originalMediaType) &&
                    detailResult.characters &&
                    detailResult.characters.nodes &&
                    detailResult.characters.nodes.length !== 0 &&
                    <>
                        {/* 등장인물 */}
                        <div className="text-3xl font-bold mt-5 mb-5">
                            {t("info.characters")}
                        </div>
                        <div className="relative flex flex-wrap items-start mt-5">
                            {
                                detailResult.characters.nodes.map((items, index) => {
                                    return (
                                        <div
                                            key={index}
                                            className="ml-1 mr-1 w-[110px]"
                                            onClick={() => {
                                                if (!items.id) {
                                                    toast.warn(t("warn.noPersonId"), { toastId: "noPersonId" });
                                                }
                                            }}
                                        >
                                            <Link to={items.id ? characterUrlQuery({ characterId: items.id }) : "#"}>
                                                <ul className="block hover:font-bold">
                                                    <li className="flex justify-center items-center w-full h-[180px]">
                                                        <img
                                                            src={items.image?.medium}
                                                            onError={(e) => {
                                                                e.currentTarget.src = COMMON_IMAGES.NO_IMAGE;
                                                            }}
                                                            alt={items.name?.full}
                                                        />
                                                    </li>
                                                    <li className="ml-1 mr-1 mb-4 text-lg">
                                                        {
                                                            items.name?.full
                                                        }
                                                    </li>
                                                </ul>
                                            </Link>
                                        </div>
                                    )
                                })
                            }
                        </div>
                    </>
            }
        </div>
    );

}