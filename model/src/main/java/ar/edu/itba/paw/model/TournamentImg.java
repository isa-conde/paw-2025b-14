package ar.edu.itba.paw.model;

public class TournamentImg {

    private Tournament tournament;
    private String base64Img;

    public TournamentImg(Tournament tournament, String base64Img) {
        this.tournament = tournament;
        this.base64Img = base64Img;
    }

    public String getBase64Img() {
        return base64Img;
    }
    public void setBase64Img(String base64Img) {
        this.base64Img = base64Img;
    }

    public void setTournament(Tournament tournament) {
        this.tournament = tournament;
    }
    public Tournament getTournament() {
        return tournament;
    }
}
