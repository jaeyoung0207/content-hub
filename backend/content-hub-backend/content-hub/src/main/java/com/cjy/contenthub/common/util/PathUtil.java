package com.cjy.contenthub.common.util;

import com.cjy.contenthub.common.constants.CommonConstants;

import lombok.NoArgsConstructor;

/**
 * 경로 관련 유틸리티 클래스
 */
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class PathUtil {

	/**
	 * 경로의 시작과 끝에 슬래시를 추가 또는 제거
	 * 
	 * @param path 경로 문자열
	 * @return 슬래시가 처리된 경로 문자열
	 */
	public static String checkSlashOfPath(String path) {
		if (path == null || path.isEmpty()) {
            return "";
		}
		if (CommonConstants.SLASH.equals(path)) {
            return path;
        }
		if (!path.startsWith(CommonConstants.SLASH)) {
			path = CommonConstants.SLASH.concat(path);
		}
		if (path.endsWith(CommonConstants.SLASH)) {
			path = path.substring(0, path.length() - 1);
		}
		return path;
	}
	
	/**
	 * 여러 경로 조각을 하나의 경로로 결합
	 * 
	 * @param paths 경로 조각들
	 * @return 결합된 전체 경로
	 */
	public static String joinPath(String... paths) {
        StringBuilder fullPath = new StringBuilder();
        for (String path : paths) {
            if (path == null || path.isEmpty()) {
                continue;
            }
            // 연속된 '/' 문자가 2개 이상인 경우 하나로 축소
            fullPath.append(checkSlashOfPath(path).replaceAll("/{2,}", "/"));
        }
        return fullPath.toString();
    }
	
}
