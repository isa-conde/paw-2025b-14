import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';
import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import { HelmetProvider } from '@dr.pogodin/react-helmet';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import { AuthProvider, AUTH_STORAGE_KEYS } from '../auth/AuthContext.jsx';
import { LoginPage } from '../pages/LoginPage.jsx';

vi.mock('react-i18next', () => ({
    useTranslation: () => ({
        t: (key, options) => options?.defaultValue ?? key,
    }),
}));

const user = {
    id: 7,
    username: 'isabel',
    verified: true,
    links: [{ rel: 'self', href: 'http://localhost/api/users/7' }],
};

const renderLoginPage = () => render(
    <HelmetProvider>
        <MemoryRouter initialEntries={['/login']}>
            <AuthProvider>
                <Routes>
                    <Route path="/login" element={<LoginPage />} />
                    <Route path="/" element={<div>Home</div>} />
                </Routes>
            </AuthProvider>
        </MemoryRouter>
    </HelmetProvider>
);

const submitLogin = () => {
    fireEvent.change(screen.getByLabelText('login.username'), {
        target: { value: 'isabel' },
    });
    fireEvent.change(screen.getByLabelText('login.password'), {
        target: { value: 'secret' },
    });
    fireEvent.click(screen.getByDisplayValue('login.title'));
};

describe('LoginPage', () => {
    beforeEach(() => {
        localStorage.clear();
    });

    afterEach(() => {
        vi.unstubAllGlobals();
    });

    it('stores token and user after a successful login', async () => {
        const fetchMock = vi.fn().mockResolvedValue(new Response(JSON.stringify(user), {
            status: 200,
            headers: {
                'Content-Type': 'application/vnd.user.v1+json',
                Authorization: 'Bearer login-token',
            },
        }));
        vi.stubGlobal('fetch', fetchMock);

        renderLoginPage();
        submitLogin();

        await waitFor(() => {
            expect(localStorage.getItem(AUTH_STORAGE_KEYS.token)).toBe('Bearer login-token');
        });
        expect(JSON.parse(localStorage.getItem(AUTH_STORAGE_KEYS.user))).toEqual(user);
        await waitFor(() => {
            expect(screen.getByText('Home')).toBeInTheDocument();
        });

        const [, options] = fetchMock.mock.calls[0];
        expect(options.credentials).toBeUndefined();
        expect(options.headers.Accept).toBe('application/vnd.user.v1+json');
        expect(options.headers['Content-Type']).toBe('application/vnd.user.login.v1+json');
    });

    it('shows a 401 error and does not authenticate', async () => {
        vi.stubGlobal('fetch', vi.fn().mockResolvedValue(new Response(JSON.stringify({}), {
            status: 401,
            headers: { 'Content-Type': 'application/json' },
        })));

        renderLoginPage();
        submitLogin();

        expect(await screen.findByRole('alert')).toHaveTextContent('login.invalidCredentials');
        expect(localStorage.getItem(AUTH_STORAGE_KEYS.token)).toBeNull();
        expect(localStorage.getItem(AUTH_STORAGE_KEYS.user)).toBeNull();
    });
});
