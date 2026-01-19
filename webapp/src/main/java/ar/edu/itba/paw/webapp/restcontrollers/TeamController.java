package ar.edu.itba.paw.webapp.restcontrollers;

import ar.edu.itba.paw.interfaces.exception.TeamNotFoundException;
import ar.edu.itba.paw.interfaces.services.TeamService;
import ar.edu.itba.paw.model.Team;
import ar.edu.itba.paw.webapp.dto.entities.ErrorDTO;
import ar.edu.itba.paw.webapp.dto.entities.TeamDTO;
import ar.edu.itba.paw.webapp.dto.entities.UserDTO;
import ar.edu.itba.paw.webapp.dto.params.ForTournamentParams;
import ar.edu.itba.paw.webapp.dto.params.PaginationParams;
import ar.edu.itba.paw.webapp.dto.params.SearchNameParams;
import ar.edu.itba.paw.webapp.dto.params.UserIdParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerMapping;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.Optional;

@Path("teams")
@Component
public class TeamController {

    @Autowired
    TeamService ts;

    @Context
    private UriInfo uriInfo;
    @Qualifier("resourceHandlerMapping")
    @Autowired
    private HandlerMapping resourceHandlerMapping;

    @GET
    @Path("/{id}")
    @Consumes(value = {MediaType.APPLICATION_JSON})
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
    @Consumes(value = {MediaType.APPLICATION_JSON})
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
            result = ts.searchByName(
                    searchNameParams.getName(),
                    paginationParams.getPage()
            ).stream().map(TeamDTO.mapper(uriInfo)).toList();

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

    //TODO set up auth to get user from request, then use as owner id
    @POST
    @Consumes(value = {MediaType.APPLICATION_JSON})
    @Produces(value = {Vendor.APPLICATION_TEAM})
    public Response createTeam(@Valid TeamDTO teamDTO){

        return Response.ok().build();
    }

    @GET
    @Path("/{id}/members")
    @Consumes(value = {MediaType.APPLICATION_JSON})
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
        ts.updateTeam(teamId, dto.getName());
        return Response.ok(dto).build();
    }

    private Response badRequest(String detail) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(ErrorDTO.of(Response.Status.BAD_REQUEST, detail))
                .build();
    }

}
