import styles from "../styles/components/Header.module.css"
import {Link} from "react-router-dom";

export const Header = ({ children }) => {
    const indexPath = "/";

    const crownPng = "/assets/crown.png";

    return (
        <header className={styles.header}>
            <Link to={indexPath} className={styles["logo-link"]}>
                <img src={crownPng} alt="logo" className={styles.logo}/>
            </Link>
            {children}
        </header>
    )
}
