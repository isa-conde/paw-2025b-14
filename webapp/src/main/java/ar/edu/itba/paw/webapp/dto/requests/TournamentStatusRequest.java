package ar.edu.itba.paw.webapp.dto.requests;

public class TournamentStatusRequest {

    private Boolean tournamentStarted;
    private Boolean openInscriptions;

    public Boolean getTournamentStarted() {
        return tournamentStarted;
    }

    public void setTournamentStarted(Boolean tournamentStarted) {
        this.tournamentStarted = tournamentStarted;
    }

    public Boolean getOpenInscriptions() {
        return openInscriptions;
    }

    public void setOpenInscriptions(Boolean openInscriptions) {
        this.openInscriptions = openInscriptions;
    }
}