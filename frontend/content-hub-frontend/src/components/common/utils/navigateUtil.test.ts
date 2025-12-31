import { expect, vi, describe, it } from "vitest";
import { navigateToDetailPage } from './navigateUtil';
import * as urlUtil from './urlUtil';
import * as checkUtil from './checkUtil';

describe('navigateToDetailPage', () => {
    it('상세화면 이동', () => {
        const mockNavigate = vi.fn();
        const mockApiId = 24428;
        const mockContentMediaType = '1201';
        const mockTabNo = 0;
        const mockDetailUrl = '/detail/' + mockContentMediaType + '/' + mockApiId + '?tabNo=' + mockTabNo;
        
        vi.spyOn(checkUtil, 'checkApiId').mockReturnValue(true);
        vi.spyOn(urlUtil, 'detailUrlQuery').mockReturnValue(mockDetailUrl);

        navigateToDetailPage(mockNavigate, mockApiId, mockContentMediaType, mockTabNo);

        expect(checkUtil.checkApiId).toHaveBeenCalledWith(mockApiId);
        expect(urlUtil.detailUrlQuery).toHaveBeenCalledWith({
            contentMediaType: mockContentMediaType,
            apiId: String(mockApiId),
            tabNo: mockTabNo,
        });
        expect(mockNavigate).toHaveBeenCalledWith(mockDetailUrl);
    });

    it('apiId가 유효하지 않은 경우 처리 종료', () => {
        const mockNavigate = vi.fn();
        const mockApiId = undefined;
        const mockContentMediaType = 'movie';
        vi.spyOn(checkUtil, 'checkApiId').mockReturnValue(false);

        navigateToDetailPage(mockNavigate, mockApiId, mockContentMediaType);
        expect(checkUtil.checkApiId).toHaveBeenCalledWith(mockApiId);
        expect(mockNavigate).not.toHaveBeenCalled();
    });
});