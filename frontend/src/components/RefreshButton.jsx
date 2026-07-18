import styles from "../styles/components/RefreshButton.module.css"

export const RefreshButton = ({ disabled = false, onClick }) => {
    const refreshButtonImage = "/assets/refresh.png";

    const buttonClass = [ styles.btn, styles.submit, disabled ? "disabled" : "" ].filter(Boolean).join(" ");

    return (
        <button type="button" className={buttonClass} disabled={disabled} onClick={onClick}>
            <img src={refreshButtonImage} alt="Refresh"/>
        </button>
    );
}
