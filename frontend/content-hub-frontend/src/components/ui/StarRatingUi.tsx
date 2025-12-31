import { useEffect, useRef, useState } from 'react';
import { Controller, FieldValues } from 'react-hook-form';
import { BsStar, BsStarHalf, BsStarFill } from 'react-icons/bs';
import { FormFieldProps } from './common/FormFieldProps';
import { ErrorMessageUi } from './common/ErrorMessageUi';
import {
  ARROW_DOWN_KEY,
  ARROW_LEFT_KEY,
  ARROW_RIGHT_KEY,
  ARROW_UP_KEY,
  END_KEY,
  ENTER_KEY,
  HOME_KEY,
} from '../common/constants/constants';
import { cn } from '@/lib/cn';

type StarSizeType = 'sm' | 'md' | 'lg';

/**
 * 별점 UI 컴포넌트 Props 타입
 * @template T - react-hook-form의 FieldValues 타입
 */
type StarRatingUiProps<T extends FieldValues> = FormFieldProps<T> & {
  isStarRatingEditable?: boolean; // 별점 수정 가능 여부
  selectedStarRating?: number; // 선택된 별점 (코멘트 목록에서 기존 별점 표시용)
  starRatingErrorMsg?: string; // 별점 관련 에러 메시지
  starSize?: StarSizeType; // 별점 크기
};

/**
 * 별점 표시 UI
 * 별점을 클릭하여 선택할 수 있으며, 선택된 별점은 고정
 * 별점은 0.5 단위로 표시되며, 최대 5점까지 선택 가능
 * @param StarRatingUiProps
 */
export const StarRatingUi = <T extends FieldValues>({
  name,
  control,
  isStarRatingEditable,
  selectedStarRating,
  starRatingErrorMsg,
  starSize = 'sm',
}: StarRatingUiProps<T>) => {
  // 별점 클릭시 고정하기 위한 상태값
  const [isSelected, setIsSelected] = useState(false);
  // 이전 별점 저장용 참조값
  const previousSelected = useRef<number>(0);
  // 색상: 빈 별(중립 / 오류 빨강), 채워진 별(노랑)
  const emptyStarColor = starRatingErrorMsg ? 'text-red-500' : 'text-black/20';
  const filledStarColor = 'text-yellow-400';
  // 별점 단위
  const starRatingUnit = 0.5;
  // 별 크기 래퍼 클래스
  const starSizeWrapperClass = {
    sm: 'h-5 w-5',
    md: 'h-6 w-6',
    lg: 'h-8 w-8',
  }[starSize];
  // 별 크기 클래스
  const starSizeClass = {
    sm: 'text-xl',
    md: 'text-2xl',
    lg: 'text-3xl',
  }[starSize];

  // 별점 상태를 0.5단위로 배열 생성(각 아이템은 "반 별" + 그 다음 "채워진 별"을 담당)
  const createStarState = () => {
    return Array.from({ length: 5 }, (_, index) => ({
      starRating: starRatingUnit + index,
    }));
  };

  // 외부 선택 값 판정
  const isSelectedStarRating = selectedStarRating || selectedStarRating === 0;

  /**
   * 이미 선택된 별점이 있는 경우 해당 별점을 고정하고 이전 별점으로 설정
   *
   * 의존성 배열에 selectedStarRating 을 넣으면
   * 수정 모드에서 기존 별점을 불러올 때마다 useEffect 가 실행되어
   * 처음 사용자가 선택한 별점이 사라지는 문제가 발생하여 의존성 배열을 빈 배열로 설정
   */
  /* eslint-disable react-hooks/exhaustive-deps */
  useEffect(() => {
    if (isSelectedStarRating) {
      setIsSelected(true);
      previousSelected.current = selectedStarRating;
    }
  }, []);

  // 값 범위 제한 함수
  const clamp = (n: number, min: number, max: number) =>
    Math.max(min, Math.min(max, n));

  return (
    // react-hook-form 의 Controller 를 이용하여 컴포넌트와 연동
    <Controller
      name={name}
      control={control}
      render={({ field: { value, onChange } }) => {
        // 선택 된 값이 이미 있는 경우, 선택 된 값을 설정
        const realValue = isSelectedStarRating ? selectedStarRating : value;

        // 키보드 조작(그룹)
        // 키보드 좌/우(상/하)로 0.5 증감, Home/End로 0/5로 이동
        const handleKeyDown = (e: React.KeyboardEvent) => {
          if (!isStarRatingEditable) return;
          let next: number; // 다음 별점 값
          if (e.key === ARROW_RIGHT_KEY || e.key === ARROW_UP_KEY) {
            next = clamp((realValue ?? 0) + 0.5, 0, 5);
            e.preventDefault();
          } else if (e.key === ARROW_LEFT_KEY || e.key === ARROW_DOWN_KEY) {
            next = clamp((realValue ?? 0) - 0.5, 0, 5);
            e.preventDefault();
          } else if (e.key === HOME_KEY) {
            next = 0;
            e.preventDefault();
          } else if (e.key === END_KEY) {
            next = 5;
            e.preventDefault();
          } else if (e.key === ENTER_KEY || e.key === ' ') {
            // 현재 값 확정
            setIsSelected(true);
            previousSelected.current = realValue;
            e.preventDefault();
            return;
          } else {
            return;
          }
          onChange(next);
        };

        return (
          <div className="block">
            <div
              className="flex justify-center outline-none md:gap-1"
              role="radiogroup"
              aria-label="별점 선택"
              aria-readonly={!isStarRatingEditable}
              tabIndex={0}
              onKeyDown={handleKeyDown}
            >
              {
                // 별점을 루프 돌아가며 표시
                createStarState().map((items, index) => {
                  // 반 별의 별점
                  const halfStarRating = items.starRating; // 0.5, 1.5, ...
                  // 채워진 별의 별점
                  const fillStarRating = items.starRating + starRatingUnit; // 1.0, 2.0, ...
                  // onMouseLeave 시의 처리
                  const handleOnMouseLeave = () => {
                    // 별점 수정 불가 시 무시
                    if (!isStarRatingEditable) return;
                    // 선택된 별점이 없는 경우에는 초기화, 있는 경우에는 이전 상태로 되돌림
                    if (isSelected) {
                      onChange(previousSelected.current);
                    } else {
                      onChange(0);
                    }
                  };
                  // onClick 시의 처리
                  const handleOnClick = (starRating: number) => {
                    // 별점 수정 불가 시 무시
                    if (!isStarRatingEditable) return;
                    // 선택한 별점 저장
                    previousSelected.current = starRating;
                    // 별점 고정
                    setIsSelected(true);
                    // 선택한 별점의 값 변경(onChange 를 통해 react-hook-form 으로 연동)
                    onChange(starRating);
                  };

                  return (
                    <div
                      className={`relative ${starSizeWrapperClass}`}
                      key={items.starRating + '_' + index}
                      aria-label={`${fillStarRating}점`}
                    >
                      {/* 반 별 */}
                      {/* z-index를 통해 우선순위를 정해서 각 별 아이콘이 겹치지 않도록 함 */}
                      {/* 별의 표시영역을 가로/세로 절반으로 설정하고, overflow-hidden을 통해 넘치는 영역을 잘라냄 */}
                      <button
                        type="button"
                        className={cn(
                          'focus-visible:ring-primary absolute z-20 h-full w-1/2 overflow-hidden focus-visible:ring-2 focus-visible:ring-offset-2 focus-visible:outline-none',
                          isStarRatingEditable && 'cursor-pointer'
                        )}
                        onMouseEnter={() =>
                          isStarRatingEditable && onChange(halfStarRating)
                        }
                        onMouseLeave={() =>
                          isStarRatingEditable && handleOnMouseLeave()
                        }
                        onClick={() =>
                          isStarRatingEditable && handleOnClick(halfStarRating)
                        }
                        tabIndex={-1}
                      >
                        {
                          // 해당 반 별의 별점 <= 현재 설정된 value값의 경우, 반별 표시
                          halfStarRating <= realValue && (
                            <BsStarHalf
                              className={`${starSizeClass} ${filledStarColor}`}
                            />
                          )
                        }
                      </button>

                      {/* 빈 별 */}
                      {/* z-index를 통해 우선순위를 정해서 각 별 아이콘이 겹치지 않도록 함 */}
                      <BsStar
                        className={`absolute z-0 ${starSizeClass} ${emptyStarColor}`}
                      />

                      {/* 채워진 별 */}
                      {/* z-index를 통해 우선순위를 정해서 각 별 아이콘이 겹치지 않도록 함 */}
                      <button
                        type="button"
                        className={cn(
                          'focus-visible:ring-primary absolute z-10 h-full w-full focus-visible:ring-2 focus-visible:ring-offset-2 focus-visible:outline-none',
                          isStarRatingEditable && 'cursor-pointer'
                        )}
                        onMouseEnter={() =>
                          isStarRatingEditable && onChange(fillStarRating)
                        }
                        onMouseLeave={() =>
                          isStarRatingEditable && handleOnMouseLeave()
                        }
                        onClick={() =>
                          isStarRatingEditable && handleOnClick(fillStarRating)
                        }
                        tabIndex={-1}
                      >
                        {
                          // 해당 채워진 별의 별점 <= 현재 설정된 value값의 경우, 채워진 별 표시
                          fillStarRating <= realValue && (
                            <BsStarFill
                              className={`${starSizeClass} ${filledStarColor}`}
                            />
                          )
                        }
                      </button>
                    </div>
                  );
                })
              }
            </div>
            {/* 에러 메세지 표시 */}
            <div className="flex justify-center">
              <ErrorMessageUi
                errorMsg={starRatingErrorMsg}
                toastId="starRatingError"
                isOnlyToast={true}
              />
            </div>
          </div>
        );
      }}
    />
  );
};
