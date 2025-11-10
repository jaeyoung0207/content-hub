import { useParams } from 'react-router-dom';
import { useCharacter } from './useCharacter';
import { LoadingUi } from '@/components/ui/common/LoadingUi';
import { useTranslation } from 'react-i18next';
import { COMMON_IMAGES } from '@/components/common/constants/constants';
import { convertDate } from '@/components/common/utils/convertUtil';
import { isStaffType } from '@/components/common/utils/typeGuardUtil';
import { memo } from 'react';
import { LazyImage } from '@/components/ui/common/LazyImageUi';

/**
 * 캐릭터 화면 컴포넌트
 */
export const Character = memo(() => {
  // URL 파라미터에서 값을 가져오는 useParams 훅
  const { comicsCreditsType, creditsId } = useParams();
  // i18n 훅
  const { t } = useTranslation();

  // useCharacter 훅을 사용하여 캐릭터 정보 조회
  const { data, isLoading, isError, isSpoilerName, toggleSpoilerName } =
    useCharacter(comicsCreditsType!, creditsId!);

  // 캐릭터 정보 스타일
  const characterInfoStyle =
    'mb-2 flex break-all text-sm md:text-base lg:text-lg';
  // 소제목 스타일
  const subTitleStyle = 'mr-2 whitespace-nowrap font-medium text-foreground';
  // 생년월일
  const birthday =
    data && data.dateOfBirth
      ? convertDate(
          data.dateOfBirth.year,
          data.dateOfBirth.month,
          data.dateOfBirth.day
        )
      : '';
  // 사망일(Staff Only)
  const deathday =
    data && isStaffType(data) && data.dateOfDeath
      ? convertDate(
          data.dateOfDeath.year,
          data.dateOfDeath.month,
          data.dateOfDeath.day
        )
      : '';
  // 활동 시작 연도(Staff Only)
  const yearsActive =
    data && isStaffType(data) && data.yearsActive
      ? data.yearsActive.join(', ')
      : '';
  // 출생지(Staff Only)
  const homeTown =
    data && isStaffType(data) && data.homeTown ? data.homeTown : '';

  // 캐릭터 설명 (HTML 태그 변환)
  const characterDescription =
    data?.description &&
    data.description
      .replace(/:__/g, ':</b>') // 볼드 태그 변환
      .replace(/__/g, '<b>') // 볼드 태그 변환
      .replace(/~!/g, '<span class="text-white bg-gray-50">') // 회색 배경 텍스트 변환
      .replace(/!~/g, '</span>'); // 회색 배경 텍스트 변환

  return (
    <div className="px-2 pt-20 pb-10 md:px-4 md:pt-24 lg:px-8">
      {
        // 로딩 중이면 로딩 UI 표시, 에러가 발생하면 에러 메시지 표시
        isLoading ? (
          <LoadingUi />
        ) : (
          isError && <div className="mt-60 text-3xl">{t('warn.noData')}</div>
        )
      }
      {/* 캐릭터 정보 */}
      {data && (
        <>
          <div className="mt-2 grid grid-cols-1 gap-6 md:grid-cols-3">
            {/* 캐릭터 이미지 */}
            <div className="md:col-span-1">
              <div className="relative mx-auto aspect-[2/3] w-3/4 md:w-full lg:w-4/5 xl:w-2/3 2xl:w-1/2">
                <LazyImage
                  src={
                    data.image?.large
                      ? data.image.large
                      : COMMON_IMAGES.NO_IMAGE
                  }
                  alt={data.name?.full || 'Character'}
                  className="h-full w-full rounded-2xl object-cover"
                />
              </div>
            </div>
            {/* 캐릭터 정보 */}
            <div className="md:col-span-2">
              {/* 이름 */}
              <div className="mb-4 text-2xl font-bold sm:text-3xl">
                {data.name?.full}
              </div>
              <ul className="mt-2">
                {/* 다른 이름 */}
                {data.name?.alternative &&
                  data.name?.alternative?.length > 0 && (
                    <li className={characterInfoStyle}>
                      <div className={subTitleStyle}>
                        {t('info.alternativeName') + t('info.colon')}
                      </div>
                      <div>
                        {data.name.alternative.join(', ')}
                        {data.name.alternativeSpoiler &&
                          data.name.alternativeSpoiler.length > 0 &&
                          (isSpoilerName ? (
                            <button
                              onClick={toggleSpoilerName}
                              className="ml-2 cursor-pointer text-blue-500 hover:underline"
                            >
                              {'(' +
                                data.name.alternativeSpoiler.join(', ') +
                                ')'}
                            </button>
                          ) : (
                            <>
                              <button
                                onClick={toggleSpoilerName}
                                className="ml-2 cursor-pointer text-blue-500 hover:underline"
                              >
                                {t('info.showSpoilerName')}
                              </button>
                            </>
                          ))}
                      </div>
                    </li>
                  )}

                {/* 성별 */}
                {data.gender && (
                  <li className={characterInfoStyle}>
                    <div className={subTitleStyle}>
                      {t('info.gender') + t('info.colon')}
                    </div>
                    <div>{data.gender}</div>
                  </li>
                )}
                {/* 생일 */}
                {birthday && (
                  <li className={characterInfoStyle}>
                    <div className={subTitleStyle}>
                      {t('info.birthday') + t('info.colon')}
                    </div>
                    <div>{birthday}</div>
                  </li>
                )}
                {/* 사망일 */}
                {deathday && (
                  <li className={characterInfoStyle}>
                    <div className={subTitleStyle}>
                      {t('info.deathday') + t('info.colon')}
                    </div>
                    <div>{deathday}</div>
                  </li>
                )}
                {/* 나이 */}
                {data.age && (
                  <li className={characterInfoStyle}>
                    <div className={subTitleStyle}>
                      {t('info.age') + t('info.colon')}
                    </div>
                    <div>{data.age}</div>
                  </li>
                )}
                {/* 혈액형 */}
                {data.bloodType && (
                  <li className={characterInfoStyle}>
                    <div className={subTitleStyle}>
                      {t('info.bloodType') + t('info.colon')}
                    </div>
                    <div>{data.bloodType}</div>
                  </li>
                )}
                {/* 출생지 */}
                {homeTown && (
                  <li className={characterInfoStyle}>
                    <div className={subTitleStyle}>
                      {t('info.homeTown') + t('info.colon')}
                    </div>
                    <div>{homeTown}</div>
                  </li>
                )}
                {/* 활동 시작 연도 */}
                {yearsActive && (
                  <li className={characterInfoStyle}>
                    <div className={subTitleStyle}>
                      {t('info.yearsActive') + t('info.colon')}
                    </div>
                    <div>{yearsActive}</div>
                  </li>
                )}
                {/* 참고URL */}
                {data.siteUrl && (
                  <li className={characterInfoStyle}>
                    <div className={subTitleStyle}>
                      {t('info.reference') + t('info.colon')}
                    </div>
                    <a
                      href={data.siteUrl}
                      target="_blank"
                      rel="noopener noreferrer"
                      className="text-blue-500 hover:underline"
                    >
                      {data.siteUrl}
                    </a>
                  </li>
                )}
              </ul>
            </div>
          </div>
          {/* 캐릭터 설명 */}
          {data.description && (
            <section className="mt-8">
              <h2 className="mb-3 text-2xl font-bold sm:text-3xl">
                {t('info.description')}
              </h2>
              <p
                className="text-base leading-relaxed whitespace-pre-wrap sm:text-lg"
                dangerouslySetInnerHTML={{ __html: characterDescription! }}
              />
            </section>
          )}
        </>
      )}
    </div>
  );
});

export default Character;
