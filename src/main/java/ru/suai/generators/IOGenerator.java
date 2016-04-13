package ru.suai.generators;

import java.sql.*;

/**
 * This class implements methods for generating read / write
 * SQL-requests to database of the virtual machine.
 */
public class IOGenerator {
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

    /*
     * Statement for connection to the database.
     */
    private Statement statement;

    /**
     * Object of the MySQL DB connection.
     */
    private Connection currentConnection;

    /**
     * Constructor of this class.
     *
     * @param databaseUrl URL of the MySQL DB.
     * @param username Login for authentication in the MySQL database.
     * @param password Password for authentication in the MySQL database.
     */
    public IOGenerator(String databaseUrl, String username, String password) {
        this.databaseUrl = databaseUrl;
        this.username = username;
        this.password = password;

        // create statement and connection
        try {
            this.currentConnection = DriverManager.getConnection(this.databaseUrl, this.username, this.password);
            this.statement = this.currentConnection.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Performs database requests in multiple threads.
     * @param threadsCount count of the threads with queries.
     * @param requestsCount count of the queries for each thread.
     * @param request MySQL query in String.
     */
    public void generateRequests(int threadsCount, int requestsCount, String request) {
        for (int i = 0; i < threadsCount; i++) {
            Statement st = this.statement;

            Thread thread = new Thread(() -> {
                for (int j = 0; j < requestsCount; j++) {
                    try {
                        st.executeQuery(request);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            });

            thread.start();
        }
    }
}
