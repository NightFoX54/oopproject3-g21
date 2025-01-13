import javafx.concurrent.Task;
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

/**
* Seat selection process for a movie.
* contains information about movies, session dates and seats.
*/
public class seatSelection {

    /**
    * A VBox for displaying movie-related content
    */
    public static VBox movieBox;
    
    /**
    * list of selected seats.
    */
    public static ArrayList<String> selectedSeats = new ArrayList<>();

    /**
    * Numbers of rows in seating.
    */
    public static int rows;
    
    /**
    * Numbers of columns in seating.
    */
    public static int cols;

    /**
    * ID of the current session.
    */
    public static int session_id = 1;

    /**
    * Path to the poster image.
    */
    public static String posterPath;

    /**
    * Name of the movie hall.
    */
    public static String hallName;

    /**
    * Session date of the movie.
    *//
    public static String sessionDate;

    /**
    * Session time of the movie.
    *//
    public static String sessionTime;

    /**
    * Name of the movie
    */
    public static String movieName;

    /**
    * Secondary controller for managing seat selection
    */
    public static CustomerSeat secondController;

     /**
     * Label to display the movie name.
     */
    @FXML
    Label name;

     /**
     * Main container for the seat selection interface.
     */
    @FXML
    private AnchorPane pane;

      /**
     * TilePane for displaying movie tiles.
     */
    @FXML
    TilePane movieTile;

     /**
     * Label to represent the screen in the UI.
     */
    @FXML
    Label screen;
    
     /**
     * Label to display warning messages to the user.
     */
    @FXML
    Label warningMessage;

     /**
     * Displays movie details such as title, hall, date, and time in the UI.
     */
    public void movieDetails(){
        Label titleLabel = new Label("Title: " + movieName);
        titleLabel.getStyleClass().add("no-hover");
        Label hallLabel = new Label("Hall: " + hallName);
        hallLabel.getStyleClass().add("no-hover");
        Label dateLabel = new Label("Date: " + sessionDate);
        dateLabel.getStyleClass().add("no-hover");
        Label timeLabel = new Label("Time: " + sessionTime);
        timeLabel.getStyleClass().add("no-hover");
        Image image = new Image(getClass().getResourceAsStream(posterPath));
        movieBox = new VBox();
        movieBox.setSpacing(5);


        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(200);
        imageView.setPreserveRatio(true);
        movieBox.getChildren().addAll(imageView, titleLabel, hallLabel, dateLabel, timeLabel);
        movieTile.getChildren().add(movieBox);
        if(secondController.seat == 0) {
            secondController.showMovie(movieName, hallName, sessionDate, sessionTime, posterPath);
            secondController.seat = 1;
        }
    }

     /**
     * Initializes the seat selection interface, setting up the seating grid and styles.
     */
    @FXML
    public void initialize() {

        movieDetails();
        warningMessage.setStyle("-fx-text-fill: transparent; -fx-background-color: transparent;");
        screen.setStyle("-fx-font-size: 25px;");
        name.setText("Welcome " + Main.currentUser.name + " " + Main.currentUser.surname + "!");
        GridPane grid = new GridPane();
        grid.setPrefHeight(520);
        double gap = 580 / (cols * 4 - 1);
        double buttonSize = gap * 3;
        double gap2 = (520 - buttonSize * rows) / (rows - 1);
        grid.setPrefWidth(580 + buttonSize + gap);

        grid.setHgap(gap);
        grid.setVgap(gap2);

        Image soldSeatImage = new Image("photos/chair_sold.png");
        Image seatImage = new Image("photos/chair.png");

        Set<String> soldSeats = soldSeats();
        char rowChar = (char) ('A' + rows - 1);
        for (int row = 0; row < rows; row++) {
            Label seatLabel1 = new Label(rowChar + "");
            grid.add(seatLabel1, 0, row);


            seatLabel1.setPrefWidth(buttonSize);
            seatLabel1.setPrefHeight(buttonSize);
            seatLabel1.setFont(Font.font(buttonSize / 3));
            seatLabel1.setStyle(String.format("-fx-font-size: %f", buttonSize / 3));
            seatLabel1.getStyleClass().add("no-hover");
            seatLabel1.setAlignment(Pos.CENTER);
            for (int col = 1; col < cols+1; col++) {
                Button seatButton = new Button();
                ImageView imageView = new ImageView(soldSeatImage);
                imageView.setFitWidth(buttonSize);
                imageView.setFitHeight(buttonSize);
                seatButton.setStyle("-fx-background-color: transparent; -fx-border-width: 500; -fx-padding: 0; -fx-scale-x: 1; -fx-scale-y: 1; -fx-transition: none; -fx-cursor: default;");
                seatButton.setDisable(true);

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

                grid.setLayoutX(215 - buttonSize - gap);
                grid.setLayoutY(30);
                String seatName = rowChar + "" + String.valueOf(col);
                String seat = String.valueOf(row) + "" + String.valueOf(col);

                if(!soldSeats.contains(seatName)) {
                    seatButton.setStyle("-fx-background-color: transparent; -fx-border-width: 500; -fx-padding: 0");
                    seatButton.setDisable(false);

                    imageView = new ImageView(seatImage);
                    seatButton.setGraphic(imageView);
                    imageView.setFitWidth(buttonSize);
                    imageView.setFitHeight(buttonSize);
                    seatButton.setOnAction(e -> {
                        String style = seatButton.getStyle();

                        if (style.contains("red")) {
                            seatButton.setStyle("-fx-background-color: transparent; -fx-border-width: 500; -fx-padding: 0");
                            selectedSeats.remove(seatName);
                            secondController.updateSeats(seat,buttonSize,0);
                        }
                        else {
                            seatButton.setStyle("-fx-background-color: red; -fx-border-width: 500; -fx-padding: 0");
                            selectedSeats.add(seatName);
                            secondController.updateSeats(seat,buttonSize,1);
                        }
                    });
                }
                if(selectedSeats.contains(seatName)){
                    seatButton.setStyle("-fx-background-color: red; -fx-border-width: 500; -fx-padding: 0");
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

     /**
     * Retrieves a set of sold seats from the database for the current session.
     * 
     * @return A set of sold seat identifiers.
     */
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

      /**
     * Navigates to the password change page.
     * 
     * @param e The action event triggered by the user.
     * @throws IOException If the FXML file cannot be loaded.
     */
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

    /**
     * Logs out the current user and navigates to the login page.
     * 
     * @param e The action event triggered by the user.
     * @throws IOException If the FXML file cannot be loaded.
     */
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

     /**
     * Navigates to the movie search page.
     * 
     * @param e The action event triggered by the user.
     * @throws IOException If the FXML file cannot be loaded.
     */
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

    /**
     * Navigates to the session selection page.
     * 
     * @param e The action event triggered by the user.
     * @throws IOException If the FXML file cannot be loaded.
     */
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

    /**
     * Confirms the user's seat selection and navigates to the age confirmation page.
     * Displays a warning if no seats are selected.
     * 
     * @param e The action event triggered by the user.
     * @throws IOException If the FXML file cannot be loaded.
     */
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
            warningMessage.setStyle("-fx-text-fill: red;");
            warningMessage.getStyleClass().add("no-hover");
        }
    }
}
