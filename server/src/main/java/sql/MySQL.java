/*
 * MySQL.java
 * Version: 0.1.0
 * Company: CUBIXEL
 *
 * */
package sql;

import user.User;

import java.sql.*;

/**
 * CLASS DESCRIPTION:
 * #################
 *
 * @author CUBIXEL
 *
 */
public class MySQL {
    // TODO: Add enum for MySQL exceptions/failures.
    private Connection connect = null;
    private Statement statement = null;
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;


    /**
     * Constructor that .....
     * // @param ## no parameters atm ##
     */
    public MySQL() {
        try {
            // This will load the MySQL driver, each DB has its own driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Setup the connection with the DB
            connect = DriverManager.getConnection("jdbc:mysql://localhost/tutorpoint?"
                    + "user=java&password=javapw");
        } catch (ClassNotFoundException cnfe){
            // TODO deal with error
        } catch (SQLException SQLe){
            // TODO deal with error
        }
    }

    /**
     * Takes a username and sends a query to the DB to check if
     * the user exists, if this is the case the user details is
     * returned from the server.
     * @param  username: Identifier of the user as received from the client.
     */
    public User getUserDetails(String username) {
        // TODO change to prepared statement
        try {
            String state = "SELECT * FROM tutorpoint.users WHERE BINARY name = '?'";
            preparedStatement = connect.prepareStatement(state);
            preparedStatement.setString(1, username);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return new User("exists");
            } else {
                return null;
            }
        } catch (SQLException SQLe){
            // TODO deal with error
            SQLe.printStackTrace();
            return null;
        }
    }

    public User checkUserDetails(String username, String hashedpw) throws SQLException {
        // HashedPW isn't direct user input so prepared statement not needed.
        if(getUserDetails(username)!=null){
            statement = connect.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM  tutorpoint.users WHERE BINARY hashedpw = '"+hashedpw+"'");
            if (resultSet.next()) {
                return new User(username);
            } else {
                return null;
            }
        } else{
            return null;
        }
    }

    public boolean createAccount(String username, String hashpw, int tutorStatus) {
        // TODO: Check docs for injection ability with these
        try {
            String state = "INSERT INTO tutorpoint.users (name, hashedpw, istutor) " +
                    "VALUES (?,?,?)";
            //statement.executeUpdate();
            preparedStatement = connect.prepareStatement(state);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, hashpw);
            preparedStatement.setString(3, String.valueOf(tutorStatus));
            preparedStatement.executeUpdate();
            return getUserDetails(username) != null;
        } catch (SQLException SQLe){
            return false;
            // TODO deal with exception
        }
    }

    public void removeAccount(String username) {
        try {
            String state = "DELETE FROM users WHERE BINARY name = '?'";
            preparedStatement = connect.prepareStatement(state);
            preparedStatement.setString(1, username);
            preparedStatement.executeUpdate();
        } catch (SQLException SQLe){
            // TODO deal with exception
        }
    }
}
