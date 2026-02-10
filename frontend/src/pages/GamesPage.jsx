import styles from "../styles/pages/GamesPage.module.css"
import {Layout} from "../components/Layout.jsx";
import {useTranslation} from "react-i18next";
import {Banner} from "../components/Banner.jsx";
import {AppText} from "../components/AppText.jsx";
import {ElementsGrid} from "../components/ElementsGrid.jsx";
import {Pagination} from "../components/Pagination.jsx";

export const GamesPage = () => {
    const { t } = useTranslation();

    const pageTitle = t("games.title");

    const moonlightImage = "/assets/moonlight.jpg";

    const gamesPageUrl = "/gamesPage";

    const pageSize = 6;

    const games = [
        { id: 1, name: "League of Legends" },
        { id: 2, name: "Valorant" },
        { id: 3, name: "Counter-Strike 2" },
        { id: 4, name: "Dota 2" },
        { id: 5, name: "Overwatch 2" },
        { id: 6, name: "Rocket League" },
        { id: 7, name: "Fortnite" },
        { id: 8, name: "Apex Legends" },
        { id: 9, name: "Rainbow Six Siege" },
        { id: 10, name: "Call of Duty: Warzone" },
        { id: 11, name: "PUBG: Battlegrounds" },
        { id: 12, name: "Teamfight Tactics" },
    ];

    const totalPages = Math.max(1, Math.ceil(games.length / pageSize));
    const currentPageParam = Number.parseInt(
        new URLSearchParams(window.location.search).get("page") ?? "0",
        10,
    );
    const currentPage = Number.isNaN(currentPageParam)
        ? 0
        : Math.min(Math.max(currentPageParam, 0), totalPages - 1);
    const pagedGames = games.slice(currentPage * pageSize, currentPage * pageSize + pageSize);

    return (
        <Layout pageTitle={pageTitle}>
            <Banner size="s" image={moonlightImage}>
                <AppText type="title" size="xl" stroke={true}>{pageTitle}</AppText>
            </Banner>
            <div className={styles["content-container"]}>
                <ElementsGrid elements={pagedGames} id="games-grid" isGame={true}/>
            </div>
            <Pagination
                currentPage={currentPage}
                totalPages={totalPages}
                url={gamesPageUrl}
            />
        </Layout>
    );
}

// TODO: un-hardcode the games and the pagination