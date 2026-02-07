import styles from "../styles/components/LinkButton.module.css"

export const LinkButton = ({ href, text, size, target = "_self" }) => {
    return (
      <a href={href}
         className={styles["link-btn"]}
         target={target}>{text}</a>
    );
}