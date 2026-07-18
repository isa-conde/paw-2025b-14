import {Helmet} from "@dr.pogodin/react-helmet";
import styles from "../styles/components/Layout.module.css"
import {Sidebar} from "./Sidebar.jsx";
import {Header} from "./Header.jsx";
import {SearchBar} from "./SearchBar.jsx";
import {ProfileButton} from "./ProfileButton.jsx";
import {LogoutButton} from "./LogoutButton.jsx";
import {useTranslation} from "react-i18next";
import {Button} from "./Button.jsx";
import {useNavigate} from "react-router-dom";
import {useAuth} from "../auth/useAuth.js";

export const Layout = ({ pageTitle, func, children }) => {
    const { t } = useTranslation();
    const navigate = useNavigate();
    const { isAuthenticated, isVerified, user, loading } = useAuth();

    const profileUrl = "/profile";
    const loginUrl = "/login";
    const registerUrl = "/register";

    const handleRedirect = (url) => {
        navigate(url);
    };

    const loginLabel = t("layout.login");
    const registerLabel = t("layout.register");
    const username = user?.username ?? t("layout.user");

    return (
        <>
            <Helmet>
                <title>{pageTitle ? `RankUp - ${pageTitle}` : "RankUp"}</title>
            </Helmet>
            <div className={styles.container}>
                <Sidebar/>
                <Header>
                    <SearchBar/>
                    {loading ? null : isAuthenticated ? (
                        <div className={styles["header-buttons"]}>
                            {!isVerified && (
                                <span className={styles["auth-status"]}>{t("layout.unverified")}</span>
                            )}
                            <ProfileButton text={username} isNotSafe={true} size="m" onClick={() => handleRedirect(profileUrl)}/>
                            <LogoutButton/>
                        </div>
                    ) : (
                        <div className={styles["header-buttons"]}>
                            <Button text={loginLabel} size="m" onClick={() => handleRedirect(loginUrl)}/>
                            <Button text={registerLabel} size="m" onClick={() => handleRedirect(registerUrl)}/>
                        </div>
                    )}
                </Header>
                <main className={styles["main-content"]}>
                    {children}
                </main>
            </div>
        </>
    );
}
