package ar.edu.itba.paw.model;

public class ParticipantUserInfo {

    private final Long user_id;
    private Integer points;
    private final String username;
    private final String email;

    public ParticipantUserInfo(final Long user_id, final String username, final String email, Integer points) {
        this.user_id = user_id;
        this.username = username;
        this.email = email;
        this.points = points;
    }

    public Long getUser_id() {
        return user_id;
    }

    public Integer getPoints() {
        return points;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

}
