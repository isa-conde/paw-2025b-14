import styles from "../styles/components/Pagination.module.css";
import { PaginationLink } from "./PaginationLink.jsx";
import { AppText } from "./AppText.jsx";

export const Pagination = ({ currentPage, totalPages, url, pageNumber = "" }) => {
    if (totalPages <= 1) {
        return null;
    }

    return (
        <div className={styles["pagination-container"]}>
            <div className={styles.pagination}>
                {currentPage > 0 && (
                    <PaginationLink pageNumber={pageNumber} page={currentPage - 1} url={url}>
                        <AppText size="l" weight="thin">&lt;</AppText>
                    </PaginationLink>
                )}

                {Array.from({ length: totalPages }, (_, i) =>
                    i === currentPage ? (
                        <AppText key={i} weight="bold" size="xl">
                            {i + 1}
                        </AppText>
                    ) : (
                        <PaginationLink key={i} pageNumber={pageNumber} page={i} url={url}>
                            <AppText weight="thin" size="l">
                                {i + 1}
                            </AppText>
                        </PaginationLink>
                    ),
                )}

                {currentPage < totalPages - 1 && (
                    <PaginationLink pageNumber={pageNumber} page={currentPage + 1} url={url}>
                        <AppText size="l" weight="thin">&gt;</AppText>
                    </PaginationLink>
                )}
            </div>
        </div>
    );
};