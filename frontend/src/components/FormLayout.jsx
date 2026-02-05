import React from "react";
import { Helmet } from "react-helmet";
import styles from "../styles/Form.module.css";

export const FormLayout = ({ pageTitle, children }) => {
  return (
    <div className={`${styles.page}`}>
        <Helmet>
            <title>{pageTitle ? `RankUp - ${pageTitle}` : "RankUp"}</title>
        </Helmet>
        {children}
    </div>
  );
};