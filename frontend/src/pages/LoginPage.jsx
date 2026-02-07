import { FormLayout } from "../components/FormLayout.jsx";
import { Form } from "../components/Form.jsx"
import { useTranslation } from "react-i18next";
import { Input } from "../components/Input.jsx";
import { LinkButton } from "../components/LinkButton.jsx"
import { AppText } from "../components/AppText.jsx";
import styles from "../styles/pages/LoginPage.module.css"

export const LoginPage = () => {
    const { t } = useTranslation();

    const title = t("login.title");
    const usernameLabel = t("login.username");
    const passwordLabel = t("login.password");
    const registerNowLabel = t("login.registerNow");
    const forgotPasswordLabel = t("login.forgotPassword");

    const registerRef = "/register"
    const forgotPasswordRef = "/forgotPassword"

    return (
        <FormLayout pageTitle={t("login.title")}>
            <AppText type="title" size="l">{title}</AppText>
            <Form method="post">
                <Input id="username" label={usernameLabel}/>
                <Input id="password" label={passwordLabel}/>
                <Input type="submit" label={title}/>
                <div className={styles["link-btn-container"]}>
                    <LinkButton href={registerRef} text={registerNowLabel}/>
                    <LinkButton href={forgotPasswordRef} text={forgotPasswordLabel}/>
                </div>
            </Form>
        </FormLayout>
    );
};
