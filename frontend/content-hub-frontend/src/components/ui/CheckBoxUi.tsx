import { useTranslation } from 'react-i18next';
import { FormFieldProps } from './common/FormFieldProps';
import { Controller, FieldValues } from 'react-hook-form';

/**
 * 체크박스 UI 컴포넌트
 * @param label 체크박스 라벨
 * @param name 체크박스 이름
 * @param control react-hook-form의 control 객체
 * @param onClick 체크박스 클릭 이벤트 핸들러
 * @param defaultChecked 기본 체크 상태
 */
export const CheckBoxUi = <T extends FieldValues>({
  label,
  name,
  control,
  onClick,
  defaultChecked,
  disabled,
}: FormFieldProps<T>) => {
  // i18n 번역 훅
  const { t } = useTranslation();
  return (
    <>
      <div className="flex items-center me-4">
        <Controller
          name={name}
          control={control}
          render={({ field: { onChange, value } }) => {
            return (
              <>
                {/* 체크박스 */}
                <input
                  id={name}
                  type="checkbox"
                  onChange={(e) => {
                    onClick?.();
                    onChange(e);
                  }}
                  value={value}
                  className="w-4 h-4 text-blue-600 bg-white border-gray-300 rounded-sm focus:ring-blue-500"
                  defaultChecked={defaultChecked}
                  disabled={disabled}
                />
                {/* 체크박스 라벨 */}
                <label
                  htmlFor={name}
                  className="px-2 text-black text-xs lg:text-xl font-normal font-['Inter']"
                >
                  {t(label!)}
                </label>
              </>
            );
          }}
        />
      </div>
    </>
  );
};
