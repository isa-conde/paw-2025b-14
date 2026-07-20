import { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useParams } from "react-router-dom";
import { getTeam, getTeamMembers, getTeamOwner } from "../api/teams.js";
import { Layout } from "../components/Layout.jsx";
import { ForbiddenPage } from "./ForbiddenPage.jsx";
import { NotFoundPage } from "./NotFoundPage.jsx";
import styles from "../styles/pages/TeamPage.module.css";

const defaultAvatar = "/assets/defaultPFP.jpg";
const defaultBanner = "/assets/tournament.jpeg";

const isAbortError = (error) => error?.name === "AbortError";

export const TeamPage = () => {
    const { t } = useTranslation();
    const { teamId } = useParams();
    const [team, setTeam] = useState(null);
    const [owner, setOwner] = useState(null);
    const [members, setMembers] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        let isMounted = true;
        const controller = new AbortController();

        const loadTeam = async () => {
            setLoading(true);
            setError(null);

            try {
                const loadedTeam = await getTeam(Number(teamId), { signal: controller.signal });
                const [loadedMembers, loadedOwner] = await Promise.all([
                    getTeamMembers(loadedTeam.id, { signal: controller.signal }),
                    getTeamOwner(loadedTeam, { signal: controller.signal }).catch((ownerError) => {
                        if (isAbortError(ownerError)) {
                            throw ownerError;
                        }
                        return null;
                    }),
                ]);

                if (isMounted) {
                    setTeam(loadedTeam);
                    setMembers(loadedMembers);
                    setOwner(loadedOwner);
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

        loadTeam();

        return () => {
            isMounted = false;
            controller.abort();
        };
    }, [teamId]);

    if (loading) {
        return (
            <Layout pageTitle={t("team.profile.loading")}>
                <div className={styles.state}>
                    <h1 className={styles.sectionTitle}>{t("team.profile.loading")}</h1>
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
            <Layout pageTitle={t("team.profile.error.title")}>
                <div className={styles.state} role="alert">
                    <h1 className={styles.sectionTitle}>{t("team.profile.error.title")}</h1>
                    <p className={styles.muted}>{t("team.profile.error.load")}</p>
                </div>
            </Layout>
        );
    }

    if (!team) {
        return <NotFoundPage />;
    }

    return (
        <Layout pageTitle={team.name}>
            <article className={styles.page}>
                <header className={styles.hero}>
                    <div
                        className={styles.heroImage}
                        style={{ backgroundImage: `url(${team.banner ?? defaultBanner})` }}
                    />
                    <div className={styles.heroContent}>
                        <img
                            className={styles.avatar}
                            src={team.profilePicture ?? defaultAvatar}
                            alt=""
                        />
                        <div>
                            <p className={styles.eyebrow}>{t("team.profile.overview")}</p>
                            <h1 className={styles.title}>{team.name}</h1>
                            <p className={styles.muted}>
                                {t("team.profile.createdBy")} {owner?.username ?? t("team.profile.unknownOwner")}
                            </p>
                        </div>
                    </div>
                </header>

                <section className={styles.section}>
                    <h2 className={styles.sectionTitle}>{t("team.profile.members")}</h2>
                    {members.length === 0 ? (
                        <p className={styles.empty}>{t("team.profile.noMembers")}</p>
                    ) : (
                        <div className={styles.membersGrid}>
                            {members.map((member) => (
                                <article key={member.id} className={styles.memberCard}>
                                    <img
                                        className={styles.memberAvatar}
                                        src={member.profilePicture ?? defaultAvatar}
                                        alt=""
                                    />
                                    <div>
                                        <p className={styles.memberName}>{member.username}</p>
                                        {Number(member.id) === Number(team.ownerId) && (
                                            <p className={styles.smallText}>{t("team.profile.ownerBadge")}</p>
                                        )}
                                    </div>
                                </article>
                            ))}
                        </div>
                    )}
                </section>
            </article>
        </Layout>
    );
};
