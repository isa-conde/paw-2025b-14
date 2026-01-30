const API_BASE = import.meta.env.VITE_API_BASE ?? '/api';

const buildUrl = (pathOrUrl) => {
    if (pathOrUrl.startsWith('http://') || pathOrUrl.startsWith('https://')) {
        return pathOrUrl;
    }
    return `${API_BASE}${pathOrUrl.startsWith('/') ? '' : '/'}${pathOrUrl}`;
};

export const fetchJson = async (pathOrUrl, { accept } = {}) => {
    const response = await fetch(buildUrl(pathOrUrl), {
        headers: {
            Accept: accept ?? 'application/json'
        },
        credentials: 'include'
    });

    if (!response.ok) {
        const error = new Error(`Request failed with ${response.status}`);
        error.status = response.status;
        throw error;
    }

    return response.json();
};

export const getLink = (links, rel) => links?.find((link) => link.rel === rel)?.href ?? null;

export const getIdFromLink = (link) => {
    if (!link) {
        return null;
    }
    const match = link.match(/\/(\d+)(?:\?.*)?$/);
    return match ? Number(match[1]) : null;
};