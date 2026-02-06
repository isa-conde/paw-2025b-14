import i18n from "i18next";
import { initReactI18next } from "react-i18next";
import LanguageDetector from "i18next-browser-languagedetector";
import Backend from "i18next-http-backend";
import en from "./public/locales/en/translation.json"
import es from "./public/locales/es/translation.json"

i18n
    .use(LanguageDetector)
    .use(Backend)
    .use(initReactI18next)
    .init({
        debug: true,
        fallbackLng: "en",
        resources: {
            en: { translation: en },
            es: { translation: es },
        },
        interpolation: {
            escapeValue: false,
        },
    });

export default i18n;
