import styles from "../styles/pages/TournamentsPage.module.css"
import {useTranslation} from "react-i18next";
import {Layout} from "../components/Layout.jsx";
import {Banner} from "../components/Banner.jsx";
import {AppText} from "../components/AppText.jsx";
import {useNavigate} from "react-router-dom";
import {Button} from "../components/Button.jsx";
import {Input} from "../components/Input.jsx";
import {RefreshButton} from "../components/RefreshButton.jsx";
import {ElementsGrid} from "../components/ElementsGrid.jsx";

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
    const gameLabel = t("tournaments.game");
    const regionLabel = t("tournaments.region");
    const eloLabel = t("tournaments.skillLevel");
    const genreLabel = t("tournaments.genre");
    const playersPerTeamLabel = t("tournaments.playerAmount");
    const startDateLabel = t("createTournament.startDate");
    const endDateLabel = t("createTournament.endDate");
    const filterLabel = t("tournaments.filter");

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
                            <Input id="gameId" name="gameId" label={gameLabel} type="select" items={} itemValue="id" itemLabel="name" emptyOption={allGames} inline={true}/>
                            <Input id="region" name="region" label={regionLabel} type="select" items={} emptyOption={allRegions} inline={true}/>
                            <Input id="elo" name="elo" label={eloLabel} type="select" items={} itemMap={} emptyOption={allLevels} inline={true}/>
                            <Input id="genre" name="genre" label={genreLabel} type="select" items={} emptyOption={allGenres} inline={true}/>
                        </div>
                        <div className={styles["filter-container"]}>
                            <Input id="playersPerTeam" name="playersPerTeam" label={playersPerTeamLabel} type="select" items={} emptyOption={allSizes} inline={true}/>
                            <Input id="startDate" name="startDate" label={startDateLabel} type="date" inline={true}/>
                            <Input id="endDate" name="endDate" label={endDateLabel} type="date" inline={true}/>
                            <Input label={filterLabel} type="submit" inline={true}/>
                            <RefreshButton disabled={!isFiltered}/>
                        </div>
                    </div>
                </form>

                {isFiltered ?
                    <ElementsGrid elements={} id="tournaments-grid" headerElements={}/>
                    :

                }


            </div>
        </Layout>
    );
}