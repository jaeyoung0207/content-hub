import { CharacterApi } from '@/api/CharacterApi';
import {
  AniListCharactersNodeDto,
  AniListStaffNodeDto,
} from '@/api/data-contracts';
import { COMICS_CREDITS_TYPE } from '@/components/common/constants/constants';
import { useQuery } from '@tanstack/react-query';
import { useState } from 'react';

/**
 * 캐릭터 화면 훅 반환 타입
 */
type UseCharacterReturnType = {
  data: AniListCharactersNodeDto | AniListStaffNodeDto | undefined;
  isLoading: boolean;
  isError: boolean;
  isSpoilerName: boolean;
  toggleSpoilerName?: () => void;
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
  // ================================================================================================== react hook

  // 스포일러 이름 표시 여부 상태
  const [isSpoilerName, setIsSpoilerName] = useState<boolean>(false);

  // ================================================================================================== react query

  // character API 인스턴스 생성
  const characterApi = new CharacterApi();

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

  // ================================================================================================== function

  /**
   * 스포일러 이름 표시 여부 토글 함수
   */
  const toggleSpoilerName = () => {
    setIsSpoilerName((prev) => !prev);
  };

  // ================================================================================================== return

  return {
    data: data,
    isLoading: isLoading,
    isError: isError,
    isSpoilerName: isSpoilerName,
    toggleSpoilerName: toggleSpoilerName,
  };
};
