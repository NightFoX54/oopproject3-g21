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
import java.util.ArrayList;
import java.util.Set;

public class CustomerSeat {
    public Stage stage;

    GridPane grid;

    @FXML
    private AnchorPane Pane;

    @FXML
    private TilePane movieTile;

    public int seat = 0;

    @FXML
    Label screen;

    @FXML
    public void initialize() {
        screen.setStyle("-fx-font-size: 25;");
        grid = new GridPane();
    }




    public void showSeats(int rows, int cols, Set<String> soldSeats, ArrayList<String> selectedSeats) {
        grid.setPrefHeight(520); // Set the preferred height of the GridPane
        double gap = 600 / (cols * 4 - 1);
        double buttonSize = gap * 3;
        double gap2 = (520 - buttonSize * rows) / (rows - 1);
        grid.setPrefWidth(600 + buttonSize + gap);

        grid.setHgap(gap);
        grid.setVgap(gap2);

        char rowChar = (char) ('A' + rows - 1);
        for (int row = 0; row < rows; row++) {
            Label seatLabel1 = new Label(rowChar + "");
            grid.add(seatLabel1, 0, row);

            seatLabel1.setPrefWidth(buttonSize);
            seatLabel1.setAlignment(Pos.CENTER);
            seatLabel1.setPrefHeight(buttonSize);
            seatLabel1.setFont(Font.font(buttonSize / 3));
            seatLabel1.setStyle(String.format("-fx-font-size: %f", buttonSize / 3));
            seatLabel1.getStyleClass().add("no-hover");
            for (int col = 1; col < cols+1; col++) {
                Button seatButton = new Button();
                Image seatImage = new Image("photos/chair_sold.png"); // Replace with your image path
                ImageView imageView = new ImageView(seatImage);
                imageView.setFitWidth(buttonSize);
                imageView.setFitHeight(buttonSize);
                seatButton.setStyle("-fx-background-color: transparent; -fx-border-width: 500; -fx-padding: 0; -fx-scale-x: 1; -fx-scale-y: 1; -fx-transition: none; -fx-cursor: default;");

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
                if(selectedSeats.contains(seatName)){
                    seatButton.setStyle("-fx-background-color: red; -fx-border-width: 500; -fx-padding: 0; -fx-scale-x: 1; -fx-scale-y: 1; -fx-transition: none; -fx-cursor: default;");
                }
                if(!soldSeats.contains(seatName)) {
                    seatImage = new Image("photos/chair.png");
                    imageView = new ImageView(seatImage);
                    seatButton.setGraphic(imageView);
                    imageView.setFitWidth(buttonSize);
                    imageView.setFitHeight(buttonSize);
                }
            }
            rowChar--;

        }
        Pane.getChildren().add(grid);
    }

    public void updateSeats(String seatName, double buttonSize, int selection) {
        int row = seatName.charAt(0) - '0';
        int col = seatName.charAt(1) - '0';
        Button seatButton = new Button();
        Image seatImage = new Image("photos/chair.png"); // Replace with your image path
        ImageView imageView = new ImageView(seatImage);
        imageView.setFitWidth(buttonSize);
        imageView.setFitHeight(buttonSize);
        if(selection == 0) {
            seatButton.setStyle("-fx-background-color: transparent; -fx-border-width: 500; -fx-padding: 0; -fx-scale-x: 1; -fx-scale-y: 1; -fx-transition: none; -fx-cursor: default;");
        }
        else {
            seatButton.setStyle("-fx-background-color: red; -fx-border-width: 500; -fx-padding: 0; -fx-scale-x: 1; -fx-scale-y: 1; -fx-transition: none; -fx-cursor: default;");
        }
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
        Node targetNode = null;
        for (Node node : grid.getChildren()) {
            Integer colIndex = GridPane.getColumnIndex(node);
            Integer rowIndex = GridPane.getRowIndex(node);

            // Default to 0 if column or row indices are null (happens for elements without set indices)
            if (colIndex == null) colIndex = 0;
            if (rowIndex == null) rowIndex = 0;

            // Check if the current node matches the specified column and row
            if (colIndex == col && rowIndex == row) {
                targetNode = node;
                break;
            }
        }

        if (targetNode != null) {
            grid.getChildren().remove(targetNode);
        }
        grid.add(seatStack, col, row);
    }

    public void showMovie(String movieName, String hallName, String sessionDate, String sessionTime, String posterPath) {
        Label titleLabel = new Label("Title: " + movieName);
        titleLabel.getStyleClass().add("no-hover");
        Label hallLabel = new Label("Hall: " + hallName);
        hallLabel.getStyleClass().add("no-hover");
        Label dateLabel = new Label("Date: " + sessionDate);
        dateLabel.getStyleClass().add("no-hover");
        Label timeLabel = new Label("Time: " + sessionTime);
        timeLabel.getStyleClass().add("no-hover");
        Image image = new Image(getClass().getResourceAsStream(posterPath));
        VBox movieBox = new VBox();
        movieBox.setSpacing(5); // Space between the image and the title

        // Load the movie poster image
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(200); // Set width for the poster
        imageView.setPreserveRatio(true);
        movieBox.getChildren().addAll(imageView, titleLabel, hallLabel, dateLabel, timeLabel);
        movieTile.getChildren().add(movieBox);
    }

    public void goToSearch() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("customerSearch.fxml"));
        Parent root = loader.load();
        CustomerSearch secondController = loader.getController();
        stage.setScene(new Scene(root));
        secondController.stage = stage;
        secondController.stage.show();
        MovieController.secondController = secondController;
    }

    public void goToSession() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("customerSession.fxml"));
        Parent root = loader.load();
        CustomerSession secondController = loader.getController();
        stage.setScene(new Scene(root));
        secondController.stage = stage;
        secondController.stage.show();
        sessionChooser.secondController = secondController;
    }

    public void goToCart() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("customerCart.fxml"));
        Parent root = loader.load();
        CustomerCart secondController = loader.getController();
        stage.setScene(new Scene(root));
        secondController.stage = stage;
        secondController.stage.show();
        ageConfirmation.secondController = secondController;
    }
}
