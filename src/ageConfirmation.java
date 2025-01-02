import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.image.ImageView;
import java.io.IOException;
import java.sql.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;

public class ageConfirmation {
    public static ArrayList<String> selectedSeats = new ArrayList<String>();
    public static ArrayList<SeatInfo> seatsToSell;
    public static ArrayList<extrasInfo> soldExtras;
    public static double total_tax = 0.0;
    public static double ticketTax = 0.2;
    public static double extrasTax = 0.1;
    public static DecimalFormat df = new DecimalFormat("#.##");
    public static CustomerCart secondController = new CustomerCart();
    public static String movieName;
    public static String posterPath;
    public static String hallName;
    public static String sessionDate;
    public static String sessionTime;
    @FXML
    Label totalPrice;
    public static double total_price = 0.0;


    public static VBox movieBox;
    @FXML
    TilePane agePane;
    @FXML
    TilePane moviePane;
    @FXML
    Label name;
    @FXML
    public void initialize() {
        soldExtras = new ArrayList<>();
        ArrayList<SeatInfo> seatsToSell = new ArrayList<>();
        Collections.sort(selectedSeats);
        total_price = 0;
        String query = "SELECT * FROM prices WHERE name = ?";
        int ticketPrice;
        int ageDiscount;
        try(Connection connection = Main.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            Statement statement2 = connection.createStatement()){
            statement.setString(1, "tickets");
            query = "SELECT * FROM discounts WHERE id = 1";
            try(ResultSet rs = statement.executeQuery();
                ResultSet rs2 = statement2.executeQuery(query);){
                rs.next();
                ticketPrice = rs.getInt("price");
                rs2.next();
                ageDiscount = rs2.getInt("percentage");
            }


        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
        }
        name.setText("Welcome " + Main.currentUser.name + " " + Main.currentUser.surname + "!");
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
        moviePane.getChildren().add(movieBox);
        secondController.showMovie(movieName,hallName,sessionDate,sessionTime,posterPath);
        VBox mainBox = new VBox();
        for(String seat : selectedSeats) {
            Label seatLabel = new Label("Seat " + seat + " is selected for: ");
            seatLabel.setAlignment(Pos.CENTER);
            seatLabel.setFont(Font.font(15));
            HBox box = new HBox();
            box.setSpacing(10);
            mainBox.setSpacing(20);
            TextField name = new TextField();
            name.setPromptText("Name");
            TextField surname = new TextField();
            surname.setPromptText("Surname");
            Label text = new Label("Age:");
            text.setAlignment(Pos.CENTER);
            text.setFont(Font.font(15));
            Spinner<Integer> age = new Spinner<>();
            SpinnerValueFactory<Integer> ageValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, 1);
            age.setValueFactory(ageValueFactory);
            age.setEditable(true);
            age.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue.matches("\\d*")) { // Regex to allow only digits
                    age.getEditor().setText(newValue.replaceAll("[^\\d]", ""));
                }
            });
            Button confirm = new Button("Confirm");
            confirm.setOnAction(e -> {
                if(!(name.getText().isEmpty() || surname.getText().isEmpty())) {
                    box.getChildren().clear();
                    String custName = name.getText().substring(0, 1).toUpperCase() + name.getText().substring(1);
                    Label nameLabel = new Label(custName);
                    nameLabel.setFont(Font.font(15));
                    nameLabel.setAlignment(Pos.CENTER);
                    String custSurname = surname.getText().substring(0, 1).toUpperCase() + surname.getText();
                    Label surnameLabel = new Label(custSurname);
                    surnameLabel.setFont(Font.font(15));
                    surnameLabel.setAlignment(Pos.CENTER);
                    Label ageLabel = new Label("Age: " + age.getValue());
                    ageLabel.setFont(Font.font(15));
                    ageLabel.setAlignment(Pos.CENTER);
                    int ageValue = age.getValue();
                    Label confirmLabel;
                    if (ageValue < 18 || ageValue >= 60) {
                        confirmLabel = new Label(ageDiscount + "% discount!");
                        confirmLabel.setFont(Font.font(15));
                        confirmLabel.setAlignment(Pos.CENTER);
                        total_tax += ticketPrice * ((double) ageDiscount /100) * ticketTax;
                        df.format(total_tax);
                        total_price += ticketPrice * ((double) ageDiscount /100) * (1 + ticketTax);
                        if(total_price < 0)
                            total_price = 0;
                        String totalString = String.format("%.2f", total_price);
                        totalPrice.setText("Total Price: " + totalString);
                        secondController.updatePrice(totalString);
                    } else {
                        confirmLabel = new Label("No discount!");
                        confirmLabel.setFont(Font.font(15));
                        confirmLabel.setAlignment(Pos.CENTER);
                        total_tax += ticketPrice * ticketTax;
                        df.format(total_tax);
                        total_price += ticketPrice * (1 + ticketTax);
                        if(total_price < 0)
                            total_price = 0;
                        String totalString = String.format("%.2f", total_price);
                        totalPrice.setText("Total Price: " + totalString);
                        secondController.updatePrice(totalString);
                    }
                    SeatInfo info = new SeatInfo(seat,custName + " " + custSurname, age.getValue());
                    seatsToSell.add(info);
                    secondController.updateTickets(seatsToSell,selectedSeats,ageDiscount);
                    box.getChildren().addAll(seatLabel ,nameLabel, surnameLabel, ageLabel, confirmLabel);
                }
                else if(name.getText().isEmpty() && surname.getText().isEmpty()){
                    box.getChildren().clear();
                    box.getChildren().addAll(seatLabel, name, surname, text, age, confirm);
                    Label warningLabel = new Label("Name and Surname are required!");
                    warningLabel.setFont(Font.font(15));
                    warningLabel.setAlignment(Pos.CENTER);
                    warningLabel.setStyle("-fx-text-fill: red");
                    box.getChildren().addAll(warningLabel);
                }
                else if(surname.getText().isEmpty()){
                    box.getChildren().clear();
                    box.getChildren().addAll(seatLabel, name, surname, text, age, confirm);
                    Label warningLabel = new Label("Surname is required!");
                    warningLabel.setFont(Font.font(15));
                    warningLabel.setAlignment(Pos.CENTER);
                    warningLabel.setStyle("-fx-text-fill: red");
                    box.getChildren().addAll(warningLabel);
                }
                else if(name.getText().isEmpty()){
                    box.getChildren().clear();
                    box.getChildren().addAll(seatLabel, name, surname, text, age, confirm);
                    Label warningLabel = new Label("Name is required!");
                    warningLabel.setFont(Font.font(15));
                    warningLabel.setAlignment(Pos.CENTER);
                    warningLabel.setStyle("-fx-text-fill: red");
                    box.getChildren().addAll(warningLabel);
                }
            });
            secondController.updateTickets(null,selectedSeats,ageDiscount);
            box.getChildren().addAll(seatLabel, name, surname, text, age, confirm);
            mainBox.getChildren().add(box);
        }

        query = "SELECT * FROM prices";
        try(Connection connection = Main.getConnection();
            Statement statement = connection.createStatement();
            ResultSet res = statement.executeQuery(query);){
            while(res.next()){
                if(!res.getString("name").equals("tickets")){
                    String extrasName = res.getString("name");
                    int extrasStock = res.getInt("stock");
                    int extrasPrice = res.getInt("price");
                    String extrasPhotoPath = res.getString("photopath");
                    extrasInfo extra = new extrasInfo(extrasName, extrasPrice, 0,extrasStock, extrasPhotoPath);
                    soldExtras.add(extra);
                    HBox extras = new HBox();
                    extras.setSpacing(10);
                    Image extrasImage = new Image(getClass().getResourceAsStream(extrasPhotoPath));
                    ImageView extrasView = new ImageView(extrasImage);
                    extrasView.setFitWidth(50); // Set width for the poster
                    extrasView.setFitHeight(50);
                    extrasView.setPreserveRatio(true);
                    Label priceLabel = new Label("Price: " + extrasPrice + "₺ + tax");
                    priceLabel.setFont(Font.font(15));
                    priceLabel.setAlignment(Pos.CENTER);
                    Label nameLabel = new Label(extrasName.substring(0, 1).toUpperCase() + extrasName.substring(1));
                    nameLabel.setFont(Font.font(15));
                    nameLabel.setAlignment(Pos.CENTER);
                    Label textLabel = new Label("Quantity: ");
                    textLabel.setFont(Font.font(15));
                    textLabel.setAlignment(Pos.CENTER);
                    Spinner<Integer> quantity = new Spinner<>();
                    SpinnerValueFactory<Integer> extrasValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, extrasStock, 0);
                    quantity.setValueFactory(extrasValueFactory);
                    quantity.setEditable(true);

                    quantity.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
                        for (extrasInfo currExtra : soldExtras) {
                            if (currExtra.extrasName.equals(extrasName)) {
                                total_tax += (quantity.getValue() - currExtra.extrasCount) * extrasPrice * extrasTax;
                                df.format(total_tax);
                                total_price += (quantity.getValue() - currExtra.extrasCount) * extrasPrice * (1 + extrasTax);
                                currExtra.extrasCount = quantity.getValue();
                                secondController.updateExtras(soldExtras);
                            }
                        }
                        if(total_price < 0)
                            total_price = 0;
                        String totalString = String.format("%.2f", total_price);
                        totalPrice.setText("Total Price: " + totalString);
                        secondController.updatePrice(totalString);

                        if (!newValue.matches("\\d*")) { // Regex to allow only digits
                            quantity.getEditor().setText(newValue.replaceAll("[^\\d]", ""));
                        }
                    });
                    extras.getChildren().addAll(extrasView, nameLabel, priceLabel, textLabel, quantity);
                    mainBox.getChildren().addAll(extras);
                }
            }
            secondController.updateExtras(soldExtras);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        agePane.getChildren().add(mainBox);
    }
    @FXML
    private void changePassword(ActionEvent e) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("passChange.fxml"));
        Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        passChangeCont.prevPage = "ageConfirmation.fxml";
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
    private void goToSession(ActionEvent e) throws IOException {
        secondController.goToSession();
        Parent root = FXMLLoader.load(getClass().getResource("sessionDecision.fxml"));
        Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

    }

    @FXML
    private void goToSeatSelection(ActionEvent e) throws IOException {
        secondController.goToSeat();
        seatSelection.selectedSeats = selectedSeats;
        Parent root = FXMLLoader.load(getClass().getResource("seatSelection.fxml"));
        Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

    }
}



class SeatInfo{
    String seatName;
    String customerName;
    int customerAge;

    public SeatInfo(String seatName, String customerName, int customerAge) {
        this.seatName = seatName;
        this.customerName = customerName;
        this.customerAge = customerAge;
    }
}

class extrasInfo{
    String extrasName;
    int extrasPrice;
    int extrasCount;
    int extrasStock;
    String extrasImage;

    public extrasInfo(String extrasName, int extrasPrice, int extrasCount, int extrasStock, String extrasImage) {
        this.extrasName = extrasName;
        this.extrasPrice = extrasPrice;
        this.extrasCount = extrasCount;
        this.extrasStock = extrasStock;
        this.extrasImage = extrasImage;
    }
}