import { NavigateFunction } from 'react-router-dom';
import { checkApiId } from './checkUtil';
import { detailUrlQuery } from './urlUtil';

/**
 * 상세화면 이동 처리
 * @param navigate 네비게이트 함수
 * @param apiId API ID
 * @param contentMediaType 컨텐츠 미디어 타입
 * @param tabNo 탭 번호
 */
export const navigateToDetailPage = (
  navigate: NavigateFunction,
  apiId: number | undefined,
  contentMediaType: string,
  tabNo = 0
) => {
  // apiId 체크
  if (!checkApiId(apiId)) {
    return;
  }
  // 상세화면 URL 생성
  const detailUrl = detailUrlQuery({
    contentMediaType: contentMediaType,
    apiId: String(apiId),
    tabNo: tabNo,
  });
  // 상세화면 이동
  navigate(detailUrl);
};
