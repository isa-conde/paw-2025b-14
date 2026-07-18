import styles from "../styles/pages/GamesPage.module.css"
import {Layout} from "../components/Layout.jsx";
import {useTranslation} from "react-i18next";
import {Banner} from "../components/Banner.jsx";
import {AppText} from "../components/AppText.jsx";
import {ElementsGrid} from "../components/ElementsGrid.jsx";
import {Pagination} from "../components/Pagination.jsx";
import {useEffect, useState} from "react";
import {useSearchParams} from "react-router-dom";
import {listGames} from "../api/games.js";
import {parsePageParam} from "../api/pagination.js";

export const GamesPage = () => {
    const { t } = useTranslation();
    const [searchParams] = useSearchParams();
    const [games, setGames] = useState([]);
    const [pagination, setPagination] = useState({currentPage: 0, totalPages: 0});
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    const pageTitle = t("games.title");

    const moonlightImage = "/assets/moonlight.jpg";

    const gamesPageUrl = "/games";

    const loadingLabel = t("games.loading");
    const errorLabel = t("games.error");
    const currentPage = parsePageParam(searchParams.get("page"));

    useEffect(() => {
        let isMounted = true;
        const controller = new AbortController();

        const loadGames = async () => {
            setLoading(true);
            setError(null);

            try {
                const response = await listGames({
                    page: currentPage,
                    signal: controller.signal,
                });

                if (!isMounted) {
                    return;
                }

                setGames(response.items);
                setPagination(response.pagination);
            } catch (loadError) {
                if (isMounted && loadError.name !== "AbortError") {
                    setError(loadError);
                }
            } finally {
                if (isMounted) {
                    setLoading(false);
                }
            }
        };

        loadGames();

        return () => {
            isMounted = false;
            controller.abort();
        };
    }, [currentPage]);

    return (
        <Layout pageTitle={pageTitle}>
            <Banner size="s" image={moonlightImage}>
                <AppText type="title" size="xl" stroke={true}>{pageTitle}</AppText>
            </Banner>
            <div className={styles["content-container"]}>
                {loading && <AppText size="l" weight="thin">{loadingLabel}</AppText>}
                {error && <div role="alert"><AppText size="l" weight="thin">{errorLabel}</AppText></div>}
                {!loading && !error && <ElementsGrid elements={games} id="games-grid" isGame={true}/>}
            </div>
            {!loading && !error && (
                <Pagination
                    currentPage={pagination.currentPage}
                    totalPages={pagination.totalPages}
                    url={gamesPageUrl}
                />
            )}
        </Layout>
    );
}
