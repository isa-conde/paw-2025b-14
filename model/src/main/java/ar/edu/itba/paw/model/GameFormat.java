package ar.edu.itba.paw.model;

public class GameFormat {

    private Long id;
    private String name;
    private Integer players_per_team;
    private Long game_id;

    public GameFormat(Long id, String name, Integer playersPerTeam, Long gameId) {
        this.id = id;
        this.name = name;
        players_per_team = playersPerTeam;
        game_id = gameId;
    }

    public GameFormat (){}

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public Integer getPlayers_per_team() {
        return players_per_team;
    }
    public void setPlayers_per_team(Integer players_per_team) {
        this.players_per_team = players_per_team;
    }

    public Long getGame_id() {
        return game_id;
    }
    public void setGame_id(Long game_id) {
        this.game_id = game_id;
    }
}
