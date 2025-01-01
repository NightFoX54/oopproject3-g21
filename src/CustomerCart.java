import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.ArrayList;

public class CustomerCart {
    public Stage stage;

    @FXML
    private TilePane moviePane;

    @FXML
    private Label totalPrice;

    @FXML
    private TilePane agePane;

    private VBox ticketBox = new VBox();
    private VBox extrasBox = new VBox();

    public void updateTickets(ArrayList<SeatInfo> seatsToSell, ArrayList<String> selectedSeats, int ageDiscount){
        ticketBox.setSpacing(15);
        ticketBox.getChildren().clear();
        if(seatsToSell == null){
            for(String seat : selectedSeats) {
                Label seatLabel = new Label("Seat " + seat + " is selected");
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
                    seatLabel.setAlignment(Pos.CENTER);
                    seatLabel.setFont(Font.font(15));
                    ticketBox.getChildren().add(seatLabel);
                }
                else{
                    Label seatLabel = new Label("Seat " + seat + " is selected for " + currentSeat.customerName);
                    seatLabel.setAlignment(Pos.CENTER);
                    seatLabel.setFont(Font.font(15));
                    Label ageLabel = new Label("Age: " + currentSeat.customerAge);
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
                    discountLabel.setFont(Font.font(15));
                    HBox ticket = new HBox(seatLabel, ageLabel, discountLabel);
                    ticket.setSpacing(15);
                    ticketBox.getChildren().add(ticket);
                }
            }
        }

    }

    public void updateExtras(ArrayList<extrasInfo> soldExtras){
        extrasBox.getChildren().clear();
        for(extrasInfo extra : soldExtras){
            HBox extras = new HBox();
            extras.setSpacing(10);
            Image extrasImage = new Image(getClass().getResourceAsStream(extra.extrasImage));
            ImageView extrasView = new ImageView(extrasImage);
            extrasView.setFitWidth(50); // Set width for the poster
            extrasView.setFitHeight(50);
            extrasView.setPreserveRatio(true);
            Label priceLabel = new Label("Price: " + extra.extrasPrice + "₺ + tax");
            priceLabel.setFont(Font.font(15));
            priceLabel.setAlignment(Pos.CENTER);
            Label nameLabel = new Label(extra.extrasName.substring(0, 1).toUpperCase() + extra.extrasName.substring(1));
            nameLabel.setFont(Font.font(15));
            nameLabel.setAlignment(Pos.CENTER);
            Label textLabel = new Label("Quantity: " + extra.extrasCount);
            textLabel.setFont(Font.font(15));
            textLabel.setAlignment(Pos.CENTER);
            extras.getChildren().addAll(extrasView, nameLabel, priceLabel, textLabel);
            extrasBox.getChildren().add(extras);
        }
    }

    public void updatePrice(String price){
        totalPrice.setText("Total Price: " + price);
    }

    public void showMovie(String movieName, String hallName, String sessionDate, String sessionTime, String posterPath) {
        Label titleLabel = new Label("Title: " + movieName);
        Label hallLabel = new Label("Hall: " + hallName);
        Label dateLabel = new Label("Date: " + sessionDate);
        Label timeLabel = new Label("Time: " + sessionTime);
        Image image = new Image(getClass().getResourceAsStream(posterPath));
        VBox movieBox = new VBox();
        movieBox.setSpacing(5); // Space between the image and the title

        // Load the movie poster image
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(200); // Set width for the poster
        imageView.setPreserveRatio(true);
        movieBox.getChildren().addAll(imageView, titleLabel, hallLabel, dateLabel, timeLabel);
        moviePane.getChildren().add(movieBox);
        agePane.getChildren().addAll(ticketBox, extrasBox);
    }
}
