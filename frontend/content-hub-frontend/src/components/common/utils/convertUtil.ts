import { AxiosError } from 'axios';
import {
  useContentMediaTypeMapStore,
  useDisplayMediaTypeMapStore,
} from '../store/globalStateStore';
import {
  MEDIA_TYPE,
  MEDIA_TYPE_KIND,
  MEDIA_TYPE_NAME,
} from '../constants/constants';

/**
 * 값을 변환하는 함수를 정의하는 유틸 파일
 */

// type mediaTypeName = 'ANI' | 'DRAMA' | 'DOCUMENTARY' | 'KIDS' | 'NEWS' | 'VARIETY' | 'MOVIE' | 'COMICS' | 'PERSON';

// export const ORIGINAL_MEDIA_TYPE = (mediaTypeName: mediaTypeName) => {
//   const contentMediaTypeMap = useContentMediaTypeMapStore.getState().contentMediaTypeMap;
//   Object.keys(contentMediaTypeMap).forEach((key) => {
//     console.log(`Key: ${key}, Value: ${contentMediaTypeMap[key]}`);
//   });
//   return contentMediaTypeMap[mediaTypeName];
// }

/**
 * 컨텐츠 미디어 타입 값을 가져오는 함수
 * @returns 컨텐츠 미디어 타입 값
 */
export const getContentMediaType = () => {
  const contentMediaType =
    useContentMediaTypeMapStore.getState().contentMediaType;
  if (!contentMediaType) {
    const errorMsg = '컨텐츠 미디어 타입이 정의되지 않았습니다.';
    console.error(errorMsg);
    throw new AxiosError(errorMsg);
  }
  return contentMediaType;
};

/**
 * 화면 표시용 미디어 타입 값을 가져오는 함수
 * @returns 화면 표시용 미디어 타입 값
 */
export const getDisplayMediaType = () => {
  const displayMediaType =
    useDisplayMediaTypeMapStore.getState().displayMediaType;
  if (!displayMediaType) {
    const errorMsg = '화면 표시용 미디어 타입이 정의되지 않았습니다.';
    console.error(errorMsg);
    throw new AxiosError(errorMsg);
  }
  return displayMediaType;
};

/**
 * 화면 표시용 미디어 타입 <-> 컨텐츠 미디어 타입으로 매핑하는 함수
 * @param mediaType 미디어 타입
 * @param toMediaTypeKind 변환할 미디어 타입 종류 (DISPLAY_MEDIA_TYPE: 화면 표시용 미디어 타입, CONTENT_MEDIA_TYPE: 컨텐츠 미디어 타입)
 * @returns 컨텐츠 미디어 타입
 */
export const mappingToMediaType = (
  mediaType: string,
  toMediaTypeKind: string
) => {
  // 화면 표시용 미디어 타입인 경우 -> 컨텐츠 미디어 타입으로 변환
  if (toMediaTypeKind === MEDIA_TYPE_KIND.CONTENT_MEDIA_TYPE) {
    if (!mediaType || mediaType.length === 4) {
      console.error('toMediaTypeKind is wrong:', mediaType);
      return mediaType;
    }
    if (mediaType == MEDIA_TYPE.ANI) {
      return getContentMediaType().aniCode;
    } else if (mediaType == MEDIA_TYPE.DRAMA) {
      return getContentMediaType().dramaCode;
    } else if (mediaType == MEDIA_TYPE.DOCUMENTARY) {
      return getContentMediaType().documentaryCode;
    } else if (mediaType == MEDIA_TYPE.KIDS) {
      return getContentMediaType().kidsCode;
    } else if (mediaType == MEDIA_TYPE.NEWS) {
      return getContentMediaType().newsCode;
    } else if (mediaType == MEDIA_TYPE.VARIETY) {
      return getContentMediaType().varietyCode;
    } else if (mediaType == MEDIA_TYPE.MOVIE) {
      return getContentMediaType().movieCode;
    } else if (mediaType == MEDIA_TYPE.PERSON) {
      return getContentMediaType().personCode;
    } else if (mediaType == MEDIA_TYPE.COMICS) {
      return getContentMediaType().comicsCode;
    } else {
      return null;
    }
  }
  // 컨텐츠 미디어 타입인 경우 -> 화면 표시용 미디어 타입으로 변환
  else {
    if (!mediaType || mediaType.length !== 4) {
      console.error('toMediaTypeKind is wrong:', mediaType);
      return mediaType;
    }
    if (mediaType == getContentMediaType().aniCode) {
      return getDisplayMediaType().aniCode;
    } else if (mediaType == getContentMediaType().dramaCode) {
      return getDisplayMediaType().dramaCode;
    } else if (mediaType == getContentMediaType().documentaryCode) {
      return getDisplayMediaType().documentaryCode;
    } else if (mediaType == getContentMediaType().kidsCode) {
      return getDisplayMediaType().kidsCode;
    } else if (mediaType == getContentMediaType().newsCode) {
      return getDisplayMediaType().newsCode;
    } else if (mediaType == getContentMediaType().varietyCode) {
      return getDisplayMediaType().varietyCode;
    } else if (mediaType == getContentMediaType().movieCode) {
      return getDisplayMediaType().movieCode;
    } else if (mediaType == getContentMediaType().personCode) {
      return getDisplayMediaType().personCode;
    } else if (mediaType == getContentMediaType().comicsCode) {
      return getDisplayMediaType().comicsCode;
    } else {
      return null;
    }
  }
};

/**
 * 화면 표시용 미디어 타입을 미디어 타입 이름으로 매핑하는 함수
 * @param displayMediaType 화면 표시용 미디어 타입
 * @returns 미디어 타입 이름
 */
export const getDisplayMediaTypeName = (displayMediaType: string) => {
  if (displayMediaType == MEDIA_TYPE.ANI) {
    return MEDIA_TYPE_NAME.ANI;
  } else if (displayMediaType == MEDIA_TYPE.DRAMA) {
    return MEDIA_TYPE_NAME.DRAMA;
  } else if (displayMediaType == MEDIA_TYPE.DOCUMENTARY) {
    return MEDIA_TYPE_NAME.DOCUMENTARY;
  } else if (displayMediaType == MEDIA_TYPE.KIDS) {
    return MEDIA_TYPE_NAME.KIDS;
  } else if (displayMediaType == MEDIA_TYPE.NEWS) {
    return MEDIA_TYPE_NAME.NEWS;
  } else if (displayMediaType == MEDIA_TYPE.VARIETY) {
    return MEDIA_TYPE_NAME.VARIETY;
  } else if (displayMediaType == MEDIA_TYPE.MOVIE) {
    return MEDIA_TYPE_NAME.MOVIE;
  } else if (displayMediaType == MEDIA_TYPE.PERSON) {
    return MEDIA_TYPE_NAME.PERSON;
  } else if (displayMediaType == MEDIA_TYPE.COMICS) {
    return MEDIA_TYPE_NAME.COMICS;
  } else {
    return null;
  }
};

/**
 * 연월일을 변환하는 함수
 * @param year 연도
 * @param month 월
 * @param day 일
 * @returns 변환된 연월일 문자열
 */
export const convertDate = (
  year: number | undefined,
  month: number | undefined,
  day: number | undefined
) => {
  const convertedYear = year ? year.toString().concat('년 ') : '';
  const convertedMonth = month ? month.toString().concat('월 ') : '';
  const convertedDay = day ? day.toString().concat('일') : '';
  return convertedYear.concat(convertedMonth).concat(convertedDay).trim();
};
