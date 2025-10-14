package ar.edu.itba.paw.model;

import javax.persistence.*;

@Entity
@Table(name = "image")
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "image_id_seq")
    @SequenceGenerator(sequenceName = "image_id_seq", name = "image_id_seq", allocationSize = 1)
    private Long id;
    @Column(name = "image", nullable = false)
    private byte[] image;

    Image(){}

    public Image(byte[] image){
        this.image = image;
    }

    public Long getId() {
        return id;
    }

    public byte[] getImage() {
        return image;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}
