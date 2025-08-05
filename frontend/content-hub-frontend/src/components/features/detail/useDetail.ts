import { useSearchParams } from "react-router-dom"
import { Dispatch, SetStateAction, useEffect, useState } from "react"
import { Detail } from "@/api/Detail"
import { useQuery, useQueryClient } from "@tanstack/react-query"
import { DetailTvResponseDto, DetailMovieResponseDto, DetailComicsResponseDto } from "@/api/data-contracts"
import { ESC_KEY, MEDIA_TYPE } from "@/components/common/constants/constants"
import { detailMainKey, detailQueryKeys } from "./queryKeys/detailQueryKeys"

/**
 * 상세 정보 결과 타입
 */
export type DetailResponseType = DetailTvResponseDto | DetailMovieResponseDto | DetailComicsResponseDto;

/**
 * useDetail 훅 반환 타입
 */
type useDetailReturnType = {
    tabIndex: number, // 현재 탭 인덱스
    setTabIndex: Dispatch<SetStateAction<number>>, // 탭 인덱스 설정 함수
    handleModalClose: () => void, // 모달 닫기 함수
    data?: DetailResponseType, // 상세 정보 데이터
    isLoading: boolean, // 로딩 중 여부
    isError: boolean, // 에러 여부
    userStarRating?: string, // 유저 평균 평점
}

/**
 * 상세 화면 커스텀 훅
 * 상세 화면에서 필요한 데이터를 가져오고, 탭 인덱스와 모달 닫기 함수를 관리하는 훅
 * @param originalMediaType 원본 미디어 타입
 * @param contentId 컨텐츠 ID
 * @param tabNo 탭 번호
 * @returns 
 */
export const useDetail = (originalMediaType: string, contentId: string, tabNo: number): useDetailReturnType => {

    // ================================================================================================== URL query string

    // URL query string 값을 가져오기 위한 useSearchParams 훅
    const [searchParams, setSearchParams] = useSearchParams();

    // ================================================================================================== react hook

    // react query 클라이언트 훅
    const queryClient = useQueryClient();
    // 탭 인덱스 상태
    const [tabIndex, setTabIndex] = useState(tabNo);
    // 유저 평균 평점 상태
    const [userStarRating, setUserStarRating] = useState<string>();

    // ================================================================================================== react query

    // 상세 API 인스턴스 생성
    const detailApi = new Detail();
    // 리퀘스트 파라미터용 컨텐츠ID
    const contentIdParam = Number(contentId);

    /**
     * 상세 정보를 가져오는 API 호출 함수
     * @returns 상세 정보 데이터
     */
    const getDetailApi = async () => {
        // 원본 미디어 타입이 ANI 또는 DRAMA인 경우
        if (originalMediaType === MEDIA_TYPE.ANI || originalMediaType === MEDIA_TYPE.DRAMA) {
            // TV 상세 정보를 가져오는 API 호출
            return (await detailApi.getTvDetail({ series_id: contentIdParam, originalMediaType: originalMediaType })).data;
        } 
        // 원본 미디어 타입이 MOVIE인 경우
        else if (originalMediaType === MEDIA_TYPE.MOVIE) {
            // MOVIE 상세 정보를 가져오는 API 호출
            return (await detailApi.getMovieDetail({ movie_id: contentIdParam, originalMediaType: originalMediaType })).data;
        } 
        // 원본 미디어 타입이 COMICS인 경우
        else if (originalMediaType === MEDIA_TYPE.COMICS) {
            // COMICS 상세 정보를 가져오는 API 호출
            return (await detailApi.getComicsDetail({ comics_id: contentIdParam })).data;
        }
    }

    /**
     * 상세 정보를 가져오는 쿼리
     */
    const {
        data,
        isLoading,
        isError,
    } = useQuery({
        queryKey: detailQueryKeys.detail.getDetail(originalMediaType, contentId),
        queryFn: async () => {
            // 상세 정보를 가져오는 API 호출
            return await getDetailApi();
        },
        enabled: !!originalMediaType, // originalMediaType이 존재할 때만 쿼리 실행
    });

    // ================================================================================================== function

    /**
     * 모달을 닫을 때 호출되는 함수
     */
    const handleModalClose = () => {
        // 닫는 순간에 상세화면의 ReactQuery에서 사용하고 있는 쿼리 삭제
        queryClient.removeQueries({ queryKey: [detailMainKey] }); // prefix 배열을 사용하여 상세화면에서 이용한 쿼리 전부 삭제
        // 리다이렉트 주소 삭제
        sessionStorage.removeItem("redirectUrl");
        // URL query 삭제
        searchParams.delete("tabNo");
        searchParams.delete("contentId");
        searchParams.delete("originalMediaType");
        setSearchParams(searchParams); // URL이 바뀌면 React Router가 감지해서 리렌더링 발생
    }

    // ================================================================================================== useEffect

    /**
     * 이벤트 리스너를 설정하는 useEffect
     * 배경 스크롤을 막고, ESC 키를 눌렀을 때 모달을 닫는 이벤트 리스너를 등록
     * 이벤트 리스너는 컴포넌트가 언마운트될 때 제거
     * 실행 조건: 컴포넌트가 마운트될 때
     */
    useEffect(() => {
        // 배경 스크롤 막기
        document.body.style.overflow = "hidden";
        // ESC키 눌렀을 시 모달 종료
        const handleOnEscKey = (e: KeyboardEvent) => {
            if (e.key === ESC_KEY) {
                handleModalClose();
            }
        }
        // ESC 키 이벤트 리스너 등록
        document.addEventListener("keydown", handleOnEscKey);

        return (() => {
            // URL query string에 viewMore가 없을 경우(전체보기 모달에서 열지 않은 경우)
            if (!searchParams.has("viewMore")) {
                // 배경 스크롤 복원
                document.body.style.removeProperty("overflow");
            }
            // ESC 키 이벤트 리스너 제거
            document.removeEventListener("keydown", handleOnEscKey);
        });
    }, []);

    /**
     * 별점 평균을 가져오는 useEffect
     * 실행 조건: tabIndex가 변경될 때
     */
    useEffect(() => {
        queryClient.fetchQuery({
            queryKey: detailQueryKeys.detail.getStarRatingAverage(originalMediaType, contentId),
            queryFn: async () => {
                // 유저 평균 평점 취득
                const response = (await detailApi.getStarRatingAverage({ originalMediaType: originalMediaType, apiId: contentId })).data
                // 유저 평균 평점 설정
                const convertResponse = response ? response.toFixed(1) : "없음"; // 소수점 한자리까지 표시(ex: 4 -> 4.0)
                // const convertResponse = response.toFixed(1); // 소수점 한자리까지 표시(ex: 4 -> 4.0) // Error Test
                setUserStarRating(convertResponse);
                return convertResponse;
            },
        })
    }, [tabIndex])

    /**
     * 탭 번호가 변경될 때마다 실행되는 useEffect
     * 실행 조건: tabNo가 변경될 때
     */
    useEffect(() => {
        // 화면 진입시에 탭 인덱스 상태설정
        if (tabNo !== tabIndex) {
            setTabIndex(tabNo);
        }
    }, [tabNo])

    // ================================================================================================== return

    return {
        isLoading: isLoading,
        tabIndex: tabIndex,
        setTabIndex: setTabIndex,
        handleModalClose: handleModalClose,
        data: data,
        isError: isError,
        userStarRating: userStarRating,
    }
}