import { useEffect, useMemo, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { Link, useLocation } from 'react-router-dom';
import { getTeamMembers, listTeams } from '../../api/teams.js';
import { Button } from '../Button.jsx';
import styles from '../../styles/pages/TournamentPage.module.css';

const isAbortError = (error) => error?.name === 'AbortError';

const buildCreateTeamPath = (location) => {
    const returnTo = `${location.pathname}${location.search}`;
    return `/teams/new?returnTo=${encodeURIComponent(returnTo)}`;
};

export const TeamJoinPanel = ({
    tournamentId,
    currentUserId,
    playersPerTeam,
    disabled,
    isSubmitting,
    onJoinTeam,
}) => {
    const { t } = useTranslation();
    const location = useLocation();
    const [entries, setEntries] = useState([]);
    const [selectedTeamId, setSelectedTeamId] = useState('');
    const [selectedMemberIds, setSelectedMemberIds] = useState([]);
    const [loading, setLoading] = useState(false);
    const [loadError, setLoadError] = useState(null);
    const [clientError, setClientError] = useState(null);

    useEffect(() => {
        if (!currentUserId || !tournamentId || playersPerTeam <= 1) {
            setEntries([]);
            return undefined;
        }

        let isMounted = true;
        const controller = new AbortController();

        const loadTeams = async () => {
            setLoading(true);
            setLoadError(null);
            setClientError(null);

            try {
                const teams = await listTeams({
                    userId: currentUserId,
                    forTournament: tournamentId,
                    signal: controller.signal,
                });
                const loadedEntries = await Promise.all(teams.map(async (team) => ({
                    team,
                    members: await getTeamMembers(team.id, { signal: controller.signal }),
                })));
                const validEntries = loadedEntries.filter(({ members }) => (
                    members.some((member) => Number(member.id) === Number(currentUserId))
                    && members.length >= playersPerTeam
                ));

                if (isMounted) {
                    setEntries(validEntries);
                    setSelectedTeamId((current) => (
                        validEntries.some(({ team }) => String(team.id) === String(current))
                            ? current
                            : ''
                    ));
                    setSelectedMemberIds([]);
                }
            } catch (error) {
                if (isMounted && !isAbortError(error)) {
                    setLoadError(error);
                    setEntries([]);
                }
            } finally {
                if (isMounted) {
                    setLoading(false);
                }
            }
        };

        loadTeams();

        return () => {
            isMounted = false;
            controller.abort();
        };
    }, [currentUserId, playersPerTeam, tournamentId]);

    const selectedEntry = useMemo(() => (
        entries.find(({ team }) => String(team.id) === String(selectedTeamId)) ?? null
    ), [entries, selectedTeamId]);

    const selectedMemberSet = useMemo(() => (
        new Set(selectedMemberIds.map((id) => Number(id)))
    ), [selectedMemberIds]);

    const handleTeamChange = (event) => {
        const nextTeamId = event.target.value;
        const nextEntry = entries.find(({ team }) => String(team.id) === String(nextTeamId));
        const includesCurrentUser = nextEntry?.members.some((member) => Number(member.id) === Number(currentUserId));

        setSelectedTeamId(nextTeamId);
        setSelectedMemberIds(includesCurrentUser ? [Number(currentUserId)] : []);
        setClientError(null);
    };

    const handleMemberToggle = (memberId) => {
        setClientError(null);
        const numericMemberId = Number(memberId);
        if (numericMemberId === Number(currentUserId)) {
            return;
        }

        setSelectedMemberIds((current) => {
            if (current.some((id) => Number(id) === numericMemberId)) {
                return current.filter((id) => Number(id) !== numericMemberId);
            }

            if (current.length >= playersPerTeam) {
                setClientError(t('tournamentDetail.teamJoin.tooManyMembers', { count: playersPerTeam }));
                return current;
            }

            return [...current, numericMemberId];
        });
    };

    const handleSubmit = async (event) => {
        event.preventDefault();
        if (!selectedEntry) {
            setClientError(t('team.selection.empty'));
            return;
        }

        if (selectedMemberIds.length !== playersPerTeam) {
            setClientError(t('team.selection.size.mismatch', { 0: playersPerTeam, count: playersPerTeam }));
            return;
        }

        if (!selectedMemberIds.some((id) => Number(id) === Number(currentUserId))) {
            setClientError(t('team.selection.currentUserRequired'));
            return;
        }

        setClientError(null);
        await onJoinTeam({
            teamId: Number(selectedEntry.team.id),
            members: selectedMemberIds,
        });
    };

    if (loading) {
        return <div className={styles.panel}>{t('tournamentDetail.teamJoin.loadingTeams')}</div>;
    }

    if (loadError) {
        return (
            <div className={`${styles.panel} ${styles.alert}`} role="alert">
                {t('tournamentDetail.teamJoin.loadError')}
            </div>
        );
    }

    if (entries.length === 0) {
        return (
            <div className={styles.teamJoinBox}>
                <p className={styles.muted}>
                    {t('tournament.join.noTeams', { 0: playersPerTeam, count: playersPerTeam })}
                </p>
                <Link className={styles.linkButton} to={buildCreateTeamPath(location)}>
                    {t('tournamentDetail.teamJoin.createTeam')}
                </Link>
            </div>
        );
    }

    return (
        <form className={styles.teamJoinBox} onSubmit={handleSubmit}>
            <fieldset className={styles.teamJoinFieldset} disabled={disabled || isSubmitting}>
                <legend className={styles.teamJoinLegend}>{t('tournament.join.chooseTeam')}</legend>
                <div className={styles.teamOptions}>
                    {entries.map(({ team, members }) => (
                        <label key={team.id} className={styles.teamOption}>
                            <input
                                type="radio"
                                name="teamId"
                                value={team.id}
                                checked={String(selectedTeamId) === String(team.id)}
                                onChange={handleTeamChange}
                            />
                            <span>{team.name}</span>
                            <span className={styles.smallText}>
                                {t('tournamentDetail.teamJoin.memberCount', { count: members.length })}
                            </span>
                        </label>
                    ))}
                </div>
            </fieldset>

            {selectedEntry && (
                <fieldset className={styles.teamJoinFieldset} disabled={disabled || isSubmitting}>
                    <legend className={styles.teamJoinLegend}>
                        {t('tournament.join.requiredSize', { 0: playersPerTeam, count: playersPerTeam })}
                    </legend>
                    <div className={styles.memberOptions}>
                        {selectedEntry.members.map((member) => (
                            <label key={member.id} className={styles.memberOption}>
                                <input
                                    type="checkbox"
                                    checked={selectedMemberSet.has(Number(member.id))}
                                    disabled={Number(member.id) === Number(currentUserId)}
                                    onChange={() => handleMemberToggle(member.id)}
                                />
                                <span>{member.username}</span>
                            </label>
                        ))}
                    </div>
                </fieldset>
            )}

            <Button
                type="submit"
                text={isSubmitting ? t('tournamentDetail.joining') : t('tournament.join.button')}
                size="m"
                disabled={disabled || isSubmitting}
            />
            {clientError && <p className={styles.formError} role="alert">{clientError}</p>}
        </form>
    );
};
