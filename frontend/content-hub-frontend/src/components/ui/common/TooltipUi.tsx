/**
 * 툴팁 컴포넌트 Props 타입
 */
type TooltipUiProps = {
  text: string;
  style: string;
};

/**
 * 툴팁 컴포넌트
 */
export const TooltipUi = ({ text, style }: TooltipUiProps) => {
  return (
    <div
      className={`absolute flex justify-center items-center bg-white border-1 border-black rounded shadow-2xl z-50 p-2 text-black text-sm ${style}`}
    >
      {text}
    </div>
  );
};

export default TooltipUi;
