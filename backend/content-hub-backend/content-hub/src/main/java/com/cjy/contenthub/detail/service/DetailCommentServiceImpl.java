package com.cjy.contenthub.detail.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.cjy.contenthub.common.exception.CommonBusinessException;
import com.cjy.contenthub.common.repository.UserRepository;
import com.cjy.contenthub.common.repository.entity.UserEntity;
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
	public DetailCommentServiceDto getCommentList(String originalMediaType, String apiId, Integer page, String userId) {

		// 코멘트 서비스 DTO 생성
		DetailCommentServiceDto serviceDto = new DetailCommentServiceDto();
		// 페이지 번호 설정
		Integer commentPage = Optional.ofNullable(page).orElse(0);
		// 페이지 요청을 위한 Pageable 객체 생성
		Pageable pageble = PageRequest.of(commentPage, commentPerPage, Sort.by("createTime").descending());
		// 코멘트 엔티티 조회
		Page<DetailCommentViewEntity> commentEntityPage = commentViewRepository.findByOriginalMediaTypeAndApiId(originalMediaType, apiId, pageble);
		// 조회된 코멘트 엔티티를 리스트로 변환
		// 리스트의 변경이 가능하도록 stream().collect(Collectors.toList()) 사용
		List<DetailCommentViewEntity> commentEntityList = commentEntityPage.isEmpty() ? new ArrayList<>() : commentEntityPage.stream().collect(Collectors.toList()); 

		// 코멘트가 존재하는 경우
		if (commentEntityList.size() != 0) {
			// 유저ID가 존재하는 경우
			if (StringUtils.isNotEmpty(userId)) {
				// 코멘트 리스트에서 유저ID에 해당하는 코멘트를 추출
				List<DetailCommentViewEntity> myCommentEntityList = commentEntityList.stream()
						.filter(e -> StringUtils.equals(e.getUserId(), userId))
						.collect(Collectors.toList());
				// 첫번째 페이지의 경우
				if (commentPage.equals(0)) {
					// 유저코멘트가 존재하는 경우
					if (!CollectionUtils.isEmpty(myCommentEntityList)) {
						// 기존 코멘트 리스트에서 유저코멘트만 지움
						commentEntityList.removeIf(e -> StringUtils.equals(e.getUserId(), userId));
						// 유저 코멘트를 리스트의 첫번째 요소에 삽입
						commentEntityList.add(0, myCommentEntityList.getFirst()); 
					}
					// 유저코멘트가 존재하지 않는 경우
					else {
						// 유저 코멘트 조회
						DetailCommentViewEntity myCommentEntity = commentViewRepository.findByOriginalMediaTypeAndApiIdAndUserId(originalMediaType, apiId, userId);
						// 유저 코멘트가 존재하는 경우
						if (ObjectUtils.isNotEmpty(myCommentEntity)) {
							// 리스트 첫번째 요소에 삽입
							commentEntityList.add(0, myCommentEntity);
						}
					}
				}
				// 첫번째 페이지 이외의 경우
				else {
					// 유저코멘트가 존재하는 경우
					if (!CollectionUtils.isEmpty(myCommentEntityList)) {
						// 기존 코멘트리스트에서 유저코멘트 지움
						commentEntityList.removeIf(e -> StringUtils.equals(e.getUserId(), userId));
					}
				}
			}
		}
		
		// 서비스 DTO 리스트 생성
		List<DetailCommentDataServiceDto> commentDatServiceDtoList = 
				commentEntityList.size() == 0 ? new ArrayList<>() 
						: mapper.commentEntityListToCommentServiceList(commentEntityList);
		// 서비스 DTO에 반환값 설정
		serviceDto.setDataList(commentDatServiceDtoList);
		serviceDto.setTotalElements(commentEntityPage.getTotalElements());
		// 서비스 DTO 반환
		return serviceDto;

	}

	/**
	 * 특정 원본 미디어 타입과 API ID에 대한 별점 평균 조회
	 * 
	 * @param originalMediaType 원본 미디어 타입
	 * @param apiId API ID
	 * @return 별점 평균
	 */
	@Override
	public BigDecimal getStarRatingAverage(String originalMediaType, String apiId) {
		
		// 별점 평균 조회
		return commentViewRepository.getStarRatingAverage(originalMediaType, apiId);
		
	}

}
