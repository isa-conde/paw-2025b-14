import styles from "../styles/components/SearchBar.module.css"
import {useTranslation} from "react-i18next";

export const SearchBar = () => {
    const { t } = useTranslation();

    const searchBarPlaceholder = t("searchBar.placeHolder");

    const searchPng = "../assets/search.png"

    return (
        <form method="get">
            <div className={styles["search-container"]}>
                <input name="q" type="text" className={styles["search-bar"]} placeholder={searchBarPlaceholder}/>
                <img src={searchPng} alt={searchBarPlaceholder} className={styles["search-icon"]}/>
            </div>
        </form>
    );
}

// TODO: add search functionality