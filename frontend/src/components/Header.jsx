import styles from "../styles/components/Header.module.css"

export const Header = ({ children }) => {
    const indexPath = "/";

    const crownPng = "/assets/crown.png";

    return (
        <header className={styles.header}>
            <a href={indexPath} className={styles["logo-link"]}>
                <img src={crownPng} alt="logo" className={styles.logo}/>
            </a>
            {children}
        </header>
    )
}