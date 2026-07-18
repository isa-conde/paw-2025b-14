import { afterEach, describe, expect, it, vi } from 'vitest';
import { requestJson, setApiToken } from '../api/client.js';

describe('api client', () => {
    afterEach(() => {
        setApiToken(null);
        vi.unstubAllGlobals();
    });

    it('adds Authorization and does not send credentials', async () => {
        const fetchMock = vi.fn().mockResolvedValue(new Response(JSON.stringify({ ok: true }), {
            status: 200,
            headers: { 'Content-Type': 'application/json' },
        }));
        vi.stubGlobal('fetch', fetchMock);
        setApiToken('test-token');

        await requestJson('/protected');

        expect(fetchMock).toHaveBeenCalledTimes(1);
        const [url, options] = fetchMock.mock.calls[0];

        expect(url).toBe('/api/protected');
        expect(options.credentials).toBeUndefined();
        expect(options.headers.Accept).toBe('application/json');
        expect(options.headers.Authorization).toBe('Bearer test-token');
    });
});
