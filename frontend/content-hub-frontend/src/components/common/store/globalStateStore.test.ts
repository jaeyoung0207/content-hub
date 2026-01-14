import { describe, it, expect, beforeEach, vi } from 'vitest';
import {
  useUserStore,
  useSearchTypeStore,
  useConfirmDialogStore,
  useProviderStore,
  useTooltipStore,
  useContentMediaTypeMapStore,
  useDisplayMediaTypeMapStore,
  useLoginDoneStore,
} from './globalStateStore';
import {
  AppContentMediaTypeDto,
  AppDisplayMediaTypeDto,
  LoginUserInfoDto,
} from '@/api/data-contracts';

describe('useUserStore', () => {
  beforeEach(() => useUserStore.setState({ 
    user: null,
    accessToken: null,
    jwt: null,
    expireDate: null,
  }));
  it('초기값은 user: null', () => {
    expect(useUserStore.getState().user).toBeNull();
  });
  it('setUser로 user 값이 된다', () => {
    const dummyUser = { userId: 1, name: '홍길동' } as LoginUserInfoDto;
    useUserStore.getState().setUser(dummyUser);
    expect(useUserStore.getState().user).toEqual(dummyUser);
  });
  it('setAccessToken로 accessToken 값이 된다', () => {
    useUserStore.getState().setAccessToken('access-token');
    expect(useUserStore.getState().accessToken).toBe('access-token');
  });
  it('setJwt로 jwt 값이 된다', () => {
    useUserStore.getState().setJwt('jwt-token');
    expect(useUserStore.getState().jwt).toBe('jwt-token');
  });
  it('setExpireDate로 expireDate 값이 된다', () => {
    const expireDate = new Date().toISOString();
    useUserStore.getState().setExpireDate(expireDate);
    expect(useUserStore.getState().expireDate).toBe(expireDate);
  });
  it('clearUser로 user 값이 null', () => {
    useUserStore.getState().setUser({ userId: 1 } as LoginUserInfoDto);
    useUserStore.getState().clearUser();
    expect(useUserStore.getState().user).toBeNull();
    expect(useUserStore.getState().accessToken).toBeNull();
    expect(useUserStore.getState().jwt).toBeNull();
    expect(useUserStore.getState().expireDate).toBeNull();
  });
});

describe('useSearchTypeStore', () => {
  beforeEach(() =>
    useSearchTypeStore.setState({
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
    })
  );
  it('초기값은 모든 flag가 true', () => {
    const state = useSearchTypeStore.getState().searchTypeState;
    Object.values(state).forEach((v) => expect(v).toBe(true));
  });
  it('setSearchTypeState로 값이 변경됨', () => {
    useSearchTypeStore
      .getState()
      .setSearchTypeState(
        false,
        false,
        false,
        false,
        false,
        false,
        false,
        false
      );
    const state = useSearchTypeStore.getState().searchTypeState;
    Object.values(state).forEach((v) => expect(v).toBe(false));
  });
});

describe('useConfirmDialogStore', () => {
  beforeEach(() =>
    useConfirmDialogStore.setState({
      isConfirmDialogOpen: false,
      onOk: () => {},
      onCancel: () => {},
      title: '',
      confirmMsg: '',
    })
  );
  it('초기값 확인', () => {
    const state = useConfirmDialogStore.getState();
    expect(state.isConfirmDialogOpen).toBe(false);
    expect(state.title).toBe('');
    expect(state.confirmMsg).toBe('');
  });
  it('setIsConfirmDialogOpen로 값이 변경됨', () => {
    useConfirmDialogStore.getState().setIsConfirmDialogOpen(true);
    expect(useConfirmDialogStore.getState().isConfirmDialogOpen).toBe(true);
  });
  it('setTitle, setConfirmMsg로 값이 변경됨', () => {
    useConfirmDialogStore.getState().setTitle('타이틀');
    useConfirmDialogStore.getState().setConfirmMsg('메시지');
    expect(useConfirmDialogStore.getState().title).toBe('타이틀');
    expect(useConfirmDialogStore.getState().confirmMsg).toBe('메시지');
  });
  it('setOnOk/setOnCancel로 콜백이 변경되고 실행됨', () => {
    const okMock = vi.fn();
    const cancelMock = vi.fn();
    useConfirmDialogStore.getState().setOnOk(okMock);
    useConfirmDialogStore.getState().setOnCancel(cancelMock);
    useConfirmDialogStore.getState().onOk();
    useConfirmDialogStore.getState().onCancel();
    expect(okMock).toHaveBeenCalled();
    expect(cancelMock).toHaveBeenCalled();
  });
});

describe('useProviderStore', () => {
  beforeEach(() => useProviderStore.setState({ provider: undefined }));
  it('초기값은 provider: undefined', () => {
    expect(useProviderStore.getState().provider).toBeUndefined();
  });
  it('setProvider/clearProvider로 값이 변경됨', () => {
    useProviderStore.getState().setProvider('NAVER');
    expect(useProviderStore.getState().provider).toBe('NAVER');
    useProviderStore.getState().clearProvider();
    expect(useProviderStore.getState().provider).toBeUndefined();
  });
});

describe('useTooltipStore', () => {
  beforeEach(() => useTooltipStore.setState({ isTooltipOpen: 0 }));
  it('초기값은 isTooltipOpen: 0', () => {
    expect(useTooltipStore.getState().isTooltipOpen).toBe(0);
  });
  it('setIsTooltipOpen로 값이 변경됨', () => {
    useTooltipStore.getState().setIsTooltipOpen(2);
    expect(useTooltipStore.getState().isTooltipOpen).toBe(2);
  });
});

describe('useContentMediaTypeMapStore', () => {
  beforeEach(() =>
    useContentMediaTypeMapStore.setState({
      contentMediaType: undefined,
      isContentMediaTypeInitialized: false,
    })
  );
  it('초기값 확인', () => {
    const state = useContentMediaTypeMapStore.getState();
    expect(state.contentMediaType).toBeUndefined();
    expect(state.isContentMediaTypeInitialized).toBe(false);
  });
  it('setContentMediaType/clearContentMediaType로 값이 변경됨', () => {
    const dummy = { aniCode: '1101' } as AppContentMediaTypeDto;
    useContentMediaTypeMapStore.getState().setContentMediaType(dummy, true);
    expect(useContentMediaTypeMapStore.getState().contentMediaType).toEqual(
      dummy
    );
    expect(
      useContentMediaTypeMapStore.getState().isContentMediaTypeInitialized
    ).toBe(true);
    useContentMediaTypeMapStore.getState().clearContentMediaType();
    expect(
      useContentMediaTypeMapStore.getState().contentMediaType
    ).toBeUndefined();
    expect(
      useContentMediaTypeMapStore.getState().isContentMediaTypeInitialized
    ).toBe(false);
  });
});

describe('useDisplayMediaTypeMapStore', () => {
  beforeEach(() =>
    useDisplayMediaTypeMapStore.setState({
      displayMediaType: undefined,
      isDisplayMediaTypeInitialized: false,
    })
  );
  it('초기값 확인', () => {
    const state = useDisplayMediaTypeMapStore.getState();
    expect(state.displayMediaType).toBeUndefined();
    expect(state.isDisplayMediaTypeInitialized).toBe(false);
  });
  it('setDisplayMediaType/clearDisplayMediaType로 값이 변경됨', () => {
    const dummy = { aniCode: '1' } as AppDisplayMediaTypeDto;
    useDisplayMediaTypeMapStore.getState().setDisplayMediaType(dummy, true);
    expect(useDisplayMediaTypeMapStore.getState().displayMediaType).toEqual(
      dummy
    );
    expect(
      useDisplayMediaTypeMapStore.getState().isDisplayMediaTypeInitialized
    ).toBe(true);
    useDisplayMediaTypeMapStore.getState().clearDisplayMediaType();
    expect(
      useDisplayMediaTypeMapStore.getState().displayMediaType
    ).toBeUndefined();
    expect(
      useDisplayMediaTypeMapStore.getState().isDisplayMediaTypeInitialized
    ).toBe(false);
  });
});

describe('useLoginDoneStore', () => {
  beforeEach(() => useLoginDoneStore.setState({ isLoginDone: false }));
  it('초기값은 isLoginDone: false', () => {
    expect(useLoginDoneStore.getState().isLoginDone).toBe(false);
  });
  it('setIsLoginDone로 값이 변경됨', () => {
    useLoginDoneStore.getState().setIsLoginDone(true);
    expect(useLoginDoneStore.getState().isLoginDone).toBe(true);
  });
});
