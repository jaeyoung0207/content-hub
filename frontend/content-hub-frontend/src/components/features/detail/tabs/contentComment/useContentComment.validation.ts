import i18n from '@/i18n';
import z from 'zod';

/**
 * useContentCommentSchema
 * 코멘트 작성 시 유효성 검사 스키마
 */
export const useContentCommentSchema = z.object({
  // 별점
  starRating: z
    .number()
    .min(0.5, { message: i18n.t('validation.starRatingError') }),
  // 코멘트
  comment: z.string(),
  // comment: z.string().min(1, { message: "코멘트를 입력해 주세요." }),
});

/**
 * ContentCommentSchema
 * useContentCommentSchema의 타입을 추론하여 ContentCommentSchema로 사용
 */
export type ContentCommentSchema = z.infer<typeof useContentCommentSchema>;
