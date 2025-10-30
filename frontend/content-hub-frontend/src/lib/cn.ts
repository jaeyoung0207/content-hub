import { clsx } from 'clsx';
import { twMerge } from 'tailwind-merge';

// tailwind 클래스 안전 병합 유틸
export function cn(...inputs: Array<string | undefined | null | false>) {
  return twMerge(clsx(inputs));
}
