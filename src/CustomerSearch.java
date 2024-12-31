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

    public Stage stage;

    @FXML
    public void initialize() throws IOException {

    }

    public void clearMovies(){
        TilePane.getChildren().clear();
    }

    public void showMovies(String name, String genre, String summary, String posterLocation){
        Label titleLabel = new Label("Title: " + name);
        Label genreLabel = new Label("Genre: " + genre);
        Label summaryLabel = new Label("Summary: " + summary);
        Image image = new Image(CustomerSearch.class.getResourceAsStream(posterLocation));
        VBox movieBox = new VBox();
        movieBox.setSpacing(5); // Space between the image and the title

        // Load the movie poster image
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(350); // Set width for the poster
        imageView.setFitHeight(470);
        imageView.setPreserveRatio(true); // Maintain the aspect ratio

        // Create a Label for the movie title
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        genreLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        summaryLabel.setStyle("-fx-font-size: 14px;");
        summaryLabel.setWrapText(true);

        // Add the image and title to the VBox
        movieBox.getChildren().addAll(imageView, titleLabel, genreLabel, summaryLabel);

        // Add the VBox to the TilePane
        this.TilePane.getChildren().add(movieBox);
    }

    @FXML
    public void goToSessions() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("customerSession.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Group21 CinemaCenter");
        stage.show();
    }
}
