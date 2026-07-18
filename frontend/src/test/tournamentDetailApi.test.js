import { afterEach, describe, expect, it, vi } from 'vitest';
import { setApiToken } from '../api/client.js';
import {
    deleteParticipant,
    getTournament,
    getTournamentMatches,
    getTournamentParticipants,
    getTournamentRules,
    joinTournamentAsUser,
    setMatchResults,
    uploadTournamentRules,
    updateTournamentStatus,
} from '../api/tournaments.js';
import { VENDOR_TYPES } from '../api/vendorTypes.js';

const tournament = {
    id: 12,
    name: 'Autumn Open',
    links: [
        { rel: 'self', href: 'http://localhost/api/tournaments/12' },
        { rel: 'creator', href: 'http://localhost/api/users/7' },
        { rel: 'game', href: 'http://localhost/api/games/1' },
        { rel: 'participants', href: 'http://localhost/api/tournaments/12/participants' },
        { rel: 'matches', href: 'http://localhost/api/tournaments/12/matches' },
        { rel: 'rules', href: 'http://localhost/api/tournaments/12/rules' },
    ],
};

const jsonResponse = (body, mediaType = 'application/json') => new Response(JSON.stringify(body), {
    status: 200,
    headers: { 'Content-Type': mediaType },
});

const noContentResponse = () => new Response(null, { status: 204 });

describe('tournament detail API module', () => {
    afterEach(() => {
        setApiToken(null);
        vi.unstubAllGlobals();
    });

    it('sends vendor Accept headers for tournament detail resources', async () => {
        const fetchMock = vi.fn((url) => {
            const requestUrl = new URL(url, 'http://localhost');
            if (requestUrl.pathname === '/api/tournaments/12') {
                return Promise.resolve(jsonResponse(tournament, VENDOR_TYPES.tournament));
            }
            if (requestUrl.pathname === '/api/tournaments/12/participants') {
                return Promise.resolve(jsonResponse([], VENDOR_TYPES.participantList));
            }
            if (requestUrl.pathname === '/api/tournaments/12/matches') {
                return Promise.resolve(jsonResponse([], VENDOR_TYPES.matchStageList));
            }
            if (requestUrl.pathname === '/api/tournaments/12/rules') {
                return Promise.resolve(new Response(new Blob(['pdf'], { type: 'application/pdf' }), {
                    status: 200,
                    headers: { 'Content-Type': 'application/pdf' },
                }));
            }
            return Promise.resolve(jsonResponse({}));
        });
        vi.stubGlobal('fetch', fetchMock);

        await getTournament(12);
        await getTournamentParticipants(12, { userId: 42 });
        await getTournamentMatches(12, { group: 2 });
        await getTournamentRules(12);

        const calls = fetchMock.mock.calls.map(([url, options]) => ({
            url: new URL(url, 'http://localhost'),
            options,
        }));

        expect(calls[0].url.pathname).toBe('/api/tournaments/12');
        expect(calls[0].options.headers.Accept).toBe(VENDOR_TYPES.tournament);
        expect(calls[0].options.credentials).toBeUndefined();

        expect(calls[1].url.pathname).toBe('/api/tournaments/12/participants');
        expect(calls[1].url.searchParams.get('userId')).toBe('42');
        expect(calls[1].options.headers.Accept).toBe(VENDOR_TYPES.participantList);

        expect(calls[2].url.pathname).toBe('/api/tournaments/12/matches');
        expect(calls[2].url.searchParams.get('group')).toBe('2');
        expect(calls[2].options.headers.Accept).toBe(VENDOR_TYPES.matchStageList);

        expect(calls[3].url.pathname).toBe('/api/tournaments/12/rules');
        expect(calls[3].options.headers.Accept).toBe('application/pdf');
    });

    it('uses current REST endpoints and vendor content types for actions', async () => {
        const fetchMock = vi.fn((url, options = {}) => {
            const requestUrl = new URL(url, 'http://localhost');
            if (requestUrl.pathname === '/api/tournaments/12/status') {
                return Promise.resolve(jsonResponse(tournament, VENDOR_TYPES.tournament));
            }
            if (requestUrl.pathname === '/api/tournaments/12' && options.method === 'PUT') {
                return Promise.resolve(jsonResponse(tournament, VENDOR_TYPES.tournament));
            }
            if (
                requestUrl.pathname === '/api/tournaments/12/participants/users'
                || requestUrl.pathname === '/api/tournaments/12/participants/88'
                || requestUrl.pathname === '/api/tournaments/12/matches/5/results'
            ) {
                return Promise.resolve(noContentResponse());
            }
            return Promise.resolve(jsonResponse({ message: 'unexpected' }, 'application/json'));
        });
        vi.stubGlobal('fetch', fetchMock);

        await joinTournamentAsUser(12);
        await deleteParticipant(12, 88);
        await updateTournamentStatus(12, { openInscriptions: false });
        await setMatchResults(12, 5, { localScore: 2, visitorScore: 1 });
        await uploadTournamentRules(tournament, new File(['pdf'], 'rules.pdf', { type: 'application/pdf' }));

        const calls = fetchMock.mock.calls.map(([url, options]) => ({
            url: new URL(url, 'http://localhost'),
            options,
        }));

        expect(calls[0].url.pathname).toBe('/api/tournaments/12/participants/users');
        expect(calls[0].options.method).toBe('POST');
        expect(calls[0].options.headers['Content-Type']).toBe(VENDOR_TYPES.tournamentJoinUser);

        expect(calls[1].url.pathname).toBe('/api/tournaments/12/participants/88');
        expect(calls[1].options.method).toBe('DELETE');
        expect(calls[1].options.headers['Content-Type']).toBe(VENDOR_TYPES.tournamentLeave);

        expect(calls[2].url.pathname).toBe('/api/tournaments/12/status');
        expect(calls[2].options.method).toBe('PUT');
        expect(calls[2].options.headers.Accept).toBe(VENDOR_TYPES.tournament);
        expect(calls[2].options.headers['Content-Type']).toBe(VENDOR_TYPES.tournamentStatus);

        expect(calls[3].url.pathname).toBe('/api/tournaments/12/matches/5/results');
        expect(calls[3].options.method).toBe('PUT');
        expect(calls[3].options.headers['Content-Type']).toBe(VENDOR_TYPES.matchResults);

        expect(calls[4].url.pathname).toBe('/api/tournaments/12');
        expect(calls[4].options.method).toBe('PUT');
        expect(calls[4].options.headers.Accept).toBe(VENDOR_TYPES.tournament);
        expect(calls[4].options.headers['Content-Type']).toBe(VENDOR_TYPES.tournamentUpdate);
        expect(JSON.parse(calls[4].options.body)).toEqual({
            name: 'Autumn Open',
            rulesBase64: 'cGRm',
        });
        expect(calls.every((call) => !call.url.pathname.includes('/me'))).toBe(true);
        expect(calls.every((call) => call.options.credentials === undefined)).toBe(true);
    });
});
