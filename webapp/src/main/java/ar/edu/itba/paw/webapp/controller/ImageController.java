package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.ImageService;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.ServletContext;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.IllegalFormatCodePointException;
import java.util.Optional;


@Controller
public class ImageController {

    private final ServletContext servletContext;
    ImageService is;

     public ImageController(ImageService is, ServletContext servletContext){
         this.is = is;
         this.servletContext = servletContext;
     }

    @RequestMapping("/image/{id}")
    public ResponseEntity<byte[]> getImage(@PathVariable Long id){
        Optional<byte[]> image = is.findById(id);

        if (image.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        return new ResponseEntity<>(image.get(), headers, HttpStatus.OK);
    }

    @RequestMapping("/pfp/{id}")
    public ResponseEntity<byte[]> getPfp(@PathVariable(required = false) Long id){
        if (id != null && id != 0){
            return getImage(id);
        }

        byte[] imageBytes;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        String realPath = servletContext.getRealPath("/images/empty_user.png");
        try (InputStream is = new FileInputStream(realPath)) {
            imageBytes = is.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK) ;

    }

    @RequestMapping("/banner/{id}")
    public ResponseEntity<byte[]> getBanner(@PathVariable(required = false) Long id){
        if (id != null && id != 0){
            return getImage(id);
        }

        byte[] imageBytes;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        String realPath = servletContext.getRealPath("/images/defaultBanner.jpg");
        try (InputStream is = new FileInputStream(realPath)) {
            imageBytes = is.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK) ;

    }

    @RequestMapping("/pfp")
    public ResponseEntity<byte[]> getDefaultPfp() {
        return getPfp(null);
    }

    @RequestMapping("/banner")
    public ResponseEntity<byte[]> getDefaultBanner() {
        return getBanner(null);
    }

}
