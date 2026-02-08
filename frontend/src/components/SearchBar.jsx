import styles from "../styles/components/SearchBar.module.css"

export const SearchBar = () => {
    const searchPng = "/assets/search.png"

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