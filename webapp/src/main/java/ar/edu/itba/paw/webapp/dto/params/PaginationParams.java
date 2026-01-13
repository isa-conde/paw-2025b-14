package ar.edu.itba.paw.webapp.dto.params;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;

public class PaginationParams {

    @QueryParam("page")
    @DefaultValue("0")
    private Integer page;

    public boolean isPaged() {
        return page != null;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }
}
