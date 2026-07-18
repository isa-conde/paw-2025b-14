import { fetchJson, getIdFromLink, getLink, requestJson } from './client.js';
import { appendQueryParams, toPagedResult } from './pagination.js';
import { VENDOR_TYPES } from './vendorTypes.js';

export const normalizeTournament = (tournament) => ({
    ...tournament,
    gameId: getIdFromLink(getLink(tournament.links, 'game')),
    image: getLink(tournament.links, 'image'),
});

export const listTournaments = async ({
    href,
    page,
    gameId,
    region,
    elo,
    genre,
    playersPerTeam,
    signal,
} = {}) => {
    const response = await requestJson(appendQueryParams(href ?? '/tournaments', {
        page,
        gameId,
        region,
        elo,
        genre,
        playersPerTeam,
    }), {
        accept: VENDOR_TYPES.tournamentList,
        signal,
    });

    return toPagedResult(response, page, normalizeTournament);
};

export const getTournament = (tournamentId) =>
    fetchJson(`/tournaments/${tournamentId}`, { accept: VENDOR_TYPES.tournament })
        .then(normalizeTournament);

export const getTournamentParticipants = (tournamentId) =>
    fetchJson(`/tournaments/${tournamentId}/participants`, { accept: VENDOR_TYPES.participantList });

export const getTournamentMatches = (tournamentId) =>
    fetchJson(`/tournaments/${tournamentId}/matches`, { accept: VENDOR_TYPES.matchStageList });

export const getTournamentCreator = (tournament) => {
    const link = getLink(tournament.links, 'creator');
    if (!link) {
        return Promise.resolve(null);
    }
    return fetchJson(link, { accept: VENDOR_TYPES.user });
};

export const getTournamentGame = (tournament) => {
    const link = getLink(tournament.links, 'game');
    if (!link) {
        return Promise.resolve(null);
    }
    return fetchJson(link, { accept: VENDOR_TYPES.game });
};

export const getGameTournaments = (game, { page = 0, signal } = {}) => {
    const href = getLink(game.links, 'tournaments') ?? game.tournamentsHref;
    return href
        ? listTournaments({ href, page, signal })
        : listTournaments({ gameId: game.id, page, signal });
};
