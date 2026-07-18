import { useTranslation } from 'react-i18next';
import styles from '../../styles/pages/TournamentPage.module.css';

const enumLabel = (t, prefix, value, fallbackKey) => {
    if (!value) {
        return t(fallbackKey);
    }
    return t(`${prefix}.${value}`, { defaultValue: value });
};

const fieldValue = (value, fallback) => {
    if (value === null || value === undefined || value === '') {
        return fallback;
    }
    return value;
};

const InfoTile = ({ title, value }) => (
    <div className={styles.infoCard}>
        <p className={styles.infoTitle}>{title}</p>
        <p className={styles.infoValue}>{value}</p>
    </div>
);

export const TournamentOverview = ({
    tournament,
    game,
    creator,
    participantsCount,
    formatDate,
}) => {
    const { t } = useTranslation();
    const status = tournament.finished
        ? t('tournamentDetail.status.finished')
        : tournament.tournamentStarted
            ? t('tournamentDetail.status.started')
            : tournament.openInscriptions
                ? t('tournamentDetail.status.open')
                : t('tournamentDetail.status.closed');

    return (
        <section className={styles.section}>
            <h2 className={styles.sectionTitle}>{t('tournament.overview')}</h2>
            <div className={styles.overviewGrid}>
                <InfoTile title={t('tournamentDetail.field.status')} value={status} />
                <InfoTile
                    title={t('tournament.participants.title')}
                    value={t('tournamentDetail.participantsCount', {
                        current: participantsCount,
                        max: tournament.maxParticipants,
                    })}
                />
                <InfoTile title={t('createTournament.game')} value={fieldValue(game?.name, t('tournamentDetail.unknown.game'))} />
                <InfoTile title={t('tournament.organizedBy')} value={fieldValue(creator?.username, t('tournamentDetail.unknown.creator'))} />
                <InfoTile title={t('createTournament.startDate')} value={formatDate(tournament.startDate)} />
                <InfoTile title={t('createTournament.endDate')} value={formatDate(tournament.endDate)} />
                <InfoTile
                    title={t('createTournament.region')}
                    value={enumLabel(t, 'region', tournament.region, 'tournamentDetail.unknown.region')}
                />
                <InfoTile
                    title={t('createTournament.skillLevel')}
                    value={enumLabel(t, 'elo', tournament.elo, 'tournamentDetail.unknown.elo')}
                />
                <InfoTile title={t('createTournament.format')} value={fieldValue(tournament.format, t('tournamentDetail.unknown.format'))} />
                <InfoTile
                    title={t('createTournament.structure')}
                    value={enumLabel(t, 'structure', tournament.structure, 'tournamentDetail.unknown.structure')}
                />
                <div className={`${styles.infoCard} ${styles.wide}`}>
                    <p className={styles.infoTitle}>{t('tournament.serverName')}</p>
                    <p className={styles.infoValue}>{fieldValue(tournament.serverName, t('tournamentDetail.unknown.server'))}</p>
                </div>
                <div className={`${styles.infoCard} ${styles.wide}`}>
                    <p className={styles.infoTitle}>{t('tournament.discordChannel')}</p>
                    <p className={styles.infoValue}>{fieldValue(tournament.discordChannel, t('tournamentDetail.unknown.discord'))}</p>
                </div>
            </div>
        </section>
    );
};
