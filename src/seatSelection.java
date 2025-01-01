import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class seatSelection {

    public static VBox movieBox;
    public static ArrayList<String> selectedSeats = new ArrayList<>();
    public static int rows;
    public static int cols;
    public static int session_id = 1;
    public static String posterPath;
    public static String hallName;
    public static String sessionDate;
    public static String sessionTime;
    public static String movieName;
    public static CustomerSeat secondController;
    @FXML
    Label name;
    @FXML
    private AnchorPane pane;
    @FXML
    TilePane movieTile;

    public void movieDetails(){
        Label titleLabel = new Label("Title: " + movieName);
        Label hallLabel = new Label("Hall: " + hallName);
        Label dateLabel = new Label("Date: " + sessionDate);
        Label timeLabel = new Label("Time: " + sessionTime);
        Image image = new Image(getClass().getResourceAsStream(posterPath));
        movieBox = new VBox();
        movieBox.setSpacing(5); // Space between the image and the title

        // Load the movie poster image
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(200); // Set width for the poster
        imageView.setPreserveRatio(true);
        movieBox.getChildren().addAll(imageView, titleLabel, hallLabel, dateLabel, timeLabel);
        movieTile.getChildren().add(movieBox);
        if(secondController.seat == 0) {
            secondController.showMovie(movieName, hallName, sessionDate, sessionTime, posterPath);
            secondController.seat = 1;
        }
    }

    @FXML
    public void initialize() {

        movieDetails();
        name.setText("Welcome " + Main.currentUser.name + " " + Main.currentUser.surname + "!");
        GridPane grid = new GridPane();
        grid.setPrefHeight(520); // Set the preferred height of the GridPane
        double gap = 600 / (cols * 4 - 1);
        double buttonSize = gap * 3;
        double gap2 = (520 - buttonSize * rows) / (rows - 1);
        grid.setPrefWidth(600 + buttonSize + gap);

        grid.setHgap(gap);
        grid.setVgap(gap2);

        Set<String> soldSeats = soldSeats();
        char rowChar = (char) ('A' + rows - 1);
        for (int row = 0; row < rows; row++) {
            Label seatLabel1 = new Label(rowChar + "");
            grid.add(seatLabel1, 0, row);

            seatLabel1.setPrefWidth(buttonSize);
            seatLabel1.setAlignment(Pos.CENTER);
            seatLabel1.setPrefHeight(buttonSize);
            seatLabel1.setFont(Font.font(buttonSize / 3));
            for (int col = 1; col < cols+1; col++) {
                Button seatButton = new Button();
                Image seatImage = new Image("photos/chair_sold.png"); // Replace with your image path
                ImageView imageView = new ImageView(seatImage);
                imageView.setFitWidth(buttonSize);
                imageView.setFitHeight(buttonSize);
                seatButton.setStyle("-fx-background-color: transparent; -fx-border-width: 500;");

                seatButton.setGraphic(imageView);
                seatButton.setPadding(Insets.EMPTY);
                seatButton.setPrefHeight(buttonSize);
                seatButton.setPrefWidth(buttonSize);

                Text seatText = new Text("" + (col));
                seatText.setFont(Font.font("Arial", 14));

                StackPane seatStack = new StackPane();
                seatStack.getChildren().addAll(seatButton, seatText);
                seatStack.setPrefWidth(buttonSize);
                seatStack.setPrefHeight(buttonSize);
                grid.add(seatStack, col, row);

                grid.setLayoutX(225 - buttonSize - gap);
                grid.setLayoutY(30);
                String seatName = rowChar + "" + String.valueOf(col);
                String seat = String.valueOf(row) + "" + String.valueOf(col);
                if(selectedSeats.contains(seatName)){
                    seatButton.setStyle("-fx-background-color: red;");
                }
                if(!soldSeats.contains(seatName)) {
                    seatImage = new Image("photos/chair.png");
                    imageView = new ImageView(seatImage);
                    seatButton.setGraphic(imageView);
                    imageView.setFitWidth(buttonSize);
                    imageView.setFitHeight(buttonSize);
                    seatButton.setOnAction(e -> {
                        String style = seatButton.getStyle();

                        if (style.contains("red")) {
                            seatButton.setStyle("-fx-background-color: transparent;");
                            selectedSeats.remove(seatName);
                            secondController.updateSeats(seat,buttonSize,0);
                        }
                        else {
                            seatButton.setStyle("-fx-background-color: red;");
                            selectedSeats.add(seatName);
                            secondController.updateSeats(seat,buttonSize,1);
                        }
                    });
                }
            }
            rowChar--;

        }
        if(secondController.seat == 1) {
            secondController.showSeats(rows, cols, soldSeats, selectedSeats);
            secondController.seat = 2;
        }

        pane.getChildren().add(grid);
    }

    private Set<String> soldSeats(){
        Set<String> soldSeats = new HashSet<>();
        String query = "SELECT * FROM sold_seats WHERE schedule_id = " + session_id;

        try(Connection connection = Main.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query)) {

            while(resultSet.next()) {
                soldSeats.add(resultSet.getString("seat_row") + resultSet.getString("seat_col"));
            }
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return soldSeats;
    }

    @FXML
    private void changePassword(ActionEvent e) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("passChange.fxml"));
        Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        passChangeCont.prevPage = "seatSelection.fxml";
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
        selectedSeats = new ArrayList<>();
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
        selectedSeats = new ArrayList<>();

    }

    @FXML
    private void goToSession(ActionEvent e) throws IOException {
        selectedSeats = new ArrayList<>();
        String query = "SELECT * FROM movies WHERE movieName = '" + movieName + "'";
        try(Connection connection = Main.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query)) {
            resultSet.next();
            sessionChooser.movieName = resultSet.getString("movieName");
            sessionChooser.movieId = resultSet.getInt("id");
            sessionChooser.summary = resultSet.getString("summary");
            sessionChooser.posterPath = resultSet.getString("posterLocation");
            sessionChooser.genre = resultSet.getString("genre");
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        secondController.goToSession();
        Parent root = FXMLLoader.load(getClass().getResource("sessionDecision.fxml"));
        Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();


    }

    @FXML
    private void confirmSelection(ActionEvent e) throws IOException {
        if(!selectedSeats.isEmpty()) {
            ageConfirmation.selectedSeats = selectedSeats;
            ageConfirmation.movieName = movieName;
            ageConfirmation.posterPath = posterPath;
            ageConfirmation.hallName = hallName;
            ageConfirmation.sessionDate = sessionDate;
            ageConfirmation.sessionTime = sessionTime;
            secondController.goToCart();
            Parent root = FXMLLoader.load(getClass().getResource("ageConfirmation.fxml"));
            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
            selectedSeats = new ArrayList<>();
        }
        else{
            Label warningMessage = new Label("Please select at least one seat!");
            warningMessage.setStyle("-fx-text-fill: red;");
            warningMessage.setFont(Font.font(15));
            warningMessage.setLayoutX(805);
            warningMessage.setLayoutY(555);
            pane.getChildren().add(warningMessage);
        }
    }
}
