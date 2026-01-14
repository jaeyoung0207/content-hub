import { beforeEach, describe, expect, it } from "vitest";
import { clearUserData } from "./clearUtil";
import { waitFor } from "@testing-library/react";
import { useProviderStore, useUserStore } from "../store/globalStateStore";

describe('clearUserData', () => {
    // 초기 상태 설정
    const userInfo = { userId: 1, nickname: 'TestUser' };
    const accessToken = 'access-token';
    const jwt = 'jwt';
    const expireDate = new Date().toISOString();
    const provider = 'NAVER';

    beforeEach(() => {
        // 초기 상태 설정
        useUserStore.getState().setUser(userInfo);
        useUserStore.getState().setAccessToken(accessToken);
        useUserStore.getState().setJwt(jwt);
        useUserStore.getState().setExpireDate(expireDate);
        useProviderStore.getState().setProvider('NAVER');
    });

    it('유저정보 및 provider정보 클리어 확인', async () => {
        // 초기 상태 검증
        expect(useUserStore.getState().user).toBe(userInfo)
        expect(useUserStore.getState().accessToken).toBe(accessToken);
        expect(useUserStore.getState().jwt).toBe(jwt);
        expect(useUserStore.getState().expireDate).toBe(expireDate);
        expect(useProviderStore.getState().provider).toBe(provider);

        // 함수 호출
        clearUserData();
        // 검증
        await waitFor(() => {
            expect(useUserStore.getState().user).toBeNull();
            expect(useUserStore.getState().accessToken).toBeNull();
            expect(useUserStore.getState().jwt).toBeNull();
            expect(useUserStore.getState().expireDate).toBeNull();
            expect(useProviderStore.getState().provider).toBeUndefined();
        });
    });
});