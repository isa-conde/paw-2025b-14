import { useState } from "react";
import { useTranslation } from "react-i18next";
import { FormLayout } from "../components/FormLayout.jsx";
import { AppText } from "../components/AppText.jsx";
import { Form } from "../components/Form.jsx";
import { Input } from "../components/Input.jsx";
import styles from "../styles/pages/RegisterPage.module.css"
import {LinkButton} from "../components/LinkButton.jsx";
import { registerUser } from "../api/auth.js";
import { useAuth } from "../auth/useAuth.js";
import { useNavigate } from "react-router-dom";

export const RegisterPage = () => {
    const { t } = useTranslation();
    const navigate = useNavigate();
    const { authenticate } = useAuth();
    const [formValues, setFormValues] = useState({
        username: "",
        email: "",
        password: "",
        repeatPassword: "",
    });
    const [fieldErrors, setFieldErrors] = useState({});
    const [submitError, setSubmitError] = useState(null);
    const [submitting, setSubmitting] = useState(false);

    const title = t("register.page.header")
    const usernameLabel = t("register.username");
    const emailLabel = t("register.email")
    const passwordLabel = t("register.password");
    const repeatPasswordLabel = t("register.confirmPassword");
    const submitLabel = t("register.submit");
    const linkButtonLabel = t("register.alreadyHaveAccount");

    const loginRef = "/login"

    const linkButtonClassName = [styles["link-btn-container"], styles["single"]].filter(Boolean).join(" ");

    const translateServerMessage = (message) => (
        message ? t(message, { defaultValue: message }) : null
    );

    const handleChange = (event) => {
        const { name, value } = event.target;
        setFormValues((current) => ({
            ...current,
            [name]: value,
        }));
        setFieldErrors((current) => ({
            ...current,
            [name]: null,
        }));
    };

    const validate = () => {
        const errors = {};
        const requiredMessage = t("form.requiredField");

        if (!formValues.username.trim()) {
            errors.username = requiredMessage;
        }
        if (!formValues.email.trim()) {
            errors.email = requiredMessage;
        } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formValues.email)) {
            errors.email = t("form.email.invalidFormat");
        }
        if (!formValues.password) {
            errors.password = requiredMessage;
        }
        if (!formValues.repeatPassword) {
            errors.repeatPassword = requiredMessage;
        } else if (formValues.password !== formValues.repeatPassword) {
            errors.repeatPassword = t("error.passwordsDontMatch");
        }

        return errors;
    };

    const handleSubmit = async (event) => {
        event.preventDefault();
        setSubmitError(null);

        const errors = validate();
        setFieldErrors(errors);
        if (Object.keys(errors).length > 0) {
            return;
        }

        setSubmitting(true);
        try {
            const auth = await registerUser(formValues);
            if (auth.token && auth.user) {
                authenticate(auth);
                navigate("/", { replace: true });
                return;
            }

            navigate("/account-verification", {
                replace: true,
                state: { email: formValues.email },
            });
        } catch (error) {
            const details = error.details ?? {};
            setFieldErrors(Object.fromEntries(
                Object.entries(details).map(([field, message]) => [field, translateServerMessage(message)])
            ));
            setSubmitError(error.message || t("register.error.generic"));
        } finally {
            setSubmitting(false);
        }
    };

    return (
        <FormLayout pageTitle={title}>
            <AppText type="title">{title}</AppText>
            {submitError && <p className={styles.error} role="alert">{submitError}</p>}
            <Form method="post" onSubmit={handleSubmit}>
                <div>
                    <Input
                        id="username"
                        name="username"
                        label={usernameLabel}
                        value={formValues.username}
                        error={fieldErrors.username}
                        onChange={handleChange}
                    />
                </div>
                <div>
                    <Input
                        id="email"
                        type="email"
                        name="email"
                        label={emailLabel}
                        value={formValues.email}
                        error={fieldErrors.email}
                        onChange={handleChange}
                    />
                </div>
                <div>
                    <Input
                        id="password"
                        type="password"
                        name="password"
                        label={passwordLabel}
                        value={formValues.password}
                        error={fieldErrors.password}
                        onChange={handleChange}
                    />
                </div>
                <div>
                    <Input
                        id="repeatPassword"
                        type="password"
                        name="repeatPassword"
                        label={repeatPasswordLabel}
                        value={formValues.repeatPassword}
                        error={fieldErrors.repeatPassword}
                        onChange={handleChange}
                    />
                </div>
                <div>
                    <Input type="submit" label={submitting ? t("register.loading") : submitLabel} disabled={submitting}/>
                </div>
            </Form>
            <div className={linkButtonClassName}>
                <LinkButton href={loginRef} text={linkButtonLabel}/>
            </div>
        </FormLayout>
    );
}
