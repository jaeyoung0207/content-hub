import { useRef, useState } from 'react';
import { Controller, FieldValues } from 'react-hook-form';
import { BsStar, BsStarHalf, BsStarFill } from 'react-icons/bs';
import { FormFieldProps } from './common/FormFieldProps';
import { ErrorMessageUi } from './common/ErrorMessageUi';

/**
 * 별점 UI 컴포넌트 Props 타입
 * @template T - react-hook-form의 FieldValues 타입
 */
type StarRatingUiProps<T extends FieldValues> = FormFieldProps<T> & {
  selectedStarRating?: number; // 선택된 별점
  starRatingErrorMsg?: string; // 별점 관련 에러 메시지
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
  selectedStarRating,
  starRatingErrorMsg,
}: StarRatingUiProps<T>) => {
  // 별점 클릭시 고정하기 위한 상태값
  const [isSelected, setIsSelected] = useState(false);
  // 이전 별점 저장용 참조값
  const previousSelected = useRef(0);
  // 별점 스타일 정의
  // 에러 메시지가 있는 경우 빨간색, 없는 경우 노란색
  const starStyle = ` text-2xl ${starRatingErrorMsg ? 'text-red-500' : 'text-yellow-300'}`;
  // const starStyle = "text-2xl text-yellow-300";
  // 별점 상태를 0.5단위로 배열 생성
  const createStarState = () => {
    return Array.from({ length: 5 }, (_, index) => ({
      starRating: 0.5 + index,
    }));
  };

  // 외부 선택 값 판정
  const isSelectedStarRating = selectedStarRating || selectedStarRating === 0;

  return (
    // react-hook-form 의 Controller 를 이용하여 컴포넌트와 연동
    <Controller
      name={name}
      control={control}
      render={({ field: { value, onChange } }) => {
        // 선택 된 값이 이미 있는 경우, 선택 된 값을 설정
        const realValue = isSelectedStarRating ? selectedStarRating : value;
        // 선택 된 값이 이미 있는 경우, 값 고정
        if (isSelectedStarRating) {
          setIsSelected(true);
        }
        return (
          <div className="block">
            <div
              className="mb-1 flex justify-center"
              role="radiogroup"
              aria-label="별점 선택"
            >
              {
                // 별점을 루프 돌아가며 표시
                createStarState().map((items, index) => {
                  // 반 별의 별점
                  const halfStarRate = items.starRating;
                  // 채워진 별의 별점
                  const fillStarRate = items.starRating + 0.5;
                  // onMouseLeave 시의 처리
                  const handleOnMouseLeave = () => {
                    // 선택된 별점이 없는 경우에는 초기화, 있는 경우에는 이전 상태로 되돌림
                    return () =>
                      !isSelected
                        ? onChange(0)
                        : onChange(previousSelected.current);
                  };
                  // onClick 시의 처리
                  const handleOnClick = (starRating: number) => {
                    // 선택한 별점 저장
                    previousSelected.current = starRating;
                    // 별점 고정
                    setIsSelected(true);
                    // 선택한 별점의 값 변경(onChange 를 통해 react-hook-form 으로 연동)
                    onChange(starRating);
                  };

                  return (
                    <div className="relative w-6 h-6" key={index}>
                      {/* 반 별 */}
                      {/* z-index를 통해 우선순위를 정해서 각 별 아이콘이 겹치지 않도록 함 */}
                      {/* 별의 표시영역을 가로/세로 절반으로 설정하고, overflow-hidden을 통해 넘치는 영역을 잘라냄 */}
                      <div
                        className="absolute z-2 w-1/2 h-full overflow-hidden cursor-pointer"
                        onMouseEnter={() =>
                          !isSelectedStarRating && onChange(halfStarRate)
                        }
                        onMouseLeave={() =>
                          !isSelectedStarRating && handleOnMouseLeave()
                        }
                        onClick={() =>
                          !isSelectedStarRating && handleOnClick(halfStarRate)
                        }
                      >
                        {
                          // 해당 반 별의 별점 <= 현재 설정된 value값의 경우, 반별 표시
                          halfStarRate <= realValue && (
                            <BsStarHalf className={starStyle} />
                          )
                        }
                      </div>

                      {/* 빈 별 */}
                      {/* z-index를 통해 우선순위를 정해서 각 별 아이콘이 겹치지 않도록 함 */}
                      <BsStar
                        className={'absolute z-0 text-2xl ' + starStyle}
                      />

                      {/* 채워진 별 */}
                      {/* z-index를 통해 우선순위를 정해서 각 별 아이콘이 겹치지 않도록 함 */}
                      <div
                        className="absolute z-1 w-full h-full cursor-pointer"
                        onMouseEnter={() =>
                          !isSelectedStarRating && onChange(fillStarRate)
                        }
                        onMouseLeave={() =>
                          !isSelectedStarRating && handleOnMouseLeave()
                        }
                        onClick={() =>
                          !isSelectedStarRating && handleOnClick(fillStarRate)
                        }
                      >
                        {
                          // 해당 채워진 별의 별점 <= 현재 설정된 value값의 경우, 채워진 별 표시
                          fillStarRate <= realValue && (
                            <BsStarFill className={starStyle} />
                          )
                        }
                      </div>
                    </div>
                  );
                })
              }
            </div>
            {/* 에러 메세지 표시 */}
            {starRatingErrorMsg && (
              <div className="flex justify-center">
                <ErrorMessageUi errorMsg={starRatingErrorMsg} />
              </div>
            )}
          </div>
        );
      }}
    />
  );
};
