package ar.edu.itba.paw.webapp.restcontrollers;

import ar.edu.itba.paw.interfaces.services.ImageService;
import ar.edu.itba.paw.webapp.exception.ApiErrorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Optional;

@Path("images")
@Component("imageRestController")
public class ImageController {

    private static final String IMAGE_PNG = "image/png";
    private static final String IMAGE_JPEG = "image/jpeg";
    private static final String IMAGE_GIF = "image/gif";
    private static final String IMAGE_WEBP = "image/webp";

    @Autowired
    private ImageService imageService;

    @GET
    @Path("/{id}")
    @Produces(MediaType.WILDCARD)
    public Response getImage(@PathParam("id") long id) {
        if (id <= 0) {
            return ApiErrorFactory.badRequest("Invalid image id");
        }

        Optional<byte[]> image = imageService.findById(id);
        if (image.isEmpty()) {
            return ApiErrorFactory.notFound("Image not found");
        }

        byte[] body = image.get();
        return Response.ok(body)
                .type(detectContentType(body))
                .build();
    }

    private String detectContentType(byte[] body) {
        if (body.length >= 8
                && (body[0] & 0xff) == 0x89
                && body[1] == 0x50
                && body[2] == 0x4e
                && body[3] == 0x47) {
            return IMAGE_PNG;
        }
        if (body.length >= 3
                && (body[0] & 0xff) == 0xff
                && (body[1] & 0xff) == 0xd8
                && (body[2] & 0xff) == 0xff) {
            return IMAGE_JPEG;
        }
        if (body.length >= 6
                && body[0] == 0x47
                && body[1] == 0x49
                && body[2] == 0x46
                && body[3] == 0x38
                && (body[4] == 0x37 || body[4] == 0x39)
                && body[5] == 0x61) {
            return IMAGE_GIF;
        }
        if (body.length >= 12
                && body[0] == 0x52
                && body[1] == 0x49
                && body[2] == 0x46
                && body[3] == 0x46
                && body[8] == 0x57
                && body[9] == 0x45
                && body[10] == 0x42
                && body[11] == 0x50) {
            return IMAGE_WEBP;
        }
        return MediaType.APPLICATION_OCTET_STREAM;
    }
}
