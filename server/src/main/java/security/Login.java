package security;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.Serializable;

public class Login implements Serializable {
    private String username = null;
    private String hashed_pw = null;
    protected boolean isLogin = true;

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
