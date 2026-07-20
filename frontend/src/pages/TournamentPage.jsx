import { useEffect, useMemo, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useLocation, useNavigate, useParams, useSearchParams } from 'react-router-dom';
import {
    deleteParticipant,
    getTournament,
    getTournamentCreator,
    getTournamentFormat,
    getTournamentGame,
    getTournamentMatches,
    getTournamentParticipants,
    getTournamentRules,
    joinTournamentAsTeam,
    joinTournamentAsUser,
    setMatchResults,
    uploadTournamentRules,
    updateTournamentStatus,
} from '../api/tournaments.js';
import { Layout } from '../components/Layout.jsx';
import SectionTabs from '../components/SectionTabs.jsx';
import { TournamentActions } from '../components/tournament/TournamentActions.jsx';
import { TournamentMatches } from '../components/tournament/TournamentMatches.jsx';
import { TournamentOverview } from '../components/tournament/TournamentOverview.jsx';
import { TournamentParticipants } from '../components/tournament/TournamentParticipants.jsx';
import { TournamentRules } from '../components/tournament/TournamentRules.jsx';
import { useAuth } from '../auth/useAuth.js';
import { ForbiddenPage } from './ForbiddenPage.jsx';
import { NotFoundPage } from './NotFoundPage.jsx';
import styles from '../styles/pages/TournamentPage.module.css';

const DEFAULT_TAB = 'overview';
const VALID_TABS = ['overview', 'matches', 'participants', 'rules'];
const defaultBanner = '/assets/tournament.jpeg';

const isAbortError = (error) => error?.name === 'AbortError';

const toNumericId = (value) => {
    const id = Number(value);
    return Number.isFinite(id) ? id : null;
};

const fetchOptional = async (promise) => {
    try {
        return await promise;
    } catch (error) {
        if (isAbortError(error)) {
            throw error;
        }
        return null;
    }
};

const fetchCurrentParticipants = async (tournament, currentUserId, signal) => {
    if (!currentUserId) {
        return [];
    }

    try {
        return await getTournamentParticipants(tournament, {
            userId: currentUserId,
            signal,
        });
    } catch (error) {
        if (isAbortError(error)) {
            throw error;
        }
        return [];
    }
};

const fetchTournamentDetail = async ({ tournamentId, currentUserId, signal }) => {
    const tournament = await getTournament(tournamentId, { signal });
    const [
        participants,
        matches,
        creator,
        game,
        format,
        currentParticipants,
    ] = await Promise.all([
        getTournamentParticipants(tournament, { signal }),
        getTournamentMatches(tournament, { signal }),
        fetchOptional(getTournamentCreator(tournament, { signal })),
        fetchOptional(getTournamentGame(tournament, { signal })),
        fetchOptional(getTournamentFormat(tournament, { signal })),
        fetchCurrentParticipants(tournament, currentUserId, signal),
    ]);

    return {
        tournament,
        participants,
        matches,
        creator,
        game,
        format,
        currentParticipants,
    };
};

const revokeRulesUrl = (rulesState) => {
    if (rulesState.isObjectUrl && rulesState.url && typeof URL.revokeObjectURL === 'function') {
        URL.revokeObjectURL(rulesState.url);
    }
};

const createRulesObjectUrl = (blob, fallbackUrl) => {
    if (blob && typeof URL.createObjectURL === 'function') {
        return {
            url: URL.createObjectURL(blob),
            isObjectUrl: true,
        };
    }

    return {
        url: fallbackUrl,
        isObjectUrl: false,
    };
};

const getActionErrorMessage = (error, t) => {
    if (error?.status === 401) {
        return t('errorExceptionPage.userNotAuthenticated.description');
    }
    if (error?.status === 403) {
        return t('auth.error.forbidden');
    }
    if (error?.status === 409) {
        return t('tournamentDetail.error.conflict');
    }
    if (error?.status === 404) {
        return t('tournamentDetail.error.notFound');
    }
    if (error?.status === 400) {
        return t('tournamentDetail.error.badRequest');
    }
    return t('tournamentDetail.error.generic');
};

const getBackendDetailMessage = (error, t) => {
    const details = error?.details;
    if (!details || typeof details !== 'object') {
        return null;
    }

    const firstDetail = Object.values(details).flat()[0];
    if (firstDetail == null) {
        return null;
    }

    const rawMessage = String(Array.isArray(firstDetail) ? firstDetail[0] : firstDetail);
    const key = rawMessage.replace(/^\{(.+)}$/, '$1');
    return t(key, { defaultValue: rawMessage });
};

const getTeamJoinErrorMessage = (error, t) => {
    const detailMessage = getBackendDetailMessage(error, t);
    if (detailMessage) {
        return detailMessage;
    }

    if (error?.status === 400) {
        return t('tournamentDetail.teamJoin.error.badRequest');
    }
    if (error?.status === 403) {
        return t('tournamentDetail.teamJoin.error.forbidden');
    }
    return getActionErrorMessage(error, t);
};

const getRulesUploadErrorMessage = (error, t) => {
    if (error?.status === 401) {
        return t('errorExceptionPage.userNotAuthenticated.description');
    }
    if (error?.status === 403) {
        return t('auth.error.forbidden');
    }
    if (error?.status === 400) {
        return t('tournamentDetail.rules.uploadInvalid');
    }
    return t('tournamentDetail.rules.uploadError');
};

const TournamentPage = () => {
    const { t, i18n } = useTranslation();
    const { tournamentId } = useParams();
    const [searchParams, setSearchParams] = useSearchParams();
    const navigate = useNavigate();
    const location = useLocation();
    const {
        user,
        isAuthenticated,
        isVerified,
    } = useAuth();
    const currentUserId = toNumericId(user?.id);

    const [tournament, setTournament] = useState(null);
    const [participants, setParticipants] = useState([]);
    const [currentParticipants, setCurrentParticipants] = useState([]);
    const [matches, setMatches] = useState([]);
    const [creator, setCreator] = useState(null);
    const [game, setGame] = useState(null);
    const [format, setFormat] = useState(null);
    const [error, setError] = useState(null);
    const [loading, setLoading] = useState(true);
    const [actionError, setActionError] = useState(null);
    const [actionLoading, setActionLoading] = useState(null);
    const [statusLoading, setStatusLoading] = useState(null);
    const [resultLoadingId, setResultLoadingId] = useState(null);
    const [rulesState, setRulesState] = useState({ status: 'idle', url: null, isObjectUrl: false });
    const [rulesReloadKey, setRulesReloadKey] = useState(0);
    const [rulesUploadState, setRulesUploadState] = useState({ status: 'idle', error: null });

    const rawTab = searchParams.get('tab');
    const activeTab = VALID_TABS.includes(rawTab) ? rawTab : DEFAULT_TAB;

    const sections = useMemo(() => [
        { id: 'overview', label: t('tournament.overview') },
        { id: 'matches', label: t('tournament.matches') },
        { id: 'participants', label: t('tournament.participants.title') },
        { id: 'rules', label: t('tournament.rules') },
    ], [t]);

    const applyDetail = (detail) => {
        setTournament(detail.tournament);
        setParticipants(detail.participants);
        setMatches(detail.matches);
        setCreator(detail.creator);
        setGame(detail.game);
        setFormat(detail.format);
        setCurrentParticipants(detail.currentParticipants);
    };

    const refreshDetail = async () => {
        const detail = await fetchTournamentDetail({
            tournamentId,
            currentUserId,
        });
        applyDetail(detail);
    };

    useEffect(() => {
        if (rawTab && !VALID_TABS.includes(rawTab)) {
            const nextParams = new URLSearchParams(searchParams);
            nextParams.set('tab', DEFAULT_TAB);
            setSearchParams(nextParams, { replace: true });
        }
    }, [rawTab, searchParams, setSearchParams]);

    useEffect(() => {
        let isMounted = true;
        const controller = new AbortController();

        const load = async () => {
            setLoading(true);
            setError(null);
            setActionError(null);

            try {
                const detail = await fetchTournamentDetail({
                    tournamentId,
                    currentUserId,
                    signal: controller.signal,
                });
                if (isMounted) {
                    applyDetail(detail);
                }
            } catch (loadError) {
                if (isMounted && !isAbortError(loadError)) {
                    setError(loadError);
                }
            } finally {
                if (isMounted) {
                    setLoading(false);
                }
            }
        };

        load();

        return () => {
            isMounted = false;
            controller.abort();
        };
    }, [tournamentId, currentUserId]);

    useEffect(() => {
        setRulesState((current) => {
            revokeRulesUrl(current);
            return { status: 'idle', url: null, isObjectUrl: false };
        });
        setRulesUploadState({ status: 'idle', error: null });
    }, [tournamentId]);

    useEffect(() => {
        if (activeTab !== 'rules' || !tournament?.id) {
            return undefined;
        }

        let isMounted = true;
        const controller = new AbortController();
        const rulesResource = tournament.rulesHref ?? tournament.id;

        setRulesState((current) => {
            revokeRulesUrl(current);
            return { status: 'loading', url: null, isObjectUrl: false };
        });
        getTournamentRules(rulesResource, { signal: controller.signal })
            .then(({ blob, url }) => {
                const rulesUrl = createRulesObjectUrl(blob, url);
                if (isMounted) {
                    setRulesState({
                        status: 'available',
                        ...rulesUrl,
                    });
                } else {
                    revokeRulesUrl({ ...rulesUrl, status: 'available' });
                }
            })
            .catch((rulesError) => {
                if (!isMounted || isAbortError(rulesError)) {
                    return;
                }
                setRulesState({
                    status: rulesError.status === 404 ? 'not-found' : 'error',
                    url: null,
                    isObjectUrl: false,
                });
            });

        return () => {
            isMounted = false;
            controller.abort();
        };
    }, [activeTab, tournament?.id, tournament?.rulesHref, rulesReloadKey]);

    useEffect(() => (
        () => {
            revokeRulesUrl(rulesState);
        }
    ), [rulesState]);

    const participantsById = useMemo(() => participants.reduce((acc, participant) => ({
        ...acc,
        [participant.id]: participant,
    }), {}), [participants]);

    const currentParticipant = useMemo(() => {
        if (!currentUserId) {
            return null;
        }

        return currentParticipants[0]
            ?? participants.find((participant) => participant.userId === currentUserId)
            ?? null;
    }, [currentParticipants, currentUserId, participants]);

    const creatorId = toNumericId(tournament?.creatorId ?? creator?.id);
    const isOwner = Boolean(currentUserId && creatorId && currentUserId === creatorId);

    const formatDate = (value) => {
        if (!value) {
            return t('tournament.match.noDate');
        }
        const date = new Date(value);
        if (Number.isNaN(date.getTime())) {
            return value;
        }
        const locale = i18n?.language?.startsWith('es') ? 'es-AR' : 'en-US';
        return new Intl.DateTimeFormat(locale, {
            day: '2-digit',
            month: 'short',
            year: 'numeric',
        }).format(date);
    };

    const handleTabChange = (tab) => {
        const nextParams = new URLSearchParams(searchParams);
        nextParams.set('tab', tab);
        setSearchParams(nextParams);
    };

    const handleLogin = () => {
        navigate('/login', {
            state: {
                from: location,
                status: 401,
            },
        });
    };

    const handleJoin = async () => {
        setActionError(null);
        setActionLoading('join');
        try {
            await joinTournamentAsUser(tournamentId);
            await refreshDetail();
        } catch (joinError) {
            setActionError(getActionErrorMessage(joinError, t));
        } finally {
            setActionLoading(null);
        }
    };

    const handleJoinTeam = async (payload) => {
        setActionError(null);
        setActionLoading('joinTeam');
        try {
            await joinTournamentAsTeam(tournamentId, payload);
            await refreshDetail();
        } catch (joinError) {
            setActionError(getTeamJoinErrorMessage(joinError, t));
        } finally {
            setActionLoading(null);
        }
    };

    const handleLeave = async () => {
        if (!currentParticipant) {
            setActionError(t('tournamentDetail.error.missingParticipant'));
            return;
        }

        setActionError(null);
        setActionLoading('leave');
        try {
            await deleteParticipant(tournamentId, currentParticipant.id);
            await refreshDetail();
        } catch (leaveError) {
            setActionError(getActionErrorMessage(leaveError, t));
        } finally {
            setActionLoading(null);
        }
    };

    const handleStatusUpdate = async (payload, loadingKey) => {
        setActionError(null);
        setStatusLoading(loadingKey);
        try {
            await updateTournamentStatus(tournamentId, payload);
            await refreshDetail();
        } catch (statusError) {
            setActionError(getActionErrorMessage(statusError, t));
        } finally {
            setStatusLoading(null);
        }
    };

    const handleSetResults = async (matchId, payload) => {
        setActionError(null);
        setResultLoadingId(matchId);
        try {
            await setMatchResults(tournamentId, matchId, payload);
            await refreshDetail();
        } catch (resultsError) {
            setActionError(getActionErrorMessage(resultsError, t));
        } finally {
            setResultLoadingId(null);
        }
    };

    const handleUploadRules = async (file) => {
        setRulesUploadState({ status: 'uploading', error: null });
        try {
            const updatedTournament = await uploadTournamentRules(tournament, file);
            setTournament(updatedTournament);
            setRulesUploadState({ status: 'success', error: null });
            setRulesReloadKey((current) => current + 1);
        } catch (uploadError) {
            setRulesUploadState({
                status: 'error',
                error: getRulesUploadErrorMessage(uploadError, t),
            });
        }
    };

    if (loading) {
        return (
            <Layout pageTitle={t('tournaments.title')}>
                <div className={styles.state}>
                    <h1 className={styles.sectionTitle}>{t('tournamentDetail.loading')}</h1>
                </div>
            </Layout>
        );
    }

    if (error?.status === 404) {
        return <NotFoundPage />;
    }

    if (error?.status === 403) {
        return <ForbiddenPage />;
    }

    if (error) {
        return (
            <Layout pageTitle={t('tournaments.title')}>
                <div className={styles.state} role="alert">
                    <h1 className={styles.sectionTitle}>{t('tournamentDetail.error.title')}</h1>
                    <p className={styles.muted}>{t('tournamentDetail.error.load')}</p>
                </div>
            </Layout>
        );
    }

    if (!tournament) {
        return <NotFoundPage />;
    }

    const bannerImage = tournament.image ?? defaultBanner;
    const gameName = game?.name ?? t('tournamentDetail.unknown.game');
    const creatorName = creator?.username ?? t('tournamentDetail.unknown.creator');
    const playersPerTeam = Number(format?.playersPerTeam ?? tournament.playersPerTeam ?? 1);

    return (
        <Layout pageTitle={tournament.name}>
            <article className={styles.page}>
                <header className={styles.hero}>
                    <div
                        className={styles.heroImage}
                        style={{ backgroundImage: `url(${bannerImage})` }}
                    />
                    <div className={styles.heroContent}>
                        <p className={styles.eyebrow}>{gameName}</p>
                        <h1 className={styles.title}>{tournament.name}</h1>
                        <p className={styles.muted}>
                            {formatDate(tournament.startDate)} - {formatDate(tournament.endDate)}
                        </p>
                        <div className={styles.chipRow}>
                            <span className={styles.chip}>{tournament.region ?? t('tournamentDetail.unknown.region')}</span>
                            <span className={styles.chip}>{tournament.format ?? t('tournamentDetail.unknown.format')}</span>
                            <span className={styles.chip}>{tournament.structure ?? t('tournamentDetail.unknown.structure')}</span>
                        </div>
                        <p className={styles.muted}>
                            {t('tournament.organizedBy')} {creatorName}
                        </p>
                    </div>
                </header>

                <SectionTabs
                    sections={sections}
                    activeSection={activeTab}
                    onChange={handleTabChange}
                    className={styles.tabs}
                    tabClassName={styles.tab}
                    activeTabClassName={styles.activeTab}
                />

                <div className={styles.content}>
                    <TournamentActions
                        tournament={tournament}
                        isAuthenticated={isAuthenticated}
                        isVerified={isVerified}
                        isParticipant={Boolean(currentParticipant)}
                        isOwner={isOwner}
                        tournamentId={tournamentId}
                        currentUserId={currentUserId}
                        playersPerTeam={playersPerTeam}
                        actionError={actionError}
                        actionLoading={actionLoading}
                        statusLoading={statusLoading}
                        onLogin={handleLogin}
                        onJoin={handleJoin}
                        onJoinTeam={handleJoinTeam}
                        onLeave={handleLeave}
                        onCloseInscriptions={() => handleStatusUpdate({ openInscriptions: false }, 'close')}
                        onStartTournament={() => handleStatusUpdate({ tournamentStarted: true }, 'start')}
                    />

                    {activeTab === 'overview' && (
                        <TournamentOverview
                            tournament={tournament}
                            game={game}
                            creator={creator}
                            participantsCount={participants.length}
                            formatDate={formatDate}
                        />
                    )}

                    {activeTab === 'matches' && (
                        <TournamentMatches
                            tournament={tournament}
                            stages={matches}
                            participantsById={participantsById}
                            isOwner={isOwner}
                            resultLoadingId={resultLoadingId}
                            onSetResults={handleSetResults}
                            formatDate={formatDate}
                        />
                    )}

                    {activeTab === 'participants' && (
                        <TournamentParticipants
                            participants={participants}
                            currentParticipantId={currentParticipant?.id}
                        />
                    )}

                    {activeTab === 'rules' && (
                        <TournamentRules
                            rulesState={rulesState}
                            isOwner={isOwner}
                            uploadState={rulesUploadState}
                            onUploadRules={handleUploadRules}
                        />
                    )}
                </div>
            </article>
        </Layout>
    );
};

export default TournamentPage;
