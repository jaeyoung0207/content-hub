package com.cjy.contenthub.character.service;

import java.io.IOException;

import com.cjy.contenthub.common.api.dto.aniist.AniListCharactersNodesDto;

/**
 * 캐릭터 서비스 인터페이스
 */
public interface CharacterService {
	
	/**
	 * 캐릭터 조회
	 *
	 * @param characterId 캐릭터 ID
	 * @return AniListCharactersNodesDto 캐릭터 정보
	 * @throws IOException 쿼리 파일 로딩 중 발생하는 예외
	 */
	AniListCharactersNodesDto getCharacter(Integer characterId) throws IOException;

}
