package ar.edu.itba.paw.model;

public class User {

    private final long id;
    private final String username;
    private final String email;

    public User(final long id, final String username, final String email) {
        this.id = id;
        this.username = username;
        this.email = email;
    }

    public long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

}
