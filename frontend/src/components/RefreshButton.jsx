import styles from "../styles/components/RefreshButton.module.css"

export const RefreshButton = ({ disabled = false }) => {
    const refreshButtonImage = "/assets/refresh.png";

    const tournamentsPageUrl = "/tournaments";

    const buttonClass = [ styles.btn, styles.submit, disabled ? "disabled" : "" ].filter(Boolean).join(" ");

    if (disabled) {
        return (
            <button type="button" className={buttonClass} disabled>
                <img src={refreshButtonImage} alt="Refresh"/>
            </button>
        );
    }

    return (
        <a href={tournamentsPageUrl}>
            <button type="button" className={buttonClass}>
                <img src={refreshButtonImage} alt="Refresh"/>
            </button>
        </a>
    );
}

// TODO: I don't think an <a> element is necessary. Could use React hooks
