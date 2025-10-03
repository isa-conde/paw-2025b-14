package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.model.Team;

import java.util.List;

public interface TeamService {

    Team create(String name, byte[] pfp, byte[] banner, Long owner_id, List<String> members);

}
