import { fetchJson, getLink, requestJson } from './client.js';
import { appendQueryParams, toPagedResult } from './pagination.js';
import { VENDOR_TYPES } from './vendorTypes.js';

export const normalizeGame = (game) => ({
    ...game,
    image: getLink(game.links, 'image'),
    tournamentsHref: getLink(game.links, 'tournaments'),
    formatsHref: getLink(game.links, 'formats'),
});

export const listGames = async ({ page, href, signal } = {}) => {
    const response = await requestJson(appendQueryParams(href ?? '/games', { page }), {
        accept: VENDOR_TYPES.gameList,
        signal,
    });

    return toPagedResult(response, page, normalizeGame);
};

export const listAllGames = async ({ signal } = {}) => {
    const firstPage = await listGames({ page: 0, signal });
    const games = [...firstPage.items];
    let nextHref = firstPage.pagination.links.next;

    while (nextHref) {
        const nextPage = await listGames({ href: nextHref, signal });
        games.push(...nextPage.items);
        nextHref = nextPage.pagination.links.next;
    }

    return games;
};

export const getGame = (gameId, { signal } = {}) =>
    fetchJson(`/games/${gameId}`, {
        accept: VENDOR_TYPES.game,
        signal,
    }).then(normalizeGame);

export const getGameFormats = (gameOrId, { signal } = {}) => {
    const href = typeof gameOrId === 'object'
        ? getLink(gameOrId.links, 'formats') ?? gameOrId.formatsHref
        : `/games/${gameOrId}/formats`;

    return fetchJson(href, {
        accept: VENDOR_TYPES.gameFormatList,
        signal,
    });
};

export const getGameFormat = (gameId, formatId, { signal } = {}) =>
    fetchJson(`/games/${gameId}/formats/${formatId}`, {
        accept: VENDOR_TYPES.gameFormat,
        signal,
    });
