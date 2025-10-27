package ar.edu.itba.paw.model.Game;

import javax.persistence.*;

@Entity
@Table(name = "game_format")
public class GameFormat {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "game_format_id_seq")
    @SequenceGenerator(sequenceName = "game_format_id_seq", name = "game_format_id_seq", allocationSize = 1)
    @Column(name = "id")
    private Long id;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "players_per_team", nullable = false)
    private Integer players_per_team;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    public GameFormat(){}

    public GameFormat(Long id, String name, Integer playersPerTeam) {
        this.id = id;
        this.name = name;
        players_per_team = playersPerTeam;
    }

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
        return game.getId();
    }

    public Game getGame() {
        return game;
    }
    public void setGame(Game game) {
        this.game = game;
    }
}
