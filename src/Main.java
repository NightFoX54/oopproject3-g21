import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Random;


/**
 * The Main class is the entry point for the Group21 CinemaCenter application.
 * It initializes the JavaFX application and provides utility methods for database connection
 * and string manipulation.
 */
public class Main extends Application {

     /**
     * Represents the currently logged-in user.
     */
    static user currentUser;

    /*public void dateInjector(){
        String query = "INSERT INTO sessions (`movie_id`, `hall_id`, `schedule_date`, `start_time`, `available_seats`) VALUES (?,?,?,?,?)";
        try (Connection connection = Main.getConnection(); PreparedStatement statement = connection.prepareStatement(query)) {
            int prev1random=0;
            int prev2random=0;
            int prev3random=0;
            int prev4random=0;
            int prev5random=0;
            for(int i = 1; i <= 28; i++){
                for(int j = 10; j <= 22; j+=2){
                    LocalDate date = LocalDate.of(2025,02,i);
                    LocalTime time = LocalTime.of(j,0,0);
                    Random random = new Random();
                    int randomValue;
                    do {
                        randomValue = random.nextInt(15000 - 1) + 1;
                        randomValue = randomValue % 13 + 1;
                    }while(randomValue == prev1random || randomValue == prev2random || randomValue == prev3random || randomValue == prev4random || randomValue == prev5random);
                    prev5random = prev4random;
                    prev4random = prev3random;
                    prev3random = prev2random;
                    prev2random = prev1random;
                    prev1random = randomValue;
                    statement.setString(1, String.valueOf(randomValue));
                    statement.setString(2,"1");
                    statement.setString(3,date.toString());
                    statement.setString(4,time.toString());
                    statement.setString(5,"16");
                    statement.executeUpdate();
                    do {
                        randomValue = random.nextInt(15000 - 1) + 1;
                        randomValue = randomValue % 13 + 1;
                    }while(randomValue == prev1random || randomValue == prev2random || randomValue == prev3random || randomValue == prev4random || randomValue == prev5random);
                    prev5random = prev4random;
                    prev4random = prev3random;
                    prev3random = prev2random;
                    prev2random = prev1random;
                    prev1random = randomValue;
                    statement.setString(1,String.valueOf(randomValue));
                    statement.setString(2,"2");
                    statement.setString(5,"48");
                    statement.executeUpdate();
                }
            }
            connection.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }*/


    /**
     * Initializes the JavaFX application by loading the login screen.
     * @param stage The primary stage for this application.
     * @throws IOException If the FXML file for the login screen cannot be loaded.
     */
    @Override
    public void start(Stage stage) throws IOException {
        //dateInjector();
        Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Group21 CinemaCenter");
        stage.show();

    }

     /**
     * Capitalizes the first letter of the given string and converts the rest to lowercase.
     * @param str The input string.
     * @return The modified string with the first letter capitalized.
     */
    public static String capitalizeFirstLetter(String str) {
        if(str == null || str.length() == 0)
            return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

     /**
     * Establishes a connection to the database.
     * @return A Connection object for the database.
     * @throws SQLException If a database access error occurs.
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/project3","myuser","1234");
    }

    /**
     * The main entry point of the application.
     * @param args Command-line arguments passed to the application.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
