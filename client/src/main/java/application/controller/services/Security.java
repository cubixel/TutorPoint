package application.controller.services;

import org.apache.commons.codec.digest.DigestUtils;

public class Security {

    public static String hashPassword(String password){
        String hash = DigestUtils.sha256Hex(password);
        System.out.println(hash);
        return hash;
    }

}
