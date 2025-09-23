package ar.edu.itba.paw.model.Game;

public class GameImg {

    private Game game;
    private String base64Img;

    public GameImg(Game game, String base64Img){
        this.game = game;
        this.base64Img = base64Img;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game){
        this.game = game;
    }

    public String getBase64Img() {
        return base64Img;
    }

    public void setBase64Img(String base64Img){
        this.base64Img = base64Img;
    }
}
