package com.cjy.contenthub.common.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cjy.contenthub.common.repository.ContentApiTbRepository;
import com.cjy.contenthub.common.repository.entity.ContentApiTbEntity;

import lombok.RequiredArgsConstructor;

/**
 * 공통 서비스 구현 클래스 
 */
@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class CommonServiceImpl implements CommonService {

	/** ContentApiTbRepository */
	private final ContentApiTbRepository repository;

	/**
	 * API 이름에 해당하는 API 키를 반환
	 * 
	 * @param apiName API 이름
	 * @return API 키
	 */
	@Override
	public String getApiKey(String apiName) {
		
		// API 이름에 해당하는 엔티티를 조회
		Optional<ContentApiTbEntity> apiEntity = repository.findById(apiName);
		
		// 엔티티가 존재하지 않으면 예외 처리
		if (!apiEntity.isPresent()) {
			throw new IllegalArgumentException("API 이름이 잘못되었거나 존재하지 않습니다: " + apiName);
		}
		
		// Optional에서 값을 가져오기 위해 get() 메소드 사용
		// 주의: get() 메소드는 Optional이 비어있을 때 NoSuchElementException을 발생시킬 수 있으므로
		// 항상 Optional이 비어있지 않은지 확인한 후 사용해야 함
		// 여기서는 이미 존재 여부를 확인했으므로 안전하게 get()을 호출함
		// API 키를 반환
		return apiEntity.get().getApiKey();
	}

}
