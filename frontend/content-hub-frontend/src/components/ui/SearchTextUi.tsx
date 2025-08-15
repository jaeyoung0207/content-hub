import { memo } from 'react';
import { Controller, FieldValues } from 'react-hook-form';
import { useTranslation } from 'react-i18next';
import { FormFieldProps } from './common/FormFieldProps';
import { BiSearch } from 'react-icons/bi';
import { commonErrorHandler } from '../common/utils/commonUtil';

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
  }: FormFieldProps<T>) => {
    // i18n 번역 훅
    const { t } = useTranslation();
    return (
      <>
        <div className="flex lg:w-sm w-xs lg:h-10 items-center border rounded-md shadow-sm">
          <Controller
            name={name}
            control={control}
            render={({ field: { value, onBlur, onChange, ref } }) => {
              // react-hook-form이 제공하는 필드 컨트롤
              return (
                <>
                  <input
                    type="text"
                    name={name}
                    placeholder={t('info.searchPlease')}
                    className="flex-1 px-4 py-2 text-sm sm:text-base md:text-lg focus:outline-none"
                    value={value} // 현재 상태의 값을 input에 반영 (제어 컴포넌트이므로 필수), react-hook-form과의 상태 동기화
                    onBlur={() => {
                      onBlur(); // touched 상태(입력값 변경 여부) 추적 및 유효성 검증 (mode: 'onBlur' 대응)
                      isFocusedRef!.current = false; // 포커스가 벗어났을 때 isFocusedRef를 false로 설정
                    }}
                    onFocus={() => {
                      isFocusedRef!.current = true; // 포커스가 들어왔을 때 isFocusedRef를 true로 설정
                    }}
                    onChange={onChange} // 입력 변경 → react-hook-form 상태 갱신
                    onKeyDown={(e) => {
                      onKeyDown?.(e); // 키보드 이벤트 핸들링
                    }}
                    onMouseDown={onMouseDown} // 마우스 다운 이벤트 핸들링
                    ref={ref} // Controller에서 ref를 연동시켜줘야 setFocus가 먹힘 -> react-hook-form의 setFocus는 내부적으로 ref로 DOM을 추적하는데, ref가 연결되지 않으면 포커스를 줄 수가 없기 때문
                  />
                  {value && (
                    <div className="mr-1 flex justify-center items-center">
                      <button
                        className="w-5 h-5 text-sm text-gray-400 cursor-pointer"
                        onClick={deleteValue && commonErrorHandler(deleteValue)}
                      >
                        X
                      </button>
                    </div>
                  )}
                  {/* 검색 아이콘 */}
                  <BiSearch
                    className="mr-3 w-6 h-6 cursor-pointer"
                    onClick={value.trim() ? onClick : undefined}
                  />
                </>
              );
            }}
          />
        </div>
      </>
    );
  }
);
