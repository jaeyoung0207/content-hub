import { KeyboardEvent, ReactElement, RefObject } from "react";
import { Control, ControllerRenderProps, FieldValues, Path } from "react-hook-form";

/**
 * FormFieldProps 타입 정의
 * React Hook Form의 필드 컴포넌트에 필요한 props를 정의
 */
export type FormFieldProps<T extends FieldValues> = {
    name: Path<T>, // 필드 이름
    control: Control<T>, // React Hook Form의 control 객체
    label?: string, // 필드 라벨
    render?: (field: ControllerRenderProps<T>) => ReactElement, // 필드 렌더링 함수
    errorMsg?: string, // 에러 메시지
    onClick?: () => void, // 클릭 이벤트 핸들러
    onMouseDown?: () => void, // 마우스 다운 이벤트 핸들러
    onKeyDown?: (e: KeyboardEvent) => void, // 키 다운 이벤트 핸들러
    isFocusedRef?: RefObject<boolean | null>, // 포커스 상태를 관리하는 Ref
    defaultChecked?: boolean, // 기본 체크 상태
    deleteValue?: () => void, // 값 삭제 함수
    disabled?: boolean, // 비활성화 상태
}