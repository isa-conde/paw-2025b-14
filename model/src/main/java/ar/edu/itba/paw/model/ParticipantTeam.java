package ar.edu.itba.paw.model;

public class ParticipantTeam {

    private final Long team_id;
    private final Long tournament_id;


    public ParticipantTeam(Long teamId, Long tournamentId) {
        team_id = teamId;
        tournament_id = tournamentId;
    }

    public Long getTeam_id() {
        return team_id;
    }

    public Long getTournament_id() {
        return tournament_id;
    }
}
