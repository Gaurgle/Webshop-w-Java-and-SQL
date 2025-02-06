import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnection {
    private static Connection conn;

    // create connection to DB's, dynamic URL
    private static final String URL_PRE = "jdbc:mysql://localhost:3306/";
    private static final String URL_POST = "?serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASSWORD = "rootPassword";

    public static Connection getConnection(String database) throws SQLException {
        if (conn == null || conn.isClosed()) {
            String url = String.format(URL_PRE + database + URL_POST);
            return DriverManager.getConnection(url, USER, PASSWORD);

        }
        return conn;
    }

    public static void closeConnection() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                System.out.println("Connection closed");

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
