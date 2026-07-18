import styles from "../styles/components/Sidebar.module.css"
import {SidebarButton} from "./SidebarButton.jsx";
import {useTranslation} from "react-i18next";
import {useLocation} from "react-router-dom";

export const Sidebar = ({ user }) => {
    const { t } = useTranslation();
    const location = useLocation();

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
    const gamesPath = "/games";
    const tournamentsPath = "/tournaments";
    const profilePath = "/profile"

    const indexEnum = {
        HOME: 0,
        GAMES: 1,
        TOURNAMENTS: 2,
        MY_TOURNAMENTS: 3
    };

    const activeButtons = [
        location.pathname === indexPath,
        location.pathname.startsWith(gamesPath),
        location.pathname.startsWith(tournamentsPath),
        location.pathname === profilePath
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
