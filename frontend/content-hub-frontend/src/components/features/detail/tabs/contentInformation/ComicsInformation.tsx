import { useTranslation } from 'react-i18next';
import DOMPurify from 'dompurify';
import {
  COMICS_CREDITS_TYPE,
  COMMON_IMAGES,
} from '@/components/common/constants/constants';
import { DetailResponseType } from '../../useDetail';
import {
  characterUrlQuery,
  checkCharacterId,
  checkStaffId,
  isDetailComicsType,
} from '@/components/common/utils/commonUtil';
import { Link } from 'react-router-dom';

/**
 * 만화 정보 컴포넌트 props 타입
 */
type ComicsInfomationPropsType = {
  detailResult: DetailResponseType;
  originalMediaType: string;
};

/**
 * 만화 크레딧 표시 컴포넌트 props 타입
 */
type DisplayComicsCreditsPropsType = ComicsInfomationPropsType & {
  creditsType?: String;
  isOmit?: boolean;
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

  // const { data, isFetchingNextPage, hasNextPage, setObserveTarget } =
  //   useComicsInformation(detailResult, originalMediaType);

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

      {detailResult &&
        isDetailComicsType(detailResult, originalMediaType) &&
        detailResult.characters &&
        detailResult.characters.edges &&
        detailResult.characters.edges.length > 0 && (
          <>
            {/* 캐릭터 */}
            <DisplayComicsCredits
              detailResult={detailResult}
              originalMediaType={originalMediaType}
              creditsType={COMICS_CREDITS_TYPE.CHARACTER}
              isOmit={true}
            />
          </>
        )}

      {detailResult &&
        isDetailComicsType(detailResult, originalMediaType) &&
        detailResult.staff &&
        detailResult.staff.edges &&
        detailResult.staff.edges.length > 0 && (
          <>
            {/* 제작진 */}
            <DisplayComicsCredits
              detailResult={detailResult}
              originalMediaType={originalMediaType}
              creditsType={COMICS_CREDITS_TYPE.STAFF}
              isOmit={true}
            />
          </>
        )}
    </div>
  );
};

/**
 * 만화 크레딧 표시 컴포넌트
 */
export const DisplayComicsCredits = ({
  detailResult,
  originalMediaType,
  creditsType,
  isOmit,
}: DisplayComicsCreditsPropsType) => {
  // i18n
  const { t } = useTranslation();
  // 크레딧 목록
  const creditsAll = isDetailComicsType(detailResult, originalMediaType)
    ? creditsType === COMICS_CREDITS_TYPE.CHARACTER
      ? detailResult.characters && detailResult.characters.edges
      : detailResult.staff && detailResult.staff.edges
    : [];
  const creditsList = isOmit
    ? creditsAll?.filter((_, index) => index < 10)
    : creditsAll;
  const isCharacter = creditsType === COMICS_CREDITS_TYPE.CHARACTER;
  return (
    <div className="mb-8">
      {/* 캐릭터 or 제작진 */}
      <div className="text-3xl font-bold mt-5 mb-5">
        {isCharacter ? t('info.characters') : t('info.crew')}
      </div>
      <div className="flex flex-wrap items-start mt-5">
        {creditsList &&
          creditsList.map((items, index) => {
            const creditsInfo = items?.node;
            const role = items.role;
            return (
              <>
                {creditsInfo && (
                  <div
                    key={index}
                    className="ml-1 mr-1 w-[230px] h-[140px]"
                    onClick={() =>
                      isCharacter
                        ? checkCharacterId(creditsInfo.id)
                        : checkStaffId(creditsInfo.id)
                    }
                  >
                    <Link
                      to={
                        creditsInfo.id
                          ? characterUrlQuery({
                              comicsCreditsType: isCharacter
                                ? COMICS_CREDITS_TYPE.CHARACTER
                                : COMICS_CREDITS_TYPE.STAFF,
                              creditsId: creditsInfo.id,
                            })
                          : '#'
                      }
                    >
                      <ul className="flex hover:font-bold w-full h-full">
                        {/* 이미지 */}
                        <li className="flex justify-center items-center max-w-[35%]">
                          <img
                            src={creditsInfo.image?.medium}
                            onError={(e) => {
                              e.currentTarget.src = COMMON_IMAGES.NO_IMAGE;
                            }}
                            alt={creditsInfo.name?.full}
                          />
                        </li>
                        {/* 이름 */}
                        <li className="flex items-center ml-4 mr-1 mb-4 text-sm max-w-[65%] break-words">
                          <div className="block">
                            <span className="mr-1">
                              {creditsInfo.name?.full}
                            </span>
                            <span className="flex items-center">{role}</span>
                          </div>
                        </li>
                      </ul>
                    </Link>
                  </div>
                )}
              </>
            );
          })}
      </div>
    </div>
  );
};
