import { StarRatingUi } from "@/components/ui/StarRatingUi"
import { useUserStore } from "@/components/common/store/globalStateStore"
import { LoadingUi } from "@/components/ui/LoadingUi"
import { useContentComment } from "./useContentComment"
import { ConfirmModalUi } from "@/components/ui/ConfirmModalUi"
import { useTranslation } from "react-i18next"
import { CommentTextAreaUi } from "@/components/ui/CommentTextAreaUi"
import { DetailResponseType } from "../../useDetail"
import { commonErrorHandler } from "@/components/common/utils/commonUtil"

/**
 * 콘텐츠 코멘트 컴포넌트 props 타입
 */
type ContentCommentPropsType = {
    detailResult: DetailResponseType,
    originalMediaType: string,
}

/**
 * 콘텐츠 코멘트 컴포넌트
 * 코멘트 작성, 수정, 삭제 기능을 제공하며, 코멘트 목록을 표시
 * @param detailResult 상세 정보 결과
 * @param originalMediaType 원본 미디어 타입
 */
export const ContentComment = ({ detailResult, originalMediaType }: ContentCommentPropsType) => {
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
        isLoginConfirmOpen,
        handleLoginConfirmOk,
        handleLoginConfirmCancel,
        textAreaRef,
        isMyComment,
        comment,
        isCommentEditable,
        isDeleteConfirmOpen,
        handleDeleteOnClick,
        handleDeleteConfirmOk,
        handleDeleteConfirmCancel,
        starRatingErrorMsg,
    } = useContentComment(detailResult, originalMediaType);

    // 코멘트 작성 버튼 스타일
    const commentButtonStyle = "w-20 h-10 border-1 rounded-md bg-blue-600 text-2xl disabled:bg-gray-500 text-white cursor-pointer";

    return (
        <>
            {/* 코멘트 작성/수정 영역 */}
            {
                // 로그인한 유저가 작성한 코멘트가 존재하지 않거나, 코멘트가 존재하고 코멘트 수정 가능 상태인 경우에만 코멘트 작성/수정 영역을 표시
                typeof isMyComment === "boolean" &&
                (!isMyComment || (isMyComment && isCommentEditable)) &&
                <div>
                    {/* 별점 선택 */}
                    <div className="mb-1 flex justify-center text-xl">
                        {t("info.starRating")}
                    </div>
                    {/* 별점 UI */}
                    <StarRatingUi name="starRating" control={control} starRatingErrorMsg={starRatingErrorMsg} />
                    {/* 코멘트 입력 UI */}
                    <CommentTextAreaUi name="comment" control={control} onClick={commonErrorHandler(handleCommentOnClick)} textAreaRef={textAreaRef} />
                    {/* 버튼 영역 */}
                    <div className="mt-2 mb-2 flex justify-center">
                        {
                            // isMyComment가 true이면서 isCommentEditable가 true인 경우에는 코멘트 수정 버튼을 표시
                            isMyComment && isCommentEditable &&
                            <button
                                className={commentButtonStyle}
                                onClick={commonErrorHandler(handleUpdateComment)}
                                disabled={!!!comment}
                            >
                                {t("info.update")}
                            </button>
                        }
                        {
                            // isMyComment가 false인 경우에는 코멘트 작성 버튼을 표시
                            !isMyComment &&
                            <button
                                className={commentButtonStyle}
                                onClick={commonErrorHandler(handleSaveComment)}
                                disabled={!!!comment}
                            >
                                {t("info.save")}
                            </button>
                        }
                    </div>
                </div>
            }

            {/* 로그인 확인 다이얼로그 */}
            {
                isLoginConfirmOpen &&
                <ConfirmModalUi isOpen={isLoginConfirmOpen} onOk={handleLoginConfirmOk} onCancel={handleLoginConfirmCancel} confirmMsg={t("info.loginConfirmMsg2")} />
            }

            {/* 삭제 확인 다이얼로그 */}
            {
                isDeleteConfirmOpen &&
                <ConfirmModalUi isOpen={isDeleteConfirmOpen} onOk={handleDeleteConfirmOk} onCancel={handleDeleteConfirmCancel} confirmMsg={t("info.deleteConfirmMsg")} />
            }
            {/* 코멘트 수 */}
            <div className="ml-5 mr-5 mb-3 flex justify-items-start border-b border-gray-300 text-2xl font-bold">
                평가 {totalElements} 개
            </div>
            {/* 코멘트 목록 */}
            <div className="mt-3 ml-10 mr-10">
                {
                    data?.pages.flat().map((items, index) => {
                        return (
                            <div key={index + "_comment"}>
                                {
                                    items &&
                                    <div className="mt-5 mb-5" key={index}>
                                        <div className="flex justify-items-start items-center">
                                            <StarRatingUi name="starRating" control={control} selectedStarRating={items.starRating} />
                                            {/* 별점 표시 */}
                                            <div className="ml-2">
                                                {items.starRating}
                                            </div>
                                        </div>
                                        <div className="flex justify-between">
                                            <div className="flex items-center mb-2">
                                                {/* 작성자 */}
                                                <div className="mr-3 font-bold text-xl">
                                                    {items.nickname}
                                                </div>
                                                {/* 작성 일자 */}
                                                <div>
                                                    {items.createTime}
                                                </div>
                                            </div>
                                            {
                                                user?.id === items.userId &&
                                                <div>
                                                    <ul className="flex">
                                                        {/* 수정 버튼 */}
                                                        <li className="mr-2">
                                                            <button
                                                                className="cursor-pointer"
                                                                onClick={commonErrorHandler(() => handleEditComment(items))}>
                                                                {t("info.update")}
                                                            </button>
                                                        </li>
                                                        {/* 삭제 버튼 */}
                                                        <li>
                                                            <button
                                                                className={`cursor-pointer ${isCommentEditable ? "opacity-50 cursor-not-allowed" : ""}`}
                                                                onClick={commonErrorHandler(() => handleDeleteOnClick(items.commentNo!))} disabled={isCommentEditable}>
                                                                {t("info.delete")}
                                                            </button>
                                                        </li>
                                                    </ul>
                                                </div>
                                            }
                                        </div>
                                        <div className="mr-1">
                                            {items.comment}
                                        </div>
                                    </div>
                                }
                            </div>
                        )
                    })
                }
            </div>
            {
                // 다음 페이지가 로딩 중인 경우 로딩 UI 표시
                isFetchingNextPage &&
                <LoadingUi />
            }
            {
                // 다음 페이지가 존재하는 경우, 관찰 대상 요소를 설정하여 무한 스크롤 구현
                hasNextPage &&
                <div ref={(el) => setObserveTarget(el)} ></div> // ref를 함수 형태로 지정 -> DOM이 생기거나 없어질 때마다 실행되면서 setObserveTarget을 호출
            }
        </>
    )

}