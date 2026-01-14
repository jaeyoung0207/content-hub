import { describe, expect, it, vi } from "vitest";
import { NavigateFunction } from 'react-router-dom';
import { afterLoginRedirect, loginConfirmDialog, setLoginInfo } from "./loginUtil";
import { REDIRECT_URL } from "../constants/constants";
import { useConfirmDialogStore } from "../store/globalStateStore";
import * as globalStateStore from "../store/globalStateStore";
import { waitFor } from "@testing-library/react";
import * as clearUtil from './clearUtil';
import { LoginUserResponseDto } from "@/api/data-contracts";
import { toast } from "react-toastify";
import * as Sentry from '@sentry/react';

vi.mock('@sentry/react', () => ({
  setUser: vi.fn(),
}));

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
    it('로그인 확인 다이얼로그 설정 및 동작 확인', () => {
        const navigate = vi.fn() as NavigateFunction;
        const message = '로그인이 필요합니다.';
        loginConfirmDialog(message, navigate);

        const confirmDialogStore = useConfirmDialogStore.getState();
        expect(confirmDialogStore.isConfirmDialogOpen).toBe(true);
        // OK 동작 테스트
        confirmDialogStore.onOk();
        waitFor(() => {
            expect(confirmDialogStore.isConfirmDialogOpen).toBe(false);
            expect(sessionStorage.getItem(REDIRECT_URL)).toBe(location.pathname + location.search);
            expect(navigate).toHaveBeenCalledWith('/login');
        });
        // Cancel 동작 테스트
        confirmDialogStore.setIsConfirmDialogOpen(true);
        confirmDialogStore.onCancel();
        waitFor(() => {
            expect(confirmDialogStore.isConfirmDialogOpen).toBe(false);
        });
    });
});

describe('setLoginInfo', () => {
    // clearUserData 모킹
    const clearUserDataMock = vi.fn();
    // 콘솔 에러 및 토스트 메시지 모킹
    const consoleErrorMock = vi.spyOn(console, 'error').mockImplementation(() => { });
    const toastErrorMock = vi.spyOn(toast, 'error').mockImplementation(() => '');

    it('유저 정보 및 토큰 상태 저장 확인', () => {
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

        // 상태 저장 함수 모킹
        const setUserMock = vi.fn();
        const setAccessTokenMock = vi.fn();
        const setJwtMock = vi.fn();
        const setExpireDateMock = vi.fn();
        const setProviderMock = vi.fn();
        vi.spyOn(globalStateStore, 'useUserStore').mockReturnValue({
            setUser: setUserMock,
            setAccessToken: setAccessTokenMock,
            setJwt: setJwtMock,
            setExpireDate: setExpireDateMock,
        });
        vi.spyOn(globalStateStore, 'useProviderStore').mockReturnValue({
            setProvider: setProviderMock,
        });
        // 함수 호출
        setLoginInfo(mockLoginInfo, provider);
        // 검증
        waitFor(() => {
            // 상태 저장 함수 호출 확인
            expect(setUserMock).toHaveBeenCalledWith(mockLoginInfo.userInfo);
            expect(setAccessTokenMock).toHaveBeenCalledWith(mockLoginInfo.accessToken);
            expect(setJwtMock).toHaveBeenCalledWith(mockLoginInfo.jwt);
            expect(setExpireDateMock).toHaveBeenCalledWith(mockLoginInfo.expireDate);
            expect(setProviderMock).toHaveBeenCalledWith(provider);
            expect(Sentry.setUser).toHaveBeenCalledWith({
                id: mockLoginInfo.userInfo!.userId,
                username: mockLoginInfo.userInfo!.nickname,
            });
        });
    });

    it('유저 정보가 없으면 유저 데이터 클리어 호출', () => {
        vi.spyOn(clearUtil, 'clearUserData').mockImplementation(clearUserDataMock);
        const mockLoginInfo = {
            userInfo: undefined,
            accessToken: 'access-token',
            jwt: 'jwt-token',
            expireDate: new Date().toISOString(),
        } as LoginUserResponseDto;
        const provider = 'KAKAO';
        // 함수 호출
        setLoginInfo(mockLoginInfo, provider);
        // 검증
        waitFor(() => {
            expect(clearUserDataMock).toHaveBeenCalled();
            expect(consoleErrorMock).toHaveBeenCalledWith('로그인 정보가 불완전합니다: ', mockLoginInfo);
            expect(toastErrorMock).toHaveBeenCalledWith('로그인 정보가 불완전합니다. 다시 로그인 해주세요.', {
                toastId: 'incompleteLoginInfo',
            });
        });
    });

    it('액세스 토큰이 없으면 유저 데이터 클리어 호출', () => {
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
        setLoginInfo(mockLoginInfo, provider);
        // 검증
        waitFor(() => {
            expect(clearUserDataMock).toHaveBeenCalled();
            expect(consoleErrorMock).toHaveBeenCalledWith('로그인 정보가 불완전합니다: ', mockLoginInfo);
            expect(toastErrorMock).toHaveBeenCalledWith('로그인 정보가 불완전합니다. 다시 로그인 해주세요.', {
                toastId: 'incompleteLoginInfo',
            });
        });
    });

    it('jwt가 없으면 유저 데이터 클리어 호출', () => {
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
        setLoginInfo(mockLoginInfo, provider);
        // 검증
        waitFor(() => {
            expect(clearUserDataMock).toHaveBeenCalled();
            expect(consoleErrorMock).toHaveBeenCalledWith('로그인 정보가 불완전합니다: ', mockLoginInfo);
            expect(toastErrorMock).toHaveBeenCalledWith('로그인 정보가 불완전합니다. 다시 로그인 해주세요.', {
                toastId: 'incompleteLoginInfo',
            });
        });
    });

    it('만료시각이 없으면 유저 데이터 클리어 호출', () => {        
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
        setLoginInfo(mockLoginInfo, provider);
        // 검증
        waitFor(() => {
            expect(clearUserDataMock).toHaveBeenCalled();
            expect(consoleErrorMock).toHaveBeenCalledWith('로그인 정보가 불완전합니다: ', mockLoginInfo);
            expect(toastErrorMock).toHaveBeenCalledWith('로그인 정보가 불완전합니다. 다시 로그인 해주세요.', {
                toastId: 'incompleteLoginInfo',
            });
        });
    });
});