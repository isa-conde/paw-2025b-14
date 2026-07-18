import { useState } from "react";
import { FormLayout } from "../components/FormLayout.jsx";
import { AppText } from "../components/AppText.jsx";
import { useTranslation } from "react-i18next";
import { Form } from "../components/Form.jsx";
import { Input } from "../components/Input.jsx";
import { useNavigate } from "react-router-dom";
import { requestPasswordReset } from "../api/auth.js";
import styles from "../styles/pages/LoginPage.module.css"

export const ForgotPasswordPage = () => {
    const { t } = useTranslation();
    const navigate = useNavigate();
    const [email, setEmail] = useState("");
    const [error, setError] = useState(null);
    const [submitting, setSubmitting] = useState(false);
    
    const title = t("forgotPassword.title");
    const text = t("forgotPassword.text");
    const emailLabel = t("forgotPassword.email");
    const submitLabel = t("forgotPassword.sendRequest");

    const handleSubmit = async (event) => {
        event.preventDefault();
        setError(null);

        if (!email.trim()) {
            setError(t("form.requiredField"));
            return;
        }

        setSubmitting(true);
        try {
            await requestPasswordReset({ email: email.trim() });
            navigate("/forgot-password/requested", { replace: true });
        } catch (requestError) {
            setError(requestError.status >= 500
                ? t("forgotPassword.error.generic")
                : requestError.message || t("forgotPassword.error.generic"));
        } finally {
            setSubmitting(false);
        }
    };

    return (
        <FormLayout>
            <AppText type="title">{title}</AppText>
            <AppText>{text}</AppText>
            {error && <p className={styles.error} role="alert">{error}</p>}
            <Form method="post" onSubmit={handleSubmit}>
                <div>
                    <Input
                        id="email"
                        name="email"
                        type="email"
                        label={emailLabel}
                        value={email}
                        onChange={(event) => setEmail(event.target.value)}
                    />
                </div>
                <div>
                    <Input type="submit" label={submitting ? t("forgotPassword.loading") : submitLabel} disabled={submitting}/>
                </div>
            </Form>
        </FormLayout>
    );
}
