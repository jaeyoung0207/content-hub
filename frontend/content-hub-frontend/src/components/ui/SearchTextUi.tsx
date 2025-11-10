import { memo } from 'react';
import { Controller, FieldValues } from 'react-hook-form';
import { useTranslation } from 'react-i18next';
import { FormFieldProps } from './common/FormFieldProps';
import { BiSearch } from 'react-icons/bi';
import { commonErrorHandler } from '../common/utils/errorUtil';
import { cn } from '@/lib/cn';
import { InputUi } from './common';
import { RiCloseLine } from 'react-icons/ri';

/**
 * 검색용 텍스트 박스 UI
 * @param FormFieldProps
 */
export const SearchTextUi = memo(
  <T extends FieldValues>({
    name,
    control,
    onClick,
    onMouseDown,
    onKeyDown,
    isFocusedRef,
    deleteValue,
    // 외부에서 폭/여백 등을 제어하려면 className을 전달
    ...rest
  }: FormFieldProps<T>) => {
    // i18n 번역 훅
    const { t } = useTranslation();
    return (
      <>
        <div
          className={cn(
            // 래퍼: 폭은 기본 w-full, 높이는 인풋이 결정
            'relative w-full',
            // 상위에서만 선택적으로 전달
            rest.className
          )}
        >
          <Controller
            name={name}
            control={control}
            render={({ field: { value, onBlur, onChange, ref } }) => {
              // 입력값 존재 여부
              const hasValue = (value ?? '').toString().trim().length > 0;
              // react-hook-form이 제공하는 필드 컨트롤
              return (
                <>
                  <InputUi
                    type="text"
                    name={name}
                    placeholder={t('info.searchPlease')}
                    // 좌우 아이콘 영역을 위한 패딩
                    className="pr-10 pl-10 text-sm sm:text-base"
                    value={value ?? ''} // 현재 상태의 값을 input에 반영 (제어 컴포넌트이므로 필수), react-hook-form과의 상태 동기화
                    onBlur={() => {
                      onBlur(); // touched 상태(입력값 변경 여부) 추적 및 유효성 검증 (mode: 'onBlur' 대응)
                      // 포커스가 벗어났을 때 isFocusedRef를 false로 설정
                      if (isFocusedRef) {
                        isFocusedRef.current = false;
                      }
                    }}
                    onFocus={() => {
                      // 포커스가 들어왔을 때 isFocusedRef를 true로 설정
                      if (isFocusedRef) {
                        isFocusedRef.current = true;
                      }
                    }}
                    onChange={onChange} // 입력 변경 → react-hook-form 상태 갱신
                    onKeyDown={(e) => onKeyDown?.(e)} // 키보드 이벤트 핸들링
                    onMouseDown={onMouseDown} // 마우스 다운 이벤트 핸들링
                    ref={ref} // Controller에서 ref를 연동시켜줘야 setFocus가 먹힘 -> react-hook-form의 setFocus는 내부적으로 ref로 DOM을 추적하는데, ref가 연결되지 않으면 포커스를 줄 수가 없기 때문
                  />
                  {/* 좌측: 검색 아이콘 (value가 있을 때만 클릭 활성화) */}
                  <button
                    type="button"
                    aria-label={t('info.searchResults') || '검색'}
                    className={cn(
                      'absolute inset-y-0 left-3 grid place-items-center',
                      !hasValue && 'pointer-events-none opacity-50'
                    )}
                    onClick={hasValue ? onClick : undefined}
                  >
                    <BiSearch className="text-foreground/70 h-5 w-5 cursor-pointer" />
                  </button>
                  {/* 우측: 지우기 버튼 (값 있을 때만 노출) */}
                  {hasValue && (
                    <button
                      type="button"
                      aria-label="입력 지우기"
                      className="text-foreground/60 hover:text-foreground absolute inset-y-0 right-3 grid cursor-pointer place-items-center"
                      onClick={
                        deleteValue
                          ? commonErrorHandler(deleteValue)
                          : undefined
                      }
                    >
                      <RiCloseLine className="h-5 w-5" />
                    </button>
                  )}
                </>
              );
            }}
          />
        </div>
      </>
    );
  }
);
