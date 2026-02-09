import styles from "../styles/components/ElementCard.module.css"
import {AppText} from "./AppText.jsx";
import {useTranslation} from "react-i18next";

export const ElementCard = ({ image, game, title, tags, id, isGame, started, finished }) => {
    const { t } = useTranslation();

    const hasChip = started != null && finished != null && !isGame;

    const url = isGame ? "/tournamentsPage/game" : "/tournament";

    const inProgressLabel = t("card.inProgress");
    const finishedLabel = t("card.finished");
    const comingSoonLabel = t("card.comingSoon");

    return (
        <a href={url} className={styles["element-card"]}>
            <img src={image} alt="Background" className={styles["element-card-image"]}/>
            <div className={`${styles["element-card-content"]} ${styles.game}`}>
                <AppText type="title" size="xs" stroke={true}>{game}</AppText>
            </div>
            <div className={styles["element-card-content"]}>
                <AppText type="title" size="s" stroke={true}>{title}</AppText>
                {hasChip &&
                    <div className={styles["date-chip"]}>
                        {() => {
                            if(started && !finished) {
                                return inProgressLabel;
                            } else if(finished) {
                                return finishedLabel;
                            } else {
                                return comingSoonLabel;
                            }
                        }}
                    </div>
                }
            </div>
        </a>
    );
}

// TODO: make the url ID dependent