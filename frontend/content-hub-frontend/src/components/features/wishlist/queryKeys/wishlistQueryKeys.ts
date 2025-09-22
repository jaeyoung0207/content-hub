import { exists } from 'i18next';

// 위시리스트 메인 쿼리 키
const wishlistMainKey = 'wishlist';

/**
 * 위시리스트 쿼리 키
 */
export const wishlistQueryKeys = {
  wishlist: {
    all: [wishlistMainKey] as const,
    add: (userId: number) => [wishlistMainKey, 'addWishlist', userId] as const,
    exists: (userId: number, contentMediaType: string, apiId: number) =>
      [
        wishlistMainKey,
        'existsWishlist',
        userId,
        contentMediaType,
        apiId,
      ] as const,
    delete: (userId: number) =>
      [wishlistMainKey, 'deleteWishlist', userId] as const,
    list: (userId: number) => ['wishlist', userId] as const,
  },
};
