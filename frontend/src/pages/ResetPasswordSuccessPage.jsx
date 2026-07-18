import { FormLayout } from "../components/FormLayout.jsx";
import { AppText } from "../components/AppText.jsx";
import { useTranslation } from "react-i18next";
import { Button } from "../components/Button.jsx";
import { useNavigate } from "react-router-dom";

export const ResetPasswordSuccessPage = () => {
    const { t } = useTranslation();
    const navigate = useNavigate();

    const pageTitle = t("login.title");
    const title = t("passwordReset.success.title");
    const message = t("passwordReset.success.message");
    const homeUrl = "/";
    const buttonText = t("verification.goToHome");

    return (
        <FormLayout pageTitle={pageTitle}>
            <AppText type="title">{title}</AppText>
            <AppText size="l">{message}</AppText>
            <br />
            <Button onClick={() => navigate(homeUrl)} text={buttonText} />
        </FormLayout>
    );
};
