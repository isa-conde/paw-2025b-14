import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import { fireEvent, render, screen, waitFor } from "@testing-library/react";
import { HelmetProvider } from "@dr.pogodin/react-helmet";
import { MemoryRouter, Route, Routes, useLocation } from "react-router-dom";
import { AuthProvider, AUTH_STORAGE_KEYS } from "../auth/AuthContext.jsx";
import { ProtectedRoute } from "../auth/ProtectedRoute.jsx";
import { setApiToken } from "../api/client.js";
import { VENDOR_TYPES } from "../api/vendorTypes.js";
import { NewTournamentPage } from "../pages/NewTournamentPage.jsx";

vi.mock("react-i18next", () => ({
    useTranslation: () => ({
        t: (key, options) => options?.defaultValue ?? key,
        i18n: { language: "en" },
    }),
}));

const games = [
    {
        id: 1,
        name: "League of Legends",
        links: [
            { rel: "self", href: "http://localhost/api/games/1" },
            { rel: "formats", href: "http://localhost/api/games/1/formats" },
        ],
    },
    {
        id: 2,
        name: "Valorant",
        links: [
            { rel: "self", href: "http://localhost/api/games/2" },
            { rel: "formats", href: "http://localhost/api/games/2/formats" },
        ],
    },
];

const formatsByGame = {
    1: [{ id: 10, name: "5v5", playersPerTeam: 5 }],
    2: [{ id: 20, name: "1v1", playersPerTeam: 1 }],
};

const createdTournament = {
    id: 99,
    name: "Autumn Open",
    links: [
        { rel: "self", href: "http://localhost/api/tournaments/99" },
        { rel: "game", href: "http://localhost/api/games/1" },
        { rel: "format", href: "http://localhost/api/games/1/formats/10" },
    ],
};

const jsonResponse = (body, mediaType = "application/json", status = 200, headers = {}) => new Response(
    JSON.stringify(body),
    {
        status,
        headers: {
            "Content-Type": mediaType,
            ...headers,
        },
    },
);

const createFetchMock = ({ postResponse } = {}) => vi.fn((url, options = {}) => {
    const requestUrl = new URL(url, "http://localhost");

    if (requestUrl.pathname === "/api/games") {
        return Promise.resolve(jsonResponse(games, VENDOR_TYPES.gameList));
    }

    const formatsMatch = requestUrl.pathname.match(/^\/api\/games\/(\d+)\/formats$/);
    if (formatsMatch) {
        return Promise.resolve(jsonResponse(
            formatsByGame[formatsMatch[1]] ?? [],
            VENDOR_TYPES.gameFormatList,
        ));
    }

    if (requestUrl.pathname === "/api/tournaments" && options.method === "POST") {
        return Promise.resolve(postResponse ?? jsonResponse(
            createdTournament,
            VENDOR_TYPES.tournament,
            201,
            { Location: "http://localhost/api/tournaments/99" },
        ));
    }

    return Promise.resolve(jsonResponse({ message: "unexpected" }, "application/json", 500));
});

const LocationProbe = () => {
    const location = useLocation();
    return (
        <>
            <div data-testid="location">{location.pathname}{location.search}</div>
            <div data-testid="from">{location.state?.from?.pathname ?? ""}</div>
            <div data-testid="reason">{location.state?.reason ?? ""}</div>
        </>
    );
};

const authenticate = (user = { id: 7, username: "owner", verified: true }) => {
    localStorage.setItem(AUTH_STORAGE_KEYS.token, "Bearer test-token");
    localStorage.setItem(AUTH_STORAGE_KEYS.user, JSON.stringify(user));
};

const renderCreateRoute = (initialEntry = "/tournaments/new") => render(
    <HelmetProvider>
        <MemoryRouter initialEntries={[initialEntry]}>
            <AuthProvider>
                <Routes>
                    <Route
                        path="/tournaments/new"
                        element={(
                            <ProtectedRoute requireVerified>
                                <NewTournamentPage />
                                <LocationProbe />
                            </ProtectedRoute>
                        )}
                    />
                    <Route path="/login" element={<><div>Login</div><LocationProbe /></>} />
                    <Route path="/403" element={<><div>Forbidden</div><LocationProbe /></>} />
                    <Route path="/tournaments/:tournamentId" element={<LocationProbe />} />
                </Routes>
            </AuthProvider>
        </MemoryRouter>
    </HelmetProvider>,
);

const requestUrls = (fetchMock) => fetchMock.mock.calls.map(([url]) => new URL(url, "http://localhost"));

const postCall = (fetchMock) => fetchMock.mock.calls.find(([url, options = {}]) => (
    new URL(url, "http://localhost").pathname === "/api/tournaments"
    && options.method === "POST"
));

const fillRequiredFields = async () => {
    fireEvent.change(screen.getByLabelText("createTournament.name"), {
        target: { value: "Autumn Open" },
    });
    await screen.findByRole("option", { name: "League of Legends" });
    fireEvent.change(screen.getByLabelText("createTournament.game"), {
        target: { value: "1" },
    });

    await screen.findByRole("option", { name: "5v5" });
    fireEvent.change(screen.getByLabelText("createTournament.format"), {
        target: { value: "10" },
    });
    fireEvent.change(screen.getByLabelText("createTournament.structure"), {
        target: { value: "LEAGUE" },
    });
    fireEvent.change(screen.getByLabelText("createTournament.region"), {
        target: { value: "LAS" },
    });
    fireEvent.change(screen.getByLabelText("createTournament.skillLevel"), {
        target: { value: "FREE" },
    });
    fireEvent.change(screen.getByLabelText("createTournament.startDate"), {
        target: { value: "2099-08-01" },
    });
    fireEvent.change(screen.getByLabelText("createTournament.endDate"), {
        target: { value: "2099-08-10" },
    });
    fireEvent.change(screen.getByLabelText("createTournament.maxParticipants"), {
        target: { value: "16" },
    });
};

describe("NewTournamentPage", () => {
    beforeEach(() => {
        localStorage.clear();
        setApiToken(null);
    });

    afterEach(() => {
        localStorage.clear();
        setApiToken(null);
        vi.unstubAllGlobals();
    });

    it("protects /tournaments/new and preserves the return path", () => {
        vi.stubGlobal("fetch", createFetchMock());

        renderCreateRoute();

        expect(screen.getByText("Login")).toBeInTheDocument();
        expect(screen.getByTestId("from")).toHaveTextContent("/tournaments/new");
    });

    it("redirects unverified users to forbidden", () => {
        authenticate({ id: 7, username: "owner", verified: false });
        vi.stubGlobal("fetch", createFetchMock());

        renderCreateRoute();

        expect(screen.getByText("Forbidden")).toBeInTheDocument();
        expect(screen.getByTestId("reason")).toHaveTextContent("unverified");
    });

    it("loads games from the API", async () => {
        authenticate();
        const fetchMock = createFetchMock();
        vi.stubGlobal("fetch", fetchMock);

        renderCreateRoute();

        expect(await screen.findByRole("option", { name: "League of Legends" })).toBeInTheDocument();
        const [, options] = fetchMock.mock.calls.find(([url]) => (
            new URL(url, "http://localhost").pathname === "/api/games"
        ));
        expect(options.headers.Accept).toBe(VENDOR_TYPES.gameList);
        expect(options.credentials).toBeUndefined();
    });

    it("loads formats after choosing a game and resets incompatible formatId", async () => {
        authenticate();
        const fetchMock = createFetchMock();
        vi.stubGlobal("fetch", fetchMock);

        renderCreateRoute();

        await screen.findByRole("option", { name: "League of Legends" });
        fireEvent.change(screen.getByLabelText("createTournament.game"), {
            target: { value: "1" },
        });

        await screen.findByRole("option", { name: "5v5" });
        fireEvent.change(screen.getByLabelText("createTournament.format"), {
            target: { value: "10" },
        });
        expect(screen.getByLabelText("createTournament.format")).toHaveValue("10");

        fireEvent.change(screen.getByLabelText("createTournament.game"), {
            target: { value: "2" },
        });

        expect(screen.getByLabelText("createTournament.format")).toHaveValue("");
        expect(await screen.findByRole("option", { name: "1v1" })).toBeInTheDocument();
        expect(requestUrls(fetchMock).some((requestUrl) => (
            requestUrl.pathname === "/api/games/2/formats"
        ))).toBe(true);
    });

    it("shows client validation errors without posting", async () => {
        authenticate();
        const fetchMock = createFetchMock();
        vi.stubGlobal("fetch", fetchMock);

        renderCreateRoute();

        await screen.findByRole("option", { name: "League of Legends" });
        fireEvent.click(screen.getByRole("button", { name: "createTournament.create" }));

        expect(await screen.findByRole("alert")).toHaveTextContent("createTournament.error.validation");
        expect(screen.getAllByText("form.requiredField").length).toBeGreaterThan(0);
        expect(postCall(fetchMock)).toBeUndefined();
    });

    it("posts with vendor headers, bearer auth, file base64, and navigates to detail", async () => {
        authenticate();
        const fetchMock = createFetchMock();
        vi.stubGlobal("fetch", fetchMock);

        renderCreateRoute();
        await fillRequiredFields();
        fireEvent.change(screen.getByLabelText("createTournament.image"), {
            target: { files: [new File(["img"], "banner.png", { type: "image/png" })] },
        });
        fireEvent.change(screen.getByLabelText("createTournament.rules"), {
            target: { files: [new File(["pdf"], "rules.pdf", { type: "application/pdf" })] },
        });
        fireEvent.change(screen.getByLabelText("createTournament.serverName"), {
            target: { value: "SA-1" },
        });
        fireEvent.change(screen.getByLabelText("createTournament.discordChannel"), {
            target: { value: "https://discord.gg/rankup" },
        });

        fireEvent.click(screen.getByRole("button", { name: "createTournament.create" }));

        await waitFor(() => {
            expect(screen.getByTestId("location")).toHaveTextContent("/tournaments/99");
        });

        const [, options] = postCall(fetchMock);
        const payload = JSON.parse(options.body);

        expect(options.headers.Accept).toBe(VENDOR_TYPES.tournament);
        expect(options.headers["Content-Type"]).toBe(VENDOR_TYPES.tournamentCreate);
        expect(options.headers.Authorization).toBe("Bearer test-token");
        expect(options.credentials).toBeUndefined();
        expect(payload).toMatchObject({
            name: "Autumn Open",
            gameId: 1,
            formatId: 10,
            structure: "LEAGUE",
            region: "LAS",
            elo: "FREE",
            maxParticipants: 16,
            imageBase64: "aW1n",
            rulesBase64: "cGRm",
            serverName: "SA-1",
            serverPassword: null,
            discordChannel: "https://discord.gg/rankup",
        });
        expect(payload.creatorId).toBeUndefined();
        expect(payload.userId).toBeUndefined();
        expect(payload.ownerId).toBeUndefined();
        expect(requestUrls(fetchMock).every((requestUrl) => !requestUrl.pathname.includes("/me"))).toBe(true);
        expect(fetchMock.mock.calls.every(([, callOptions = {}]) => callOptions.credentials === undefined)).toBe(true);
    });

    it("shows backend validation details by field", async () => {
        authenticate();
        const fetchMock = createFetchMock({
            postResponse: jsonResponse({
                status: 400,
                error: "Bad Request",
                message: "Validation failed",
                details: {
                    name: "{createTournament.notNull}",
                    maxParticipants: "{createTournament.minParticipants}",
                },
            }, "application/json", 400),
        });
        vi.stubGlobal("fetch", fetchMock);

        renderCreateRoute();
        await fillRequiredFields();
        fireEvent.click(screen.getByRole("button", { name: "createTournament.create" }));

        expect(await screen.findByRole("alert")).toHaveTextContent("createTournament.error.validation");
        expect(screen.getByText(/createTournament\.notNull/)).toBeInTheDocument();
        expect(screen.getByText(/createTournament\.minParticipants/)).toBeInTheDocument();
    });
});
