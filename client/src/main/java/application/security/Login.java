package application.security;
import org.apache.commons.codec.digest.DigestUtils;

public class Login {
    private String username = null;
    private String hashed_pw = null;

    public Login(String uname, String password){
        username = uname;
        hashed_pw = hashPassword(password);
    }

    public String hashPassword(String password){
        //Use someone elses
        String hash = DigestUtils.sha256Hex(password);
        System.out.println(hash);
        return hash;
    }
}
