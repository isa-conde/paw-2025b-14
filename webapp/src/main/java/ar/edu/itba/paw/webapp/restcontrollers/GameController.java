package ar.edu.itba.paw.webapp.restcontrollers;

import ar.edu.itba.paw.interfaces.exception.GameNotFoundException;
import ar.edu.itba.paw.interfaces.services.GameService;
import ar.edu.itba.paw.model.Game.Game;
import ar.edu.itba.paw.model.Game.GameFormat;
import ar.edu.itba.paw.webapp.dto.entities.GameDTO;
import ar.edu.itba.paw.webapp.dto.entities.GameFormatDTO;
import ar.edu.itba.paw.webapp.dto.params.FavouriteParams;
import ar.edu.itba.paw.webapp.dto.params.PaginationParams;
import ar.edu.itba.paw.webapp.dto.params.SearchNameParams;
import ar.edu.itba.paw.webapp.exception.ApiErrorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.List;

@Path(("games"))
@Component("gameRestController")
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
        int page = paginationParams.getPage();
        if (!searchNameParams.isEmpty() && !favouriteParams.isEmpty()) {
            return ApiErrorFactory.badRequest("Query parameters 'name' and 'favouritedBy' cannot be used together");
        }

        if (!searchNameParams.isEmpty()) {
            totalPages = gs.countSearchByNameGame(searchNameParams.getName());
            page = adjustPage(page, totalPages);
            games = gs.searchByName(searchNameParams.getName(), page).stream().map(GameDTO.mapper(uriInfo)).toList();
        } else if (!favouriteParams.isEmpty()) {
            games = gs.getFavourites(favouriteParams.getUserId()).stream().map(GameDTO.mapper(uriInfo)).toList();
        } else if (paginationParams.isPaged()) {
            totalPages = gs.getPageAmount();
            page = adjustPage(page, totalPages);
            games = gs.findAllPaged(page).stream().map(GameDTO.mapper(uriInfo)).toList();
        } else {
            games = gs.findAll().stream().map(GameDTO.mapper(uriInfo)).toList();
        }
        Response.ResponseBuilder responseBuilder = Response.ok(new GenericEntity<>(games) {});
        if (paginationParams.isPaged() && totalPages > 0) {
            addPaginationLinks(responseBuilder, page, totalPages);
        }
        return responseBuilder.build();
    }

    @GET
    @Path("/{id}/formats")
    @Produces(value = {Vendor.APPLICATION_GAME_FORMAT_LIST})
    public Response getFormats(@PathParam("id") long id) {
        if (id <= 0) {
            return ApiErrorFactory.badRequest("Invalid game id");
        }

        List<GameFormatDTO> gameFormats = gs.getFormats(id).stream().map(GameFormatDTO.mapper(uriInfo)).toList();
        return Response.ok(new GenericEntity<>(gameFormats) {}).build();
    }

    @GET
    @Path("/{gameId}/formats/{formatId}")
    @Produces(value = {Vendor.APPLICATION_GAME_FORMAT})
    public Response getFormat(@PathParam("gameId") long gameId, @PathParam("formatId") long formatId){
        if (gameId <= 0 || formatId <= 0) {
            return ApiErrorFactory.badRequest("Invalid game or format id");
        }

        GameFormatDTO gf = GameFormatDTO.mapper(uriInfo).apply(gs.getFormat(formatId));
        return Response.ok(gf).build();
    }

    @GET
    @Path("/{id}")
    @Produces(value = {Vendor.APPLICATION_GAME})
    public Response getGame(@PathParam("id") long id) {
        if(id <= 0) {
            return ApiErrorFactory.badRequest("Invalid game id");
        }

        GameDTO game = GameDTO.mapper(uriInfo).apply(gs.findById(id).orElseThrow(GameNotFoundException::new));
        return Response.ok(game).build();
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
