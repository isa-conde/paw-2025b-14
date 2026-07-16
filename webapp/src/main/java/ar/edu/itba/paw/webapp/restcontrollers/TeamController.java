package ar.edu.itba.paw.webapp.restcontrollers;

import ar.edu.itba.paw.interfaces.exception.TeamNotFoundException;
import ar.edu.itba.paw.interfaces.services.TeamService;
import ar.edu.itba.paw.model.Team;
import ar.edu.itba.paw.webapp.auth.CurrentUserProvider;
import ar.edu.itba.paw.webapp.dto.entities.ErrorDTO;
import ar.edu.itba.paw.webapp.dto.entities.TeamDTO;
import ar.edu.itba.paw.webapp.dto.entities.UserDTO;
import ar.edu.itba.paw.webapp.dto.params.ForTournamentParams;
import ar.edu.itba.paw.webapp.dto.params.PaginationParams;
import ar.edu.itba.paw.webapp.dto.params.SearchNameParams;
import ar.edu.itba.paw.webapp.dto.params.UserIdParams;
import ar.edu.itba.paw.webapp.dto.requests.CreateTeamRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Path("teams")
@Component("teamRestController")
public class TeamController {

    @Autowired
    TeamService ts;

    @Autowired
    private CurrentUserProvider currentUserProvider;

    @Context
    private UriInfo uriInfo;

    @GET
    @Path("/{id}")
    @Produces(value = {Vendor.APPLICATION_TEAM})
    public Response getTeam(@PathParam("id") long id){
        if (id <= 0) {
            return badRequest("invalid id");
        }

        Optional<Team> team = ts.findById(id);

        if (team.isEmpty()){
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        TeamDTO teamDTO = TeamDTO.fromTeam(uriInfo, team.get());
        return Response.ok(teamDTO).build();
    }

    @GET
    @Produces(value = {Vendor.APPLICATION_TEAM_LIST})
    public Response searchTeams(@BeanParam SearchNameParams searchNameParams,
                                @BeanParam PaginationParams paginationParams,
                                @BeanParam UserIdParams userIdParams,
                                @BeanParam ForTournamentParams forTournamentParams){

        boolean byName = !searchNameParams.isEmpty();
        boolean byUser = !userIdParams.isEmpty();
        boolean paged = paginationParams.isPaged();
        boolean forTournament = !forTournamentParams.isEmpty();

        if (!byName && !byUser) {
            return badRequest("Either searchName or userId must be provided.");
        }

        if (byName && byUser) {
            return badRequest("searchName and userId cannot be used together.");
        }

        if (byName && forTournament) {
            return badRequest("forTournament cannot be used with searchName.");
        }

        List<TeamDTO> result;

        if (byName) {
            int totalPages = ts.countSearchByNameTeam(searchNameParams.getName());
            int page = adjustPage(paginationParams.getPage(), totalPages);
            result = ts.searchByName(
                    searchNameParams.getName(),
                    page
            ).stream().map(TeamDTO.mapper(uriInfo)).toList();
            Response.ResponseBuilder responseBuilder = Response.ok(result);
            addPaginationLinks(responseBuilder, page, totalPages);
            return responseBuilder.build();

        } else if (forTournament) {
            result = ts.getUserTeamsBySizeNotInTournament(
                    userIdParams.getId(),
                    forTournamentParams.getTournamentId()
            ).stream().map(TeamDTO.mapper(uriInfo)).toList();

        } else {
            result = ts.getUserTeams(userIdParams.getId())
                    .stream().map(TeamDTO.mapper(uriInfo)).toList();
        }

        return Response.ok(result).build();
    }

    @POST
    @Consumes(value = {Vendor.APPLICATION_TEAM_CREATE})
    @Produces(value = {Vendor.APPLICATION_TEAM})
    public Response createTeam(@Valid CreateTeamRequest request){
        byte[] profilePicture = decodeBase64(request.getProfilePictureBase64());
        byte[] banner = decodeBase64(request.getBannerBase64());

        if (profilePicture == null && hasPayload(request.getProfilePictureBase64())) {
            return badRequest("Invalid profilePictureBase64 payload");
        }
        if (banner == null && hasPayload(request.getBannerBase64())) {
            return badRequest("Invalid bannerBase64 payload");
        }

        List<String> members = request.getMembers() == null ? List.of() : request.getMembers();
        Team team = ts.create(request.getName(), profilePicture, banner, currentUserProvider.getCurrentUserId(), members);

        URI location = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(team.getId()))
                .build();

        return Response.created(location).entity(TeamDTO.fromTeam(uriInfo, team)).build();
    }

    @GET
    @Path("/{id}/members")
    @Produces(value = {Vendor.APPLICATION_USER_LIST})
    public Response getTeamMembers(@PathParam("id") long id){
        if (id <= 0){
            return badRequest("invalid id");
        }

        List<UserDTO> toReturn;
        try {
            toReturn = ts.getTeamMembers(id).stream().map(UserDTO.mapper(uriInfo)).toList();
        } catch (TeamNotFoundException t){
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.ok(toReturn).build();
    }

    @PUT
    @Path("/{teamId}")
    @Consumes(Vendor.APPLICATION_TEAM)
    @Produces(Vendor.APPLICATION_TEAM)
    public Response updateTeam(@PathParam("teamId") long teamId, TeamDTO dto) {
        if (teamId <= 0) {
            return badRequest("Invalid team id");
        }

        if (teamId != dto.getId()) {
            return badRequest("Team id in body does not match URI");
        }

        //TODO OBTENER EL USUARIO DE LA REQUEST Y VERIFICAR SI TIENE PERMISOS PARA REALIZAR EL UPDATE

        //TODO VERIFICAR
        ts.updateTeam(teamId, dto.getName(), null, null, null);
        return Response.ok(dto).build();
    }

    private Response badRequest(String detail) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(ErrorDTO.of(Response.Status.BAD_REQUEST, detail))
                .build();
    }

    private int adjustPage(int page, int totalPages) {
        if (totalPages <= 0) return 0;
        if (page < 0) return 0;
        if (page >= totalPages) return totalPages - 1;
        return page;
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
}
