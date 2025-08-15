import z from 'zod';

/**
 * 홈 스키마
 * @returns 홈 스키마
 */
export const useHomeSchema = () => {
  const homeSchema = z.object({});
  return homeSchema;
};

/**
 * 홈 스키마 타입 정의
 */
export type HomeSchema = z.infer<ReturnType<typeof useHomeSchema>>;
