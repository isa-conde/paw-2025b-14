import { getPaginationLinks } from './client.js';

export const appendQueryParams = (pathOrUrl, params = {}) => {
    const isAbsolute = /^https?:\/\//i.test(pathOrUrl);
    const url = new URL(pathOrUrl, 'http://localhost');

    Object.entries(params).forEach(([key, value]) => {
        if (value !== undefined && value !== null && value !== '') {
            url.searchParams.set(key, value);
        }
    });

    return isAbsolute ? url.toString() : `${url.pathname}${url.search}`;
};

export const getPageFromHref = (href) => {
    if (!href) {
        return null;
    }

    const page = Number.parseInt(new URL(href, 'http://localhost').searchParams.get('page') ?? '', 10);
    return Number.isNaN(page) ? null : page;
};

export const parsePageParam = (value) => {
    const page = Number.parseInt(value ?? '0', 10);
    return Number.isNaN(page) || page < 0 ? 0 : page;
};

export const toPagedResult = ({ data, response }, requestedPage = 0, mapper = (item) => item) => {
    const links = getPaginationLinks(response);
    const items = Array.isArray(data) ? data.map(mapper) : [];
    const currentPage = getPageFromHref(links.self) ?? requestedPage ?? 0;
    const lastPage = getPageFromHref(links.last);

    return {
        items,
        pagination: {
            currentPage,
            totalPages: lastPage == null ? (items.length > 0 ? 1 : 0) : lastPage + 1,
            links,
        },
    };
};
