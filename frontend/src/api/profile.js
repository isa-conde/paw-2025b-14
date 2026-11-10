import { appendQueryParams, toPagedResult } from './pagination.js';
import { fetchJson, getLink, requestJson } from './client.js';
import { VENDOR_TYPES } from './vendorTypes.js';
import { getUser, normalizeUser } from './users.js';

export const getUserAccounts = (userId, { signal } = {}) =>
    fetchJson(`/users/${userId}/accounts`, { accept: VENDOR_TYPES.userAccountList, signal });

export const addUserAccount = (userId, account, { signal } = {}) =>
    requestJson(`/users/${userId}/accounts`, {
        method: 'POST', accept: VENDOR_TYPES.userAccountList,
        contentType: VENDOR_TYPES.userAccountCreate, data: account, signal,
    }).then(({ data }) => data);

export const deleteUserAccount = (userId, platform, { signal } = {}) =>
    requestJson(`/users/${userId}/accounts/${encodeURIComponent(platform)}`, {
        method: 'DELETE', signal,
    });

export const getUserComments = async (userId, { page = 0, signal } = {}) => {
    const response = await requestJson(appendQueryParams(`/users/${userId}/comments`, { page }), {
        accept: VENDOR_TYPES.commentList, signal,
    });
    const comments = await Promise.all((response.data ?? []).map(async (comment) => {
        const commenterHref = getLink(comment.links, 'commenter');
        return { ...comment, commenter: commenterHref ? await getUser(commenterHref, { signal }) : null };
    }));
    return { ...toPagedResult(response, page), items: comments };
};

export const createUserComment = (userId, comment, { signal } = {}) =>
    requestJson(`/users/${userId}/comments`, {
        method: 'POST', accept: VENDOR_TYPES.comment,
        contentType: VENDOR_TYPES.commentCreate, data: { comment }, signal,
    }).then(({ data }) => data);

export { normalizeUser };
