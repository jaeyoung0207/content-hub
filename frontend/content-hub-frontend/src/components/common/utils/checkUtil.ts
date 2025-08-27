import { toast } from 'react-toastify';
import i18n from 'i18next';

/**
 * contentId 체크 함수
 * @param contentId 콘텐츠 ID
 */
export const checkContentId = (contentId: number | undefined) => {
  if (!contentId) {
    console.error('no contentId');
    toast.error(i18n.t('warn.noContentId'), { toastId: 'noContentId' });
  }
};

/**
 * personId 체크 함수
 * @param personId 인물 ID
 */
export const checkPersonId = (personId: number | undefined) => {
  if (!personId) {
    console.error('no personId');
    toast.error(i18n.t('warn.noPersonId'), { toastId: 'noPersonId' });
  }
};

/**
 * characterId 체크 함수
 * @param characterId 캐릭터 ID
 */
export const checkCharacterId = (characterId: number | undefined) => {
  if (!characterId) {
    console.error('no characterId');
    toast.error(i18n.t('warn.noCharacterId'), { toastId: 'noCharacterId' });
  }
};

/**
 * staffId 체크 함수
 * @param staffId 제작진 ID
 */
export const checkStaffId = (staffId: number | undefined) => {
  if (!staffId) {
    console.error('no staffId');
    toast.error(i18n.t('warn.noStaffId'), { toastId: 'noStaffId' });
  }
};
