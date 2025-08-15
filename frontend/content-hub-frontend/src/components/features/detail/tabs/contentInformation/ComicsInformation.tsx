import { useTranslation } from 'react-i18next';
import DOMPurify from 'dompurify';
import { COMMON_IMAGES } from '@/components/common/constants/constants';
import { DetailResponseType } from '../../useDetail';
import {
  characterUrlQuery,
  checkCharacterId,
} from '@/components/common/utils/commonUtil';
import { Link } from 'react-router-dom';
import { useComicsInformation } from './useComicsInformation';
import { LoadingUi } from '@/components/ui/LoadingUi';
import { NodataMessageUi } from '@/components/ui/common/NodataMessageUi';

/**
 * 만화 정보 컴포넌트 props 타입
 */
type ComicsInfomationPropsType = {
  detailResult: DetailResponseType;
  originalMediaType: string;
};

/**
 * 만화 정보 컴포넌트
 * @param detailResult 상세 정보 결과
 * @param originalMediaType 원본 미디어 타입
 */
export const ComicsInfomation = ({
  detailResult,
  originalMediaType,
}: ComicsInfomationPropsType) => {
  // i18n 번역 훅
  const { t } = useTranslation();

  const { data, isFetchingNextPage, hasNextPage, setObserveTarget } =
    useComicsInformation(detailResult, originalMediaType);

  // 개요 변수 선언
  // DOMPurify를 사용하여 XSS 공격을 방지하며 HTML로 처리
  const overview =
    detailResult.overview && DOMPurify.sanitize(detailResult.overview);

  return (
    <div className="ml-5 mr-5">
      {overview && (
        <>
          {/* 개요 */}
          <div className="text-3xl font-bold mb-5">{t('info.overview')}</div>
          <div className="mb-10">
            <div dangerouslySetInnerHTML={{ __html: overview }}></div>
          </div>
        </>
      )}

      {data ? (
        <>
          {/* 등장인물 */}
          <div className="text-3xl font-bold mt-5 mb-5">
            {t('info.characters')}
          </div>
          <div className="flex flex-wrap items-start mt-5">
            {data.pages.length !== 0 &&
              data.pages.flat().map((items, index) => {
                return (
                  <>
                    {items && (
                      <div
                        key={index}
                        className="ml-1 mr-1 w-[110px]"
                        onClick={() => checkCharacterId(items.id)}
                      >
                        <Link
                          to={
                            items.id
                              ? characterUrlQuery({ characterId: items.id })
                              : '#'
                          }
                        >
                          <ul className="block hover:font-bold">
                            {/* 캐릭터 이미지 */}
                            <li className="flex justify-center items-center w-full h-[180px]">
                              <img
                                src={items.image?.medium}
                                onError={(e) => {
                                  e.currentTarget.src = COMMON_IMAGES.NO_IMAGE;
                                }}
                                alt={items.name?.full}
                              />
                            </li>
                            {/* 캐릭터 이름 */}
                            <li className="ml-1 mr-1 mb-4 text-lg">
                              {items.name?.full}
                            </li>
                          </ul>
                        </Link>
                      </div>
                    )}
                  </>
                );
              })}
          </div>
          {
            // 다음 페이지 로딩 중인 경우 로딩 UI 표시
            isFetchingNextPage && <LoadingUi />
          }
          {
            // 다음 페이지가 있는 경우 무한 스크롤을 위한 div 태그
            hasNextPage && (
              <div ref={(el) => setObserveTarget && setObserveTarget(el)}></div>
            )
          }
        </>
      ) : (
        <LoadingUi />
      )}
      {
        // 추천 콘텐츠 데이터가 없을 때 표시할 메시지
        data && data.pages[0]?.length === 0 && (
          <NodataMessageUi message={t('warn.noCharacterId')} />
        )
      }
    </div>
  );
};
