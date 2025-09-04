package com.cjy.contenthub.home.service.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 홈 랭킹 리스트 서비스 DTO
 */
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HomeRankingListServiceDto {
	
	/** 애니 랭킹 리스트 */
	List<HomeRankingServiceDto> aniRankingList;
	
	/** 드라마 랭킹 리스트 */
	List<HomeRankingServiceDto> dramaRankingList;
	
	/** 영화 랭킹 리스트 */
	List<HomeRankingServiceDto> movieRankingList;
	
	/** 만화 랭킹 리스트 */
	List<HomeRankingServiceDto> comicsRankingList;

}
