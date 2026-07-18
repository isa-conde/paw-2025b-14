import { useContext } from "react";
import { useTranslation } from "react-i18next";
import { useNavigate } from "react-router-dom";
import styles from "../styles/components/LogoutButton.module.css"
import { AuthContext } from "../auth/AuthContext.jsx";

export const LogoutButton = () => {
    const { t } = useTranslation();
    const navigate = useNavigate();
    const auth = useContext(AuthContext);
    const logoutPng = "/assets/logoutButton.png";

    const handleLogout = () => {
        auth?.logout();
        navigate("/", { replace: true });
    };

    return (
        <button type="button" className={styles["logout-button"]} onClick={handleLogout} aria-label={t("logout.action")}>
            <img src={logoutPng} alt=""/>
        </button>
    );
}
