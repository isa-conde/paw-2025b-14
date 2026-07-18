import { useTranslation } from 'react-i18next';
import { Button } from '../Button.jsx';
import styles from '../../styles/pages/TournamentPage.module.css';

const scoreLabel = (score, fallback) => (
    score === null || score === undefined ? fallback : score
);

const participantName = (participantId, participantsById, fallback) => (
    participantId ? participantsById[participantId]?.name ?? fallback : fallback
);

const MatchResultsForm = ({
    match,
    localName,
    visitorName,
    disabled,
    isSubmitting,
    onSetResults,
}) => {
    const { t } = useTranslation();

    const handleSubmit = (event) => {
        event.preventDefault();
        const formData = new FormData(event.currentTarget);
        onSetResults(match.id, {
            localScore: Number(formData.get('localScore')),
            visitorScore: Number(formData.get('visitorScore')),
        });
    };

    return (
        <form className={styles.resultForm} onSubmit={handleSubmit}>
            <label className={styles.field}>
                <span className={styles.fieldLabel}>
                    {t('tournamentDetail.results.localScoreFor', { participant: localName })}
                </span>
                <input
                    className={styles.scoreInput}
                    name="localScore"
                    type="number"
                    min="0"
                    defaultValue={match.localScore ?? ''}
                    disabled={disabled || isSubmitting}
                    required
                />
            </label>
            <label className={styles.field}>
                <span className={styles.fieldLabel}>
                    {t('tournamentDetail.results.visitorScoreFor', { participant: visitorName })}
                </span>
                <input
                    className={styles.scoreInput}
                    name="visitorScore"
                    type="number"
                    min="0"
                    defaultValue={match.visitorScore ?? ''}
                    disabled={disabled || isSubmitting}
                    required
                />
            </label>
            <Button
                type="submit"
                size="s"
                text={isSubmitting ? t('tournamentDetail.saving') : t('tournament.setMatchResults.set')}
                disabled={disabled || isSubmitting}
            />
        </form>
    );
};

export const TournamentMatches = ({
    stages,
    participantsById,
    isOwner,
    resultLoadingId,
    onSetResults,
    formatDate,
}) => {
    const { t } = useTranslation();
    const tbd = t('tournamentDetail.matches.tbd');
    const notPlayed = t('tournament.match.notPlayed');

    return (
        <section className={styles.section}>
            <h2 className={styles.sectionTitle}>{t('tournament.matches')}</h2>
            {stages.length === 0 ? (
                <p className={styles.empty}>{t('tournament.noMatches')}</p>
            ) : (
                stages.map((stage) => (
                    <div key={stage.stage} className={styles.stageBlock}>
                        <h3 className={styles.stageTitle}>
                            {t('tournamentDetail.matches.stage', { stage: stage.stage })}
                        </h3>
                        <div className={styles.matchGrid}>
                            {stage.matches.map((match) => {
                                const localName = participantName(match.localParticipantId, participantsById, tbd);
                                const visitorName = participantName(match.visitorParticipantId, participantsById, tbd);
                                const hasBothParticipants = Boolean(match.localParticipantId && match.visitorParticipantId);

                                return (
                                    <article key={match.id} className={styles.matchCard}>
                                        <div className={styles.matchTeams}>
                                            <div className={styles.matchSide}>
                                                <p className={styles.matchName}>{localName}</p>
                                                <p className={styles.matchScore}>{scoreLabel(match.localScore, '-')}</p>
                                            </div>
                                            <span className={styles.versus}>{t('tournamentDetail.matches.versus')}</span>
                                            <div className={styles.matchSide}>
                                                <p className={styles.matchName}>{visitorName}</p>
                                                <p className={styles.matchScore}>{scoreLabel(match.visitorScore, '-')}</p>
                                            </div>
                                        </div>
                                        <p className={styles.muted}>
                                            {match.localScore === null || match.localScore === undefined
                                                ? notPlayed
                                                : t('tournamentDetail.matches.played')}
                                            {' '}
                                            {formatDate(match.date)}
                                        </p>
                                        {isOwner && (
                                            <MatchResultsForm
                                                match={match}
                                                localName={localName}
                                                visitorName={visitorName}
                                                disabled={!hasBothParticipants}
                                                isSubmitting={resultLoadingId === match.id}
                                                onSetResults={onSetResults}
                                            />
                                        )}
                                    </article>
                                );
                            })}
                        </div>
                    </div>
                ))
            )}
        </section>
    );
};
