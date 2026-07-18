import { afterEach, describe, expect, it, vi } from 'vitest';
import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import { HelmetProvider } from '@dr.pogodin/react-helmet';
import { MemoryRouter, Route, Routes, useLocation } from 'react-router-dom';
import { AuthProvider } from '../auth/AuthContext.jsx';
import { GamesPage } from '../pages/GamesPage.jsx';
import { TournamentsPage } from '../pages/TournamentsPage.jsx';
import { VENDOR_TYPES } from '../api/vendorTypes.js';

vi.mock('react-i18next', () => ({
    useTranslation: () => ({
        t: (key) => key,
    }),
}));

const games = [
    {
        id: 1,
        name: 'League of Legends',
        genre: 'MOBA',
        links: [
            { rel: 'self', href: 'http://localhost/api/games/1' },
            { rel: 'tournaments', href: 'http://localhost/api/tournaments?gameId=1' },
            { rel: 'image', href: 'http://localhost/api/images/10' },
        ],
    },
    {
        id: 2,
        name: 'Valorant',
        genre: 'FPS',
        links: [
            { rel: 'self', href: 'http://localhost/api/games/2' },
            { rel: 'tournaments', href: 'http://localhost/api/tournaments?gameId=2' },
        ],
    },
];

const tournaments = [
    {
        id: 7,
        name: 'Summer Cup',
        tournamentStarted: false,
        finished: false,
        links: [
            { rel: 'self', href: 'http://localhost/api/tournaments/7' },
            { rel: 'game', href: 'http://localhost/api/games/1' },
        ],
    },
];

const pageLink = (resource, page, rel, params = {}) => {
    const url = new URL(`http://localhost/api/${resource}`);
    Object.entries(params).forEach(([key, value]) => {
        url.searchParams.set(key, value);
    });
    url.searchParams.set('page', page);
    return `<${url.toString()}>; rel="${rel}"`;
};

const paginationHeader = (resource, currentPage, lastPage, params = {}) => [
    pageLink(resource, currentPage, 'self', params),
    pageLink(resource, 0, 'first', params),
    pageLink(resource, lastPage, 'last', params),
    currentPage > 0 ? pageLink(resource, currentPage - 1, 'prev', params) : null,
    currentPage < lastPage ? pageLink(resource, currentPage + 1, 'next', params) : null,
].filter(Boolean).join(', ');

const jsonResponse = (body, mediaType, linkHeader) => new Response(JSON.stringify(body), {
    status: 200,
    headers: {
        'Content-Type': mediaType,
        ...(linkHeader ? { Link: linkHeader } : {}),
    },
});

const errorResponse = () => new Response(JSON.stringify({message: 'boom'}), {
    status: 500,
    headers: {'Content-Type': 'application/json'},
});

const createFetchMock = ({
    gamesBody = games,
    tournamentsBody = tournaments,
    gamesStatus = 'ok',
    tournamentsStatus = 'ok',
    gamesLastPage = 0,
    tournamentsLastPage = 0,
} = {}) => vi.fn((url) => {
    const requestUrl = new URL(url, 'http://localhost');
    const page = Number.parseInt(requestUrl.searchParams.get('page') ?? '0', 10);

    if (requestUrl.pathname === '/api/games') {
        if (gamesStatus === 'pending') {
            return new Promise(() => {});
        }
        if (gamesStatus === 'error') {
            return Promise.resolve(errorResponse());
        }
        return Promise.resolve(jsonResponse(
            gamesBody,
            VENDOR_TYPES.gameList,
            paginationHeader('games', page, gamesLastPage),
        ));
    }

    if (requestUrl.pathname === '/api/tournaments') {
        if (tournamentsStatus === 'error') {
            return Promise.resolve(errorResponse());
        }
        const params = {};
        ['gameId', 'region', 'elo', 'genre', 'playersPerTeam'].forEach((key) => {
            const value = requestUrl.searchParams.get(key);
            if (value) {
                params[key] = value;
            }
        });
        return Promise.resolve(jsonResponse(
            tournamentsBody,
            VENDOR_TYPES.tournamentList,
            paginationHeader('tournaments', page, tournamentsLastPage, params),
        ));
    }

    return Promise.resolve(jsonResponse({}, 'application/json'));
});

const LocationProbe = () => {
    const location = useLocation();
    return <div data-testid="location">{location.pathname}{location.search}</div>;
};

const renderListingsRoute = (initialEntry) => render(
    <HelmetProvider>
        <MemoryRouter initialEntries={[initialEntry]}>
            <AuthProvider>
                <Routes>
                    <Route path="/games" element={<><GamesPage /><LocationProbe /></>} />
                    <Route path="/tournaments" element={<><TournamentsPage /><LocationProbe /></>} />
                    <Route path="/tournaments/:id" element={<div>Tournament detail</div>} />
                </Routes>
            </AuthProvider>
        </MemoryRouter>
    </HelmetProvider>
);

const tournamentFetchCalls = (fetchMock) => fetchMock.mock.calls
    .map(([url]) => new URL(url, 'http://localhost'))
    .filter((url) => url.pathname === '/api/tournaments');

describe('listing pages', () => {
    afterEach(() => {
        localStorage.clear();
        vi.unstubAllGlobals();
    });

    it('TournamentsPage applies gameId from the URL', async () => {
        const fetchMock = createFetchMock();
        vi.stubGlobal('fetch', fetchMock);

        renderListingsRoute('/tournaments?gameId=1');

        await waitFor(() => {
            expect(tournamentFetchCalls(fetchMock).some((url) => (
                url.searchParams.get('gameId') === '1'
            ))).toBe(true);
        });
        expect(await screen.findByLabelText('tournaments.game')).toHaveValue('1');
    });

    it('updates query params when filters change', async () => {
        vi.stubGlobal('fetch', createFetchMock());

        renderListingsRoute('/tournaments?page=2');

        await screen.findByRole('option', {name: 'League of Legends'});
        fireEvent.change(screen.getByLabelText('tournaments.region'), {
            target: {value: 'LAS'},
        });

        await waitFor(() => {
            expect(screen.getByTestId('location')).toHaveTextContent('/tournaments?region=LAS');
        });
    });

    it('preserves filters when paginating tournaments', async () => {
        const fetchMock = createFetchMock({tournamentsLastPage: 1});
        vi.stubGlobal('fetch', fetchMock);

        renderListingsRoute('/tournaments?gameId=1&page=0');

        fireEvent.click(await screen.findByRole('link', {name: '2'}));

        await waitFor(() => {
            expect(screen.getByTestId('location')).toHaveTextContent('/tournaments?gameId=1&page=1');
        });
        await waitFor(() => {
            expect(tournamentFetchCalls(fetchMock).some((url) => (
                url.searchParams.get('gameId') === '1' && url.searchParams.get('page') === '1'
            ))).toBe(true);
        });
    });

    it('GamesPage renders API data and links games to tournament filters', async () => {
        vi.stubGlobal('fetch', createFetchMock());

        renderListingsRoute('/games');

        const gameTitle = await screen.findByText('League of Legends');
        expect(gameTitle.closest('a')).toHaveAttribute('href', '/tournaments?gameId=1');
    });

    it('GamesPage shows loading, empty, and error states', async () => {
        vi.stubGlobal('fetch', createFetchMock({gamesStatus: 'pending'}));
        const loadingRender = renderListingsRoute('/games');
        expect(screen.getByText('games.loading')).toBeInTheDocument();
        loadingRender.unmount();

        vi.stubGlobal('fetch', createFetchMock({gamesBody: []}));
        const emptyRender = renderListingsRoute('/games');
        expect(await screen.findByText('elementGrid.noGames')).toBeInTheDocument();
        emptyRender.unmount();

        vi.stubGlobal('fetch', createFetchMock({gamesStatus: 'error'}));
        renderListingsRoute('/games');
        expect(await screen.findByRole('alert')).toHaveTextContent('games.error');
    });

    it('TournamentsPage shows empty and error states', async () => {
        vi.stubGlobal('fetch', createFetchMock({tournamentsBody: []}));
        const emptyRender = renderListingsRoute('/tournaments');
        expect(await screen.findByText('elementGrid.noTournaments')).toBeInTheDocument();
        emptyRender.unmount();

        vi.stubGlobal('fetch', createFetchMock({tournamentsStatus: 'error'}));
        renderListingsRoute('/tournaments');
        expect(await screen.findByRole('alert')).toHaveTextContent('tournaments.error');
    });

    it('TournamentsPage still renders tournaments if the games filter API fails', async () => {
        vi.stubGlobal('fetch', createFetchMock({gamesStatus: 'error'}));

        renderListingsRoute('/tournaments');

        expect(await screen.findByText('Summer Cup')).toBeInTheDocument();
        expect(screen.queryByRole('alert')).not.toBeInTheDocument();
        expect(screen.getByLabelText('tournaments.game')).toBeDisabled();
    });
});
