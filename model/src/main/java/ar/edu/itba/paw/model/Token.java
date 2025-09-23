package ar.edu.itba.paw.model;

import java.time.LocalDate;

public class Token {

    private final Long id;
    private final Long user_id;
    private final Long token;
    private final LocalDate expiry_date;
    private boolean used;


    public Token(final Long id, final Long user_id, final Long token, final LocalDate expiry_date) {
        this.id = id;
        this.user_id = user_id;
        this.token = token;
        this.expiry_date = expiry_date;
        this.used = false;
    }

    public Long getId() {
        return id;
    }

    public Long getUser_id() {
        return user_id;
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
}
