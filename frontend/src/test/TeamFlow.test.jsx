import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import { fireEvent, render, screen, waitFor } from "@testing-library/react";
import { HelmetProvider } from "@dr.pogodin/react-helmet";
import { MemoryRouter, Route, Routes, useLocation } from "react-router-dom";
import { AuthProvider, AUTH_STORAGE_KEYS } from "../auth/AuthContext.jsx";
import { ProtectedRoute } from "../auth/ProtectedRoute.jsx";
import { setApiToken } from "../api/client.js";
import { VENDOR_TYPES } from "../api/vendorTypes.js";
import { NewTeamPage } from "../pages/NewTeamPage.jsx";
import { TeamPage } from "../pages/TeamPage.jsx";

vi.mock("react-i18next", () => ({
    useTranslation: () => ({
        t: (key, options) => options?.defaultValue ?? key,
        i18n: { language: "en" },
    }),
}));

const owner = {
    id: 7,
    username: "owner",
    verified: true,
    links: [{ rel: "self", href: "http://localhost/api/users/7" }],
};

const bob = {
    id: 8,
    username: "bob",
    verified: true,
    links: [{ rel: "self", href: "http://localhost/api/users/8" }],
};

const charlie = {
    id: 9,
    username: "charlie",
    verified: true,
    links: [{ rel: "self", href: "http://localhost/api/users/9" }],
};

const createdTeam = {
    id: 55,
    name: "Alpha",
    links: [
        { rel: "self", href: "http://localhost/api/teams/55" },
        { rel: "owner", href: "http://localhost/api/users/7" },
        { rel: "profilePicture", href: "http://localhost/api/images/100" },
        { rel: "banner", href: "http://localhost/api/images/101" },
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
    const path = requestUrl.pathname;

    if (path === "/api/users" && options.method !== "POST") {
        const name = requestUrl.searchParams.get("name") ?? "";
        const users = [owner, bob, charlie].filter((user) => (
            user.username.includes(name.toLowerCase())
        ));
        return Promise.resolve(jsonResponse(users, VENDOR_TYPES.userList));
    }

    if (path === "/api/teams" && options.method === "POST") {
        return Promise.resolve(postResponse ?? jsonResponse(
            createdTeam,
            VENDOR_TYPES.team,
            201,
            { Location: "http://localhost/api/teams/55" },
        ));
    }

    if (path === "/api/teams/55") {
        return Promise.resolve(jsonResponse(createdTeam, VENDOR_TYPES.team));
    }

    if (path === "/api/teams/55/members") {
        return Promise.resolve(jsonResponse([owner, bob], VENDOR_TYPES.userList));
    }

    if (path === "/api/users/7") {
        return Promise.resolve(jsonResponse(owner, VENDOR_TYPES.user));
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

const authenticate = (user = owner) => {
    localStorage.setItem(AUTH_STORAGE_KEYS.token, "Bearer test-token");
    localStorage.setItem(AUTH_STORAGE_KEYS.user, JSON.stringify(user));
};

const renderNewTeamRoute = (initialEntry = "/teams/new") => render(
    <HelmetProvider>
        <MemoryRouter initialEntries={[initialEntry]}>
            <AuthProvider>
                <Routes>
                    <Route
                        path="/teams/new"
                        element={(
                            <ProtectedRoute requireVerified>
                                <NewTeamPage />
                                <LocationProbe />
                            </ProtectedRoute>
                        )}
                    />
                    <Route path="/login" element={<><div>Login</div><LocationProbe /></>} />
                    <Route path="/403" element={<><div>Forbidden</div><LocationProbe /></>} />
                    <Route path="/teams/:teamId" element={<LocationProbe />} />
                    <Route path="/tournaments/:tournamentId" element={<LocationProbe />} />
                </Routes>
            </AuthProvider>
        </MemoryRouter>
    </HelmetProvider>,
);

const renderTeamRoute = () => render(
    <HelmetProvider>
        <MemoryRouter initialEntries={["/teams/55"]}>
            <AuthProvider>
                <Routes>
                    <Route path="/teams/:teamId" element={<TeamPage />} />
                </Routes>
            </AuthProvider>
        </MemoryRouter>
    </HelmetProvider>,
);

const requestUrls = (fetchMock) => fetchMock.mock.calls.map(([url]) => new URL(url, "http://localhost"));

const postTeamCall = (fetchMock) => fetchMock.mock.calls.find(([url, options = {}]) => (
    new URL(url, "http://localhost").pathname === "/api/teams"
    && options.method === "POST"
));

describe("team frontend flow", () => {
    beforeEach(() => {
        localStorage.clear();
        setApiToken(null);
    });

    afterEach(() => {
        localStorage.clear();
        setApiToken(null);
        vi.unstubAllGlobals();
    });

    it("protects /teams/new and preserves the return path", () => {
        vi.stubGlobal("fetch", createFetchMock());

        renderNewTeamRoute("/teams/new?returnTo=%2Ftournaments%2F12");

        expect(screen.getByText("Login")).toBeInTheDocument();
        expect(screen.getByTestId("from")).toHaveTextContent("/teams/new");
    });

    it("redirects unverified users away from /teams/new", () => {
        authenticate({ ...owner, verified: false });
        vi.stubGlobal("fetch", createFetchMock());

        renderNewTeamRoute();

        expect(screen.getByText("Forbidden")).toBeInTheDocument();
        expect(screen.getByTestId("reason")).toHaveTextContent("unverified");
    });

    it("creates a team with vendor headers, bearer auth, selected usernames and no ownerId", async () => {
        authenticate();
        const fetchMock = createFetchMock();
        vi.stubGlobal("fetch", fetchMock);

        renderNewTeamRoute();

        fireEvent.change(screen.getByLabelText("team.create.name"), {
            target: { value: "Alpha" },
        });
        fireEvent.change(screen.getByLabelText("team.create.searchMembers"), {
            target: { value: "bo" },
        });
        fireEvent.click(await screen.findByRole("button", { name: "bob" }));
        fireEvent.change(screen.getByLabelText("team.create.teamImage"), {
            target: { files: [new File(["pfp"], "pfp.png", { type: "image/png" })] },
        });

        fireEvent.click(screen.getByRole("button", { name: "team.create.create" }));

        await waitFor(() => {
            expect(screen.getByTestId("location")).toHaveTextContent("/teams/55");
        });

        const [, options] = postTeamCall(fetchMock);
        const payload = JSON.parse(options.body);

        expect(options.headers.Accept).toBe(VENDOR_TYPES.team);
        expect(options.headers["Content-Type"]).toBe(VENDOR_TYPES.teamCreate);
        expect(options.headers.Authorization).toBe("Bearer test-token");
        expect(options.credentials).toBeUndefined();
        expect(payload).toMatchObject({
            name: "Alpha",
            profilePictureBase64: "cGZw",
            bannerBase64: null,
            members: ["bob"],
        });
        expect(payload.ownerId).toBeUndefined();
        expect(payload.userId).toBeUndefined();
        expect(requestUrls(fetchMock).every((requestUrl) => !requestUrl.pathname.includes("/me"))).toBe(true);
    });

    it("searches users by name on a single paginated query and filters duplicates", async () => {
        authenticate();
        const fetchMock = createFetchMock();
        vi.stubGlobal("fetch", fetchMock);

        renderNewTeamRoute();

        fireEvent.change(screen.getByLabelText("team.create.searchMembers"), {
            target: { value: "bo" },
        });
        fireEvent.click(await screen.findByRole("button", { name: "bob" }));
        expect(screen.getAllByTestId("selected-member")).toHaveLength(1);

        fireEvent.change(screen.getByLabelText("team.create.searchMembers"), {
            target: { value: "bob" },
        });

        expect(await screen.findByText("team.create.searchEmpty")).toBeInTheDocument();
        expect(screen.getAllByTestId("selected-member")).toHaveLength(1);

        const userCalls = fetchMock.mock.calls.filter(([url]) => (
            new URL(url, "http://localhost").pathname === "/api/users"
        ));
        expect(userCalls.length).toBeGreaterThan(0);
        const firstSearchUrl = new URL(userCalls[0][0], "http://localhost");
        expect(firstSearchUrl.searchParams.get("name")).toBe("bo");
        expect(firstSearchUrl.searchParams.get("page")).toBe("0");
        expect(userCalls.every(([, options = {}]) => options.credentials === undefined)).toBe(true);
    });

    it("returns to a tournament after creating a team from the join flow", async () => {
        authenticate();
        const fetchMock = createFetchMock();
        vi.stubGlobal("fetch", fetchMock);

        renderNewTeamRoute("/teams/new?returnTo=%2Ftournaments%2F12");

        fireEvent.change(screen.getByLabelText("team.create.name"), {
            target: { value: "Alpha" },
        });
        fireEvent.click(screen.getByRole("button", { name: "team.create.create" }));

        await waitFor(() => {
            expect(screen.getByTestId("location")).toHaveTextContent("/tournaments/12");
        });
    });

    it("loads the team page with real owner and members", async () => {
        const fetchMock = createFetchMock();
        vi.stubGlobal("fetch", fetchMock);

        renderTeamRoute();

        expect(await screen.findByRole("heading", { name: "Alpha" })).toBeInTheDocument();
        expect(screen.getAllByText("owner").length).toBeGreaterThan(0);
        expect(screen.getByText("bob")).toBeInTheDocument();

        const membersCall = fetchMock.mock.calls.find(([url]) => (
            new URL(url, "http://localhost").pathname === "/api/teams/55/members"
        ));
        expect(membersCall[1].headers.Accept).toBe(VENDOR_TYPES.userList);
    });
});
