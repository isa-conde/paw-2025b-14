import { Navigate, Route, Routes } from 'react-router-dom';
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

const App = () => (
    <Routes>
        <Route path="/" element={<HomePage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />
        <Route path="/forgotPassword" element={<ForgotPasswordPage />} />
        <Route path="/requestPasswordReset" element={<RequestPasswordResetPage />} />
        <Route path="/resetPassword" element={<ResetPasswordPage />} />
        <Route path="/resetPasswordSuccess" element={<ResetPasswordSuccessPage />} />
        <Route path="/gamesPage" element={<GamesPage />} />
        <Route path="/tournamentsPage" element={<TournamentsPage />} />
    </Routes>
);

export default App;