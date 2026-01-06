package ar.edu.itba.paw.webapp.restcontrollers;

import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.webapp.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Path("users")
@Component
public class UserController {

    @Autowired
    private UserService us;

    @Context
    private UriInfo uriInfo;

    @POST
    @Consumes(value = {MediaType.APPLICATION_JSON})
    @Produces(value = {"application/vnd.user.v1+json"})
    public Response createUser(final UserDTO userDto) {

    }

}
