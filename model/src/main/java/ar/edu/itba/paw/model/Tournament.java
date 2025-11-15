package ar.edu.itba.paw.model;

import ar.edu.itba.paw.model.Game.Game;
import ar.edu.itba.paw.model.Game.GameFormat;
import ar.edu.itba.paw.model.Match.Match;
import ar.edu.itba.paw.model.Participant;
import ar.edu.itba.paw.model.Rules;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.enums.Elo;
import ar.edu.itba.paw.model.enums.Region;
import ar.edu.itba.paw.model.enums.Structure;
import org.hibernate.annotations.ColumnTransformer;

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
    @Column(name = "region", columnDefinition = "region_enum")
    private Region region;

    @Enumerated(EnumType.STRING)
    @Column(name = "elo")
    private Elo elo;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "format")
    private String format;

    @Enumerated(EnumType.STRING)
    @Column(name = "structure", columnDefinition = "structure_enum")
    private Structure structure;

    @Column(name = "max_participants", nullable = false)
    private Integer maxParticipants;

    @Column(name = "image_id")
    private Long imageId;

    @Column(name = "open_inscriptions")
    private Boolean openInscriptions;

    @Column(name = "is_finished")
    private Boolean isFinished;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tournament_winner")
    private Participant winner;

    @Column(name = "is_group_stage")
    private Boolean isGroupStage;

    @Column(name = "tournament_started")
    private Boolean tournamentStarted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "format_id", nullable = false)
    private GameFormat formatEntity;

    @Column(name = "rating")
    private Float rating;

    @Column(name = "server_name")
    private String serverName;

    @Column(name = "server_password")
    private String serverPassword;

    @Column(name = "discord_channel")
    private String discordChannel;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "tournament")
    private List<Participant> participants = new ArrayList<>();

    @OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Match> matches = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "rules_id", referencedColumnName = "id")
    private Rules rules;

    public Tournament(User creator, String name, Game game, Region region, LocalDate startDate, LocalDate endDate, String format,
                      Structure structure, Integer maxParticipants, Long imageId, Boolean openInscriptions, Boolean isFinished,
                      GameFormat formatEntity, String serverName, String serverPassword, String discordChannel) {
        this.creator = creator;
        this.name = name;
        this.game = game;
        this.gameId = game.getId();
        this.region = region;
        this.startDate = startDate;
        this.endDate = endDate;
        this.format = format;
        this.structure = structure;
        this.maxParticipants = maxParticipants;
        this.imageId = imageId;
        this.openInscriptions = openInscriptions;
        this.isFinished = isFinished;
        this.formatEntity = formatEntity;
        this.serverName = serverName;
        this.serverPassword = serverPassword;
        this.discordChannel = discordChannel;
    }

    public Tournament() {}

    public Long getId() {
        return id;
    }

    public Long getCreatorId() {
        return creator.getId();
    }

    public String getName() {
        return name;
    }

    public Long getGameId() {
        return gameId;
    }

    public Region getRegion() {
        return region;
    }

    public Elo getElo() {
        return elo;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
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

    public Integer getMaxParticipants() {
        return maxParticipants;
    }

    public Long getImageId() {
        return imageId;
    }

    public Boolean getOpenInscriptions() {
        return openInscriptions;
    }

    public Boolean getFinished() {
        return isFinished;
    }

    public Long getTournamentWinner() {
        if (winner == null)
            return null;
        return winner.getId();
    }

    public Boolean getIsGroupStage() {
        return isGroupStage;
    }

    public Boolean getTournamentStarted() {
        return tournamentStarted;
    }

    public Long getFormatId(){
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

    public Boolean getIsFinished() {
        return isFinished;
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

    public Participant getWinner() {
        return winner;
    }

    public void setWinner(Participant winner) {
        this.winner = winner;
    }

    public void setImageId(Long imageId) {
        this.imageId = imageId;
    }

    public void setMaxParticipants(Integer maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public void setStructure(Structure structure) {
        this.structure = structure;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setFormatEntity(GameFormat formatEntity) {
        this.formatEntity = formatEntity;
    }

    public void setIsFinished(Boolean isFinished) {
        this.isFinished = isFinished;
    }

    public void setIsGroupStage(Boolean isGroupStage) {
        this.isGroupStage = isGroupStage;
    }

    public void setOpenInscriptions(Boolean openInscriptions) {
        this.openInscriptions = openInscriptions;
    }

    public void setTournamentStarted(Boolean tournamentStarted) {
        this.tournamentStarted = tournamentStarted;
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

    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }

    public List<Match> getMatches() {
        return matches;
    }

    public void setMatches(List<Match> matches) {
        this.matches = matches;
    }

    public Rules getRules() {
        return rules;
    }

    public void setRules(Rules rules) {
        this.rules = rules;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getServerPassword() {
        return serverPassword;
    }

    public void setServerPassword(String serverPassword) {
        this.serverPassword = serverPassword;
    }

    public String getDiscordChannel() {
        return discordChannel;
    }

    public void setDiscordChannel(String discordChannel) {
        this.discordChannel = discordChannel;
    }
}


