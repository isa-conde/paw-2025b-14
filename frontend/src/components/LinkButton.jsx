import { Link } from "react-router-dom";
import styles from "../styles/components/LinkButton.module.css"

export const LinkButton = ({ href, text, size, target = "_self" }) => {
    const isExternal = href?.startsWith("http://") || href?.startsWith("https://") || target !== "_self";

    if (!isExternal) {
        return (
            <Link to={href} className={styles["link-btn"]}>{text}</Link>
        );
    }

    return (
      <a href={href}
         className={styles["link-btn"]}
         target={target}>{text}</a>
    );
}
