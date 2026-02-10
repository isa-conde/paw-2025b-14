import styles from "../styles/pages/TournamentsPage.module.css"
import {useTranslation} from "react-i18next";
import {Layout} from "../components/Layout.jsx";
import {Banner} from "../components/Banner.jsx";
import {AppText} from "../components/AppText.jsx";
import {useNavigate} from "react-router-dom";
import {Button} from "../components/Button.jsx";

export const TournamentsPage = () => {
    const { t } = useTranslation();
    const navigate = useNavigate();

    const handleNavigate = (url) => {
        navigate(url);
    };

    const createTournamentUrl = "/tournaments/new/step1";
    const createTeamUrl = "/team/create";

    const tournamentImage = "/assets/tournament.jpeg"

    const allGames = t("tournaments.game.emptyOption");
    const allRegions = t("tournaments.region.emptyOption");
    const allLevels = t("tournaments.skillLevel.emptyOption");
    const allGenres = t("tournaments.genre.emptyOption");
    const allSizes = t("tournaments.playerAmount.emptyOption");
    const pageTitle = t("tournaments.title");
    const createTournamentLabel = t("createTournament.butText");
    const createTeamLabel = t("tournaments.team.butText");

    return (
        <Layout pageTitle={pageTitle}>
            <Banner image={tournamentImage}>
                <div className={styles["page-title"]}>
                    <AppText type="title" size="xl" stroke={true}>{pageTitle}</AppText>
                </div>
            </Banner>
            <div className={styles["content-container"]}>
                <div className={styles["tournament-buttons-container"]}>
                    <Button onClick={() => handleNavigate(createTournamentUrl)} text={createTournamentLabel} size="l"/>
                    <Button onClick={() => handleNavigate(createTeamUrl)} text={createTeamLabel} size="l"/>
                </div>
                <form className={styles.form} method="get">
                    <div className={styles["filter-container"]}>
                        <div className={styles["filter-container"]}>

                        </div>
                    </div>
                </form>
            </div>
        </Layout>
    );
}