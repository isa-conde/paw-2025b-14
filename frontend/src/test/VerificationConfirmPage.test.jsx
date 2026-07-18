import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import { HelmetProvider } from '@dr.pogodin/react-helmet';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import { AuthProvider, AUTH_STORAGE_KEYS } from '../auth/AuthContext.jsx';
import { VerificationConfirmPage } from '../pages/VerificationConfirmPage.jsx';
import { VENDOR_TYPES } from '../api/vendorTypes.js';

vi.mock('react-i18next', () => ({
    useTranslation: () => ({
        t: (key, options) => options?.defaultValue ?? key,
    }),
}));

const user = {
    id: 11,
    username: 'new-user',
    verified: true,
    links: [{ rel: 'self', href: 'http://localhost/api/users/11' }],
};

const renderVerificationConfirmPage = (emailToken) => render(
    <HelmetProvider>
        <MemoryRouter initialEntries={[`/verify/confirm?token=${emailToken}`]}>
            <AuthProvider>
                <Routes>
                    <Route path="/verify/confirm" element={<VerificationConfirmPage />} />
                </Routes>
            </AuthProvider>
        </MemoryRouter>
    </HelmetProvider>
);

describe('VerificationConfirmPage', () => {
    beforeEach(() => {
        localStorage.clear();
    });

    afterEach(() => {
        vi.unstubAllGlobals();
    });

    it('confirms the emailed token and stores auth state', async () => {
        const fetchMock = vi.fn().mockResolvedValue(new Response(JSON.stringify(user), {
            status: 200,
            headers: {
                'Content-Type': VENDOR_TYPES.user,
                Authorization: 'Bearer verified-token',
            },
        }));
        vi.stubGlobal('fetch', fetchMock);
        const emailToken = '9223372036854775807';

        renderVerificationConfirmPage(emailToken);

        await screen.findByText('verification.successful.title');
        expect(localStorage.getItem(AUTH_STORAGE_KEYS.token)).toBe('Bearer verified-token');
        expect(JSON.parse(localStorage.getItem(AUTH_STORAGE_KEYS.user))).toEqual(user);

        await waitFor(() => {
            expect(fetchMock).toHaveBeenCalledTimes(1);
        });
        const [, options] = fetchMock.mock.calls[0];
        expect(options.headers['Content-Type']).toBe(VENDOR_TYPES.userVerificationConfirm);
        expect(options.body).toContain(`"token":${emailToken}`);
    });
});
