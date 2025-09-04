package com.cjy.contenthub.home.service;

import com.cjy.contenthub.home.service.dto.HomeRankingListServiceDto;

/**
 * 홈 서비스 인터페이스
 */
public interface HomeService {

	/**
	 * 콘텐츠 랭킹 정보를 필터링하여 조회
	 * 
	 * @return 필터링된 콘텐츠 랭킹 정보 DTO
	 */
	HomeRankingListServiceDto getContentRankings();

}
