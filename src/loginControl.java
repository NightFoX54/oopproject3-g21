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

/**
 * Handles the login functionality for the application.
 * Verifies user credentials and redirects to the appropriate page based on the user's role.
 */
public class loginControl {

    /**
     * The current stage for managing scenes.
     */
    private Stage stage;

    /**
     * The current scene for the application.
     */
    private Scene scene;

    /**
     * The root node for the current FXML layout.
     */
    private Parent root;

     /**
     * Input field for the username.
     */
    @FXML
    TextField username;

     /**
     * Input field for the password.
     */
    @FXML
    PasswordField password;

     /**
     * Label for displaying warning messages.
     */
    @FXML
    Label warning;

    /**
     * Verifies the user's login credentials and redirects to the appropriate page based on their role.
     * Displays a warning if the credentials are invalid.
     *
     * @param event The event triggered by the login button.
     * @throws IOException If an error occurs while loading the FXML file.
     */
    @FXML
    private void loginControl(ActionEvent event) throws IOException {
        String username = this.username.getText();
        String password = this.password.getText();
        boolean logged = login.login(username,password);
        if(logged){
            FXMLLoader loader = null;
            if(Main.currentUser.role.equals("manager")){
                loader = new FXMLLoader(getClass().getResource(("manager.fxml")));
            }
            else if(Main.currentUser.role.equals("cashier")){
                loader = new FXMLLoader(getClass().getResource(("sinema.fxml")));
            }
            else{
                loader = new FXMLLoader(getClass().getResource(("admin.fxml")));
            }

            root = loader.load();
            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        }
        else{
            warning.setText("Incorrect Username or Password! Try again!");
            warning.setStyle("-fx-text-fill: red");
            warning.getStyleClass().add("no-hover");
        }
    }
}
