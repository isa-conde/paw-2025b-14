package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.RulesService;
import ar.edu.itba.paw.model.Rules;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
public class RulesController {

    RulesService rs;

    RulesController(RulesService rs){
        this.rs = rs;
    }

    @RequestMapping("/rules/{id}")
    public ResponseEntity<byte[]> getRules(@PathVariable Long id){
        Optional<Rules> rulesOpt = rs.findById(id);

        if (rulesOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "rules.pdf");

        return new ResponseEntity<>(rulesOpt.get().getFile(), headers, HttpStatus.OK);
    }

}
