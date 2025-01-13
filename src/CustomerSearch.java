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
import java.util.ArrayList;
import java.util.Locale;

/**
 * CustomerSearch is responsible for displaying a list of movies to customers,
 * allowing them to search and filter movies by genre and name.
 */
public class CustomerSearch {

    /**
     * List of all movies available.
     */
    public ArrayList<MovieController.movies> movieList = new ArrayList<>();

     /**
     * Filtered list of movies based on the search criteria.
     */
    public ArrayList<MovieController.movies> subList = new ArrayList<>();

     /**
     * The TilePane UI component where movie information is displayed.
     */
    @FXML
    private TilePane TilePane;

      /**
     * The main stage for the customer search screen.
     */
    public Stage stage = new Stage();

    /**
     * Initializes the CustomerSearch controller.
     * @throws IOException If an error occurs during initialization.
     */
    @FXML
    public void initialize() throws IOException {

    }

    /**
     * Clears all movies displayed in the TilePane.
     */
    public void clearMovies(){
        TilePane.getChildren().clear();
    }

    /*public void displayMovies(String genre, String name) {
        TilePane.getChildren().clear();
        clearMovies();
        for(MovieController.movies movie : movieList) {
            if (genre.length() == 0 && name.length() == 0) {
                TilePane.getChildren().add(movie.movieBox);

            } else {
                if(movie.movieName.toLowerCase(Locale.ENGLISH).contains(name.toLowerCase()) && movie.genre.toLowerCase(Locale.ENGLISH).contains(genre.toLowerCase())) {
                    TilePane.getChildren().add(movie.movieBox);
                }
            }
        }
    }*/


    /**
     * Navigates to the session selection screen.
     * @throws IOException If the FXML file for the session screen cannot be loaded.
     */
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

     /**
     * Filters the movies based on the provided genre and name.
     * @param genre The genre to filter movies by.
     * @param name The name to filter movies by.
     */
    public void searchMovies(String genre, String name) {
        TilePane.getChildren().clear();
        subList.clear();
        for(MovieController.movies movie : movieList) {
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
     * Updates the TilePane with a paginated view of the movies.
     * @param pageIndex The current page index.
     * @param itemsPerPage The number of items to display per page.
     */
    public void updateTableData(int pageIndex, int itemsPerPage) {
        TilePane.getChildren().clear();
        int i = 0;
        int fromIndex = pageIndex * itemsPerPage;
        int toIndex = fromIndex + itemsPerPage;
        for(MovieController.movies movie : subList) {
            if(i >= fromIndex && i < toIndex) {
                TilePane.getChildren().add(movie.movieBox);
            }
            i++;
        }
    }
}
