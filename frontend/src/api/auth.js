import { getLink, requestJson } from './client.js';
import { VENDOR_TYPES } from './vendorTypes.js';

const normalizeEmailToken = (token) => String(token ?? '').trim();
const LONG_TOKEN_PATTERN = /^[1-9]\d*$/;

const serializeEmailTokenRequest = (token, fields = {}) => {
    const normalizedToken = normalizeEmailToken(token);
    const tokenValue = LONG_TOKEN_PATTERN.test(normalizedToken)
        ? normalizedToken
        : JSON.stringify(normalizedToken);
    const fieldsBody = JSON.stringify(fields).slice(1, -1);

    return `{${[`"token":${tokenValue}`, fieldsBody].filter(Boolean).join(',')}}`;
};

export const loginUser = async ({ username, password }) => {
    const { data, authorization } = await requestJson('/users/sessions', {
        method: 'POST',
        accept: VENDOR_TYPES.user,
        contentType: VENDOR_TYPES.userLogin,
        data: { username: username.trim(), password },
    });

    return { user: data, token: authorization };
};

export const registerUser = async ({ username, email, password, repeatPassword }) => {
    const { data, authorization, response } = await requestJson('/users', {
        method: 'POST',
        accept: VENDOR_TYPES.user,
        contentType: VENDOR_TYPES.userCreate,
        data: { username, email, password, repeatPassword },
    });

    return {
        user: data,
        token: authorization,
        location: response.headers.get('Location'),
    };
};

export const requestPasswordReset = ({ email }) =>
    requestJson('/users/password-requests', {
        method: 'POST',
        contentType: VENDOR_TYPES.passwordResetRequest,
        data: { email },
    }).then(({ data }) => data);

export const resetPassword = async ({ token, password }) => {
    const { data, authorization } = await requestJson('/users/password-requests', {
        method: 'PUT',
        accept: VENDOR_TYPES.user,
        contentType: VENDOR_TYPES.passwordReset,
        body: serializeEmailTokenRequest(token, { password }),
    });

    return { user: data, token: authorization };
};

export const confirmVerification = async ({ token }) => {
    const { data, authorization } = await requestJson('/users/verifications', {
        method: 'PUT',
        accept: VENDOR_TYPES.user,
        contentType: VENDOR_TYPES.userVerificationConfirm,
        body: serializeEmailTokenRequest(token),
    });

    return { user: data, token: authorization };
};

export const fetchUserFromSelfLink = (user) => {
    const selfLink = getLink(user?.links, 'self');
    if (!selfLink) {
        return Promise.resolve(user ?? null);
    }

    return requestJson(selfLink, {
        accept: VENDOR_TYPES.user,
    }).then(({ data }) => data);
};
