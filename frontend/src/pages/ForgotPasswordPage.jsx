import { FormLayout } from "../components/FormLayout.jsx";
import { AppText } from "../components/AppText.jsx";
import { useTranslation } from "react-i18next";
import { Form } from "../components/Form.jsx";
import { Input } from "../components/Input.jsx";

export const ForgotPasswordPage = () => {
    const { t } = useTranslation();
    
    const title = t("forgotPassword.title");
    const text = t("forgotPassword.text");
    const emailLabel = t("forgotPassword.email");
    const submitLabel = t("forgotPassword.sendRequest");

    return (
        <FormLayout>
            <AppText type="title">{title}</AppText>
            <AppText>{text}</AppText>
            <Form method="post">
                <div>
                    <Input name="email" type="email" label={emailLabel}/>
                </div>
                <div>
                    <Input type="submit" label={submitLabel}/>
                </div>
            </Form>
        </FormLayout>
    );
}