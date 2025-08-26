import {
  VIDEO_CREDITS_TYPE,
  MEDIA_TYPE,
  COMICS_CREDITS_TYPE,
} from '@/components/common/constants/constants';
import {
  DisplayVideoCredits,
  VideoInformationPropsType,
} from './contentInformation/VideoInformation';
import { ComicsCharacterInformation } from './contentInformation/ComicsCharacterInformation';

export const CrewInformation = ({
  detailResult,
  originalMediaType,
}: VideoInformationPropsType) => {
  return (
    <div className="ml-5 mr-5">
      {(originalMediaType === MEDIA_TYPE.ANI ||
        originalMediaType === MEDIA_TYPE.DRAMA ||
        originalMediaType === MEDIA_TYPE.MOVIE) && (
        <DisplayVideoCredits
          detailResult={detailResult}
          originalMediaType={originalMediaType}
          creditsType={VIDEO_CREDITS_TYPE.CREW}
          isOmit={false}
        />
      )}
      {originalMediaType === MEDIA_TYPE.COMICS && (
        <ComicsCharacterInformation
          detailResult={detailResult}
          originalMediaType={originalMediaType}
          creditsType={COMICS_CREDITS_TYPE.STAFF}
        />
      )}
    </div>
  );
};
