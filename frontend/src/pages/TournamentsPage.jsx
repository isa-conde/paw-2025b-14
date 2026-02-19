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
import {Carrousel} from "../components/Carrousel.jsx";
import {Pagination} from "../components/Pagination.jsx";
import {useMemo, useState} from "react";

export const TournamentsPage = () => {
    const { t } = useTranslation();
    const navigate = useNavigate();
    const [isFiltered, setIsFiltered] = useState(false);
    const [filterValues, setFilterValues] = useState({
        gameId: "",
        region: "",
        elo: "",
        genre: "",
        playersPerTeam: "",
        startDate: "",
        endDate: "",
    });

    const hasAppliedFilters = useMemo(
        () => Object.values(filterValues).some((value) => value !== ""),
        [filterValues],
    );

    const playersPerTeamOptions = [1, 2, 3, 4, 5];

    const hardcodedGames = [
        { id: 1, name: "League of Legends" },
        { id: 2, name: "Valorant" },
        { id: 3, name: "Counter-Strike" },
        { id: 4, name: "Dota 2" },
        { id: 5, name: "Overwatch" },
    ];

    const hardcodedRegions = ["NA", "LAS", "LAN", "BR", "EUW", "EUNE", "OCE", "ASIA"];
    const hardcodedElos = {
        LOW: t("elo.LOW"),
        MID: t("elo.MID"),
        HIGH: t("elo.HIGH"),
        FREE: t("elo.FREE"),
    };
    const hardcodedGenres = ["MOBA", "FPS", "Fighting", "TPS", "BattleRoyale", "RTS", "Sports", "DGC", "MOBILE"];

    const hardcodedGameTournaments = [
        {
            game: hardcodedGames[0],
            tournaments: [
                { id: 101, name: "LoL Tournament 1" },
                { id: 102, name: "LoL Tournament 2" },
                { id: 103, name: "LoL Tournament 3" },
            ],
        },
        {
            game: hardcodedGames[1],
            tournaments: [
                { id: 201, name: "Valorant Tournament 1" },
                { id: 202, name: "Valorant Tournament 2" },
            ],
        },
        {
            game: hardcodedGames[2],
            tournaments: [
                { id: 301, name: "CS:GO Tournament 1" },
                { id: 302, name: "CS:GO Tournament 2" },
                { id: 303, name: "CS:GO Tournament 3" },
            ],
        },
        {
            game: hardcodedGames[3],
            tournaments: [
                { id: 401, name: "Dota 2 Tournament 1" },
                { id: 402, name: "Dota 2 Tournament 2" },
            ],
        },
        {
            game: hardcodedGames[4],
            tournaments: [
                { id: 501, name: "Overwatch Tournament 1" },
            ],
        },
    ];

    const hardcodedTournaments = hardcodedGameTournaments.flatMap(({ game, tournaments }) =>
        tournaments.map((tournament) => ({
            ...tournament,
            gameId: game.id,
        })),
    );

    const pageSize = 6;
    const totalPages = Math.max(1, Math.ceil(hardcodedTournaments.length / pageSize));
    const currentPageParam = Number.parseInt(
        new URLSearchParams(window.location.search).get("page") ?? "0",
        10,
    );
    const currentPage = Number.isNaN(currentPageParam)
        ? 0
        : Math.min(Math.max(currentPageParam, 0), totalPages - 1);
    const pagedTournaments = hardcodedTournaments.slice(
        currentPage * pageSize,
        currentPage * pageSize + pageSize,
    );

    const handleNavigate = (url) => {
        navigate(url);
    };

    const handleFilterSubmit = (event) => {
        event.preventDefault();
        setIsFiltered(hasAppliedFilters);
    };

    const handleFilterChange = (event) => {
        const {name, value} = event.target;
        setFilterValues((currentValues) => ({
            ...currentValues,
            [name]: value,
        }));
    }

    const createTournamentUrl = "/tournaments/new/step1";
    const createTeamUrl = "/team/create";
    const tournamentsPageUrl = "/tournamentsPage"

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
                <form className={styles.form} method="get" onSubmit={handleFilterSubmit}>
                    <div className={styles["filter-container"]}>
                        <Input
                            id="gameId"
                            name="gameId"
                            label={gameLabel}
                            type="select"
                            items={hardcodedGames}
                            itemValue="id"
                            itemLabel="name"
                            emptyOption={allGames}
                            inline={true}
                            value={filterValues.gameId}
                            onChange={handleFilterChange}
                        />
                        <Input
                            id="region"
                            name="region"
                            label={regionLabel}
                            type="select"
                            items={hardcodedRegions}
                            emptyOption={allRegions}
                            inline={true}
                            value={filterValues.region}
                            onChange={handleFilterChange}
                        />
                        <Input
                            id="elo"
                            name="elo"
                            label={eloLabel}
                            type="select"
                            itemMap={hardcodedElos}
                            emptyOption={allLevels}
                            inline={true}
                            value={filterValues.elo}
                            onChange={handleFilterChange}
                        />
                        <Input
                            id="genre"
                            name="genre"
                            label={genreLabel}
                            type="select"
                            items={hardcodedGenres}
                            emptyOption={allGenres}
                            inline={true}
                            value={filterValues.genre}
                            onChange={handleFilterChange}
                        />
                    </div>
                    <div className={styles["filter-container"]}>
                        <Input
                            id="playersPerTeam"
                            name="playersPerTeam"
                            label={playersPerTeamLabel}
                            type="select"
                            items={playersPerTeamOptions}
                            emptyOption={allSizes}
                            inline={true}
                            value={filterValues.playersPerTeam}
                            onChange={handleFilterChange}
                        />
                        <Input
                            id="startDate"
                            name="startDate"
                            label={startDateLabel}
                            type="date"
                            inline={true}
                            value={filterValues.startDate}
                            onChange={handleFilterChange}
                        />
                        <Input
                            id="endDate"
                            name="endDate"
                            label={endDateLabel}
                            type="date"
                            inline={true}
                            value={filterValues.endDate}
                            onChange={handleFilterChange}
                        />
                        <Input label={filterLabel} type="submit" inline={true}/>
                        <RefreshButton disabled={!hasAppliedFilters}/>
                    </div>
                </form>

                {isFiltered ?
                    <ElementsGrid elements={pagedTournaments} id="tournaments-grid" headerElements={hardcodedGames}/>
                    :
                    hardcodedGameTournaments.map(({game, tournaments}) => (
                        tournaments.length > 0 && (
                            <div key={game.id}>
                                <div className={styles["carrousel-title"]}>
                                    <a href={`/tournamentsPage?gameId=${game.id}`} className={styles["title-link"]}>
                                        <AppText type="title" size="s">{game.name}</AppText>
                                    </a>
                                </div>
                                <Carrousel id={`game-${game.id}-tournaments`} elements={tournaments}/>
                            </div>
                        )
                    ))
                }
                <Pagination currentPage={currentPage} totalPages={totalPages} url={tournamentsPageUrl}/>
            </div>
        </Layout>
    );
}