import { AppText } from "./AppText.jsx";
import styles from "../styles/components/Button.module.css";
import starIcon from "../../public/assets/roundedStarOn.png";

export const Button = ({
                           text,
                           size = "l",
                           onClick,
                           image,
                           fill = true,
                           disabled = false,
                           isNotSafe = false,
                           secondary = false,
                           rating = 0,
                           type = "button",
                           children,
                       }) => {
    const hasImage = Boolean(image);
    const content = text ?? children;
    const hasText = Boolean(content);
    const isEmpty = fill === false;

    const className = [
        styles.btn,
        hasImage ? styles.image : "",
        hasImage && !hasText ? styles.noText : "",
        isEmpty ? styles.empty : "",
        secondary ? styles.secondary : "",
    ]
        .filter(Boolean)
        .join(" ");

    const buttonText = text
        ? isNotSafe
            ? text
            : text
        : children;

    return (
        <button className={className} onClick={onClick} disabled={disabled} type={type}>
            {hasImage && (
                <img
                    className={`${styles.buttonImage} ${styles[size]}`}
                    src={image}
                    alt=""
                />
            )}
            {hasText && <AppText size={size}>{buttonText}</AppText>}
            {rating !== null && rating !== 0 && (
                <div className={styles.ratingContainer}>
                    <img src={starIcon} className={styles.bannerStar} alt="" />
                    <AppText size="s" weight="thin">
                        {rating}
                    </AppText>
                </div>
            )}
        </button>
    );
};