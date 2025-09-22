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
	private List<HomeRankingReponseDto> aniRankingList;
	
	/** 드라마 랭킹 리스트 */
	private List<HomeRankingReponseDto> dramaRankingList;
	
	/** 다큐 랭킹 리스트 */
	private List<HomeRankingReponseDto> documentaryRankingList;
	
	/** 키즈 랭킹 리스트 */
	private List<HomeRankingReponseDto> kidsRankingList;
	
	/** 뉴스 랭킹 리스트 */
	private List<HomeRankingReponseDto> newsRankingList;
	
	/** 예능 랭킹 리스트 */
	private List<HomeRankingReponseDto> varietyRankingList;
	
	/** 영화 랭킹 리스트 */
	private List<HomeRankingReponseDto> movieRankingList;
	
	/** 만화 랭킹 리스트 */
	private List<HomeRankingReponseDto> comicsRankingList;
	
}
