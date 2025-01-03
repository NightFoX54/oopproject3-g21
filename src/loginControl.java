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

public class loginControl {

    private Stage stage;
    private Scene scene;
    private Parent root;
    @FXML
    TextField username;
    @FXML
    PasswordField password;
    @FXML
    Label warning;

    @FXML
    private void loginControl(ActionEvent event) throws IOException {
        String username = this.username.getText();
        String password = this.password.getText();
        boolean logged = login.login(username,password);
        if(logged){
            FXMLLoader loader = new FXMLLoader(getClass().getResource(("sinema.fxml")));
            root = FXMLLoader.load(getClass().getResource("sinema.fxml"));
            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        }
        else{
            warning.setText("Incorrect Username or Password! Try again!");
        }
    }
}
