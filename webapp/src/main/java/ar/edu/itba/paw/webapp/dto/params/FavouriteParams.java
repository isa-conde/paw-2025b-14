package ar.edu.itba.paw.webapp.dto.params;

import javax.ws.rs.QueryParam;

public class FavouriteParams {

    @QueryParam("favouritedBy")
    private Long userId;

    public Long getUserId() {
        return userId;
    }

    public boolean isEmpty() {
        return userId == null;
    }
}
