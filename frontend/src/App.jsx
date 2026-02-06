import { Navigate, Route, Routes } from 'react-router-dom';
import TournamentPage from './pages/TournamentPage.jsx';
import { LoginPage } from './pages/LoginPage.jsx'

const App = () => (
    <Routes>
        <Route path="/" element={<LoginPage />} />
        <Route path="/tournaments/:tournamentId" element={<TournamentPage />} />
        <Route path="*" element={<Navigate to="/tournaments/1" replace />} />
    </Routes>
);

export default App;