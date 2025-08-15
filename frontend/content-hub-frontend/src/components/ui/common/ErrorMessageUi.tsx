/**
 * 에러 메시지를 표시하는 UI 컴포넌트 props 타입
 */
type ErrorMessageUiPropsType = {
  errorMsg: string; // 표시할 에러 메시지
};

/**
 * 에러 메시지를 표시하는 UI 컴포넌트
 * @param errorMsg 표시할 에러 메시지
 */
export const ErrorMessageUi = ({ errorMsg }: ErrorMessageUiPropsType) => {
  return <div className="text-lg text-red-500">{errorMsg}</div>;
};
