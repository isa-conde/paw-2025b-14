import styles from "../styles/pages/GamesPage.module.css"
import {Layout} from "../components/Layout.jsx";
import {useTranslation} from "react-i18next";
import {Banner} from "../components/Banner.jsx";
import {AppText} from "../components/AppText.jsx";
import {ElementsGrid} from "../components/ElementsGrid.jsx";
import {useState} from "react";
import {PaginationLink} from "../components/PaginationLink.jsx";

export const GamesPage = () => {
    const { t } = useTranslation();
    const [ currentPage, setCurrentPage ] = useState(0);

    const handleChangeCurrentPage = (page) => {
        setCurrentPage(page);
    }

    const pageTitle = t("games.title");

    const moonlightImage = "/assets/moonlight.jpg";

    const gamesPageUrl = "/gamesPage";

    return (
        <Layout pageTitle={pageTitle}>
            <Banner size="s" image={moonlightImage}>
                <AppText type="title" size="xl" stroke={true}>{pageTitle}</AppText>
            </Banner>
            <div className={styles["content-container"]}>
                <ElementsGrid elements={games} id="games-grid" isGame={true}/>
            </div>

            <div className={styles["pagination-container"]}>
                {totalPages > 1 &&
                    <div className={styles.pagination}>
                        {currentPage > 0 &&
                            <PaginationLink page={currentPage - 1} url={gamesPageUrl}>
                                <AppText size="l" weight="thin"> > </AppText>
                            </PaginationLink>
                        }
                        {[...Array(totalPages)].map((_, i) => (
                            i === currentPage ? (
                                <AppText weight="bold" size="xl">{i + 1}</AppText>
                            ) : (
                                <PaginationLink page={i} url={gamesPageUrl}>
                                    <AppText weight="thin" size="l">{i + 1}</AppText>
                                </PaginationLink>
                            )
                        ))}
                        {currentPage < totalPages - 1 &&
                            <PaginationLink page={currentPage + 1} url={gamesPageUrl}>
                                <AppText size="l" weight="thin"> > </AppText>
                            </PaginationLink>
                        }
                    </div>
                }
            </div>
        </Layout>
    );
}

// TODO: un-hardcode the games and the pagination