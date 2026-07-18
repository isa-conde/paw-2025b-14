import { useEffect, useMemo, useState } from "react";
import { useTranslation } from "react-i18next";
import { useLocation, useNavigate } from "react-router-dom";
import { Layout } from "../components/Layout.jsx";
import { AppText } from "../components/AppText.jsx";
import { Input } from "../components/Input.jsx";
import { Button } from "../components/Button.jsx";
import { listAllGames, getGameFormats } from "../api/games.js";
import { createTournament, readFileAsBase64 } from "../api/tournaments.js";
import { getIdFromLink } from "../api/client.js";
import {
    getEloOptions,
    getRegionOptions,
    getStructureOptions,
} from "../domain/tournamentFilters.js";
import styles from "../styles/pages/NewTournamentPage.module.css";

const MIN_PARTICIPANTS = 4;
const MAX_PARTICIPANTS = 32;
const MAX_IMAGE_SIZE = 3 * 1024 * 1024;
const MAX_RULES_SIZE = 4 * 1024 * 1024;
const IMAGE_TYPES = ["image/jpeg", "image/png"];
const IMAGE_EXTENSION_PATTERN = /\.(jpe?g|png)$/i;
const DISCORD_URL_PATTERN = /^https:\/\/(discord\.gg|discord(?:app)?\.com)\/[A-Za-z0-9/@._-]+$/;

const initialValues = {
    name: "",
    gameId: "",
    formatId: "",
    structure: "",
    region: "",
    elo: "",
    startDate: "",
    endDate: "",
    maxParticipants: "",
    serverName: "",
    serverPassword: "",
    discordChannel: "",
};

const requiredFields = [
    "name",
    "gameId",
    "formatId",
    "structure",
    "region",
    "elo",
    "startDate",
    "endDate",
    "maxParticipants",
];

const getTodayInputValue = () => {
    const today = new Date();
    today.setMinutes(today.getMinutes() - today.getTimezoneOffset());
    return today.toISOString().slice(0, 10);
};

const optionalValue = (value) => {
    const trimmed = value.trim();
    return trimmed ? trimmed : null;
};

const isImageFile = (file) => (
    IMAGE_TYPES.includes(file.type) || IMAGE_EXTENSION_PATTERN.test(file.name)
);

const isPdfFile = (file) => (
    file.type === "application/pdf" || /\.pdf$/i.test(file.name)
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

const buildCreatePayload = async (values, files) => ({
    name: values.name.trim(),
    gameId: Number(values.gameId),
    region: values.region,
    elo: values.elo,
    startDate: values.startDate,
    endDate: values.endDate,
    structure: values.structure,
    maxParticipants: Number(values.maxParticipants),
    formatId: Number(values.formatId),
    imageBase64: files.image ? await readFileAsBase64(files.image) : null,
    rulesBase64: files.rules ? await readFileAsBase64(files.rules) : null,
    serverName: optionalValue(values.serverName),
    serverPassword: optionalValue(values.serverPassword),
    discordChannel: optionalValue(values.discordChannel),
});

export const NewTournamentPage = () => {
    const { t } = useTranslation();
    const navigate = useNavigate();
    const location = useLocation();
    const today = useMemo(() => getTodayInputValue(), []);
    const regionOptions = useMemo(() => getRegionOptions(t), [t]);
    const eloOptions = useMemo(() => getEloOptions(t), [t]);
    const structureOptions = useMemo(() => getStructureOptions(t), [t]);

    const [values, setValues] = useState(initialValues);
    const [files, setFiles] = useState({ image: null, rules: null });
    const [errors, setErrors] = useState({});
    const [globalError, setGlobalError] = useState(null);
    const [games, setGames] = useState([]);
    const [formats, setFormats] = useState([]);
    const [gamesLoading, setGamesLoading] = useState(true);
    const [formatsLoading, setFormatsLoading] = useState(false);
    const [gamesError, setGamesError] = useState(null);
    const [formatsError, setFormatsError] = useState(null);
    const [submitting, setSubmitting] = useState(false);

    useEffect(() => {
        let isMounted = true;
        const controller = new AbortController();

        const loadGames = async () => {
            setGamesLoading(true);
            setGamesError(null);

            try {
                const loadedGames = await listAllGames({ signal: controller.signal });
                if (isMounted) {
                    setGames(loadedGames);
                }
            } catch (loadError) {
                if (isMounted && loadError.name !== "AbortError") {
                    setGamesError(loadError);
                }
            } finally {
                if (isMounted) {
                    setGamesLoading(false);
                }
            }
        };

        loadGames();

        return () => {
            isMounted = false;
            controller.abort();
        };
    }, []);

    useEffect(() => {
        if (!values.gameId) {
            setFormats([]);
            setFormatsError(null);
            setFormatsLoading(false);
            return;
        }

        let isMounted = true;
        const controller = new AbortController();
        const selectedGame = games.find((game) => String(game.id) === String(values.gameId));

        const loadFormats = async () => {
            setFormats([]);
            setFormatsLoading(true);
            setFormatsError(null);

            try {
                const loadedFormats = await getGameFormats(selectedGame ?? values.gameId, {
                    signal: controller.signal,
                });
                if (isMounted) {
                    setFormats(Array.isArray(loadedFormats) ? loadedFormats : []);
                }
            } catch (loadError) {
                if (isMounted && loadError.name !== "AbortError") {
                    setFormatsError(loadError);
                }
            } finally {
                if (isMounted) {
                    setFormatsLoading(false);
                }
            }
        };

        loadFormats();

        return () => {
            isMounted = false;
            controller.abort();
        };
    }, [games, values.gameId]);

    const formatItems = useMemo(() => formats.map((format) => ({
        ...format,
        label: format.name ?? `${format.playersPerTeam}v${format.playersPerTeam}`,
    })), [formats]);

    const validate = () => {
        const nextErrors = {};

        requiredFields.forEach((field) => {
            if (!String(values[field] ?? "").trim()) {
                nextErrors[field] = t("form.requiredField");
            }
        });

        if (values.name.trim().length > 100) {
            nextErrors.name = t("Size", { defaultValue: "Max size is 100 characters long." });
        }

        if (values.startDate && values.startDate < today) {
            nextErrors.startDate = t("error.tournamentForm.invalidStartDate");
        }

        if (values.endDate && values.endDate < today) {
            nextErrors.endDate = t("error.tournamentForm.invalidEndDate");
        }

        if (values.startDate && values.endDate && values.endDate <= values.startDate) {
            nextErrors.endDate = t("error.tournamentForm.invalidDates");
        }

        const maxParticipants = Number(values.maxParticipants);
        if (values.maxParticipants && (!Number.isInteger(maxParticipants) || maxParticipants < MIN_PARTICIPANTS)) {
            nextErrors.maxParticipants = t("createTournament.minParticipants");
        }

        if (values.maxParticipants && maxParticipants > MAX_PARTICIPANTS) {
            nextErrors.maxParticipants = t("createTournament.maxParticipantsError");
        }

        if (values.serverPassword.trim() && !values.serverName.trim()) {
            nextErrors.serverPassword = t("error.tournamentForm.noServerName");
        }

        if (values.discordChannel.trim() && !DISCORD_URL_PATTERN.test(values.discordChannel.trim())) {
            nextErrors.discordChannel = t("error.tournamentForm.notADiscordLink");
        }

        if (files.image && !isImageFile(files.image)) {
            nextErrors.image = t("error.tournamentForm.invalidImage");
        }

        if (files.image && files.image.size > MAX_IMAGE_SIZE) {
            nextErrors.image = t("error.invalidImage.maxsize");
        }

        if (files.rules && !isPdfFile(files.rules)) {
            nextErrors.rules = t("error.invalidPdf");
        }

        if (files.rules && files.rules.size > MAX_RULES_SIZE) {
            nextErrors.rules = t("error.invalidPdf.maxsize");
        }

        setErrors(nextErrors);
        return Object.keys(nextErrors).length === 0;
    };

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
            ...(name === "gameId" ? { formatId: "" } : {}),
        }));
        clearError(name);
        if (name === "gameId") {
            clearError("formatId");
        }
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

    const handleSubmitError = (requestError) => {
        if (requestError.status === 401) {
            navigate("/login", { replace: true, state: { from: location, status: 401 } });
            return;
        }

        if (requestError.status === 400 && requestError.details) {
            setErrors(mapBackendDetails(requestError.details, t));
            setGlobalError(t("createTournament.error.validation"));
            return;
        }

        if (requestError.status === 403) {
            setGlobalError(t("createTournament.error.forbidden"));
            return;
        }

        if (requestError.status === 409) {
            setGlobalError(requestError.message || t("createTournament.error.conflict"));
            return;
        }

        setGlobalError(requestError.message || t("createTournament.error.generic"));
    };

    const handleSubmit = async (event) => {
        event.preventDefault();
        setGlobalError(null);

        if (!validate()) {
            setGlobalError(t("createTournament.error.validation"));
            return;
        }

        setSubmitting(true);
        try {
            const payload = await buildCreatePayload(values, files);
            const { tournament, location: createdLocation } = await createTournament(payload);
            const tournamentId = getIdFromLink(createdLocation) ?? tournament?.id;

            navigate(tournamentId ? `/tournaments/${tournamentId}` : "/tournaments", { replace: true });
        } catch (requestError) {
            handleSubmitError(requestError);
        } finally {
            setSubmitting(false);
        }
    };

    const pageTitle = t("createTournament.pageTitle");
    const emptyOption = t("createTournament.emptyOption");
    const tournamentImage = "/assets/tournament.jpeg";
    const formDisabled = submitting;
    const formatDisabled = formDisabled || !values.gameId || formatsLoading || !!formatsError;

    return (
        <Layout pageTitle={pageTitle}>
            <div className={styles.page}>
                <div className={styles.image}>
                    <img src={tournamentImage} alt="" />
                </div>
                <section className={styles.panel}>
                    <AppText type="title" size="l">{pageTitle}</AppText>
                    {gamesLoading && <p className={styles.status}>{t("createTournament.loadingGames")}</p>}
                    {gamesError && (
                        <p className={styles.error} role="alert">{t("createTournament.error.loadGames")}</p>
                    )}
                    {formatsLoading && <p className={styles.status}>{t("createTournament.loadingFormats")}</p>}
                    {formatsError && (
                        <p className={styles.error} role="alert">{t("createTournament.error.loadFormats")}</p>
                    )}
                    {globalError && <p className={styles.error} role="alert">{globalError}</p>}

                    <form className={styles.form} onSubmit={handleSubmit} noValidate>
                        <fieldset className={styles.section} disabled={formDisabled}>
                            <legend className={styles.legend}>{t("createTournament.section.basic")}</legend>
                            <Input
                                id="name"
                                name="name"
                                label={t("createTournament.name")}
                                value={values.name}
                                disabled={formDisabled}
                                error={errors.name}
                                onChange={handleChange}
                            />
                            <div className={styles.row}>
                                <Input
                                    id="gameId"
                                    name="gameId"
                                    label={t("createTournament.game")}
                                    type="select"
                                    items={games}
                                    itemValue="id"
                                    itemLabel="name"
                                    emptyOption={emptyOption}
                                    value={values.gameId}
                                    disabled={formDisabled || gamesLoading || !!gamesError}
                                    error={errors.gameId}
                                    onChange={handleChange}
                                />
                                <Input
                                    id="formatId"
                                    name="formatId"
                                    label={t("createTournament.format")}
                                    type="select"
                                    items={formatItems}
                                    itemValue="id"
                                    itemLabel="label"
                                    emptyOption={emptyOption}
                                    value={values.formatId}
                                    disabled={formatDisabled}
                                    error={errors.formatId}
                                    onChange={handleChange}
                                />
                            </div>
                            <div className={styles.row}>
                                <Input
                                    id="structure"
                                    name="structure"
                                    label={t("createTournament.structure")}
                                    type="select"
                                    itemMap={structureOptions}
                                    emptyOption={emptyOption}
                                    value={values.structure}
                                    disabled={formDisabled}
                                    error={errors.structure}
                                    onChange={handleChange}
                                />
                                <Input
                                    id="region"
                                    name="region"
                                    label={t("createTournament.region")}
                                    type="select"
                                    itemMap={regionOptions}
                                    emptyOption={emptyOption}
                                    value={values.region}
                                    disabled={formDisabled}
                                    error={errors.region}
                                    onChange={handleChange}
                                />
                                <Input
                                    id="elo"
                                    name="elo"
                                    label={t("createTournament.skillLevel")}
                                    type="select"
                                    itemMap={eloOptions}
                                    emptyOption={emptyOption}
                                    value={values.elo}
                                    disabled={formDisabled}
                                    error={errors.elo}
                                    onChange={handleChange}
                                />
                            </div>
                        </fieldset>

                        <fieldset className={styles.section} disabled={formDisabled}>
                            <legend className={styles.legend}>{t("createTournament.section.dates")}</legend>
                            <div className={styles.row}>
                                <Input
                                    id="startDate"
                                    name="startDate"
                                    label={t("createTournament.startDate")}
                                    type="date"
                                    min={today}
                                    value={values.startDate}
                                    disabled={formDisabled}
                                    error={errors.startDate}
                                    onChange={handleChange}
                                />
                                <Input
                                    id="endDate"
                                    name="endDate"
                                    label={t("createTournament.endDate")}
                                    type="date"
                                    min={values.startDate || today}
                                    value={values.endDate}
                                    disabled={formDisabled}
                                    error={errors.endDate}
                                    onChange={handleChange}
                                />
                            </div>
                        </fieldset>

                        <fieldset className={styles.section} disabled={formDisabled}>
                            <legend className={styles.legend}>{t("createTournament.section.participants")}</legend>
                            <Input
                                id="maxParticipants"
                                name="maxParticipants"
                                label={t("createTournament.maxParticipants")}
                                type="number"
                                min={MIN_PARTICIPANTS}
                                max={MAX_PARTICIPANTS}
                                step="1"
                                value={values.maxParticipants}
                                disabled={formDisabled}
                                error={errors.maxParticipants}
                                onChange={handleChange}
                            />
                        </fieldset>

                        <fieldset className={styles.section} disabled={formDisabled}>
                            <legend className={styles.legend}>{t("createTournament.section.optional")}</legend>
                            <div className={styles.row}>
                                <FileInput
                                    id="image"
                                    name="image"
                                    label={t("createTournament.image")}
                                    accept="image/jpeg,image/png"
                                    file={files.image}
                                    error={errors.image}
                                    onChange={handleFileChange}
                                />
                                <FileInput
                                    id="rules"
                                    name="rules"
                                    label={t("createTournament.rules")}
                                    accept="application/pdf"
                                    file={files.rules}
                                    error={errors.rules}
                                    onChange={handleFileChange}
                                />
                            </div>
                            <div className={styles.row}>
                                <Input
                                    id="serverName"
                                    name="serverName"
                                    label={t("createTournament.serverName")}
                                    value={values.serverName}
                                    disabled={formDisabled}
                                    error={errors.serverName}
                                    onChange={handleChange}
                                />
                                <Input
                                    id="serverPassword"
                                    name="serverPassword"
                                    label={t("createTournament.serverPassword")}
                                    type="password"
                                    value={values.serverPassword}
                                    disabled={formDisabled}
                                    error={errors.serverPassword}
                                    onChange={handleChange}
                                />
                            </div>
                            <Input
                                id="discordChannel"
                                name="discordChannel"
                                label={t("createTournament.discordChannel")}
                                type="url"
                                value={values.discordChannel}
                                disabled={formDisabled}
                                error={errors.discordChannel}
                                onChange={handleChange}
                            />
                        </fieldset>

                        <div className={styles.actions}>
                            <Button
                                type="submit"
                                text={submitting ? t("createTournament.submitting") : t("createTournament.create")}
                                disabled={formDisabled || gamesLoading || !!gamesError}
                            />
                            <Button
                                type="button"
                                text={t("createTournament.cancel")}
                                secondary
                                disabled={formDisabled}
                                onClick={() => navigate("/tournaments")}
                            />
                        </div>
                    </form>
                </section>
            </div>
        </Layout>
    );
};

const FileInput = ({ id, name, label, accept, file, error, onChange }) => (
    <label htmlFor={id} className={styles.fileLabel}>
        <AppText size="l" weight="semi-bold">{label}</AppText>
        <input
            id={id}
            name={name}
            type="file"
            accept={accept}
            className={styles.fileInput}
            onChange={onChange}
        />
        {file && <span className={styles.fileName}>{file.name}</span>}
        {error && <p className={styles.fieldError}>{error}</p>}
    </label>
);
