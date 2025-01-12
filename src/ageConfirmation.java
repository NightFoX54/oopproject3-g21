import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.border.DashedBorder;
import com.itextpdf.layout.border.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.image.ImageView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.*;
import java.text.DecimalFormat;
import java.time.LocalDate;
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
    public int discount;
    public static TicketInvoiceDisplay thirdController;
    @FXML
    public Button buttons5;
    @FXML
    public Button buttons4;
    @FXML
    public Button buttons3;
    @FXML
    public Button buttons2;
    @FXML
    public Button buttons1;
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
    AnchorPane mainPane;
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
                discount = rs2.getInt("percentage");
            }


        } catch (SQLException e) {
            throw new RuntimeException(e);
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
        movieBox.setSpacing(5);


        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(200);
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
                        confirmLabel = new Label(discount + "% discount!");
                        confirmLabel.getStyleClass().add("no-hover");
                        confirmLabel.setFont(Font.font(15));
                        confirmLabel.setAlignment(Pos.CENTER);
                        total_tax += ticketPrice * (1.0 - (double)discount /100) * ticketTax;
                        df.format(total_tax);
                        total_price += ticketPrice * (1.0 - (double)discount /100) * (1 + ticketTax);
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
                    SeatInfo info = new SeatInfo(seat,custName + " " + custSurname, age.getValue(), ticketPrice);
                    seatsToSell.add(info);
                    secondController.updateTickets(seatsToSell,selectedSeats,discount);
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
            secondController.updateTickets(null,selectedSeats,discount);
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
                    extrasView.setFitWidth(50);
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
    public void confirm() throws FileNotFoundException, MalformedURLException {
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

    private void finishSession(ActionEvent e) throws IOException {
        secondController.finishSession();
        Parent root = FXMLLoader.load(getClass().getResource("sinema.fxml"));
        Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

    }

    private void createTicket(String ticketNo, String date) throws FileNotFoundException, MalformedURLException {
        Paragraph space = new Paragraph("\n");
        String path = "ticket.pdf";
        PdfWriter pdfWriter = new PdfWriter(path);
        PdfDocument invoice = new PdfDocument(pdfWriter);
        invoice.setDefaultPageSize(PageSize.A4);
        Document document = new Document(invoice);
        float threeCol = 190f;
        float twoCol1 = 285f;
        float twoCol2 = 435f;
        float twoCols[] = {twoCol2, twoCol1};
        float fullWidth[] = {threeCol*3};
        float threeCols[] = {threeCol,threeCol,threeCol};



        Table table = new Table(twoCols);

        table.addCell(new Cell().add("Ticket").setFontSize(20f).setBorder(Border.NO_BORDER).setBold());
        Table table2 = new Table(new float[]{twoCol1 /2, twoCol1 /2});
        table2.addCell(new Cell().add("Ticket No:").setBorder(Border.NO_BORDER).setBold().setTextAlignment(TextAlignment.RIGHT));
        table2.addCell(new Cell().add(ticketNo).setBorder(Border.NO_BORDER));
        table2.addCell(new Cell().add("Ticket Date:").setBorder(Border.NO_BORDER).setBold().setTextAlignment(TextAlignment.RIGHT));
        table2.addCell(new Cell().add(date).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(table2).setBorder(Border.NO_BORDER));

        document.add(table);
        document.add(space);

        Border border = new SolidBorder(Color.GRAY,2f);
        Table divider = new Table(fullWidth);
        divider.setBorder(border);

        document.add(divider);
        document.add(space);

        ImageData imageData = ImageDataFactory.create("src\\" + posterPath);
        com.itextpdf.layout.element.Image image = new com.itextpdf.layout.element.Image(imageData);
        image.setMaxHeight(200.0F);
        Table movie = new Table(new float[]{150.0F, 250.0F});

        movie.addCell(new Cell().add(image).setBorder(Border.NO_BORDER));


        Table movieDetails = new Table(new float[]{twoCol2});
        movieDetails.addCell(new Cell().add("Movie Name: " + movieName).setBorder(Border.NO_BORDER));
        movieDetails.addCell(new Cell().add("Hall Name: " + hallName).setBorder(Border.NO_BORDER));
        movieDetails.addCell(new Cell().add("Session Date: " + sessionDate).setBorder(Border.NO_BORDER));
        movieDetails.addCell(new Cell().add("Session Time: " + sessionTime).setBorder(Border.NO_BORDER));

        movie.addCell(new Cell().add(movieDetails).setBorder(Border.NO_BORDER));
        document.add(movie);
        document.add(divider);
        document.add(space);
        Paragraph productsP = new Paragraph("Seats");

        document.add(productsP.setBold());

        Table seatsLabelTable = new Table(threeCols);
        seatsLabelTable.setBackgroundColor(Color.BLACK, 0.7f);

        seatsLabelTable.addCell(new Cell().add("Seat Number").setBold().setFontColor(Color.WHITE).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT));
        seatsLabelTable.addCell(new Cell().add("Seat Holder Name").setBold().setFontColor(Color.WHITE).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER));
        seatsLabelTable.addCell(new Cell().add("Age Discount").setBold().setFontColor(Color.WHITE).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT));

        document.add(seatsLabelTable);

        Table seatsTable = new Table(threeCols);
        for(SeatInfo seat : seatsToSell){
            seatsTable.addCell(new Cell().add(seat.seatName).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT));
            seatsTable.addCell(new Cell().add(seat.customerName).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER));
            if(seat.customerAge < 18 || seat.customerAge >= 60)
                seatsTable.addCell(new Cell().add("YES").setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT));
            else
                seatsTable.addCell(new Cell().add("NO").setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT));
        }

        Table divider2 =new Table(fullWidth);
        Border dashed = new DashedBorder(Color.GRAY,0.5f);
        document.add(seatsTable);

        document.add(divider2.setBorder(dashed));
        Table warning =new Table(fullWidth);
        warning.addCell(new Cell().add("Warning").setBold().setBorder(Border.NO_BORDER));
        warning.addCell(new Cell().add("Seat Holders occupying age-discounted seats must present valid identification to verify eligibility for the discount (e.g., ID card, passport, or other government-issued proof of age). Failure to provide the required identification upon request may result in the ticket being deemed invalid, and additional charges may apply.").setBorder(Border.NO_BORDER));
        warning.addCell(new Cell().add("Thank you for your understanding and cooperation.").setBorder(Border.NO_BORDER));

        document.add(warning);
        document.close();
    }

    private void createInvoice(String date, String invoiceNo) throws FileNotFoundException, MalformedURLException {
        Paragraph space = new Paragraph("\n");
        String path = "invoice.pdf";
        PdfWriter pdfWriter = new PdfWriter(path);
        PdfDocument invoice = new PdfDocument(pdfWriter);
        invoice.setDefaultPageSize(PageSize.A4);
        Document document = new Document(invoice);
        float fourCol = 143f;
        float twoCol1 = 285f;
        float twoCol2 = 435f;
        float twoCols[] = {twoCol2, twoCol1};
        float fullWidth[] = {fourCol*4};
        float fourCols[] = {fourCol,fourCol,fourCol,fourCol};



        Table table = new Table(twoCols);

        table.addCell(new Cell().add("Invoice").setFontSize(20f).setBorder(Border.NO_BORDER).setBold());
        Table table2 = new Table(new float[]{twoCol1 /2, twoCol1 /2});
        table2.addCell(new Cell().add("Invoice No:").setBorder(Border.NO_BORDER).setBold().setTextAlignment(TextAlignment.RIGHT));
        table2.addCell(new Cell().add(invoiceNo).setBorder(Border.NO_BORDER));
        table2.addCell(new Cell().add("Invoice Date:").setBorder(Border.NO_BORDER).setBold().setTextAlignment(TextAlignment.RIGHT));
        table2.addCell(new Cell().add(date).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(table2).setBorder(Border.NO_BORDER));

        document.add(table);
        document.add(space);

        Border border = new SolidBorder(Color.GRAY,2f);
        Table divider = new Table(fullWidth);
        divider.setBorder(border);

        document.add(divider);
        document.add(space);

        Paragraph productsP = new Paragraph("Products");

        document.add(productsP.setBold());

        Table productsLabelTable = new Table(fourCols);
        productsLabelTable.setBackgroundColor(Color.BLACK, 0.7f);

        productsLabelTable.addCell(new Cell().add("Description").setBold().setFontColor(Color.WHITE).setBorder(Border.NO_BORDER));
        productsLabelTable.addCell(new Cell().add("Quantity").setBold().setFontColor(Color.WHITE).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER));
        productsLabelTable.addCell(new Cell().add("Price").setBold().setFontColor(Color.WHITE).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT));
        productsLabelTable.addCell(new Cell().add("Tax").setBold().setFontColor(Color.WHITE).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT));

        document.add(productsLabelTable);

        Table productsTable = new Table(fourCols);
        double totalPrice = 0;
        int seatQuantity = 0;
        double seatPrice = 0;
        double seatTax = 0;
        seatQuantity = seatsToSell.size();
        for(SeatInfo seat : seatsToSell){
            if(seat.customerAge < 18 || seat.customerAge >= 60){
                seatPrice += seat.price * (1.0 - (double)discount / 100);
                seatTax += seat.price * (1.0 - (double)discount / 100) * ticketTax;
            }
            else{
                seatPrice += seat.price;
                seatTax += seat.price * ticketTax;
            }
        }
        totalPrice += seatPrice + seatTax;
        productsTable.addCell(new Cell().add("Ticket").setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT));
        productsTable.addCell(new Cell().add(String.valueOf(seatQuantity)).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER));
        productsTable.addCell(new Cell().add(String.valueOf(seatPrice)).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT));
        productsTable.addCell(new Cell().add(String.valueOf(seatTax)).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT));

        for(extrasInfo extra : soldExtras){
            if(extra.extrasCount > 0) {
                productsTable.addCell(new Cell().add(extra.extrasName).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT));
                productsTable.addCell(new Cell().add(String.valueOf(extra.extrasCount)).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER));
                productsTable.addCell(new Cell().add(String.valueOf((double) extra.extrasPrice * (double) extra.extrasCount)).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT));
                productsTable.addCell(new Cell().add(String.valueOf((double) extra.extrasPrice * extrasTax * (double) extra.extrasCount)).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT));
                totalPrice += (double) extra.extrasPrice * (double) extra.extrasCount * (1.0 + extrasTax);
            }
        }

        Table divider2 =new Table(fullWidth);
        Border dashed = new DashedBorder(Color.GRAY,0.5f);
        document.add(productsTable.setMarginBottom(20f));
        float priceDivider[] = {fourCol*2, fourCol*2};
        Table priceDividerTable = new Table(priceDivider);
        priceDividerTable.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
        priceDividerTable.addCell(new Cell().add(divider2.setBorder(dashed)).setBorder(Border.NO_BORDER));
        document.add(priceDividerTable);
        Table totalPriceTable = new Table(fourCols);

        totalPriceTable.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
        totalPriceTable.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
        totalPriceTable.addCell(new Cell().add("Total Price With Tax").setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT));
        DecimalFormat decimalFormat = new DecimalFormat(".#");
        String formatted = decimalFormat.format(totalPrice);
        totalPriceTable.addCell(new Cell().add(formatted).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT));
        document.add(totalPriceTable);
        document.add(divider2);



        document.close();
    }

    public void addPaymentButton(){
        Button payButton = new Button("Approve the Payment");
        payButton.setOnAction(e -> {
            Collections.sort(selectedSeats);
            String query1 = "INSERT INTO completed_sells (ticket_count, extras_count, total_price, total_tax, sell_date) VALUES (?,?,?,?,?)";
            String query2 = "INSERT INTO sold_seats (schedule_id, seat_row, seat_col, customer_name, customer_age, sell_id) VALUES (?,?,?,?,?,?)";
            String query3 = "INSERT INTO sold_extras (extras_id, extras_count, completed_sell_id) VALUES (?,?,?)";
            String query4 = "UPDATE completed_sells SET ticket = ?, invoice = ? WHERE sell_id = ?";
            try(Connection connection = Main.getConnection();
                PreparedStatement preparedStatement1 = connection.prepareStatement(query1, PreparedStatement.RETURN_GENERATED_KEYS);
                PreparedStatement preparedStatement2 = connection.prepareStatement(query2);
                PreparedStatement preparedStatement3 = connection.prepareStatement(query3);
                PreparedStatement preparedStatement4 = connection.prepareStatement(query4);

            ) {
                LocalDate date = LocalDate.now();
                preparedStatement1.setInt(1, seatsToSell.size());
                int extras_count = 0;
                for(extrasInfo extra : ageConfirmation.soldExtras){
                    extras_count += extra.extrasCount;
                }
                preparedStatement1.setInt(2, extras_count);
                preparedStatement1.setDouble(3, Double.parseDouble(totalPrice.getText().substring(13).replace(",", ".")));
                preparedStatement1.setDouble(4, ageConfirmation.total_tax);
                preparedStatement1.setString(5, date.toString());


                int sell_id = 0;
                preparedStatement1.executeUpdate();
                try(ResultSet resultSet = preparedStatement1.getGeneratedKeys()){
                    if(resultSet.next()){
                        sell_id = resultSet.getInt(1);
                    }
                }
                createInvoice(date.toString(), String.valueOf(sell_id));
                createTicket(date.toString(), String.valueOf(sell_id));
                FileInputStream invoice = new FileInputStream("invoice.pdf");
                FileInputStream ticket = new FileInputStream("ticket.pdf");
                preparedStatement4.setBlob(1, ticket);
                preparedStatement4.setBlob(2, invoice);
                preparedStatement4.setInt(3, sell_id);
                preparedStatement4.executeUpdate();
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
                secondController.displayTicket();
                mainPane.getChildren().remove(buttons1);
                mainPane.getChildren().remove(buttons2);
                mainPane.getChildren().remove(buttons3);
                mainPane.getChildren().remove(buttons4);
                mainPane.getChildren().remove(buttons5);
                mainPane.getChildren().remove(payButton);
                Button finishButton = new Button("Finish The Session");
                finishButton.setOnAction(event -> {
                    try {
                        finishSession(event);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                });
                mainPane.getChildren().add(finishButton);
                finishButton.setLayoutX(1012);
                finishButton.setLayoutY(735);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            } catch (FileNotFoundException ex) {
                throw new RuntimeException(ex);
            } catch (MalformedURLException ex) {
                throw new RuntimeException(ex);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        mainPane.getChildren().add(payButton);
        payButton.setLayoutX(1012);
        payButton.setLayoutY(735);
    }


}



class SeatInfo{
    String seatName;
    String customerName;
    int customerAge;
    int price;

    public SeatInfo(String seatName, String customerName, int customerAge, int price) {
        this.seatName = seatName;
        this.customerName = customerName;
        this.customerAge = customerAge;
        this.price = price;
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