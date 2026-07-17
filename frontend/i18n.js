import i18n from "i18next";
import { initReactI18next } from "react-i18next";
import LanguageDetector from "i18next-browser-languagedetector";
import Backend from "i18next-http-backend";

i18n
    .use(LanguageDetector)
    .use(Backend)
    .use(initReactI18next)
    .init({
        debug: import.meta.env.DEV,
        fallbackLng: "en",
        supportedLngs: ["en", "es"],
        nonExplicitSupportedLngs: true,
        load: "languageOnly",
        backend: {
            loadPath: "/locales/{{lng}}/{{ns}}.json",
        },
        interpolation: {
            escapeValue: false,
        },
    });

export default i18n;
