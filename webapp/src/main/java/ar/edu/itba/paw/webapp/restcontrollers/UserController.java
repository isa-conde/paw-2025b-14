
package ar.edu.itba.paw.webapp.restcontrollers;

import ar.edu.itba.paw.interfaces.exception.UserNotFoundException;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.model.Token;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.webapp.auth.JwtTokenService;
import ar.edu.itba.paw.webapp.dto.entities.UserDTO;
import ar.edu.itba.paw.webapp.dto.params.ListUsersByNameParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.List;

// TODO: general. We have to check if the logic behind profile that is in the old controller makes any sense here, taking into consideration that there's a hyperlink to user tournaments, favorite games, etc etc etc

@Path("users")
@Component
public class UserController {

    @Autowired
    private UserService us;

    @Context
    private UriInfo uriInfo;
    @Autowired
    private JwtTokenService jwtTokenService;

    //TODO AGREGAR HEADERS DE PAGINACION
    @GET
    @Produces(value = {Vendor.APPLICATION_USER_LIST})
    public Response listUsersByName(@Valid @BeanParam ListUsersByNameParams params) {
        String name = params.getName();
        int totalPages = us.countSearchByNameUser(name);
        params.setPage(adjustPage(params.getPage(), totalPages));

        List<UserDTO> users = us.searchByName(name, params.getPage()).stream().map(UserDTO.mapper(uriInfo)).toList();

        return Response.ok(new GenericEntity<>(users) {}).build();
    }

    @GET
    @Path("/{id}")
    @Produces(value = {Vendor.APPLICATION_USER})
    public Response getUser(@PathParam("id") long id) {
        User user = us.findById(id).orElseThrow(UserNotFoundException::new);

        return Response.ok(UserDTO.fromUser(uriInfo, user)).build();
    }

    @POST
    @Consumes(value = {MediaType.APPLICATION_JSON})
    @Produces(value = {Vendor.APPLICATION_USER})
    public Response createUser(@Valid UserDTO userDto) {
        User user = us.create(userDto.getUsername(), userDto.getEmail(), userDto.getPassword());

        URI uri = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(user.getId())).build();

        return Response.created(uri).build();
    }

    @POST
    @Path("/verifications")
    @Consumes(value = {MediaType.APPLICATION_JSON})
    @Produces(value = {Vendor.APPLICATION_TOKEN})
    public Response resendVerificationEmail(@Valid UserDTO userDto) {
        User user = us.findByEmail(userDto.getEmail()).orElseThrow(UserNotFoundException::new);

        Token token = us.resendVerification(user);

        URI uri = uriInfo.getAbsolutePathBuilder()
                // TODO: check if this happens in the context of /users or /users/verifications. Is it a GET on that resource? If so, make it
                .path(String.valueOf(token.getId()))
                .build();

        // TODO: need to see how this ends up working. How does it know to return the structure of the DTO and not the model? Does/Should it return a JSON?
        return Response.created(uri).build();
    }

    @PUT
    @Path("/verifications")
    @Produces(value = {Vendor.APPLICATION_USER})
    public Response verifyUser(@QueryParam("token") long token) {
        User user = us.findUserByToken(token);

        us.verifyEmail(token, user.getId());

        return Response.ok(UserDTO.fromUser(uriInfo, user))
                .header(HttpHeaders.AUTHORIZATION, jwtTokenService.createJwsToken(user))
                .build();
    }

    // TODO: do the same for this function as mentioned in resendVerificationEmail
    @POST
    @Path("/password-requests")
    @Consumes(value = {MediaType.APPLICATION_JSON})
    @Produces(value = {Vendor.APPLICATION_TOKEN})
    public Response requestPasswordReset(@Valid UserDTO userDto) {
        String userEmail = userDto.getEmail();

        Token token = us.requestPasswordReset(userEmail);

        URI uri = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(token.getId()))
                .build();

        return Response.created(uri).build();
    }

    @PUT
    @Path("/password-requests")
    @Consumes(value = {MediaType.APPLICATION_JSON})
    @Produces(value = {Vendor.APPLICATION_USER})
    public Response resetPassword(@Valid UserDTO userDto, @QueryParam("token") long token) {
        us.resetPassword(token, userDto.getPassword()); // TODO: check if this works by only populating the 'password' field of UserDTO

        User user = us.findUserByToken(token);

        return Response.ok(UserDTO.fromUser(uriInfo, user))
                .header(HttpHeaders.AUTHORIZATION, jwtTokenService.createJwsToken(user))
                .build();
    }



    private int adjustPage(int page, int totalPages) {
        if (totalPages <= 0) return 0;
        if (page < 0) return 0;
        if (page >= totalPages) return totalPages - 1;
        return page;
    }

}


