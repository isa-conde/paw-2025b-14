import { useTranslation } from "react-i18next";
import { useNavigate } from "react-router-dom";
import { AppText } from "../components/AppText.jsx";
import { Button } from "../components/Button.jsx";
import { FormLayout } from "../components/FormLayout.jsx";

export const AccountVerificationPage = () => {
    const { t } = useTranslation();
    const navigate = useNavigate();

    return (
        <FormLayout pageTitle={t("accountVerification.page.title")}>
            <AppText type="title">{t("accountVerification.title")}</AppText>
            <AppText size="l">{t("accountVerification.instruction")}</AppText>
            <Button
                text={t("verification.goToLogin")}
                size="m"
                onClick={() => navigate("/login")}
            />
        </FormLayout>
    );
};
