import { commonErrorHandler } from '../common/utils/errorUtil';
import { ButtonUi, ModalUi } from './common';

export type ConfirmModalUiProps = {
  isOpen: boolean;
  onOk: () => void;
  onCancel: () => void;
  confirmMsg: string;
  title: string;
};

/**
 * ConfirmModalUi 컴포넌트
 * 사용자에게 확인 메시지를 표시하고, OK 또는 Cancel 버튼을 클릭할 수 있는 모달 컴포넌트
 * @param isOpen 모달 열림 여부
 * @param onOk OK 버튼 클릭 핸들러
 * @param onCancel Cancel 버튼 클릭 핸들러
 * @param confirmMsg 확인 메시지
 */
export const ConfirmModalUi = ({
  isOpen,
  onOk,
  onCancel,
  title,
  confirmMsg,
}: ConfirmModalUiProps) => {
  return (
    <>
      {/* 모달 컴포넌트 */}
      <ModalUi open={isOpen} onClose={onCancel} title={title}>
        <div>
          <div
            className="flex justify-center text-gray-800"
            style={{ whiteSpace: 'pre-line' }}
          >
            {confirmMsg}
          </div>
          <div className="mt-8 flex justify-center gap-4">
            {/* OK 버튼 */}
            <ButtonUi
              className="cursor-pointer rounded bg-blue-500 px-4 py-1 text-white hover:bg-blue-600"
              onClick={commonErrorHandler(onOk)}
            >
              OK
            </ButtonUi>
            {/* Cancel 버튼 */}
            <ButtonUi
              className="cursor-pointer rounded bg-gray-300 px-4 py-1 text-gray-800 hover:bg-gray-400"
              onClick={commonErrorHandler(onCancel)}
            >
              Cancel
            </ButtonUi>
          </div>
        </div>
      </ModalUi>
    </>
  );
};
