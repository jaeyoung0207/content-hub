import { Controller, FieldValues } from 'react-hook-form';
import { FormFieldProps } from './common/FormFieldProps';
import { useTranslation } from 'react-i18next';
import { RefObject } from 'react';
import { commonErrorHandler } from '../common/utils/errorUtil';
import { TextareaUi } from './common';

/**
 * CommentTextAreaUi 컴포넌트 props 타입
 */
type CommentTextAreaUiProps<T extends FieldValues> = FormFieldProps<T> & {
  textAreaRef: RefObject<HTMLTextAreaElement | null>;
  maxLength: number;
};

/**
 * 텍스트 영역 컴포넌트
 * @param name 필드 이름
 * @param control react-hook-form의 control 객체
 * @param onClick 클릭 이벤트 핸들러
 * @param textAreaRef 텍스트 영역 참조 객체
 * @param maxLength 최대 입력 길이
 */
export const CommentTextAreaUi = <T extends FieldValues>({
  name,
  control,
  onClick,
  textAreaRef,
  maxLength,
}: CommentTextAreaUiProps<T>) => {
  // i18n 번역 훅
  const { t } = useTranslation();
  return (
    <>
      <div className="flex justify-center">
        {/* 반응형 폭 제어 */}
        <div className="w-xs md:w-xl lg:w-2xl">
          <Controller
            name={name}
            render={({ field: { name, onBlur, onChange, value } }) => {
              return (
                <>
                  <div className="block">
                    {/* 텍스트 영역 */}
                    <TextareaUi
                      className={'mt-2'}
                      textareaSize="lg"
                      placeholder={t('info.requireComment')}
                      name={name}
                      value={value ?? ''}
                      onChange={onChange}
                      onBlur={onBlur}
                      onClick={
                        onClick ? commonErrorHandler(onClick) : undefined
                      } // false일 때는 undefined로 설정
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
      </div>
    </>
  );
};
