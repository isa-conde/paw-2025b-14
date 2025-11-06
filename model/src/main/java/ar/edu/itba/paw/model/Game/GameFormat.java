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
    private Integer playersPerTeam;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    public GameFormat(){}

    public GameFormat(Long id, String name, Integer playersPerTeam) {
        this.id = id;
        this.name = name;
        this.playersPerTeam = playersPerTeam;
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

    public Integer getPlayersPerTeam() {
        return playersPerTeam;
    }
    public void setPlayersPerTeam(Integer playersPerTeam) {
        this.playersPerTeam = playersPerTeam;
    }

    public Long getGameId() {
        return game.getId();
    }

    public Game getGame() {
        return game;
    }
    public void setGame(Game game) {
        this.game = game;
    }
}
