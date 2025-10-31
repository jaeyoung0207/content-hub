/**
 * NoDataMessageUiProps 타입
 */
type NoDataMessageUiProps = {
  message?: string; // 메세지 텍스트
  description?: string; // 설명 텍스트
  action?: React.ReactNode; // 추가 액션 컴포넌트
};

/**
 * NoDataMessageUi 컴포넌트
 * 데이터가 없을 때 표시되는 안내 UI
 * @param message 메세지 텍스트
 * @param description 설명 텍스트
 * @param action 추가 액션 컴포넌트
 */
export const NoDataMessageUi = ({
  message = '데이터가 없습니다.',
  description,
  action,
}: NoDataMessageUiProps) => {
  return (
    <div className="text-muted-foreground flex flex-col items-center justify-center py-16 text-center">
      <div className="text-foreground mb-3 text-xl font-semibold">
        {message}
      </div>
      {description && <p className="mb-6 max-w-md">{description}</p>}
      {action}
    </div>
  );
};
