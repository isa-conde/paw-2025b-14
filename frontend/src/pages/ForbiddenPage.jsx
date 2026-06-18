import { useTranslation } from "react-i18next";
import { useNavigate } from "react-router-dom";
import { AppText } from "../components/AppText.jsx";
import { Button } from "../components/Button.jsx";
import { Layout } from "../components/Layout.jsx";

export const ForbiddenPage = () => {
    const { t } = useTranslation();
    const navigate = useNavigate();

    return (
        <Layout pageTitle={t("error403Page.pageTitle")}>
            <div className="page-state">
                <AppText type="title" size="xl">{t("error403Page.pageTitle")}</AppText>
                <AppText size="l">{t("error403Page.generalAccessDenied")}</AppText>
                <Button
                    text={t("error404Page.goToHome")}
                    size="m"
                    onClick={() => navigate("/")}
                />
            </div>
        </Layout>
    );
};
