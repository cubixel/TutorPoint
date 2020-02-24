/*
 * Security.java
 * Version: 1.0.0
 * Company: CUBIXEL
 *
 * */

package application.controller.services;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * CLASS DESCRIPTION.
 *
 * @author CUBIXEL
 *
 */
public class Security {

  /**
   * METHOD DESCRIPTION.
   */
  public static String hashPassword(String password) {

    // TODO: Add salt to password before hashing.
    // *** NOTE ***
    // - Password should add salt to minimise collisions or remove identical
    //   hashes on commonly used passwords.
    // - Salt must be a unique fixed length string of random characters and
    //   stored along side the user's data on the server database.
    //      - Could use the user's ID if uID is randomly assigned.
    // - Salt is appended to the start of the password BEFORE the hashing algorithm.
    // Author: OS [21:00 15/02/2020]

    // Hash password using the SHA3_256 algorithm.
    String hash = DigestUtils.sha3_256Hex(password);
    return hash;
  }
}
