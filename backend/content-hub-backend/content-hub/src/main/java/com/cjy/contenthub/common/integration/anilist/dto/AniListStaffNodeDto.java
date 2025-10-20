package com.cjy.contenthub.common.integration.anilist.dto;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * AniList API 스태프 노드 DTO
 */
@Setter
@Getter
@NoArgsConstructor
public class AniListStaffNodeDto {
	
	/** 스태프 ID */
	private int id;
	
	/** 나이 */
	private int age;
	
	/** 성별 */
	private String gender;
	
	/** 혈액형 */
	private String bloodType;
	
	/** 생일 */
	private AniListDateDto dateOfBirth;
	
	/** 사망일 */
	private AniListDateDto dateOfDeath;
	
	/** 출생지 */
	private String homeTown;
	
	/** 이름 */
	private AniListNameDto name;
	
	/** 이미지 */
	private AniListCoverImageDto image;
	
	/** 웹사이트 */
	private String siteUrl;
	
	/** 설명 */
	private String description;
	
	/** 주요 직무 */
	private List<String> primaryOccupations;
	
	/** 활동 연도 */
	private List<Integer> yearsActive;

}
