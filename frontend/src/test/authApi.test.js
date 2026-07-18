import { afterEach, describe, expect, it, vi } from 'vitest';
import { confirmVerification, resetPassword } from '../api/auth.js';
import { VENDOR_TYPES } from '../api/vendorTypes.js';

const user = {
    id: 7,
    username: 'isabel',
    verified: true,
    links: [{ rel: 'self', href: 'http://localhost/api/users/7' }],
};

const mockAuthResponse = () => vi.fn().mockResolvedValue(new Response(JSON.stringify(user), {
    status: 200,
    headers: {
        'Content-Type': VENDOR_TYPES.user,
        Authorization: 'Bearer token',
    },
}));

describe('auth API', () => {
    afterEach(() => {
        vi.unstubAllGlobals();
    });

    it('serializes password reset email tokens without losing integer precision', async () => {
        const fetchMock = mockAuthResponse();
        vi.stubGlobal('fetch', fetchMock);
        const emailToken = '9223372036854775807';

        await resetPassword({ token: emailToken, password: 'Password1' });

        const [, options] = fetchMock.mock.calls[0];
        expect(options.headers['Content-Type']).toBe(VENDOR_TYPES.passwordReset);
        expect(options.body).toContain(`"token":${emailToken}`);
        expect(JSON.parse(options.body).password).toBe('Password1');
    });

    it('serializes verification email tokens without losing integer precision', async () => {
        const fetchMock = mockAuthResponse();
        vi.stubGlobal('fetch', fetchMock);
        const emailToken = '9223372036854775807';

        await confirmVerification({ token: emailToken });

        const [, options] = fetchMock.mock.calls[0];
        expect(options.headers['Content-Type']).toBe(VENDOR_TYPES.userVerificationConfirm);
        expect(options.body).toContain(`"token":${emailToken}`);
    });
});
