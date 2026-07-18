import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';
import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import { HelmetProvider } from '@dr.pogodin/react-helmet';
import { MemoryRouter, Route, Routes, useLocation } from 'react-router-dom';
import { AuthProvider, AUTH_STORAGE_KEYS } from '../auth/AuthContext.jsx';
import { setApiToken } from '../api/client.js';
import TournamentPage from '../pages/TournamentPage.jsx';
import { VENDOR_TYPES } from '../api/vendorTypes.js';

vi.mock('react-i18next', () => ({
    useTranslation: () => ({
        t: (key, options) => options?.defaultValue ?? key,
        i18n: { language: 'en' },
    }),
}));

const tournament = {
    id: 12,
    name: 'Autumn Open',
    region: 'LAS',
    elo: 'FREE',
    startDate: '2026-08-01',
    endDate: '2026-08-10',
    format: '5v5',
    structure: 'LEAGUE',
    maxParticipants: 16,
    openInscriptions: true,
    finished: false,
    tournamentStarted: false,
    serverName: 'SA-1',
    discordChannel: 'https://discord.gg/rankup',
    links: [
        { rel: 'self', href: 'http://localhost/api/tournaments/12' },
        { rel: 'creator', href: 'http://localhost/api/users/7' },
        { rel: 'game', href: 'http://localhost/api/games/1' },
        { rel: 'participants', href: 'http://localhost/api/tournaments/12/participants' },
        { rel: 'matches', href: 'http://localhost/api/tournaments/12/matches' },
        { rel: 'rules', href: 'http://localhost/api/tournaments/12/rules' },
    ],
};

const game = {
    id: 1,
    name: 'League of Legends',
    links: [{ rel: 'self', href: 'http://localhost/api/games/1' }],
};

const creator = {
    id: 7,
    username: 'owner-user',
    verified: true,
    links: [{ rel: 'self', href: 'http://localhost/api/users/7' }],
};

const currentParticipant = {
    id: 88,
    name: 'isabel',
    points: 4,
    groupNumber: 1,
    links: [
        { rel: 'self', href: 'http://localhost/api/tournaments/12/participants/88' },
        { rel: 'user', href: 'http://localhost/api/users/42' },
        { rel: 'tournament', href: 'http://localhost/api/tournaments/12' },
    ],
};

const rivalParticipant = {
    id: 77,
    name: 'rival',
    points: 2,
    links: [
        { rel: 'self', href: 'http://localhost/api/tournaments/12/participants/77' },
        { rel: 'user', href: 'http://localhost/api/users/99' },
        { rel: 'tournament', href: 'http://localhost/api/tournaments/12' },
    ],
};

const matches = [
    {
        stage: 1,
        matches: [
            {
                id: 5,
                localScore: null,
                visitorScore: null,
                winner: null,
                stage: 1,
                groupStage: false,
                date: '2026-08-02',
                links: [
                    { rel: 'self', href: 'http://localhost/api/tournaments/12/matches/5' },
                    { rel: 'tournament', href: 'http://localhost/api/tournaments/12' },
                    { rel: 'localParticipant', href: 'http://localhost/api/tournaments/12/participants/88' },
                    { rel: 'visitorParticipant', href: 'http://localhost/api/tournaments/12/participants/77' },
                ],
            },
        ],
    },
];

const jsonResponse = (body, mediaType = 'application/json', status = 200) => new Response(JSON.stringify(body), {
    status,
    headers: { 'Content-Type': mediaType },
});

const noContentResponse = () => new Response(null, { status: 204 });

const createFetchMock = ({
    tournamentBody = tournament,
    matchesBody = matches,
    tournamentStatus = 200,
    statusResponseStatus = 200,
    authenticatedParticipant = null,
    rulesStatus = 200,
    participantsBody = [currentParticipant, rivalParticipant],
} = {}) => {
    let joined = false;
    let left = false;
    let uploadedRules = false;
    let currentTournament = tournamentBody;

    return vi.fn((url, options = {}) => {
        const requestUrl = new URL(url, 'http://localhost');
        const path = requestUrl.pathname;

        if (path === '/api/tournaments/12' && options.method === 'PUT') {
            uploadedRules = true;
            return Promise.resolve(jsonResponse(currentTournament, VENDOR_TYPES.tournament));
        }

        if (path === '/api/tournaments/12' && options.method !== 'PUT') {
            return Promise.resolve(jsonResponse(
                tournamentStatus === 200 ? currentTournament : { message: 'not found' },
                tournamentStatus === 200 ? VENDOR_TYPES.tournament : 'application/json',
                tournamentStatus,
            ));
        }

        if (path === '/api/users/7') {
            return Promise.resolve(jsonResponse(creator, VENDOR_TYPES.user));
        }

        if (path === '/api/games/1') {
            return Promise.resolve(jsonResponse(game, VENDOR_TYPES.game));
        }

        if (path === '/api/tournaments/12/participants' && options.method !== 'POST') {
            if (requestUrl.searchParams.get('userId')) {
                const selected = authenticatedParticipant ?? (joined ? [currentParticipant] : []);
                const current = left ? [] : Array.isArray(selected) ? selected : [selected];
                return Promise.resolve(jsonResponse(current, VENDOR_TYPES.participantList));
            }
            const allParticipants = left
                ? [rivalParticipant]
                : joined
                    ? [currentParticipant, rivalParticipant]
                    : participantsBody;
            return Promise.resolve(jsonResponse(allParticipants, VENDOR_TYPES.participantList));
        }

        if (path === '/api/tournaments/12/matches') {
            return Promise.resolve(jsonResponse(matchesBody, VENDOR_TYPES.matchStageList));
        }

        if (path === '/api/tournaments/12/rules') {
            if (rulesStatus === 404 && !uploadedRules) {
                return Promise.resolve(jsonResponse({ message: 'rules not found' }, 'application/json', 404));
            }
            return Promise.resolve(new Response(new Blob(['pdf'], { type: 'application/pdf' }), {
                status: 200,
                headers: { 'Content-Type': 'application/pdf' },
            }));
        }

        if (path === '/api/tournaments/12/participants/users' && options.method === 'POST') {
            joined = true;
            return Promise.resolve(noContentResponse());
        }

        if (path === '/api/tournaments/12/participants/88' && options.method === 'DELETE') {
            left = true;
            return Promise.resolve(noContentResponse());
        }

        if (path === '/api/tournaments/12/status' && options.method === 'PUT') {
            if (statusResponseStatus !== 200) {
                return Promise.resolve(jsonResponse({ message: 'status error' }, 'application/json', statusResponseStatus));
            }
            const payload = JSON.parse(options.body ?? '{}');
            currentTournament = {
                ...currentTournament,
                ...(payload.openInscriptions === false ? { openInscriptions: false } : {}),
                ...(payload.tournamentStarted === true ? { tournamentStarted: true } : {}),
            };
            return Promise.resolve(jsonResponse(currentTournament, VENDOR_TYPES.tournament));
        }

        if (path === '/api/tournaments/12/matches/5/results' && options.method === 'PUT') {
            return Promise.resolve(noContentResponse());
        }

        return Promise.resolve(jsonResponse({ message: 'unexpected' }, 'application/json', 500));
    });
};

const LocationProbe = () => {
    const location = useLocation();
    return <div data-testid="location">{location.pathname}{location.search}</div>;
};

const authenticate = (user = { id: 42, username: 'isabel', verified: true }) => {
    localStorage.setItem(AUTH_STORAGE_KEYS.token, 'Bearer test-token');
    localStorage.setItem(AUTH_STORAGE_KEYS.user, JSON.stringify(user));
};

const renderTournamentPage = (initialEntry = '/tournaments/12') => render(
    <HelmetProvider>
        <MemoryRouter initialEntries={[initialEntry]}>
            <AuthProvider>
                <Routes>
                    <Route path="/tournaments/:tournamentId" element={<><TournamentPage /><LocationProbe /></>} />
                    <Route path="/login" element={<><div>Login</div><LocationProbe /></>} />
                    <Route path="/" element={<div>Home</div>} />
                </Routes>
            </AuthProvider>
        </MemoryRouter>
    </HelmetProvider>
);

const requestUrls = (fetchMock) => fetchMock.mock.calls.map(([url]) => new URL(url, 'http://localhost'));

describe('TournamentPage', () => {
    beforeEach(() => {
        localStorage.clear();
        setApiToken(null);
    });

    afterEach(() => {
        localStorage.clear();
        setApiToken(null);
        vi.unstubAllGlobals();
    });

    it('loads tournament detail with API data', async () => {
        vi.stubGlobal('fetch', createFetchMock());

        renderTournamentPage();

        expect(await screen.findByRole('heading', { name: 'Autumn Open' })).toBeInTheDocument();
        expect(screen.getAllByText('League of Legends').length).toBeGreaterThan(0);
        expect(screen.getAllByText('owner-user').length).toBeGreaterThan(0);
        expect(screen.getByText('SA-1')).toBeInTheDocument();
    });

    it('shows NotFound when the tournament API returns 404', async () => {
        vi.stubGlobal('fetch', createFetchMock({ tournamentStatus: 404 }));

        renderTournamentPage();

        expect(await screen.findByText('error404Page.title')).toBeInTheDocument();
    });

    it('shows Forbidden when the tournament API returns 403', async () => {
        vi.stubGlobal('fetch', createFetchMock({ tournamentStatus: 403 }));

        renderTournamentPage();

        expect(await screen.findByText('error403Page.pageTitle')).toBeInTheDocument();
    });

    it('reads and writes the tab in the URL without reloading the route', async () => {
        vi.stubGlobal('fetch', createFetchMock());

        renderTournamentPage('/tournaments/12?tab=matches');

        expect(await screen.findByRole('heading', { name: 'tournament.matches' })).toBeInTheDocument();
        fireEvent.click(screen.getByRole('button', { name: 'tournament.participants.title' }));

        await waitFor(() => {
            expect(screen.getByTestId('location')).toHaveTextContent('/tournaments/12?tab=participants');
        });
        expect(screen.getByRole('heading', { name: 'tournament.participants.title' })).toBeInTheDocument();
    });

    it('detects the authenticated participant with userId and keeps participant id separate', async () => {
        authenticate();
        const fetchMock = createFetchMock({ authenticatedParticipant: currentParticipant });
        vi.stubGlobal('fetch', fetchMock);

        renderTournamentPage('/tournaments/12?tab=participants');

        expect(await screen.findByRole('button', { name: 'tournament.leave.butText' })).toBeInTheDocument();
        await waitFor(() => {
            expect(requestUrls(fetchMock).some((url) => (
                url.pathname === '/api/tournaments/12/participants'
                && url.searchParams.get('userId') === '42'
            ))).toBe(true);
        });
        expect(currentParticipant.id).not.toBe(42);
    });

    it('joins through the users participant endpoint and never calls /me', async () => {
        authenticate();
        const fetchMock = createFetchMock({
            authenticatedParticipant: [],
            participantsBody: [rivalParticipant],
        });
        vi.stubGlobal('fetch', fetchMock);

        renderTournamentPage();

        fireEvent.click(await screen.findByRole('button', { name: 'tournament.join.button' }));

        await waitFor(() => {
            expect(fetchMock.mock.calls.some(([url, options]) => {
                const requestUrl = new URL(url, 'http://localhost');
                return requestUrl.pathname === '/api/tournaments/12/participants/users'
                    && options.method === 'POST';
            })).toBe(true);
        });
        expect(requestUrls(fetchMock).every((url) => !url.pathname.includes('/me'))).toBe(true);
    });

    it('leaves through the real participant id and never calls /me', async () => {
        authenticate();
        const fetchMock = createFetchMock({ authenticatedParticipant: currentParticipant });
        vi.stubGlobal('fetch', fetchMock);

        renderTournamentPage();

        fireEvent.click(await screen.findByRole('button', { name: 'tournament.leave.butText' }));

        await waitFor(() => {
            expect(fetchMock.mock.calls.some(([url, options]) => {
                const requestUrl = new URL(url, 'http://localhost');
                return requestUrl.pathname === '/api/tournaments/12/participants/88'
                    && options.method === 'DELETE';
            })).toBe(true);
        });
        expect(requestUrls(fetchMock).every((url) => !url.pathname.includes('/me'))).toBe(true);
    });

    it('keeps the page usable when rules are missing', async () => {
        vi.stubGlobal('fetch', createFetchMock({ rulesStatus: 404 }));

        renderTournamentPage('/tournaments/12?tab=rules');

        expect(await screen.findByText('tournamentDetail.rules.empty')).toBeInTheDocument();
        expect(screen.getByRole('heading', { name: 'Autumn Open' })).toBeInTheDocument();
    });

    it('lets the owner upload rules when none are available', async () => {
        authenticate({ id: 7, username: 'owner-user', verified: true });
        const fetchMock = createFetchMock({ rulesStatus: 404, authenticatedParticipant: [] });
        vi.stubGlobal('fetch', fetchMock);

        renderTournamentPage('/tournaments/12?tab=rules');

        expect(await screen.findByText('tournamentDetail.rules.empty')).toBeInTheDocument();
        const file = new File(['pdf'], 'rules.pdf', { type: 'application/pdf' });
        fireEvent.change(screen.getByLabelText('tournamentDetail.rules.selectFile'), {
            target: { files: [file] },
        });
        fireEvent.click(screen.getByRole('button', { name: 'tournamentDetail.rules.upload' }));

        await waitFor(() => {
            expect(fetchMock.mock.calls.some(([url, options]) => {
                const requestUrl = new URL(url, 'http://localhost');
                return requestUrl.pathname === '/api/tournaments/12'
                    && options.method === 'PUT';
            })).toBe(true);
        });

        const [, updateOptions] = fetchMock.mock.calls.find(([url, options]) => {
            const requestUrl = new URL(url, 'http://localhost');
            return requestUrl.pathname === '/api/tournaments/12'
                && options.method === 'PUT';
        });
        expect(updateOptions.headers['Content-Type']).toBe(VENDOR_TYPES.tournamentUpdate);
        expect(JSON.parse(updateOptions.body)).toEqual({
            name: 'Autumn Open',
            rulesBase64: 'cGRm',
        });
        expect(await screen.findByText('tournamentDetail.rules.uploadSuccess')).toBeInTheDocument();
        await waitFor(() => {
            expect(fetchMock.mock.calls.filter(([url]) => (
                new URL(url, 'http://localhost').pathname === '/api/tournaments/12/rules'
            )).length).toBeGreaterThanOrEqual(2);
        });
    });

    it('shows owner controls only to the tournament creator', async () => {
        authenticate({ id: 7, username: 'owner-user', verified: true });
        vi.stubGlobal('fetch', createFetchMock());
        const ownerRender = renderTournamentPage();

        expect(await screen.findByRole('button', { name: 'tournament.closeInscriptions' })).toBeInTheDocument();
        expect(screen.queryByRole('button', { name: 'tournament.startTournament' })).not.toBeInTheDocument();
        ownerRender.unmount();

        vi.stubGlobal('fetch', createFetchMock({
            tournamentBody: {
                ...tournament,
                openInscriptions: false,
            },
            authenticatedParticipant: [],
        }));
        const closedOwnerRender = renderTournamentPage();

        expect(await screen.findByRole('button', { name: 'tournament.startTournament' })).toBeInTheDocument();
        expect(screen.queryByRole('button', { name: 'tournament.closeInscriptions' })).not.toBeInTheDocument();
        closedOwnerRender.unmount();

        localStorage.clear();
        setApiToken(null);
        authenticate({ id: 42, username: 'isabel', verified: true });
        vi.stubGlobal('fetch', createFetchMock({ authenticatedParticipant: [] }));
        renderTournamentPage();

        await screen.findByRole('heading', { name: 'Autumn Open' });
        expect(screen.queryByRole('button', { name: 'tournament.closeInscriptions' })).not.toBeInTheDocument();
        expect(screen.queryByRole('button', { name: 'tournament.startTournament' })).not.toBeInTheDocument();
    });

    it('closes inscriptions through status and refreshes matches', async () => {
        authenticate({ id: 7, username: 'owner-user', verified: true });
        const fetchMock = createFetchMock({ authenticatedParticipant: [] });
        vi.stubGlobal('fetch', fetchMock);

        renderTournamentPage();

        fireEvent.click(await screen.findByRole('button', { name: 'tournament.closeInscriptions' }));

        await waitFor(() => {
            const statusCall = fetchMock.mock.calls.find(([url, options]) => (
                new URL(url, 'http://localhost').pathname === '/api/tournaments/12/status'
                && options.method === 'PUT'
            ));
            expect(statusCall).toBeTruthy();
            expect(JSON.parse(statusCall[1].body)).toEqual({ openInscriptions: false });
        });
        await waitFor(() => {
            expect(fetchMock.mock.calls.filter(([url]) => (
                new URL(url, 'http://localhost').pathname === '/api/tournaments/12/matches'
            )).length).toBeGreaterThanOrEqual(2);
        });
    });

    it('starts a closed tournament through status and refreshes matches', async () => {
        authenticate({ id: 7, username: 'owner-user', verified: true });
        const fetchMock = createFetchMock({
            tournamentBody: {
                ...tournament,
                openInscriptions: false,
            },
            authenticatedParticipant: [],
        });
        vi.stubGlobal('fetch', fetchMock);

        renderTournamentPage();

        fireEvent.click(await screen.findByRole('button', { name: 'tournament.startTournament' }));

        await waitFor(() => {
            const statusCall = fetchMock.mock.calls.find(([url, options]) => (
                new URL(url, 'http://localhost').pathname === '/api/tournaments/12/status'
                && options.method === 'PUT'
            ));
            expect(statusCall).toBeTruthy();
            expect(JSON.parse(statusCall[1].body)).toEqual({ tournamentStarted: true });
        });
        await waitFor(() => {
            expect(fetchMock.mock.calls.filter(([url]) => (
                new URL(url, 'http://localhost').pathname === '/api/tournaments/12/matches'
            )).length).toBeGreaterThanOrEqual(2);
        });
    });

    it('shows the pending-start empty state for a closed hybrid group stage', async () => {
        vi.stubGlobal('fetch', createFetchMock({
            tournamentBody: {
                ...tournament,
                structure: 'HYBRID',
                openInscriptions: false,
                groupStage: true,
            },
            matchesBody: [],
            authenticatedParticipant: [],
        }));

        renderTournamentPage('/tournaments/12?tab=matches');

        expect(await screen.findByText('tournamentDetail.matches.pendingStart')).toBeInTheDocument();
    });

    it('sets match results through the real endpoint', async () => {
        authenticate({ id: 7, username: 'owner-user', verified: true });
        const fetchMock = createFetchMock({
            tournamentBody: {
                ...tournament,
                openInscriptions: false,
                tournamentStarted: true,
            },
            authenticatedParticipant: [],
        });
        vi.stubGlobal('fetch', fetchMock);

        renderTournamentPage('/tournaments/12?tab=matches');

        fireEvent.change(await screen.findByLabelText('tournamentDetail.results.localScoreFor'), {
            target: { value: '2' },
        });
        fireEvent.change(screen.getByLabelText('tournamentDetail.results.visitorScoreFor'), {
            target: { value: '1' },
        });
        fireEvent.click(screen.getByRole('button', { name: 'tournament.setMatchResults.set' }));

        await waitFor(() => {
            const resultsCall = fetchMock.mock.calls.find(([url, options]) => (
                new URL(url, 'http://localhost').pathname === '/api/tournaments/12/matches/5/results'
                && options.method === 'PUT'
            ));
            expect(resultsCall).toBeTruthy();
            expect(JSON.parse(resultsCall[1].body)).toEqual({ localScore: 2, visitorScore: 1 });
        });
    });

    it('shows status errors without breaking the page', async () => {
        authenticate({ id: 7, username: 'owner-user', verified: true });
        vi.stubGlobal('fetch', createFetchMock({
            authenticatedParticipant: [],
            statusResponseStatus: 409,
        }));

        renderTournamentPage();

        fireEvent.click(await screen.findByRole('button', { name: 'tournament.closeInscriptions' }));

        expect(await screen.findByRole('alert')).toHaveTextContent('tournamentDetail.error.conflict');
        expect(screen.getByRole('heading', { name: 'Autumn Open' })).toBeInTheDocument();
    });

    it('validates elimination ties before submitting results', async () => {
        authenticate({ id: 7, username: 'owner-user', verified: true });
        const fetchMock = createFetchMock({
            tournamentBody: {
                ...tournament,
                structure: 'ELIMINATION',
                openInscriptions: false,
                tournamentStarted: true,
            },
            authenticatedParticipant: [],
        });
        vi.stubGlobal('fetch', fetchMock);

        renderTournamentPage('/tournaments/12?tab=matches');

        fireEvent.change(await screen.findByLabelText('tournamentDetail.results.localScoreFor'), {
            target: { value: '1' },
        });
        fireEvent.change(screen.getByLabelText('tournamentDetail.results.visitorScoreFor'), {
            target: { value: '1' },
        });
        fireEvent.click(screen.getByRole('button', { name: 'tournament.setMatchResults.set' }));

        expect(await screen.findByRole('alert')).toHaveTextContent('setMatchResultsForm.noTieOnEliminationConstraint');
        expect(fetchMock.mock.calls.some(([url]) => (
            new URL(url, 'http://localhost').pathname === '/api/tournaments/12/matches/5/results'
        ))).toBe(false);
    });
});
