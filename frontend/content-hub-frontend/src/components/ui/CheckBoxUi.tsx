import { cn } from '@/lib/cn';
import { FormFieldProps } from './common/FormFieldProps';
import { Controller, FieldValues } from 'react-hook-form';
import { ReactNode } from 'react';

/**
 * 체크박스 UI 컴포넌트
 * @param label 체크박스 라벨
 * @param name 체크박스 이름
 * @param control react-hook-form의 control 객체
 * @param onClick 체크박스 클릭 이벤트 핸들러
 * @param defaultChecked 기본 체크 상태
 * @param disabled 비활성화 여부
 * @param className 추가적인 클래스 이름
 */
export const CheckBoxUi = <T extends FieldValues>({
  label,
  name,
  control,
  onClick,
  defaultChecked,
  disabled,
  className,
}: FormFieldProps<T>) => {
  // i18n 번역 훅
  return (
    <>
      <div className={cn('mb-1 inline-flex items-center gap-2', className)}>
        <Controller
          name={name}
          control={control}
          render={({ field: { onChange, value, ref } }) => {
            const checked =
              typeof value === 'boolean' ? value : !!defaultChecked;
            return (
              <>
                {/* 체크박스 */}
                <input
                  id={name}
                  type="checkbox"
                  onChange={(e) => {
                    onClick?.();
                    onChange(e.target.checked);
                  }}
                  value={value}
                  ref={ref}
                  checked={checked}
                  disabled={disabled}
                  className={cn(
                    // 사이즈: 모바일에서 더 큼 → 상위 뷰포트로 갈수록 작아짐
                    'h-5 w-5 sm:h-4 sm:w-4',
                    // 라운드/보더만 유지, 배경은 지정하지 않음(브라우저가 accent 처리)
                    'rounded border border-black/20',
                    // 포커스 링
                    'focus-visible:ring-primary focus-visible:ring-2 focus-visible:ring-offset-2 focus-visible:outline-none',
                    // 파란 배경 + 흰 체크
                    'accent-primary',
                    // 비활성화
                    disabled && 'pointer-events-none opacity-50'
                  )}
                />
                {/* 체크박스 라벨(클릭 영역 확대) */}
                <label
                  htmlFor={name}
                  className={cn(
                    'cursor-pointer select-none',
                    'text-foreground text-sm md:text-base'
                  )}
                >
                  {label as ReactNode}
                </label>
              </>
            );
          }}
        />
      </div>
    </>
  );
};
