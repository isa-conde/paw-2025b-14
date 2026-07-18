import { beforeEach, describe, expect, it } from 'vitest';
import { render, screen } from '@testing-library/react';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import { AuthProvider, AUTH_STORAGE_KEYS } from '../auth/AuthContext.jsx';
import { ProtectedRoute } from '../auth/ProtectedRoute.jsx';

const renderProtectedRoute = () => render(
    <MemoryRouter initialEntries={['/private']}>
        <AuthProvider>
            <Routes>
                <Route
                    path="/private"
                    element={(
                        <ProtectedRoute>
                            <div>Private</div>
                        </ProtectedRoute>
                    )}
                />
                <Route path="/login" element={<div>Login</div>} />
            </Routes>
        </AuthProvider>
    </MemoryRouter>
);

describe('ProtectedRoute', () => {
    beforeEach(() => {
        localStorage.clear();
    });

    it('redirects anonymous users to login', () => {
        renderProtectedRoute();

        expect(screen.getByText('Login')).toBeInTheDocument();
        expect(screen.queryByText('Private')).not.toBeInTheDocument();
    });

    it('allows authenticated users', () => {
        localStorage.setItem(AUTH_STORAGE_KEYS.token, 'Bearer stored-token');
        localStorage.setItem(AUTH_STORAGE_KEYS.user, JSON.stringify({
            id: 1,
            username: 'verified-user',
            verified: true,
        }));

        renderProtectedRoute();

        expect(screen.getByText('Private')).toBeInTheDocument();
    });
});
