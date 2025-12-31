import { toast } from 'react-toastify';
import i18n from 'i18next';

/**
 * 체크 함수를 정의 하는 유틸 파일
 */

/**
 * apiId 체크 함수
 * @param apiId API ID
 */
export const checkApiId = (apiId: number | undefined) => {
  if (!apiId) {
    const errorMsg = i18n.t('warn.noApiIdInfo');
    console.error(errorMsg);
    toast.error(errorMsg, { toastId: 'noContentInfo' });
    return false;
  }
  return true;
};

/**
 * personId 체크 함수
 * @param personId 인물 ID
 */
export const checkPersonId = (personId: number | undefined) => {
  if (!personId) {
    const errorMsg = i18n.t('warn.noPersonIdInfo');
    console.error(errorMsg);
    toast.error(errorMsg, { toastId: 'noPersonIdInfo' });
    return false;
  }
  return true;
};

/**
 * characterId 체크 함수
 * @param characterId 캐릭터 ID
 */
export const checkCharacterId = (characterId: number | undefined) => {
  if (!characterId) {
    const errorMsg = i18n.t('warn.noCharacterIdInfo');
    console.error(errorMsg);
    toast.error(errorMsg, { toastId: 'noCharacterIdInfo' });
    return false;
  }
  return true;
};

/**
 * staffId 체크 함수
 * @param staffId 제작진 ID
 */
export const checkStaffId = (staffId: number | undefined) => {
  if (!staffId) {
    const errorMsg = i18n.t('warn.noStaffIdInfo');
    console.error(errorMsg);
    toast.error(errorMsg, { toastId: 'noStaffIdInfo' });
    return false;
  }
  return true;
};
