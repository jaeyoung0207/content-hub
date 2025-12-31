import i18n from '@/i18n';
import z from 'zod';

/**
 * useDetailCommentsSchema
 * 코멘트 작성 시 유효성 검사 스키마
 */
export const useDetailCommentsSchema = z.object({
  // 별점
  starRating: z
    .number()
    .min(0.5, { message: i18n.t('validation.starRatingError') }),
  // 코멘트
  comment: z.string(),
});

/**
 * DetailCommentsSchema
 * useDetailCommentsSchema의 타입을 추론하여 DetailCommentsSchema로 사용
 */
export type DetailCommentsSchema = z.infer<typeof useDetailCommentsSchema>;
