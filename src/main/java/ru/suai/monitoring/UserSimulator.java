package ru.suai.monitoring;

import ru.suai.view.Visualizator;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;

/**
 * This class implements methods for generating read / write
 * SQL-requests to database of the virtual machine.
 */
public class UserSimulator {
    /**
     * Message for error message box.
     */
    public static final String SQL_EXCEPTION_MESSAGE = "Error with SQL connection: ";
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
        ArrayList<Thread> threads = new ArrayList<>();

        for (int i = 0; i < usersCount; i++) {
            threads.add(new Thread(() -> {
                for (int j = 0; j < requestsCount; j++) {
                    try {
                        UserSimulator.i++;
                        String query;
                        PreparedStatement preparedStmt;

                        query = "insert into test1 (number, number2, text, text1)" + " values (?, ?, ?, ?)";

                        preparedStmt = this.currentConnection.prepareStatement(query);
                        preparedStmt.setInt(1, requestsCount + UserSimulator.i);
                        preparedStmt.setInt(2, requestsCount + UserSimulator.i + 1);
                        preparedStmt.setString(3, testStringForDatabase);
                        preparedStmt.setString(4, testStringForDatabase + UserSimulator.i);
                        // execute the prepared statement
                        preparedStmt.execute();

                    } catch (SQLException e) {
                        e.printStackTrace();
                        Visualizator.showErrorMessageBox(SQL_EXCEPTION_MESSAGE + e.getMessage());
                    }
                }
            }));
        }

        for (int i = 0; i < usersCount; i++) {
            threads.get(i).start();
        }

        for (int i = 0; i < usersCount; i++) {
            try {
                threads.get(i).join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
