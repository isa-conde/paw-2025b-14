package ar.edu.itba.paw.webapp.restcontrollers;

import ar.edu.itba.paw.interfaces.exception.ParticipantNotFoundException;
import ar.edu.itba.paw.interfaces.exception.UserNotFoundException;
import ar.edu.itba.paw.interfaces.services.MatchService;
import ar.edu.itba.paw.interfaces.services.ParticipantService;
import ar.edu.itba.paw.interfaces.services.RulesService;
import ar.edu.itba.paw.interfaces.services.TeamService;
import ar.edu.itba.paw.interfaces.services.TournamentService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.model.Game.GameFormat;
import ar.edu.itba.paw.model.Match.Match;
import ar.edu.itba.paw.model.Participant;
import ar.edu.itba.paw.model.Rules;
import ar.edu.itba.paw.model.Team;
import ar.edu.itba.paw.model.Tournament;
import ar.edu.itba.paw.model.filters.TournamentFilter;
import ar.edu.itba.paw.webapp.auth.ApiAuthorizationService;
import ar.edu.itba.paw.webapp.auth.CurrentUserProvider;
import ar.edu.itba.paw.webapp.dto.entities.MatchDTO;
import ar.edu.itba.paw.webapp.dto.entities.MatchStageDTO;
import ar.edu.itba.paw.webapp.dto.entities.ParticipantDTO;
import ar.edu.itba.paw.webapp.dto.entities.TournamentDTO;
import ar.edu.itba.paw.webapp.dto.params.PaginationParams;
import ar.edu.itba.paw.webapp.dto.params.TournamentFilterParams;
import ar.edu.itba.paw.webapp.dto.requests.CreateTournamentRequest;
import ar.edu.itba.paw.webapp.dto.requests.JoinTournamentTeamRequest;
import ar.edu.itba.paw.webapp.dto.requests.SetMatchResultsRequest;
import ar.edu.itba.paw.webapp.dto.requests.TournamentStatusRequest;
import ar.edu.itba.paw.webapp.dto.requests.UpdateTournamentRequest;
import ar.edu.itba.paw.webapp.exception.ApiErrorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.BeanParam;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Path("tournaments")
@Component("tournamentRestController")
public class TournamentController {

    private final static Logger LOGGER = LoggerFactory.getLogger(TournamentController.class);

    private static final int PAGE_SIZE = 9;

    @Autowired
    private TournamentService tournamentService;

    @Autowired
    private RulesService rulesService;

    @Autowired
    private ParticipantService participantService;

    @Autowired
    private MatchService matchService;

    @Autowired
    private UserService userService;

    @Autowired
    private TeamService teamService;

    @Autowired
    private CurrentUserProvider currentUserProvider;

    @Autowired
    private ApiAuthorizationService apiAuthorizationService;

    @Context
    private UriInfo uriInfo;

    @GET
    @Produces(value = {Vendor.APPLICATION_TOURNAMENT_LIST})
    public Response listTournaments(@BeanParam TournamentFilterParams filterParams,
                                    @BeanParam PaginationParams paginationParams) {
        if (filterParams.getGameId() != null && filterParams.getGameId() <= 0) {
            return badRequest("Invalid gameId");
        }

        if (filterParams.getPlayersPerTeam() != null && filterParams.getPlayersPerTeam() <= 0) {
            return badRequest("playersPerTeam must be greater than 0");
        }

        TournamentFilter filter;
        try {
            filter = filterParams.toFilter();
        } catch (IllegalArgumentException ex) {
            return badRequest(ex.getMessage());
        }

        int totalPages = tournamentService.getPageAmount(PAGE_SIZE, filter);
        int page = adjustPage(paginationParams.getPage(), totalPages);

        List<TournamentDTO> tournaments = tournamentService.findTournaments(filter, page).stream()
                .map(TournamentDTO.mapper(uriInfo))
                .toList();

        Response.ResponseBuilder responseBuilder = Response.ok(new GenericEntity<>(tournaments) {});
        addPaginationLinks(responseBuilder, page, totalPages);
        return responseBuilder.build();
    }

    @POST
    @Consumes(value = {Vendor.APPLICATION_TOURNAMENT_CREATE})
    @Produces(value = {Vendor.APPLICATION_TOURNAMENT})
    public Response createTournament(@Valid CreateTournamentRequest request) {
        byte[] image = decodeBase64(request.getImageBase64());
        byte[] rules = decodeBase64(request.getRulesBase64());

        if (image == null && hasPayload(request.getImageBase64())) {
            return badRequest("Invalid imageBase64 payload");
        }

        if (rules == null && hasPayload(request.getRulesBase64())) {
            return badRequest("Invalid rulesBase64 payload");
        }

        Tournament tournament = tournamentService.create(
                currentUserProvider.getCurrentUserId(),
                request.getName(),
                request.getGameId(),
                request.getRegion(),
                request.getElo(),
                request.getStartDate(),
                request.getEndDate(),
                null,
                request.getStructure(),
                request.getMaxParticipants(),
                image,
                true,
                false,
                request.getFormatId(),
                rules,
                request.getServerName(),
                request.getServerPassword(),
                request.getDiscordChannel()
        );

        URI location = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(tournament.getId()))
                .build();

        return Response.created(location).entity(TournamentDTO.fromTournament(uriInfo, tournament)).build();
    }

    @GET
    @Path("/{id}")
    @Produces(value = {Vendor.APPLICATION_TOURNAMENT})
    public Response getTournament(@PathParam("id") long id) {
        if (id <= 0) {
            return badRequest("Invalid tournament id");
        }

        Optional<Tournament> tournament = tournamentService.findById(id);
        if (tournament.isEmpty()) {
            return notFound("Tournament not found");
        }

        return Response.ok(TournamentDTO.fromTournament(uriInfo, tournament.get())).build();
    }

    @PUT
    @Path("/{id}")
    @Consumes(value = {Vendor.APPLICATION_TOURNAMENT_UPDATE})
    @Produces(value = {Vendor.APPLICATION_TOURNAMENT})
    public Response updateTournament(@PathParam("id") long id, @Valid UpdateTournamentRequest request) {
        if (id <= 0) {
            return badRequest("Invalid tournament id");
        }

        Optional<Tournament> tournament = tournamentService.findById(id);
        if (tournament.isEmpty()) {
            return notFound("Tournament not found");
        }

        apiAuthorizationService.assertTournamentCreator(id);

        byte[] image = decodeBase64(request.getImageBase64());
        byte[] rules = decodeBase64(request.getRulesBase64());

        if (image == null && hasPayload(request.getImageBase64())) {
            return badRequest("Invalid imageBase64 payload");
        }

        if (rules == null && hasPayload(request.getRulesBase64())) {
            return badRequest("Invalid rulesBase64 payload");
        }

        tournamentService.updateTournamentInfo(
                id,
                request.getName(),
                request.getStartDate(),
                request.getEndDate(),
                request.getMaxParticipants(),
                image,
                request.getServerName(),
                request.getServerPassword(),
                request.getDiscordChannel()
        );

        if (rules != null) {
            rulesService.updateRules(id, rules);
        }

        Optional<Tournament> updated = tournamentService.findById(id);
        if (updated.isEmpty()) {
            return notFound("Tournament not found");
        }

        return Response.ok(TournamentDTO.fromTournament(uriInfo, updated.get())).build();
    }

    @GET
    @Path("/{id}/participants")
    @Produces(value = {Vendor.APPLICATION_PARTICIPANT_LIST})
    public Response getParticipants(@PathParam("id") long id,
                                    @QueryParam("teamSize") Integer teamSize,
                                    @QueryParam("userId") Long userId) {
        if (id <= 0) {
            return badRequest("Invalid tournament id");
        }

        if (userId != null && userId <= 0) {
            return badRequest("Invalid user id");
        }

        int resolvedTeamSize = resolveTeamSize(id, teamSize);
        List<Participant> participants = participantService.getTournamentParticipants(id, resolvedTeamSize);
        if (userId != null) {
            participants = filterParticipantsByUser(participants, userId);
        }

        List<ParticipantDTO> response = participants.stream()
                .map(ParticipantDTO.mapper(uriInfo))
                .toList();

        return Response.ok(new GenericEntity<>(response) {}).build();
    }

    @GET
    @Path("/{id}/participants/{participantId}")
    @Produces(value = {Vendor.APPLICATION_PARTICIPANT})
    public Response getParticipant(@PathParam("id") long id, @PathParam("participantId") long participantId) {
        if (id <= 0 || participantId <= 0) {
            return badRequest("Invalid tournament or participant id");
        }

        Participant participant = participantService.getTournamentParticipant(id, participantId);
        return Response.ok(ParticipantDTO.fromParticipant(uriInfo, participant)).build();
    }

    @POST
    @Path("/{id}/participants/users")
    public Response joinTournamentUser(@PathParam("id") long id) {
        if (id <= 0) {
            return badRequest("Invalid tournament id");
        }

        participantService.joinTournamentUser(currentUserProvider.getCurrentUserId(), id);
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @POST
    @Path("/{id}/participants/teams")
    @Consumes(value = {Vendor.APPLICATION_TOURNAMENT_JOIN_TEAM})
    public Response joinTournamentTeam(@PathParam("id") long id, @Valid JoinTournamentTeamRequest request) {
        if (id <= 0) {
            return badRequest("Invalid tournament id");
        }

        apiAuthorizationService.assertTeamOwner(request.getTeamId());

        participantService.joinTournamentTeam(id, request.getTeamId(), request.getMembers());
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @DELETE
    @Path("/{id}/participants/me")
    @Deprecated
    public Response leaveTournamentAsCurrentUser(@PathParam("id") long id) {
        if (id <= 0) {
            return badRequest("Invalid tournament id");
        }

        Participant participant = findCurrentUserParticipant(id);
        participantService.removeParticipant(id, participant.getId());
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @DELETE
    @Path("/{id}/participants/{participantId}")
    public Response removeParticipant(@PathParam("id") long id, @PathParam("participantId") long participantId) {
        if (id <= 0 || participantId <= 0) {
            return badRequest("Invalid tournament or participant id");
        }

        apiAuthorizationService.assertCanDeleteParticipant(id, participantId);

        participantService.removeParticipant(id, participantId);
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @DELETE
    @Path("/{id}/participants/users/{userId}")
    @Deprecated
    public Response leaveTournament(@PathParam("id") long id, @PathParam("userId") long userId) {
        if (id <= 0 || userId <= 0) {
            return badRequest("Invalid tournament or user id");
        }

        if (currentUserProvider.getCurrentUserId() != userId) {
            apiAuthorizationService.assertTournamentCreator(id);
        }

        participantService.leaveTournament(userId, id);
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @GET
    @Path("/{id}/matches")
    @Produces(value = {Vendor.APPLICATION_MATCH_STAGE_LIST})
    public Response getMatches(@PathParam("id") long id, @QueryParam("group") Integer group) {
        if (id <= 0) {
            return badRequest("Invalid tournament id");
        }

        LOGGER.debug("API correctly entered to /tournaments/{}/matches endpoint with group {}", id, group);
        Map<Integer, List<Match>> matchesByStage = matchService.getTournamentMatchesByStage(id, group);
        List<MatchStageDTO> response = matchesByStage.entrySet().stream()
                .sorted(Comparator.comparingInt(Map.Entry::getKey))
                .map(entry -> new MatchStageDTO(
                        entry.getKey(),
                        entry.getValue().stream().map(MatchDTO.mapper(uriInfo)).toList()
                ))
                .toList();

        return Response.ok(new GenericEntity<>(response) {}).build();
    }

    @GET
    @Path("/{id}/rules")
    @Produces(MediaType.WILDCARD)
    public Response getRules(@PathParam("id") long id, @Context Request request) {
        if (id <= 0) {
            return badRequest("Invalid tournament id");
        }

        Optional<Tournament> tournament = tournamentService.findById(id);
        if (tournament.isEmpty()) {
            return notFound("Tournament not found");
        }

        Rules tournamentRules = tournament.get().getRules();
        if (tournamentRules == null) {
            return notFound("Rules not found");
        }

        Optional<Rules> rules = rulesService.findById(tournamentRules.getId());
        if (rules.isEmpty() || rules.get().getFile() == null) {
            return notFound("Rules not found");
        }

        byte[] body = rules.get().getFile();
        return BinaryResponseSupport.conditionalOk(request, body, "application/pdf", "tournament-rules-" + id);
    }

    @GET
    @Path("/{id}/matches/{matchId}")
    @Produces(value = {Vendor.APPLICATION_MATCH})
    public Response getMatch(@PathParam("id") long id, @PathParam("matchId") long matchId) {
        if (id <= 0 || matchId <= 0) {
            return badRequest("Invalid tournament or match id");
        }

        Map<Integer, List<Match>> matchesByStage = matchService.getTournamentMatchesByStage(id, null);
        Optional<Match> match = matchesByStage.values().stream()
                .flatMap(List::stream)
                .filter(item -> item.getId() == matchId)
                .findFirst();

        if (match.isEmpty()) {
            return notFound("Match not found");
        }

        return Response.ok(MatchDTO.fromMatch(uriInfo, match.get())).build();
    }

    @PUT
    @Path("/{id}/matches/{matchId}/results")
    @Consumes(value = {Vendor.APPLICATION_MATCH_RESULTS})
    public Response setMatchResults(@PathParam("id") long id,
                                    @PathParam("matchId") long matchId,
                                    @Valid SetMatchResultsRequest request) {
        if (id <= 0 || matchId <= 0) {
            return badRequest("Invalid tournament or match id");
        }

        apiAuthorizationService.assertTournamentCreator(id);

        matchService.setMatchResults(matchId, id, request.getLocalScore(), request.getVisitorScore());
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @PUT
    @Path("/{id}/status")
    @Consumes(value = {Vendor.APPLICATION_TOURNAMENT_STATUS})
    @Produces(value = {Vendor.APPLICATION_TOURNAMENT})
    public Response updateTournamentStatus(@PathParam("id") long id, @Valid TournamentStatusRequest request) {
        if (id <= 0) {
            return badRequest("Invalid tournament id");
        }

        Optional<Tournament> tournament = tournamentService.findById(id);
        if (tournament.isEmpty()) {
            return notFound("Tournament not found");
        }

        apiAuthorizationService.assertTournamentCreator(id);

        if (request.getTournamentStarted() == null && request.getOpenInscriptions() == null) {
            return badRequest("status update must include tournamentStarted or openInscriptions");
        }

        if (Boolean.TRUE.equals(request.getTournamentStarted())) {
            tournamentService.startTournament(id);
        } else if (Boolean.FALSE.equals(request.getTournamentStarted())) {
            return badRequest("tournamentStarted can only transition to true");
        }

        if (Boolean.FALSE.equals(request.getOpenInscriptions())) {
            tournamentService.closeInscriptions(id);
        } else if (Boolean.TRUE.equals(request.getOpenInscriptions())) {
            return badRequest("openInscriptions can only transition to false");
        }

        Optional<Tournament> updated = tournamentService.findById(id);
        if (updated.isEmpty()) {
            return notFound("Tournament not found");
        }

        return Response.ok(TournamentDTO.fromTournament(uriInfo, updated.get())).build();
    }

    private int resolveTeamSize(long tournamentId, Integer requestedTeamSize) {
        if (requestedTeamSize != null && requestedTeamSize > 0) {
            return requestedTeamSize;
        }

        GameFormat format = tournamentService.getFormat(tournamentId);
        if (format == null) {
            return 1;
        }

        return format.getPlayersPerTeam();
    }

    private List<Participant> filterParticipantsByUser(List<Participant> participants, long userId) {
        userService.findById(userId).orElseThrow(UserNotFoundException::new);
        Set<Long> userTeamIds = teamService.getUserTeams(userId).stream()
                .map(Team::getId)
                .collect(Collectors.toSet());

        return participants.stream()
                .filter(participant -> (participant.getUser() != null && participant.getUser().getId() == userId)
                        || (participant.getTeam() != null && userTeamIds.contains(participant.getTeam().getId())))
                .toList();
    }

    private Participant findCurrentUserParticipant(long tournamentId) {
        int teamSize = resolveTeamSize(tournamentId, null);
        return participantService.getTournamentParticipants(tournamentId, teamSize).stream()
                .filter(apiAuthorizationService::ownsParticipant)
                .findFirst()
                .orElseThrow(ParticipantNotFoundException::new);
    }

    private Response badRequest(String detail) {
        return ApiErrorFactory.badRequest(detail);
    }

    private Response notFound(String detail) {
        return ApiErrorFactory.notFound(detail);
    }

    private byte[] decodeBase64(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Base64.getDecoder().decode(value.getBytes(StandardCharsets.UTF_8));
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    private boolean hasPayload(String value) {
        return value != null && !value.isBlank();
    }

    private void addPaginationLinks(Response.ResponseBuilder responseBuilder, int page, int totalPages) {
        if (totalPages <= 0) {
            return;
        }
        responseBuilder.link(buildPageUri(page), "self");
        responseBuilder.link(buildPageUri(0), "first");
        responseBuilder.link(buildPageUri(totalPages - 1), "last");
        if (page > 0) {
            responseBuilder.link(buildPageUri(page - 1), "prev");
        }
        if (page + 1 < totalPages) {
            responseBuilder.link(buildPageUri(page + 1), "next");
        }
    }

    private URI buildPageUri(int page) {
        return uriInfo.getRequestUriBuilder()
                .replaceQueryParam("page", page)
                .build();
    }

    private int adjustPage(int page, int totalPages) {
        if (totalPages <= 0) {
            return 0;
        }
        if (page < 0) {
            return 0;
        }
        if (page >= totalPages) {
            return totalPages - 1;
        }
        return page;
    }
}
