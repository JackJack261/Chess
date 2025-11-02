package dataaccess;

import java.sql.*;
import java.util.Properties;

public class DatabaseManager {
    private static String databaseName;
    private static String dbUsername;
    private static String dbPassword;
    private static String connectionUrl;

    /*
     * Load the database information for the db.properties file.
     */
    static {
        loadPropertiesFromResources();
    }

    /**
     * Creates the database if it does not already exist.
     */
    static public void createDatabase() throws DataAccessException {
        var statement = "CREATE DATABASE IF NOT EXISTS " + databaseName;
        try (var conn = DriverManager.getConnection(connectionUrl, dbUsername, dbPassword);
             var preparedStatement = conn.prepareStatement(statement)) {
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException("failed to create database", ex);
        }
    }

    /**
     * Create a connection to the database and sets the catalog based upon the
     * properties specified in db.properties. Connections to the database should
     * be short-lived, and you must close the connection when you are done with it.
     * The easiest way to do that is with a try-with-resource block.
     * <br/>
     * <code>
     * try (var conn = DatabaseManager.getConnection()) {
     * // execute SQL statements.
     * }
     * </code>
     */
    public static Connection getConnection() throws DataAccessException {
        try {
            //do not wrap the following line with a try-with-resources
            var conn = DriverManager.getConnection(connectionUrl, dbUsername, dbPassword);
            conn.setCatalog(databaseName);
            return conn;
        } catch (SQLException ex) {
            throw new DataAccessException("failed to get connection", ex);
        }
    }

    private static void loadPropertiesFromResources() {
        try (var propStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("db.properties")) {
            if (propStream == null) {
                throw new Exception("Unable to load db.properties");
            }
            Properties props = new Properties();
            props.load(propStream);
            loadProperties(props);
        } catch (Exception ex) {
            throw new RuntimeException("unable to process db.properties", ex);
        }
    }

    private static void loadProperties(Properties props) {
        databaseName = props.getProperty("db.name");
        dbUsername = props.getProperty("db.user");
        dbPassword = props.getProperty("db.password");

        var host = props.getProperty("db.host");
        var port = Integer.parseInt(props.getProperty("db.port"));
        connectionUrl = String.format("jdbc:mysql://%s:%d", host, port);
    }


    private static String readFile(String resourceFileName) {
        try (var inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceFileName)) {
            if (inputStream == null) {
                throw new RuntimeException("Resource file not found: " + resourceFileName);
            }

            // Use a Scanner to read the entire stream content into a single String
            java.util.Scanner scanner = new java.util.Scanner(inputStream).useDelimiter("\\A");

            return scanner.hasNext() ? scanner.next() : "";

        } catch (java.io.IOException ex) {
            throw new RuntimeException("Error reading resource file: " + resourceFileName, ex);
        }
    }


    // Inside DatabaseManager.java
    public static void configureDatabase() throws DataAccessException {
        String createStatements = readFile("setup.sql");

        DatabaseManager.createDatabase(); // Ensure the database exists

        try (var conn = getConnection();
             var statement = conn.createStatement()) {

            // Execute the entire SQL script
            statement.executeUpdate(createStatements);

        } catch (SQLException e) {
            // Handle SQL exception
            throw new DataAccessException("Failed to configure database schema: " + e.getMessage(), e);
        }
    }
}
