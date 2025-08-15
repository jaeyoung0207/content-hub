/**
 * 데이터가 없음을 표시하는 UI 컴포넌트 props 타입
 */
type NodataMessageUiPropsType = {
  message: string; // 표시할 메시지
};

/**
 * 데이터가 없음을 표시하는 UI 컴포넌트
 * @param message 표시할 메시지
 * @returns
 */
export const NodataMessageUi = ({ message }: NodataMessageUiPropsType) => {
  return (
    <>
      <div className="flex justify-center mt-25 text-3xl">{message}</div>
    </>
  );
};
