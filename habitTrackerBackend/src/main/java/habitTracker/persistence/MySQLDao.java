package habitTracker.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Properties;

public class MySQLDao {
    private Properties properties;
    private Connection conn;

    public MySQLDao() {
        loadProperties();
    }

    public MySQLDao(Connection conn) {
        this.conn = conn;
        loadProperties();
    }

    public MySQLDao(String propertiesFilename) {
        loadProperties();
    }

    private void loadProperties() {
        properties = new Properties();

        /*
        String host = System.getenv("DB_HOST");
        String database = System.getenv("DB_NAME");
        String username = System.getenv("DB_USERNAME");
        String password = System.getenv("DB_PASSWORD");

         */


        String host = System.getenv("RDS_HOSTNAME");
        String database = System.getenv("RDS_DB_NAME");
        String username = System.getenv("RDS_USERNAME");
        String password = System.getenv("RDS_PASSWORD");


        if (host != null && database != null && username != null) {
            properties.setProperty("driver", "com.mysql.cj.jdbc.Driver");
            properties.setProperty("url", "jdbc:mysql://" + host + ":3306/");
            properties.setProperty("database", database);
            properties.setProperty("username", username);
            properties.setProperty("password", password != null ? password : "");
        } else {
            try {
                var resource = Thread.currentThread()
                        .getContextClassLoader()
                        .getResourceAsStream("database.properties");

                if (resource == null) {
                    throw new RuntimeException("Could not find database.properties and no environment variables set");
                }

                properties.load(resource);
            } catch (Exception e) {
                throw new RuntimeException("Failed to load database properties", e);
            }
        }
    }

    public Connection getConnection() {
        if (conn != null) {
            return conn;
        }

        if (properties == null) {
            loadProperties();
        }

        String driver = properties.getProperty("driver");
        String url = properties.getProperty("url");
        String database = properties.getProperty("database");
        String username = properties.getProperty("username");
        String password = properties.getProperty("password", "");

        try {
            Class.forName(driver);

            try {
                return DriverManager.getConnection(url + database, username, password);
            } catch (SQLException e) {
                System.out.println(LocalDateTime.now()
                        + ": An SQLException occurred while trying to connect to the database.");
                System.out.println("Error: " + e.getMessage());
                e.printStackTrace();
            }

        } catch (ClassNotFoundException e) {
            System.out.println(LocalDateTime.now()
                    + ": A ClassNotFoundException occurred while trying to load the MySQL driver.");
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    public void freeConnection(Connection con) {
        try {
            if (con != null) {
                con.close();
            }
        } catch (SQLException e) {
            System.out.println("SQL Exception occurred when attempting to free connection to database.");
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}