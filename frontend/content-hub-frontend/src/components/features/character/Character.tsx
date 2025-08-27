import { useParams } from 'react-router-dom';
import { useCharacter } from './useCharacter';
import { LoadingUi } from '@/components/ui/LoadingUi';
import { useTranslation } from 'react-i18next';
import { COMMON_IMAGES } from '@/components/common/constants/constants';
import { convertDate } from '@/components/common/utils/convertUtil';
import { isStaffType } from '@/components/common/utils/typeGuardUtil';

/**
 * 캐릭터 화면 컴포넌트
 */
export const Character = () => {
  // URL 파라미터에서 값을 가져오는 useParams 훅
  const { comicsCreditsType, creditsId } = useParams();
  // i18n 훅
  const { t } = useTranslation();

  // useCharacter 훅을 사용하여 캐릭터 정보 조회
  const { data, isLoading, isError } = useCharacter(
    comicsCreditsType!,
    creditsId!
  );

  // 캐릭터 정보 스타일
  const characterInfoStyle = 'flex text-xl mb-2 mr-3 break-all';
  // 소제목 스타일
  const subTitleStyle = 'mr-2 whitespace-nowrap';
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

  return (
    <div className="block mt-30 mb-10">
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
          <div className="flex justify-center m-5">
            {/* 캐릭터 이미지 */}
            <div className="flex justify-center items-center mb-4 w-[30%]">
              <img
                src={
                  data.image?.large ? data.image.large : COMMON_IMAGES.NO_IMAGE
                }
                onError={(e) => {
                  e.currentTarget.src = COMMON_IMAGES.NO_IMAGE;
                }}
                alt={data.name?.full}
              />
            </div>
            {/* 캐릭터 정보 */}
            <div className="w-[70%] block">
              {/* 이름 */}
              <div className="text-3xl mb-3 mr-3">{data.name?.full}</div>

              <ul className="mt-5">
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
            <div className="text-2xl mb-5 mr-3">
              <div className="text-3xl font-bold mb-3">
                {t('info.description')}
              </div>
              <div className="text-lg whitespace-pre-wrap ml-5">
                {data.description}
              </div>
            </div>
          )}
        </>
      )}
    </div>
  );
};
