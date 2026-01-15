package ar.edu.itba.paw.webapp.dto.params;

import javax.ws.rs.QueryParam;

public class ForTournamentParams {

    @QueryParam("forTournament")
    private Long tournamentId;

    public Long getTournamentId() {
        return tournamentId;
    }

    public void setTournamentId(Long tournamentId) {
        this.tournamentId = tournamentId;
    }

    public Boolean isEmpty(){
        return tournamentId == null;
    }
}
