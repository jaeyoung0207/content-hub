import { Controller, FieldValues } from 'react-hook-form';
import { FormFieldProps } from './common/FormFieldProps';
import { cn } from '@/lib/cn';

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
  displayStyle?: 'block' | 'flex'; // 기본값: 'block'
  onClickRadioButton?: (value: string) => void;
  defaultValue?: string;
};

/**
 * 라디오 버튼 그룹 UI 컴포넌트
 * @template T - FormFieldProps의 제네릭 타입
 * @param name - 폼 필드 이름
 * @param control - react-hook-form의 control 객체
 * @param label - 라디오 버튼 그룹 라벨
 * @param onClickRadioButton - 라디오 버튼 클릭 이벤트 핸들러
 * @param radioButtonList - 라디오 버튼 목록
 * @param displayStyle - 라디오 버튼 그룹의 디스플레이 스타일
 * @param defaultChecked - 기본 선택 상태
 * @param defaultValue - 기본 값
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
  // 접근성: 라벨과 그룹을 연결
  const groupLabelId = String(name);
  // 디스플레이 스타일에 따른 클래스 설정
  const groupClass =
    displayStyle === 'flex'
      ? 'flex flex-wrap items-center gap-3'
      : 'grid gap-1';

  return (
    <div>
      {label && (
        <div
          id={groupLabelId}
          className="mb-1 text-sm font-semibold md:text-base"
        >
          {label}
        </div>
      )}
      <Controller
        name={name}
        control={control}
        render={({ field: { onChange, value } }) => {
          return (
            <div
              role="radiogroup"
              aria-labelledby={label ? groupLabelId : undefined}
              className={groupClass}
            >
              {/* 라디오 버튼 */}
              {radioButtonList.map((items) => {
                const id = `${String(name)}-${items.value}`;
                const checked = value === items.value;
                return (
                  <label
                    key={items.value}
                    htmlFor={id}
                    className={cn(
                      'inline-flex cursor-pointer items-center gap-2 select-none'
                    )}
                  >
                    <input
                      id={id}
                      type="radio"
                      onChange={() => {
                        onClickRadioButton?.(items.value);
                        onChange(items.value);
                      }}
                      value={items.value}
                      checked={checked}
                      defaultValue={defaultValue}
                      defaultChecked={defaultChecked}
                      className={cn(
                        'h-5 w-5 sm:h-4 sm:w-4',
                        'rounded-full border border-black/20',
                        'focus-visible:ring-primary focus-visible:ring-2 focus-visible:ring-offset-2 focus-visible:outline-none',
                        'accent-primary'
                      )}
                    />
                    <span className="text-foreground text-sm md:text-base">
                      {items.label}
                    </span>
                  </label>
                );
              })}
            </div>
          );
        }}
      />
    </div>
  );
};

export default RadioButtonGroupUi;
