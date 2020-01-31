package application.security;
import org.junit.Test;

import static org.junit.Assert.*;

public class SecurityTest {
    private Login login = null;
    private Register reg = null;
    private String uname = "qwerty";
    private String pword = "asdfg";
    private int isTutor = 1;

    @Test
    public void createLogin(){
        login = new Login(uname, pword);
        assertNotNull(login);
    }

    @Test
    public void createRegistration(){
        reg = new Register(uname, pword, isTutor); //Add rest
        assertNotNull(reg);
    }
}
