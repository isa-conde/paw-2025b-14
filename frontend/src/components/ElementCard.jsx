import styles from "../styles/components/ElementCard.module.css"
import {AppText} from "./AppText.jsx";
import {useTranslation} from "react-i18next";
import {Link} from "react-router-dom";
import {useEffect, useState} from "react";

export const ElementCard = ({ image, game, title, id, isGame, started, finished, url }) => {
    const { t } = useTranslation();
    const fallbackImage = "/assets/arcane.jpg";
    const [currentImage, setCurrentImage] = useState(image ?? fallbackImage);

    useEffect(() => {
        setCurrentImage(image ?? fallbackImage);
    }, [image]);

    const hasChip = started != null && finished != null && !isGame;

    const destination = url ?? (isGame ? `/tournaments?gameId=${id}` : `/tournaments/${id}`);

    const inProgressLabel = t("card.inProgress");
    const finishedLabel = t("card.finished");
    const comingSoonLabel = t("card.comingSoon");
    const statusLabel = finished ? finishedLabel : started ? inProgressLabel : comingSoonLabel;

    return (
        <Link to={destination} className={styles["element-card"]}>
            <img
                src={currentImage}
                alt="Background"
                className={styles["element-card-image"]}
                onError={() => setCurrentImage(fallbackImage)}
            />
            <div className={`${styles["element-card-content"]} ${styles.game}`}>
                <AppText type="title" size="xs" stroke={true}>{game}</AppText>
            </div>
            <div className={styles["element-card-content"]}>
                <AppText type="title" size="s" stroke={true}>{title}</AppText>
                {hasChip &&
                    <div className={styles["date-chip"]}>
                        {statusLabel}
                    </div>
                }
            </div>
        </Link>
    );
}
