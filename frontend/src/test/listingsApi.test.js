import { afterEach, describe, expect, it, vi } from 'vitest';
import { listGames, getGameFormats } from '../api/games.js';
import { listTournaments } from '../api/tournaments.js';
import { VENDOR_TYPES } from '../api/vendorTypes.js';

const okList = (body = [], mediaType = 'application/json') => new Response(JSON.stringify(body), {
    status: 200,
    headers: {
        'Content-Type': mediaType,
    },
});

describe('listing API modules', () => {
    afterEach(() => {
        vi.unstubAllGlobals();
    });

    it('sends the games list vendor Accept header', async () => {
        const fetchMock = vi.fn().mockResolvedValue(okList([], VENDOR_TYPES.gameList));
        vi.stubGlobal('fetch', fetchMock);

        await listGames({page: 0});

        const [url, options] = fetchMock.mock.calls[0];
        expect(url).toBe('/api/games?page=0');
        expect(options.headers.Accept).toBe(VENDOR_TYPES.gameList);
        expect(options.credentials).toBeUndefined();
    });

    it('sends the tournaments list vendor Accept header with filters', async () => {
        const fetchMock = vi.fn().mockResolvedValue(okList([], VENDOR_TYPES.tournamentList));
        vi.stubGlobal('fetch', fetchMock);

        await listTournaments({
            page: 2,
            gameId: 1,
            region: 'LAS',
            elo: 'FREE',
            genre: 'MOBA',
            playersPerTeam: 5,
        });

        const [url, options] = fetchMock.mock.calls[0];
        const requestUrl = new URL(url, 'http://localhost');

        expect(requestUrl.pathname).toBe('/api/tournaments');
        expect(requestUrl.searchParams.get('page')).toBe('2');
        expect(requestUrl.searchParams.get('gameId')).toBe('1');
        expect(requestUrl.searchParams.get('region')).toBe('LAS');
        expect(requestUrl.searchParams.get('elo')).toBe('FREE');
        expect(requestUrl.searchParams.get('genre')).toBe('MOBA');
        expect(requestUrl.searchParams.get('playersPerTeam')).toBe('5');
        expect(options.headers.Accept).toBe(VENDOR_TYPES.tournamentList);
        expect(options.credentials).toBeUndefined();
    });

    it('sends the game formats list vendor Accept header', async () => {
        const fetchMock = vi.fn().mockResolvedValue(okList([], VENDOR_TYPES.gameFormatList));
        vi.stubGlobal('fetch', fetchMock);

        await getGameFormats(3);

        const [url, options] = fetchMock.mock.calls[0];
        expect(url).toBe('/api/games/3/formats');
        expect(options.headers.Accept).toBe(VENDOR_TYPES.gameFormatList);
    });
});
