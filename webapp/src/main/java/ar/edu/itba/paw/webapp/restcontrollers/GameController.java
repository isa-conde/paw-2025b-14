package ar.edu.itba.paw.webapp.restcontrollers;

import ar.edu.itba.paw.interfaces.services.GameService;
import ar.edu.itba.paw.model.Game.Game;
import ar.edu.itba.paw.model.Game.GameFormat;
import ar.edu.itba.paw.webapp.dto.entities.GameDTO;
import ar.edu.itba.paw.webapp.dto.entities.GameFormatDTO;
import ar.edu.itba.paw.webapp.dto.params.FavouriteParams;
import ar.edu.itba.paw.webapp.dto.params.PaginationParams;
import ar.edu.itba.paw.webapp.dto.params.SearchNameParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;

@Path(("games"))
@Component
public class GameController {

    @Autowired
    private GameService gs;

    @Context
    private UriInfo uriInfo;

    //TODO AGREGAR HEADERS DE PAGINACION
    @GET
    public Response getGames(@BeanParam PaginationParams paginationParams, @BeanParam SearchNameParams searchNameParams, @BeanParam FavouriteParams favouriteParams){
        List<GameDTO> games;
        if (!searchNameParams.isEmpty() && !favouriteParams.isEmpty()){
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        if (!searchNameParams.isEmpty()){
            int totalPages = gs.countSearchByNameGame(searchNameParams.getName());
            int page = adjustPage(totalPages, paginationParams.getPage());
            games = gs.searchByName(searchNameParams.getName(), page).stream().map(GameDTO.mapper(uriInfo)).toList();
        } else if (!favouriteParams.isEmpty()) {
            games = gs.getFavourites(favouriteParams.getUserId()).stream().map(GameDTO.mapper(uriInfo)).toList();
        } else if (paginationParams.isPaged()) {
            int totalPages = gs.getPageAmount();
            int page = adjustPage(paginationParams.getPage(), totalPages);
            games = gs.findAllPaged(page).stream().map(GameDTO.mapper(uriInfo)).toList();
        } else {
            games = gs.findAll().stream().map(GameDTO.mapper(uriInfo)).toList();
        }
        return Response.ok(games).build();
    }

    @GET
    @Path("/{gameId}/formats")
    public Response getFormats(@PathParam("gameId") long gameId){
        if (gameId <= 0) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        List<GameFormatDTO> gameFormats = gs.getFormats(gameId).stream().map(GameFormatDTO.mapper(uriInfo)).toList();
        return Response.ok(gameFormats).build();
    }

    @GET
    @Path("/{gameId}/formats/{formatId}")
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
}
