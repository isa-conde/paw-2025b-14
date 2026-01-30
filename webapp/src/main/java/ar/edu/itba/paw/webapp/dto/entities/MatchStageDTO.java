package ar.edu.itba.paw.webapp.dto.entities;

import java.util.List;

public class MatchStageDTO {

    private int stage;
    private List<MatchDTO> matches;

    public MatchStageDTO() {}

    public MatchStageDTO(int stage, List<MatchDTO> matches) {
        this.stage = stage;
        this.matches = matches;
    }

    public int getStage() {
        return stage;
    }

    public void setStage(int stage) {
        this.stage = stage;
    }

    public List<MatchDTO> getMatches() {
        return matches;
    }

    public void setMatches(List<MatchDTO> matches) {
        this.matches = matches;
    }
}