import styles from "../styles/pages/TournamentsPage.module.css"
import {useTranslation} from "react-i18next";
import {Layout} from "../components/Layout.jsx";
import {Banner} from "../components/Banner.jsx";
import {AppText} from "../components/AppText.jsx";
import {useNavigate, useSearchParams} from "react-router-dom";
import {Button} from "../components/Button.jsx";
import {Input} from "../components/Input.jsx";
import {RefreshButton} from "../components/RefreshButton.jsx";
import {ElementsGrid} from "../components/ElementsGrid.jsx";
import {Pagination} from "../components/Pagination.jsx";
import {useEffect, useMemo, useState} from "react";
import {listAllGames} from "../api/games.js";
import {listTournaments} from "../api/tournaments.js";
import {parsePageParam} from "../api/pagination.js";
import {
    getEloOptions,
    getGenreOptions,
    getRegionOptions,
    PLAYERS_PER_TEAM_OPTIONS,
    TOURNAMENT_FILTER_KEYS
} from "../domain/tournamentFilters.js";

export const TournamentsPage = () => {
    const { t } = useTranslation();
    const navigate = useNavigate();
    const [searchParams, setSearchParams] = useSearchParams();
    const [games, setGames] = useState([]);
    const [tournaments, setTournaments] = useState([]);
    const [pagination, setPagination] = useState({currentPage: 0, totalPages: 0});
    const [gamesLoading, setGamesLoading] = useState(true);
    const [tournamentsLoading, setTournamentsLoading] = useState(true);
    const [gamesError, setGamesError] = useState(null);
    const [tournamentsError, setTournamentsError] = useState(null);

    const filterValues = useMemo(() => TOURNAMENT_FILTER_KEYS.reduce((values, key) => ({
        ...values,
        [key]: searchParams.get(key) ?? "",
    }), {}), [searchParams]);

    const hasQueryParams = Array.from(searchParams.keys()).length > 0;
    const currentPage = parsePageParam(searchParams.get("page"));
    const regionOptions = useMemo(() => getRegionOptions(t), [t]);
    const eloOptions = useMemo(() => getEloOptions(t), [t]);
    const genreOptions = useMemo(() => getGenreOptions(t), [t]);

    useEffect(() => {
        let isMounted = true;
        const controller = new AbortController();

        const loadGames = async () => {
            setGamesLoading(true);
            setGamesError(null);

            try {
                const loadedGames = await listAllGames({signal: controller.signal});
                if (isMounted) {
                    setGames(loadedGames);
                }
            } catch (loadError) {
                if (isMounted && loadError.name !== "AbortError") {
                    setGamesError(loadError);
                }
            } finally {
                if (isMounted) {
                    setGamesLoading(false);
                }
            }
        };

        loadGames();

        return () => {
            isMounted = false;
            controller.abort();
        };
    }, []);

    useEffect(() => {
        let isMounted = true;
        const controller = new AbortController();

        const loadTournaments = async () => {
            setTournamentsLoading(true);
            setTournamentsError(null);

            try {
                const response = await listTournaments({
                    page: currentPage,
                    ...filterValues,
                    signal: controller.signal,
                });

                if (!isMounted) {
                    return;
                }

                setTournaments(response.items);
                setPagination(response.pagination);
            } catch (loadError) {
                if (isMounted && loadError.name !== "AbortError") {
                    setTournamentsError(loadError);
                }
            } finally {
                if (isMounted) {
                    setTournamentsLoading(false);
                }
            }
        };

        loadTournaments();

        return () => {
            isMounted = false;
            controller.abort();
        };
    }, [currentPage, filterValues]);

    const handleNavigate = (url) => {
        navigate(url);
    };

    const handleFilterSubmit = (event) => {
        event.preventDefault();
    };

    const handleFilterChange = (event) => {
        const {name, value} = event.target;
        const nextParams = new URLSearchParams(searchParams);
        if (value) {
            nextParams.set(name, value);
        } else {
            nextParams.delete(name);
        }
        nextParams.delete("page");
        setSearchParams(nextParams);
    }

    const handleResetFilters = () => {
        setSearchParams({});
    };

    const createTournamentUrl = "/tournaments/new/step1";
    const createTeamUrl = "/team/create";
    const tournamentsPageUrl = "/tournaments"

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
    const filterLabel = t("tournaments.filter");
    const loadingLabel = t("tournaments.loading");
    const errorLabel = t("tournaments.error");
    const isLoading = tournamentsLoading;
    const hasError = tournamentsError;

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
                            items={games}
                            itemValue="id"
                            itemLabel="name"
                            emptyOption={allGames}
                            inline={true}
                            value={filterValues.gameId}
                            disabled={gamesLoading || !!gamesError}
                            onChange={handleFilterChange}
                        />
                        <Input
                            id="region"
                            name="region"
                            label={regionLabel}
                            type="select"
                            itemMap={regionOptions}
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
                            itemMap={eloOptions}
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
                            itemMap={genreOptions}
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
                            items={PLAYERS_PER_TEAM_OPTIONS}
                            emptyOption={allSizes}
                            inline={true}
                            value={filterValues.playersPerTeam}
                            onChange={handleFilterChange}
                        />
                        <Input label={filterLabel} type="submit" inline={true}/>
                        <RefreshButton disabled={!hasQueryParams} onClick={handleResetFilters}/>
                    </div>
                </form>

                {isLoading && !hasError && <AppText size="l" weight="thin">{loadingLabel}</AppText>}
                {hasError && (
                    <div role="alert">
                        <AppText size="l" weight="thin">{errorLabel}</AppText>
                    </div>
                )}
                {!isLoading && !hasError && (
                    <ElementsGrid elements={tournaments} id="tournaments-grid" headerElements={games}/>
                )}
                {!isLoading && !hasError && (
                    <Pagination currentPage={pagination.currentPage} totalPages={pagination.totalPages} url={tournamentsPageUrl}/>
                )}
            </div>
        </Layout>
    );
}
