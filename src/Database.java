import java.sql.*;

public class Database {
    private static final String URL = "jdbc:mysql://localhost:3306/school_management";
    private static final String USER = "root";
    private static final String PASSWORD = "sanskar@06";

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
