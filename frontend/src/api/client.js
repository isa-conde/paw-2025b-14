const API_BASE = import.meta.env.VITE_API_BASE ?? '/api';
const BEARER_PREFIX = /^Bearer\s+/i;

let apiToken = null;

export const setApiToken = (token) => {
    apiToken = token || null;
};

export const getApiToken = () => apiToken;

export const buildUrl = (pathOrUrl) => {
    if (pathOrUrl.startsWith('http://') || pathOrUrl.startsWith('https://')) {
        return pathOrUrl;
    }
    return `${API_BASE}${pathOrUrl.startsWith('/') ? '' : '/'}${pathOrUrl}`;
};

const normalizeHeaders = (headers = {}) => {
    if (headers instanceof Headers) {
        return Object.fromEntries(headers.entries());
    }
    if (Array.isArray(headers)) {
        return Object.fromEntries(headers);
    }
    return { ...headers };
};

const setHeader = (headers, name, value) => {
    if (!value) {
        return;
    }
    const existingKey = Object.keys(headers).find((key) => key.toLowerCase() === name.toLowerCase());
    headers[existingKey ?? name] = value;
};

const buildAuthorizationValue = (token) => (
    BEARER_PREFIX.test(token) ? token : `Bearer ${token}`
);

const buildHeaders = ({ accept, contentType, headers, token, hasBody }) => {
    const requestHeaders = normalizeHeaders(headers);

    setHeader(requestHeaders, 'Accept', accept ?? 'application/json');
    if (hasBody || contentType) {
        setHeader(requestHeaders, 'Content-Type', contentType ?? 'application/json');
    }

    const selectedToken = token === undefined ? apiToken : token;
    if (selectedToken) {
        setHeader(requestHeaders, 'Authorization', buildAuthorizationValue(selectedToken));
    }

    return requestHeaders;
};

const parseResponseBody = async (response) => {
    if (response.status === 204 || response.status === 205) {
        return null;
    }

    const text = await response.text();
    if (!text) {
        return null;
    }

    const contentType = response.headers.get('Content-Type') ?? '';
    const trimmed = text.trim();
    if (contentType.includes('json') || trimmed.startsWith('{') || trimmed.startsWith('[')) {
        return JSON.parse(text);
    }

    return text;
};

const buildApiError = (response, data) => {
    const message = data?.message || response.statusText || `Request failed with ${response.status}`;
    const error = new Error(message);
    error.name = 'ApiError';
    error.status = data?.status ?? response.status;
    error.error = data?.error ?? response.statusText;
    error.details = data?.details ?? null;
    error.data = data;
    error.response = response;
    return error;
};

export const getAuthorizationHeader = (headers) => headers?.get?.('Authorization') ?? null;

export const parseLinkHeader = (header) => {
    if (!header) {
        return [];
    }

    return header
        .split(',')
        .map((entry) => {
            const [hrefPart, ...paramParts] = entry.split(';');
            const href = hrefPart.trim().replace(/^<|>$/g, '');
            const link = { href };

            paramParts.forEach((part) => {
                const [key, value] = part.trim().split('=');
                if (key && value) {
                    link[key] = value.trim().replace(/^"|"$/g, '');
                }
            });

            return link;
        })
        .filter((link) => link.href && link.rel);
};

export const getPaginationLinks = (headersOrResponse) => {
    const headers = headersOrResponse?.headers ?? headersOrResponse;
    const header = headers?.get?.('Link') ?? headers?.Link ?? headers?.link;
    return parseLinkHeader(header).reduce((pagination, link) => ({
        ...pagination,
        [link.rel]: link.href,
    }), {});
};

export const requestJson = async (pathOrUrl, options = {}) => {
    const {
        method,
        accept,
        contentType,
        headers,
        body,
        data,
        token,
        signal,
    } = options;

    const requestBody = data !== undefined ? JSON.stringify(data) : body;
    const response = await fetch(buildUrl(pathOrUrl), {
        method: method ?? (requestBody !== undefined ? 'POST' : 'GET'),
        headers: buildHeaders({
            accept,
            contentType,
            headers,
            token,
            hasBody: requestBody !== undefined,
        }),
        ...(signal ? { signal } : {}),
        ...(requestBody !== undefined ? { body: requestBody } : {}),
    });

    const parsedBody = await parseResponseBody(response);
    if (!response.ok) {
        throw buildApiError(response, parsedBody);
    }

    return {
        data: parsedBody,
        authorization: getAuthorizationHeader(response.headers),
        links: parseLinkHeader(response.headers.get('Link')),
        response,
    };
};

export const fetchJson = async (pathOrUrl, options = {}) => {
    const { data } = await requestJson(pathOrUrl, options);
    return data;
};

export const getLink = (links, rel) => {
    if (Array.isArray(links)) {
        return links.find((link) => link.rel === rel)?.href ?? null;
    }

    const link = links?.[rel];
    return typeof link === 'string' ? link : link?.href ?? null;
};

export const getIdFromLink = (link) => {
    if (!link) {
        return null;
    }
    const match = link.match(/\/(\d+)(?:\?.*)?$/);
    return match ? Number(match[1]) : null;
};
