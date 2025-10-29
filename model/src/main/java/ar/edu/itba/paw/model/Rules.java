package ar.edu.itba.paw.model;


import javax.persistence.*;

@Entity
@Table(name = "rules")
public class Rules {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "rules_id_seq")
    @SequenceGenerator(sequenceName = "rules_id_seq", name = "rules_id_seq", allocationSize = 1)
    @Column(name = "id")
    private Long id;

    @Column(name = "file", nullable = false)
    private byte[] file;

    public Rules(){}

    public Rules(byte[] file){
        this.file = file;
    }

    public Rules(Long id){
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }
}
