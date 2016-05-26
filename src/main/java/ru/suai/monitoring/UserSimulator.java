package ru.suai.monitoring;

import java.io.IOException;
import java.sql.*;

/**
 * This class implements methods for generating read / write
 * SQL-requests to database of the virtual machine.
 */
public class UserSimulator {
    /**
     * URL of the MySQL DB.
     */
    private String databaseUrl;

    /**
     * Login for authentication in the MySQL database.
     */
    private String username;

    /**
     * Password for authentication in the MySQL database.
     */
    private String password;

    /**
     * Counter for fill database.
     */
    private static int i;

    /**
     * Object of the MySQL DB connection.
     */
    private Connection currentConnection;

    /**
     * String for filling MySQL database.
     */
    private static final String testStringForDatabase = "12345678900-(*^&@^%$^@#&%$%@#faergkiyrkughf;ewflckndlvkujdfivlou";

    /**
     * Constructor of this class.
     *
     * @param databaseUrl URL of the MySQL DB.
     * @param username Login for authentication in the MySQL database.
     * @param password Password for authentication in the MySQL database.
     */
    public UserSimulator(String databaseUrl, String username, String password) {
        this.databaseUrl = databaseUrl;
        this.username = username;
        this.password = password;

        // create statement and connection
        try {
            this.currentConnection = DriverManager.getConnection(this.databaseUrl, this.username, this.password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Performs database requests in multiple threads.
     * @param usersCount count of the threads with queries.
     * @param requestsCount count of the queries for each thread.
     */
    public void generateRequests(int usersCount, int requestsCount) throws IOException {
        for (int i = 0; i < usersCount; i++) {
            // the mysql insert statement
            String query = "insert into test (number, number2, text)"
                    + " values (?, ?, ?)";

            Thread thread = new Thread(() -> {
                for (int j = 0; j < requestsCount; j++) {
                    try {
                        this.i++;
                        // create the mysql insert preparedstatement
                        PreparedStatement preparedStmt = this.currentConnection.prepareStatement(query);
                        preparedStmt.setInt(1, requestsCount + this.i);
                        preparedStmt.setInt(2, requestsCount + this.i + 1);
                        preparedStmt.setString(3, testStringForDatabase);

                        // execute the prepared statement
                        preparedStmt.execute();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            });

            thread.start();

            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
