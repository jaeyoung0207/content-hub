const myCommentsMainKey = 'myComments';

export const myCommentsQueryKeys = {
  all: () => [myCommentsMainKey] as const,
  page: (userId: number, currentPage: number) =>
    [myCommentsMainKey, 'page', userId, currentPage] as const,
};
