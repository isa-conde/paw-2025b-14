import { beforeEach, describe, expect, it, vi } from 'vitest';
import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import { HelmetProvider } from '@dr.pogodin/react-helmet';
import { MemoryRouter, Route, Routes, useLocation } from 'react-router-dom';
import { AuthProvider, AUTH_STORAGE_KEYS } from '../auth/AuthContext.jsx';
import { ProfilePage } from '../pages/ProfilePage.jsx';

vi.mock('react-i18next', () => ({ useTranslation: () => ({ t: (key) => key, i18n: { language: 'en' } }) }));

const owner = { id: 7, username: 'owner', verified: true, bio: 'bio', rating: null, ratingCount: 0, links: [] };
const visitor = { id: 8, username: 'visitor', verified: true, links: [] };
const json = (body, headers = {}) => Promise.resolve(new Response(JSON.stringify(body), { status: 200, headers: { 'Content-Type': 'application/json', ...headers } }));

const mockApi = () => vi.fn((url) => {
    const parsed = new URL(url, 'http://localhost');
    if (parsed.pathname === '/api/users/7') return json(owner);
    if (parsed.pathname === '/api/users/8') return json(visitor);
    if (parsed.pathname === '/api/users/7/accounts') return json([]);
    if (parsed.pathname === '/api/users/7/comments') return json([]);
    if (parsed.pathname === '/api/games') return json([]);
    if (parsed.pathname === '/api/tournaments') return json([]);
    if (parsed.pathname === '/api/teams') return json([]);
    return json({ message: `Unexpected ${parsed.pathname}` });
});

const Probe = () => <output data-testid="location">{useLocation().pathname}{useLocation().search}</output>;
const renderPage = (entry) => render(<HelmetProvider><MemoryRouter initialEntries={[entry]}><AuthProvider><Routes>
    <Route path="/profile" element={<><ProfilePage/><Probe/></>}/>
    <Route path="/users/:userId" element={<><ProfilePage/><Probe/></>}/>
    <Route path="/teams/new" element={<div>new team</div>}/>
</Routes></AuthProvider></MemoryRouter></HelmetProvider>);

describe('ProfilePage', () => {
    beforeEach(() => { localStorage.clear(); global.fetch = mockApi(); });

    it('/profile uses the AuthContext user id, loads overview resources and never /users/me', async () => {
        localStorage.setItem(AUTH_STORAGE_KEYS.token, 'test-token');
        localStorage.setItem(AUTH_STORAGE_KEYS.user, JSON.stringify(owner));
        renderPage('/profile');
        expect(await screen.findByRole('heading', { name: 'owner' })).toBeInTheDocument();
        await waitFor(() => expect(fetch.mock.calls.some(([url]) => String(url).includes('/users/7/accounts'))).toBe(true));
        const urls = fetch.mock.calls.map(([url]) => String(url));
        expect(urls.some((url) => url.includes('favouritedBy=7'))).toBe(true);
        expect(urls.some((url) => url.includes('hasUser=7'))).toBe(true);
        expect(urls.every((url) => !url.includes('/users/me'))).toBe(true);
        expect(screen.getByText('profile.modal.addusername')).toBeInTheDocument();
    });

    it('loads a public user, hides private accounts, and keeps tabs/subtabs in the URL', async () => {
        renderPage('/users/7?tab=tournaments&section=finished&page1=0&page2=0');
        expect(await screen.findByRole('heading', { name: 'owner' })).toBeInTheDocument();
        expect(screen.queryByText('profile.modal.addusername')).not.toBeInTheDocument();
        expect(screen.getByTestId('location')).toHaveTextContent('tab=tournaments&section=finished&page1=0&page2=0');
        fireEvent.click(screen.getByRole('button', { name: 'profile.tournaments.created' }));
        expect(screen.getByTestId('location')).toHaveTextContent('section=created');
        fireEvent.click(await screen.findByRole('button', { name: 'profile.navbar.teams' }));
        await waitFor(() => expect(screen.getByTestId('location')).toHaveTextContent('tab=teams'));
        await waitFor(() => expect(fetch.mock.calls.some(([url]) => String(url).includes('/teams?userId=7'))).toBe(true));
    });
});


describe('ProfilePage regression cases', () => {
    beforeEach(() => { localStorage.clear(); global.fetch = mockApi(); });
    const signIn = (user) => {
        localStorage.setItem(AUTH_STORAGE_KEYS.token, 'test-token');
        localStorage.setItem(AUTH_STORAGE_KEYS.user, JSON.stringify(user));
    };
    const failure = (status) => Promise.resolve(new Response(JSON.stringify({ status }), { status, headers: { 'Content-Type': 'application/json' } }));

    it('shows initial failures and allows retry instead of loading forever', async () => {
        fetch.mockImplementationOnce(() => failure(500));
        renderPage('/users/7');
        expect(await screen.findByRole('alert')).toHaveTextContent('profile.error');
        expect(screen.queryByText('profile.loading')).not.toBeInTheDocument();
        fireEvent.click(screen.getByRole('button', { name: 'profile.retry' }));
        expect(await screen.findByRole('heading', { name: 'owner' })).toBeInTheDocument();
    });

    it('requests separate active and finished created tournaments', async () => {
        renderPage('/users/7?tab=tournaments&section=created&page1=2&page2=1');
        await screen.findByRole('heading', { name: 'owner' });
        const queries = fetch.mock.calls.map(([url]) => new URL(url, 'http://localhost')).filter((url) => url.pathname === '/api/tournaments');
        expect(queries.some((url) => url.searchParams.get('createdBy') === '7' && url.searchParams.get('finished') === 'false' && url.searchParams.get('page') === '2')).toBe(true);
        expect(queries.some((url) => url.searchParams.get('finished') === 'true' && url.searchParams.get('page') === '1')).toBe(true);
    });

    it('keeps account input after a conflict, uses the returned accounts and advances the platform', async () => {
        signIn(owner);
        const base = mockApi();
        let attempts = 0;
        global.fetch = vi.fn((url, options = {}) => {
            if (options.method === 'POST') {
                attempts++;
                return attempts === 1 ? failure(409) : json([{ platform: 'DISCORD', username: 'saved-name' }]);
            }
            if (options.method === 'DELETE') return failure(403);
            return base(url, options);
        });
        renderPage('/profile');
        const input = await screen.findByRole('textbox', { name: 'profile.modal.username' });
        fireEvent.change(input, { target: { value: 'new-name' } });
        fireEvent.click(screen.getByRole('button', { name: 'profile.modal.addusername' }));
        expect(await screen.findByRole('alert')).toHaveTextContent('profile.errors.409');
        expect(input).toHaveValue('new-name');
        fireEvent.click(screen.getByRole('button', { name: 'profile.modal.addusername' }));
        expect(await screen.findByText('DISCORD: saved-name')).toBeInTheDocument();
        expect(screen.getByRole('combobox')).toHaveValue('STEAM');
        expect(input).toHaveValue('');
        fireEvent.click(screen.getByRole('button', { name: 'profile.removeAccount' }));
        expect(await screen.findByRole('alert')).toHaveTextContent('profile.errors.403');
        expect(screen.getByText('DISCORD: saved-name')).toBeInTheDocument();
    });

    it('keeps a failed comment for retry and prevents duplicate submissions', async () => {
        signIn(visitor);
        const base = mockApi();
        let finish;
        global.fetch = vi.fn((url, options = {}) => options.method === 'POST'
            ? new Promise((resolve) => { finish = resolve; }) : base(url, options));
        renderPage('/users/7');
        const input = await screen.findByRole('textbox', { name: 'profile.comments.placeholder' });
        fireEvent.change(input, { target: { value: 'hello' } });
        const submit = screen.getByRole('button', { name: 'profile.comments.submit' });
        fireEvent.click(submit);
        expect(submit).toBeDisabled();
        fireEvent.click(submit);
        expect(fetch.mock.calls.filter(([, options]) => options?.method === 'POST')).toHaveLength(1);
        finish(await failure(500));
        expect(await screen.findByRole('alert')).toBeInTheDocument();
        expect(input).toHaveValue('hello');
        expect(submit).not.toBeDisabled();
    });
});
