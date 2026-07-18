import { useEffect, useRef, useState } from "react";
import { useTranslation } from "react-i18next";
import { useNavigate, useSearchParams } from "react-router-dom";
import { confirmVerification } from "../api/auth.js";
import { useAuth } from "../auth/useAuth.js";
import { AppText } from "../components/AppText.jsx";
import { Button } from "../components/Button.jsx";
import { FormLayout } from "../components/FormLayout.jsx";
import styles from "../styles/pages/LoginPage.module.css";

export const VerificationConfirmPage = () => {
    const { t } = useTranslation();
    const navigate = useNavigate();
    const [searchParams] = useSearchParams();
    const { authenticate } = useAuth();
    const [status, setStatus] = useState("loading");
    const [error, setError] = useState(null);
    const confirmStarted = useRef(false);

    const token = searchParams.get("token");

    useEffect(() => {
        if (confirmStarted.current) {
            return;
        }

        confirmStarted.current = true;

        if (!token) {
            setStatus("failed");
            setError(t("verification.failed.text"));
            return;
        }

        confirmVerification({ token })
            .then((auth) => {
                if (auth.token && auth.user) {
                    authenticate(auth);
                }
                setStatus("success");
            })
            .catch((requestError) => {
                setStatus("failed");
                setError(requestError.status === 400 || requestError.status === 404
                    ? t("verification.failed.text")
                    : requestError.message || t("verification.error.generic"));
            });
    }, [authenticate, t, token]);

    return (
        <FormLayout pageTitle={t("accountVerification.page.title")}>
            {status === "loading" && (
                <>
                    <AppText type="title">{t("accountVerification.title")}</AppText>
                    <AppText size="l">{t("verification.confirming")}</AppText>
                </>
            )}
            {status === "success" && (
                <>
                    <AppText type="title">{t("verification.successful.title")}</AppText>
                    <AppText size="l">{t("verification.successful.text")}</AppText>
                    <br />
                    <Button onClick={() => navigate("/")} text={t("verification.goToHome")} />
                </>
            )}
            {status === "failed" && (
                <>
                    <AppText type="title">{t("verification.failed.title")}</AppText>
                    {error && <p className={styles.error} role="alert">{error}</p>}
                    <Button onClick={() => navigate("/account-verification")} text={t("verification.resend")} />
                </>
            )}
        </FormLayout>
    );
};
