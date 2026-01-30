import { fetchJson, getLink } from './client.js';
import { VENDOR_TYPES } from './vendorTypes.js';

export const getTournament = (tournamentId) =>
    fetchJson(`/tournaments/${tournamentId}`, { accept: VENDOR_TYPES.tournament });

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