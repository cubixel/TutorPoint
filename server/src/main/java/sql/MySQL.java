package sql;

import user.User;

import java.sql.*;

public class MySQL {
    private Connection connect = null;
    private Statement statement = null;
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;


    public MySQL() throws ClassNotFoundException, SQLException {
        // This will load the MySQL driver, each DB has its own driver
        Class.forName("com.mysql.cj.jdbc.Driver");
        // Setup the connection with the DB
        connect = DriverManager.getConnection("jdbc:mysql://localhost/tutorpoint?"
                                                + "user=java&password=javapw");
    }

    public User getUserDetails(String username) throws SQLException {
        statement = connect.createStatement();
        resultSet = statement.executeQuery("select * from tutorpoint.users");
        if(resultSet.next()){
            return new User("exists");
        } else{
            return null;
        }
    }
}
