import { describe, expect, it } from "vitest";
import en from "../../public/locales/en/translation.json";
import es from "../../public/locales/es/translation.json";

describe("i18n critical keys", () => {
    const requiredKeys = [
        "createTournament.next",
        "login.forgotPassword",
        "verification.goToLogin",
        "error403Page.accessDeniedTitle",
    ];

    it("defines critical keys in English and Spanish", () => {
        for (const key of requiredKeys) {
            expect(en[key], `${key} missing in en`).toBeTruthy();
            expect(es[key], `${key} missing in es`).toBeTruthy();
        }
    });

    it("does not keep the old forgot password casing", () => {
        const legacyKey = ["login", "ForgotPassword"].join(".");

        expect(en[legacyKey]).toBeUndefined();
        expect(es[legacyKey]).toBeUndefined();
    });
});
