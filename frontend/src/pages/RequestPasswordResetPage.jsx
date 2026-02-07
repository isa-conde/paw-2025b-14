import { FormLayout } from "../components/FormLayout.jsx";
import { AppText } from "../components/AppText.jsx";
import { useTranslation } from "react-i18next";
import { Button } from "../components/Button.jsx";

export const RequestPasswordResetPage = () => {
    const { t } = useTranslation();

    const pageTitle = t("login.title");
    const title = t("passwordReset.sent.title");
    const instruction = t("passwordReset.sent.instruction");
    const buttonLabel = t("passwordReset.sent.buttonLabel");
    const loginUrl = "/login";

    return (
        <FormLayout pageTitle={pageTitle}>
            <AppText type="title">{title}</AppText>
            <AppText size="l">{instruction}</AppText>
            <br />
            <Button onClick={() => window.location.href = loginUrl} text={buttonLabel} />
        </FormLayout>
    );
};
