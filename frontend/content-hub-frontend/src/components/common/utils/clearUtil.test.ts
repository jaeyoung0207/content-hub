import { describe, expect, it, vi } from "vitest";
import * as globalStateStore from "../store/globalStateStore";
import { clearUserData } from "./clearUtil";
import { waitFor } from "@testing-library/react";

describe('clearUserData', () => {
    it('유저정보 및 provider정보 클리어 확인', () => {
        // 상태 클리어 함수 모킹
        const clearUserMock = vi.fn();
        const clearProviderMock = vi.fn();
        vi.spyOn(globalStateStore, 'useUserStore').mockReturnValue({
            clearUser: clearUserMock,
        });
        vi.spyOn(globalStateStore, 'useProviderStore').mockReturnValue({
            clearProvider: clearProviderMock,
        });
        // 함수 호출
        clearUserData();
        // 검증
        waitFor(() => {
            expect(clearUserMock).toHaveBeenCalled();
            expect(clearProviderMock).toHaveBeenCalled();
        });
    });
});