import { useEffect, useMemo, useRef, useState } from 'react';
import { Link, Navigate, useNavigate, useParams, useSearchParams } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { Layout } from '../components/Layout.jsx';
import { ElementCard } from '../components/ElementCard.jsx';
import { Pagination } from '../components/Pagination.jsx';
import SectionTabs from '../components/SectionTabs.jsx';
import { Button } from '../components/Button.jsx';
import { useAuth } from '../auth/useAuth.js';
import { getUser } from '../api/users.js';
import { listGames } from '../api/games.js';
import { listTeams } from '../api/teams.js';
import { listTournaments } from '../api/tournaments.js';
import { addUserAccount, createUserComment, deleteUserAccount, getUserAccounts, getUserComments } from '../api/profile.js';
import { parsePageParam } from '../api/pagination.js';
import styles from '../styles/pages/ProfilePage.module.css';

const PLATFORMS = ['DISCORD', 'STEAM', 'EPIC_GAMES', 'RIOT_GAMES', 'PLAYSTATION', 'XBOX', 'NINTENDO', 'BATTLE_NET', 'UBISOFT_CONNECT', 'ROCKSTAR'];
const emptyPage = { items: [], pagination: { currentPage: 0, totalPages: 0 } };

const CardRow = ({ items, type, empty }) => (
    items.length ? <div className={styles.cards}>{items.map((item) => (
        <ElementCard key={item.id} id={item.id} title={item.name} image={item.image ?? item.profilePicture}
            game={item.gameName} isGame={type === 'game'} url={type === 'team' ? `/teams/${item.id}` : undefined}
            started={item.tournamentStarted} finished={item.finished ?? item.isFinished}/>
    ))}</div> : <p className={styles.empty}>{empty}</p>
);

export const ProfilePage = () => {
    const { t, i18n } = useTranslation();
    const { user: authUser, isAuthenticated, isVerified } = useAuth();
    const { userId } = useParams();
    const navigate = useNavigate();
    const [params, setParams] = useSearchParams();
    const targetId = userId ? Number(userId) : authUser?.id;
    const validId = Number.isSafeInteger(Number(targetId)) && Number(targetId) > 0;
    const ownProfile = Boolean(authUser && Number(authUser.id) === targetId);
    const tab = ['overview', 'tournaments', 'teams'].includes(params.get('tab')) ? params.get('tab') : 'overview';
    const section = ['active', 'finished', 'created'].includes(params.get('section')) ? params.get('section') : 'active';
    const page1 = parsePageParam(params.get('page1'));
    const page2 = parsePageParam(params.get('page2'));
    const [profile, setProfile] = useState(null);
    const [data, setData] = useState({ accounts: [], games: emptyPage, primary: emptyPage, secondary: emptyPage, comments: emptyPage, teams: [] });
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [account, setAccount] = useState({ platform: PLATFORMS[0], username: '' });
    const [comment, setComment] = useState('');
    const [pending, setPending] = useState(false);
    const [actionError, setActionError] = useState(null);
    const [retry, setRetry] = useState(0);
    const actionLock = useRef(false);
    const scope = `${targetId}:${tab}:${section}:${page1}:${page2}`;
    const currentScope = useRef(scope);
    currentScope.current = scope;
    const availablePlatforms = PLATFORMS.filter((platform) => !data.accounts.some((a) => a.platform === platform));
    const selectedPlatform = availablePlatforms.includes(account.platform) ? account.platform : availablePlatforms[0] ?? '';
    const errorMessage = (failure) => t([401, 403, 409].includes(failure?.status) ? `profile.errors.${failure.status}` : 'profile.error');

    const setQuery = (changes) => {
        const next = new URLSearchParams(params);
        Object.entries(changes).forEach(([key, value]) => value == null ? next.delete(key) : next.set(key, value));
        setParams(next);
    };

    useEffect(() => {
        if (!validId) return;
        const controller = new AbortController();
        const signal = controller.signal;
        setLoading(true);
        setError(null);
        setActionError(null);
        setComment('');
        setAccount({ platform: PLATFORMS[0], username: '' });
        const load = async () => {
            try {
                const loadedProfile = await getUser(targetId, { signal });
                let loadedData;
                if (tab === 'overview') {
                    const [accounts, games, primary, secondary, comments] = await Promise.all([
                        getUserAccounts(targetId, { signal }), listGames({ favouritedBy: targetId, signal }),
                        listTournaments({ hasUser: targetId, finished: false, signal }),
                        listTournaments({ hasUser: targetId, finished: true, signal }),
                        getUserComments(targetId, { page: page1, signal }),
                    ]);
                    loadedData = { accounts, games, primary, secondary, comments, teams: [] };
                } else if (tab === 'teams') {
                    loadedData = { ...data, teams: await listTeams({ userId: targetId, signal }) };
                } else {
                    const query = section === 'created' ? { createdBy: targetId, finished: false } : { hasUser: targetId, finished: section === 'finished' };
                    const [primary, secondary] = await Promise.all([
                        listTournaments({ ...query, page: page1, signal }),
                        section === 'finished' ? listTournaments({ wonBy: targetId, page: page2, signal })
                            : section === 'created' ? listTournaments({ createdBy: targetId, finished: true, page: page2, signal }) : Promise.resolve(emptyPage),
                    ]);
                    loadedData = { ...data, primary, secondary };
                }
                if (!signal.aborted) { setProfile(loadedProfile); setData(loadedData); }
            } catch (failure) {
                if (!signal.aborted) setError(failure);
            } finally {
                if (!signal.aborted) setLoading(false);
            }
        };
        load();
        return () => controller.abort();
    }, [targetId, validId, tab, section, page1, page2, retry]);

    const mutate = async (operation) => {
        if (actionLock.current) return;
        actionLock.current = true;
        setPending(true);
        setActionError(null);
        const activeScope = scope;
        const apply = (callback) => { if (currentScope.current === activeScope) callback(); };
        try { await operation(apply); }
        catch (failure) { apply(() => setActionError(failure)); }
        finally { actionLock.current = false; setPending(false); }
    };
    const saveAccount = (event) => {
        event.preventDefault();
        if (!ownProfile || !isVerified || !selectedPlatform || !account.username.trim()) return;
        mutate(async (apply) => {
            const accounts = await addUserAccount(targetId, { platform: selectedPlatform, username: account.username.trim() });
            apply(() => { setData((d) => ({ ...d, accounts })); setAccount({ platform: '', username: '' }); });
        });
    };
    const removeAccount = (platform) => {
        if (!ownProfile || !isVerified) return;
        mutate(async (apply) => {
            await deleteUserAccount(targetId, platform);
            apply(() => setData((d) => ({ ...d, accounts: d.accounts.filter((a) => a.platform !== platform) })));
        });
    };
    const submitComment = (event) => {
        event.preventDefault();
        if (!isVerified || ownProfile || !comment.trim()) return;
        mutate(async (apply) => {
            await createUserComment(targetId, comment.trim());
            apply(() => setComment(''));
            const comments = await getUserComments(targetId, { page: 0 });
            apply(() => { setData((d) => ({ ...d, comments })); setQuery({ page1: 0 }); });
        });
    };

    const tabs = useMemo(() => ['overview', 'tournaments', 'teams'].map((id) => ({ id, label: t(`profile.navbar.${id}`) })), [t]);
    const sections = useMemo(() => ['active', 'finished', 'created'].map((id) => ({ id, label: t(`profile.tournaments.${id}`) })), [t]);
    if (!userId && !isAuthenticated) return <Navigate to="/login" replace/>;
    if (!validId || error?.status === 404) return <Navigate to="/404" replace/>;
    if (error?.status === 403) return <Navigate to="/403" replace/>;
    if (error) return <Layout><div className={styles.status}><p role="alert">{errorMessage(error)}</p><Button text={t('profile.retry')} onClick={() => setRetry((value) => value + 1)}/></div></Layout>;
    if (loading || !profile) return <Layout><p role="status" className={styles.status}>{t('profile.loading')}</p></Layout>;

    return <Layout pageTitle={profile.username}>
        <section className={styles.hero} style={{ backgroundImage: `url(${profile.banner ?? '/assets/moonlight.jpg'})` }}>
            <img className={styles.avatar} src={profile.profilePicture ?? '/assets/defaultPFP.jpg'} alt={profile.username}/>
            <div><h1>{profile.username}</h1><p>{profile.bio}</p><p>★ {t('profile.rating.organizerRating')} {profile.rating == null ? t('profile.rating.noRating') : `${profile.rating} (${profile.ratingCount})`}</p></div>
        </section>
        <SectionTabs sections={tabs} activeSection={tab} onChange={(value) => setQuery({ tab: value, section: null, page1: null, page2: null })} className={styles.tabs} tabClassName={styles.tab} activeTabClassName={styles.active}/>
        <main className={styles.content}>
        {tab === 'overview' && <>
            <aside className={styles.accounts}><h2>{t('profile.accounts')}</h2>{data.accounts.map((a) => <div key={a.platform}><span>{a.platform.replaceAll('_', ' ')}: {a.username}</span>{ownProfile && isVerified && <button disabled={pending} aria-label={t('profile.removeAccount', { platform: a.platform })} onClick={() => removeAccount(a.platform)}>×</button>}</div>)}
                {!data.accounts.length && <p>{t('profile.modal.noAccounts')}</p>}
                {ownProfile && isVerified && availablePlatforms.length > 0 && <form onSubmit={saveAccount}><select aria-label={t('profile.platform')} disabled={pending} value={selectedPlatform} onChange={(e) => setAccount({ ...account, platform: e.target.value })}>{availablePlatforms.map((p) => <option key={p}>{p}</option>)}</select><input aria-label={t('profile.modal.username')} disabled={pending} required maxLength={100} value={account.username} onChange={(e) => setAccount({ ...account, username: e.target.value })} placeholder={t('profile.modal.username')}/><Button disabled={pending || !account.username.trim()} type="submit" text={t('profile.modal.addusername')} size="m"/></form>}
            </aside>
            <div className={styles.overview}><h2>{t('profile.favouriteGames.title')}</h2><CardRow items={data.games.items} type="game" empty={t('profile.favouriteGames.empty', { 0: profile.username })}/><h2>{t('profile.upcomingTournaments.title')}</h2><CardRow items={data.primary.items} empty={t('profile.activeTournaments.empty')}/><h2>{t('profile.lastTournaments.title')}</h2><CardRow items={data.secondary.items} empty={t('profile.lastTournaments.empty')}/></div>
            <section className={styles.comments}><h2>{t('profile.comments.title')}</h2>{isAuthenticated && isVerified && !ownProfile && <form onSubmit={submitComment}><textarea aria-label={t('profile.comments.placeholder')} disabled={pending} required maxLength="1000" value={comment} onChange={(e) => setComment(e.target.value)} placeholder={t('profile.comments.placeholder')}/><Button disabled={pending || !comment.trim()} type="submit" text={t('profile.comments.submit')}/></form>}{data.comments.items.map((c) => <article key={c.id}><Link to={`/users/${c.commenter?.id}`}>{c.commenter?.username}</Link><time>{new Intl.DateTimeFormat(i18n.language, { dateStyle: 'medium' }).format(new Date(c.createdAt))}</time><p>{c.comment}</p></article>)}{!data.comments.items.length && <p>{t('profile.comments.empty')}</p>}<Pagination currentPage={data.comments.pagination.currentPage} totalPages={data.comments.pagination.totalPages} url={`/users/${targetId}?tab=overview`} pageNumber="1"/></section>
        </>}
        {tab === 'tournaments' && <><SectionTabs sections={sections} activeSection={section} onChange={(value) => setQuery({ section: value, page1: 0, page2: 0 })} className={styles.subtabs} tabClassName={styles.tab} activeTabClassName={styles.active}/><h2>{t(`profile.tournaments.${section}`)}</h2><CardRow items={data.primary.items} empty={t('elementGrid.noTournaments')}/><Pagination currentPage={data.primary.pagination.currentPage} totalPages={data.primary.pagination.totalPages} url={`/users/${targetId}?tab=tournaments&section=${section}`} pageNumber="1"/>{section !== 'active' && <><h2>{t(section === 'finished' ? 'profile.tournaments.won' : 'profile.tournaments.createdFinished')}</h2><CardRow items={data.secondary.items} empty={t('elementGrid.noTournaments')}/><Pagination currentPage={data.secondary.pagination.currentPage} totalPages={data.secondary.pagination.totalPages} url={`/users/${targetId}?tab=tournaments&section=${section}`} pageNumber="2"/></>}</>}
        {tab === 'teams' && <><div className={styles.heading}><h2>{t('profile.teams.title')}</h2>{ownProfile && <Button text={t('tournaments.team.butText')} onClick={() => navigate('/teams/new')}/>}</div><CardRow items={data.teams} type="team" empty={t('profile.teams.empty')}/></>}
        {actionError && <p role="alert">{errorMessage(actionError)}</p>}
        </main>
    </Layout>;
};

export const LegacyProfileRedirect = ({ tab }) => {
    const { userId } = useParams();
    return <Navigate replace to={`/users/${userId}${tab ? `?tab=${tab}` : ''}`}/>;
};
