import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.scene.control.Dialog;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;





public class ManagerController {
    @FXML
    public Label totalRevenueLabel;
    @FXML
    public Label totalTaxLabel;
    @FXML
    public Label totalSalesLabel;
    @FXML
    private TextField addStockField;

    @FXML
    private TableView<Product> inventoryTable;

    @FXML
    private TableView<User> personnelTable;

    @FXML
    private TableColumn<Product, String> productNameColumn;

    @FXML
    private TableColumn<Product, String> productCategoryColumn;

    @FXML
    private TableColumn<Product, Integer> productStockColumn;

    @FXML
    private TableColumn<User, String> employeeNameColumn;

    @FXML
    private TableColumn<User, String> employeeRoleColumn;

    @FXML
    private TextField productNameField;

    @FXML
    private TextField newPriceField;

    @FXML
    private TextField ticketPriceField;

    @FXML
    private TextField updateProductNameField;

    @FXML
    private Label userInfoLabel;

    @FXML
    private TextField addStockProductNameField;

    @FXML   
    private TextField addStockQuantityField;

    @FXML
    private TextField addStockPriceField;

    @FXML
    private TextField imageFilePathField;

    private final ObservableList<Product> productList = FXCollections.observableArrayList();
    private final ObservableList<User> userList = FXCollections.observableArrayList();

    public static class Product {
        private final SimpleStringProperty name;
        private final SimpleStringProperty category;
        private final SimpleIntegerProperty stock;

        public Product(String name, String category, int stock) {
            this.name = new SimpleStringProperty(name);
            this.category = new SimpleStringProperty(category);
            this.stock = new SimpleIntegerProperty(stock);
        }

        public String getName() {
            return name.get();
        }

        public String getCategory() {
            return category.get();
        }

        public int getStock() {
            return stock.get();
        }
    }

    public static class User {
        private final SimpleStringProperty username;
        private final SimpleStringProperty role;

        public User(String username, String role) {
            this.username = new SimpleStringProperty(username);
            this.role = new SimpleStringProperty(role);
        }

        public String getUsername() {
            return username.get();
        }

        public String getRole() {
            return role.get();
        }
    }

    @FXML
    public void initialize() {
        // Bind product table columns
        productNameColumn.setCellValueFactory(cellData -> cellData.getValue().name);
        productCategoryColumn.setCellValueFactory(cellData -> cellData.getValue().category);
        productStockColumn.setCellValueFactory(cellData -> cellData.getValue().stock.asObject());

        // Bind user table columns
        employeeNameColumn.setCellValueFactory(cellData -> cellData.getValue().username);
        employeeRoleColumn.setCellValueFactory(cellData -> cellData.getValue().role);

        // Load data
        loadInventoryData();
        loadUserData();

        if (Main.currentUser != null) {
            String username = Main.currentUser.getName();
            String role = Main.currentUser.getRole();
            userInfoLabel.setText("Welcome" + username);
        }
        refreshDetails();
    }

    private void loadInventoryData() {
        productList.clear();
        try (Connection connection = Main.getConnection()) {
            String query = "SELECT id, name, stock FROM prices";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
               if(!"tickets".equals(resultSet.getString("name"))){
                    productList.add(new Product(
                            resultSet.getString("name"),
                            resultSet.getString("id"),
                            resultSet.getInt("stock")));
               }

            }
            inventoryTable.setItems(productList);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Database Error", "Failed to load inventory:\n" + e.getMessage());
        }
    }

    private void loadUserData() {
        userList.clear();
        try (Connection connection = Main.getConnection()) {
            String query = "SELECT username, role FROM users";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                userList.add(new User(
                        resultSet.getString("username"),
                        resultSet.getString("role")));
            }
            personnelTable.setItems(userList);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Database Error", "Failed to load employee data:\n" + e.getMessage());
        }
    }

    @FXML
    void updateStock(ActionEvent event) {
        String productName = productNameField.getText();
        String additionalStockText = addStockField.getText();

        if (productName == null || productName.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Product Name Required", "Please enter a product name.");
            return;
        }

        if (additionalStockText == null || additionalStockText.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Stock Quantity Required", "Please enter a stock quantity.");
            return;
        }

        try {
            int additionalStock = Integer.parseInt(additionalStockText);

            if (additionalStock < 0) {
                showAlert(Alert.AlertType.ERROR, "Error", "Invalid Stock Quantity", "Please enter a positive number.");
                return;
            }

            try (Connection connection = Main.getConnection()) {
                String query = "UPDATE prices SET stock = stock + ? WHERE name = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, additionalStock);
                preparedStatement.setString(2, productName);

                int affectedRows = preparedStatement.executeUpdate();
                if (affectedRows > 0) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Stock Updated",
                            "Product: " + productName + "\nAdded Stock: " + additionalStock);
                    addStockField.clear();
                    productNameField.clear();
                    loadInventoryData();
                } else {
                    showAlert(Alert.AlertType.WARNING, "Warning", "Product Not Found", "No product found with the given name.");
                }
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Invalid Input", "Please enter a valid number for the stock quantity.");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Database Error", "An error occurred while updating the stock:\n" + e.getMessage());
        }
    }

    @FXML
    void updatePrice(ActionEvent event) {
        String productName = updateProductNameField.getText();
        String newPriceText = newPriceField.getText();

        if (productName == null || productName.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Product Name Required", "Please enter a product name.");
            return;
        }

        if (newPriceText == null || newPriceText.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Price Required", "Please enter a new price.");
            return;
        }

        try {
            double newPrice = Double.parseDouble(newPriceText);

            if (newPrice < 0) {
                showAlert(Alert.AlertType.ERROR, "Error", "Invalid Price", "Please enter a positive value for the price.");
                return;
            }

            try (Connection connection = Main.getConnection()) {
                String query = "UPDATE prices SET price = ? WHERE name = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setDouble(1, newPrice);
                preparedStatement.setString(2, productName);

                int affectedRows = preparedStatement.executeUpdate();
                if (affectedRows > 0) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Price Updated",
                            "Product: " + productName + "\nNew Price: $" + newPrice);
                    updateProductNameField.clear();
                    newPriceField.clear();
                } else {
                    showAlert(Alert.AlertType.WARNING, "Warning", "Product Not Found", "No product found with the given name.");
                }
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Invalid Input", "Please enter a valid number for the price.");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Database Error", "An error occurred while updating the price:\n" + e.getMessage());
        }
    }

    @FXML
    void updateTicketPrice(ActionEvent event) {
        // Kullanıcının girdiği değeri al
        String newTicketPriceText = ticketPriceField.getText();
    
        // Girdi boş mu veya null mı kontrol et
        if (newTicketPriceText == null || newTicketPriceText.trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Ticket Price Required", "Please enter a new ticket price.");
            return;
        }
    
        try {
            // Girilen fiyatı double olarak dönüştür
            double newTicketPrice = Double.parseDouble(newTicketPriceText.trim());
    
            // Negatif fiyat kontrolü
            if (newTicketPrice < 0) {
                showAlert(Alert.AlertType.ERROR, "Error", "Invalid Price", "Please enter a positive value for the ticket price.");
                return;
            }
    
            // Database bağlantısı ve güncelleme işlemi
            try (Connection connection = Main.getConnection()) {
                String query = "UPDATE prices SET price = ? WHERE name = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setDouble(1, newTicketPrice);
                preparedStatement.setString(2, "tickets"); // String parametreyi ekle
    
                int affectedRows = preparedStatement.executeUpdate();
    
                // Güncelleme başarılı mı kontrol et
                if (affectedRows > 0) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Ticket Price Updated",
                            "New Ticket Price: $" + String.format("%.2f", newTicketPrice));
                    ticketPriceField.clear();
                } else {
                    showAlert(Alert.AlertType.WARNING, "Warning", "Operation Failed",
                            "No ticket price was updated. Please check if the record exists.");
                }
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Invalid Input", "Please enter a valid number for the ticket price.");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Database Error", "An error occurred while updating the ticket price:\n" + e.getMessage());
        }
    }
    
    @FXML
    private void hireEmployeePopup() {
        // Show a popup for hiring a new employee
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Hire Employee");
        dialog.setHeaderText("Enter employee details:");
    
        TextField nameField = new TextField();
        nameField.setPromptText("Name");
    
        TextField surnameField = new TextField();
        surnameField.setPromptText("Surname");
    
        TextField roleField = new TextField();
        roleField.setPromptText("Role");
    
        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
    
        VBox content = new VBox(10, 
            new Label("Name:"), nameField, 
            new Label("Surname:"), surnameField, 
            new Label("Role:"), roleField, 
            new Label("Username:"), usernameField
        );
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
    
        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                String name = nameField.getText().trim();
                String surname = surnameField.getText().trim();
                String role = roleField.getText().trim();
                String username = usernameField.getText().trim();
    
                if (!name.isEmpty() && !surname.isEmpty() && !role.isEmpty() && !username.isEmpty()) {
                    addEmployeeToDatabase(name, surname, role, username);
                } else {
                    showAlert(Alert.AlertType.WARNING, "Invalid Input", "All fields are required!", null);
                }
            }
        });
    }
    
    private void addEmployeeToDatabase(String name, String surname, String role, String username) {
        try (Connection connection = Main.getConnection()) {
            String query = "INSERT INTO users (name, surname, role, username) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, name);
            statement.setString(2, surname);
            statement.setString(3, role);
            statement.setString(4, username);
    
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Employee Added Successfully", 
                    "Name: " + name + "\nSurname: " + surname + "\nRole: " + role + "\nUsername: " + username);
                loadUserData(); // Refresh the table or UI
            } else {
                showAlert(Alert.AlertType.WARNING, "Failed", "Failed to Add Employee", null);
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to add employee:\n" + e.getMessage(), null);
        }
    }
    @FXML
    private void fireEmployee() {
        User selectedEmployee = personnelTable.getSelectionModel().getSelectedItem();
    
        if (selectedEmployee == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText(null);
            alert.setContentText("Please select an employee to fire.");
            alert.showAndWait();
            return;
        }
    
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirmation");
        confirmationAlert.setHeaderText("Are you sure?");
        confirmationAlert.setContentText("Do you really want to fire " + selectedEmployee.getUsername() + "?");
    
        confirmationAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    Connection connection = Main.getConnection();
                    String query = "DELETE FROM users WHERE username = ? AND role = ?";
                    PreparedStatement statement = connection.prepareStatement(query);
                    statement.setString(1, selectedEmployee.getUsername());
                    statement.setString(2, selectedEmployee.getRole());
                    int rowsAffected = statement.executeUpdate();
    
                    if (rowsAffected > 0) {
                        personnelTable.getItems().remove(selectedEmployee);
                        Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                        successAlert.setTitle("Success");
                        successAlert.setHeaderText(null);
                        successAlert.setContentText("Employee " + selectedEmployee.getUsername() + " has been successfully fired.");
                        successAlert.showAndWait();
                    } else {
                        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                        errorAlert.setTitle("Error");
                        errorAlert.setHeaderText(null);
                        errorAlert.setContentText("Failed to fire the employee. Please try again.");
                        errorAlert.showAndWait();
                    }
    
                    statement.close();
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Database Error");
                    errorAlert.setHeaderText(null);
                    errorAlert.setContentText("An error occurred while connecting to the database.");
                    errorAlert.showAndWait();
                }
            }
        });
    }
    @FXML
    private void addStock() {
        // Ürün adı ve eklenecek miktar alanlarını al
        String productName = addStockProductNameField.getText().trim(); // Doğru alan bağlandı
        String quantityToAddText = addStockQuantityField.getText().trim();
        String PirceText = addStockPriceField.getText().trim();
        String Photopath =  imageFilePathField.getText().trim();
        
    
        // Alanların boş olup olmadığını kontrol et
        if (productName.isEmpty() || quantityToAddText.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Invalid Input", "All fields are required!", null);
            return;
        }
    
        try {
            // Eklenecek miktarın sayısal bir değer olup olmadığını kontrol et
            int quantityToAdd = Integer.parseInt(quantityToAddText);
            if (quantityToAdd <= 0) {
                showAlert(Alert.AlertType.WARNING, "Invalid Input", "Quantity must be a positive number!", null);
                return;
            }
    
            // Veritabanına bağlantı
            try (Connection connection = Main.getConnection()) {
                // Veritabanı sorgusunu hazırla
                String query = "INSERT INTO prices (name, stock, price, photopath) VALUES (?, ?, ?, ?)";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, productName); // Eklenecek miktar
                statement.setInt(2, quantityToAdd); // Ürün adı
                statement.setString(3, PirceText);
                statement.setString(4, Photopath);
    
                int rowsAffected = statement.executeUpdate();
                if (rowsAffected > 0) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Stock updated successfully",
                        "Product: " + productName + "\nAdded Quantity: " + quantityToAdd);
                    loadInventoryData(); // Tabloyu güncelle
                    clearAddStockFields(); // Alanları temizle
                } else {
                    showAlert(Alert.AlertType.WARNING, "Failed", "No such product found in the database!", null);
                }
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Quantity must be a valid number!", null);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to update stock:\n" + e.getMessage(), null);
        }
    }
    
    // Giriş alanlarını temizleme metodu
    private void clearAddStockFields() {
        addStockProductNameField.clear();
        addStockQuantityField.clear();
    }

    @FXML
    private void selectImageFile() throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        File selectedFile = fileChooser.showOpenDialog(new Stage());
        if (selectedFile != null) {
            File destinationFile = new File("src/photos/" + selectedFile.getName());
            Files.copy(selectedFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            imageFilePathField.setText("photos/" + selectedFile.getName());
        }
    }

    
    @FXML
    public void refreshDetails(){
        double total_revenue = 0;
        double total_tax = 0;
        int total_sales = 0;
        String query = "SELECT * FROM completed_sells";
        try(Connection connection = Main.getConnection();
            ResultSet resultSet = connection.createStatement().executeQuery(query)) {
            while (resultSet.next()) {
                total_sales++;
                total_revenue += resultSet.getDouble("total_price");
                total_tax += resultSet.getDouble("total_tax");
            }
            totalRevenueLabel.setText("Total Gross Revenue: " + total_revenue);
            totalTaxLabel.setText("Total Tax: " + total_tax);
            totalSalesLabel.setText("Total Sales: " + total_sales);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void logout(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        Main.currentUser = null;
    }

    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}