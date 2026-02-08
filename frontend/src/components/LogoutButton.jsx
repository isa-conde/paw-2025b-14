import styles from "../styles/components/LogoutButton.module.css"

export const LogoutButton = () => {
    const logoutPng = "/assets/logoutButton.png";

    return (
        <button type="button" className={styles["logout-button"]}>
            <img src={logoutPng} alt="Logout"/>
        </button>
    );
}

// TODO: add modal functionality and actual logout action