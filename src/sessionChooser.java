import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class sessionChooser {
    public static String movieName;
    public static String genre;
    public static String summary;
    public static String posterPath;
    public static int movieId;
    public static CustomerSession secondController;


    @FXML
    private Label name;
    @FXML
    FlowPane sessionFlowPane;
    @FXML
    private void changePassword(ActionEvent e) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("passChange.fxml"));
        Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        passChangeCont.prevPage = "sessionDecision.fxml";
        passChangeCont.stage2 = secondController.stage;
    }

    @FXML
    private void logOut(ActionEvent e) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
        Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        Main.currentUser = null;
        secondController.stage.close();
        MovieController.secondController = null;
    }

    @FXML
    private void goToSearch(ActionEvent e) throws IOException {
        secondController.goToSearch();
        Parent root = FXMLLoader.load(getClass().getResource("sinema.fxml"));
        Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }



    @FXML
    public void initialize() {
        name.setText("Welcome " + Main.currentUser.name + " " + Main.currentUser.surname + "!");
        Label titleLabel = new Label("Title: " + movieName);
        Label genreLabel = new Label("Genre: " + genre);
        Label summaryLabel = new Label("Summary: " + summary);
        Image image = new Image(getClass().getResourceAsStream(posterPath));
        VBox textBox = new VBox();
        textBox.setSpacing(5);
        HBox movieBox = new HBox();
        movieBox.setSpacing(5); // Space between the image and the title

        // Load the movie poster image
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(210); // Set width for the poster
        imageView.setFitHeight(280);
        imageView.setPreserveRatio(true); // Maintain the aspect ratio

        // Create a Label for the movie title
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        genreLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        summaryLabel.setStyle("-fx-font-size: 14px;");
        summaryLabel.setWrapText(true);


        // Add the image and title to the VBox
        textBox.getChildren().addAll(titleLabel, genreLabel, summaryLabel);
        movieBox.getChildren().addAll(imageView,textBox);
        movieBox.setPrefWidth(1050);
        movieBox.setPrefHeight(280);

        // Add the VBox to the TilePane
        sessionFlowPane.getChildren().add(movieBox);
        secondController.showMovie(movieName,genre,summary,posterPath);
        showSessions();
    }

    public void showSessions(){
        String query = "SELECT * FROM sessions WHERE movie_id = " + movieId;
        try(Connection connection = Main.getConnection();
            Statement statement = connection.createStatement();
            ResultSet res = statement.executeQuery(query);){

            while(res.next()){
                HBox box = new HBox();
                box.setSpacing(80);
                int sessionId = res.getInt("schedule_id");
                String hall_id = res.getString("hall_id");
                String schedule_date = res.getString("schedule_date");
                String start_time = res.getString("start_time");
                String available_seats = res.getString("available_seats");

                query = "SELECT * FROM halls WHERE hall_id = " + hall_id;
                Statement statement2 = connection.createStatement();
                ResultSet res2 = statement2.executeQuery(query);
                res2.next();
                String hall_name = res2.getString("name");
                String hall_capacity = res2.getString("capacity");
                int seat_rows = res2.getInt("seat_row_count");
                int seat_cols = res2.getInt("seat_col_count");
                secondController.showSession(hall_name, hall_capacity, available_seats, schedule_date, start_time);
                Label nameLabel = new Label("Hall Name: " + hall_name);
                Label capacityLabel = new Label("Hall Capacity: " + hall_capacity);
                Label scheduleLabel = new Label("Session Date: " + schedule_date);
                Label startLabel = new Label("Session Start Time: " + start_time);
                Label availableSeatsLabel = new Label("Available Seats: " + available_seats);
                Button selectSeatsButton = new Button("Select Seats");
                selectSeatsButton.setOnAction((ActionEvent e) -> {
                    seatSelection.sessionDate = schedule_date;
                    seatSelection.hallName = hall_name;
                    seatSelection.session_id = sessionId;
                    seatSelection.sessionTime = start_time;
                    seatSelection.movieName = movieName;
                    seatSelection.posterPath = posterPath;
                    seatSelection.cols = seat_cols;
                    seatSelection.rows = seat_rows;
                    Parent root = null;
                    try {
                        secondController.goToSeatSelection();
                        root = FXMLLoader.load(getClass().getResource("seatSelection.fxml"));
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                    Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
                    Scene scene = new Scene(root);
                    stage.setScene(scene);
                    stage.show();
                });
                box.getChildren().addAll(nameLabel, scheduleLabel, startLabel, capacityLabel, availableSeatsLabel,selectSeatsButton);
                box.setPrefHeight(30);
                box.setPrefWidth(1050);
                sessionFlowPane.getChildren().add(box);
            }


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}