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
import {
  HIGHLIGHT_HOVER_COLOR,
  OVERFLOW_AUTO_STYLE,
} from '../common/constants/tailwindStyles';

/**
 * 만화 크레딧 표시 컴포넌트 props 타입
 */
type DisplayComicsCreditsPropsType = {
  contentMediaType: string;
  apiId: number;
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
 * @param apiId 만화 API ID
 * @param contentMediaType 콘텐츠 미디어 타입
 * @param creditsAllList 크레딧 전체 목록
 * @param creditsType 크레딧 타입 (캐릭터/제작진)
 * @param isOmit 생략 여부
 */
export const DisplayComicsCredits = ({
  apiId,
  contentMediaType,
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
      <div className="mt-5 mb-4 flex items-center justify-between">
        <div className="text-2xl font-bold sm:text-3xl">
          {isCharacter ? t('info.characters') : t('info.crew')}
        </div>
        {isOmit && creditsAllList!.length > settings.detailComicsCount && (
          <div className="text-lx">
            <Link
              to={detailUrlQuery({
                contentMediaType: contentMediaType,
                apiId: String(apiId),
                tabNo: tabNo,
              })}
              className={`${HIGHLIGHT_HOVER_COLOR}`}
            >
              {t('info.seeMore')} &gt;
            </Link>
          </div>
        )}
      </div>

      {/* 크레딧 목록 */}
      <div className="grid grid-cols-2 gap-3 md:grid-cols-3 lg:grid-cols-5">
        {creditsList &&
          creditsList.map((items, index) => {
            const creditsInfo = items?.node;
            const role = items?.role;
            return (
              <div key={items?.id + '_' + index}>
                {creditsInfo && (
                  <Link
                    key={items?.id + '_' + index}
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
                    aria-label={`${creditsInfo.name?.full ?? ''} ${role ?? ''}`}
                    className={`${HIGHLIGHT_HOVER_COLOR} block cursor-pointer`}
                    onClick={() =>
                      isCharacter
                        ? checkCharacterId(creditsInfo.id)
                        : checkStaffId(creditsInfo.id)
                    }
                  >
                    <div
                      className={`flex h-[105px] w-full items-center rounded-2xl border border-black/5 p-2 sm:p-3 md:h-[140px]`}
                    >
                      {/* 이미지 */}
                      <div className="relative flex h-full max-w-[35%] shrink-0 basis-[35%] items-center justify-center">
                        <LazyImage
                          src={
                            creditsInfo.image?.medium ?? COMMON_IMAGES.NO_IMAGE
                          }
                          alt={creditsInfo.name?.full || 'Profile'}
                          className="h-full rounded-2xl object-cover lg:max-h-[105px] lg:w-auto"
                        />
                      </div>
                      {/* 이름 & 역할 */}
                      <div
                        className={`ml-3 flex h-full max-w-[65%] basis-[65%] flex-col`}
                      >
                        <div
                          className={`grow rounded-md bg-white/0 p-1 text-sm md:text-base ${OVERFLOW_AUTO_STYLE}`}
                        >
                          <div>
                            <div className="font-medium">
                              {creditsInfo.name?.full}
                            </div>
                            {role && (
                              <div className="mt-1 text-sm text-gray-600">
                                {role}
                              </div>
                            )}
                          </div>
                        </div>
                      </div>
                    </div>
                  </Link>
                )}
              </div>
            );
          })}
      </div>
    </div>
  );
};

export default DisplayComicsCredits;
