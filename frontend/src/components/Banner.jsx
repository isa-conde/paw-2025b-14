import styles from "../styles/components/Banner.module.css"
import {Button} from "./Button.jsx";

export const Banner = ({ size = "m", image, cornerIcon, cornerText, cornerOnClick, cropTop = false, children }) => {
    const imageClass = [ styles["banner-image"], cropTop ? styles["top-cropped"] : "" ].filter(Boolean).join(" ");

    const hasCorner = !!(cornerIcon || cornerText);

    return (
        <div className={`${styles.banner} ${size}`}>
            <img className={imageClass} src={image} alt="Banner"/>
            <div className={styles["banner-content"]}>
                {children}
            </div>
            {hasCorner &&
                <div className={styles["banner-corner"]}>
                    <Button onClick={cornerOnClick} text={cornerText} image={cornerIcon} size="m" secondary={true}/>
                </div>
            }
        </div>
    );
}