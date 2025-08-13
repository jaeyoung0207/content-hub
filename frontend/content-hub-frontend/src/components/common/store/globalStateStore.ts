import { LoginUserInfoDto } from "@/api/data-contracts";
import { create } from "zustand";

// ================================================================================================== type

/**
 * 홈 버튼 클릭시 초기화 상태관리 타입
 */
type useInitializeStoreType = {
    isReadyForInitialization: boolean,
    setIsReadyForInitialization: () => void,
    setIsNotReadyForInitialization: () => void,
}

/**
 * 유저 정보 상태관리 타입
 */
type useUserStoreType = {
    user: LoginUserInfoDto | null,
    setUser: (user: LoginUserInfoDto) => void,
    clearUser: () => void,
}

/**
 * 코멘트 수정 상태관리 타입
 */
type useCommentStoreType = {
    isCommentEditable: boolean,
    setIsCommentEditable: () => void,
}

/**
 * 검색 필터 상태관리 타입
 */
type useStarRatingStoreType = {
    userStarRating?: number,
    setUserStarRating: (userStarRating: number) => void,
}

/**
 * 확인 다이얼로그 상태관리 타입
 */
type useConfirmDialogStoreType = {
    isConfirmDialogOpen: boolean,
    setIsConfirmDialogOpen: () => void,
}

/**
 * 검색 필터 상태관리 타입
 */
export type useSearchTypeStoreType = {
    searchTypeState: {
        aniFlg: boolean,
        movieFlg: boolean,
        dramaFlg: boolean,
        comicsFlg: boolean,
        novelFlg: boolean,
    },
    setSearchTypeState: (
        aniFlg: boolean,
        movieFlg: boolean,
        dramaFlg: boolean,
        comicsFlg: boolean,
        novelFlg: boolean,
    ) => void,
}

/**
 * 에러 상태관리 타입
 */
type useErrorStoreType = {
    status?: number,
    message?: string,
    url?: string,
    setError: (status?: number, message?: string, url?: string) => void,
}

/**
 * 유저 평점 상태관리 타입
 */
type useProviderStoreType = {
    provider?: string,
    setProvider: (provider: string) => void,
    clearProvider: () => void,
}

// ================================================================================================== store

/**
 * 홈 버튼 클릭시 초기화 상태관리
 */
export const useInitializeStore = create<useInitializeStoreType>((set) => ({
    isReadyForInitialization: false,
    setIsReadyForInitialization: () => set(() => ({ isReadyForInitialization: true })),
    setIsNotReadyForInitialization: () => set(() => ({ isReadyForInitialization: false })),
}));

/**
 * 유저 정보 상태관리
 */
export const useUserStore = create<useUserStoreType>((set) => ({
    user: null,
    setUser: (user) => set(() => ({ user })),
    clearUser: () => set(() => ({ user: null })),
}));

/**
 * 코멘트 수정 상태관리
 */
export const useCommentStore = create<useCommentStoreType>((set) => ({
    isCommentEditable: false,
    setIsCommentEditable: () => set((state) => ({ isCommentEditable: !state.isCommentEditable })),
}));

/**
 * 검색 필터 상태관리
 */
export const useSearchTypeStore = create<useSearchTypeStoreType>((set) => ({
    searchTypeState: {
        aniFlg: true,
        movieFlg: true,
        dramaFlg: true,
        comicsFlg: true,
        novelFlg: true,
    },
    setSearchTypeState: (aniFlg, dramaFlg, movieFlg, comicsFlg, novelFlg) => set(() => ({
        searchTypeState: {
            aniFlg: aniFlg,
            dramaFlg: dramaFlg,
            movieFlg: movieFlg,
            comicsFlg: comicsFlg,
            novelFlg: novelFlg,
        }
    })),
}));

/**
 * 유저 평점 상태관리
 */
export const useStarRatingStore = create<useStarRatingStoreType>((set) => ({
    userStarRating: undefined,
    setUserStarRating: (userStarRating) => set(() => ({ userStarRating: userStarRating })),
}));

/**
 * 확인 다이얼로그 상태관리
 */
export const useConfirmDialogStore = create<useConfirmDialogStoreType>((set) => ({
    isConfirmDialogOpen: false,
    setIsConfirmDialogOpen: () => set((state) => ({ isConfirmDialogOpen: !state.isConfirmDialogOpen })),
}));

/**
 * 에러 상태관리
 */
export const useErrorStore = create<useErrorStoreType>((set) => ({
    status: undefined,
    message: "",
    url: "",
    setError: (status, message, url) => set({ status, message, url }),
}));

/**
 * 프로바이더 상태관리
 */
export const useProviderStore = create<useProviderStoreType>((set) => ({
    provider: undefined,
    setProvider: (provider) => set({ provider }),
    clearProvider: () => set({ provider: undefined })
}));
