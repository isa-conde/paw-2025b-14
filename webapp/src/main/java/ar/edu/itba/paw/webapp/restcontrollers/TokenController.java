package ar.edu.itba.paw.webapp.restcontrollers;

import ar.edu.itba.paw.interfaces.exception.TokenNotFoundException;
import ar.edu.itba.paw.interfaces.persistence.TokenDao;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.model.Token;
import ar.edu.itba.paw.webapp.dto.entities.TokenDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;

@Path("tokens")
@Component
public class TokenController {

    @Autowired
    private UserService us;

    @Context
    private UriInfo uriInfo;

    @GET
    @Path("/{value}")
    @Produces(value = {Vendor.APPLICATION_TOKEN})
    public Response getToken(@PathParam("value") long value) {
        Token token = us.findToken(value).orElseThrow(TokenNotFoundException::new);

        return Response.ok(TokenDTO.fromToken(uriInfo, token)).build();
    }

    @GET
    @Produces(value = {Vendor.APPLICATION_TOKEN_LIST})
    public Response getAssignedTokenList(@QueryParam("userId") long userId) {
        List<TokenDTO> assignedTokens = us.findAssignedTokens(userId).stream().map(TokenDTO.mapper(uriInfo)).toList();

        return Response.ok(new GenericEntity<>(assignedTokens) {}).build();
    }
}
