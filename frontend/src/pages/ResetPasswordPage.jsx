import { useState } from "react";
import { FormLayout } from "../components/FormLayout.jsx";
import { AppText } from "../components/AppText.jsx";
import { useTranslation } from "react-i18next";
import { Form } from "../components/Form.jsx";
import { Input } from "../components/Input.jsx";
import { Button } from "../components/Button.jsx";
import { useNavigate, useSearchParams } from "react-router-dom";
import { resetPassword } from "../api/auth.js";
import { useAuth } from "../auth/useAuth.js";
import styles from "../styles/pages/LoginPage.module.css"

export const ResetPasswordPage = () => {
    const { t } = useTranslation();
    const navigate = useNavigate();
    const { authenticate } = useAuth();
    const [searchParams] = useSearchParams();
    const [formValues, setFormValues] = useState({
        password: "",
        repeatPassword: "",
    });
    const [fieldError, setFieldError] = useState(null);
    const [submitting, setSubmitting] = useState(false);

    const token = searchParams.get("token");
    const forgotPasswordUrl = "/forgot-password";

    const pageTitle = t("login.title");
    const validToken = Boolean(token);

    const handleChange = (event) => {
        const { name, value } = event.target;
        setFormValues((current) => ({
            ...current,
            [name]: value,
        }));
    };

    const handleSubmit = async (event) => {
        event.preventDefault();
        setFieldError(null);

        if (!formValues.password || !formValues.repeatPassword) {
            setFieldError(t("form.requiredField"));
            return;
        }
        if (formValues.password !== formValues.repeatPassword) {
            setFieldError(t("error.passwordsDontMatch"));
            return;
        }

        setSubmitting(true);
        try {
            const auth = await resetPassword({ token, password: formValues.password });
            if (auth.token && auth.user) {
                authenticate(auth);
            }
            navigate("/reset-password/success", { replace: true });
        } catch (requestError) {
            setFieldError(requestError.status >= 500
                ? t("passwordReset.error.generic")
                : requestError.message || t("passwordReset.failed.message"));
        } finally {
            setSubmitting(false);
        }
    };

    return (
        <FormLayout pageTitle={pageTitle}>
            {validToken ? (
                <>
                    <AppText type="title">{t("passwordReset.page.title")}</AppText>
                    {fieldError && <p className={styles.error} role="alert">{fieldError}</p>}
                    <Form method="post" onSubmit={handleSubmit}>
                        <div>
                            <Input
                                id="password"
                                name="password"
                                type="password"
                                label={t("passwordReset.newPassword")}
                                value={formValues.password}
                                onChange={handleChange}
                            />
                        </div>
                        <div>
                            <Input
                                id="repeatPassword"
                                name="repeatPassword"
                                type="password"
                                label={t("passwordReset.confirmNewPassword")}
                                value={formValues.repeatPassword}
                                onChange={handleChange}
                            />
                        </div>
                        <div>
                            <Input
                                type="submit"
                                label={submitting ? t("passwordReset.loading") : t("passwordReset.submit")}
                                disabled={submitting}
                            />
                        </div>
                    </Form>
                </>
            ) : (
                <>
                    <AppText type="title">{t("passwordReset.failed.title")}</AppText>
                    <AppText size="l">{t("passwordReset.failed.message")}</AppText>
                    <br />
                    <Button onClick={() => navigate(forgotPasswordUrl)} text={t("passwordReset.failed.resend")} />
                </>
            )}
        </FormLayout>
    );
};
