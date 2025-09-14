package com.cjy.contenthub.home.controller.dto;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** 
 * 홈 랭킹 리스트 응답 DTO 
 */
@Setter
@Getter
@NoArgsConstructor
public class HomeRankingListResponseDto {
	
	/** 애니 랭킹 리스트 */
	List<HomeRankingReponseDto> aniRankingList;
	
	/** 드라마 랭킹 리스트 */
	List<HomeRankingReponseDto> dramaRankingList;
	
	/** 영화 랭킹 리스트 */
	List<HomeRankingReponseDto> movieRankingList;
	
	/** 만화 랭킹 리스트 */
	List<HomeRankingReponseDto> comicsRankingList;

}
