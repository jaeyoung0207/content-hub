import { StarRatingUi } from '@/components/ui/StarRatingUi';
import { useUserStore } from '@/components/common/store/globalStateStore';
import { LoadingUi } from '@/components/ui/common/LoadingUi';
import { useDetailComments } from './useDetailComments';
import { ConfirmModalUi } from '@/components/ui/ConfirmModalUi';
import { useTranslation } from 'react-i18next';
import { CommentTextAreaUi } from '@/components/ui/CommentTextAreaUi';
import { DetailResponseType } from '../../useDetail';
import { commonErrorHandler } from '@/components/common/utils/errorUtil';
import { settings } from '@/components/common/config/settings';

/**
 * 콘텐츠 코멘트 컴포넌트 props 타입
 */
type DetailCommentsPropsType = {
  detailResult: DetailResponseType;
  contentMediaType: string;
};

/**
 * 콘텐츠 코멘트 컴포넌트
 * 코멘트 작성, 수정, 삭제 기능을 제공하며, 코멘트 목록을 표시
 * @param detailResult 상세 정보 결과
 * @param contentMediaType 컨텐츠 미디어 타입
 */
export const DetailComments = ({
  detailResult,
  contentMediaType,
}: DetailCommentsPropsType) => {
  // i18n 번역 훅
  const { t } = useTranslation();
  // 유저 정보 전역 상태 저장 훅
  const { user } = useUserStore();

  // useDetailComments 훅을 사용하여 코멘트 관련 로직을 처리
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
  } = useDetailComments(detailResult, contentMediaType);

  // 코멘트 작성 버튼 스타일
  const commentButtonStyle =
    'w-20 h-10 rounded-md bg-blue-600 text-base md:text-lg disabled:bg-gray-400 text-white cursor-pointer disabled:cursor-not-allowed';
  // 코멘트 최대 글자 수
  const commentMaxLength = settings.commentMaxLength;
  // 코멘트 생략 처리 기준(개행 문자 수)
  const isOmitCommentLf = settings.commentLfOmissionLength;
  // 코멘트 생략 처리 기준(글자 수)
  const isOmitCommentLength = settings.commentLengthOmissionLength;

  return (
    <>
      {/* 코멘트 작성/수정 영역 */}
      <div className="px-4 md:px-6 lg:px-8">
        {
          // 로그인한 유저가 작성한 코멘트가 존재하지 않거나, 코멘트가 존재하고 코멘트 수정 가능 상태인 경우에만 코멘트 작성/수정 영역을 표시
          typeof isMyComment === 'boolean' &&
            (!isMyComment || (isMyComment && isCommentEditable)) && (
              <section aria-label={t('info.writeComment') || 'Write comment'}>
                {/* 별점 선택 */}
                <div className="mb-1 flex justify-center text-lg">
                  {t('info.starRating')}
                </div>
                {/* 별점 UI */}
                <StarRatingUi
                  name="starRating"
                  control={control}
                  isStarRatingEditable={true}
                  selectedStarRating={
                    isCommentEditable ? starRating : undefined
                  }
                  starRatingErrorMsg={starRatingErrorMsg}
                  starSize="md"
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
              </section>
            )
        }
      </div>

      {/* 삭제 확인 다이얼로그 */}
      {isDeleteConfirmOpen && (
        <ConfirmModalUi
          isOpen={isDeleteConfirmOpen}
          onOk={handleDeleteConfirmOk}
          onCancel={handleDeleteConfirmCancel}
          title={t('info.deleteConfirmTitle')}
          confirmMsg={t('info.deleteConfirmMsg')}
        />
      )}
      {/* 코멘트 수 */}
      <div className="flex justify-items-start border-b border-gray-300 px-1 text-lg font-bold md:px-2 md:text-xl lg:px-4">
        {t('info.totalReviewCount', { count: totalElements })}
      </div>
      {/* 코멘트 목록 */}
      <div className="px-1 md:px-2 lg:px-4">
        {data?.pages.flat().map((items, index) => {
          // items가 없으면 null 반환
          if (!items) {
            return null;
          }
          // 코멘트 배열화
          const commentArray = items.comment!.split('\n') || [];
          // 코멘트 생략 처리(개행 문자)
          const isLfOmit = commentArray.length > isOmitCommentLf;
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
            <article
              key={items.commentId}
              className="py-2"
              aria-label={t('info.userComment') || 'User comment'}
            >
              <div className="flex items-center">
                {/* 별점 아이콘 */}
                <StarRatingUi
                  name="starRating"
                  control={control}
                  isStarRatingEditable={false}
                  selectedStarRating={items.starRating}
                />
                {/* 별점 숫자 */}
                <div className="ml-2 text-sm font-bold md:text-base">
                  {items.starRating}
                </div>
              </div>
              {/* 헤더 */}
              <div className="mt-1 flex justify-between lg:mb-1">
                <div className="mb-1 flex items-center">
                  {/* 작성자 */}
                  <div className="mr-3 text-sm font-bold">{items.nickname}</div>
                  {/* 작성 일자 */}
                  <time className="text-xs text-gray-500 md:text-sm">
                    {items.createTime}
                  </time>
                </div>
                {/* 코멘트 작성자가 현재 유저인 경우에만 수정/삭제 액션 표시 */}
                {user?.id === items.providerId && (
                  <ul className="flex gap-3 text-sm">
                    {/* 수정 버튼 */}
                    <li>
                      <button
                        className="cursor-pointer text-gray-500 hover:underline disabled:cursor-not-allowed disabled:opacity-50"
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
                        className="cursor-pointer text-gray-500 hover:underline disabled:cursor-not-allowed disabled:opacity-50"
                        onClick={commonErrorHandler(() =>
                          handleDeleteOnClick(items.commentId!)
                        )}
                        disabled={isCommentEditable}
                      >
                        {t('info.delete')}
                      </button>
                    </li>
                  </ul>
                )}
              </div>

              {/* 코멘트 내용 */}
              <div className="mr-1 text-sm whitespace-pre-line">
                <div>{comment}</div>
                {(isLfOmit || isLengthOmit) && (
                  <button
                    className="mt-2 cursor-pointer text-sm text-gray-600 underline"
                    onClick={() => handleOnClickOmitComment(index)}
                  >
                    {isOmitComment[index]
                      ? t('info.inShort')
                      : t('info.readMore')}
                  </button>
                )}
              </div>
            </article>
          );
        })}
      </div>
      {
        // 다음 페이지가 로딩 중인 경우 로딩 UI 표시
        isFetchingNextPage && (
          <div className="py-4">
            <LoadingUi />
          </div>
        )
      }
      {
        // 다음 페이지가 존재하는 경우, 관찰 대상 요소를 설정하여 무한 스크롤 구현
        hasNextPage && (
          <div ref={(el) => setObserveTarget(el)} aria-hidden="true" />
        ) // ref를 함수 형태로 지정 -> DOM이 생기거나 없어질 때마다 실행되면서 setObserveTarget을 호출
      }
    </>
  );
};

export default DetailComments;
