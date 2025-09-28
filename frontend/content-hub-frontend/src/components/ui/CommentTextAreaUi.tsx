import { Controller, FieldValues } from 'react-hook-form';
import { FormFieldProps } from './common/FormFieldProps';
import { useTranslation } from 'react-i18next';
import { RefObject } from 'react';
import { commonErrorHandler } from '../common/utils/errorUtil';

type CommentTextAreaUiProps<T extends FieldValues> = FormFieldProps<T> & {
  textAreaRef: RefObject<HTMLTextAreaElement | null>;
  maxLength: number;
  textAreaStyle?: string;
};

/**
 * 텍스트 영역 컴포넌트
 * @param name 필드 이름
 * @param control react-hook-form의 control 객체
 * @param onClick 클릭 이벤트 핸들러
 * @param textAreaRef 텍스트 영역 참조 객체
 */
export const CommentTextAreaUi = <T extends FieldValues>({
  name,
  control,
  onClick,
  textAreaRef,
  maxLength,
  textAreaStyle,
}: CommentTextAreaUiProps<T>) => {
  // i18n 번역 훅
  const { t } = useTranslation();
  return (
    <>
      <div className="flex justify-center">
        <Controller
          name={name}
          render={({ field: { name, onBlur, onChange, value } }) => {
            return (
              <>
                <div className="block">
                  {/* 텍스트 영역 */}
                  <textarea
                    className={`mt-2 text-xl border-1 resize-none p-3 ${textAreaStyle ? textAreaStyle : 'w-2xl h-50'}`}
                    placeholder={t('info.requireComment')}
                    name={name}
                    value={value}
                    onChange={onChange}
                    onBlur={onBlur}
                    onClick={onClick && commonErrorHandler(onClick)}
                    ref={textAreaRef}
                    maxLength={maxLength}
                  />
                </div>
              </>
            );
          }}
          control={control}
        ></Controller>
      </div>
    </>
  );
};
