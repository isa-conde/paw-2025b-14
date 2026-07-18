import { createContext, useCallback, useEffect, useMemo, useState } from 'react';
import { fetchUserFromSelfLink, loginUser } from '../api/auth.js';
import { setApiToken } from '../api/client.js';

export const AuthContext = createContext(null);

export const AUTH_STORAGE_KEYS = {
    token: 'rankup.auth.token',
    user: 'rankup.auth.user',
};

const emptyAuthState = {
    token: null,
    user: null,
    loading: false,
    authError: null,
};

const getStorage = () => {
    if (typeof window === 'undefined') {
        return null;
    }
    return window.localStorage;
};

const clearStoredAuth = () => {
    const storage = getStorage();
    storage?.removeItem(AUTH_STORAGE_KEYS.token);
    storage?.removeItem(AUTH_STORAGE_KEYS.user);
};

const readStoredUser = (storage) => {
    const rawUser = storage?.getItem(AUTH_STORAGE_KEYS.user);
    if (!rawUser) {
        return null;
    }

    try {
        return JSON.parse(rawUser);
    } catch {
        return null;
    }
};

const readInitialAuthState = () => {
    const storage = getStorage();
    const token = storage?.getItem(AUTH_STORAGE_KEYS.token) ?? null;
    const user = readStoredUser(storage);

    if (token && user) {
        return {
            token,
            user,
            loading: false,
            authError: null,
        };
    }

    if (token || user) {
        clearStoredAuth();
    }

    return emptyAuthState;
};

const persistAuth = ({ token, user }) => {
    const storage = getStorage();
    storage?.setItem(AUTH_STORAGE_KEYS.token, token);
    storage?.setItem(AUTH_STORAGE_KEYS.user, JSON.stringify(user));
};

const getUserAuthorities = (user) => {
    const authorities = user?.authorities ?? user?.roles ?? [];
    if (!Array.isArray(authorities)) {
        return [];
    }

    return authorities.map((authority) => (
        typeof authority === 'string' ? authority : authority?.authority
    )).filter(Boolean);
};

export const AuthProvider = ({ children }) => {
    const [authState, setAuthState] = useState(readInitialAuthState);

    useEffect(() => {
        setApiToken(authState.token);
    }, [authState.token]);

    const authenticate = useCallback(({ token, user }) => {
        if (!token || !user) {
            throw new Error('Missing authentication data');
        }

        persistAuth({ token, user });
        setApiToken(token);
        setAuthState({
            token,
            user,
            loading: false,
            authError: null,
        });
    }, []);

    const login = useCallback(async (credentials) => {
        setAuthState((current) => ({
            ...current,
            loading: true,
            authError: null,
        }));

        try {
            const auth = await loginUser(credentials);
            authenticate(auth);
            return auth;
        } catch (error) {
            setAuthState((current) => ({
                ...current,
                loading: false,
                authError: error,
            }));
            throw error;
        }
    }, [authenticate]);

    const logout = useCallback(() => {
        clearStoredAuth();
        setApiToken(null);
        setAuthState(emptyAuthState);
    }, []);

    const setUser = useCallback((user) => {
        setAuthState((current) => {
            if (!current.token || !user) {
                return current;
            }

            persistAuth({ token: current.token, user });
            return {
                ...current,
                user,
                authError: null,
            };
        });
    }, []);

    const refreshUser = useCallback(async () => {
        if (!authState.user) {
            return null;
        }

        const user = await fetchUserFromSelfLink(authState.user);
        setUser(user);
        return user;
    }, [authState.user, setUser]);

    const value = useMemo(() => {
        const roles = getUserAuthorities(authState.user);

        return {
            ...authState,
            roles,
            authorities: roles,
            isAuthenticated: Boolean(authState.token && authState.user),
            isVerified: Boolean(authState.user?.verified),
            authenticate,
            login,
            logout,
            setUser,
            refreshUser,
        };
    }, [authState, authenticate, login, logout, refreshUser, setUser]);

    return (
        <AuthContext.Provider value={value}>
            {children}
        </AuthContext.Provider>
    );
};
