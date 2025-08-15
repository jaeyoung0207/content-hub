import { Character } from '@/api/Character';
import { AniListCharactersNodesDto } from '@/api/data-contracts';
import { useQuery } from '@tanstack/react-query';

/**
 * 캐릭터 화면 훅 반환 타입
 */
type UseCharacterReturnType = {
  data: AniListCharactersNodesDto | undefined;
  isLoading: boolean;
  isError: boolean;
};

/**
 * 캐릭터 화면 훅
 * @param characterId 캐릭터 ID
 * @returns UseCharacterReturnType
 */
export const useCharacter = (characterId: string): UseCharacterReturnType => {
  // ================================================================================================== react query

  // character API 인스턴스 생성
  const characterApi = new Character();

  // 캐릭터 데이터 API 호출
  const { data, isLoading, isError } = useQuery<AniListCharactersNodesDto>({
    queryKey: ['character', characterId],
    queryFn: async () => {
      return (
        await characterApi.getCharacter({ characterId: Number(characterId) })
      ).data;
    },
  });

  // ================================================================================================== return

  return {
    data,
    isLoading,
    isError,
  };
};
