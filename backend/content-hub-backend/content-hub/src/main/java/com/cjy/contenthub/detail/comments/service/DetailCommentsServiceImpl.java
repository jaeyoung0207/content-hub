package com.cjy.contenthub.detail.comments.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cjy.contenthub.common.exception.CommonBusinessException;
import com.cjy.contenthub.common.util.MessageUtil;
import com.cjy.contenthub.core.constants.DomainEnum.DomainMessagesErrorEnum;
import com.cjy.contenthub.core.repository.UserRepository;
import com.cjy.contenthub.core.repository.entity.ContentEntity;
import com.cjy.contenthub.core.repository.entity.UserEntity;
import com.cjy.contenthub.core.shared.service.ContentSharedService;
import com.cjy.contenthub.detail.comments.helper.DetailCommentsHelper;
import com.cjy.contenthub.detail.comments.mapper.DetailCommentsMapper;
import com.cjy.contenthub.detail.comments.repository.DetailCommentsRepository;
import com.cjy.contenthub.detail.comments.repository.DetailCommentsViewRepository;
import com.cjy.contenthub.detail.comments.repository.entity.DetailCommentsEntity;
import com.cjy.contenthub.detail.comments.repository.entity.DetailCommentsViewEntity;
import com.cjy.contenthub.detail.comments.service.dto.DetailCommentsDataServiceDto;
import com.cjy.contenthub.detail.comments.service.dto.DetailCommentsServiceDto;

import lombok.RequiredArgsConstructor;

/**
 * 상세 코멘트 서비스 구현 클래스
 * 
 * @see DetailCommentService
 */
@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class DetailCommentsServiceImpl implements DetailCommentsService {
	
	/** 상세 페이지 헬퍼 클래스 */
	private final DetailCommentsHelper helper;

	/** 코멘트 엔티티 리포지토리 */	
	private final DetailCommentsRepository commentsRepository;
	
	/** 코멘트 뷰 엔티티 리포지토리 */
	private final DetailCommentsViewRepository commentsViewRepository;
	
	/** 유저 엔티티 리포지토리 */
	private final UserRepository userRepository;
	
	/** 상세 페이지 매퍼 */
	private final DetailCommentsMapper commentsMapper;
	
	/** 콘텐츠 공유 서비스 */
	private final ContentSharedService contentSharedService;
	
	/** 메시지 유틸 */
	private final MessageUtil messageUtil;
	
	/** 페이지당 코멘트 수 */
	@Value("${app.comment.per-page}")
	private int commentPerPage;

	/**
	 * 코멘트 등록
	 * 
	 * @param commentParam 상세 코멘트 데이터 서비스 DTO
	 * @return boolean 등록 성공 여부
	 */
	@Override
	public boolean saveComment(DetailCommentsDataServiceDto commentParam) {

		// 서비스 DTO를 엔티티로 변환
		DetailCommentsEntity comment = commentsMapper.commentsServiceToCommentsEntity(commentParam);
		
		// 유저 엔티티 조회
		UserEntity user = userRepository.findByProviderAndProviderId(commentParam.getProvider(), commentParam.getProviderId());
		// 유저 엔티티가 존재하지 않는 경우 예외 처리
		if (ObjectUtils.isEmpty(user)) {
			throw new CommonBusinessException(
					messageUtil.getMessageKO(DomainMessagesErrorEnum.ERROR_LOGIN_NOT_FOUND_USER.getMessageCode()));
		}
		// 유저 ID 설정
		comment.setUserEntity(user);
		
		// 콘텐츠 엔티티 조회
		ContentEntity content = contentSharedService.getContentEntity(
				commentParam.getContentMediaType(), commentParam.getApiId(),
				commentParam.getTitle(), commentParam.getThumbnailImageUrl(), commentParam.getGenreIds(), null);
		// 콘텐츠 ID 설정
		comment.setContentEntity(content);

		// 테이블에 등록
		DetailCommentsEntity saveResult = commentsRepository.save(comment);
		
		// 등록 결과가 비어있지 않으면 true 반환
		return ObjectUtils.isNotEmpty(saveResult);

	}
	
	/**
	 * 코멘트 갱신
	 * 
	 * @param commentParam 상세 코멘트 데이터 서비스 DTO
	 * @return boolean 갱신 성공 여부
	 */
	@Override
	public boolean updateComment(DetailCommentsDataServiceDto commentParam) {
		
		// 서비스 DTO를 엔티티로 변환
		DetailCommentsEntity comment = commentsMapper.commentsServiceToCommentsEntity(commentParam);

		// 코멘트 엔티티를 조회
		Optional<DetailCommentsEntity> selectedComment = commentsRepository.findById(comment.getCommentId());
		// 코멘트가 존재하지 않는 경우 예외 처리
		if (!selectedComment.isPresent()) {
			throw new CommonBusinessException(
					messageUtil.getMessageKO(DomainMessagesErrorEnum.ERROR_DETAIL_COMMENT_COMMENT_NOT_FOUND.getMessageCode()));
		}
		// 코멘트 및 별점 설정
		selectedComment.get().setCommentAndStarRating(commentParam.getComment(), commentParam.getStarRating());
		
		// 테이블에 등록(갱신)
		DetailCommentsEntity updateResultEntity = commentsRepository.save(selectedComment.get());

		// 등록 결과가 비어있지 않으면 true 반환
		return ObjectUtils.isNotEmpty(updateResultEntity);
		
	}
	
	/**
	 * 코멘트 삭제
	 * 
	 * @param commentId 코멘트 ID
	 * @return boolean 삭제 성공 여부
	 */
	@Override
	public boolean deleteComment(Long commentId) {
		
		// 해당 코멘트 삭제
		commentsRepository.deleteById(commentId);
		
		// 처리 성공 여부 반환
		return true;
	}

	/**
	 * 코멘트 목록 조회
	 * 
	 * @param contentMediaType 컨텐츠 미디어 타입
	 * @param apiId API ID
	 * @param page 페이지 번호
	 * @param providerId 유저 ID
	 * @return 상세 코멘트 서비스 DTO
	 */
	@Override
	public DetailCommentsServiceDto getCommentList(String contentMediaType, String apiId, Integer page, String providerId) {

		// 페이지 번호 설정
		Integer commentPage = Optional.ofNullable(page).orElse(0);
		// 페이지 요청을 위한 Pageable 객체 생성
		Pageable pageble = PageRequest.of(commentPage, commentPerPage, Sort.by("createTime").descending());
		// 코멘트 엔티티 조회
		Page<DetailCommentsViewEntity> commentEntityPage = commentsViewRepository.findByContentMediaTypeAndApiId(contentMediaType, apiId, pageble);
		
		// 조회된 코멘트 엔티티 리스트 생성
		List<DetailCommentsViewEntity> commentList = new ArrayList<>(commentEntityPage.getContent());

		// 코멘트 & 유저ID가 존재하는 경우
		if (!commentList.isEmpty() && StringUtils.isNotEmpty(providerId)) {
			// 각 페이지당 코멘트 리스트 처리
			helper.getCommentListPerPage(commentList, contentMediaType, apiId, commentPage, providerId);
		}
		
		// 서비스 DTO 리스트 생성
		List<DetailCommentsDataServiceDto> commentDataServiceDtoList = 
				commentList.isEmpty() ? new ArrayList<>() 
						: commentsMapper.commentEntityListToCommentsServiceList(commentList);
		// 서비스 DTO 반환
		return DetailCommentsServiceDto.builder()
				.dataList(commentDataServiceDtoList)
				.totalElements(commentEntityPage.getTotalElements())
				.build();
	}

	/**
	 * 특정 컨텐츠 미디어 타입과 API ID에 대한 별점 평균 조회
	 * 
	 * @param contentMediaType 컨텐츠 미디어 타입
	 * @param apiId API ID
	 * @return 별점 평균
	 */
	@Override
	public BigDecimal getStarRatingAverage(String contentMediaType, String apiId) {
		
		// 별점 평균 조회
		return commentsViewRepository.getStarRatingAverage(contentMediaType, apiId);
		
	}

}
