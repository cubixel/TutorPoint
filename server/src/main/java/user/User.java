package user;

public class User {
    private String username = null;
    private String hashedpw = null;
    private boolean isTutor = false;

    public User(String username){
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getHashedpw() {
        return hashedpw;
    }

    public void setHashedpw(String hashedpw) {
        this.hashedpw = hashedpw;
    }

    public boolean isTutor() {
        return isTutor;
    }

    public void setTutor(boolean isTutor) {
        this.isTutor = isTutor;
    }
}
