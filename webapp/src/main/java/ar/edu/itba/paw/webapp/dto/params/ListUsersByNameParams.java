package ar.edu.itba.paw.webapp.dto.params;

import javax.validation.constraints.NotBlank;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;

public class ListUsersByNameParams {

    @QueryParam("page")
    @DefaultValue("0")
    private int page;

    @QueryParam("name")
    @NotBlank
    private String name;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
