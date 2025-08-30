package ar.edu.itba.paw.model;

public class Image {
    private final Long id;
    private final byte[] img;


    public Image(Long id, byte[] img) {
        this.id = id;
        this.img = img;
    }

    public Long getId() {
        return id;
    }

    public byte[] getImg() {
        return img;
    }
}
