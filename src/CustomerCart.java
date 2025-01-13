import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class CustomerCart {
    public Stage stage;

    @FXML
    public TilePane moviePane;

    @FXML
    private Label totalPrice;

    @FXML
    private TilePane agePane;

    private VBox ticketBox = new VBox();
    private VBox extrasBox = new VBox();

    /**
     * If ticket information added to cart. The function updates teh information of the ticket in the cart.
     * @param seatsToSell List of seat information respective to associated customer.
     * @param selectedSeats List of selected seats.
     * @param ageDiscount Percentage of discount depending on customers age.
     */
    
    public void updateTickets(ArrayList<SeatInfo> seatsToSell, ArrayList<String> selectedSeats, int ageDiscount){
        ticketBox.setSpacing(20);
        ticketBox.getChildren().clear();
        ticketBox.setPrefHeight(0);
        if(seatsToSell == null){
            for(String seat : selectedSeats) {
                Label seatLabel = new Label("Seat " + seat + " is selected");
                seatLabel.getStyleClass().add("no-hover");
                seatLabel.setAlignment(Pos.CENTER);
                seatLabel.setFont(Font.font(15));
                ticketBox.getChildren().add(seatLabel);
            }
        }
        else{
            for(String seat : selectedSeats) {
                int sold = 0;
                SeatInfo currentSeat = null;
                for(SeatInfo seatInfo : seatsToSell) {
                    if(seatInfo.seatName.equals(seat)) {
                        sold = 1;
                        currentSeat = seatInfo;
                    }
                }
                if(sold == 0){
                    Label seatLabel = new Label("Seat " + seat + " is selected");
                    seatLabel.getStyleClass().add("no-hover");
                    seatLabel.setAlignment(Pos.CENTER);
                    seatLabel.setFont(Font.font(15));
                    ticketBox.getChildren().add(seatLabel);
                }
                else{
                    Label seatLabel = new Label("Seat " + seat + " is selected for " + currentSeat.customerName);
                    seatLabel.getStyleClass().add("no-hover");
                    seatLabel.setAlignment(Pos.CENTER);
                    seatLabel.setFont(Font.font(15));
                    Label ageLabel = new Label("Age: " + currentSeat.customerAge);
                    ageLabel.getStyleClass().add("no-hover");
                    ageLabel.setAlignment(Pos.CENTER);
                    ageLabel.setFont(Font.font(15));
                    Label discountLabel;
                    if(currentSeat.customerAge < 18 || currentSeat.customerAge > 60){
                        discountLabel = new Label(ageDiscount + "% Discount!");
                    }
                    else{
                        discountLabel = new Label("No discount!");
                    }
                    discountLabel.setAlignment(Pos.CENTER);
                    discountLabel.getStyleClass().add("no-hover");
                    discountLabel.setFont(Font.font(15));
                    HBox ticket = new HBox(seatLabel, ageLabel, discountLabel);
                    ticket.setSpacing(15);
                    ticketBox.getChildren().add(ticket);
                }
            }
        }

    }


     /**
     * The method is updating the extras in the cart.
     * @param soldExtras List of extra items in the cart.
     */
    public void updateExtras(ArrayList<extrasInfo> soldExtras){
        extrasBox.getChildren().clear();
        extrasBox.setSpacing(20);
        for(extrasInfo extra : soldExtras){
            HBox extras = new HBox();
            extras.setSpacing(10);
            ImageView extrasView = new ImageView(extra.extrasImage);
            extrasView.setFitWidth(50);
            extrasView.setFitHeight(50);
            extrasView.setPreserveRatio(true);
            Label priceLabel = new Label("Price: " + extra.extrasPrice + "₺ + tax");
            priceLabel.setFont(Font.font(15));
            priceLabel.getStyleClass().add("no-hover");
            priceLabel.setAlignment(Pos.CENTER);
            Label nameLabel = new Label(extra.extrasName.substring(0, 1).toUpperCase() + extra.extrasName.substring(1));
            nameLabel.setFont(Font.font(15));
            nameLabel.getStyleClass().add("no-hover");
            nameLabel.setAlignment(Pos.CENTER);
            Label textLabel = new Label("Quantity: " + extra.extrasCount);
            textLabel.setFont(Font.font(15));
            textLabel.getStyleClass().add("no-hover");
            textLabel.setAlignment(Pos.CENTER);
            extras.getChildren().addAll(extrasView, nameLabel, priceLabel, textLabel);
            extrasBox.getChildren().add(extras);
        }
    }

    /**
     * The method is updating the total price.
     * @param price Total price.
     */
    public void updatePrice(String price){
        totalPrice.setText("Total Price: " + price);
    }

    /**
     * The method for displaying the selected movies details. 
     * @param movieName Name of the movie.
     * @param hallName Name of the hall.
     * @param sessionDate Date of the session.
     * @param sessionTime Time of the session.
     * @param image Poster of the movie.
     */
    public void showMovie(String movieName, String hallName, String sessionDate, String sessionTime, Image image) {
        Label titleLabel = new Label("Title: " + movieName);
        titleLabel.getStyleClass().add("no-hover");
        Label hallLabel = new Label("Hall: " + hallName);
        hallLabel.getStyleClass().add("no-hover");
        Label dateLabel = new Label("Date: " + sessionDate);
        dateLabel.getStyleClass().add("no-hover");
        Label timeLabel = new Label("Time: " + sessionTime);
        timeLabel.getStyleClass().add("no-hover");
        VBox movieBox = new VBox();
        movieBox.setSpacing(5);


        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(200);
        imageView.setPreserveRatio(true);
        movieBox.getChildren().addAll(imageView, titleLabel, hallLabel, dateLabel, timeLabel);
        moviePane.getChildren().add(movieBox);
        ticketBox.setPrefWidth(0);
        extrasBox.setPrefWidth(0);
        agePane.getChildren().addAll(ticketBox, extrasBox);
    }

    /**
     * Navigation operation to the search screen.
     * @throws IOException If the FXML file cannot be loaded.
     */
    public void goToSearch() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("customerSearch.fxml"));
        Parent root = loader.load();
        CustomerSearch secondController = loader.getController();
        stage.setTitle("Group21 CinemaCenter");
        stage.setScene(new Scene(root));
        stage.show();
        secondController.stage = stage;
        MovieController.secondController = secondController;
    }

     /**
     * Navigation operation for the session selection screen.
     * @throws IOException If the FXML file cannot be loaded.
     */
    public void goToSession() throws IOException {
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
     * Navigateion operation to the seat selection screen.
     * @throws IOException If the FXML file cannot be loaded.
     */
    public void goToSeat() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("customerSeat.fxml"));
        Parent root = loader.load();
        CustomerSeat secondController = loader.getController();
        stage.setTitle("Group21 CinemaCenter");
        stage.setScene(new Scene(root));
        stage.show();
        secondController.stage = stage;
        seatSelection.secondController = secondController;
    }

    /**
     * Displaying operation for the ticket invoice.
     * @throws IOException If the FXML file cannot be loaded.
     */
    public void displayTicket() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ticket_invoice_display.fxml"));
        Parent root = loader.load();
        TicketInvoiceDisplay secondController = loader.getController();
        stage.setTitle("Group21 CinemaCenter");
        stage.setScene(new Scene(root));
        stage.show();
        secondController.stage = stage;
        ageConfirmation.thirdController = secondController;
    }

    /**
     * Ends the current session and navigates back to the search screen.
     * @throws IOException If the FXML file cannot be loaded.
     */
    public void finishSession() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("customerSearch.fxml"));
        Parent root = loader.load();
        CustomerSearch secondController = loader.getController();
        stage.setTitle("Group21 CinemaCenter");
        stage.setScene(new Scene(root));
        stage.show();
        secondController.stage = stage;
        MovieController.secondController = secondController;
    }
}
