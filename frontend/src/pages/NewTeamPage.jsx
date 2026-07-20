import { useEffect, useMemo, useState } from "react";
import { useTranslation } from "react-i18next";
import { useLocation, useNavigate, useSearchParams } from "react-router-dom";
import { createTeam } from "../api/teams.js";
import { getIdFromLink } from "../api/client.js";
import { readFileAsBase64 } from "../api/tournaments.js";
import { searchUsersByName } from "../api/users.js";
import { useAuth } from "../auth/useAuth.js";
import { AppText } from "../components/AppText.jsx";
import { Button } from "../components/Button.jsx";
import { Input } from "../components/Input.jsx";
import { Layout } from "../components/Layout.jsx";
import styles from "../styles/pages/NewTeamPage.module.css";

const MAX_IMAGE_SIZE = 3 * 1024 * 1024;
const IMAGE_TYPES = ["image/jpeg", "image/png"];
const IMAGE_EXTENSION_PATTERN = /\.(jpe?g|png)$/i;
const SUGGESTION_LIMIT = 8;
const MIN_SEARCH_LENGTH = 2;

const initialValues = {
    name: "",
    userSearch: "",
};

const isImageFile = (file) => (
    IMAGE_TYPES.includes(file.type) || IMAGE_EXTENSION_PATTERN.test(file.name)
);

const isSafeReturnTo = (value) => (
    typeof value === "string" && value.startsWith("/") && !value.startsWith("//")
);

const translateApiDetail = (detail, t) => {
    const firstDetail = Array.isArray(detail) ? detail[0] : detail;
    if (firstDetail == null) {
        return "";
    }

    const rawMessage = String(firstDetail);
    const key = rawMessage.replace(/^\{(.+)}$/, "$1");
    return t(key, { defaultValue: rawMessage });
};

const mapBackendDetails = (details, t) => {
    if (!details || typeof details !== "object") {
        return {};
    }

    return Object.entries(details).reduce((errors, [field, detail]) => ({
        ...errors,
        [field]: translateApiDetail(detail, t),
    }), {});
};

const buildCreatePayload = async (values, files, members) => ({
    name: values.name.trim(),
    profilePictureBase64: files.profilePicture ? await readFileAsBase64(files.profilePicture) : null,
    bannerBase64: files.banner ? await readFileAsBase64(files.banner) : null,
    members: members.map((member) => member.username),
});

export const NewTeamPage = () => {
    const { t } = useTranslation();
    const { user } = useAuth();
    const navigate = useNavigate();
    const routerLocation = useLocation();
    const [searchParams] = useSearchParams();
    const returnToParam = searchParams.get("returnTo");
    const returnTo = isSafeReturnTo(returnToParam) ? returnToParam : null;

    const [values, setValues] = useState(initialValues);
    const [files, setFiles] = useState({ profilePicture: null, banner: null });
    const [members, setMembers] = useState([]);
    const [errors, setErrors] = useState({});
    const [globalError, setGlobalError] = useState(null);
    const [submitting, setSubmitting] = useState(false);
    const [suggestions, setSuggestions] = useState([]);
    const [suggestionState, setSuggestionState] = useState("idle");
    const [suggestionError, setSuggestionError] = useState(null);

    const ownerId = Number(user?.id);
    const selectedMemberIds = useMemo(() => new Set(members.map((member) => Number(member.id))), [members]);

    useEffect(() => {
        const query = values.userSearch.trim();
        if (query.length < MIN_SEARCH_LENGTH) {
            setSuggestions([]);
            setSuggestionState("idle");
            setSuggestionError(null);
            return undefined;
        }

        const controller = new AbortController();
        const timeout = window.setTimeout(() => {
            setSuggestionState("loading");
            setSuggestionError(null);

            searchUsersByName(query, {
                page: 0,
                limit: SUGGESTION_LIMIT,
                signal: controller.signal,
            })
                .then((users) => {
                    const filteredUsers = users.filter((candidate) => (
                        Number(candidate.id) !== ownerId
                        && !selectedMemberIds.has(Number(candidate.id))
                    ));
                    setSuggestions(filteredUsers);
                    setSuggestionState("ready");
                })
                .catch((error) => {
                    if (error.name === "AbortError") {
                        return;
                    }
                    setSuggestions([]);
                    setSuggestionError(error);
                    setSuggestionState("error");
                });
        }, 300);

        return () => {
            window.clearTimeout(timeout);
            controller.abort();
        };
    }, [ownerId, selectedMemberIds, values.userSearch]);

    const clearError = (field) => {
        setErrors((current) => {
            const next = { ...current };
            delete next[field];
            return next;
        });
    };

    const handleChange = (event) => {
        const { name, value } = event.target;
        setGlobalError(null);
        setValues((current) => ({
            ...current,
            [name]: value,
        }));
        clearError(name);
    };

    const handleFileChange = (event) => {
        const { name, files: selectedFiles } = event.target;
        setGlobalError(null);
        setFiles((current) => ({
            ...current,
            [name]: selectedFiles?.[0] ?? null,
        }));
        clearError(name);
    };

    const validate = () => {
        const nextErrors = {};

        if (!values.name.trim()) {
            nextErrors.name = t("form.requiredField");
        }

        if (values.name.trim().length > 100) {
            nextErrors.name = t("Size", { defaultValue: "Max size is 100 characters long." });
        }

        ["profilePicture", "banner"].forEach((field) => {
            const file = files[field];
            if (!file) {
                return;
            }
            if (!isImageFile(file)) {
                nextErrors[field] = t("team.create.error.invalidImage");
            } else if (file.size > MAX_IMAGE_SIZE) {
                nextErrors[field] = t("error.invalidImage.maxsize");
            }
        });

        const uniqueIds = new Set(members.map((member) => Number(member.id)));
        if (uniqueIds.size !== members.length) {
            nextErrors.members = t("team.create.error.duplicateMember");
        }

        setErrors(nextErrors);
        return Object.keys(nextErrors).length === 0;
    };

    const handleAddMember = (candidate) => {
        setGlobalError(null);
        if (!candidate?.id || !candidate?.username) {
            setErrors((current) => ({
                ...current,
                members: t("team.create.error.invalidSelectedUser"),
            }));
            return;
        }

        if (Number(candidate.id) === ownerId || selectedMemberIds.has(Number(candidate.id))) {
            setErrors((current) => ({
                ...current,
                members: t("team.create.error.duplicateMember"),
            }));
            return;
        }

        setMembers((current) => [...current, candidate]);
        setValues((current) => ({ ...current, userSearch: "" }));
        setSuggestions([]);
        clearError("members");
    };

    const handleRemoveMember = (memberId) => {
        setMembers((current) => current.filter((member) => Number(member.id) !== Number(memberId)));
        clearError("members");
    };

    const handleSubmitError = (requestError) => {
        if (requestError.status === 401) {
            navigate("/login", { replace: true, state: { from: routerLocation, status: 401 } });
            return;
        }

        if (requestError.status === 400 && requestError.details) {
            setErrors(mapBackendDetails(requestError.details, t));
            setGlobalError(t("team.create.error.validation"));
            return;
        }

        if (requestError.status === 403) {
            setGlobalError(t("team.create.error.forbidden"));
            return;
        }

        if (requestError.status === 409) {
            setGlobalError(requestError.message || t("team.create.error.nameTaken"));
            return;
        }

        setGlobalError(requestError.message || t("team.create.error.generic"));
    };

    const handleSubmit = async (event) => {
        event.preventDefault();
        setGlobalError(null);

        if (!validate()) {
            setGlobalError(t("team.create.error.validation"));
            return;
        }

        setSubmitting(true);
        try {
            const payload = await buildCreatePayload(values, files, members);
            const { team, location: createdLocation } = await createTeam(payload);
            const teamId = getIdFromLink(createdLocation) ?? team?.id;
            navigate(returnTo ?? (teamId ? `/teams/${teamId}` : "/tournaments"), { replace: true });
        } catch (requestError) {
            handleSubmitError(requestError);
        } finally {
            setSubmitting(false);
        }
    };

    const handleCancel = () => {
        navigate(returnTo ?? "/tournaments");
    };

    const pageTitle = t("team.create.pageTitle");
    const formDisabled = submitting;

    return (
        <Layout pageTitle={pageTitle}>
            <div className={styles.page}>
                <div className={styles.image}>
                    <img src="/assets/tournament.jpeg" alt="" />
                </div>
                <section className={styles.panel}>
                    <AppText type="title" size="l">{pageTitle}</AppText>
                    {globalError && <p className={styles.error} role="alert">{globalError}</p>}

                    <form className={styles.form} onSubmit={handleSubmit} noValidate>
                        <fieldset className={styles.section} disabled={formDisabled}>
                            <legend className={styles.legend}>{t("team.create.section.basic")}</legend>
                            <Input
                                id="name"
                                name="name"
                                label={t("team.create.name")}
                                value={values.name}
                                disabled={formDisabled}
                                error={errors.name}
                                onChange={handleChange}
                            />
                        </fieldset>

                        <fieldset className={styles.section} disabled={formDisabled}>
                            <legend className={styles.legend}>{t("team.create.section.images")}</legend>
                            <div className={styles.row}>
                                <FileInput
                                    id="profilePicture"
                                    name="profilePicture"
                                    label={t("team.create.teamImage")}
                                    file={files.profilePicture}
                                    error={errors.profilePicture}
                                    onChange={handleFileChange}
                                />
                                <FileInput
                                    id="banner"
                                    name="banner"
                                    label={t("team.create.teamBanner")}
                                    file={files.banner}
                                    error={errors.banner}
                                    onChange={handleFileChange}
                                />
                            </div>
                        </fieldset>

                        <fieldset className={styles.section} disabled={formDisabled}>
                            <legend className={styles.legend}>{t("team.create.members")}</legend>
                            <p className={styles.hint}>{t("team.create.ownerHint")}</p>
                            <label htmlFor="userSearch" className={styles.searchLabel}>
                                <AppText size="l" weight="semi-bold">{t("team.create.searchMembers")}</AppText>
                                <input
                                    id="userSearch"
                                    name="userSearch"
                                    type="search"
                                    className={styles.searchInput}
                                    value={values.userSearch}
                                    disabled={formDisabled}
                                    placeholder={t("team.create.addMember.placeholder")}
                                    onChange={handleChange}
                                />
                            </label>
                            <UserSuggestions
                                state={suggestionState}
                                error={suggestionError}
                                suggestions={suggestions}
                                onAdd={handleAddMember}
                            />
                            {members.length > 0 && (
                                <div className={styles.memberList} aria-label={t("team.create.selectedMembers")}>
                                    {members.map((member) => (
                                        <div key={member.id} className={styles.memberChip} data-testid="selected-member">
                                            <span>{member.username}</span>
                                            <button
                                                type="button"
                                                className={styles.removeButton}
                                                disabled={formDisabled}
                                                onClick={() => handleRemoveMember(member.id)}
                                            >
                                                {t("team.create.removeMember")}
                                            </button>
                                        </div>
                                    ))}
                                </div>
                            )}
                            {errors.members && <p className={styles.fieldError}>{errors.members}</p>}
                        </fieldset>

                        <div className={styles.actions}>
                            <Button
                                type="submit"
                                text={submitting ? t("team.create.submitting") : t("team.create.create")}
                                disabled={formDisabled}
                            />
                            <Button
                                type="button"
                                text={t("team.create.cancel")}
                                secondary
                                disabled={formDisabled}
                                onClick={handleCancel}
                            />
                        </div>
                    </form>
                </section>
            </div>
        </Layout>
    );
};

const UserSuggestions = ({ state, error, suggestions, onAdd }) => {
    const { t } = useTranslation();

    if (state === "idle") {
        return <p className={styles.hint}>{t("team.create.searchHint")}</p>;
    }

    if (state === "loading") {
        return <p className={styles.status}>{t("team.create.searchLoading")}</p>;
    }

    if (state === "error") {
        return (
            <p className={styles.error} role="alert">
                {error?.message || t("team.create.searchError")}
            </p>
        );
    }

    if (suggestions.length === 0) {
        return <p className={styles.empty}>{t("team.create.searchEmpty")}</p>;
    }

    return (
        <div className={styles.suggestions}>
            {suggestions.map((candidate) => (
                <button
                    key={candidate.id}
                    type="button"
                    className={styles.suggestionButton}
                    onClick={() => onAdd(candidate)}
                >
                    {candidate.username}
                </button>
            ))}
        </div>
    );
};

const FileInput = ({ id, name, label, file, error, onChange }) => (
    <label htmlFor={id} className={styles.fileLabel}>
        <AppText size="l" weight="semi-bold">{label}</AppText>
        <input
            id={id}
            name={name}
            type="file"
            accept="image/jpeg,image/png"
            className={styles.fileInput}
            onChange={onChange}
        />
        {file && <span className={styles.fileName}>{file.name}</span>}
        {error && <p className={styles.fieldError}>{error}</p>}
    </label>
);
