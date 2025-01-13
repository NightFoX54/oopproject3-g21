import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Locale;

/**
 * The MovieController class manages the display, search, and selection of movies in the application.
 * It provides functionalities to search movies by name or genre, display paginated results, and handle user actions like session selection and logging out.
 */
public class MovieController {

    @FXML
    public AnchorPane anchorPane;
    
    /**
     * List of all available movies.
     */
    ArrayList<movies> movieList = new ArrayList<>();
    
    /**
     * Filtered list of movies based on search criteria.
     */
    ArrayList<movies> subList = new ArrayList<>();

    
    @FXML
    private TextField nameField;

    @FXML
    private TextField genreField;

    @FXML
    private TilePane movieTilePane;


    @FXML
    private Label name;

    /**
     * Reference to the CustomerSearch controller.
     */
    public static CustomerSearch secondController;

    /**
     * Pagination component for displaying paginated movie results.
     */
    public Pagination pagination;
    
    /**
     * Initializes the MovieController by setting up data and UI components.
     * @throws IOException If the FXML file for the customer search cannot be loaded.
     */
    @FXML
    public void initialize() throws IOException {

        movieList = new ArrayList<>();
        if(secondController == null) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("customerSearch.fxml"));
            Parent root = loader.load();

            secondController = loader.getController();
            secondController.stage = new Stage();
            secondController.stage.setTitle("Group21 CinemaCenter");
            secondController.stage.setScene(new Scene(root));
            secondController.stage.setX(50);
            secondController.stage.setY(50);
            secondController.stage.show();
        }
        loadMovies();
        searchMovies("","");

        pagination = new Pagination();
        int itemsPerPage = 3;
        pagination.setPageCount((int) Math.ceil(subList.size() / (double) itemsPerPage));
        int pageCount = pagination.getPageCount();
        pagination.setCurrentPageIndex(0);
        pagination.setMaxPageIndicatorCount(pageCount);
        pagination.currentPageIndexProperty().addListener((observable, oldValue, newValue) -> {
            updateTableData(newValue.intValue(), itemsPerPage);
            secondController.updateTableData(newValue.intValue(), 3);
        });
        pagination.setLayoutX(14);
        pagination.setLayoutY(695);
        anchorPane.getChildren().add(pagination);
        updateTableData(0,3);
        name.setText("Welcome " + Main.currentUser.name + " " + Main.currentUser.surname + "!");
    }

     /**
     * Updates the movie table with paginated data.
     * @param pageIndex The current page index.
     * @param itemsPerPage The number of items to display per page.
     */
    private void updateTableData(int pageIndex, int itemsPerPage) {
        secondController.updateTableData(pageIndex, itemsPerPage);
        movieTilePane.getChildren().clear();
        int i = 0;
        int fromIndex = pageIndex * itemsPerPage;
        int toIndex = fromIndex + itemsPerPage;
        for(movies movie : subList) {
            if(i >= fromIndex && i < toIndex) {
                movieTilePane.getChildren().add(movie.movieBox);
            }
            i++;
        }
    }

    /**
     * Handles the search action by filtering movies based on user input.
     */
    @FXML
    private void Search() {
        String name = nameField.getText().toLowerCase(Locale.ENGLISH);
        String genre = genreField.getText().toLowerCase(Locale.ENGLISH);
        secondController.searchMovies(genre,name);
        searchMovies(genre,name);
        pagination.setCurrentPageIndex(0);
        pagination.setPageCount((int) Math.ceil(subList.size() / (double) 3));
        int pageCount = pagination.getPageCount();
        pagination.setMaxPageIndicatorCount(pageCount);
        pagination.currentPageIndexProperty().addListener((observable, oldValue, newValue) -> {
            updateTableData(newValue.intValue(), 3);
        });
        updateTableData(0,3);
    }

     /**
     * Changes the user password and redirects to the password change screen.
     * @param e The ActionEvent triggered by the change password button.
     * @throws IOException If the FXML file for the password change screen cannot be loaded.
     */
    @FXML
    private void changePassword(ActionEvent e) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("passChange.fxml"));
        Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        passChangeCont.prevPage = "sinema.fxml";
        passChangeCont.stage2 = secondController.stage;
    }

    /**
     * Logs out the current user and redirects to the login screen.
     * @param e The ActionEvent triggered by the logout button.
     * @throws IOException If the FXML file for the login screen cannot be loaded.
     */
    @FXML
    private void logOut(ActionEvent e) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
        Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        Main.currentUser = null;
        secondController.stage.close();
        secondController = null;
    }

    /**
     * Loads movie data from the database and populates the movie list.
     */
    private void loadMovies() {

        String query = "SELECT id, movieName, genre, summary, posterLocation FROM movies";
        try (Connection connection = Main.getConnection(); Statement statement = connection.createStatement(); ResultSet res = statement.executeQuery(query);) {
            movieList.clear();

            while (res.next()) {
                Image image = new Image(res.getString(5));
                movieList.add(new movies(res.getString(2), res.getString(3),image, res.getString(5), res.getInt(1), res.getString(4), 300, 400, true));

                secondController.movieList.add(new movies(res.getString(2), res.getString(3),image, res.getString(5), res.getInt(1), res.getString(4), 390, 510, false));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

     /**
     * Filters movies based on genre and name.
     * @param genre The genre to filter by.
     * @param name The name to filter by.
     */
    private void searchMovies(String genre, String name) {
        movieTilePane.getChildren().clear();
        secondController.searchMovies(genre,name);
        subList.clear();
        for(movies movie : movieList) {
            if (genre.length() == 0 && name.length() == 0) {
                subList.add(movie);
            } else {
                if(movie.movieName.toLowerCase(Locale.ENGLISH).contains(name.toLowerCase()) && movie.genre.toLowerCase(Locale.ENGLISH).contains(genre.toLowerCase())) {
                    subList.add(movie);
                }
            }
        }
    }

     /**
     * Redirects the user to the session selection screen for the selected movie.
     * @param e The ActionEvent triggered by the select session button.
     * @param name The name of the movie.
     * @param genre The genre of the movie.
     * @param summary The summary of the movie.
     * @param posterLocation The poster location of the movie.
     * @param movieId The ID of the movie.
     * @throws IOException If the FXML file for the session decision screen cannot be loaded.
     */
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

    /**
     * The Movies class contains the necessary informations about a movie.
     */
    public class movies{
        
        /**
         * The name of the movie.
         */
        String movieName;

        /**
         * The genre of the movie.
         */
        String genre;

         /**
         * The VBox containing the movie's details and image.
         */
        VBox movieBox;

        /**
         * Constructs a movie object and initializes its UI components.
         * 
         * @param name The name of the movie.
         * @param genre The genre of the movie.
         * @param image The image representing the movie poster.
         * @param posterLocation The file path to the movie poster.
         * @param id The unique ID of the movie.
         * @param summary A brief summary of the movie.
         * @param width The width of the movie poster image.
         * @param height The height of the movie poster image.
         * @param button A flag indicating whether to include a "Select Session" button.
         */
        movies(String name, String genre, Image image, String posterLocation, int id, String summary, int width, int height, boolean button) {
            this.movieName = name;
            this.genre = genre;
            Label titleLabel = new Label("Title: " + name);
            titleLabel.getStyleClass().add("no-hover");
            Label genreLabel = new Label("Genre: " + genre);
            genreLabel.getStyleClass().add("no-hover");
            Label summaryLabel = new Label("Summary: " + summary);
            summaryLabel.getStyleClass().add("no-hover");
            movieBox = new VBox();
            movieBox.setSpacing(5);


            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(width);
            imageView.setFitHeight(height);
            imageView.setPreserveRatio(true);


            titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            genreLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            summaryLabel.setStyle("-fx-font-size: 14px;");
            summaryLabel.setWrapText(true);



            movieBox.getChildren().addAll(imageView, titleLabel, genreLabel, summaryLabel);
            if(button) {
                Button addToCartButton = new Button("Select Session");
                addToCartButton.setOnAction(event -> {
                    try {
                        secondController.goToSessions();
                        addToCart(event, name, genre, summary, posterLocation, id);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
                movieBox.getChildren().add(addToCartButton);
            }
        }
    }
}



