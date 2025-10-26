package ar.edu.itba.paw.model.ids;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class MatchId implements Serializable {

    private Long id;
    private Long tournamentId;

    public MatchId(){}

    public MatchId(Long id, Long tournamentId) {
        this.id = id;
        this.tournamentId = tournamentId;
    }

    public Long getId() {
        return id;
    }

    public Long getTournamentId() {
        return tournamentId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof MatchId matchId)) return false;
        return Objects.equals(id, matchId.id) && Objects.equals(tournamentId, matchId.tournamentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, tournamentId);
    }
}
