import {
  AppContentMediaTypeDto,
  AppDisplayMediaTypeDto,
  LoginUserInfoDto,
} from '@/api/data-contracts';
import { create } from 'zustand';
import { persist } from 'zustand/middleware';

// ================================================================================================== type

/**
 * 홈 버튼 클릭시 초기화 상태관리 타입
 */
type UseInitializeStoreType = {
  isReadyForInitialization: boolean;
  setIsReadyForInitialization: () => void;
  setIsNotReadyForInitialization: () => void;
};

/**
 * 유저 정보 상태관리 타입
 */
type UseUserStoreType = {
  user: LoginUserInfoDto | null;
  setUser: (user: LoginUserInfoDto) => void;
  clearUser: () => void;
};

/**
 * 코멘트 수정 상태관리 타입
 */
type UseCommentStoreType = {
  isCommentEditable: boolean;
  setIsCommentEditable: () => void;
};

/**
 * 검색 필터 상태관리 타입
 */
type UseStarRatingStoreType = {
  userStarRating?: number;
  setUserStarRating: (userStarRating: number) => void;
};

/**
 * 확인 다이얼로그 상태관리 타입
 */
type UseConfirmDialogStoreType = {
  isConfirmDialogOpen: boolean;
  setIsConfirmDialogOpen: (isConfirmDialogOpen: boolean) => void;
  onOk: () => void;
  setOnOk: (onOk: () => void) => void;
  onCancel: () => void;
  setOnCancel: (onCancel: () => void) => void;
  title: string;
  setTitle: (title: string) => void;
  confirmMsg: string;
  setConfirmMsg: (confirmMsg: string) => void;
};

/**
 * 검색 필터 상태관리 타입
 */
export type UseSearchTypeStoreType = {
  searchTypeState: {
    aniFlg: boolean;
    movieFlg: boolean;
    dramaFlg: boolean;
    documentaryFlg: boolean;
    kidsFlg: boolean;
    newsFlg: boolean;
    varietyFlg: boolean;
    comicsFlg: boolean;
  };
  setSearchTypeState: (
    aniFlg: boolean,
    movieFlg: boolean,
    dramaFlg: boolean,
    documentaryFlg: boolean,
    kidsFlg: boolean,
    newsFlg: boolean,
    varietyFlg: boolean,
    comicsFlg: boolean
  ) => void;
};

/**
 * 에러 상태관리 타입
 */
type UseErrorStoreType = {
  status?: number;
  message?: string;
  url?: string;
  setError: (status?: number, message?: string, url?: string) => void;
};

/**
 * 유저 평점 상태관리 타입
 */
type UseProviderStoreType = {
  provider?: string;
  setProvider: (provider: string) => void;
  clearProvider: () => void;
};

/**
 * 툴팁 상태관리 타입
 */
type UseTooltipStoreType = {
  isTooltipOpen: number;
  setIsTooltipOpen: (isOpen: number) => void;
};

/**
 * 컨텐츠 미디어 타입 맵 상태관리 타입
 */
type UseContentMediaTypeMapStoreType = {
  contentMediaType: AppContentMediaTypeDto | undefined;
  isContentMediaTypeInitialized: boolean;
  setContentMediaType: (
    contentMediaType: AppContentMediaTypeDto,
    isContentMediaTypeInitialized: boolean
  ) => void;
  clearContentMediaType: () => void;
};

/**
 * 화면 표시용 미디어 타입 맵 상태관리 타입
 */
type UseDisplayMediaTypeMapStoreType = {
  displayMediaType: AppDisplayMediaTypeDto | undefined;
  isDisplayMediaTypeInitialized: boolean;
  setDisplayMediaType: (
    displayMediaType: AppDisplayMediaTypeDto,
    isDisplayMediaTypeInitialized: boolean
  ) => void;
  clearDisplayMediaType: () => void;
};

// ================================================================================================== store

/**
 * 홈 버튼 클릭시 초기화 상태관리
 */
export const useInitializeStore = create<UseInitializeStoreType>((set) => ({
  isReadyForInitialization: false,
  setIsReadyForInitialization: () =>
    set(() => ({ isReadyForInitialization: true })),
  setIsNotReadyForInitialization: () =>
    set(() => ({ isReadyForInitialization: false })),
}));

/**
 * 유저 정보 상태관리
 */
export const useUserStore = create<UseUserStoreType>((set) => ({
  user: null,
  setUser: (user) => set(() => ({ user })),
  clearUser: () => set(() => ({ user: null })),
}));

/**
 * 코멘트 수정 상태관리
 */
export const useCommentStore = create<UseCommentStoreType>((set) => ({
  isCommentEditable: false,
  setIsCommentEditable: () =>
    set((state) => ({ isCommentEditable: !state.isCommentEditable })),
}));

/**
 * 검색 필터 상태관리
 */
export const useSearchTypeStore = create<UseSearchTypeStoreType>((set) => ({
  searchTypeState: {
    aniFlg: true,
    dramaFlg: true,
    movieFlg: true,
    documentaryFlg: true,
    kidsFlg: true,
    newsFlg: true,
    varietyFlg: true,
    comicsFlg: true,
  },
  setSearchTypeState: (
    aniFlg,
    dramaFlg,
    movieFlg,
    documentaryFlg,
    kidsFlg,
    newsFlg,
    varietyFlg,
    comicsFlg
  ) =>
    set(() => ({
      searchTypeState: {
        aniFlg: aniFlg,
        dramaFlg: dramaFlg,
        movieFlg: movieFlg,
        documentaryFlg: documentaryFlg,
        kidsFlg: kidsFlg,
        newsFlg: newsFlg,
        varietyFlg: varietyFlg,
        comicsFlg: comicsFlg,
      },
    })),
}));

/**
 * 유저 평점 상태관리
 */
export const useStarRatingStore = create<UseStarRatingStoreType>((set) => ({
  userStarRating: undefined,
  setUserStarRating: (userStarRating) =>
    set(() => ({ userStarRating: userStarRating })),
}));

/**
 * 확인 다이얼로그 상태관리
 */
export const useConfirmDialogStore = create<UseConfirmDialogStoreType>(
  (set) => ({
    isConfirmDialogOpen: false,
    setIsConfirmDialogOpen: (isConfirmDialogOpen) =>
      set(() => ({ isConfirmDialogOpen: isConfirmDialogOpen })),
    onOk: () => {},
    setOnOk: (onOk) => set(() => ({ onOk })),
    onCancel: () => {},
    setOnCancel: (onCancel) => set(() => ({ onCancel })),
    title: '',
    setTitle: (title) => set(() => ({ title })),
    confirmMsg: '',
    setConfirmMsg: (confirmMsg) => set(() => ({ confirmMsg })),
  })
);

/**
 * 에러 상태관리
 */
export const useErrorStore = create<UseErrorStoreType>((set) => ({
  status: undefined,
  message: '',
  url: '',
  setError: (status, message, url) => set({ status, message, url }),
}));

/**
 * 프로바이더 상태관리
 */
export const useProviderStore = create<UseProviderStoreType>((set) => ({
  provider: undefined,
  setProvider: (provider) => set({ provider }),
  clearProvider: () => set({ provider: undefined }),
}));

/**
 * 툴팁 상태관리
 */
export const useTooltipStore = create<UseTooltipStoreType>((set) => ({
  isTooltipOpen: 0,
  setIsTooltipOpen: (isOpen: number) => set({ isTooltipOpen: isOpen }),
}));

/**
 * 컨텐츠 미디어 타입 맵 상태관리
 */
export const useContentMediaTypeMapStore =
  create<UseContentMediaTypeMapStoreType>()(
    persist(
      (set) => ({
        contentMediaType: undefined,
        isContentMediaTypeInitialized: false,
        setContentMediaType: (
          contentMediaType,
          isContentMediaTypeInitialized
        ) => set({ contentMediaType, isContentMediaTypeInitialized }),
        clearContentMediaType: () =>
          set({
            contentMediaType: undefined,
            isContentMediaTypeInitialized: false,
          }),
      }),
      {
        name: 'contentMediaTypeMapStore',
      }
    )
  );

/**
 * 화면 표시용 미디어 타입 맵 상태관리
 */
export const useDisplayMediaTypeMapStore =
  create<UseDisplayMediaTypeMapStoreType>()(
    persist(
      (set) => ({
        displayMediaType: undefined,
        isDisplayMediaTypeInitialized: false,
        setDisplayMediaType: (
          displayMediaType,
          isDisplayMediaTypeInitialized
        ) => set({ displayMediaType, isDisplayMediaTypeInitialized }),
        clearDisplayMediaType: () =>
          set({
            displayMediaType: undefined,
            isDisplayMediaTypeInitialized: false,
          }),
      }),
      {
        name: 'displayMediaTypeMapStore',
      }
    )
  );
