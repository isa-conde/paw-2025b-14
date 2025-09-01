package ar.edu.itba.paw.model;

public class TournamentFormat {

    private final Long id;
    private final String name;
    private final Integer players_per_team;
    private final Long game_id;

    public TournamentFormat(Long id, String name, Integer playersPerTeam, Long game_id) {
        this.id = id;
        this.name = name;
        players_per_team = playersPerTeam;
        this.game_id = game_id;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getPlayers_per_team() {
        return players_per_team;
    }

    public Long getGame_id() {
        return game_id;
    }
}
