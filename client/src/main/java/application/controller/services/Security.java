/*
 * Security.java
 * Version: 1.0.0
 * Company: CUBIXEL
 *
 * */

package application.controller.services;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * CLASS DESCRIPTION:
 *
 * @author Daniel Bishop
 *
 */
public class Security {

    public static String hashPassword(String password){
        String hash = DigestUtils.sha256Hex(password);
        return hash;
    }

}
