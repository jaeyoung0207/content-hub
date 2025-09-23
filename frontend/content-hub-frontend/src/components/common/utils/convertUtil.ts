import { AxiosError } from 'axios';
import {
  useContentMediaTypeMapStore,
  useDisplayMediaTypeMapStore,
} from '../store/globalStateStore';
import { MEDIA_TYPE_KIND, MEDIA_TYPE_NAME } from '../constants/constants';

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
    if (mediaType == getDisplayMediaType().aniCode) {
      return getContentMediaType().aniCode;
    } else if (mediaType == getDisplayMediaType().dramaCode) {
      return getContentMediaType().dramaCode;
    } else if (mediaType == getDisplayMediaType().documentaryCode) {
      return getContentMediaType().documentaryCode;
    } else if (mediaType == getDisplayMediaType().kidsCode) {
      return getContentMediaType().kidsCode;
    } else if (mediaType == getDisplayMediaType().newsCode) {
      return getContentMediaType().newsCode;
    } else if (mediaType == getDisplayMediaType().varietyCode) {
      return getContentMediaType().varietyCode;
    } else if (mediaType == getDisplayMediaType().movieCode) {
      return getContentMediaType().movieCode;
    } else if (mediaType == getDisplayMediaType().personCode) {
      return getContentMediaType().personCode;
    } else if (mediaType == getDisplayMediaType().comicsCode) {
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
  if (displayMediaType == getDisplayMediaType().aniCode) {
    return MEDIA_TYPE_NAME.ANI;
  } else if (displayMediaType == getDisplayMediaType().dramaCode) {
    return MEDIA_TYPE_NAME.DRAMA;
  } else if (displayMediaType == getDisplayMediaType().documentaryCode) {
    return MEDIA_TYPE_NAME.DOCUMENTARY;
  } else if (displayMediaType == getDisplayMediaType().kidsCode) {
    return MEDIA_TYPE_NAME.KIDS;
  } else if (displayMediaType == getDisplayMediaType().newsCode) {
    return MEDIA_TYPE_NAME.NEWS;
  } else if (displayMediaType == getDisplayMediaType().varietyCode) {
    return MEDIA_TYPE_NAME.VARIETY;
  } else if (displayMediaType == getDisplayMediaType().movieCode) {
    return MEDIA_TYPE_NAME.MOVIE;
  } else if (displayMediaType == getDisplayMediaType().personCode) {
    return MEDIA_TYPE_NAME.PERSON;
  } else if (displayMediaType == getDisplayMediaType().comicsCode) {
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
