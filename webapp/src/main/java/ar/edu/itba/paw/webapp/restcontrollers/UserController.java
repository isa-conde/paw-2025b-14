
package ar.edu.itba.paw.webapp.restcontrollers;

import ar.edu.itba.paw.interfaces.exception.UserNotFoundException;
import ar.edu.itba.paw.interfaces.services.MailService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.model.Token;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.webapp.dto.entities.UserDTO;
import ar.edu.itba.paw.webapp.dto.params.VerifyEmailParams;
import ar.edu.itba.paw.webapp.dto.requests.CreateUserRequest;
import ar.edu.itba.paw.webapp.form.UserForm;
import ar.edu.itba.paw.webapp.dto.params.ListUsersByNameParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.List;
import java.util.Optional;

@Path("users")
@Component
public class UserController {

    @Autowired
    private UserService us;

    @Autowired
    private MailService ms;

    @Context
    private UriInfo uriInfo;

    @GET
    @Produces(value = {"application/vnd.userlist.v1+json"})
    public Response listUsersByName(@Valid @BeanParam ListUsersByNameParams params) {
        String name = params.getName();
        int totalPages = us.countSearchByNameUser(name);
        params.setPage(adjustPage(params.getPage(), totalPages));

        final List<UserDTO> users = us.searchByName(name, params.getPage()).stream().map(UserDTO.mapper(uriInfo)).toList();

        return Response.ok(new GenericEntity<>(users) {}).build();
    }

    @GET
    @Path("/{id}")
    @Produces(value = {"application/vnd.user.v1+json"})
    public Response getUser(@PathParam("id") final int id) {
        User user = us.findById(id).orElseThrow(UserNotFoundException::new);

        return Response.ok(UserDTO.fromUser(uriInfo, user)).build();
    }


    @POST
    @Consumes(value = {MediaType.APPLICATION_JSON})
    @Produces(value = {"application/vnd.user.v1+json"})
    public Response createUser(@Valid UserDTO userDto) {
        final User user = us.create(userDto.getUsername(), userDto.getEmail(), userDto.getPassword());

        final URI uri = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(user.getId())).build();

        return Response.created(uri).build();
    }

    @PUT
    @Path("/verifications")
    @Produces(value = {"application/vnd.user.v1+json"})
    public Response verifyUser(@Valid @BeanParam VerifyEmailParams params) {
        final User user = us.findById(params.getUserId()).orElseThrow(UserNotFoundException::new);

        us.verifyEmail(params.getToken(), params.getUserId());

        return Response.ok(UserDTO.fromUser(uriInfo, user)).build();
    }

    @POST
    @Path("/verifications")
    @Consumes(value = {MediaType.APPLICATION_JSON})
    @Produces(value = {"application/vnd.token.v1+json"})
    public Response sendVerificationEmail(@Valid UserDTO userDto) {
        final User user = us.findByEmail(userDto.getEmail()).orElseThrow(UserNotFoundException::new);

        final Token token = us.resendVerification(user);

        final URI uri = uriInfo.getAbsolutePathBuilder()
                // TODO: check if this happens in the context of /users or /users/verifications
                .path(String.valueOf(token.getId()))
                .build();

        return Response.created(uri).build();
    }

    private int adjustPage(int page, int totalPages) {
        if (totalPages <= 0) return 0;
        if (page < 0) return 0;
        if (page >= totalPages) return totalPages - 1;
        return page;
    }

}


