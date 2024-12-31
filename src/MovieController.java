import com.mysql.cj.protocol.Resultset;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MovieController {

    @FXML
    private TextField nameField; // Search bar for movie names

    @FXML
    private TextField genreField;

    @FXML
    private TilePane movieTilePane; // Container for displaying movie posters

    @FXML
    private Label name;

    private CustomerSearch secondController;

    @FXML
    public void initialize() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("customerSearch.fxml"));
        Parent root = loader.load();

        secondController = loader.getController();
        secondController.stage = new Stage();
        secondController.stage.setTitle("Group21 CinemaCenter");
        secondController.stage.setScene(new Scene(root));
        secondController.stage.show();
        displayMovies("",""); // Display all movies initially


        name.setText("Welcome " + Main.currentUser.name + " " + Main.currentUser.surname + "!");
    }

    @FXML
    private void Search() {
        String name = nameField.getText().toLowerCase();
        String genre = genreField.getText().toLowerCase();

        displayMovies(genre,name);
    }

    @FXML
    private void changePassword(ActionEvent e) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("passChange.fxml"));
        Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        passChangeCont.prevPage = "sinema.fxml";
    }

    @FXML
    private void logOut(ActionEvent e) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
        Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        //Main.currentUser = null;
    }

    private void displayMovies(String genre, String name) {
        movieTilePane.getChildren().clear();
        secondController.clearMovies();
        if(genre.length() == 0 && name.length() == 0) {
            String query = "SELECT id, movieName, genre, summary, posterLocation FROM movies";
            try (Connection connection = Main.getConnection(); Statement statement = connection.createStatement(); ResultSet res = statement.executeQuery(query);) {
                addMovieBox(res);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        else{
            String query = "SELECT id, movieName, genre, summary, posterLocation FROM movies WHERE movieName LIKE ? AND genre LIKE ?";
            try (Connection connection = Main.getConnection(); PreparedStatement statement = connection.prepareStatement(query);) {
                statement.setString(1, "%"+name+"%");
                statement.setString(2, "%"+genre+"%");
                try(ResultSet res = statement.executeQuery()) {
                    addMovieBox(res);
                }

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void addMovieBox(ResultSet res){
        try {
            while (res.next()) {
                String name = res.getString(2);
                String genre = res.getString(3);
                String summary = res.getString(4);
                String posterLocation = res.getString(5);
                String id = res.getString(1);
                secondController.showMovies(name, genre, summary, posterLocation);
                Label titleLabel = new Label("Title: " + name);
                Label genreLabel = new Label("Genre: " + genre);
                Label summaryLabel = new Label("Summary: " + summary);
                Image image = new Image(getClass().getResourceAsStream(posterLocation));
                VBox movieBox = new VBox();
                movieBox.setSpacing(5); // Space between the image and the title

                // Load the movie poster image
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(300); // Set width for the poster
                imageView.setFitHeight(400);
                imageView.setPreserveRatio(true); // Maintain the aspect ratio

                // Create a Label for the movie title
                titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
                genreLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
                summaryLabel.setStyle("-fx-font-size: 14px;");
                summaryLabel.setWrapText(true);
                Button addToCartButton = new Button("Select Session");
                addToCartButton.setOnAction(event -> {
                    try {
                        secondController.goToSessions();
                        addToCart(event,name,genre,summary,posterLocation,Integer.parseInt(id));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });

                // Add the image and title to the VBox
                movieBox.getChildren().addAll(imageView, titleLabel, genreLabel, summaryLabel,addToCartButton);

                // Add the VBox to the TilePane
                movieTilePane.getChildren().add(movieBox);
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void addToCart(ActionEvent e,String name, String genre, String summary, String posterLocation, int movieId) throws IOException {
        sessionChooser.movieName = name;
        sessionChooser.genre = genre;
        sessionChooser.posterPath = posterLocation;
        sessionChooser.summary = summary;
        sessionChooser.movieId = movieId;

        Parent root = FXMLLoader.load(getClass().getResource("sessionDecision.fxml"));
        Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
