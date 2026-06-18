import { Navigate, Route, Routes, useParams } from 'react-router-dom';
import TournamentPage from './pages/TournamentPage.jsx';
import { LoginPage } from './pages/LoginPage.jsx'
import { RegisterPage } from "./pages/RegisterPage.jsx";
import { ForgotPasswordPage } from "./pages/ForgotPasswordPage.jsx";
import { RequestPasswordResetPage } from "./pages/RequestPasswordResetPage.jsx";
import { ResetPasswordPage } from "./pages/ResetPasswordPage.jsx";
import { ResetPasswordSuccessPage } from "./pages/ResetPasswordSuccessPage.jsx";
import { HomePage } from "./pages/HomePage.jsx";
import { GamesPage } from "./pages/GamesPage.jsx";
import { TournamentsPage } from "./pages/TournamentsPage.jsx";
import { NotFoundPage } from "./pages/NotFoundPage.jsx";
import { ForbiddenPage } from "./pages/ForbiddenPage.jsx";

const LegacyTournamentRedirect = () => {
    const { tournamentId } = useParams();

    return <Navigate to={`/tournaments/${tournamentId}`} replace />;
};

const App = () => (
    <Routes>
        <Route path="/" element={<HomePage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />
        <Route path="/forgot-password" element={<ForgotPasswordPage />} />
        <Route path="/forgot-password/requested" element={<RequestPasswordResetPage />} />
        <Route path="/reset-password" element={<ResetPasswordPage />} />
        <Route path="/reset-password/success" element={<ResetPasswordSuccessPage />} />
        <Route path="/games" element={<GamesPage />} />
        <Route path="/tournaments" element={<TournamentsPage />} />
        <Route path="/tournaments/new/*" element={<NotFoundPage />} />
        <Route path="/tournaments/:tournamentId" element={<TournamentPage />} />
        <Route path="/403" element={<ForbiddenPage />} />

        <Route path="/forgotPassword" element={<Navigate to="/forgot-password" replace />} />
        <Route path="/requestPasswordReset" element={<Navigate to="/forgot-password/requested" replace />} />
        <Route path="/resetPassword" element={<Navigate to="/reset-password" replace />} />
        <Route path="/resetPasswordSuccess" element={<Navigate to="/reset-password/success" replace />} />
        <Route path="/gamesPage" element={<Navigate to="/games" replace />} />
        <Route path="/tournamentsPage" element={<Navigate to="/tournaments" replace />} />
        <Route path="/tournament/:tournamentId" element={<LegacyTournamentRedirect />} />
        <Route path="/tournament" element={<Navigate to="/tournaments" replace />} />
        <Route path="*" element={<NotFoundPage />} />
    </Routes>
);

export default App;
