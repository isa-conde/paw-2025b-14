import {Layout} from "../components/Layout.jsx";
import {Banner} from "../components/Banner.jsx";
import {AppText} from "../components/AppText.jsx";
import {useTranslation} from "react-i18next";
import styles from "../styles/pages/HomePage.module.css"
import {ButtonCard} from "../components/ButtonCard.jsx";
import {useNavigate} from "react-router-dom";
import {Carrousel} from "../components/Carrousel.jsx";

export const HomePage = () => {
    const { t } = useTranslation();
    const navigate = useNavigate();

    const handleNavigate = (url) => {
        navigate(url);
    };

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

    const hardcodedGames = [
        { id: 1, name: "League of Legends" },
        { id: 2, name: "Valorant" },
        { id: 3, name: "Counter-Strike" },
        { id: 4, name: "Dota 2" },
        { id: 5, name: "Overwatch" },
    ];

    const hardcodedTournaments = {
        1: [
            { id: 101, name: "LoL Tournament 1" },
            { id: 102, name: "LoL Tournament 2" },
            { id: 103, name: "LoL Tournament 3" },
        ],
        2: [
            { id: 201, name: "Valorant Tournament 1" },
            { id: 202, name: "Valorant Tournament 2" },
        ],
        3: [
            { id: 301, name: "CS:GO Tournament 1" },
            { id: 302, name: "CS:GO Tournament 2" },
            { id: 303, name: "CS:GO Tournament 3" },
        ],
        4: [
            { id: 401, name: "Dota 2 Tournament 1" },
            { id: 402, name: "Dota 2 Tournament 2" },
        ],
        5: [
            { id: 501, name: "Overwatch Tournament 1" },
        ],
    };
    
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
                    <a href={gamesPageUrl} className={styles["title-link"]}>
                        <AppText type="title" size="l">{gamesPageLabel}</AppText>
                    </a>
                </div>
                <Carrousel elements={hardcodedGames} isGame={true}/>
                <div className={styles["content-title"]}>
                    <a href={joinTournamentUrl} className={styles["title-link"]}>
                        <AppText type="title" size="l">{tournamentsLabel}</AppText>
                    </a>
                </div>
                {hardcodedGames.map((game) => (
                    <div key={game.id}>
                        <div className={styles["content-title"]}>
                            <a href={`${tournamentsGameUrl}?gameId=${game.id}`} className={styles["title-link"]}>
                                <AppText type="title" size="s">{game.name}</AppText>
                            </a>
                            <Carrousel elements={hardcodedTournaments[game.id]} isGame={false}/>
                        </div>
                    </div>
                ))}
            </div>
        </Layout>
    );
}

// TODO: collect real games and tournaments, not hardcoded
// TODO: change hardcoded tournament sections to API-driven data
