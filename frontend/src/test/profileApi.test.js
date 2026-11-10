import { beforeEach, describe, expect, it, vi } from 'vitest';
import { addUserAccount, createUserComment, deleteUserAccount, getUserComments } from '../api/profile.js';
import { listTournaments } from '../api/tournaments.js';
import { setApiToken } from '../api/client.js';
import { VENDOR_TYPES } from '../api/vendorTypes.js';

const response = (body, headers = {}) => new Response(body == null ? null : JSON.stringify(body), {
    status: body == null ? 204 : 200, headers: { 'Content-Type': 'application/json', ...headers },
});

describe('profile API', () => {
    beforeEach(() => { setApiToken('test-token'); global.fetch = vi.fn(() => Promise.resolve(response([]))); });

    it('uses vendor media types and Bearer without credentials for account mutations', async () => {
        await addUserAccount(7, { platform: 'STEAM', username: 'alice' });
        const [, options] = fetch.mock.calls[0];
        expect(options.headers.Accept).toBe(VENDOR_TYPES.userAccountList);
        expect(options.headers['Content-Type']).toBe(VENDOR_TYPES.userAccountCreate);
        expect(options.headers.Authorization).toBe('Bearer test-token');
        expect(options).not.toHaveProperty('credentials');
    });

    it('uses the comment vendor contract and Bearer token', async () => {
        await createUserComment(8, 'hello');
        const [url, options] = fetch.mock.calls[0];
        expect(url).toBe('/api/users/8/comments');
        expect(options.headers['Content-Type']).toBe(VENDOR_TYPES.commentCreate);
        expect(options.headers.Authorization).toBe('Bearer test-token');
    });

    it('parses Link pagination for comments and tournament filters', async () => {
        fetch.mockResolvedValueOnce(response([], { Link: '<http://localhost/api/users/7/comments?page=2>; rel="last", <http://localhost/api/users/7/comments?page=0>; rel="self"' }));
        const result = await getUserComments(7, { page: 0 });
        expect(result.pagination.totalPages).toBe(3);
        fetch.mockResolvedValueOnce(response([]));
        await listTournaments({ hasUser: 7, finished: false });
        expect(fetch.mock.calls[1][0]).toContain('hasUser=7');
        expect(fetch.mock.calls[1][0]).toContain('finished=false');
    });

    it('deletes by id and platform without credentials', async () => {
        fetch.mockResolvedValueOnce(response(null));
        await deleteUserAccount(7, 'RIOT_GAMES');
        expect(fetch.mock.calls[0][0]).toBe('/api/users/7/accounts/RIOT_GAMES');
        expect(fetch.mock.calls[0][1]).not.toHaveProperty('credentials');
    });
});
