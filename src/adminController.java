import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class adminController {
    @FXML
    public Label userInfoLabel;
    @FXML
    public TableView<movieDetails> movieTable;
    @FXML
    public TableColumn<movieDetails,String> moviePosterColumn;
    @FXML
    public TableColumn<movieDetails,String> movieNameColumn;
    @FXML
    public TableColumn<movieDetails,String> movieGenreColumn;
    @FXML
    public TableColumn<movieDetails,String> movieSummaryColumn;
    @FXML
    public TableColumn<movieDetails,Button> updateColumn;
    @FXML
    public TableColumn<movieDetails,Boolean> deleteColumn;
    @FXML
    public TableColumn<sessionDetails,Integer> sessionIdColumn;
    @FXML
    public TableColumn<sessionDetails,String> movieNameColumn2;
    @FXML
    public TableColumn<sessionDetails,String> hallNameColumn;
    @FXML
    public TableColumn<sessionDetails,String> scheduleDateColumn;
    @FXML
    public TableColumn<sessionDetails,String> scheduleTimeColumn;
    @FXML
    public TableColumn<sessionDetails,Boolean> deleteColumn2;
    @FXML
    public TableView<sessionDetails> sessionTable;
    @FXML
    public AnchorPane updatePane1;
    @FXML
    public ChoiceBox<String> movieNameChoice;
    @FXML
    public ChoiceBox<String> hallNameChoice;
    @FXML
    public DatePicker scheduleDateChoice;
    @FXML
    public ChoiceBox<String> scheduleTimeChoice;
    @FXML
    public Button saveScheduleButton;
    @FXML
    public Label dateText;
    @FXML
    public Label hallText;
    @FXML
    public Label timeText;

    private int updateMovieId = 0;
    private String updateMoviePoster = "";
    private final ObservableList<sessionDetails> sessionList = FXCollections.observableArrayList();
    private final ObservableList<movieDetails> movieList = FXCollections.observableArrayList();
    public AnchorPane updatePane;
    public TextField nameField;
    public TextField summaryField;
    public TextField genreField;
    public TextField posterField;

    public class CenteredTableCell<T, V> extends TableCell<T, V> {
        private final Label label = new Label();

        {
            label.getStyleClass().add("no-hover");
            label.setWrapText(true);
            label.setAlignment(Pos.CENTER);
        }
        @Override
        protected void updateItem(V item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setGraphic(null);
            } else {
                label.setText(item.toString());
                setGraphic(label);
            }
            setAlignment(Pos.CENTER); // Center the content
        }
    }

    @FXML
    public void initialize() {
        updatePane.setVisible(false);
        updatePane.setManaged(false);
        hideSessionBox();
        movieList.clear();
        moviePosterColumn.setCellValueFactory(new PropertyValueFactory<>("poster"));
        movieNameColumn.setCellValueFactory(new PropertyValueFactory<>("movieName"));
        movieGenreColumn.setCellValueFactory(new PropertyValueFactory<>("movieGenre"));
        movieSummaryColumn.setCellValueFactory(new PropertyValueFactory<>("movieSummary"));
        movieSummaryColumn.setCellFactory(column -> new TableCell<movieDetails, String>() {
            private final Label summaryLabel = new Label();

            {
                summaryLabel.getStyleClass().add("no-hover");
                summaryLabel.setWrapText(true);
                summaryLabel.setAlignment(Pos.CENTER);
            }

            @Override
            protected void updateItem(String summary, boolean empty) {
                super.updateItem(summary, empty);
                if (empty || summary == null) {
                    setGraphic(null);
                } else {
                    summaryLabel.setText(summary);
                    setGraphic(summaryLabel);
                }
                setAlignment(Pos.CENTER);
            }
        });
        updateColumn.setCellValueFactory(new PropertyValueFactory<>("updateButton"));
        deleteColumn.setCellFactory(column -> new TableCell<movieDetails,Boolean>(){
            private final Button deleteButton = new Button("Delete");
            private final Label cannotDeleteLabel = new Label("This Movie Can Not Be Deleted Since There Are Sold Tickets For This Movie");

            {
                cannotDeleteLabel.getStyleClass().add("no-hover");
                cannotDeleteLabel.setWrapText(true);
                cannotDeleteLabel.setAlignment(Pos.CENTER);
                deleteButton.setAlignment(Pos.CENTER);
            }
            @Override
            protected void updateItem(Boolean deleteColumn,boolean empty){
                super.updateItem(deleteColumn,empty);
                if (empty) {
                    setGraphic(null);
                } else if (deleteColumn) {
                    // Show the delete button for deletable movies
                    deleteButton.setOnAction(event -> {
                        movieDetails movie = getTableView().getItems().get(getIndex());
                        deleteMovie(movie.getMovieName());
                    });
                    setGraphic(deleteButton);
                    deleteButton.setAlignment(Pos.CENTER);
                } else {
                    setGraphic(cannotDeleteLabel);
                    cannotDeleteLabel.setAlignment(Pos.CENTER);
                }
                setAlignment(Pos.CENTER);
            }
        });
        deleteColumn.setCellValueFactory(new PropertyValueFactory<>("delete"));
        movieNameColumn.setCellFactory(column -> new CenteredTableCell<>());
        movieGenreColumn.setCellFactory(column -> new CenteredTableCell<>());
        updateColumn.setCellFactory(column -> new TableCell<movieDetails, Button>() {
            @Override
            protected void updateItem(Button item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    setGraphic(item);
                }
                setAlignment(Pos.CENTER); // Center align the button
            }
        });
        getMovieDetails();
        movieTable.setItems(movieList);
        setSessionTable();
    }

    public void getMovieDetails(){
        String query = "SELECT m.*,\n" +
                "    NOT EXISTS (\n" +
                "        SELECT 1\n" +
                "        FROM sessions s\n" +
                "        JOIN sold_seats ss ON s.schedule_id = ss.schedule_id\n" +
                "        WHERE s.movie_id = m.id\n" +
                "    ) AS can_be_deleted\n" +
                "FROM movies m;";
        try(Connection connection = Main.getConnection();
            ResultSet resultSet = connection.createStatement().executeQuery(query)) {
            while(resultSet.next()){
                Button updateButton = new Button();
                String movieName = resultSet.getString(2);
                String movieGenre = resultSet.getString(3);
                String movieSummary = resultSet.getString(4);
                String posterPath = resultSet.getString(5).substring(7);
                int movieId = resultSet.getInt(1);
                updateButton.setText("Update");
                updateButton.setOnAction(event -> {
                    updateMovie(movieName, movieGenre, movieSummary, posterPath, movieId);
                });
                Image image = new Image(resultSet.getString(5));
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(150);
                imageView.setPreserveRatio(true);
                movieDetails movie = new movieDetails(imageView, movieName ,resultSet.getString(3) ,resultSet.getString(4), updateButton, resultSet.getBoolean(6));
                movie.posterPath = resultSet.getString(5);
                movieList.add(movie);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateMovie(String movieName, String movieGenre, String movieSummary, String posterPath, int id){
        updatePane.setVisible(true);
        updatePane.setManaged(true);
        nameField.setText(movieName);
        genreField.setText(movieGenre);
        summaryField.setText(movieSummary);
        posterField.setText(posterPath);
        updateMovieId = id;
        updateMoviePoster = "photos/" + posterPath;
    }

    public void deleteMovie(String movieName) {
        String query = "DELETE FROM movies WHERE movieName = ?";
        try(Connection connection = Main.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, movieName);
            preparedStatement.executeUpdate();
            movieList.clear();
            getMovieDetails();
            movieTable.setItems(movieList);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void saveMovie(){
        String query = "";
        if(updateMovieId == 0)
            query = "INSERT INTO movies (movieName,genre,summary,posterLocation) VALUES(?,?,?,?);";
        else
            query = "UPDATE movies SET movieName = ?,genre = ?,summary = ?,posterLocation = ? WHERE id = ?;";
        if(nameField.getText().isEmpty() || genreField.getText().isEmpty() || summaryField.getText().isEmpty() || posterField.getText().isEmpty()){

        }
        else{
            try(Connection connection = Main.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1,nameField.getText());
                preparedStatement.setString(2,genreField.getText());
                preparedStatement.setString(3,summaryField.getText());
                String posterPath = "photos/" + posterField.getText();
                preparedStatement.setString(4,"photos/" + posterField.getText());
                if(updateMovieId != 0) {
                    preparedStatement.setInt(5, updateMovieId);
                }
                preparedStatement.executeUpdate();
                nameField.setText("");
                genreField.setText("");
                summaryField.setText("");
                posterField.setText("");
                updatePane.setVisible(false);
                updatePane.setManaged(false);
                connection.close();
                updateMovieId = 0;
                updateMoviePoster = "";
                movieList.clear();
                getMovieDetails();
                movieTable.setItems(movieList);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @FXML
    private void addMovieBox(){
        updatePane.setVisible(true);
        updatePane.setManaged(true);
        updateMovieId = 0;
        updateMoviePoster = "";
    }

    @FXML
    private void selectImageFile() throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        File selectedFile = fileChooser.showOpenDialog(new Stage());
        if (selectedFile != null) {
            File destinationFile = new File("src/photos/" + selectedFile.getName());
            File destinationFile2 = new File("out/production/oopproject3/photos/" + selectedFile.getName());
            Files.copy(selectedFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            Files.copy(selectedFile.toPath(), destinationFile2.toPath(), StandardCopyOption.REPLACE_EXISTING);
            posterField.setText(selectedFile.getName());
        }
    }

    private void getSessionDetails() {
        sessionList.clear();
        String query = "SELECT \n" +
                "    s.*,\n" +
                "    m.movieName AS movie_name,\n" +
                "    h.name AS hall_name,\n" +
                "    NOT EXISTS (\n" +
                "        SELECT 1\n" +
                "        FROM sold_seats ss\n" +
                "        WHERE ss.schedule_id = s.schedule_id\n" +
                "    ) AS can_be_deleted\n" +
                "FROM sessions s\n" +
                "JOIN movies m ON s.movie_id = m.id\n" +
                "JOIN halls h ON s.hall_id = h.hall_id\n" +
                "ORDER BY s.schedule_id ASC;";
        try(Connection connection = Main.getConnection();
            ResultSet resultSet = connection.createStatement().executeQuery(query)) {
            while(resultSet.next()){
                Button updateButton = new Button();
                int sessionId = resultSet.getInt(1);
                String scheduleDate = resultSet.getString(4);
                String scheduleTime = resultSet.getString(5);
                String movieName = resultSet.getString(7);
                String hallName = resultSet.getString(8);
                boolean canBeDeleted = resultSet.getBoolean(9);
                sessionList.add(new sessionDetails(sessionId, movieName, hallName, scheduleDate, scheduleTime, canBeDeleted));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void setSessionTable(){
        sessionIdColumn.setCellValueFactory(new PropertyValueFactory<>("sessionId"));
        sessionIdColumn.setCellFactory(column -> new CenteredTableCell<>());
        sessionIdColumn.setSortType(TableColumn.SortType.ASCENDING);
        movieNameColumn2.setCellValueFactory(new PropertyValueFactory<>("movieName"));
        movieNameColumn2.setCellFactory(column -> new CenteredTableCell<>());
        hallNameColumn.setCellValueFactory(new PropertyValueFactory<>("hallName"));
        hallNameColumn.setCellFactory(column -> new CenteredTableCell<>());
        scheduleDateColumn.setCellValueFactory(new PropertyValueFactory<>("scheduleDate"));
        scheduleDateColumn.setCellFactory(column -> new CenteredTableCell<>());
        scheduleTimeColumn.setCellValueFactory(new PropertyValueFactory<>("scheduleTime"));
        scheduleTimeColumn.setCellFactory(column -> new CenteredTableCell<>());
        deleteColumn2.setCellValueFactory(new PropertyValueFactory<>("deleteButton"));
        deleteColumn2.setCellFactory(column -> new TableCell<sessionDetails,Boolean>(){
            private final Button deleteButton = new Button("Delete");
            private final Label cannotDeleteLabel = new Label("This Movie Can Not Be Deleted Since There Are Sold Tickets For This Movie");

            {
                cannotDeleteLabel.getStyleClass().add("no-hover");
                cannotDeleteLabel.setWrapText(true);
                cannotDeleteLabel.setAlignment(Pos.CENTER);
                deleteButton.setAlignment(Pos.CENTER);
            }
            @Override
            protected void updateItem(Boolean deleteColumn,boolean empty){
                super.updateItem(deleteColumn,empty);
                if (empty) {
                    setGraphic(null);
                } else if (deleteColumn) {
                    // Show the delete button for deletable movies
                    deleteButton.setOnAction(event -> {
                        sessionDetails session = getTableView().getItems().get(getIndex());
                        deleteSession(session);
                    });
                    setGraphic(deleteButton);
                    deleteButton.setAlignment(Pos.CENTER);
                } else {
                    setGraphic(cannotDeleteLabel);
                    cannotDeleteLabel.setAlignment(Pos.CENTER);
                }
                setAlignment(Pos.CENTER);
            }
        });
        getSessionDetails();
        sessionTable.getSortOrder().add(sessionIdColumn);
        sessionTable.setItems(sessionList);
        sessionTable.sort();
    }

    private void deleteSession(sessionDetails session) {
        String query = "DELETE FROM sessions WHERE schedule_id = ?";
        try(Connection connection = Main.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1,session.getSessionId());
            preparedStatement.executeUpdate();
            getSessionDetails();
            sessionTable.setItems(sessionList);
            sessionTable.sort();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void addSessionBox(){
        updatePane1.setVisible(true);
        updatePane1.setManaged(true);
        movieNameChoice.setVisible(true);
        movieNameChoice.setManaged(true);
        for(movieDetails movie: movieList){
            movieNameChoice.getItems().add(movie.getMovieName());
        }
        ArrayList<String> hallNames = new ArrayList<>();
        String query = "SELECT * FROM halls";
        try(Connection connection = Main.getConnection();
            ResultSet resultSet = connection.createStatement().executeQuery(query)) {
            while(resultSet.next()){
                hallNames.add(resultSet.getString("name"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        movieNameChoice.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue!=null){
                hallNameChoice.setVisible(true);
                hallNameChoice.setManaged(true);
                hallText.setVisible(true);
                hallText.setManaged(true);
                hallNameChoice.getItems().clear();
                for(String hallName: hallNames){
                    hallNameChoice.getItems().add(hallName);
                }
                hallNameChoice.getSelectionModel().selectedItemProperty().addListener((observable1, oldValue1, newValue1) -> {
                    if(newValue1!=null){
                        scheduleDateChoice.setValue(null);
                        dateText.setVisible(true);
                        dateText.setManaged(true);
                        scheduleDateChoice.setVisible(true);
                        scheduleDateChoice.setManaged(true);
                        scheduleDateChoice.setDayCellFactory(dp -> new DateCell(){
                            @Override
                            public void updateItem(LocalDate date, boolean empty) {
                                super.updateItem(date, empty);

                                if (empty || date == null) {
                                    return;
                                }
                                List<String> possibleTimes = List.of(
                                        "10:00:00",
                                        "12:00:00",
                                        "14:00:00",
                                        "16:00:00",
                                        "18:00:00",
                                        "20:00:00",
                                        "22:00:00"
                                );
                                // Check if all session times are booked for this date
                                boolean fullyBooked = sessionList.stream()
                                        .filter(session -> session.getScheduleLocalDate().equals(date))
                                        .filter(session -> session.getHallName().equals(hallNameChoice.getValue()))
                                        .map(sessionDetails::getScheduleTime)
                                        .collect(Collectors.toSet())
                                        .containsAll(possibleTimes);
                                if (fullyBooked) {
                                    setDisable(true);
                                    setStyle("-fx-background-color: #ffcccc;"); // Optional: color fully booked dates
                                }
                                if(date.isBefore(LocalDate.now())){
                                    setDisable(true);
                                    setStyle("-fx-background-color: #ffcccc;");
                                }
                                if(date.isEqual(LocalDate.now())){
                                    setDisable(true);
                                    setStyle("-fx-background-color: #ffcccc;");
                                }
                            }
                        });
                        List<String> possibleTimes = new ArrayList<>(List.of(
                                "10:00:00",
                                "12:00:00",
                                "14:00:00",
                                "16:00:00",
                                "18:00:00",
                                "20:00:00",
                                "22:00:00"
                        ));
                        // Check if all session times are booked for this date
                        List<String> bookedTimes = sessionList.stream()
                                .filter(session -> session.getScheduleLocalDate().equals(scheduleDateChoice.getValue()))
                                .filter(session -> session.getHallName().equals(hallNameChoice.getValue()))
                                .map(sessionDetails::getScheduleTime)
                                .toList();

                        possibleTimes.removeAll(bookedTimes);
                        scheduleDateChoice.valueProperty().addListener((observable2, oldValue2, newValue2) -> {
                            if(newValue2!=null){
                                timeText.setVisible(true);
                                timeText.setManaged(true);
                                scheduleTimeChoice.setVisible(true);
                                scheduleTimeChoice.setManaged(true);
                                scheduleTimeChoice.getItems().clear();
                                scheduleTimeChoice.getItems().addAll(possibleTimes);
                                scheduleTimeChoice.valueProperty().addListener((observable3, oldValue3, newValue3) -> {
                                    if(newValue3!=null){
                                        saveScheduleButton.setVisible(true);
                                        saveScheduleButton.setManaged(true);
                                        saveScheduleButton.setOnAction(event -> {
                                            saveSchedule(movieNameChoice.getValue(), hallNameChoice.getValue(), scheduleDateChoice.getValue().toString(), scheduleTimeChoice.getValue());
                                        });
                                    }
                                    else{
                                        saveScheduleButton.setVisible(false);
                                        saveScheduleButton.setManaged(false);
                                    }
                                });
                            }
                            else{
                                timeText.setVisible(false);
                                timeText.setManaged(false);
                                scheduleTimeChoice.setVisible(false);
                                scheduleTimeChoice.setManaged(false);
                            }
                        });
                    }
                    else{
                        scheduleDateChoice.setValue(null);
                        dateText.setVisible(false);
                        dateText.setManaged(false);
                        scheduleDateChoice.setVisible(false);
                        scheduleDateChoice.setManaged(false);
                    }
                });
            }
        });
    }

    public void saveSchedule(String movieName, String hallName, String scheduleDate, String scheduleTime) {

        String query = "INSERT INTO sessions (movie_id, hall_id, schedule_date, start_time, available_seats)\n" +
                "SELECT\n" +
                "    m.id,\n" +
                "    h.hall_id,\n" +
                "    ?,\n" +
                "    ?,\n" +
                "    h.capacity\n" +
                "FROM movies m\n" +
                "JOIN halls h ON m.movieName = ? AND h.name = ?\n" +
                "LIMIT 1;";
        try(Connection connection = Main.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, scheduleDate);
            preparedStatement.setString(2, scheduleTime);
            preparedStatement.setString(3, movieName);
            preparedStatement.setString(4, hallName);
            preparedStatement.executeUpdate();
            movieNameChoice.getItems().clear();
            updatePane1.setVisible(false);
            updatePane.setManaged(false);
            getSessionDetails();
            sessionTable.setItems(sessionList);
            sessionTable.sort();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void hideSessionBox(){
        updatePane1.setVisible(false);
        updatePane1.setManaged(false);
        movieNameChoice.setVisible(false);
        hallNameChoice.setVisible(false);
        scheduleDateChoice.setVisible(false);
        scheduleTimeChoice.setVisible(false);
        movieNameChoice.setManaged(false);
        hallNameChoice.setManaged(false);
        scheduleDateChoice.setManaged(false);
        scheduleTimeChoice.setManaged(false);
        saveScheduleButton.setVisible(false);
        saveScheduleButton.setManaged(false);
        hallText.setVisible(false);
        hallText.setManaged(false);
        dateText.setVisible(false);
        dateText.setManaged(false);
        timeText.setVisible(false);
        timeText.setManaged(false);
    }

    public class sessionDetails {
        private final SimpleIntegerProperty sessionId;
        private final SimpleStringProperty movieName;
        private final SimpleStringProperty hallName;
        private final SimpleStringProperty scheduleDate;
        private final SimpleStringProperty scheduleTime;
        private final SimpleBooleanProperty deleteButton;

        public sessionDetails(int sessionId, String movieName, String hallName, String scheduleDate, String scheduleTime, Boolean deleteButton) {
            this.sessionId = new SimpleIntegerProperty(sessionId);
            this.movieName = new SimpleStringProperty(movieName);
            this.hallName = new SimpleStringProperty(hallName);
            this.scheduleDate = new SimpleStringProperty(scheduleDate);
            this.scheduleTime = new SimpleStringProperty(scheduleTime);
            this.deleteButton = new SimpleBooleanProperty(deleteButton);
        }
        public int getSessionId() {
            return sessionId.get();
        }
        public String getMovieName() {
            return movieName.get();
        }
        public String getHallName() {
            return hallName.get();
        }
        public String getScheduleDate() {
            return scheduleDate.get();
        }
        public String getScheduleTime() {
            return scheduleTime.get();
        }
        public boolean getDeleteButton() {
            return deleteButton.get();
        }
        public LocalDate getScheduleLocalDate(){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            return LocalDate.parse(scheduleDate.get(), formatter);
        }
    }

    public class movieDetails {
        public String posterPath;
        private ImageView poster;
        private final SimpleStringProperty movieName;
        private final SimpleStringProperty movieGenre;
        private final SimpleStringProperty movieSummary;
        private final SimpleObjectProperty<Button> updateButton;
        private final SimpleBooleanProperty delete;

        public movieDetails(ImageView poster, String movieName, String movieGenre, String movieSummary,Button updateButton, Boolean delete) {
            this.poster = poster;
            this.movieName = new SimpleStringProperty(movieName);
            this.movieGenre = new SimpleStringProperty(movieGenre);
            this.movieSummary = new SimpleStringProperty(movieSummary);
            this.updateButton = new SimpleObjectProperty<>(updateButton);
            this.delete = new SimpleBooleanProperty(delete);
        }

        public ImageView getPoster(){
            return poster;
        }


        public void setPhoto(ImageView poster)
        {
            this.poster = poster;

        }

        public String getMovieName() {
            return movieName.get();
        }

        public void setMovieName(String name) {
            this.movieName.set(name);
        }



        public String getMovieGenre() {
            return movieGenre.get();
        }

        public void setMovieGenre(String genre) {
            movieGenre.set(genre);
        }



        public String getMovieSummary() {
            return movieSummary.get();
        }
        public Button getUpdateButton() {
            return updateButton.get();
        }
        public Boolean getDelete() {
            return delete.get();
        }


    }

}
