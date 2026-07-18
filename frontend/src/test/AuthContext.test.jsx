import { describe, expect, it, beforeEach } from 'vitest';
import { render, screen } from '@testing-library/react';
import { AuthProvider, AUTH_STORAGE_KEYS } from '../auth/AuthContext.jsx';
import { useAuth } from '../auth/useAuth.js';

const AuthStateProbe = () => {
    const { user, token, isAuthenticated, isVerified } = useAuth();

    return (
        <div>
            <span>{token}</span>
            <span>{user?.username}</span>
            <span>{isAuthenticated ? 'authenticated' : 'anonymous'}</span>
            <span>{isVerified ? 'verified' : 'unverified'}</span>
        </div>
    );
};

describe('AuthContext', () => {
    beforeEach(() => {
        localStorage.clear();
    });

    it('rehydrates token and user from storage', () => {
        localStorage.setItem(AUTH_STORAGE_KEYS.token, 'Bearer stored-token');
        localStorage.setItem(AUTH_STORAGE_KEYS.user, JSON.stringify({
            id: 10,
            username: 'stored-user',
            verified: true,
            links: [{ rel: 'self', href: 'http://localhost/api/users/10' }],
        }));

        render(
            <AuthProvider>
                <AuthStateProbe />
            </AuthProvider>
        );

        expect(screen.getByText('Bearer stored-token')).toBeInTheDocument();
        expect(screen.getByText('stored-user')).toBeInTheDocument();
        expect(screen.getByText('authenticated')).toBeInTheDocument();
        expect(screen.getByText('verified')).toBeInTheDocument();
    });
});
