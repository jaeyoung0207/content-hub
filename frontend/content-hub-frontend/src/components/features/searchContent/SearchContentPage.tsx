import { useSearchParams } from "react-router-dom"
import { SearchContent } from "./SearchContent";
import { SearchContentMore } from "./modal/SearchContentMore";
import { Detail } from "../detail/Detail";

/**
 * 검색 콘텐츠 페이지 컴포넌트의 props 타입
 */
export type SearchContentPropsType = {
    keyword: string, // 검색어
    isAdult?: string, // 성인물 포함 여부
    mediaType?: string, // 미디어 타입
    viewMore?: string, // 전체보기 여부
    originalMediaType?: string, // 원본 미디어 타입
    contentId?: string, // 콘텐츠 ID
    tabNo?: number, // 탭 번호
}

/**
 * 검색 콘텐츠 페이지 컴포넌트
 * URL 쿼리 파라미터를 받아서 각 컴포넌트에 필요한 데이터를 전달하고 렌더링하는 컴포넌트
 */
export const SearchContentPage = () => {
    // URL 쿼리 파라미터를 가져오기 위한 훅
    const [searchParams] = useSearchParams();
    const keyword = searchParams.get("keyword")!; // 검색어
    const isAdult = searchParams.get("isAdult")!; // 성인물 포함 여부
    const mediaType = searchParams.get("mediaType"); // 미디어 타입
    const viewMore = searchParams.get("viewMore"); // 전체보기 여부
    const originalMediaType = searchParams.get("originalMediaType"); // 원본 미디어 타입
    const contentId = searchParams.get("contentId"); // 콘텐츠 ID
    const tabNo = Number(searchParams.get("tabNo")); // 탭 번호

    return (
        <>
            {/* 검색 화면 컴포넌트 */}
            <SearchContent keyword={keyword} isAdult={isAdult} />
            {
                // 검색어, 성인물 포함 여부, 미디어 타입, 전체보기 여부가 존재하는 경우
                // 전체보기 모달화면 컴포넌트를 렌더링
                keyword && isAdult && mediaType && viewMore &&
                <SearchContentMore keyword={keyword} isAdult={isAdult} mediaType={mediaType} />
            }
            {
                // 검색어, 성인물 포함 여부, 원본 미디어 타입, 콘텐츠 ID, 탭 번호가 존재하는 경우
                // 상세 화면 컴포넌트를 렌더링
                keyword && isAdult && originalMediaType && contentId && tabNo !== undefined &&
                <Detail keyword={keyword} isAdult={isAdult} originalMediaType={originalMediaType} contentId={contentId} tabNo={tabNo} />
            }
        </>
    )
}