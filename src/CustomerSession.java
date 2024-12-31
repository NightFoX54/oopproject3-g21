import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class CustomerSession {
    public Stage stage;

    @FXML
    private FlowPane sessionFlowPane;


    public void showMovie(String movieName, String movieGenre, String movieSummary, String moviePosterPath) {
        Label titleLabel = new Label("Title: " + movieName);
        Label genreLabel = new Label("Genre: " + movieGenre);
        Label summaryLabel = new Label("Summary: " + movieSummary);
        Image image = new Image(getClass().getResourceAsStream(moviePosterPath));
        VBox textBox = new VBox();
        textBox.setSpacing(5);
        HBox movieBox = new HBox();
        movieBox.setSpacing(5); // Space between the image and the title

        // Load the movie poster image
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(240); // Set width for the poster
        imageView.setFitHeight(320);
        imageView.setPreserveRatio(true); // Maintain the aspect ratio

        // Create a Label for the movie title
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        genreLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        summaryLabel.setStyle("-fx-font-size: 14px;");
        summaryLabel.setWrapText(true);


        // Add the image and title to the VBox
        textBox.getChildren().addAll(titleLabel, genreLabel, summaryLabel);
        movieBox.getChildren().addAll(imageView,textBox);
        movieBox.setPrefWidth(1150);
        movieBox.setPrefHeight(320);

        // Add the VBox to the TilePane
        sessionFlowPane.getChildren().add(movieBox);
    }

    public void showSession(String hall_name, String hall_capacity, String available_seats, String schedule_date, String start_time){
        HBox box = new HBox();
        box.setSpacing(145);
        Label nameLabel = new Label("Hall Name: " + hall_name);
        Label capacityLabel = new Label("Hall Capacity: " + hall_capacity);
        Label scheduleLabel = new Label("Session Date: " + schedule_date);
        Label startLabel = new Label("Session Start Time: " + start_time);
        Label availableSeatsLabel = new Label("Available Seats: " + available_seats);
        box.getChildren().addAll(nameLabel, scheduleLabel, startLabel, capacityLabel, availableSeatsLabel);
        box.setPrefHeight(30);
        box.setPrefWidth(1150);
        sessionFlowPane.getChildren().add(box);

    }

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
