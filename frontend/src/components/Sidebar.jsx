import styles from "../styles/components/Sidebar.module.css"
import {SidebarButton} from "./SidebarButton.jsx";
import {useTranslation} from "react-i18next";

export const Sidebar = ({ user }) => {
    const { t } = useTranslation();

    const isLoggedIn = false;

    const homePng = "/assets/home.png";
    const joystickPng = "/assets/joystick.png";
    const cupPng = "/assets/cup.png";
    const badgePng = "/assets/badge.png";

    const homeLabel = t("sidebar.home");
    const gamesLabel = t("sidebar.games");
    const tourneysLabel = t("sidebar.tourneys");
    const myTourneysLabel = t("sidebar.myTourneys");

    const indexPath = "/";
    const gamesPath = "/gamesPage";
    const tournamentsPath = "/tournamentsPage";
    const profilePath = "/profile"

    const indexEnum = {
        HOME: 0,
        GAMES: 1,
        TOURNAMENTS: 2,
        MY_TOURNAMENTS: 3
    };

    const activeButtons = [
        window.location.pathname === indexPath,       // Home button
        window.location.pathname === gamesPath,      // Games button
        window.location.pathname === tournamentsPath, // Tournaments button
        window.location.pathname === profilePath     // My Tournaments button
    ];

    return (
      <aside className={styles.sidebar}>
          <div className={styles["sidebar-content"]}>
              <SidebarButton icon={homePng}
                             text={homeLabel}
                             href={indexPath}
                             isActive={activeButtons[indexEnum.HOME]}/>
              <SidebarButton icon={joystickPng}
                             text={gamesLabel}
                             href={gamesPath}
                             isActive={activeButtons[indexEnum.GAMES]}/>
              <SidebarButton icon={cupPng}
                             text={tourneysLabel}
                             href={tournamentsPath}
                             isActive={activeButtons[indexEnum.TOURNAMENTS]}/>
              <SidebarButton icon={badgePng}
                             text={myTourneysLabel}
                             href={profilePath}
                             isActive={activeButtons[indexEnum.MY_TOURNAMENTS]}/>
          </div>
      </aside>
    );
}

// TODO: make my tournaments button dependent on ongoing session