import { useState } from "react";
import { FormLayout } from "../components/FormLayout.jsx";
import { Form } from "../components/Form.jsx"
import { useTranslation } from "react-i18next";
import { Input } from "../components/Input.jsx";
import { LinkButton } from "../components/LinkButton.jsx"
import { AppText } from "../components/AppText.jsx";
import styles from "../styles/pages/LoginPage.module.css"
import { useAuth } from "../auth/useAuth.js";
import { useLocation, useNavigate } from "react-router-dom";

export const LoginPage = () => {
    const { t } = useTranslation();
    const { login, loading } = useAuth();
    const navigate = useNavigate();
    const location = useLocation();
    const [formValues, setFormValues] = useState({
        username: "",
        password: "",
    });
    const [error, setError] = useState(null);

    const title = t("login.title");
    const usernameLabel = t("login.username");
    const passwordLabel = t("login.password");
    const registerNowLabel = t("login.registerNow");
    const forgotPasswordLabel = t("login.forgotPassword");
    const notice = location.state?.notice;

    const registerRef = "/register"
    const forgotPasswordRef = "/forgot-password"

    const handleChange = (event) => {
        const { name, value } = event.target;
        setFormValues((current) => ({
            ...current,
            [name]: value,
        }));
    };

    const getErrorMessage = (requestError) => {
        if (requestError.status === 400 || requestError.status === 401) {
            return t("login.invalidCredentials");
        }
        if (requestError.status === 403) {
            return t("auth.error.forbidden");
        }
        return requestError.message || t("login.error.generic");
    };

    const handleSubmit = async (event) => {
        event.preventDefault();
        setError(null);

        try {
            await login(formValues);
            const from = location.state?.from?.pathname ?? "/";
            navigate(from, { replace: true });
        } catch (requestError) {
            setError(getErrorMessage(requestError));
        }
    };

    return (
        <FormLayout pageTitle={t("login.title")}>
            <AppText type="title" size="l">{title}</AppText>
            {notice && <p className={styles.message}>{notice}</p>}
            {error && <p className={styles.error} role="alert">{error}</p>}
            <Form method="post" onSubmit={handleSubmit}>
                <Input
                    id="username"
                    name="username"
                    label={usernameLabel}
                    value={formValues.username}
                    onChange={handleChange}
                />
                <Input
                    id="password"
                    name="password"
                    type="password"
                    label={passwordLabel}
                    value={formValues.password}
                    onChange={handleChange}
                />
                <Input type="submit" label={loading ? t("login.loading") : title} disabled={loading}/>
                <div className={styles["link-btn-container"]}>
                    <LinkButton href={registerRef} text={registerNowLabel}/>
                    <LinkButton href={forgotPasswordRef} text={forgotPasswordLabel}/>
                </div>
            </Form>
        </FormLayout>
    );
};
