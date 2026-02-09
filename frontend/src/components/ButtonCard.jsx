import styles from "../styles/components/ButtonCard.module.css"
import {AppText} from "./AppText.jsx";
import {Input} from "./Input.jsx";
import {Button} from "./Button.jsx";

export const ButtonCard = ({ title, text, buttonText, texture, icon, onClick, method, tournamentId, disabled, secondary }) => {
    const cardClass = [ styles.card, texture ? "texture" : "" ].filter(Boolean).join(" ");

    const hasIcon = !!icon;
    const methodIsPost = method === "post";
    const hasTournamentId = !!tournamentId;

    return (
        <div className={cardClass}>
            <div className={styles["card-content-container"]}>
                <div>
                    <div className={styles["card-title"]}>
                        <AppText type="title" size="l">{title}</AppText>
                    </div>
                    {text && text.trim() &&
                        <div className={styles["card-text"]}>
                            <AppText size="l" weight="thin">{text}</AppText>
                        </div>
                    }
                    {buttonText && buttonText.trim() &&
                        <div className={styles["card-button-container"]}>
                            {methodIsPost ?
                                <form method={method}>
                                    {hasTournamentId &&
                                        <input type="hidden" name="tournamentId" value={tournamentId}/>
                                    }
                                    <Input type="submit" label={buttonText} secondary={secondary}/>
                                </form>
                                :
                                <Button text={buttonText} onClick={onClick} disabled={disabled} secondary={secondary}/>
                            }
                        </div>
                    }
                </div>
                {hasIcon &&
                    <div className={styles["card-icon"]}>
                        <img src={icon} alt="format icon"/>
                    </div>
                }
            </div>
        </div>
    );
}

// TODO: add functionality to form (action and onclick attributes)