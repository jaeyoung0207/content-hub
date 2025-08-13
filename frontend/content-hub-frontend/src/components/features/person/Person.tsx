import { Link, useNavigate, useParams } from "react-router-dom";
import { usePerson } from "./usePerson"
import { LoadingUi } from "@/components/ui/LoadingUi";
import { useTranslation } from "react-i18next";
import { COMMON_IMAGES, IS_MOBILE, MEDIA_TYPE, TMDB_API_IMAGE_DOMAIN, WIDTH_185, WIDTH_300 } from "@/components/common/constants/constants";
import { detailUrlQuery, isCreditsCastType } from "@/components/common/utils/commonUtil";
import dayjs from "dayjs";
import { toast } from "react-toastify";
import { PersonCreditsCastDto, PersonCreditsCrewDto } from "@/api/data-contracts";
import { memo } from "react";

export type PersonCredits = PersonCreditsCastDto | PersonCreditsCrewDto;

type DisplayCreditsType = {
    credits: PersonCredits[],
}

/** 
 * 인물 화면 컴포넌트
 */
export const Person = () => {

    // i18n 번역 훅
    const { t } = useTranslation();
    // navigate 훅
    const navigate = useNavigate();
    // URL 파라미터에서 personId 추출
    const { personId } = useParams<string>();

    // personId가 없으면 처리종료
    if (!personId) {
        console.error("personId 가 없습니다.");
        toast.error("인물 ID가 없습니다.", { toastId: "noPersonId" });
        navigate(-1);
        return;
    }

    // 인물 정보 가져오기 훅
    const {
        data,
        isLoading,
        isError,
    } = usePerson(personId);

    // 인물 정보 스타일
    const personInfoStyle = "flex text-xl mb-2 mr-3 break-all";
    // 소제목 스타일
    const subTitleStyle = "mr-2 whitespace-nowrap";

    return (
        <div className="block mt-30 mb-10">
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
            {/* 인물 정보 */}
            {data &&
                <>
                    <div className="flex justify-center m-5">

                        {/* 인물 이미지 */}
                        <div className="flex justify-center items-center mb-4 w-[30%]">
                            <img
                                src={
                                    data?.profilePath
                                        ? TMDB_API_IMAGE_DOMAIN + (IS_MOBILE ? WIDTH_185 : WIDTH_300) + data?.profilePath
                                        : COMMON_IMAGES.NO_IMAGE
                                }
                                className={(IS_MOBILE ? "w-[200px]" : "w-[300px]") + " h-full"}
                                onError={(e) => {
                                    e.currentTarget.src = COMMON_IMAGES.NO_IMAGE;
                                }}
                                alt={data.name}
                            />
                        </div>
                        {/* 인물 신상 정보 */}
                        <div className="w-[70%] block">
                            {/* 이름 */}
                            <div className="text-3xl mb-3 mr-3">
                                {data.name}
                            </div>
                            <ul className="mt-5">
                                {/* 다른 이름 */}
                                {
                                    data.alsoKnownAs && data.alsoKnownAs.length > 0 &&
                                    <li className={personInfoStyle}>
                                        <div className={subTitleStyle}>
                                            {t("info.alsoKnownAs") + t("info.colon")}
                                        </div>
                                        <div>
                                            {data.alsoKnownAs.join(", ")}
                                        </div>
                                    </li>
                                }
                                {/* 성별 */}
                                <li className={personInfoStyle}>
                                    <div className={subTitleStyle}>
                                        {t("info.gender") + t("info.colon")}
                                    </div>
                                    <div>
                                        {data.genderValue}
                                    </div>
                                </li>
                                {/* 출생지 */}
                                {
                                    data.placeOfBirth &&
                                    <li className={personInfoStyle}>
                                        <div className={subTitleStyle}>
                                            {t("info.placeOfBirth") + t("info.colon")}
                                        </div>
                                        <div>
                                            {data.placeOfBirth}
                                        </div>
                                    </li>
                                }
                                {/* 생일 */}
                                {
                                    data.birthday &&
                                    <li className={personInfoStyle}>
                                        <div className={subTitleStyle}>
                                            {t("info.birthday") + t("info.colon")}
                                        </div>
                                        <div>
                                            {dayjs(data.birthday).format("YYYY년 MM월 DD일")}
                                        </div>
                                    </li>
                                }
                                {/* 사망일 */}
                                {
                                    data.deathday &&
                                    <li className={personInfoStyle}>
                                        <div className={subTitleStyle}>
                                            {t("info.deathday") + t("info.colon")}
                                        </div>
                                        <div>
                                            {dayjs(data.deathday).format("YYYY년 MM월 DD일")}
                                        </div>
                                    </li>
                                }
                                {/* 전문 분야 */}
                                {
                                    data.knownForDepartment &&
                                    <li className={personInfoStyle}>
                                        <div className={subTitleStyle}>
                                            {t("info.knownForDepartment") + t("info.colon")}
                                        </div>
                                        <div>
                                            {data.knownForDepartment}
                                            {
                                                data.adult &&
                                                " (" + t("info.adultActor") + ")"
                                            }
                                        </div>
                                    </li>
                                }
                                {/* 출연작 수 */}
                                {
                                    typeof data.castCount === "number" && data.castCount > 0 &&
                                    <li className={personInfoStyle}>
                                        <div className={subTitleStyle}>
                                            {t("info.castCount") + t("info.colon")}
                                        </div>
                                        <div>
                                            {data.castCount}
                                        </div>
                                    </li>
                                }
                                {/* 제작 참여작 수 */}
                                {
                                    typeof data.crewCount === "number" && data.crewCount > 0 &&
                                    <li className={personInfoStyle}>
                                        <div className={subTitleStyle}>
                                            {t("info.crewCount") + t("info.colon")}
                                        </div>
                                        <div>
                                            {data.crewCount}
                                        </div>
                                    </li>
                                }
                                {/* 홈페이지 */}
                                {
                                    data.homepage &&
                                    <li className={personInfoStyle}>
                                        <div className={subTitleStyle}>
                                            {t("info.homepage") + t("info.colon")}
                                        </div>
                                        <div>
                                            <a href={data.homepage} target="_blank" className="text-blue-500 hover:underline">
                                                {data.homepage}
                                            </a>
                                        </div>
                                    </li>
                                }
                            </ul>
                        </div>
                    </div>
                    {/* 약력 */}
                    {
                        data.biography &&
                        <div className="text-2xl mb-5 mr-3">
                            <div className="text-3xl font-bold mb-3">
                                {t("info.biography")}
                            </div>
                            <div className="text-lg whitespace-pre-wrap ml-5">
                                {data.biography}
                            </div>
                        </div>
                    }
                    {/* 출연작 */}
                    {
                        data.cast && data.cast.length !== 0 &&
                        <>
                            <div className="text-3xl font-bold mt-5 mb-5">
                                {t("info.singleCast")}
                            </div>
                            <div className="mt-3 p-2">
                                <DisplayCredits credits={data.cast} />
                            </div>
                        </>
                    }
                    {/* 제작 참여작 */}
                    {
                        data.crew && data.crew.length !== 0 &&
                        <>
                            <div className="text-3xl font-bold mt-5 mb-5">
                                {t("info.singleCrew")}
                            </div>
                            <div className="mt-3 p-2">
                                <DisplayCredits credits={data.crew} />
                            </div>
                        </>
                    }
                </>
            }
        </div>
    )
}

/**
 * 크레딧 정보를 표시하는 컴포넌트
 * @param credits - 크레딧 정보 배열
 */
const DisplayCredits = memo(({ credits }: DisplayCreditsType) => {

    // i18n 번역 훅
    const { t } = useTranslation();
    return (
        <div>
            {
                credits.map((items, index) => {
                    // 캐스트인지 타입 확인
                    const isCast = isCreditsCastType(items);
                    // TV인 경우 애니메이션 고정, 그 이외의 경우 영화로 설정
                    const originalMediaType = items.mediaType === "TV" ? MEDIA_TYPE.ANI : MEDIA_TYPE.MOVIE;
                    // 캐스트인 경우 캐릭터, 크루인 경우 작업 역할 표시
                    const role = isCast ? items.character : items.job;
                    return (
                        <div className="flex mb-1" key={index}>
                            <div className={`w-full h-full grid grid-rows-[${role ? 2 : 1}] grid-cols-[0.1fr_0.1fr_1.8fr] gap-1`}>
                                {/* 작업 연도 */}
                                <div className="flex justify-center pt-1 row-span-2">
                                    {
                                        items.releaseYear ?
                                            index !== 0 ?
                                                (credits[index - 1].releaseYear === items.releaseYear ? "" : items.releaseYear)
                                                : (items.releaseYear ?? t("info.unknown"))
                                            : t("info.unknown")
                                    }
                                </div>
                                {/* 미디어 타입 */}
                                <div className="flex justify-center pt-1">
                                    {items.mediaType}
                                </div>
                                {/* 작품 링크 */}
                                <Link
                                    to={detailUrlQuery({
                                        originalMediaType: originalMediaType,
                                        contentId: String(items.id),
                                        tabNo: 0
                                    })}
                                    className="pl-1 text-lg font-bold hover:underline">
                                    {items.title}
                                </Link>
                                {/* 역할 */}
                                {
                                    role &&
                                    <div className="pl-1 col-start-3">
                                        {role}
                                        {isCast && t("info.role")}
                                    </div>
                                }
                            </div>
                        </div>
                    )
                })
            }
        </div>
    )
})