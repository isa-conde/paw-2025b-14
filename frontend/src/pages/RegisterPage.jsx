import { useTranslation } from "react-i18next";
import { FormLayout } from "../components/FormLayout.jsx";
import { AppText } from "../components/AppText.jsx";
import { Form } from "../components/Form.jsx";
import { Input } from "../components/Input.jsx";
import styles from "../styles/pages/RegisterPage.module.css"
import {LinkButton} from "../components/LinkButton.jsx";

export const RegisterPage = () => {
    const { t } = useTranslation();

    const title = t("register.page.header")
    const usernameLabel = t("register.username");
    const emailLabel = t("register.email")
    const passwordLabel = t("register.password");
    const repeatPasswordLabel = t("register.confirmPassword");
    const submitLabel = t("register.submit");
    const linkButtonLabel = t("register.alreadyHaveAccount");

    const loginRef = "/login"

    const linkButtonClassName = [styles["link-btn-container"], styles["single"]].filter(Boolean).join(" ");

    return (
        <FormLayout pageTitle={title}>
            <AppText type="title">{title}</AppText>
            <Form method="post">
                <div>
                    <Input name="username" label={usernameLabel}/>
                </div>
                <div>
                    <Input type="email" name="email" label={emailLabel}/>
                </div>
                <div>
                    <Input type="password" name="password" label={passwordLabel}/>
                </div>
                <div>
                    <Input type="password" name="repeatPassword" label={repeatPasswordLabel}/>
                </div>
                <div>
                    <Input type="submit" label={submitLabel}/>
                </div>
            </Form>
            <div className={linkButtonClassName}>
                <LinkButton href={loginRef} text={linkButtonLabel}/>
            </div>
        </FormLayout>
    );
}