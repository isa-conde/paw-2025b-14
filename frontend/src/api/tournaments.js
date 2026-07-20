import { buildUrl, fetchJson, getIdFromLink, getLink, requestBlob, requestJson } from './client.js';
import { appendQueryParams, toPagedResult } from './pagination.js';
import { VENDOR_TYPES } from './vendorTypes.js';

export const normalizeTournament = (tournament) => ({
    ...tournament,
    gameId: getIdFromLink(getLink(tournament.links, 'game')),
    creatorId: getIdFromLink(getLink(tournament.links, 'creator')),
    formatId: getIdFromLink(getLink(tournament.links, 'format')),
    image: getLink(tournament.links, 'image'),
    participantsHref: getLink(tournament.links, 'participants'),
    matchesHref: getLink(tournament.links, 'matches'),
    rulesHref: getLink(tournament.links, 'rules'),
});

export const normalizeParticipant = (participant) => ({
    ...participant,
    userId: getIdFromLink(getLink(participant.links, 'user')),
    teamId: getIdFromLink(getLink(participant.links, 'team')),
    tournamentId: getIdFromLink(getLink(participant.links, 'tournament')),
    profilePicture: getLink(participant.links, 'profilePicture'),
});

export const normalizeMatch = (match) => ({
    ...match,
    tournamentId: getIdFromLink(getLink(match.links, 'tournament')),
    localParticipantId: getIdFromLink(getLink(match.links, 'localParticipant')),
    visitorParticipantId: getIdFromLink(getLink(match.links, 'visitorParticipant')),
});

export const normalizeMatchStage = (stage) => ({
    ...stage,
    matches: Array.isArray(stage?.matches) ? stage.matches.map(normalizeMatch) : [],
});

const resolveTournamentResource = (tournamentOrIdOrHref, rel) => {
    if (typeof tournamentOrIdOrHref === 'object' && tournamentOrIdOrHref !== null) {
        const href = getLink(tournamentOrIdOrHref.links, rel) ?? tournamentOrIdOrHref[`${rel}Href`];
        if (href) {
            return href;
        }
        return `/tournaments/${tournamentOrIdOrHref.id}/${rel}`;
    }

    const value = String(tournamentOrIdOrHref);
    if (value.startsWith('http://') || value.startsWith('https://') || value.startsWith('/')) {
        return value;
    }

    return `/tournaments/${value}/${rel}`;
};

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

export const getTournament = (tournamentId, { signal } = {}) =>
    fetchJson(`/tournaments/${tournamentId}`, {
        accept: VENDOR_TYPES.tournament,
        signal,
    })
        .then(normalizeTournament);

export const getTournamentParticipants = (tournamentOrIdOrHref, {
    signal,
    ...params
} = {}) =>
    fetchJson(appendQueryParams(resolveTournamentResource(tournamentOrIdOrHref, 'participants'), params), {
        accept: VENDOR_TYPES.participantList,
        signal,
    }).then((participants) => (
        Array.isArray(participants) ? participants.map(normalizeParticipant) : []
    ));

export const getTournamentParticipant = (tournamentId, participantId, { signal } = {}) =>
    fetchJson(`/tournaments/${tournamentId}/participants/${participantId}`, {
        accept: VENDOR_TYPES.participant,
        signal,
    }).then(normalizeParticipant);

export const getTournamentMatches = (tournamentOrIdOrHref, {
    signal,
    ...params
} = {}) =>
    fetchJson(appendQueryParams(resolveTournamentResource(tournamentOrIdOrHref, 'matches'), params), {
        accept: VENDOR_TYPES.matchStageList,
        signal,
    }).then((stages) => (
        Array.isArray(stages) ? stages.map(normalizeMatchStage) : []
    ));

export const getTournamentMatch = (tournamentId, matchId, { signal } = {}) =>
    fetchJson(`/tournaments/${tournamentId}/matches/${matchId}`, {
        accept: VENDOR_TYPES.match,
        signal,
    }).then(normalizeMatch);

export const getTournamentRulesUrl = (tournamentOrIdOrHref) =>
    buildUrl(resolveTournamentResource(tournamentOrIdOrHref, 'rules'));

export const getTournamentRules = async (tournamentOrIdOrHref, { signal } = {}) => {
    const href = resolveTournamentResource(tournamentOrIdOrHref, 'rules');
    const { data, response } = await requestBlob(href, {
        accept: 'application/pdf',
        signal,
    });

    return {
        blob: data,
        contentType: response.headers.get('Content-Type'),
        url: buildUrl(href),
    };
};

export const readFileAsBase64 = (file) => new Promise((resolve, reject) => {
    if (!file) {
        reject(new Error('File is required'));
        return;
    }

    const reader = new FileReader();
    reader.onload = () => {
        const result = typeof reader.result === 'string' ? reader.result : '';
        const separatorIndex = result.indexOf(',');
        resolve(separatorIndex >= 0 ? result.slice(separatorIndex + 1) : result);
    };
    reader.onerror = () => reject(reader.error ?? new Error('Could not read file'));
    reader.readAsDataURL(file);
});

export const createTournament = (payload, { signal } = {}) =>
    requestJson('/tournaments', {
        method: 'POST',
        accept: VENDOR_TYPES.tournament,
        contentType: VENDOR_TYPES.tournamentCreate,
        data: payload,
        signal,
    }).then(({ data, response }) => ({
        tournament: data ? normalizeTournament(data) : null,
        location: response.headers.get('Location'),
    }));

export const updateTournament = (tournamentId, payload, { signal } = {}) =>
    requestJson(`/tournaments/${tournamentId}`, {
        method: 'PUT',
        accept: VENDOR_TYPES.tournament,
        contentType: VENDOR_TYPES.tournamentUpdate,
        data: payload,
        signal,
    }).then(({ data }) => normalizeTournament(data));

export const uploadTournamentRules = async (tournament, file, { signal } = {}) => {
    const rulesBase64 = await readFileAsBase64(file);
    return updateTournament(tournament.id, {
        name: tournament.name,
        rulesBase64,
    }, { signal });
};

export const getTournamentCreator = (tournament, { signal } = {}) => {
    const link = getLink(tournament.links, 'creator');
    if (!link) {
        return Promise.resolve(null);
    }
    return fetchJson(link, {
        accept: VENDOR_TYPES.user,
        signal,
    });
};

export const getTournamentGame = (tournament, { signal } = {}) => {
    const link = getLink(tournament.links, 'game');
    if (!link) {
        return Promise.resolve(null);
    }
    return fetchJson(link, {
        accept: VENDOR_TYPES.game,
        signal,
    });
};

export const getTournamentFormat = (tournament, { signal } = {}) => {
    const link = getLink(tournament.links, 'format');
    if (!link) {
        return Promise.resolve(null);
    }
    return fetchJson(link, {
        accept: VENDOR_TYPES.gameFormat,
        signal,
    });
};

export const getGameTournaments = (game, { page = 0, signal } = {}) => {
    const href = getLink(game.links, 'tournaments') ?? game.tournamentsHref;
    return href
        ? listTournaments({ href, page, signal })
        : listTournaments({ gameId: game.id, page, signal });
};

export const joinTournamentAsUser = (tournamentId, { signal } = {}) =>
    requestJson(`/tournaments/${tournamentId}/participants/users`, {
        method: 'POST',
        contentType: VENDOR_TYPES.tournamentJoinUser,
        signal,
    }).then(({ data }) => data);

export const joinTournamentAsTeam = (tournamentId, payload, { signal } = {}) =>
    requestJson(`/tournaments/${tournamentId}/participants/teams`, {
        method: 'POST',
        contentType: VENDOR_TYPES.tournamentJoinTeam,
        data: payload,
        signal,
    }).then(({ data }) => data);

export const deleteParticipant = (tournamentId, participantId, { signal } = {}) =>
    requestJson(`/tournaments/${tournamentId}/participants/${participantId}`, {
        method: 'DELETE',
        contentType: VENDOR_TYPES.tournamentLeave,
        signal,
    }).then(({ data }) => data);

export const updateTournamentStatus = (tournamentId, payload, { signal } = {}) =>
    requestJson(`/tournaments/${tournamentId}/status`, {
        method: 'PUT',
        accept: VENDOR_TYPES.tournament,
        contentType: VENDOR_TYPES.tournamentStatus,
        data: payload,
        signal,
    }).then(({ data }) => normalizeTournament(data));

export const setMatchResults = (tournamentId, matchId, payload, { signal } = {}) =>
    requestJson(`/tournaments/${tournamentId}/matches/${matchId}/results`, {
        method: 'PUT',
        contentType: VENDOR_TYPES.matchResults,
        data: payload,
        signal,
    }).then(({ data }) => data);
