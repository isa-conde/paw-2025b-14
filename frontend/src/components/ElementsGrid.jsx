import styles from "../styles/components/ElementsGrid.module.css"
import {useTranslation} from "react-i18next";
import {AppText} from "./AppText.jsx";
import {ElementCard} from "./ElementCard.jsx";
import {ProfileCard} from "./ProfileCard.jsx";

export const ElementsGrid = ({ elements, headerElements, isGame, id, isUser, isTeam }) => {
    const { t } = useTranslation();

    const hasHeader = !!headerElements;
    const noElements = (elements ?? []).length <= 0;

    const defaultElementCardImage = "/assets/arcane.jpg";

    const noGames = t("elementGrid.noGames");
    const noTournaments = t("elementGrid.noTournaments");

    return (
         noElements ?
            <div className={styles["no-cards-container"]}>
                <AppText size="l" weight="thin">
                    {isGame ? noGames : noTournaments}
                </AppText>
            </div>
            :
            <div className={styles.grid}>
                {elements.map((element) => {
                    if(isGame) {
                        return <ElementCard
                            key={element.id}
                            image={element.image ?? defaultElementCardImage}
                            title={element.name}
                            id={element.id}
                            isGame={true}/>
                    } else if(isUser) {
                        return <ProfileCard key={element.id} isUser={true}/>
                    } else if(isTeam) {
                        return <ProfileCard key={element.id} isTeam={true}/>
                    } else {
                        let tournamentGame = null;
                        if(hasHeader) {
                            tournamentGame = headerElements.find((headerElement) => headerElement.id === element.gameId);
                        }
                        return <ElementCard
                            key={element.id}
                            image={element.image ?? defaultElementCardImage}
                            title={element.name}
                            started={element.tournamentStarted}
                            finished={element.finished ?? element.isFinished}
                            game={element.gameName ?? tournamentGame?.name ?? ""}
                            id={element.id}
                            isGame={false}/>
                    }
                })}
            </div>
    );
}
