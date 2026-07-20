import { appendQueryParams } from './pagination.js';
import { fetchJson, getIdFromLink, getLink, requestJson } from './client.js';
import { VENDOR_TYPES } from './vendorTypes.js';
import { getUser, normalizeUser } from './users.js';

export const normalizeTeam = (team) => ({
    ...team,
    ownerId: getIdFromLink(getLink(team?.links, 'owner')),
    ownerHref: getLink(team?.links, 'owner'),
    profilePicture: getLink(team?.links, 'profilePicture'),
    banner: getLink(team?.links, 'banner'),
    selfHref: getLink(team?.links, 'self'),
});

export const listTeams = ({ signal, ...params } = {}) =>
    fetchJson(appendQueryParams('/teams', params), {
        accept: VENDOR_TYPES.teamList,
        signal,
    }).then((teams) => (
        Array.isArray(teams) ? teams.map(normalizeTeam) : []
    ));

export const getTeam = (teamIdOrHref, { signal } = {}) =>
    fetchJson(
        String(teamIdOrHref).startsWith('http') || String(teamIdOrHref).startsWith('/')
            ? teamIdOrHref
            : `/teams/${teamIdOrHref}`,
        {
            accept: VENDOR_TYPES.team,
            signal,
        },
    ).then(normalizeTeam);

export const getTeamMembers = (teamId, { signal } = {}) =>
    fetchJson(`/teams/${teamId}/members`, {
        accept: VENDOR_TYPES.userList,
        signal,
    }).then((users) => (
        Array.isArray(users) ? users.map(normalizeUser) : []
    ));

export const getTeamOwner = (team, { signal } = {}) => (
    team?.ownerHref ? getUser(team.ownerHref, { signal }) : Promise.resolve(null)
);

export const createTeam = (payload, { signal } = {}) =>
    requestJson('/teams', {
        method: 'POST',
        accept: VENDOR_TYPES.team,
        contentType: VENDOR_TYPES.teamCreate,
        data: payload,
        signal,
    }).then(({ data, response }) => ({
        team: data ? normalizeTeam(data) : null,
        location: response.headers.get('Location'),
    }));

export const updateTeam = (teamId, payload, { signal } = {}) =>
    requestJson(`/teams/${teamId}`, {
        method: 'PUT',
        accept: VENDOR_TYPES.team,
        contentType: VENDOR_TYPES.teamUpdate,
        data: payload,
        signal,
    }).then(({ data }) => normalizeTeam(data));
