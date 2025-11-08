import { PagenationUi } from '@/components/ui/PagenationUi';
import { useMyComments } from './useMyComments';
import { LoadingUi } from '@/components/ui/common/LoadingUi';
import {
  COMMON_IMAGES,
  TMDB_API_IMAGE_DOMAIN,
  WIDTH_300,
} from '@/components/common/constants/constants';
import { getContentMediaType } from '@/components/common/utils/convertUtil';
import { useNavigate } from 'react-router-dom';
import { detailUrlQuery } from '@/components/common/utils/urlUtil';
import { useTranslation } from 'react-i18next';
import { settings } from '@/components/common/config/settings';
import { StarRatingUi } from '@/components/ui/StarRatingUi';
import { ButtonUi, LazyImage } from '@/components/ui/common';
import { BsStarFill } from 'react-icons/bs';
import { isMobileOnly } from 'react-device-detect';

/**
 * 나의 코멘트 컴포넌트
 */
export const MyComments = () => {
  // i18n 훅
  const { t } = useTranslation();
  // navigate 훅
  const navigate = useNavigate();

  // 나의 코멘트 훅
  const {
    data,
    isLoading,
    currentPage,
    totalPages,
    totalElements,
    handlePageOnClick,
    isOmitComment,
    handleOnClickOmitComment,
    control,
  } = useMyComments();

  // 썸네일 이미지 경로
  const thumbnailImagePath = TMDB_API_IMAGE_DOMAIN + WIDTH_300;
  // 코멘트 생략 처리 기준(개행 문자 수)
  const isOmitCommentLf = settings.commentLfOmissionLength;
  // 코멘트 생략 처리 기준(글자 수)
  const isOmitCommentLength = settings.commentLengthOmissionLength;
  return (
    <div className="px-4 pt-20 pb-10 sm:px-6 lg:px-8">
      <div className="mb-4 text-2xl font-bold sm:text-3xl">
        {t('info.myComments')}
      </div>
      {isLoading ? (
        <LoadingUi />
      ) : data && data.length > 0 ? (
        <div>
          <div className="mb-4 text-lg font-semibold sm:text-xl">
            {t('info.totalComments', { totalElements })}
          </div>

          {/* 코멘트 리스트 */}
          <div className="grid grid-cols-1 gap-4 xl:grid-cols-2">
            {data.map((items, index) => {
              // 코믹스 여부
              const isComics =
                items.contentMediaType === getContentMediaType().comicsCode;
              // 썸네일 이미지 경로
              const thumbnailImageUrl = items.thumbnailImageUrl
                ? isComics
                  ? items.thumbnailImageUrl
                  : thumbnailImagePath + items.thumbnailImageUrl
                : COMMON_IMAGES.NO_IMAGE;
              // 코멘트 배열화
              const commentArray = (items.comment ?? '').split('\n');
              // 코멘트 생략 처리(개행 문자 수)
              const isLfOmit = commentArray.length > isOmitCommentLf;
              // 코멘트 생략 처리(글자 수)
              const isLengthOmit =
                (items.comment ?? '').length > isOmitCommentLength;
              // 표시할 코멘트
              const comment = !isOmitComment[index]
                ? isLfOmit
                  ? commentArray.slice(0, isOmitCommentLf).join('\n') +
                  t('info.omissionString')
                  : isLengthOmit
                    ? items.comment?.substring(0, isOmitCommentLength) +
                    t('info.omissionString')
                    : items.comment
                : items.comment;

              return (
                <article
                  key={items.commentId}
                  className="flex gap-1 rounded-2xl border border-black/5 p-3 shadow-sm"
                  aria-label={t('info.userComment') || 'User comment'}
                >
                  <div className="flex w-full gap-3">
                    <div className="relative flex w-[28%] shrink-0 overflow-hidden rounded-xl bg-white sm:w-40 md:pl-2">
                      <div
                        className={`relative aspect-[16/9] w-full cursor-pointer`}
                        onClick={() => {
                          const detailUrl = detailUrlQuery({
                            contentMediaType: items.contentMediaType,
                            apiId: items.apiId,
                            tabNo: 3, // 코멘트 탭
                          });
                          navigate(detailUrl);
                        }}
                        title={items.title || ''} // 썸네일에 제목 툴팁 추가
                      >
                        {/* 썸네일 이미지 */}
                        <div
                          className={`relative flex aspect-[16/9] justify-center overflow-hidden rounded-2xl`}
                        >
                          <LazyImage
                            src={thumbnailImageUrl}
                            alt={items.title || 'thumbnail'}
                            className="inset-0 h-full w-full object-contain"
                          />
                        </div>
                        {/* 제목 */}
                        <div className="mt-1 w-full truncate text-left text-sm font-medium hover:underline">
                          {items.title}
                        </div>
                      </div>
                    </div>

                    <div className="w-[72%] min-w-0 grow px-2">
                      <div className="mb-1 flex items-center justify-between gap-2">
                        {items.starRating && (
                          <div
                            className="inline-flex items-center px-1.5 py-0.5 text-xs font-medium md:gap-2 md:text-base"
                            aria-label={`별점 ${items.starRating}점 / 5점`}
                          >
                            {/* 별점 표시 */}
                            {isMobileOnly ? (
                              <BsStarFill
                                className={`mr-2 h-4 w-4 text-yellow-400`}
                                aria-hidden="true"
                              />
                            ) : (
                              <StarRatingUi
                                name={'starRating'}
                                control={control}
                                selectedStarRating={items.starRating}
                                isStarRatingEditable={false}
                              />
                            )}
                            {/* 별점 수 */}
                            <span className="text-sm text-gray-700 md:text-base">
                              {items.starRating}
                            </span>
                          </div>
                        )}
                        {/* 작성 일시 */}
                        <time className="shrink-0 text-xs text-gray-500 md:text-sm">
                          {items.createTimeStr}
                        </time>
                      </div>
                      {/* 코멘트 내용 */}
                      <div className="pl-2 text-sm whitespace-pre-line sm:text-base">
                        <div>
                          <div>{comment}</div>
                          {(isLfOmit || isLengthOmit) && (
                            <ButtonUi
                              type="button"
                              variant="ghost"
                              size="sm"
                              className="mt-2 cursor-pointer p-0 text-gray-600 underline"
                              onClick={() => handleOnClickOmitComment(index)}
                            >
                              {isOmitComment[index]
                                ? t('info.inShort')
                                : t('info.readMore')}
                            </ButtonUi>
                          )}
                        </div>
                      </div>
                    </div>
                  </div>
                </article>
              );
            })}
          </div>
          {/* 페이지 네비게이션 */}
          <div className="mt-6 flex justify-center">
            <PagenationUi
              pageCount={totalPages} // 전체 페이지 수
              pageRangeDisplayed={settings.pageRangeDisplayed} // 한 번에 표시할 페이지 수
              marginPagesDisplayed={settings.marginPagesDisplayed} // 양쪽에 표시할 페이지 수
              currentPage={currentPage} // 현재 페이지 (0부터 시작)
              onPageChange={(selectedItem) =>
                handlePageOnClick(selectedItem.selected)
              } // 페이지 변경 시 호출되는 함수
            />
          </div>
        </div>
      ) : (
        // 코멘트가 없을 때
        <div className="mt-20 flex h-full items-center justify-center text-2xl">
          {t('info.noMyComments')}
        </div>
      )}
    </div>
  );
};

export default MyComments;
