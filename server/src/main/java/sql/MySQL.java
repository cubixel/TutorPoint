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
    private Connection connect = null;
    private Statement statement = null;
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;


    /**
     * Constructor that .....
     * // @param ## no parameters atm ##
     */
    public MySQL() throws ClassNotFoundException, SQLException {
        // This will load the MySQL driver, each DB has its own driver
        Class.forName("com.mysql.cj.jdbc.Driver");
        // Setup the connection with the DB
        connect = DriverManager.getConnection("jdbc:mysql://localhost/tutorpoint?"
                                                + "user=java&password=javapw");
    }

    /**
     * Takes a username and sends a query to the DB to check if
     * the user exists, if this is the case the user details is
     * returned from the server.
     * @param  username
     */
    public User getUserDetails(String username) throws SQLException {
        statement = connect.createStatement();
        resultSet = statement.executeQuery("select * from tutorpoint.users");
        if(resultSet.next()){
            return new User("exists");
        } else{
            return null;
        }
    }

    public void creatAccount(String username, String hashpw, int tutorStatus) throws SQLException {
        String state = "INSERT INTO tutorpoint.users (name, hashedpw, istutor) " +
                "VALUES (?,?,?)";
        //statement.executeUpdate();
        PreparedStatement preparedStatement = connect.prepareStatement(state);
        preparedStatement.setString(1, username);
        preparedStatement.setString(2, hashpw);
        preparedStatement.setString(3, String.valueOf(tutorStatus));
        preparedStatement.executeUpdate();
    }
}
