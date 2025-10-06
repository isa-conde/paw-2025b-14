package ar.edu.itba.paw.model;

public class User {

    private final long id;
    private final String username;
    private final String email;
    private String password;
    private boolean verified;
    private String bio;
    private Long pfp_id;
    private Long banner_id;

    public User(final long id, final String username, final String email, String password, boolean verified, String bio, Long pfp_id, Long banner_id) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.verified = verified;
        this.bio = bio;
        this.pfp_id = pfp_id;
        this.banner_id = banner_id;
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

    public String getPassword() {
        return password;
    }

    public String getBio(){ return bio; }

    public Long getPfp_id() {return pfp_id;}

    public Long getBanner_id() { return banner_id; }

    public boolean isVerified() {
        return verified;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public void setBio(String bio){
        this.bio = bio;
    }

    public void setPfp_id(Long id){
        this.pfp_id = id;
    }

    public void setBanner_id(Long id){
        this.banner_id=id;
    }
}
