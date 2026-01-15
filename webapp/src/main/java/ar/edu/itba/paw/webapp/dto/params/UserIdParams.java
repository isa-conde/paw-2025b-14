package ar.edu.itba.paw.webapp.dto.params;

import javax.ws.rs.QueryParam;

public class UserIdParams {

    @QueryParam("userId")
    private Long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Boolean isEmpty(){
        return id == null;
    }
}
