import { useTranslation } from 'react-i18next';
import styles from '../../styles/pages/TournamentPage.module.css';

const defaultAvatar = '/assets/defaultPFP.jpg';

export const TournamentParticipants = ({ participants, currentParticipantId }) => {
    const { t } = useTranslation();

    return (
        <section className={styles.section}>
            <h2 className={styles.sectionTitle}>{t('tournament.participants.title')}</h2>
            {participants.length === 0 ? (
                <p className={styles.empty}>{t('usersGrid.noParticipants')}</p>
            ) : (
                <div className={styles.participantsGrid}>
                    {participants.map((participant) => (
                        <article key={participant.id} className={styles.participantCard}>
                            <img
                                className={styles.avatar}
                                src={participant.profilePicture ?? defaultAvatar}
                                alt=""
                            />
                            <div>
                                <p className={styles.participantName}>
                                    {participant.name}
                                    {participant.id === currentParticipantId ? ` ${t('tournamentDetail.participants.current')}` : ''}
                                </p>
                                <p className={styles.muted}>
                                    {participant.points ?? 0} {t('tournament.participants.points')}
                                </p>
                                {participant.groupNumber !== null && participant.groupNumber !== undefined && (
                                    <p className={styles.smallText}>
                                        {t('tournamentDetail.participants.group', { group: participant.groupNumber })}
                                    </p>
                                )}
                            </div>
                        </article>
                    ))}
                </div>
            )}
        </section>
    );
};
