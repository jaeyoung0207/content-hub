import { useTranslation } from 'react-i18next';
import { useSearch } from './useSearch';
import { SEARCH_SCREEN_TYPE } from '@/components/common/constants/constants';
import { useSearchTypeStore } from '@/components/common/store/globalStateStore';
import { LoadingUi } from '@/components/ui/LoadingUi';
import { SearchPropsType } from './SearchPage';
import { NodataMessageUi } from '@/components/ui/common/NodataMessageUi';
import DisplaySearchResults from '@/components/ui/DisplaySearchResultsUi';
import { getDisplayMediaType } from '@/components/common/utils/convertUtil';

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
  const { data, isLoading } = useSearch(keyword, isAdult!);

  // 검색결과에 따라 미디어 타입별로 결과를 분리
  const aniSearchResults = data?.videoResult?.aniResults;
  const dramaSearchResults = data?.videoResult?.dramaResults;
  const movieSearchResults = data?.videoResult?.movieResults;
  const comicsSearchResults = data?.comicsResult?.comicsResults;
  const documentarySearchResults = data?.videoResult?.documentaryResults;
  const kidsSearchResults = data?.videoResult?.kidsResults;
  const newsSearchResults = data?.videoResult?.newsResults;
  const varietySearchResults = data?.videoResult?.varietyResults;
  const isAniViewMore = data?.videoResult?.isAniViewMore;
  const isDramaViewMore = data?.videoResult?.isDramaViewMore;
  const isMovieViewMore = data?.videoResult?.isMovieViewMore;
  const isComicsViewMore = data?.comicsResult?.isComicsViewMore;
  const isDocumentaryViewMore = data?.videoResult?.isDocumentaryViewMore;
  const isKidsViewMore = data?.videoResult?.isKidsViewMore;
  const isNewsViewMore = data?.videoResult?.isNewsViewMore;
  const isVarietyViewMore = data?.videoResult?.isVarietyViewMore;

  // 검색 결과 인자값 리스트
  const dataList = [
    {
      displayFlg: searchTypeState.aniFlg,
      dataResults: aniSearchResults,
      media: t('info.animation'),
      isViewMore: isAniViewMore,
      displayMediaType: getDisplayMediaType().aniCode,
    },
    {
      displayFlg: searchTypeState.dramaFlg,
      dataResults: dramaSearchResults,
      media: t('info.drama'),
      isViewMore: isDramaViewMore,
      displayMediaType: getDisplayMediaType().dramaCode,
    },
    {
      displayFlg: searchTypeState.movieFlg,
      dataResults: movieSearchResults,
      media: t('info.movie'),
      isViewMore: isMovieViewMore,
      displayMediaType: getDisplayMediaType().movieCode,
    },
    {
      displayFlg: searchTypeState.documentaryFlg,
      dataResults: documentarySearchResults,
      media: t('info.documentary'),
      isViewMore: isDocumentaryViewMore,
      displayMediaType: getDisplayMediaType().documentaryCode,
    },
    {
      displayFlg: searchTypeState.kidsFlg,
      dataResults: kidsSearchResults,
      media: t('info.kids'),
      isViewMore: isKidsViewMore,
      displayMediaType: getDisplayMediaType().kidsCode,
    },
    {
      displayFlg: searchTypeState.newsFlg,
      dataResults: newsSearchResults,
      media: t('info.news'),
      isViewMore: isNewsViewMore,
      displayMediaType: getDisplayMediaType().newsCode,
    },
    {
      displayFlg: searchTypeState.varietyFlg,
      dataResults: varietySearchResults,
      media: t('info.variety'),
      isViewMore: isVarietyViewMore,
      displayMediaType: getDisplayMediaType().varietyCode,
    },
    {
      displayFlg: searchTypeState.comicsFlg,
      dataResults: comicsSearchResults,
      media: t('info.comics'),
      isViewMore: isComicsViewMore,
      displayMediaType: getDisplayMediaType().comicsCode,
    },
  ];

  const isSearchResultEmpty = dataList.every(
    (item) => !item.dataResults || item.dataResults.length === 0
  );

  return (
    <>
      <div className="mt-28 w-sm lg:w-7xl">
        {/* 검색 결과 */}
        {isLoading ? (
          <>
            <LoadingUi />
            <div className="mt-25 lg:mt-60 flex justify-center items-center text-black text-xl lg:text-2xl font-normal font-['Inter']">
              {t('info.beforeSearchMessage')}
            </div>
          </>
        ) : (
          data &&
          dataList.map((items, index) => {
            return (
              <div key={index}>
                {items.displayFlg &&
                  items.dataResults &&
                  items.dataResults.length !== 0 && (
                    // 각 미디어 검색결과 컴포넌트
                    <DisplaySearchResults
                      mediaName={items.media}
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
        )}
        {/* 검색 결과가 없을 때 표시할 메시지 */}
        {!isLoading && isSearchResultEmpty && (
          <div className="mt-60">
            <NodataMessageUi message={t('warn.noSearchData')} />
          </div>
        )}
      </div>
    </>
  );
};

export default Search;
