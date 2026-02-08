import styles from "../styles/components/SidebarButton.module.css"

export const SidebarButton = ({ icon, text, href, isActive = false }) => {
    const buttonClass = [ styles["sidebar-button"], isActive ? styles.active : "" ].filter(Boolean).join(" ");

    return (
        <a href={href} className={buttonClass}>
            <img src={icon} alt={text} className={styles["sidebar-icon"]}/>
            <div className={styles["sidebar-text"]}>{text}</div>
        </a>
    )
}