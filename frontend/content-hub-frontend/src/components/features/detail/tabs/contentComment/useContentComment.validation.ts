import z from 'zod';

/**
 * useContentCommentSchema
 * 코멘트 작성 시 유효성 검사 스키마
 */
export const useContentCommentSchema = z.object({
  // 별점
  starRating: z.number().optional(),
  // 코멘트 작성 시 최소 0.5 이상이어야 함
  // starRating: z.number().min(0.5, {message: "별점을 입력해 주세요."}),
  // 코멘트
  comment: z.string(),
  // 코멘트 작성 시 최소 1자 이상이어야 함
  // comment: z.string().min(1, { message: "코멘트를 입력해 주세요." }),
});

/**
 * ContentCommentSchema
 * useContentCommentSchema의 타입을 추론하여 ContentCommentSchema로 사용
 */
export type ContentCommentSchema = z.infer<typeof useContentCommentSchema>;
