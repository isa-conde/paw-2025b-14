import styles from "../styles/components/PaginationLink.module.css"

export const PaginationLink = ({ page, url, pageNumber = "", children }) => {
    const pageParamName = "page" + pageNumber;

    const newUrl = new URL(url, "http://localhost:5173");

    newUrl.searchParams.append(pageParamName, page);

    const currentParams = new URLSearchParams(window.location.search);
    currentParams.forEach((value, key) => {
        if (key !== pageParamName) {
            newUrl.searchParams.append(key, value);
        }
    });

    const href = `${newUrl.pathname}${newUrl.search}`;

    return (
        <a href={href} className={styles["title-link"]}>
            {children}
        </a>
    );
}
