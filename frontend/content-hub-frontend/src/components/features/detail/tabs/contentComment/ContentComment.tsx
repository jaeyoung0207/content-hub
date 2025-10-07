import { StarRatingUi } from '@/components/ui/StarRatingUi';
import { useUserStore } from '@/components/common/store/globalStateStore';
import { LoadingUi } from '@/components/ui/LoadingUi';
import { useContentComment } from './useContentComment';
import { ConfirmModalUi } from '@/components/ui/ConfirmModalUi';
import { useTranslation } from 'react-i18next';
import { CommentTextAreaUi } from '@/components/ui/CommentTextAreaUi';
import { DetailResponseType } from '../../useDetail';
import { commonErrorHandler } from '@/components/common/utils/errorUtil';
import { settings } from '@/components/common/config/settings';
import { IS_MOBILE } from '@/components/common/constants/constants';

/**
 * 콘텐츠 코멘트 컴포넌트 props 타입
 */
type ContentCommentPropsType = {
  detailResult: DetailResponseType;
  contentMediaType: string;
};

/**
 * 콘텐츠 코멘트 컴포넌트
 * 코멘트 작성, 수정, 삭제 기능을 제공하며, 코멘트 목록을 표시
 * @param detailResult 상세 정보 결과
 * @param contentMediaType 컨텐츠 미디어 타입
 */
export const ContentComment = ({
  detailResult,
  contentMediaType,
}: ContentCommentPropsType) => {
  // i18n 번역 훅
  const { t } = useTranslation();
  // 유저 정보 전역 상태 저장 훅
  const { user } = useUserStore();

  // useContentComment 훅을 사용하여 코멘트 관련 로직을 처리
  const {
    control,
    data,
    handleCommentOnClick,
    handleEditComment,
    handleSaveComment,
    handleUpdateComment,
    hasNextPage,
    isFetchingNextPage,
    setObserveTarget,
    totalElements,
    textAreaRef,
    isMyComment,
    comment,
    starRating,
    isCommentEditable,
    isDeleteConfirmOpen,
    handleDeleteOnClick,
    handleDeleteConfirmOk,
    handleDeleteConfirmCancel,
    starRatingErrorMsg,
    isOmitComment,
    handleOnClickOmitComment,
  } = useContentComment(detailResult, contentMediaType);

  // 코멘트 작성 버튼 스타일
  const commentButtonStyle =
    'w-20 h-10 border-1 rounded-md bg-blue-600 text-2xl disabled:bg-gray-500 text-white cursor-pointer';
  // 코멘트 최대 글자 수
  const commentMaxLength = settings.commentMaxLength;
  // 코멘트 생략 처리 기준(개행 문자 수)
  const isOmitCommentLf = settings.commentLfOmissionLength;
  // 코멘트 생략 처리 기준(글자 수)
  const isOmitCommentLength = settings.commentLengthOmissionLength;

  return (
    <>
      {/* 코멘트 작성/수정 영역 */}
      {
        // 로그인한 유저가 작성한 코멘트가 존재하지 않거나, 코멘트가 존재하고 코멘트 수정 가능 상태인 경우에만 코멘트 작성/수정 영역을 표시
        typeof isMyComment === 'boolean' &&
          (!isMyComment || (isMyComment && isCommentEditable)) && (
            <div>
              {/* 별점 선택 */}
              <div className="mb-1 flex justify-center text-xl">
                {t('info.starRating')}
              </div>
              {/* 별점 UI */}
              <StarRatingUi
                name="starRating"
                control={control}
                isStarRatingEditable={true}
                selectedStarRating={isCommentEditable ? starRating : undefined}
                starRatingErrorMsg={starRatingErrorMsg}
              />
              {/* 코멘트 입력 UI */}
              <div className="flex justify-center">
                <div>
                  <CommentTextAreaUi
                    name="comment"
                    control={control}
                    onClick={commonErrorHandler(handleCommentOnClick)}
                    textAreaRef={textAreaRef}
                    maxLength={commentMaxLength}
                    textAreaStyle={IS_MOBILE ? 'w-sm h-32' : undefined}
                  />
                  {/* 글자 수 */}
                  <div className="flex justify-end text-sm text-gray-500">
                    {comment ? comment.length : 0}/{commentMaxLength}
                  </div>
                </div>
              </div>
              {/* 버튼 영역 */}
              <div className="mt-1 mb-2 flex justify-center">
                {
                  // isMyComment가 true이면서 isCommentEditable가 true인 경우에는 코멘트 수정 버튼을 표시
                  isMyComment && isCommentEditable && (
                    <button
                      className={commentButtonStyle}
                      onClick={commonErrorHandler(handleUpdateComment)}
                      disabled={!comment}
                    >
                      {t('info.update')}
                    </button>
                  )
                }
                {
                  // isMyComment가 false인 경우에는 코멘트 작성 버튼을 표시
                  !isMyComment && (
                    <button
                      className={commentButtonStyle}
                      onClick={commonErrorHandler(handleSaveComment)}
                      disabled={!comment}
                    >
                      {t('info.save')}
                    </button>
                  )
                }
              </div>
            </div>
          )
      }

      {/* 삭제 확인 다이얼로그 */}
      {isDeleteConfirmOpen && (
        <ConfirmModalUi
          isOpen={isDeleteConfirmOpen}
          onOk={handleDeleteConfirmOk}
          onCancel={handleDeleteConfirmCancel}
          confirmMsg={t('info.deleteConfirmMsg')}
        />
      )}
      {/* 코멘트 수 */}
      <div className="ml-5 mr-5 mb-3 flex justify-items-start border-b border-gray-300 text-2xl font-bold">
        {t('info.totalReviewCount', { count: totalElements })}
      </div>
      {/* 코멘트 목록 */}
      <div className="mt-3 ml-10 mr-10">
        {data?.pages.flat().map((items, index) => {
          // items가 없으면 null 반환
          if (!items) {
            return null;
          }
          // 코멘트 배열화
          const commentArray = items.comment!.split('\n');
          // 코멘트 생략 처리(개행 문자)
          const isLfOmit =
            commentArray && commentArray.length > isOmitCommentLf;
          // 코멘트 생략 처리(글자 수)
          const isLengthOmit =
            items.comment && items.comment!.length > isOmitCommentLength;
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
            <div key={items?.commentId}>
              {items && (
                <div className="mt-5 mb-5">
                  <div className="flex justify-items-start items-center">
                    <StarRatingUi
                      name="starRating"
                      control={control}
                      isStarRatingEditable={false}
                      selectedStarRating={items.starRating}
                    />
                    {/* 별점 표시 */}
                    <div className="ml-2">{items.starRating}</div>
                  </div>
                  <div className="flex justify-between">
                    <div className="flex items-center mb-2">
                      {/* 작성자 */}
                      <div className="mr-3 font-bold text-xl">
                        {items.nickname}
                      </div>
                      {/* 작성 일자 */}
                      <div>{items.createTime}</div>
                    </div>
                    {user?.id === items.providerId && (
                      <div>
                        <ul className="flex">
                          {/* 수정 버튼 */}
                          <li className="mr-2">
                            <button
                              className="cursor-pointer"
                              onClick={commonErrorHandler(() =>
                                handleEditComment(items)
                              )}
                            >
                              {t('info.update')}
                            </button>
                          </li>
                          {/* 삭제 버튼 */}
                          <li>
                            <button
                              className={`cursor-pointer ${isCommentEditable ? 'opacity-50 cursor-not-allowed' : ''}`}
                              onClick={commonErrorHandler(() =>
                                handleDeleteOnClick(items.commentId!)
                              )}
                              disabled={isCommentEditable}
                            >
                              {t('info.delete')}
                            </button>
                          </li>
                        </ul>
                      </div>
                    )}
                  </div>
                  {/* 코멘트 내용 */}
                  <div className="mr-1 whitespace-pre-line">
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
                  </div>
                </div>
              )}
            </div>
          );
        })}
      </div>
      {
        // 다음 페이지가 로딩 중인 경우 로딩 UI 표시
        isFetchingNextPage && <LoadingUi />
      }
      {
        // 다음 페이지가 존재하는 경우, 관찰 대상 요소를 설정하여 무한 스크롤 구현
        hasNextPage && <div ref={(el) => setObserveTarget(el)}></div> // ref를 함수 형태로 지정 -> DOM이 생기거나 없어질 때마다 실행되면서 setObserveTarget을 호출
      }
    </>
  );
};

export default ContentComment;
