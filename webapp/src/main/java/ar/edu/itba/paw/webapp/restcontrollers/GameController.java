package ar.edu.itba.paw.webapp.restcontrollers;

import ar.edu.itba.paw.interfaces.services.GameService;
import ar.edu.itba.paw.model.Game.Game;
import ar.edu.itba.paw.model.Game.GameFormat;
import ar.edu.itba.paw.webapp.dto.entities.ErrorDTO;
import ar.edu.itba.paw.webapp.dto.entities.GameDTO;
import ar.edu.itba.paw.webapp.dto.entities.GameFormatDTO;
import ar.edu.itba.paw.webapp.dto.params.FavouriteParams;
import ar.edu.itba.paw.webapp.dto.params.PaginationParams;
import ar.edu.itba.paw.webapp.dto.params.SearchNameParams;
import org.eclipse.persistence.internal.codegen.NonreflectiveMethodDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.List;

@Path(("games"))
@Component
public class GameController {

    @Autowired
    private GameService gs;

    @Context
    private UriInfo uriInfo;

    @GET
    @Produces(value = {Vendor.APPLICATION_GAME_LIST})
    public Response getGames(@BeanParam PaginationParams paginationParams, @BeanParam SearchNameParams searchNameParams, @BeanParam FavouriteParams favouriteParams) {
        List<GameDTO> games;
        int totalPages = 0;
        if (!searchNameParams.isEmpty() && !favouriteParams.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity(
                    ErrorDTO.of(Response.Status.BAD_REQUEST,
                            "Query parameters 'name' and 'favouritedBy' cannot be used together")).build();
        }

        if (!searchNameParams.isEmpty()) {
            totalPages = gs.countSearchByNameGame(searchNameParams.getName());
            int page = adjustPage(totalPages, paginationParams.getPage());
            games = gs.searchByName(searchNameParams.getName(), page).stream().map(GameDTO.mapper(uriInfo)).toList();
        } else if (!favouriteParams.isEmpty()) {
            games = gs.getFavourites(favouriteParams.getUserId()).stream().map(GameDTO.mapper(uriInfo)).toList();
        } else if (paginationParams.isPaged()) {
            totalPages = gs.getPageAmount();
            int page = adjustPage(paginationParams.getPage(), totalPages);
            games = gs.findAllPaged(page).stream().map(GameDTO.mapper(uriInfo)).toList();
        } else {
            games = gs.findAll().stream().map(GameDTO.mapper(uriInfo)).toList();
        }
        Response.ResponseBuilder responseBuilder = Response.ok(games);
        if (paginationParams.isPaged() && totalPages > 0) {
            addPaginationLinks(responseBuilder, paginationParams.getPage(), totalPages);
        }
        return responseBuilder.build();
    }

    @GET
    @Path("/{gameId}/formats")
    @Produces(value = {Vendor.APPLICATION_GAME_FORMAT_LIST})
    public Response getFormats(@PathParam("gameId") long gameId) {
        if (gameId <= 0) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        List<GameFormatDTO> gameFormats = gs.getFormats(gameId).stream().map(GameFormatDTO.mapper(uriInfo)).toList();
        return Response.ok(gameFormats).build();
    }

    @GET
    @Path("/{gameId}/formats/{formatId}")
    @Produces(value = {Vendor.APPLICATION_GAME_FORMAT})
    public Response getFormat(@PathParam("gameId") long gameId, @PathParam("formatId") long formatId){
        if (gameId <= 0 || formatId <= 0) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        GameFormatDTO gf = GameFormatDTO.mapper(uriInfo).apply(gs.getFormat(formatId));
        return Response.ok(gf).build();
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
}