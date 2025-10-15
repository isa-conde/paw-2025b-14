package ar.edu.itba.paw.model.Tournament;

import ar.edu.itba.paw.model.Game.Game;
import ar.edu.itba.paw.model.Game.GameFormat;
import ar.edu.itba.paw.model.Match;
import ar.edu.itba.paw.model.Participant;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.enums.Elo;
import ar.edu.itba.paw.model.enums.Region;
import ar.edu.itba.paw.model.enums.Structure;
import org.w3c.dom.css.CSSStyleRule;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tournament")
public class Tournament {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tournament_id_seq")
    @SequenceGenerator(sequenceName = "tournament_id_seq", name = "tournament_id_seq", allocationSize = 1)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    @Column(name = "game_id", insertable = false, updatable = false)
    private Long gameId;

    @Enumerated(EnumType.STRING)
    @Column(name = "region")
    private Region region;

    @Enumerated(EnumType.STRING)
    private Elo elo;

    private LocalDate start_date;

    private LocalDate end_date;

    private String format;

    @Enumerated(EnumType.STRING)
    private Structure structure;

    @Column(nullable = false)
    private Integer max_participants;

    private Long image_id;

    private Boolean open_inscriptions;

    private Boolean is_finished;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tournament_winner")
    private User winner;

    private Boolean is_group_stage;

    private Boolean tournament_started;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "format_id", nullable = false)
    private GameFormat formatEntity;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "tournament")
    private List<Participant> participants = new ArrayList<>();

    @OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Match> matches = new ArrayList<>();

    Tournament(){}

    public Tournament(User creator, String name, Game game, Region region, Elo elo, LocalDate start_date, LocalDate end_date, String format, Structure structure, Integer max_participants, Long image_id, Boolean openInscriptions, Boolean isFinished, GameFormat formatEntity){
        this.creator = creator;
        this.name = name;
        this.game = game;
        this.gameId = game.getId();
        this.region = region;
        this.elo = elo;
        this.start_date = start_date;
        this.end_date = end_date;
        this.format = format;
        this.structure = structure;
        this.max_participants = max_participants;
        this.image_id = image_id;
        this.open_inscriptions = openInscriptions;
        this.is_finished = isFinished;
        this.formatEntity = formatEntity;
    }

    public Tournament(Long id, Long creator_id, String name, Long game_id, Region region, Elo elo, LocalDate start_date, LocalDate end_date, String format, Structure structure, Integer max_participants, Long imageId, Boolean openInscriptions, Boolean isFinished, Long tournamentWinner, Boolean isGroupStage, Boolean tournamentStarted, Long format_id) {
        this.id = id;
        this.name = name;
        this.region = region;
        this.elo = elo;
        this.start_date = start_date;
        this.end_date = end_date;
        this.format = format;
        this.structure = structure;
        this.max_participants = max_participants;
        this.image_id = imageId;
        this.open_inscriptions = openInscriptions;
        this.is_finished = isFinished;
        this.is_group_stage = isGroupStage;
        this.tournament_started = tournamentStarted;
        this.gameId = game_id;
    }

    public Long getId() {
        return id;
    }

    public Long getCreator_id() {
        return creator.getId();
    }

    public String getName() {
        return name;
    }

    public Long getGame_id() {
        return gameId;
    }

    public Region getRegion() {
        return region;
    }

    public Elo getElo() {
        return elo;
    }

    public LocalDate getStart_date() {
        return start_date;
    }

    public LocalDate getEnd_date() {
        return end_date;
    }

    public String getFormat() {
        return format;
    }
    public void setFormat(String format) {
        this.format = format;
    }

    public Structure getStructure() {
        return structure;
    }

    public Integer getMax_participants() {
        return max_participants;
    }

    public Long getImage_id() {
        return image_id;
    }

    public Boolean getOpenInscriptions() {
        return open_inscriptions;
    }

    public Boolean getFinished() {
        return is_finished;
    }

    public Long getTournament_winner() {
        if (winner == null)
            return null;
        return winner.getId();
    }

    public Boolean getIs_group_stage() {
        return is_group_stage;
    }

    public Boolean getTournamentStarted() {
        return tournament_started;
    }

    public Long getFormat_id(){
        if (formatEntity == null)
            return null;
        return formatEntity.getId();
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public Game getGame() {
        return game;
    }

    public Boolean getIs_finished() {
        return is_finished;
    }

    public Boolean getOpen_inscriptions() {
        return open_inscriptions;
    }

    public Boolean getTournament_started() {
        return tournament_started;
    }

    public User getCreator() {
        return creator;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public void setElo(Elo elo) {
        this.elo = elo;
    }

    public User getWinner() {
        return winner;
    }

    public void setWinner(User winner) {
        this.winner = winner;
    }

    public void setImage_id(Long image_id) {
        this.image_id = image_id;
    }

    public void setMax_participants(Integer max_participants) {
        this.max_participants = max_participants;
    }

    public void setStructure(Structure structure) {
        this.structure = structure;
    }

    public void setEnd_date(LocalDate end_date) {
        this.end_date = end_date;
    }

    public void setStart_date(LocalDate start_date) {
        this.start_date = start_date;
    }

    public void setFormatEntity(GameFormat formatEntity) {
        this.formatEntity = formatEntity;
    }

    public void setIs_finished(Boolean is_finished) {
        this.is_finished = is_finished;
    }

    public void setIs_group_stage(Boolean is_group_stage) {
        this.is_group_stage = is_group_stage;
    }

    public void setOpen_inscriptions(Boolean open_inscriptions) {
        this.open_inscriptions = open_inscriptions;
    }

    public void setTournament_started(Boolean tournament_started) {
        this.tournament_started = tournament_started;
    }

    public List<Participant> getParticipants() {
        return participants;
    }

    public void setParticipants(List<Participant> participants) {
        this.participants = participants;
    }

    public GameFormat getFormatEntity() {
        return formatEntity;
    }

    public List<Match> getMatches() {
        return matches;
    }

    public void setMatches(List<Match> matches) {
        this.matches = matches;
    }
}


