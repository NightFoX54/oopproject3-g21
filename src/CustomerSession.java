import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * CustomerSession class manages the session selection functionality for customers.
 * It allows customers to view available movie sessions and navigate to seat selection or search screens.
 */
public class CustomerSession {

    /**
     * The main stage for the session selection screen.
     */
    public Stage stage;

     /**
     * TableView to display session details.
     */
    public TableView<sessionChooser.session> table;

    @FXML
    private FlowPane sessionFlowPane;

    /**
     * Displays the movie details including title, genre, summary, and poster image.
     * @param movieName The name of the movie.
     * @param movieGenre The genre of the movie.
     * @param movieSummary A short summary of the movie.
     * @param moviePosterPath The file path to the movie poster image.
     */
    public void showMovie(String movieName, String movieGenre, String movieSummary, String moviePosterPath) {
        Label titleLabel = new Label("Title: " + movieName);
        titleLabel.getStyleClass().add("no-hover");
        Label genreLabel = new Label("Genre: " + movieGenre);
        genreLabel.getStyleClass().add("no-hover");
        Label summaryLabel = new Label("Summary: " + movieSummary);
        summaryLabel.getStyleClass().add("no-hover");
        Image image = new Image(getClass().getResourceAsStream(moviePosterPath));
        VBox textBox = new VBox();
        textBox.setSpacing(5);
        HBox movieBox = new HBox();
        movieBox.setSpacing(5);


        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(240);
        imageView.setFitHeight(320);
        imageView.setPreserveRatio(true);


        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        genreLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        summaryLabel.setStyle("-fx-font-size: 14px;");
        summaryLabel.setWrapText(true);



        textBox.getChildren().addAll(titleLabel, genreLabel, summaryLabel);
        movieBox.getChildren().addAll(imageView,textBox);
        movieBox.setPrefWidth(1150);
        movieBox.setPrefHeight(320);


        sessionFlowPane.getChildren().add(movieBox);
    }

    /**
     * Displays the session details in the sessionFlowPane.
     */
    public void showSession(){
        sessionFlowPane.getChildren().add(table);
    }

     /**
     * Navigates to the search screen.
     * @throws IOException If the FXML file cannot be loaded.
     */
    @FXML
    public void goToSearch() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("customerSearch.fxml"));
        Parent root = loader.load();
        CustomerSearch secondController = loader.getController();
        stage.setScene(new Scene(root));
        secondController.stage = stage;
        secondController.stage.show();
        MovieController.secondController = secondController;
    }

     /**
     * Navigates to the seat selection screen.
     * @throws IOException If the FXML file cannot be loaded.
     */
    public void goToSeatSelection() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("customerSeat.fxml"));
        Parent root = loader.load();
        CustomerSeat secondController = loader.getController();
        stage.setScene(new Scene(root));
        secondController.stage = stage;
        secondController.stage.show();
        seatSelection.secondController = secondController;
    }

}
