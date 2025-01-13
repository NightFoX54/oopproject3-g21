import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Controller class for managing password change functionality.
 * Handles user interactions for changing their password and navigation between scenes.
 */
public class passChangeCont {
    
    /**
     * The current stage for this controller.
     */
    private Stage stage;

    /**
     * The current scene for this controller.
     */
    private Scene scene;

    /**
     * The root node for the current FXML layout.
     */
    private Parent root;

    /**
     * The previous page's FXML file path for navigation.
     */
    static String prevPage;
    @FXML

     /**
     * Input field for the old password.
     */
    PasswordField oldPass;
    @FXML

    /**
     * Input field for the new password.
     */
    PasswordField newPass;

     /**
     * Input field for confirming the new password.
     */
    @FXML
    @FXML
    PasswordField newPass2;

     /**
     * Label to display warning messages.
     */
    @FXML
    @FXML
    Label warning;

    /**
     * A secondary stage that may be managed by this controller.
     */
    public static Stage stage2;

     /**
     * Returns to the previous page.
     *
     * @param event The event triggered by the user.
     * @throws IOException If the FXML file cannot be loaded.
     */
    public void prevMenu(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(prevPage));
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        prevPage = null;
    }

    /**
     * Changes the user's password if valid.
     * 
     * @param event The event triggered by the user.
     * @throws IOException If the FXML file cannot be loaded.
     */
    public void passChange(ActionEvent event) throws IOException {
        if(stage2 != null)
            stage2.close();
        String oldPass = this.oldPass.getText();
        String newPass = this.newPass.getText();
        String newPass2 = this.newPass2.getText();
        if(!oldPass.equals(newPass) && newPass.equals(newPass2)) {
            String query = "SELECT * FROM users WHERE username COLLATE utf8mb4_bin = ? AND password COLLATE utf8mb4_bin = ?";
            try (Connection connection = Main.getConnection(); PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, Main.currentUser.username);
                statement.setString(2, oldPass);
                try(ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        query = "UPDATE users SET password = ? WHERE username = ?";
                        PreparedStatement statement2 = connection.prepareStatement(query);
                        statement2.setString(2, Main.currentUser.username);
                        statement2.setString(1, newPass);
                        statement2.executeUpdate();
                        connection.close();
                        Main.currentUser = null;
                    }
                }

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            FXMLLoader loader = new FXMLLoader(getClass().getResource(("login.fxml")));
            root = FXMLLoader.load(getClass().getResource("login.fxml"));
            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
            MovieController.secondController = null;
        }
        else if(!newPass.equals(newPass2)) {
            warning.setText("Both new password sections needs to be the same! Try again!");
        }
        else{
            warning.setText("New password can't be the same! Try again!");
        }
    }
}
