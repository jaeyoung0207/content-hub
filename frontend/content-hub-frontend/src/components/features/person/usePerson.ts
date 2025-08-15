import { Person } from '@/api/Person';
import { useQuery } from '@tanstack/react-query';
import { personQueryKeys } from './queryKeys/personQueryKeys';
import { PersonResponseDto } from '@/api/data-contracts';

/**
 * 인물 화면 훅 반환 타입
 */
type UsePersonReturnType = {
  data: PersonResponseDto | undefined;
  isLoading: boolean;
  isError: boolean;
};

/**
 * 인물 화면 훅
 * @param personId 인물 ID
 * @returns UsePersonReturnType
 */
export const usePerson = (personId: string): UsePersonReturnType => {
  // ================================================================================================== react query

  // person API 인스턴스 생성
  const personApi = new Person();

  // 인물 데이터 API 호출
  const { data, isLoading, isError } = useQuery<PersonResponseDto>({
    queryKey: personQueryKeys.person(personId),
    queryFn: async () => {
      return (await personApi.getPersonDetails({ personId: Number(personId) }))
        .data;
    },
  });

  // ================================================================================================== return

  return {
    data: data,
    isLoading: isLoading,
    isError: isError,
  };
};
