import styles from "../styles/components/Header.module.css"

export const Header = () => {
    const indexPath = "/";

    const crownPng = "/assets/crown.png";

    return (
        <header className={styles.header}>
            <a href={indexPath} className={styles["styles.logo-link"]}>
                <img src={crownPng} alt="logo" className={styles.logo}/>
            </a>
        </header>
    )
}