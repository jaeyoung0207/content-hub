import { useTranslation } from "react-i18next"
import { Controller, FieldValues } from "react-hook-form";
import { FormFieldProps } from "./common/FormFieldProps";

/**
 * 모바일용 체크박스 UI 컴포넌트
 * @param label 체크박스 라벨
 * @param name 체크박스 이름
 * @param control react-hook-form의 control 객체
 * @param onClick 체크박스 클릭 이벤트 핸들러
 * @param defaultChecked 기본 체크 상태
 */
export const CheckBoxUiM = <T extends FieldValues>({ label, name, control, disabled }: FormFieldProps<T>) => {
    // i18n 번역 훅
    const { t } = useTranslation();
    return (
        <>
            <div className="px-2">
                <Controller
                    name={name}
                    control={control}
                    render={({field : { onChange }}) => {
                        return (
                            <>
                                {/* 체크박스 */}
                                <input
                                    id={name}
                                    type='checkbox'
                                    onChange={onChange}
                                    className='hidden peer'
                                    defaultChecked
                                    disabled={disabled}
                                />
                                {/* 체크박스 라벨 */}
                                <label htmlFor={name} className='inline-flex items-center justify-between w-full p-2 text-gray-500 bg-white border-2 border-gray-200 rounded-lg cursor-pointer dark:hover:text-gray-300 dark:border-gray-700 peer-checked:border-blue-600 dark:peer-checked:border-blue-600 hover:text-gray-600 dark:peer-checked:text-gray-300 peer-checked:text-gray-600 hover:bg-gray-50 dark:text-gray-400 dark:bg-gray-800 dark:hover:bg-gray-700'>
                                    {t(label!)}
                                </label>
                            </>
                        )
                    }}
                />
            </div>
        </>
    )
}