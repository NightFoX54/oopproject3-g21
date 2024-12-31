import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class login {
    public static boolean login(String username, String password) {
        String query = "SELECT * FROM users WHERE username COLLATE utf8mb4_bin = ? AND password COLLATE utf8mb4_bin = ?";
        try (Connection connection = Main.getConnection(); PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            statement.setString(2, password);
            try(ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String user = resultSet.getString("username");
                    String role = resultSet.getString("role");
                    String name = Main.capitalizeFirstLetter(resultSet.getString("name"));
                    String surname = Main.capitalizeFirstLetter(resultSet.getString("surname"));
                    if (role.equals("admin")) {
                        Main.currentUser = new admin(name, surname, user, role);
                    } else if (role.equals("manager")) {
                        Main.currentUser = new manager(name, surname, user, role);
                    } else if (role.equals("cashier")) {
                        Main.currentUser = new cashier(name, surname, user, role);
                    }
                    return true;
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }
}
