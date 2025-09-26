import { Controller, FieldValues } from 'react-hook-form';
import { FormFieldProps } from './common/FormFieldProps';

/**
 * 라디오 버튼 Props 타입
 */
export type RadioButtonProps = {
  label: string;
  value: string;
};

/**
 * 라디오 버튼 그룹 UI 컴포넌트 Props 타입
 * @template T - FormFieldProps의 제네릭 타입
 */
type RadioButtonGroupUiProps<T extends FieldValues> = FormFieldProps<T> & {
  radioButtonList: RadioButtonProps[];
  displayStyle?: 'block' | 'flex'; // 라디오 버튼 그룹의 디스플레이 스타일 (기본값: 'block')
  onClickRadioButton?: (value: string) => void; // 라디오 버튼 클릭 이벤트 핸들러
  defaultValue?: string;
};

/**
 * 라디오 버튼 그룹 UI 컴포넌트
 * @template T - FormFieldProps의 제네릭 타입
 * @param name - 폼 필드 이름
 * @param control - react-hook-form의 control 객체
 * @param defaultChecked - 기본 선택 상태
 * @param radioButtonList - 라디오 버튼 목록
 */
export const RadioButtonGroupUi = <T extends FieldValues>({
  name,
  control,
  label,
  onClickRadioButton,
  radioButtonList,
  displayStyle,
  defaultChecked,
  defaultValue,
}: RadioButtonGroupUiProps<T>) => {
  return (
    <>
      <div>
        {label && <div className="text-md font-bold mb-1">{label}</div>}
        <Controller
          name={name}
          control={control}
          render={({ field: { onChange, value } }) => {
            return (
              <div className={displayStyle}>
                {/* 라디오 버튼 */}
                {radioButtonList.map((items, index) => (
                  <div key={index} className="flex items-center me-2 mb-1">
                    <input
                      id={`${name}-${items.value}`}
                      className="w-4 h-4 text-blue-600 bg-white border-gray-300 focus:ring-blue-500"
                      type="radio"
                      onChange={() => {
                        onClickRadioButton?.(items.value);
                        onChange(items.value);
                      }}
                      value={items.value}
                      checked={value === items.value}
                      defaultValue={defaultValue}
                      defaultChecked={defaultChecked}
                    />
                    <label
                      htmlFor={`${name}-${items.value}`}
                      className="px-2 text-black text-xs lg:text-lg font-normal font-['Inter']"
                    >
                      {items.label}
                    </label>
                  </div>
                ))}
              </div>
            );
          }}
        />
      </div>
    </>
  );
};

export default RadioButtonGroupUi;
