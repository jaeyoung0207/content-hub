import { useTranslation } from 'react-i18next';
import {
  COMICS_CREDITS_TYPE,
  COMMON_IMAGES,
} from '@/components/common/constants/constants';
import { DetailResponseType } from '../../useDetail';
import { characterUrlQuery } from '@/components/common/utils/urlUtil';
import { Link } from 'react-router-dom';
import { useComicsCharacterInformation } from './useComicsCharacterInformation';
import { LoadingUi } from '@/components/ui/LoadingUi';
import { NodataMessageUi } from '@/components/ui/common/NodataMessageUi';
import { checkCharacterId } from '@/components/common/utils/checkUtil';

/**
 * 만화 정보 컴포넌트 props 타입
 */
type ComicsCharacterInformationPropsType = {
  detailResult: DetailResponseType;
  originalMediaType: string;
  creditsType: string;
};

/**
 * 만화 정보 컴포넌트
 * @param detailResult 상세 정보 결과
 * @param originalMediaType 원본 미디어 타입
 */
export const ComicsCharacterInformation = ({
  detailResult,
  originalMediaType,
  creditsType,
}: ComicsCharacterInformationPropsType) => {
  // i18n 번역 훅
  const { t } = useTranslation();

  const { data, isFetchingNextPage, hasNextPage, setObserveTarget } =
    useComicsCharacterInformation(detailResult, originalMediaType, creditsType);

  return (
    <div className="ml-5 mr-5">
      {data ? (
        <>
          {/* 캐릭터 */}
          <div className="text-3xl font-bold mt-5 mb-5">
            {t('info.characters')}
          </div>
          <div className="flex flex-wrap items-start mt-5">
            {data.pages.length !== 0 &&
              data.pages.flat().map((items, index) => {
                const characterInfo = items?.node;
                return (
                  <>
                    {characterInfo && (
                      <div
                        key={index}
                        className="ml-1 mr-1 w-[110px]"
                        onClick={() => checkCharacterId(characterInfo.id)}
                      >
                        <Link
                          to={
                            characterInfo.id
                              ? characterUrlQuery({
                                  comicsCreditsType:
                                    COMICS_CREDITS_TYPE.CHARACTER,
                                  creditsId: characterInfo.id,
                                })
                              : '#'
                          }
                        >
                          <ul className="block hover:font-bold">
                            {/* 캐릭터 이미지 */}
                            <li className="flex justify-center items-center w-full h-[180px]">
                              <img
                                src={characterInfo.image?.medium}
                                onError={(e) => {
                                  e.currentTarget.src = COMMON_IMAGES.NO_IMAGE;
                                }}
                                alt={characterInfo.name?.full}
                              />
                            </li>
                            {/* 캐릭터 이름 */}
                            <li className="ml-1 mr-1 mb-4 text-lg">
                              {characterInfo.name?.full}
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
        // 데이터가 없을 때 표시할 메시지
        data && data.pages[0]?.length === 0 && (
          <NodataMessageUi message={t('warn.noCharacterId')} />
        )
      }
    </div>
  );
};
