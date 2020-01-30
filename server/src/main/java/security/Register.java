package security;

public class Register extends Login {
    private int tutorStatus;

    public Register(String uname, String password, int tutorStatus) {
        super(uname, password);
        this.tutorStatus = tutorStatus;
        this.isLogin = false;
    }


}
