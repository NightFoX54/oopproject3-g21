import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomerSearch {

    @FXML
    private TilePane TilePane;

    public Stage stage = new Stage();

    @FXML
    public void initialize() throws IOException {

    }

    public void clearMovies(){
        TilePane.getChildren().clear();
    }

    public void showMovies(String name, String genre, String summary, String posterLocation){
        Label titleLabel = new Label("Title: " + name);
        titleLabel.getStyleClass().add("no-hover");
        Label genreLabel = new Label("Genre: " + genre);
        genreLabel.getStyleClass().add("no-hover");
        Label summaryLabel = new Label("Summary: " + summary);
        summaryLabel.getStyleClass().add("no-hover");
        Image image = new Image(CustomerSearch.class.getResourceAsStream(posterLocation));
        VBox movieBox = new VBox();
        movieBox.setSpacing(5); // Space between the image and the title

        // Load the movie poster image
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(390); // Set width for the poster
        imageView.setFitHeight(510);
        imageView.setPreserveRatio(true); // Maintain the aspect ratio

        // Create a Label for the movie title
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        genreLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        summaryLabel.setStyle("-fx-font-size: 14px;");
        summaryLabel.setWrapText(true);

        // Add the image and title to the VBox
        movieBox.getChildren().addAll(imageView, titleLabel, genreLabel, summaryLabel);
        movieBox.setPrefWidth(390);

        // Add the VBox to the TilePane
        this.TilePane.getChildren().add(movieBox);
    }

    @FXML
    public void goToSessions() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("customerSession.fxml"));
        Parent root = loader.load();
        CustomerSession secondController = loader.getController();
        stage.setTitle("Group21 CinemaCenter");
        stage.setScene(new Scene(root));
        stage.show();
        secondController.stage = stage;
        sessionChooser.secondController = secondController;
    }
}
