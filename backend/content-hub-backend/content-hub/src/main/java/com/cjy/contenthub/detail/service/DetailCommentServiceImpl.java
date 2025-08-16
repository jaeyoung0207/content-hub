package com.cjy.contenthub.detail.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cjy.contenthub.common.exception.CommonBusinessException;
import com.cjy.contenthub.common.repository.UserRepository;
import com.cjy.contenthub.common.repository.entity.UserEntity;
import com.cjy.contenthub.detail.helper.DetailCommentHelper;
import com.cjy.contenthub.detail.mapper.DetailMapper;
import com.cjy.contenthub.detail.repository.DetailCommentRepository;
import com.cjy.contenthub.detail.repository.DetailCommentViewRepository;
import com.cjy.contenthub.detail.repository.entity.DetailCommentEntity;
import com.cjy.contenthub.detail.repository.entity.DetailCommentViewEntity;
import com.cjy.contenthub.detail.service.dto.DetailCommentDataServiceDto;
import com.cjy.contenthub.detail.service.dto.DetailCommentServiceDto;

import lombok.RequiredArgsConstructor;

/**
 * 상세 페이지 코멘트 관련 서비스 구현 클래스
 * 
 * @see DetailCommentService
 */
@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class DetailCommentServiceImpl implements DetailCommentService {
	
	/** 상세 페이지 헬퍼 클래스 */
	private final DetailCommentHelper helper;

	/** 코멘트 엔티티 리포지토리 */	
	private final DetailCommentRepository commentRepository;
	
	/** 코멘트 뷰 엔티티 리포지토리 */
	private final DetailCommentViewRepository commentViewRepository;
	
	/** 유저 엔티티 리포지토리 */
	private final UserRepository userRepository;

	/** 상세 페이지 매퍼 */
	private final DetailMapper mapper;
	
	/** 페이지당 코멘트 수 */
	@Value("${app.comment.perPage}")
	private int commentPerPage;

	/**
	 * 코멘트 등록
	 * 
	 * @param commentDto 상세 코멘트 데이터 서비스 DTO
	 * @return boolean 등록 성공 여부
	 */
	@Override
	@CacheEvict(value = "commentList", allEntries = true)
	public boolean saveComment(DetailCommentDataServiceDto commentDto) {

		// 서비스 DTO를 엔티티로 변환
		DetailCommentEntity commentEntity = mapper.commentServiceToCommentEntity(commentDto);
		
		// 유저 엔티티 조회
		UserEntity userEntity = userRepository.findByProviderAndProviderId(commentDto.getProvider(), commentDto.getUserId());
		// 유저 엔티티가 존재하지 않는 경우 예외 처리
		if (ObjectUtils.isEmpty(userEntity)) {
			throw new CommonBusinessException("유저 정보가 존재하지 않습니다.");
		}
		// 유저 시퀀스 설정
		commentEntity.setUserSeq(userEntity.getSeq());

		// 테이블에 등록
		DetailCommentEntity saveResultEntity = commentRepository.save(commentEntity);
		
		// 등록 결과가 비어있지 않으면 true 반환
		return ObjectUtils.isNotEmpty(saveResultEntity);

	}
	
	/**
	 * 코멘트 갱신
	 * 
	 * @param commentDto 상세 코멘트 데이터 서비스 DTO
	 * @return boolean 갱신 성공 여부
	 */
	@Override
	@CacheEvict(value = "commentList", allEntries = true)
	public boolean updateComment(DetailCommentDataServiceDto commentDto) {
		
		// 서비스 DTO를 엔티티로 변환
		DetailCommentEntity commentEntity = mapper.commentServiceToCommentEntity(commentDto);

		// 코멘트 엔티티를 조회
		Optional<DetailCommentEntity> selectedEntity = commentRepository.findById(commentEntity.getCommentNo());
		// 코멘트가 존재하지 않는 경우 예외 처리
		if (!selectedEntity.isPresent()) {
			throw new CommonBusinessException("코멘트가 존재하지 않습니다.");
		}
		// 코멘트 및 별점 설정
		selectedEntity.get().setCommentAndStarRating(commentDto.getComment(), commentDto.getStarRating());
		
		// 테이블에 등록(갱신)
		DetailCommentEntity updateResultEntity = commentRepository.save(selectedEntity.get());

		// 등록 결과가 비어있지 않으면 true 반환
		return ObjectUtils.isNotEmpty(updateResultEntity);
		
	}
	
	/**
	 * 코멘트 삭제
	 * 
	 * @param commentNo 코멘트 번호
	 * @return boolean 삭제 성공 여부
	 */
	@Override
	@CacheEvict(value = "commentList", allEntries = true)
	public boolean deleteComment(Long commentNo) {
		
		// 해당 코멘트 삭제
		commentRepository.deleteById(commentNo);
		
		// 처리 성공 여부 반환
		return true;
	}

	/**
	 * 코멘트 목록 조회
	 * 
	 * @param originalMediaType 원본 미디어 타입
	 * @param apiId API ID
	 * @param page 페이지 번호
	 * @param userId 유저 ID
	 * @return 상세 코멘트 서비스 DTO
	 */
	@Override
	@Cacheable(value = "commentList", key = "#originalMediaType + '_' + #apiId + '_' + #page + '_' + #userId", unless = "#result == null")
	public DetailCommentServiceDto getCommentList(String originalMediaType, String apiId, Integer page, String userId) {

		// 페이지 번호 설정
		Integer commentPage = Optional.ofNullable(page).orElse(0);
		// 페이지 요청을 위한 Pageable 객체 생성
		Pageable pageble = PageRequest.of(commentPage, commentPerPage, Sort.by("createTime").descending());
		// 코멘트 엔티티 조회
		Page<DetailCommentViewEntity> commentEntityPage = commentViewRepository.findByOriginalMediaTypeAndApiId(originalMediaType, apiId, pageble);
		
		// 조회된 코멘트 엔티티 리스트 생성
		List<DetailCommentViewEntity> commentList = new ArrayList<>(commentEntityPage.getContent());

		// 코멘트 & 유저ID가 존재하는 경우
		if (!commentList.isEmpty() && StringUtils.isNotEmpty(userId)) {
			// 각 페이지당 코멘트 리스트 처리
			helper.getCommentListPerPage(commentList, originalMediaType, apiId, commentPage, userId);
		}
		
		// 서비스 DTO 리스트 생성
		List<DetailCommentDataServiceDto> commentDatServiceDtoList = 
				commentList.isEmpty() ? new ArrayList<>() 
						: mapper.commentEntityListToCommentServiceList(commentList);
		// 서비스 DTO 반환
		return DetailCommentServiceDto.builder()
				.dataList(commentDatServiceDtoList)
				.totalElements(commentEntityPage.getTotalElements())
				.build();

	}

	/**
	 * 특정 원본 미디어 타입과 API ID에 대한 별점 평균 조회
	 * 
	 * @param originalMediaType 원본 미디어 타입
	 * @param apiId API ID
	 * @return 별점 평균
	 */
	@Override
	@Cacheable(value = "starRatingAverage", key = "#originalMediaType + '_' + #apiId")
	public BigDecimal getStarRatingAverage(String originalMediaType, String apiId) {
		
		// 별점 평균 조회
		return commentViewRepository.getStarRatingAverage(originalMediaType, apiId);
		
	}

}
