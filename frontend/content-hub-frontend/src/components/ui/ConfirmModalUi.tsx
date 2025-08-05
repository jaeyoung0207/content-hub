import Modal from "react-modal";
import { commonErrorHandler } from "../common/utils/commonUtil";

export type ConfirmModalUiProps = {
    isOpen: boolean,
    onOk: () => void,
    onCancel: () => void,
    confirmMsg: string,
}

/**
 * ConfirmModalUi 컴포넌트
 * 사용자에게 확인 메시지를 표시하고, OK 또는 Cancel 버튼을 클릭할 수 있는 모달 컴포넌트
 * @param isOpen 모달 열림 여부
 * @param onOk OK 버튼 클릭 핸들러
 * @param onCancel Cancel 버튼 클릭 핸들러
 * @param confirmMsg 확인 메시지
 */
export const ConfirmModalUi = ({ isOpen, onOk, onCancel, confirmMsg }: ConfirmModalUiProps) => {
    return (
        <>
            {/* 모달 컴포넌트 */}
            <Modal
                className="block w-100 h-50 shadow-xl bg-white rounded-xl p-4 mx-auto mt-120"
                isOpen={isOpen}
                ariaHideApp={false}
                style={{
                    overlay: {
                        display: 'fixed',
                        justifyContent: 'center',
                        alignItems: 'center',
                        backgroundColor: 'rgba(0, 0, 0, 0.5)',
                        zIndex: 1000,
                    },
                }}
            >
                <div className="mt-10">
                    <p className="flex justify-center text-gray-800">{confirmMsg}</p>
                    <div className="mt-8 flex justify-center gap-4">
                        {/* OK 버튼 */}
                        <button className="bg-blue-500 text-white px-4 py-1 rounded hover:bg-blue-600 cursor-pointer" onClick={commonErrorHandler(onOk)}>OK</button>
                        {/* Cancel 버튼 */}
                        <button className="bg-gray-300 text-gray-800 px-4 py-1 rounded hover:bg-gray-400 cursor-pointer" onClick={commonErrorHandler(onCancel)}>Cancel</button>
                    </div>
                </div>
            </Modal >
        </>
    )
}