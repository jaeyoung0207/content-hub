import { toast } from 'react-toastify';
import i18n from 'i18next';

/**
 * contentId 체크 함수
 * @param contentId 콘텐츠 ID
 */
export const checkContentId = (contentId: number | undefined) => {
  if (!contentId) {
    console.error('no contentId');
    toast.error(i18n.t('warn.noContentInfo'), { toastId: 'noContentInfo' });
  }
};

/**
 * personId 체크 함수
 * @param personId 인물 ID
 */
export const checkPersonId = (personId: number | undefined) => {
  if (!personId) {
    console.error('no personId');
    toast.error(i18n.t('warn.noPersonInfo'), { toastId: 'noPersonInfo' });
  }
};

/**
 * characterId 체크 함수
 * @param characterId 캐릭터 ID
 */
export const checkCharacterId = (characterId: number | undefined) => {
  if (!characterId) {
    console.error('no characterId');
    toast.error(i18n.t('warn.noCharacterInfo'), { toastId: 'noCharacterInfo' });
  }
};

/**
 * staffId 체크 함수
 * @param staffId 제작진 ID
 */
export const checkStaffId = (staffId: number | undefined) => {
  if (!staffId) {
    console.error('no staffId');
    toast.error(i18n.t('warn.noStaffInfo'), { toastId: 'noStaffInfo' });
  }
};
