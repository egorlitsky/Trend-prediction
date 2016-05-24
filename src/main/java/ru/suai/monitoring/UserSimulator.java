package ru.suai.monitoring;

import java.io.IOException;
import java.nio.file.*;
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

    /*
     * Counter for fill database.
     */
    private static int i;

    /**
     * Object of the MySQL DB connection.
     */
    private Connection currentConnection;

    /**
     * The flag who shows that will be generated requests to
     * database if true, else - will be copied files on the project
     * directory of disk.
     */
    private boolean isDatabaseOperations;

    /**
     * Name of original existing file in project directory
     * for copying.
     */
    private String originalFileName;

    /**
     * Name of new file (copy of original file)
     * in project directory.
     */
    private String copyFileName;

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
        this.isDatabaseOperations = true;

        // create statement and connection
        try {
            this.currentConnection = DriverManager.getConnection(this.databaseUrl, this.username, this.password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public UserSimulator(String originalFileName, String copyFileName) {
        this.originalFileName = originalFileName;
        this.copyFileName = copyFileName;
        this.isDatabaseOperations = false;
    }

    /**
     * Performs database requests in multiple threads.
     * @param usersCount count of the threads with queries.
     * @param requestsCount count of the queries for each thread.
     */
    public void generateRequests(int usersCount, int requestsCount) throws IOException {
        if (this.isDatabaseOperations) {
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
                            preparedStmt.setString(3, "ExampleExampleExample");

                            // execute the preparedstatement
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
        } else {
            FileSystem system = FileSystems.getDefault();
            Path original = system.getPath(this.originalFileName);
            Path target = system.getPath(this.copyFileName);

            for (int k = 0; k < requestsCount; k++) {
                Files.copy(original, target, StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }
}
