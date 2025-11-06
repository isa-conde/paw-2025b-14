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
    private LocalDate expiryDate;

    @Column(name = "used", nullable = false)
    private boolean used;

    public Token(){}

    public Token(User user, Long token, LocalDate expiryDate){
        this.user = user;
        this.token = token;
        this.expiryDate = expiryDate;
    }

    public Token(final Long id, final Long userId, final Long token, final LocalDate expiryDate) {
        this.id = id;
        this.token = token;
        this.expiryDate = expiryDate;
        this.used = false;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return user.getId();
    }

    public Long getToken() {
        return token;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
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

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
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
