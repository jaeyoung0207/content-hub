import { cn } from '@/lib/cn';

/**
 * Modal 컴포넌트 Props 타입
 */
interface ModalUiProps {
  open: boolean;
  onClose: () => void;
  title?: string;
  children?: React.ReactNode;
}

/**
 * Modal 컴포넌트
 * 중앙에 표시되는 대화상자 UI
 * @param open 모달 열림 상태
 * @param onClose 모달 닫기 핸들러
 * @param title 모달 제목
 * @param children 모달 내부 콘텐츠
 */
export const ModalUi = ({ open, onClose, title, children }: ModalUiProps) => {
  if (!open) return null;
  return (
    <div className="fixed inset-0 z-50">
      <div
        className="absolute inset-0 bg-black/40"
        onClick={onClose}
        aria-hidden
      />
      <div className="absolute inset-0 flex items-start justify-center pt-80">
        <div className={cn('w-full max-w-lg rounded-lg bg-white shadow-xl')}>
          {title && (
            <div className="flex items-center justify-between border-b border-black/5 p-4">
              <h3 className="text-lg font-semibold">{title}</h3>
              <button
                className="rounded p-1 hover:bg-black/5"
                onClick={onClose}
                aria-label="닫기"
              >
                ✕
              </button>
            </div>
          )}
          <div className="p-4">{children}</div>
        </div>
      </div>
    </div>
  );
};
