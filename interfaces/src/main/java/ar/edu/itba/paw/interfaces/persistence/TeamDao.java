package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.model.Team;

public interface TeamDao {

    Team create(String name, Long pfp_id, Long banner_id, Long owner_id);

}
