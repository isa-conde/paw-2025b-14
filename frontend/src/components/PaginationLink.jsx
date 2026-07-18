import styles from "../styles/components/PaginationLink.module.css"
import {Link, useLocation} from "react-router-dom";

export const PaginationLink = ({ page, url, pageNumber = "", children }) => {
    const location = useLocation();
    const pageParamName = "page" + pageNumber;
    const baseUrl = new URL(url, "http://localhost:5173");
    const nextParams = new URLSearchParams(location.search);

    baseUrl.searchParams.forEach((value, key) => {
        nextParams.set(key, value);
    });
    nextParams.set(pageParamName, page);

    const search = nextParams.toString();
    const href = `${baseUrl.pathname}${search ? `?${search}` : ""}`;

    return (
        <Link to={href} className={styles["title-link"]}>
            {children}
        </Link>
    );
}
