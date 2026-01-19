package ar.edu.itba.paw.webapp.dto.params;

import javax.validation.constraints.NotBlank;
import javax.ws.rs.QueryParam;

public class SearchNameParams {
    @QueryParam("name")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean isEmpty(){
        return name.isEmpty() || name.isBlank();
    }
}
