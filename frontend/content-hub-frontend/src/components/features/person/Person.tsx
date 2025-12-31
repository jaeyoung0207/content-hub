import { Link, useParams } from 'react-router-dom';
import { usePerson } from './usePerson';
import { LoadingUi } from '@/components/ui/common/LoadingUi';
import { useTranslation } from 'react-i18next';
import {
  COMMON_IMAGES,
  TMDB_API_IMAGE_DOMAIN,
  WIDTH_185,
  WIDTH_300,
} from '@/components/common/constants/constants';
import { detailUrlQuery } from '@/components/common/utils/urlUtil';
import dayjs from 'dayjs';
import {
  PersonCreditsCastDto,
  PersonCreditsCrewDto,
} from '@/api/data-contracts';
import { memo } from 'react';
import { isPersonCreditsCastType } from '@/components/common/utils/typeGuardUtil';
import { LazyImage } from '@/components/ui/common/LazyImageUi';
import { NoDataMessageUi } from '@/components/ui/common';
import { isMobileOnly } from 'react-device-detect';

export type PersonCredits = PersonCreditsCastDto | PersonCreditsCrewDto;

type DisplayPersonCreditsType = {
  credits: PersonCredits[];
};

/**
 * 인물 화면 컴포넌트
 */
export const Person = memo(() => {
  // i18n 번역 훅
  const { t } = useTranslation();
  // URL 파라미터에서 personId 추출
  const { personId } = useParams<string>();

  // 인물 정보 가져오기 훅
  const { data, isLoading, isError } = usePerson(personId!);

  // 인물 정보 스타일
  const personInfoStyle = 'mb-2 flex break-all text-sm md:text-base lg:text-lg';
  // 소제목 스타일
  const subTitleStyle = 'mr-2 whitespace-nowrap font-medium text-foreground';

  let imagePath: string;
  if (data?.profilePath) {
    imagePath =
      TMDB_API_IMAGE_DOMAIN +
      (isMobileOnly ? WIDTH_185 : WIDTH_300) +
      data.profilePath;
  } else {
    imagePath = COMMON_IMAGES.NO_IMAGE;
  }

  return (
    <div className="px-2 pt-20 pb-10 md:px-4 md:pt-24 lg:px-8">
      {
        // 로딩 중이면 로딩 UI 표시, 에러가 발생하면 에러 메시지 표시
        isLoading ? (
          <LoadingUi />
        ) : (
          isError && <NoDataMessageUi message={t('warn.noData')} />
        )
      }
      {/* 인물 정보 */}
      {data && (
        <>
          <div className="mt-2 grid grid-cols-1 gap-6 md:grid-cols-3">
            {/* 인물 이미지 */}
            <div className="md:col-span-1">
              <div className="relative mx-auto aspect-[2/3] w-3/4 md:w-full lg:w-4/5 xl:w-2/3 2xl:w-1/2">
                <LazyImage
                  src={imagePath}
                  className="h-full w-full rounded-2xl object-cover"
                  alt={data.name}
                />
              </div>
            </div>
            {/* 인물 신상 정보 */}
            <div className="md:col-span-2">
              {/* 이름 */}
              <div className="mb-4 text-2xl font-bold sm:text-3xl">
                {data.name}
              </div>
              <ul className="mt-2">
                {/* 다른 이름 */}
                {data.alsoKnownAs && data.alsoKnownAs.length > 0 && (
                  <li className={personInfoStyle}>
                    <div className={subTitleStyle}>
                      {t('info.alsoKnownAs') + t('info.colon')}
                    </div>
                    <div>{data.alsoKnownAs.join(', ')}</div>
                  </li>
                )}
                {/* 성별 */}
                {data.genderValue && (
                  <li className={personInfoStyle}>
                    <div className={subTitleStyle}>
                      {t('info.gender') + t('info.colon')}
                    </div>
                    <div>{data.genderValue}</div>
                  </li>
                )}
                {/* 출생지 */}
                {data.placeOfBirth && (
                  <li className={personInfoStyle}>
                    <div className={subTitleStyle}>
                      {t('info.placeOfBirth') + t('info.colon')}
                    </div>
                    <div>{data.placeOfBirth}</div>
                  </li>
                )}
                {/* 생일 */}
                {data.birthday && (
                  <li className={personInfoStyle}>
                    <div className={subTitleStyle}>
                      {t('info.birthday') + t('info.colon')}
                    </div>
                    <div>{dayjs(data.birthday).format('YYYY년 MM월 DD일')}</div>
                  </li>
                )}
                {/* 사망일 */}
                {data.deathday && (
                  <li className={personInfoStyle}>
                    <div className={subTitleStyle}>
                      {t('info.deathday') + t('info.colon')}
                    </div>
                    <div>{dayjs(data.deathday).format('YYYY년 MM월 DD일')}</div>
                  </li>
                )}
                {/* 전문 분야 */}
                {data.knownForDepartment && (
                  <li className={personInfoStyle}>
                    <div className={subTitleStyle}>
                      {t('info.knownForDepartment') + t('info.colon')}
                    </div>
                    <div>
                      {data.knownForDepartment}
                      {data.adult && ' (' + t('info.adultActor') + ')'}
                    </div>
                  </li>
                )}
                {/* 출연작 수 */}
                {typeof data.castCount === 'number' && data.castCount > 0 && (
                  <li className={personInfoStyle}>
                    <div className={subTitleStyle}>
                      {t('info.castCount') + t('info.colon')}
                    </div>
                    <div>{data.castCount}</div>
                  </li>
                )}
                {/* 제작 참여작 수 */}
                {typeof data.crewCount === 'number' && data.crewCount > 0 && (
                  <li className={personInfoStyle}>
                    <div className={subTitleStyle}>
                      {t('info.crewCount') + t('info.colon')}
                    </div>
                    <div>{data.crewCount}</div>
                  </li>
                )}
                {/* 홈페이지 */}
                {data.homepage && (
                  <li className={personInfoStyle}>
                    <div className={subTitleStyle}>
                      {t('info.homepage') + t('info.colon')}
                    </div>
                    <div>
                      <a
                        href={data.homepage}
                        target="_blank"
                        rel="noopener noreferrer"
                        className="text-blue-500 hover:underline"
                      >
                        {data.homepage}
                      </a>
                    </div>
                  </li>
                )}
              </ul>
            </div>
          </div>
          {/* 약력 */}
          {data.biography && (
            <section className="mt-8">
              <h2 className="mb-3 text-2xl font-bold sm:text-3xl">
                {t('info.biography')}
              </h2>
              <p className="text-base leading-relaxed whitespace-pre-wrap sm:text-lg">
                {data.biography}
              </p>
            </section>
          )}
          {/* 출연작 */}
          {data.cast && data.cast.length !== 0 && (
            <section className="mt-8">
              <h2 className="mb-4 text-2xl font-bold sm:text-3xl">
                {t('info.singleCast')}
              </h2>
              <DisplayPersonCredits credits={data.cast} />
            </section>
          )}
          {/* 제작 참여작 */}
          {data.crew && data.crew.length !== 0 && (
            <section className="mt-8">
              <h2 className="mb-4 text-2xl font-bold sm:text-3xl">
                {t('info.singleCrew')}
              </h2>
              <DisplayPersonCredits credits={data.crew} />
            </section>
          )}
        </>
      )}
    </div>
  );
});

/**
 * 크레딧 정보를 표시하는 컴포넌트
 * @param credits - 크레딧 정보 배열
 */
const DisplayPersonCredits = memo(({ credits }: DisplayPersonCreditsType) => {
  // i18n 번역 훅
  const { t } = useTranslation();
  return (
    <div className="divide-y divide-black/5">
      {credits.map((items, index) => {
        // 캐스트인지 타입 확인
        const isCast = isPersonCreditsCastType(items);
        // 캐스트인 경우 캐릭터, 크루인 경우 작업 역할 표시
        const role = isCast ? items.character : items.job;
        // 연도 라벨(이전 항목과 동일하면 빈 문자열)
        const prevYear =
          index > 0 ? credits[index - 1]?.releaseYear : undefined;
        let yearLabel: string;
        if (items.releaseYear) {
          if (prevYear === items.releaseYear) {
            yearLabel = '';
          } else {
            yearLabel = items.releaseYear;
          }
        } else {
          yearLabel = t('info.unknown');
        }

        return (
          <div className="py-2" key={items.id + '_' + index}>
            <div className="grid grid-cols-[40px_60px_1fr] items-start gap-1 md:grid-cols-[60px_90px_1fr]">
              {/* 작업 연도 */}
              <div
                className={`${role ? 'row-span-2' : ''} flex justify-center pt-1 text-base text-gray-700 md:text-lg`}
              >
                {yearLabel}
              </div>
              {/* 미디어 타입 */}
              <div className="flex justify-center">
                <div className="pt-1 text-base text-gray-700 md:text-lg">
                  {items.contentMediaTypeName}
                </div>
              </div>
              {/* 작품 링크 */}
              <Link
                to={detailUrlQuery({
                  contentMediaType: items.contentMediaType,
                  apiId: String(items.id),
                  tabNo: 0,
                })}
                className="pl-1 text-base font-bold hover:underline sm:text-lg"
              >
                {items.title}
              </Link>
              {/* 역할 */}
              {role && (
                <div className="col-start-3 pl-1 text-sm text-gray-700">
                  {role}
                  {isCast && t('info.role')}
                </div>
              )}
            </div>
          </div>
        );
      })}
    </div>
  );
});

export default Person;
