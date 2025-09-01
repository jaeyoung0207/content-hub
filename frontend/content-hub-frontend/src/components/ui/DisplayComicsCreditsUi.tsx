import {
  AniListCharactersEdgesDto,
  AniListStaffEdgesDto,
} from '@/api/data-contracts';
import { useTranslation } from 'react-i18next';
import { settings } from '../common/config/settings';
import {
  COMICS_CREDITS_TYPE,
  COMMON_IMAGES,
  DETAIL_TAB_ID,
} from '../common/constants/constants';
import { Link } from 'react-router-dom';
import { characterUrlQuery, detailUrlQuery } from '../common/utils/urlUtil';
import { checkCharacterId, checkStaffId } from '../common/utils/checkUtil';
import { LazyImage } from './common/LazyImageUi';

/**
 * 만화 크레딧 표시 컴포넌트 props 타입
 */
type DisplayComicsCreditsPropsType = {
  originalMediaType: string;
  contentId: number;
  creditsAllList: (
    | AniListCharactersEdgesDto
    | AniListStaffEdgesDto
    | undefined
  )[];
  creditsType?: string;
  isOmit?: boolean;
};

/**
 * 만화 크레딧 표시 컴포넌트
 */
export const DisplayComicsCredits = ({
  contentId,
  originalMediaType,
  creditsAllList,
  creditsType,
  isOmit,
}: DisplayComicsCreditsPropsType) => {
  // i18n
  const { t } = useTranslation();
  // 크레딧 목록 필터링
  const creditsList =
    creditsAllList &&
    (isOmit
      ? creditsAllList?.filter((_, index) => index < settings.detailComicsCount)
      : creditsAllList);
  // 캐릭터/제작진 구분
  const isCharacter = creditsType === COMICS_CREDITS_TYPE.CHARACTER;
  // 탭 번호
  const tabNo = isCharacter ? DETAIL_TAB_ID.cast : DETAIL_TAB_ID.crew;
  return (
    <div className="mb-8">
      {/* 캐릭터 or 제작진 */}
      <div className="flex justify-between mt-5 mb-5">
        <div className="text-3xl font-bold mt-5 mb-5">
          {isCharacter ? t('info.characters') : t('info.crew')}
        </div>
        <div className="text-lx">
          {isOmit && creditsAllList!.length > settings.detailComicsCount && (
            <Link
              to={detailUrlQuery({
                originalMediaType: originalMediaType,
                contentId: String(contentId),
                tabNo: tabNo,
              })}
              className={'ml-5 hover:font-bold'}
            >
              {t('info.seeMore') + ' >'}
            </Link>
          )}
        </div>
      </div>
      <div className="flex flex-wrap items-start mt-5">
        {creditsList &&
          creditsList.map((items, index) => {
            const creditsInfo = items?.node;
            const role = items?.role;
            return (
              <>
                {creditsInfo && (
                  <div
                    key={index}
                    className="ml-1 mr-1 w-[220px] h-[140px]"
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
                          <LazyImage
                            src={
                              creditsInfo.image?.medium ??
                              COMMON_IMAGES.NO_IMAGE
                            }
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

export default DisplayComicsCredits;
