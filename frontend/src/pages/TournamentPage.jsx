import { useEffect, useMemo, useState } from 'react';
import { useParams } from 'react-router-dom';
import {
    getTournament,
    getTournamentCreator,
    getTournamentGame,
    getTournamentMatches,
    getTournamentParticipants
} from '../api/tournaments.js';
import { getIdFromLink, getLink } from '../api/client.js';
import InfoCard from '../components/InfoCard.jsx';
import SectionTabs from '../components/SectionTabs.jsx';

const formatDate = (value) => {
    if (!value) {
        return 'Sin fecha';
    }
    const date = new Date(value);
    if (Number.isNaN(date.getTime())) {
        return value;
    }
    return new Intl.DateTimeFormat('es-AR', {
        day: '2-digit',
        month: 'short',
        year: 'numeric'
    }).format(date);
};

const buildMatchLabel = (match, participantsById) => {
    const localId = getIdFromLink(getLink(match.links, 'localParticipant'));
    const visitorId = getIdFromLink(getLink(match.links, 'visitorParticipant'));
    const local = localId ? participantsById[localId]?.name : 'TBD';
    const visitor = visitorId ? participantsById[visitorId]?.name : 'TBD';
    const localScore = match.localScore ?? '-';
    const visitorScore = match.visitorScore ?? '-';
    return { local, visitor, localScore, visitorScore };
};

const TournamentPage = () => {
    const { tournamentId } = useParams();
    const [activeSection, setActiveSection] = useState('overview');
    const [tournament, setTournament] = useState(null);
    const [participants, setParticipants] = useState([]);
    const [matches, setMatches] = useState([]);
    const [creator, setCreator] = useState(null);
    const [game, setGame] = useState(null);
    const [error, setError] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        let isMounted = true;
        setActiveSection('overview');
        const loadTournament = async () => {
            setLoading(true);
            setError(null);

            try {
                const tournamentResponse = await getTournament(tournamentId);
                const [participantsResponse, matchesResponse, creatorResponse, gameResponse] = await Promise.all([
                    getTournamentParticipants(tournamentId),
                    getTournamentMatches(tournamentId),
                    getTournamentCreator(tournamentResponse),
                    getTournamentGame(tournamentResponse)
                ]);

                if (!isMounted) {
                    return;
                }

                setTournament(tournamentResponse);
                setParticipants(participantsResponse);
                setMatches(matchesResponse);
                setCreator(creatorResponse);
                setGame(gameResponse);
            } catch (loadError) {
                if (!isMounted) {
                    return;
                }
                setError(loadError);
            } finally {
                if (isMounted) {
                    setLoading(false);
                }
            }
        };

        loadTournament();

        return () => {
            isMounted = false;
        };
    }, [tournamentId]);

    const participantsById = useMemo(() => {
        return participants.reduce((acc, participant) => {
            acc[participant.id] = participant;
            return acc;
        }, {});
    }, [participants]);

    const sections = [
        { id: 'overview', label: 'Resumen' },
        { id: 'matches', label: 'Partidos' },
        { id: 'participants', label: 'Participantes' },
        { id: 'rules', label: 'Reglas' }
    ];

    if (loading) {
        return (
            <div className="page-state">
                <h2>Cargando torneo...</h2>
            </div>
        );
    }

    if (error) {
        return (
            <div className="page-state">
                <h2>Ups, algo salió mal.</h2>
                <p>No se pudo cargar el torneo. Revisá que la API esté levantada.</p>
            </div>
        );
    }

    if (!tournament) {
        return (
            <div className="page-state">
                <h2>No encontramos el torneo.</h2>
            </div>
        );
    }

    const bannerImage = getLink(tournament.links, 'image');
    const heroStyle = bannerImage
        ? { backgroundImage: `url(${bannerImage})` }
        : { backgroundImage: 'linear-gradient(135deg, #0f2238, #1d3a5a)' };
    const creatorName = creator?.username ?? 'Organizador';
    const gameName = game?.name ?? 'Juego';

    return (
        <div className="page">
            <header className="hero">
                <div className="hero-image" style={heroStyle} />
                <div className="hero-content">
                    <p className="hero-game">{gameName}</p>
                    <h1>{tournament.name}</h1>
                    <p className="hero-dates">
                        {formatDate(tournament.startDate)} · {formatDate(tournament.endDate)}
                    </p>
                    <div className="hero-chips">
                        <span>{tournament.region ?? 'Región por definir'}</span>
                        <span>{tournament.format ?? 'Formato'}</span>
                        <span>{tournament.structure ?? 'Estructura'}</span>
                    </div>
                    <div className="hero-organizer">
                        <span>Organiza</span>
                        <strong>{creatorName}</strong>
                    </div>
                </div>
            </header>

            <SectionTabs
                sections={sections}
                activeSection={activeSection}
                onChange={setActiveSection}
            />

            <main className="page-content">
                {activeSection === 'overview' && (
                    <section className="section">
                        <h2>Resumen</h2>
                        <div className="info-grid">
                            <InfoCard title="Estado" value={tournament.openInscriptions ? 'Inscripciones abiertas' : 'Cerradas'} />
                            <InfoCard title="Participantes" value={`${participants.length} / ${tournament.maxParticipants}`} />
                            <InfoCard title="Elo" value={tournament.elo ?? 'Libre'} />
                            <InfoCard title="Discord" value={tournament.discordChannel ?? 'No definido'} />
                        </div>
                        <div className="summary-card">
                            <h3>Servidor</h3>
                            <p>{tournament.serverName ?? 'Sin servidor asignado aún.'}</p>
                        </div>
                    </section>
                )}

                {activeSection === 'matches' && (
                    <section className="section">
                        <h2>Partidos</h2>
                        {matches.length === 0 ? (
                            <p>No hay partidos cargados todavía.</p>
                        ) : (
                            matches.map((stage) => (
                                <div key={stage.stage} className="stage-block">
                                    <h3>Etapa {stage.stage}</h3>
                                    <div className="match-grid">
                                        {stage.matches.map((match) => {
                                            const label = buildMatchLabel(match, participantsById);
                                            return (
                                                <div key={match.id} className="match-card">
                                                    <div>
                                                        <p>{label.local}</p>
                                                        <p className="match-score">{label.localScore}</p>
                                                    </div>
                                                    <span className="match-vs">vs</span>
                                                    <div>
                                                        <p>{label.visitor}</p>
                                                        <p className="match-score">{label.visitorScore}</p>
                                                    </div>
                                                    <p className="match-date">{formatDate(match.date)}</p>
                                                </div>
                                            );
                                        })}
                                    </div>
                                </div>
                            ))
                        )}
                    </section>
                )}

                {activeSection === 'participants' && (
                    <section className="section">
                        <h2>Participantes</h2>
                        {participants.length === 0 ? (
                            <p>No hay participantes todavía.</p>
                        ) : (
                            <div className="participants-grid">
                                {participants.map((participant) => (
                                    <div key={participant.id} className="participant-card">
                                        <p>{participant.name}</p>
                                        <span>{participant.points} pts</span>
                                    </div>
                                ))}
                            </div>
                        )}
                    </section>
                )}

                {activeSection === 'rules' && (
                    <section className="section">
                        <h2>Reglas</h2>
                        <div className="summary-card">
                            <p>
                                Las reglas se descargan desde el backend. Cuando el endpoint esté disponible,
                                vamos a mostrar el archivo PDF o un resumen acá.
                            </p>
                        </div>
                    </section>
                )}
            </main>
        </div>
    );
};

export default TournamentPage;