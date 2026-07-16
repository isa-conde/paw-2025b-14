
package ar.edu.itba.paw.webapp.restcontrollers;

import ar.edu.itba.paw.interfaces.exception.UserNotFoundException;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.model.Token;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.enums.Platform;
import ar.edu.itba.paw.webapp.auth.CurrentUserProvider;
import ar.edu.itba.paw.webapp.auth.JwtTokenService;
import ar.edu.itba.paw.webapp.dto.entities.CommentDTO;
import ar.edu.itba.paw.webapp.dto.entities.UserAccountDTO;
import ar.edu.itba.paw.webapp.dto.entities.UserDTO;
import ar.edu.itba.paw.webapp.dto.params.ListUsersByNameParams;
import ar.edu.itba.paw.webapp.dto.params.PaginationParams;
import ar.edu.itba.paw.webapp.dto.requests.AddUserAccountRequest;
import ar.edu.itba.paw.webapp.dto.requests.CreateUserRequest;
import ar.edu.itba.paw.webapp.exception.ApiErrorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.List;

// TODO: general. We have to check if the logic behind profile that is in the old controller makes any sense here, taking into consideration that there's a hyperlink to user tournaments, favorite games, etc etc etc

@Path("users")
@Component("userRestController")
public class UserController {

    @Autowired
    private UserService us;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Context
    private UriInfo uriInfo;

    @Autowired
    private JwtTokenService jwtTokenService;

    @Autowired
    private CurrentUserProvider currentUserProvider;

    @GET
    @Produces(value = {Vendor.APPLICATION_USER_LIST})
    public Response listUsersByName(@Valid @BeanParam ListUsersByNameParams params) {
        String name = params.getName();
        int totalPages = us.countSearchByNameUser(name);
        params.setPage(adjustPage(params.getPage(), totalPages));

        List<UserDTO> users = us.searchByName(name, params.getPage()).stream().map(UserDTO.mapper(uriInfo)).toList();

        Response.ResponseBuilder responseBuilder = Response.ok(new GenericEntity<>(users) {});
        addPaginationLinks(responseBuilder, params.getPage(), totalPages);
        return responseBuilder.build();
    }

    @GET
    @Path("/me")
    @Produces(value = {Vendor.APPLICATION_USER})
    public Response getCurrentUser() {
        return Response.ok(UserDTO.fromUser(uriInfo, currentUserProvider.getCurrentUser())).build();
    }

    @GET
    @Path("/{id}")
    @Produces(value = {Vendor.APPLICATION_USER})
    public Response getUser(@PathParam("id") long id) {
        User user = us.findById(id).orElseThrow(UserNotFoundException::new);

        return Response.ok(UserDTO.fromUser(uriInfo, user)).build();
    }

    @GET
    @Path("/{id}/comments")
    @Produces(value = {Vendor.APPLICATION_COMMENT_LIST})
    public Response getCommentsReceived(@PathParam("id") long id, @BeanParam PaginationParams paginationParams) {
        if (id <= 0) {
            return ApiErrorFactory.badRequest("Invalid user id");
        }

        int totalPages = us.getCommentPages(id);
        int page = adjustPage(paginationParams.getPage(), totalPages);
        List<CommentDTO> comments = us.getCommentsReceived(id, page).stream()
                .map(CommentDTO.mapper(uriInfo))
                .toList();

        Response.ResponseBuilder responseBuilder = Response.ok(new GenericEntity<>(comments) {});
        addPaginationLinks(responseBuilder, page, totalPages);
        return responseBuilder.build();
    }

    @GET
    @Path("/{id}/accounts")
    @Produces(value = {Vendor.APPLICATION_USER_ACCOUNT_LIST})
    public Response getUserAccounts(@PathParam("id") long id) {
        if (id <= 0) {
            return ApiErrorFactory.badRequest("Invalid user id");
        }

        List<UserAccountDTO> accounts = us.getUserAccounts(id).stream()
                .map(UserAccountDTO.mapper(uriInfo))
                .toList();

        return Response.ok(new GenericEntity<>(accounts) {}).build();
    }

    @GET
    @Path("/{id}/accounts/{platform}")
    @Produces(value = {Vendor.APPLICATION_USER_ACCOUNT})
    public Response getUserAccount(@PathParam("id") long id, @PathParam("platform") Platform platform) {
        if (id <= 0) {
            return ApiErrorFactory.badRequest("Invalid user id");
        }
        if (platform == null) {
            return ApiErrorFactory.badRequest("Invalid platform");
        }

        return us.getUserAccounts(id).stream()
                .filter(account -> account.getPlatform() == platform)
                .findFirst()
                .map(account -> Response.ok(UserAccountDTO.fromUserAccount(uriInfo, account)).build())
                .orElseGet(() -> ApiErrorFactory.notFound("User account not found"));
    }

    @POST
    @Path("/me/accounts")
    @Consumes(value = {Vendor.APPLICATION_USER_ACCOUNT_CREATE})
    @Produces(value = {Vendor.APPLICATION_USER_ACCOUNT_LIST})
    public Response addCurrentUserAccount(@Valid AddUserAccountRequest request) {
        us.addUserAccount(currentUserProvider.getCurrentUserId(), request.getPlatform(), request.getUsername());

        List<UserAccountDTO> accounts = us.getUserAccounts(currentUserProvider.getCurrentUserId()).stream()
                .map(UserAccountDTO.mapper(uriInfo))
                .toList();

        URI location = uriInfo.getBaseUriBuilder()
                .path("users")
                .path(String.valueOf(currentUserProvider.getCurrentUserId()))
                .path("accounts")
                .path(request.getPlatform().name())
                .build();

        return Response.created(location).entity(new GenericEntity<>(accounts) {}).build();
    }

    @DELETE
    @Path("/me/accounts/{platform}")
    public Response deleteCurrentUserAccount(@PathParam("platform") Platform platform) {
        if (platform == null) {
            return ApiErrorFactory.badRequest("Invalid platform");
        }

        us.deleteUserAccount(currentUserProvider.getCurrentUserId(), platform);
        return Response.noContent().build();
    }

    @POST
    @Consumes(value = {Vendor.APPLICATION_USER_CREATE})
    @Produces(value = {Vendor.APPLICATION_USER})
    public Response createUser(@Valid CreateUserRequest request) {
        User user = us.create(request.getUsername(), request.getEmail(), request.getPassword());

        URI uri = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(user.getId())).build();

        return Response.created(uri).build();
    }

    @POST
    @Path("/verifications")
    @Consumes(value = {Vendor.APPLICATION_USER_VERIFICATION})
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
    @Consumes(value = {Vendor.APPLICATION_PASSWORD_RESET_REQUEST})
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
    @Consumes(value = {Vendor.APPLICATION_PASSWORD_RESET})
    @Produces(value = {Vendor.APPLICATION_USER})
    public Response resetPassword(@Valid UserDTO userDto, @QueryParam("token") long token) {
        us.resetPassword(token, userDto.getPassword()); // TODO: check if this works by only populating the 'password' field of UserDTO

        User user = us.findUserByToken(token);

        return Response.ok(UserDTO.fromUser(uriInfo, user))
                .header(HttpHeaders.AUTHORIZATION, jwtTokenService.createJwsToken(user))
                .build();
    }

    @POST
    @Path("/sessions")
    @Consumes(value = {Vendor.APPLICATION_USER_LOGIN})
    @Produces(value = {Vendor.APPLICATION_USER})
    public Response login(@Valid UserDTO userDto) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDto.getUsername(), userDto.getPassword());
        authenticationManager.authenticate(authentication);
        User user = us.findByUsername(userDto.getUsername()).orElseThrow(UserNotFoundException::new);
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
