import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
    private static List<Spinner<Integer>> spinners = new ArrayList<>();
    @FXML
    Label totalPrice;
    public static double total_price = 0.0;

    private int confirmed = 0;

    @FXML
    Label paymentLabel;

    @FXML
    Button confirmButton;

    public static VBox movieBox;
    @FXML
    TilePane agePane;
    @FXML
    TilePane moviePane;
    @FXML
    Label name;
    @FXML
    public void initialize() {
        confirmed = 0;
        paymentLabel.setStyle("-fx-text-fill: transparent");
        soldExtras = new ArrayList<>();
        seatsToSell = new ArrayList<>();
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
        titleLabel.getStyleClass().add("no-hover");
        Label hallLabel = new Label("Hall: " + hallName);
        hallLabel.getStyleClass().add("no-hover");
        Label dateLabel = new Label("Date: " + sessionDate);
        dateLabel.getStyleClass().add("no-hover");
        Label timeLabel = new Label("Time: " + sessionTime);
        timeLabel.getStyleClass().add("no-hover");
        Image image = new Image(getClass().getResourceAsStream(posterPath));
        movieBox = new VBox();
        movieBox.setSpacing(5); // Space between the image and the title

        // Load the movie poster image
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(200); // Set width for the poster
        imageView.setPreserveRatio(true);
        movieBox.getChildren().addAll(imageView, titleLabel, hallLabel, dateLabel, timeLabel);
        moviePane.getChildren().add(movieBox);
        if(secondController.moviePane.getChildren().isEmpty())
            secondController.showMovie(movieName,hallName,sessionDate,sessionTime,image);
        VBox mainBox = new VBox();
        for(String seat : selectedSeats) {
            Label seatLabel = new Label("Seat " + seat + " is selected for: ");
            seatLabel.setAlignment(Pos.CENTER);
            seatLabel.setFont(Font.font(15));
            seatLabel.getStyleClass().add("no-hover");
            HBox box = new HBox();
            box.setPrefWidth(920);
            box.setSpacing(10);
            mainBox.setSpacing(20);
            TextField name = new TextField();
            name.setPromptText("Name");
            name.setPrefWidth(130);
            TextField surname = new TextField();
            surname.setPromptText("Surname");
            surname.setPrefWidth(130);
            Label text = new Label("Age:");
            text.setAlignment(Pos.CENTER);
            text.getStyleClass().add("no-hover");
            text.setFont(Font.font(15));
            Spinner<Integer> age = new Spinner<>();
            SpinnerValueFactory<Integer> ageValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, 1);
            age.setValueFactory(ageValueFactory);
            age.setPrefWidth(100);
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
                    String custName = name.getText().substring(0, 1).toUpperCase() + name.getText().substring(1).toLowerCase();
                    Label nameLabel = new Label(custName);
                    nameLabel.getStyleClass().add("no-hover");
                    nameLabel.setFont(Font.font(15));
                    nameLabel.setAlignment(Pos.CENTER);
                    String custSurname = surname.getText().substring(0, 1).toUpperCase() + surname.getText().substring(1).toLowerCase();
                    Label surnameLabel = new Label(custSurname);
                    surnameLabel.getStyleClass().add("no-hover");
                    surnameLabel.setFont(Font.font(15));
                    surnameLabel.setAlignment(Pos.CENTER);
                    Label ageLabel = new Label("Age: " + age.getValue());
                    ageLabel.getStyleClass().add("no-hover");
                    ageLabel.setFont(Font.font(15));
                    ageLabel.setAlignment(Pos.CENTER);
                    int ageValue = age.getValue();
                    Label confirmLabel;
                    if (ageValue < 18 || ageValue >= 60) {
                        confirmLabel = new Label(ageDiscount + "% discount!");
                        confirmLabel.getStyleClass().add("no-hover");
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
                        confirmLabel.getStyleClass().add("no-hover");
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
                    warningLabel.getStyleClass().add("no-hover");
                    warningLabel.setFont(Font.font(15));
                    warningLabel.setAlignment(Pos.CENTER);
                    warningLabel.setStyle("-fx-text-fill: red");
                    box.getChildren().addAll(warningLabel);
                }
                else if(surname.getText().isEmpty()){
                    box.getChildren().clear();
                    box.getChildren().addAll(seatLabel, name, surname, text, age, confirm);
                    Label warningLabel = new Label("Surname is required!");
                    warningLabel.getStyleClass().add("no-hover");
                    warningLabel.setFont(Font.font(15));
                    warningLabel.setAlignment(Pos.CENTER);
                    warningLabel.setStyle("-fx-text-fill: red");
                    box.getChildren().addAll(warningLabel);
                }
                else if(name.getText().isEmpty()){
                    box.getChildren().clear();
                    box.getChildren().addAll(seatLabel, name, surname, text, age, confirm);
                    Label warningLabel = new Label("Name is required!");
                    warningLabel.getStyleClass().add("no-hover");
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
                    int extrasId = res.getInt("id");
                    Image extrasImage = new Image(getClass().getResourceAsStream(extrasPhotoPath));
                    extrasInfo extra = new extrasInfo(extrasName, extrasPrice, 0,extrasStock, extrasImage,extrasId);
                    soldExtras.add(extra);
                    HBox extras = new HBox();
                    extras.setSpacing(10);

                    ImageView extrasView = new ImageView(extrasImage);
                    extrasView.setFitWidth(50); // Set width for the poster
                    extrasView.setFitHeight(50);
                    extrasView.setPreserveRatio(true);
                    Label priceLabel = new Label("Price: " + extrasPrice + "₺ + tax");
                    priceLabel.getStyleClass().add("no-hover");
                    priceLabel.setFont(Font.font(15));
                    priceLabel.setAlignment(Pos.CENTER);
                    Label nameLabel = new Label(extrasName.substring(0, 1).toUpperCase() + extrasName.substring(1));
                    nameLabel.getStyleClass().add("no-hover");
                    nameLabel.setFont(Font.font(15));
                    nameLabel.setAlignment(Pos.CENTER);
                    Label textLabel = new Label("Quantity: ");
                    textLabel.getStyleClass().add("no-hover");
                    textLabel.setFont(Font.font(15));
                    textLabel.setAlignment(Pos.CENTER);
                    Spinner<Integer> quantity = new Spinner<>();
                    SpinnerValueFactory<Integer> extrasValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, extrasStock, 0);
                    quantity.setValueFactory(extrasValueFactory);
                    quantity.setEditable(true);
                    spinners.add(quantity);
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
    public void confirm() throws FileNotFoundException {
        if(selectedSeats.size() == seatsToSell.size()) {
            for(Spinner<Integer> spinner: spinners) {
                spinner.setDisable(true);
            }
            confirmButton.setVisible(false);
            confirmButton.setManaged(false);
            paymentLabel.setStyle("-fx-text-fill: red");
            paymentLabel.setText("Waiting For The Payment");
            paymentLabel.setLayoutX(489);
            addPaymentButton();
            createTicket();
        }
        else{
            paymentLabel.setStyle("-fx-text-fill: red");
            paymentLabel.setText("Confirm All The Seats Before Confirming The Selections");
            paymentLabel.setLayoutX(276);
        }
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

    private static void createTicket() throws FileNotFoundException {

    }

    private void createInvoice(){

    }

    public void addPaymentButton(){
        Button payButton = new Button("Approve the Payment");
        payButton.setAlignment(Pos.CENTER);
        payButton.setOnAction(e -> {
            String query1 = "INSERT INTO completed_sells (ticket_count, extras_count, total_price, total_tax, ticket, invoice) VALUES (?,?,?,?,?,?)";
            String query2 = "INSERT INTO sold_seats (schedule_id, seat_row, seat_col, customer_name, customer_age, sell_id) VALUES (?,?,?,?,?,?)";
            String query3 = "INSERT INTO sold_extras (extras_id, extras_count, completed_sell_id) VALUES (?,?,?)";
            try(Connection connection = Main.getConnection();
                PreparedStatement preparedStatement1 = connection.prepareStatement(query1, PreparedStatement.RETURN_GENERATED_KEYS);
                PreparedStatement preparedStatement2 = connection.prepareStatement(query2);
                PreparedStatement preparedStatement3 = connection.prepareStatement(query3);
                FileInputStream invoice = new FileInputStream("invoice.pdf");
                FileInputStream ticket = new FileInputStream("ticket.pdf");
            ) {
                preparedStatement1.setInt(1, seatsToSell.size());
                int extras_count = 0;
                for(extrasInfo extra : ageConfirmation.soldExtras){
                    extras_count += extra.extrasCount;
                }
                preparedStatement1.setInt(2, extras_count);
                preparedStatement1.setDouble(3, Double.parseDouble(totalPrice.getText().substring(13).replace(",", ".")));
                preparedStatement1.setDouble(4, ageConfirmation.total_tax);
                preparedStatement1.setBlob(5, ticket);
                preparedStatement1.setBlob(6, invoice);
                int sell_id = 0;
                preparedStatement1.executeUpdate();
                try(ResultSet resultSet = preparedStatement1.getGeneratedKeys()){
                    if(resultSet.next()){
                        sell_id = resultSet.getInt(1);
                    }
                }
                for(SeatInfo seat : ageConfirmation.seatsToSell){
                    preparedStatement2.setInt(1, seatSelection.session_id);
                    preparedStatement2.setString(2, seat.seatName.substring(0,1));
                    preparedStatement2.setString(3, seat.seatName.substring(1));
                    preparedStatement2.setString(4, seat.customerName);
                    preparedStatement2.setInt(5, seat.customerAge);
                    preparedStatement2.setInt(6,sell_id);
                    preparedStatement2.addBatch();
                }
                preparedStatement2.executeBatch();

                for(extrasInfo extras : ageConfirmation.soldExtras){
                    if(extras.extrasCount != 0) {
                        preparedStatement3.setInt(1, extras.extras_id);
                        preparedStatement3.setInt(2, extras.extrasCount);
                        preparedStatement3.setInt(3, sell_id);
                        preparedStatement3.addBatch();
                    }
                }
                preparedStatement3.executeBatch();

            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            } catch (FileNotFoundException ex) {
                throw new RuntimeException(ex);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        agePane.getChildren().add(payButton);
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
    Image extrasImage;
    int extras_id;

    public extrasInfo(String extrasName, int extrasPrice, int extrasCount, int extrasStock, Image extrasImage, int extras_id) {
        this.extrasName = extrasName;
        this.extrasPrice = extrasPrice;
        this.extrasCount = extrasCount;
        this.extrasStock = extrasStock;
        this.extrasImage = extrasImage;
        this.extras_id = extras_id;
    }
}