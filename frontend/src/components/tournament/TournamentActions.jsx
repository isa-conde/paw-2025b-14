import { useTranslation } from 'react-i18next';
import { Button } from '../Button.jsx';
import styles from '../../styles/pages/TournamentPage.module.css';

export const TournamentActions = ({
    tournament,
    isAuthenticated,
    isVerified,
    isParticipant,
    isOwner,
    actionError,
    actionLoading,
    statusLoading,
    onLogin,
    onJoin,
    onLeave,
    onCloseInscriptions,
    onStartTournament,
}) => {
    const { t } = useTranslation();
    const canJoin = Boolean(
        tournament.openInscriptions
        && !tournament.tournamentStarted
        && !tournament.finished
        && !isParticipant
    );

    return (
        <section className={styles.section}>
            <div className={styles.actions}>
                {isParticipant && (
                    <>
                        <div className={`${styles.panel} ${styles.success}`}>
                            {t('tournament.member')}
                        </div>
                        {!tournament.finished && (
                            <Button
                                text={actionLoading === 'leave' ? t('tournamentDetail.leaving') : t('tournament.leave.butText')}
                                size="m"
                                secondary
                                disabled={Boolean(actionLoading)}
                                onClick={onLeave}
                            />
                        )}
                    </>
                )}

                {!isParticipant && !isAuthenticated && canJoin && (
                    <Button
                        text={t('tournamentDetail.loginToJoin')}
                        size="m"
                        disabled={Boolean(actionLoading)}
                        onClick={onLogin}
                    />
                )}

                {!isParticipant && isAuthenticated && !isVerified && canJoin && (
                    <div className={`${styles.panel} ${styles.alert}`}>
                        {t('tournamentDetail.verifyToJoin')}
                    </div>
                )}

                {!isParticipant && isAuthenticated && isVerified && canJoin && (
                    <Button
                        text={actionLoading === 'join' ? t('tournamentDetail.joining') : t('tournament.join.button')}
                        size="m"
                        disabled={Boolean(actionLoading)}
                        onClick={onJoin}
                    />
                )}

                {!canJoin && !isParticipant && (
                    <div className={styles.panel}>
                        {t('tournamentDetail.joinUnavailable')}
                    </div>
                )}
            </div>

            {isOwner && (
                <div className={styles.ownerControls}>
                    {tournament.openInscriptions && !tournament.tournamentStarted && !tournament.finished && (
                        <Button
                            text={statusLoading === 'close' ? t('tournamentDetail.saving') : t('tournament.closeInscriptions')}
                            size="m"
                            secondary
                            disabled={Boolean(statusLoading)}
                            onClick={onCloseInscriptions}
                        />
                    )}
                    {!tournament.tournamentStarted && !tournament.finished && (
                        <Button
                            text={statusLoading === 'start' ? t('tournamentDetail.saving') : t('tournament.startTournament')}
                            size="m"
                            disabled={Boolean(statusLoading)}
                            onClick={onStartTournament}
                        />
                    )}
                </div>
            )}

            {actionError && (
                <div className={`${styles.panel} ${styles.alert}`} role="alert">
                    {actionError}
                </div>
            )}
        </section>
    );
};
