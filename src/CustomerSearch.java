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

public class CustomerSearch {

    public ArrayList<MovieController.movies> movieList = new ArrayList<>();
    public ArrayList<MovieController.movies> subList = new ArrayList<>();

    @FXML
    private TilePane TilePane;

    public Stage stage = new Stage();

    @FXML
    public void initialize() throws IOException {

    }

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
