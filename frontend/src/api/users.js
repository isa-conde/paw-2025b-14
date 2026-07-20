import { appendQueryParams } from './pagination.js';
import { fetchJson, getLink } from './client.js';
import { VENDOR_TYPES } from './vendorTypes.js';

export const normalizeUser = (user) => ({
    ...user,
    profilePicture: getLink(user?.links, 'profilePicture'),
    banner: getLink(user?.links, 'banner'),
    teamsHref: getLink(user?.links, 'partOfTeam'),
});

export const getUser = (userIdOrHref, { signal } = {}) =>
    fetchJson(
        String(userIdOrHref).startsWith('http') || String(userIdOrHref).startsWith('/')
            ? userIdOrHref
            : `/users/${userIdOrHref}`,
        {
            accept: VENDOR_TYPES.user,
            signal,
        },
    ).then(normalizeUser);

export const searchUsersByName = async (name, {
    page = 0,
    limit = 8,
    signal,
} = {}) => {
    const normalizedName = String(name ?? '').trim();
    if (!normalizedName) {
        return [];
    }

    const users = await fetchJson(appendQueryParams('/users', {
        name: normalizedName,
        page,
    }), {
        accept: VENDOR_TYPES.userList,
        signal,
    });

    const normalizedUsers = Array.isArray(users) ? users.map(normalizeUser) : [];
    return Number.isFinite(limit) && limit > 0
        ? normalizedUsers.slice(0, limit)
        : normalizedUsers;
};
