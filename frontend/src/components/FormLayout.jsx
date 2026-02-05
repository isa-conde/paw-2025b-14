import React from "react";
import { Helmet } from "react-helmet";
import styles from "../styles/Form.module.css";

export const FormLayout = ({ pageTitle, children }) => {
  return (
    <>
        <Helmet>
            <title>{pageTitle ? `RankUp - ${pageTitle}` : "RankUp"}</title>
        </Helmet>
        {children}
    </>
  );
};