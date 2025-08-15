import { useTranslation } from 'react-i18next';
import {
  MEDIA_TYPE,
  TMDB_API_IMAGE_DOMAIN,
  WIDTH_185,
  COMMON_IMAGES,
  SEPERATE_SLASH,
} from '@/components/common/constants/constants';
import { DetailResponseType } from '../../useDetail';
import {
  checkPersonId,
  isDetailMovieType,
  isDetailTvType,
  personUrlQuery,
} from '@/components/common/utils/commonUtil';
import { Link } from 'react-router-dom';

/**
 * 비디오 정보 컴포넌트 props 타입
 */
type VideoInformationPropsType = {
  detailResult: DetailResponseType;
  originalMediaType: string;
};

/**
 * 비디오 정보 컴포넌트
 * @param detailResult 상세 정보 결과
 * @param originalMediaType 원본 미디어 타입
 */
export const VideoInformation = ({
  detailResult,
  originalMediaType,
}: VideoInformationPropsType) => {
  // i18n 번역 훅
  const { t } = useTranslation();

  // 개요 변수 선언
  const overview = detailResult.overview;

  // 썸네일 이미지 경로
  const thumbnailImagePath = TMDB_API_IMAGE_DOMAIN + WIDTH_185;

  return (
    <div className="ml-5 mr-5">
      {overview && (
        <>
          {/* 개요 */}
          <div className="text-3xl font-bold mb-5">{t('info.overview')}</div>
          <div className="mb-10">
            {originalMediaType === MEDIA_TYPE.COMICS ? (
              <div dangerouslySetInnerHTML={{ __html: overview }}></div>
            ) : (
              overview
            )}
          </div>
        </>
      )}

      {
        // 상세 정보 결과의 타입이 TV 또는 MOVIE인 경우
        (isDetailTvType(detailResult, originalMediaType) ||
          isDetailMovieType(detailResult, originalMediaType)) && (
          <>
            {detailResult.credits &&
              detailResult.credits.cast &&
              detailResult.credits.cast.length !== 0 && (
                <>
                  {/* 출연진 */}
                  <div className="text-3xl font-bold mt-5 mb-5">
                    {t('info.cast')}
                  </div>
                  <div className="flex flex-wrap items-start mt-5">
                    {detailResult.credits.cast.map((items, index) => {
                      const character = isDetailTvType(
                        detailResult,
                        originalMediaType
                      )
                        ? items.roles
                            ?.map((role) => role.character)
                            .join(SEPERATE_SLASH)
                        : items.character;
                      return (
                        <div
                          key={index}
                          className="ml-1 mr-1 w-[190px]"
                          onClick={() => checkPersonId(items.id)}
                        >
                          <Link
                            to={
                              items.id
                                ? personUrlQuery({ personId: items.id })
                                : '#'
                            }
                          >
                            <ul className="block hover:font-bold">
                              <li className="flex justify-center items-center w-full h-[285px]">
                                <img
                                  src={thumbnailImagePath + items.profilePath}
                                  onError={(e) => {
                                    e.currentTarget.src =
                                      COMMON_IMAGES.NO_IMAGE;
                                  }}
                                  alt={items.name}
                                />
                              </li>
                              <li className="ml-1 mr-1 mb-4 text-lg">
                                {items.name! +
                                  (character &&
                                    '(' + character + t('info.role') + ')')}
                              </li>
                            </ul>
                          </Link>
                        </div>
                      );
                    })}
                  </div>
                </>
              )}
            {detailResult.credits &&
              detailResult.credits.crew &&
              detailResult.credits.crew.length !== 0 && (
                <>
                  {/* 제작진 */}
                  <div className="text-3xl font-bold mt-5 mb-5">
                    {t('info.crew')}
                  </div>
                  <div className="flex flex-wrap items-start mt-5">
                    {detailResult.credits.crew.map((items, index) => {
                      const job = isDetailTvType(
                        detailResult,
                        originalMediaType
                      )
                        ? items.jobs?.map((job) => job.job).join(SEPERATE_SLASH)
                        : items.job;
                      return (
                        <div
                          key={index}
                          className="ml-1 mr-1 w-[190px]"
                          onClick={() => checkPersonId(items.id)}
                        >
                          <Link
                            to={
                              items.id
                                ? personUrlQuery({ personId: items.id })
                                : '#'
                            }
                          >
                            <ul className="block hover:font-bold">
                              <li className="flex justify-center items-center w-full h-[285px]">
                                <img
                                  src={thumbnailImagePath + items.profilePath}
                                  onError={(e) => {
                                    e.currentTarget.src =
                                      COMMON_IMAGES.NO_IMAGE;
                                  }}
                                  alt={items.name}
                                />
                              </li>
                              <li className="ml-1 mr-1 mb-4 text-lg">
                                {items.name! + (job && '(' + job + ')')}
                              </li>
                            </ul>
                          </Link>
                        </div>
                      );
                    })}
                  </div>
                </>
              )}
          </>
        )
      }
    </div>
  );
};
