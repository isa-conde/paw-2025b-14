package ar.edu.itba.paw.webapp.form;

import javax.validation.constraints.NotNull;

public class RemoveParticipantForm {

    @NotNull
    private Long participantId;

    @NotNull
    private Long tournamentId;

    public Long getParticipantId() {
        return participantId;
    }

    public void setParticipantId(Long participantId) {
        this.participantId = participantId;
    }

    public Long getTournamentId() {
        return tournamentId;
    }

    public void setTournamentId(Long tournamentId) {
        this.tournamentId = tournamentId;
    }
}
