import Text from "../components/Text.jsx";
import FormLayout from "../components/FormLayout.jsx";
import Form from "../components/Form.jsx"
import { useState } from "react";
import { useTranslation } from "react-i18next";
import { Input } from "../components/Input.jsx";

export const LoginPage = () => {
    const { t } = useTranslation();

    const [values, setValues] = useState({ email: "", password: "" });
    const [errors, setErrors] = useState({});
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [submitError, setSubmitError] = useState(null);

    return (
        <FormLayout pageTitle={t("login.title")}>
            <Form method="post">
                <Input id="username" label={t("login.username")}/>
            </Form>
        </FormLayout>
    );
};
