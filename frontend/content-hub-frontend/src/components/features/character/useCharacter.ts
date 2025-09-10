import { Character } from '@/api/Character';
import {
  AniListCharactersNodeDto,
  AniListStaffNodeDto,
} from '@/api/data-contracts';
import { COMICS_CREDITS_TYPE } from '@/components/common/constants/constants';
import { useQuery } from '@tanstack/react-query';

/**
 * 캐릭터 화면 훅 반환 타입
 */
type UseCharacterReturnType = {
  data: AniListCharactersNodeDto | AniListStaffNodeDto | undefined;
  isLoading: boolean;
  isError: boolean;
};

/**
 * 캐릭터 화면 훅
 * @param characterId 캐릭터 ID
 * @returns UseCharacterReturnType
 */
export const useCharacter = (
  comicsCreditsType: string,
  creditsId: string
): UseCharacterReturnType => {
  // ================================================================================================== react query

  // character API 인스턴스 생성
  const characterApi = new Character();

  // 캐릭터 데이터 API 호출
  const { data, isLoading, isError } = useQuery<
    AniListCharactersNodeDto | AniListStaffNodeDto
  >({
    queryKey: ['character', creditsId],
    queryFn: async () => {
      if (comicsCreditsType === COMICS_CREDITS_TYPE.CHARACTER) {
        return (
          await characterApi.getCharacter({ character_id: Number(creditsId) })
        ).data;
      } else {
        return (await characterApi.getStaff({ staff_id: Number(creditsId) }))
          .data;
      }
    },
  });

  // ================================================================================================== return

  return {
    data,
    isLoading,
    isError,
  };
};
