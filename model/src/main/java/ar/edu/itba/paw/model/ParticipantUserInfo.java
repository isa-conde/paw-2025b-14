package ar.edu.itba.paw.model;

public class ParticipantUserInfo {

    private final Long user_id;
    private Integer points;
    private final String username;
    private final String email;
    private final Integer group_number;

    public ParticipantUserInfo(final Long user_id, final String username, final String email, Integer points, Integer group_number) {
        this.user_id = user_id;
        this.username = username;
        this.email = email;
        this.points = points;
        this.group_number = group_number;
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

    public Integer getGroupNumber() {
        return group_number;
    }

}
