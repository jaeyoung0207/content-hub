import { afterEach, describe, expect, it, vi } from "vitest";
import { NavigateFunction } from 'react-router-dom';
import { afterLoginRedirect, loginConfirmDialog, setLoginInfo } from "./loginUtil";
import { REDIRECT_URL } from "../constants/constants";
import { useConfirmDialogStore, useProviderStore, useUserStore } from "../store/globalStateStore";
import { waitFor } from "@testing-library/react";
import * as clearUtil from './clearUtil';
import { LoginUserResponseDto } from "@/api/data-contracts";
import { toast } from "react-toastify";
import * as Sentry from '@sentry/react';
import { act } from "react";

vi.mock('@sentry/react', () => ({
    setUser: vi.fn(),
}));

vi.mock('@components/common/store/globalStateStore', async () => {
    const actual = await vi.importActual('@components/common/store/globalStateStore');

    const setUserMock = vi.fn();
    const setAccessTokenMock = vi.fn();
    const setJwtMock = vi.fn()
    const setExpireDateMock = vi.fn();
    const setProviderMock = vi.fn();

    return {
        ...actual,
        useUserStore: {
            getState: vi.fn().mockReturnValue({
                setUser: setUserMock,
                setAccessToken: setAccessTokenMock,
                setJwt: setJwtMock,
                setExpireDate: setExpireDateMock,
            })
        },
        useProviderStore: {
            getState: vi.fn().mockReturnValue({
                setProvider: setProviderMock,
            }),
        },
    };
});

afterEach(() => sessionStorage.clear());

describe('afterLoginRedirect', () => {
    it('리다이렉트 URL이 있다면 해당 URL로 이동', () => {
        const navigate = vi.fn() as NavigateFunction;
        const redirectUrl = '/home/rankings';
        sessionStorage.setItem(REDIRECT_URL, redirectUrl);
        afterLoginRedirect(navigate);
        expect(navigate).toHaveBeenCalledWith(redirectUrl, { replace: true });
        expect(sessionStorage.getItem(REDIRECT_URL)).toBeNull();
    });

    it('리다이렉트 URL이 없다면 홈으로 이동', () => {
        const navigate = vi.fn() as NavigateFunction;
        sessionStorage.removeItem(REDIRECT_URL);
        afterLoginRedirect(navigate);
        expect(navigate).toHaveBeenCalledWith('/', { replace: true });
    });
});

describe('loginConfirmDialog', () => {
    it('로그인 확인 다이얼로그 설정 및 동작 확인', async () => {
        const navigate = vi.fn() as NavigateFunction;
        const message = '로그인이 필요합니다.';

        // 함수 호출
        loginConfirmDialog(message, navigate);

        const confirmDialogStore = useConfirmDialogStore.getState();
        expect(confirmDialogStore.isConfirmDialogOpen).toBe(true);
        // OK 동작 테스트
        act(() => {
            confirmDialogStore.onOk();
        });
        await waitFor(() => {
            expect(useConfirmDialogStore.getState().isConfirmDialogOpen).toBe(false); // getState()를 다시 호출하여 최신 상태를 확인
            expect(sessionStorage.getItem(REDIRECT_URL)).toBe(location.pathname + location.search);
            expect(navigate).toHaveBeenCalledWith('/login');
        });
        // Cancel 동작 테스트
        confirmDialogStore.setIsConfirmDialogOpen(true);
        act(() => {
            confirmDialogStore.onCancel();
        });
        await waitFor(() => {
            expect(useConfirmDialogStore.getState().isConfirmDialogOpen).toBe(false); // getState()를 다시 호출하여 최신 상태를 확인
        });
    });
});

describe('setLoginInfo', () => {
    // clearUserData 모킹
    const clearUserDataMock = vi.fn();
    // 콘솔 에러 및 토스트 메시지 모킹
    const consoleErrorMock = vi.spyOn(console, 'error').mockImplementation(() => { });
    const toastErrorMock = vi.spyOn(toast, 'error').mockImplementation(() => '');

    it('유저 정보 및 토큰 상태 저장 확인', async () => {
        const mockLoginInfo = {
            userInfo: {
                userId: 1,
                name: 'name',
                nickname: 'nickname',
            },
            accessToken: 'access-token',
            jwt: 'jwt-token',
            expireDate: new Date().toISOString(),
        } as LoginUserResponseDto;
        const provider = 'NAVER';

        // 함수 호출
        await setLoginInfo(mockLoginInfo, provider);
        // 검증
        await waitFor(() => {
            const userStoreState = useUserStore.getState();
            const providerStoreState = useProviderStore.getState();
            // 상태 저장 함수 호출 확인
            expect(userStoreState.setUser).toHaveBeenCalledWith(mockLoginInfo.userInfo);
            expect(userStoreState.setAccessToken).toHaveBeenCalledWith(mockLoginInfo.accessToken);
            expect(userStoreState.setJwt).toHaveBeenCalledWith(mockLoginInfo.jwt);
            expect(userStoreState.setExpireDate).toHaveBeenCalledWith(mockLoginInfo.expireDate);
            expect(providerStoreState.setProvider).toHaveBeenCalledWith(provider);
            expect(Sentry.setUser).toHaveBeenCalledWith({
                id: mockLoginInfo.userInfo!.userId,
                username: mockLoginInfo.userInfo!.nickname,
            });
        });
    });

    it('유저 정보가 없으면 유저 데이터 클리어 호출', async () => {
        vi.spyOn(clearUtil, 'clearUserData').mockImplementation(clearUserDataMock);
        const mockLoginInfo = {
            userInfo: undefined,
            accessToken: 'access-token',
            jwt: 'jwt-token',
            expireDate: new Date().toISOString(),
        } as LoginUserResponseDto;
        const provider = 'KAKAO';
        // 함수 호출
        await setLoginInfo(mockLoginInfo, provider);
        // 검증
        await waitFor(() => {
            expect(clearUserDataMock).toHaveBeenCalled();
            expect(consoleErrorMock).toHaveBeenCalledWith('로그인 정보가 불완전합니다.', {
                hasUserInfo: false,
                hasAccessToken: true,
                hasJwt: true,
                hasExpireDate: true,
                userId: undefined,
            });
            expect(toastErrorMock).toHaveBeenCalledWith('로그인 정보가 불완전합니다. 다시 로그인 해주세요.', {
                toastId: 'incompleteLoginInfo',
            });
        });
    });

    it('액세스 토큰이 없으면 유저 데이터 클리어 호출', async () => {
        vi.spyOn(clearUtil, 'clearUserData').mockImplementation(clearUserDataMock);
        const mockLoginInfo = {
            userInfo: {
                userId: 1,
                name: 'name',
                nickname: 'nickname',
            },
            accessToken: undefined,
            jwt: 'jwt-token',
            expireDate: new Date().toISOString(),
        } as LoginUserResponseDto;
        const provider = 'KAKAO';
        // 함수 호출
        await setLoginInfo(mockLoginInfo, provider);
        // 검증
        await waitFor(() => {
            expect(clearUserDataMock).toHaveBeenCalled();
            expect(consoleErrorMock).toHaveBeenCalledWith('로그인 정보가 불완전합니다.', {
                hasUserInfo: true,
                hasAccessToken: false,
                hasJwt: true,
                hasExpireDate: true,
                userId: mockLoginInfo.userInfo?.userId,
            });
            expect(toastErrorMock).toHaveBeenCalledWith('로그인 정보가 불완전합니다. 다시 로그인 해주세요.', {
                toastId: 'incompleteLoginInfo',
            });
        });
    });

    it('jwt가 없으면 유저 데이터 클리어 호출', async () => {
        vi.spyOn(clearUtil, 'clearUserData').mockImplementation(clearUserDataMock);
        const mockLoginInfo = {
            userInfo: {
                userId: 1,
                name: 'name',
                nickname: 'nickname',
            },
            accessToken: 'access-token',
            jwt: undefined,
            expireDate: new Date().toISOString(),
        } as LoginUserResponseDto;
        const provider = 'KAKAO';
        // 함수 호출
        await setLoginInfo(mockLoginInfo, provider);
        // 검증
        await waitFor(() => {
            expect(clearUserDataMock).toHaveBeenCalled();
            expect(consoleErrorMock).toHaveBeenCalledWith('로그인 정보가 불완전합니다.', {
                hasUserInfo: true,
                hasAccessToken: true,
                hasJwt: false,
                hasExpireDate: true,
                userId: mockLoginInfo.userInfo?.userId,
            });
            expect(toastErrorMock).toHaveBeenCalledWith('로그인 정보가 불완전합니다. 다시 로그인 해주세요.', {
                toastId: 'incompleteLoginInfo',
            });
        });
    });

    it('만료시각이 없으면 유저 데이터 클리어 호출', async () => {
        vi.spyOn(clearUtil, 'clearUserData').mockImplementation(clearUserDataMock);
        const mockLoginInfo = {
            userInfo: {
                userId: 1,
                name: 'name',
                nickname: 'nickname',
            },
            accessToken: 'access-token',
            jwt: 'jwt-token',
            expireDate: undefined,
        } as LoginUserResponseDto;
        const provider = 'KAKAO';
        // 함수 호출
        await setLoginInfo(mockLoginInfo, provider);
        // 검증
        await waitFor(() => {
            expect(clearUserDataMock).toHaveBeenCalled();
            expect(consoleErrorMock).toHaveBeenCalledWith('로그인 정보가 불완전합니다.', {
                hasUserInfo: true,
                hasAccessToken: true,
                hasJwt: true,
                hasExpireDate: false,
                userId: mockLoginInfo.userInfo?.userId,
            });
            expect(toastErrorMock).toHaveBeenCalledWith('로그인 정보가 불완전합니다. 다시 로그인 해주세요.', {
                toastId: 'incompleteLoginInfo',
            });
        });
    });
});