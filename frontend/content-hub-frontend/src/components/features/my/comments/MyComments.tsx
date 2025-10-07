import { PagenationUi } from '@/components/ui/PagenationUi';
import { useMyComments } from './useMyComments';
import { LoadingUi } from '@/components/ui/LoadingUi';
import {
  COMMON_IMAGES,
  IS_MOBILE,
  TMDB_API_IMAGE_DOMAIN,
  WIDTH_300,
} from '@/components/common/constants/constants';
import { getContentMediaType } from '@/components/common/utils/convertUtil';
import { useNavigate } from 'react-router-dom';
import { detailUrlQuery } from '@/components/common/utils/urlUtil';
import { useTranslation } from 'react-i18next';
import { settings } from '@/components/common/config/settings';
import { StarRatingUi } from '@/components/ui/StarRatingUi';

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
    perPageCountRef,
    control,
  } = useMyComments();

  // 썸네일 이미지 경로
  const thumbnailImagePath = TMDB_API_IMAGE_DOMAIN + WIDTH_300;
  // 아이템 번호
  const itemNo = totalElements - currentPage * perPageCountRef.current;
  // 코멘트 생략 처리 기준(개행 문자 수)
  const isOmitCommentLf = settings.commentLfOmissionLength;
  // 코멘트 생략 처리 기준(글자 수)
  const isOmitCommentLength = settings.commentLengthOmissionLength;
  return (
    <div className="mt-25 p-4">
      <div className="mb-5 text-3xl font-bold">{t('info.myComments')}</div>
      <div className="">
        {isLoading ? (
          <LoadingUi />
        ) : data && data.length > 0 ? (
          <div>
            <div className="mb-5 text-xl font-bold">
              {t('info.totalComments', { totalElements })}
            </div>
            {data.map((items, index) => {
              // 썸네일 이미지 경로
              const thumbnailImageUrl = items.thumbnailImageUrl
                ? items.contentMediaType === getContentMediaType().comicsCode
                  ? items.thumbnailImageUrl
                  : thumbnailImagePath + items.thumbnailImageUrl
                : COMMON_IMAGES.NO_IMAGE;
              // 코멘트 배열화
              const commentArray = items.comment!.split('\n');
              // 코멘트 생략 처리(개행 문자 수)
              const isLfOmit = commentArray.length > isOmitCommentLf;
              // 코멘트 생략 처리(글자 수)
              const isLengthOmit = items.comment!.length > isOmitCommentLength;
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
                <div
                  key={items.commentId}
                  className={`mb-3 pt-4 flex items-start border-1 p-3 rounded-2xl shadow-md ${IS_MOBILE ? 'w-sm' : ''}`}
                >
                  {/* 코멘트 번호 */}
                  <div className="flex justify-center w-[3%] break-words">
                    <div className="max-w-full">
                      {itemNo - (data.length - (data.length - index))}
                    </div>
                  </div>
                  <div
                    className="w-[25%] block hover:text-blue-500 cursor-pointer"
                    onClick={() => {
                      const detailUrl = detailUrlQuery({
                        contentMediaType: items.contentMediaType,
                        apiId: items.apiId,
                        tabNo: 3, // 코멘트 탭
                      });
                      navigate(detailUrl);
                    }}
                  >
                    <div>
                      {/* 썸네일 이미지 */}
                      <div className="flex justify-center items-center mb-2">
                        <img
                          className="w-[250px] h-[150px] object-scale-down rounded-2xl"
                          alt="thumbnail"
                          src={thumbnailImageUrl}
                          onError={(e) => {
                            e.currentTarget.src = COMMON_IMAGES.NO_IMAGE;
                          }}
                        />
                      </div>
                      {/* 제목 */}
                      <div className="flex justify-center items-center">
                        {items.title}
                      </div>
                    </div>
                  </div>
                  <div className="w-[72%]">
                    <div className="flex items-center justify-between">
                      {items.starRating && (
                        <div className="flex justify-start items-center ml-4">
                          <div>
                            {/* 별점 표시 */}
                            <StarRatingUi
                              name={'starRating'}
                              control={control}
                              selectedStarRating={items.starRating}
                              isStarRatingEditable={false}
                            />
                          </div>
                          {/* 별점 수 */}
                          <div className="ml-3">{items.starRating}</div>
                        </div>
                      )}
                      {/* 작성 일시 */}
                      <div className="mr-5">{items.createTimeStr}</div>
                    </div>
                    {/* 코멘트 내용 */}
                    <li className="flex items-end p-4 whitespace-pre-line">
                      <div>
                        <div>{comment}</div>
                        {(isLfOmit || isLengthOmit) && (
                          <div
                            className="mt-2 text-sm text-gray-500 cursor-pointer"
                            onClick={() => handleOnClickOmitComment(index)}
                          >
                            {isOmitComment[index]
                              ? t('info.inShort')
                              : t('info.readMore')}
                          </div>
                        )}
                      </div>
                    </li>
                  </div>
                </div>
              );
            })}
            {/* 페이지 네비게이션 */}
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
        ) : (
          // 코멘트가 없을 때
          <div className="mt-20 flex justify-center items-center h-full text-2xl">
            {t('info.noMyComments')}
          </div>
        )}
      </div>
    </div>
  );
};

export default MyComments;
