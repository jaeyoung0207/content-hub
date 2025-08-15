import { useEffect, useState } from 'react';
import { handleUnExceptedError } from '../utils/commonUtil';

/**
 * 대상 값을 지정된 시간만큼 지연시킨 후에 반환하는 hook
 * @param value 대상 값
 * @param delay 지연 시간
 * @returns 지연된 후의 값
 */
export const useDebounce = <T>(value: T, delay: number): T => {
  const [debouncedValue, setDebouncedValue] = useState<T>(value);

  useEffect(() => {
    try {
      const timeout = setTimeout(() => setDebouncedValue(value), delay);
      return () => clearTimeout(timeout);
    } catch (error) {
      handleUnExceptedError(error);
    }
  });

  return debouncedValue;
};
