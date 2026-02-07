import { FormLayout } from "../components/FormLayout.jsx";
import { AppText } from "../components/AppText.jsx";
import { useTranslation } from "react-i18next";
import { Form } from "../components/Form.jsx";
import { Input } from "../components/Input.jsx";
import { Button } from "../components/Button.jsx";
import { useSearchParams } from "react-router-dom";

export const ResetPasswordPage = () => {
    const { t } = useTranslation();
    const [searchParams] = useSearchParams();

    const token = searchParams.get("token");
    const userId = searchParams.get("userId");

    const resetPasswordUrl = `/forgotPassword/reset?token=${token}`;
    const forgotPasswordUrl = "/forgotPassword";

    const pageTitle = t("login.title");
    const validToken = true; // Always true for now

    return (
        <FormLayout pageTitle={pageTitle}>
            {validToken ? (
                <>
                    <AppText type="title">{t("passwordReset.page.title")}</AppText>
                    <Form method="post" action={resetPasswordUrl}>
                        <input type="hidden" name="userId" value={userId} />
                        <div>
                            <Input name="password" type="password" label={t("passwordReset.newPassword")} />
                        </div>
                        <div>
                            <Input name="repeatPassword" type="password" label={t("passwordReset.confirmNewPassword")} />
                        </div>
                        <div>
                            <Input type="submit" label={t("passwordReset.submit")} />
                        </div>
                    </Form>
                </>
            ) : (
                <>
                    <AppText type="title">{t("passwordReset.failed.title")}</AppText>
                    <AppText size="l">{t("passwordReset.failed.message")}</AppText>
                    <br />
                    <Button onClick={() => (window.location.href = forgotPasswordUrl)} text={t("passwordReset.failed.resend")} />
                </>
            )}
        </FormLayout>
    );
};
