package ar.edu.itba.paw.model;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "tokens")
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tokens_id_seq")
    @SequenceGenerator(sequenceName = "tokens_id_seq", name = "tokens_id_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER,optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "token", nullable = false)
    private Long token;

    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiry_date;

    @Column(name = "used", nullable = false)
    private boolean used;

    Token(){}

    public Token(User user, Long token, LocalDate expiry_date){
        this.user = user;
        this.token = token;
        this.expiry_date = expiry_date;
    }

    public Token(final Long id, final Long user_id, final Long token, final LocalDate expiry_date) {
        this.id = id;
        this.token = token;
        this.expiry_date = expiry_date;
        this.used = false;
    }

    public Long getId() {
        return id;
    }

    public Long getUser_id() {
        return user.getId();
    }

    public Long getToken() {
        return token;
    }

    public LocalDate getExpiry_date() {
        return expiry_date;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setExpiry_date(LocalDate expiry_date) {
        this.expiry_date = expiry_date;
    }

    public void setToken(Long token) {
        this.token = token;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
