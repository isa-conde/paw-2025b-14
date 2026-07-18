import {Layout} from "../components/Layout.jsx";
import {Banner} from "../components/Banner.jsx";
import {AppText} from "../components/AppText.jsx";
import {useTranslation} from "react-i18next";
import styles from "../styles/pages/HomePage.module.css"
import {ButtonCard} from "../components/ButtonCard.jsx";
import {Link, useNavigate} from "react-router-dom";
import {Carrousel} from "../components/Carrousel.jsx";
import {useEffect, useState} from "react";
import {getGame, listGames} from "../api/games.js";
import {listTournaments} from "../api/tournaments.js";

export const HomePage = () => {
    const { t } = useTranslation();
    const navigate = useNavigate();
    const [games, setGames] = useState([]);
    const [tournamentSections, setTournamentSections] = useState([]);
    const [gamesLoading, setGamesLoading] = useState(true);
    const [gamesError, setGamesError] = useState(null);
    const [tournamentsLoading, setTournamentsLoading] = useState(true);
    const [tournamentsError, setTournamentsError] = useState(null);

    const handleNavigate = (url) => {
        navigate(url);
    };

    useEffect(() => {
        let isMounted = true;
        const controller = new AbortController();

        const loadGames = async () => {
            setGamesLoading(true);
            setGamesError(null);

            try {
                const gamesResponse = await listGames({page: 0, signal: controller.signal});
                if (!isMounted) {
                    return;
                }

                setGames(gamesResponse.items);
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
                const tournamentsResponse = await listTournaments({page: 0, signal: controller.signal});
                const tournaments = tournamentsResponse.items;
                const gameIds = [...new Set(tournaments.map((tournament) => tournament.gameId).filter(Boolean))];
                const gameResults = await Promise.allSettled(
                    gameIds.map((gameId) => getGame(gameId, {signal: controller.signal})
                        .then((game) => [gameId, game])),
                );

                if (!isMounted) {
                    return;
                }

                const gamesById = new Map(gameResults
                    .filter(({status}) => status === "fulfilled")
                    .map(({value}) => value));

                setTournamentSections(gameIds
                    .map((gameId) => ({
                        game: gamesById.get(gameId),
                        tournaments: tournaments.filter((tournament) => tournament.gameId === gameId),
                    }))
                    .filter(({game, tournaments}) => game && tournaments.length > 0));
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
    }, []);

    const createTournamentUrl = "/tournaments/new/step1";
    const joinTournamentUrl = "/tournaments";
    const gamesPageUrl = "/games";
    const tournamentsGameUrl = "/tournaments";

    const bannerImage = "/assets/arcane.jpg";

    const bannerTitle = t("home.welcome.title");
    const bannerSubtitle = t("home.welcome.subtitle");
    const createTournamentTitle = t("createTournament.title");
    const joinTournamentTitle = t("home.joinTournament.title");
    const createTournamentButtonText = t("createTournament.butText");
    const joinTournamentButtonText = t("home.joinTournament.butText");
    const gamesPageLabel = t("home.games");
    const tournamentsLabel = t("home.tournaments");
    const loadingLabel = t("home.loading");
    const errorLabel = t("home.error");
    const noGamesLabel = t("home.noGames");
    const noTournamentsLabel = t("home.noTournaments");
    const hasTournamentSections = tournamentSections.some(({tournaments}) => tournaments.length > 0);
    
    return (
        <Layout>
            <Banner size="l" image={bannerImage}>
                <AppText type="title" size="xl">{bannerTitle}</AppText>
                <br/>
                <AppText type="title" size="l">{bannerSubtitle}</AppText>
            </Banner>
            <div className={styles["content-container"]}>
                <div className={styles["cards-container"]}>
                    <ButtonCard
                        title={createTournamentTitle}
                        buttonText={createTournamentButtonText}
                        onClick={() => handleNavigate(createTournamentUrl)}
                        texture={true}/>
                    <ButtonCard
                        title={joinTournamentTitle}
                        buttonText={joinTournamentButtonText}
                        onClick={() => handleNavigate(joinTournamentUrl)}
                        texture={true}/>
                </div>
                <div className={styles["content-title"]}>
                    <Link to={gamesPageUrl} className={styles["title-link"]}>
                        <AppText type="title" size="l">{gamesPageLabel}</AppText>
                    </Link>
                </div>
                {gamesLoading && <AppText size="l" weight="thin">{loadingLabel}</AppText>}
                {gamesError && <div role="alert"><AppText size="l" weight="thin">{errorLabel}</AppText></div>}
                {!gamesLoading && !gamesError && games.length === 0 && (
                    <AppText size="l" weight="thin">{noGamesLabel}</AppText>
                )}
                {!gamesLoading && !gamesError && games.length > 0 && <Carrousel elements={games} isGame={true}/>}
                <div className={styles["content-title"]}>
                    <Link to={joinTournamentUrl} className={styles["title-link"]}>
                        <AppText type="title" size="l">{tournamentsLabel}</AppText>
                    </Link>
                </div>
                {tournamentsLoading && <AppText size="l" weight="thin">{loadingLabel}</AppText>}
                {tournamentsError && <div role="alert"><AppText size="l" weight="thin">{errorLabel}</AppText></div>}
                {!tournamentsLoading && !tournamentsError && !hasTournamentSections && (
                    <AppText size="l" weight="thin">{noTournamentsLabel}</AppText>
                )}
                {!tournamentsLoading && !tournamentsError && tournamentSections.map(({game, tournaments}) => (
                    tournaments.length > 0 && (
                        <div key={game.id}>
                            <div className={styles["content-title"]}>
                                <Link to={`${tournamentsGameUrl}?gameId=${game.id}`} className={styles["title-link"]}>
                                    <AppText type="title" size="s">{game.name}</AppText>
                                </Link>
                                <Carrousel elements={tournaments} isGame={false}/>
                            </div>
                        </div>
                    )
                ))}
            </div>
        </Layout>
    );
}
