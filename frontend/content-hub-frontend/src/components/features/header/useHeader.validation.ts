import z from 'zod';

/**
 * 헤더 스키마 정의
 * @returns 헤더 스키마
 */
export const useHeaderSchema = () => {

    const headerSchema = z.object({
        keyword: z.string().nullable().optional(),
        adultFlg: z.boolean().optional(),
    });
    return headerSchema;
}

/**
 * 헤더 스키마 타입 정의
 */
export type HeaderSchema = z.infer<ReturnType<typeof useHeaderSchema>>;
