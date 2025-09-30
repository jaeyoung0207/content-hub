import { useTranslation } from 'react-i18next';
import {
  MEDIA_TYPE_NAME,
  SEARCH_SCREEN_TYPE,
} from '@/components/common/constants/constants';
import { useSearchTypeStore } from '@/components/common/store/globalStateStore';
import { LoadingUi } from '@/components/ui/LoadingUi';
import { SearchPropsType } from './SearchPage';
import { NodataMessageUi } from '@/components/ui/common/NodataMessageUi';
import DisplaySearchResults from '@/components/ui/DisplaySearchResultsUi';
import { getDisplayMediaType } from '@/components/common/utils/convertUtil';
import { useVideoSearch } from './useVideoSearch';
import { useComicsSearch } from './useComicsSearch';
import {
  SearchComicsResultDto,
  SearchMovieResultsDto,
  SearchTvResultsDto,
} from '@/api/data-contracts';

// 공통 검색 결과 타입
export type SearchCommonResultType =
  | SearchTvResultsDto
  | SearchMovieResultsDto
  | SearchComicsResultDto;

/**
 * 검색 화면 컴포넌트
 * @param keyword 검색어
 * @param isAdult 성인물 포함 여부
 */
export const Search = ({ keyword, isAdult }: SearchPropsType) => {
  // i18n 번역 훅
  const { t } = useTranslation();
  // 검색 타입 상태 훅
  const { searchTypeState } = useSearchTypeStore();

  // 검색 훅 호출
  const { videoData, isVideoLoading } = useVideoSearch(keyword, isAdult!);
  const { comicsData, isComicsLoading } = useComicsSearch(keyword, isAdult!);

  // 검색결과에 따라 미디어 타입별로 결과를 분리
  const aniSearchResults = videoData?.aniResults;
  const dramaSearchResults = videoData?.dramaResults;
  const movieSearchResults = videoData?.movieResults;
  const comicsSearchResults = comicsData?.comicsResults;
  const documentarySearchResults = videoData?.documentaryResults;
  const kidsSearchResults = videoData?.kidsResults;
  const newsSearchResults = videoData?.newsResults;
  const varietySearchResults = videoData?.varietyResults;
  const isAniViewMore = videoData?.isAniViewMore;
  const isDramaViewMore = videoData?.isDramaViewMore;
  const isMovieViewMore = videoData?.isMovieViewMore;
  const isComicsViewMore = comicsData?.isComicsViewMore;
  const isDocumentaryViewMore = videoData?.isDocumentaryViewMore;
  const isKidsViewMore = videoData?.isKidsViewMore;
  const isNewsViewMore = videoData?.isNewsViewMore;
  const isVarietyViewMore = videoData?.isVarietyViewMore;

  // 검색 결과 인자값 리스트
  const dataList = [
    {
      displayFlg: searchTypeState.aniFlg,
      dataResults: aniSearchResults,
      mediaName: t('info.animation'),
      isViewMore: isAniViewMore,
      displayMediaType: getDisplayMediaType().aniCode,
      isLoading: isVideoLoading,
    },
    {
      displayFlg: searchTypeState.dramaFlg,
      dataResults: dramaSearchResults,
      mediaName: t('info.drama'),
      isViewMore: isDramaViewMore,
      displayMediaType: getDisplayMediaType().dramaCode,
      isLoading: isVideoLoading,
    },
    {
      displayFlg: searchTypeState.movieFlg,
      dataResults: movieSearchResults,
      mediaName: t('info.movie'),
      isViewMore: isMovieViewMore,
      displayMediaType: getDisplayMediaType().movieCode,
      isLoading: isVideoLoading,
    },
    {
      displayFlg: searchTypeState.documentaryFlg,
      dataResults: documentarySearchResults,
      mediaName: t('info.documentary'),
      isViewMore: isDocumentaryViewMore,
      displayMediaType: getDisplayMediaType().documentaryCode,
      isLoading: isVideoLoading,
    },
    {
      displayFlg: searchTypeState.kidsFlg,
      dataResults: kidsSearchResults,
      mediaName: t('info.kids'),
      isViewMore: isKidsViewMore,
      displayMediaType: getDisplayMediaType().kidsCode,
      isLoading: isVideoLoading,
    },
    {
      displayFlg: searchTypeState.newsFlg,
      dataResults: newsSearchResults,
      mediaName: t('info.news'),
      isViewMore: isNewsViewMore,
      displayMediaType: getDisplayMediaType().newsCode,
      isLoading: isVideoLoading,
    },
    {
      displayFlg: searchTypeState.varietyFlg,
      dataResults: varietySearchResults,
      mediaName: t('info.variety'),
      isViewMore: isVarietyViewMore,
      displayMediaType: getDisplayMediaType().varietyCode,
      isLoading: isVideoLoading,
    },
    {
      displayFlg: searchTypeState.comicsFlg,
      dataResults: comicsSearchResults,
      mediaName: t('info.comics'),
      isViewMore: isComicsViewMore,
      displayMediaType: getDisplayMediaType().comicsCode,
      isLoading: isComicsLoading,
    },
  ];
  // 검색 결과 존재 여부
  const isSearchResultEmpty = dataList.every(
    (item) => !item.dataResults || item.dataResults.length === 0
  );

  // 검색용 미디어 타입 체크박스가 모두 해제된 경우
  const isSelectedSearchTypeEmpty = Object.values(searchTypeState).every(
    (value) => value === false
  );

  // 선택된 미디어 타입 중 검색 결과가 하나도 없는 경우(searchTypeState 의 각 플러그 값이 dataList의 displayFlg 값과 일치하므로 이를 활용)
  const isOneSelectedAndNoResult = Object.values(searchTypeState).some(
    (value) =>
      value === true &&
      !dataList.find(
        (item) =>
          item.displayFlg && item.dataResults && item.dataResults.length > 0
      )
  );

  // 미디어 타입 이름 문자열 생성
  const mediaTypes = Object.keys(MEDIA_TYPE_NAME)
    .map((key) => t(MEDIA_TYPE_NAME[key as keyof typeof MEDIA_TYPE_NAME]))
    .join('/');

  return (
    <>
      <div className="mt-28 w-sm lg:w-7xl">
        {/* 검색 결과 */}
        {
          dataList.map((items) => {
            return (
              <div key={items.mediaName}>
                {items.displayFlg &&
                  !items.isLoading &&
                  items.dataResults &&
                  items.dataResults.length !== 0 && (
                    // 각 미디어 검색결과 컴포넌트
                    <DisplaySearchResults
                      mediaName={items.mediaName}
                      results={items.dataResults}
                      isViewMore={items.isViewMore}
                      displayMediaType={items.displayMediaType}
                      keyword={keyword!}
                      isAdult={isAdult!}
                      searchScreenType={SEARCH_SCREEN_TYPE.MAIN}
                    />
                  )}
              </div>
            );
          })
          // )
        }
        {/* 로딩 중 메시지 */}
        {(isVideoLoading || isComicsLoading) && (
          <>
            <LoadingUi />
            <div className="mt-25 lg:mt-60 flex justify-center items-center text-black text-xl lg:text-2xl font-normal font-['Inter']">
              {t('info.beforeSearchMessage', { mediaTypes: mediaTypes })}
            </div>
          </>
        )}
        {/* 검색 결과가 없을 때 표시할 메시지 */}
        {!isVideoLoading &&
          !isComicsLoading &&
          (isSearchResultEmpty ||
            isSelectedSearchTypeEmpty ||
            isOneSelectedAndNoResult) && (
            <div className="mt-60">
              <NodataMessageUi message={t('warn.noSearchData')} />
            </div>
          )}
      </div>
    </>
  );
};

export default Search;
