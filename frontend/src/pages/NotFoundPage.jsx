import { useTranslation } from "react-i18next";
import { useNavigate } from "react-router-dom";
import { AppText } from "../components/AppText.jsx";
import { Button } from "../components/Button.jsx";
import { Layout } from "../components/Layout.jsx";

export const NotFoundPage = () => {
    const { t } = useTranslation();
    const navigate = useNavigate();

    return (
        <Layout pageTitle={t("error404Page.title")}>
            <div className="page-state">
                <AppText type="title" size="xl">{t("error404Page.title")}</AppText>
                <AppText size="l">{t("error404Page.description")}</AppText>
                <Button
                    text={t("error404Page.goToHome")}
                    size="m"
                    onClick={() => navigate("/")}
                />
            </div>
        </Layout>
    );
};
