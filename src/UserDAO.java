import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;

public class UserDAO {

    public static String userFirstName = "";

    public static int authenticateUser(String login, String password) {
        int customerId = -1;
        String sqlAuthorize = "{CALL sensitive_data.AuthenticateUser(?,?,?,?)}"; // calls SP AuthenticateUser in sensitive data DB

        try (Connection conn = DbConnection.getConnection("sensitive_data");
             CallableStatement stmtWebshop = conn.prepareCall(sqlAuthorize)) {

            String hashedPassword = hashPassword(password);

            stmtWebshop.setString(1, login);
            stmtWebshop.setString(2, hashedPassword);
            stmtWebshop.registerOutParameter(3, Types.INTEGER);
            stmtWebshop.registerOutParameter(4, Types.VARCHAR);

            stmtWebshop.execute();
            customerId = stmtWebshop.getInt(3);
            userFirstName = stmtWebshop.getString(4);

            if (userFirstName == null || userFirstName.isBlank()) {
                userFirstName = "Guest";

            }

        } catch (SQLException e) {
            e.getMessage();
        }
        return customerId;
    }

    public static int registerUser(String firstName, String lastName, String username, String pnr, String address,
                                   String city, String email, String phone, String password, String creditCard, String encryptionKey) {

        int newCustomerId = -1;
        String sqlWebshop = "{CALL Webshop.RegisterUser(?,?,?,?,?,?,?,?,?,?,?)}";   // calls SP RegisterUser in webshop DB


        try (Connection conn = DbConnection.getConnection("Webshop");
             CallableStatement stmtWebshop = conn.prepareCall(sqlWebshop)) {

            stmtWebshop.setString(1, firstName);
            stmtWebshop.setString(2, lastName);
            stmtWebshop.setString(3, username);
            stmtWebshop.setString(4, pnr);
            stmtWebshop.setString(5, address);
            stmtWebshop.setString(6, city);
            stmtWebshop.setString(7, email);
            stmtWebshop.setString(8, phone);
            stmtWebshop.registerOutParameter(9, Types.INTEGER);
            stmtWebshop.registerOutParameter(10, Types.VARCHAR);
            stmtWebshop.registerOutParameter(11, Types.INTEGER);
            stmtWebshop.execute();

            int userNameExists = stmtWebshop.getInt(11);
            if (userNameExists > 0) {
                System.out.println("Username is taken, choose another one");
                return -1;
            }

            newCustomerId = stmtWebshop.getInt(9);
            userFirstName = stmtWebshop.getString(10);

            if (newCustomerId > 0) {
                storeCredentials(newCustomerId, password, creditCard, encryptionKey);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return newCustomerId;
    }

    // split input to webshop / sensitive data DB's
    private static void storeCredentials(int customerId, String password, String creditCard, String encryptionKey) {
        String sqlSensitiveData = "{CALL sensitive_data.storeCredentials(?,?,?,?)}";    // calls SP in SensitiveData DB

        try (Connection conn = DbConnection.getConnection("sensitive_data");
             CallableStatement stmt = conn.prepareCall(sqlSensitiveData)) {

            stmt.setInt(1, customerId);
            stmt.setString(2, hashPassword(password));
            stmt.setString(3, creditCard);
            stmt.setString(4, encryptionKey);

            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // TODO not finished;
    // encrypting passwords
    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public static String getUserFirstName() {
        if (userFirstName == null || userFirstName.isBlank()) {
            return "Guest";
        }
        return userFirstName.substring(0, 1).toUpperCase() + userFirstName.substring(1);
    }

    public static int checkUserName(String username) {
        String sql = "SELECT COUNT(*) FROM webshop.customers WHERE username = ?";
        int count = 0;

        try (Connection conn = DbConnection.getConnection("Webshop");
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }
}
