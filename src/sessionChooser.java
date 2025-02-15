import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.sql.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * The sessionChooser class is responsible for managing the movie session selection process.
 * It displays the movie details and available sessions to the user and handles navigation
 * to related screens like seat selection or search.
 *
 * <p>Dependencies:</p>
 * <ul>
 *     <li>JavaFX components (e.g., TableView, Button, FlowPane, etc.)</li>
 *     <li>Database connection for fetching session data</li>
 *     <li>External FXML files for UI transitions</li>
 * </ul>
 */
public class sessionChooser {
    /**
     * The name of the movie currently selected.
     */
    public static String movieName;

    /**
     * The genre of the movie currently selected.
     */
    public static String genre;

    /**
     * A brief summary of the movie.
     */
    public static String summary;

    /**
     * The path to the poster image for the movie.
     */
    public static String posterPath;

    /**
     * The unique identifier of the movie in the database.
     */
    public static int movieId;

    /**
     * A reference to the CustomerSession controller for managing interactions.
     */
    public static CustomerSession secondController;

    /**
     * A list of session objects representing the available sessions for the selected movie.
     */
    ObservableList<session> sessionList = FXCollections.observableArrayList();

    /**
     * Represents a session with its attributes such as hall name, date, time, and a button for seat selection.
     */
    public static class session {
        private final SimpleStringProperty hall_name;
        private final SimpleStringProperty schedule_date;
        private final SimpleStringProperty start_time;
        private final SimpleStringProperty hall_capacity;
        private final SimpleStringProperty available_seats;
        private final SimpleObjectProperty<Button> select_button;

         /**
         * Constructs a session object with the given parameters.
         *
         * @param hall_name       The name of the hall where the session is held.
         * @param schedule_date   The date of the session.
         * @param start_time      The start time of the session.
         * @param hall_capacity   The total capacity of the hall.
         * @param available_seats The number of available seats for the session.
         * @param select_button   A button to trigger seat selection for this session.
         */
        public session(String hall_name, String schedule_date, String start_time, String hall_capacity, String available_seats, Button select_button) {
            this.hall_name = new SimpleStringProperty(hall_name);
            this.schedule_date = new SimpleStringProperty(schedule_date);
            this.start_time = new SimpleStringProperty(start_time);
            this.hall_capacity = new SimpleStringProperty(hall_capacity);
            this.available_seats = new SimpleStringProperty(available_seats);
            this.select_button = new SimpleObjectProperty<>(select_button);
        }

        /**
         * Gets the hall name.
         * 
         * @return The name of the hall.
         */
        public String getHallName() {
            return hall_name.get();
        }

        /**
         * Gets the schedule date of the session.
         * 
         * @return The schedule date.
         */
        public String getScheduleDate() {
            return schedule_date.get();
        }

        /**
         * Gets the start time of the session.
         * 
         * @return The start time.
         */
        public String getStartTime() {
            return start_time.get();
        }

        /**
         * Gets the hall capacity.
         * 
         * @return The hall capacity.
         */
        public String getHallCapacity() {
            return hall_capacity.get();
        }

        /**
         * Gets the number of available seats.
         * 
         * @return The number of available seats.
         */
        public String getAvailableSeats() {
            return available_seats.get();
        }

        /**
         * Gets the button for selecting seats.
         * 
         * @return The select seats button.
         */
        public Button getSelectButton() {
            return select_button.get();
        }

    }


    @FXML
    private Label name;
    @FXML
    FlowPane sessionFlowPane;
    /**
     * Navigates to the password change screen.
     *
     * @param e The ActionEvent triggered by the user.
     * @throws IOException If the FXML file for the password change screen cannot be loaded.
     */
    @FXML
    private void changePassword(ActionEvent e) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("passChange.fxml"));
        Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        passChangeCont.prevPage = "sessionDecision.fxml";
        passChangeCont.stage2 = secondController.stage;
    }

    /**
     * Logs out the current user and navigates to the login screen.
     *
     * @param e The ActionEvent triggered by the user.
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
        MovieController.secondController = null;
    }

    /**
     * Navigates to the search screen.
     *
     * @param e The ActionEvent triggered by the user.
     * @throws IOException If the FXML file for the search screen cannot be loaded.
     */
    @FXML
    private void goToSearch(ActionEvent e) throws IOException {
        secondController.goToSearch();
        Parent root = FXMLLoader.load(getClass().getResource("sinema.fxml"));
        Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }



    /**
     * Initializes the session chooser screen by setting up movie details and sessions.
     */
    @FXML
    public void initialize() {
        name.setText("Welcome " + Main.currentUser.name + " " + Main.currentUser.surname + "!");
        Label titleLabel = new Label("Title: " + movieName);
        titleLabel.getStyleClass().add("no-hover");
        Label genreLabel = new Label("Genre: " + genre);
        genreLabel.getStyleClass().add("no-hover");
        Label summaryLabel = new Label("Summary: " + summary);
        summaryLabel.getStyleClass().add("no-hover");
        Image image = new Image(getClass().getResourceAsStream(posterPath));
        VBox textBox = new VBox();
        textBox.setSpacing(5);
        HBox movieBox = new HBox();
        movieBox.setSpacing(5);


        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(210);
        imageView.setFitHeight(280);
        imageView.setPreserveRatio(true);


        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        genreLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        summaryLabel.setStyle("-fx-font-size: 14px;");
        summaryLabel.setWrapText(true);



        textBox.getChildren().addAll(titleLabel, genreLabel, summaryLabel);
        movieBox.getChildren().addAll(imageView,textBox);
        movieBox.setPrefWidth(1020);
        movieBox.setPrefHeight(280);


        sessionFlowPane.getChildren().add(movieBox);
        secondController.showMovie(movieName,genre,summary,posterPath);
        showSessions();
    }

    /**
     * Displays the available sessions for the selected movie and sets up pagination.
     */
    public void showSessions(){
        TableView<session> sessionTable = createSessionTable(true);
        Pagination pagination = new Pagination();
        int itemsPerPage = 9;

        secondController.table = createSessionTable(false);
        String query = "SELECT * FROM sessions WHERE movie_id = " + movieId + " ORDER BY schedule_date ASC, start_time ASC";
        try(Connection connection = Main.getConnection();
            Statement statement = connection.createStatement();
            ResultSet res = statement.executeQuery(query);){

            while(res.next()){
                int sessionId = res.getInt("schedule_id");
                String hall_id = res.getString("hall_id");
                String schedule_date = res.getString("schedule_date");
                String start_time = res.getString("start_time");
                String available_seats = res.getString("available_seats");

                query = "SELECT * FROM halls WHERE hall_id = " + hall_id;
                Statement statement2 = connection.createStatement();
                ResultSet res2 = statement2.executeQuery(query);
                res2.next();
                String hall_name = res2.getString("name");
                String hall_capacity = res2.getString("capacity");
                int seat_rows = res2.getInt("seat_row_count");
                int seat_cols = res2.getInt("seat_col_count");
                Button selectSeatsButton = new Button("Select Seats");
                selectSeatsButton.setOnAction((ActionEvent e) -> {
                    seatSelection.sessionDate = schedule_date;
                    seatSelection.hallName = hall_name;
                    seatSelection.session_id = sessionId;
                    seatSelection.sessionTime = start_time;
                    seatSelection.movieName = movieName;
                    seatSelection.posterPath = posterPath;
                    seatSelection.cols = seat_cols;
                    seatSelection.rows = seat_rows;
                    Parent root = null;
                    try {
                        secondController.goToSeatSelection();
                        root = FXMLLoader.load(getClass().getResource("seatSelection.fxml"));
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                    Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
                    Scene scene = new Scene(root);
                    stage.setScene(scene);
                    stage.show();
                });
                sessionList.add(new session(hall_name, schedule_date, start_time, hall_capacity, available_seats, selectSeatsButton));
            }
            pagination.setPageCount((int) Math.ceil(sessionList.size() / (double) itemsPerPage));
            int pageCount = pagination.getPageCount();
            pagination.setCurrentPageIndex(0);
            pagination.setMaxPageIndicatorCount(pageCount);
            pagination.currentPageIndexProperty().addListener((observable, oldValue, newValue) -> {
                updateTableData(newValue.intValue(), sessionTable, itemsPerPage);
                updateTableData(newValue.intValue(),secondController.table, itemsPerPage);
            });
            updateTableData(0, sessionTable, itemsPerPage);
            updateTableData(0,secondController.table, itemsPerPage);
            secondController.table.setRowFactory(tableView -> {
                TableRow<session> row = new TableRow<>();
                row.heightProperty().addListener((observable, oldValue, newValue) -> {
                    row.setPrefHeight(48); // You can change 100 to any value that fits your content
                });
                return row;
            });
            //sessionTable.setItems(sessionList);
            secondController.showSession();
            sessionFlowPane.getChildren().add(sessionTable);
            sessionFlowPane.getChildren().add(pagination);


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Updates the data displayed in the session table for the given page index.
     *
     * @param pageIndex     The index of the page to display.
     * @param sessionTable  The TableView to update.
     * @param itemsPerPage  The number of items to display per page.
     */
    private void updateTableData(int pageIndex, TableView<session> sessionTable, int itemsPerPage) {
        int fromIndex = pageIndex * itemsPerPage;
        int toIndex = Math.min(fromIndex + itemsPerPage, sessionList.size());

        ObservableList<session> pageData = FXCollections.observableArrayList(
                sessionList.subList(fromIndex, toIndex)
        );

        sessionTable.setItems(pageData);
    }

    /**
     * Creates a TableView for displaying session data.
     *
     * @param includeButton Whether to include the "Select Seats" button column.
     * @return A configured TableView for session data.
     */
    private TableView<session> createSessionTable(boolean includeButton) {
        TableView<session> sessionTable = new TableView<>();


        TableColumn<session, String> hall_names = new TableColumn<>("Hall Name");
        hall_names.setCellValueFactory(cellData -> cellData.getValue().hall_name);
        hall_names.setPrefWidth(232);

        TableColumn<session, String> date_col = new TableColumn<>("Schedule Date");
        date_col.setCellValueFactory(cellData -> cellData.getValue().schedule_date);
        date_col.setPrefWidth(232);

        TableColumn<session, String> time_col = new TableColumn<>("Start Time");
        time_col.setCellValueFactory(cellData -> cellData.getValue().start_time);
        time_col.setPrefWidth(232);

        TableColumn<session, String> capacity_col = new TableColumn<>("Hall Capacity");
        capacity_col.setCellValueFactory(cellData -> cellData.getValue().hall_capacity);
        capacity_col.setPrefWidth(232);

        TableColumn<session, String> seats_col = new TableColumn<>("Available Seats");
        seats_col.setCellValueFactory(cellData -> cellData.getValue().available_seats);
        seats_col.setPrefWidth(232);


        TableColumn<session, Button> button_col = null;
        if (includeButton) {
            hall_names.setPrefWidth(165);
            date_col.setPrefWidth(165);
            time_col.setPrefWidth(165);
            capacity_col.setPrefWidth(165);
            seats_col.setPrefWidth(165);
            button_col = new TableColumn<>("");
            button_col.setCellValueFactory(cellData -> cellData.getValue().select_button);
            button_col.setPrefWidth(165);

            button_col.setCellFactory(param -> new TableCell<session, Button>() {
                @Override
                protected void updateItem(Button item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(item);
                        setStyle("-fx-alignment: center;");
                    }
                }
            });
        }

        sessionTable.setPrefWidth(1190);
        sessionTable.setPrefHeight(465);

        sessionTable.getColumns().addAll(hall_names, date_col, time_col, capacity_col, seats_col);
        if (includeButton) {
            sessionTable.getColumns().add(button_col);
            sessionTable.setPrefWidth(1020);
            sessionTable.setPrefHeight(415);
        }



        hall_names.setCellFactory(createCenterAlignedCellFactory());
        date_col.setCellFactory(createCenterAlignedCellFactory());
        time_col.setCellFactory(createCenterAlignedCellFactory());
        capacity_col.setCellFactory(createCenterAlignedCellFactory());
        seats_col.setCellFactory(createCenterAlignedCellFactory());

        return sessionTable;
    }


    /**
     * Creates a cell factory for aligning cell content to the center.
     *
     * @param <T> The type of data in the table column.
     * @return A Callback for creating TableCell instances with center alignment.
     */
    private <T> Callback<TableColumn<session, T>, TableCell<session, T>> createCenterAlignedCellFactory() {
        return column -> new TableCell<session, T>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(item != null ? item.toString() : "");
                    setStyle("-fx-alignment: center;");
                }
            }
        };
    }
}
