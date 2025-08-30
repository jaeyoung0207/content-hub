import { useTranslation } from 'react-i18next';
import { useSearch } from './useSearch';
import {
  MEDIA_TYPE,
  SEARCH_SCREEN_TYPE,
} from '@/components/common/constants/constants';
import { useSearchTypeStore } from '@/components/common/store/globalStateStore';
import { LoadingUi } from '@/components/ui/LoadingUi';
import { SearchPropsType } from './SearchPage';
import { NodataMessageUi } from '@/components/ui/common/NodataMessageUi';
import DisplaySearchResults from '@/components/ui/DisplaySearchResultsUi';

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
  const isAniViewMore = data?.videoResult?.aniViewMore;
  const isDramaViewMore = data?.videoResult?.dramaViewMore;
  const isMovieViewMore = data?.videoResult?.movieViewMore;
  const isComicsViewMore = data?.comicsResult?.comicsViewMore;

  // 검색 결과 인자값 리스트
  const dataList = [
    {
      displayFlg: searchTypeState.aniFlg,
      dataResults: aniSearchResults,
      media: t('info.ani'),
      isViewMore: isAniViewMore,
      mediaType: MEDIA_TYPE.ANI,
    },
    {
      displayFlg: searchTypeState.dramaFlg,
      dataResults: dramaSearchResults,
      media: t('info.drama'),
      isViewMore: isDramaViewMore,
      mediaType: MEDIA_TYPE.DRAMA,
    },
    {
      displayFlg: searchTypeState.movieFlg,
      dataResults: movieSearchResults,
      media: t('info.movie'),
      isViewMore: isMovieViewMore,
      mediaType: MEDIA_TYPE.MOVIE,
    },
    {
      displayFlg: searchTypeState.comicsFlg,
      dataResults: comicsSearchResults,
      media: t('info.comics'),
      isViewMore: isComicsViewMore,
      mediaType: MEDIA_TYPE.COMICS,
    },
  ];

  return (
    <>
      <div className="mt-28 w-sm lg:w-7xl">
        {/* 검색 결과 */}
        {isLoading ? (
          <LoadingUi />
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
                      mediaType={items.mediaType}
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
        {!isLoading &&
          (!aniSearchResults || aniSearchResults?.length === 0) &&
          (!dramaSearchResults || dramaSearchResults?.length === 0) &&
          (!movieSearchResults || movieSearchResults?.length === 0) &&
          (!comicsSearchResults || comicsSearchResults?.length === 0) && (
            <div className="mt-60">
              <NodataMessageUi message={t('warn.noSearchData')} />
            </div>
          )}
        {/* 검색 전 메세지 */}
        {aniSearchResults === undefined &&
          dramaSearchResults === undefined &&
          movieSearchResults === undefined &&
          comicsSearchResults === undefined && (
            <div className="mt-25 lg:mt-60 flex justify-center items-center text-black text-xl lg:text-2xl font-normal font-['Inter']">
              {t('info.beforeSearchMessage')}
            </div>
          )}
      </div>
    </>
  );
};

export default Search;
