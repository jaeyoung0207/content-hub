import { MEDIA_TYPE } from '../constants/constants';

/**
 * 미디어 타입에 따라 타입 문자열을 반환
 * @param mediaType 미디어 타입
 * @returns 타입 문자열
 */
export const detailMediaType = (mediaType: string) => {
  if (mediaType === MEDIA_TYPE.ANI) {
    return '1';
  } else if (mediaType === MEDIA_TYPE.DRAMA) {
    return '2';
  } else if (mediaType === MEDIA_TYPE.MOVIE) {
    return '3';
  } else if (mediaType === MEDIA_TYPE.COMICS) {
    return '4';
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
