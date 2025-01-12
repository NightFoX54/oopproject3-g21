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

public class CustomerSession {
    public Stage stage;

    public TableView<sessionChooser.session> table;

    @FXML
    private FlowPane sessionFlowPane;


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

    public void showSession(){
        sessionFlowPane.getChildren().add(table);
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
