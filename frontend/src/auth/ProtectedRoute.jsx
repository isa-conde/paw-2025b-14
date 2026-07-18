import { Navigate, useLocation } from 'react-router-dom';
import { useAuth } from './useAuth.js';

export const ProtectedRoute = ({ children, requireVerified = false }) => {
    const location = useLocation();
    const { isAuthenticated, isVerified, loading } = useAuth();

    if (loading) {
        return null;
    }

    if (!isAuthenticated) {
        return <Navigate to="/login" replace state={{ from: location, status: 401 }} />;
    }

    if (requireVerified && !isVerified) {
        return <Navigate to="/403" replace state={{ status: 403, reason: 'unverified' }} />;
    }

    return children;
};
