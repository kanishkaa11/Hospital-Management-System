package kjmn;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Main application class for Hospital Management System
 */
public class Kjmn extends Application {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/kjmn";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "k@ni11";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setScene(new Scene(createTitlePane(), 600, 500));
        primaryStage.setTitle("Hospital Management System - Welcome");
        primaryStage.show();
    }

    private GridPane createTitlePane() {
        GridPane grid = new GridPane();
        grid.setAlignment(javafx.geometry.Pos.CENTER);
        grid.setHgap(30);
        grid.setVgap(30);
        grid.setPadding(new Insets(40, 40, 40, 40));

        Label titleLabel = new Label("Welcome to KJMN Hospital 🏥");
        titleLabel.setStyle("-fx-font-size: 30px; -fx-font-weight: bold;");
        grid.add(titleLabel, 0, 0);

        Button proceedButton = new Button("Proceed to Login");
        proceedButton.setStyle("-fx-font-size: 20px;");
        GridPane.setHalignment(proceedButton, javafx.geometry.HPos.CENTER);
        grid.add(proceedButton, 0, 1);

        proceedButton.setOnAction((ActionEvent event) -> {
            loadLoginPage((Stage) proceedButton.getScene().getWindow());
        });

        return grid;
    }

    private void loadLoginPage(Stage stage) {
        stage.setScene(new Scene(createLoginPane(), 600, 500));
        stage.setTitle("Hospital Management System - Login");
    }

    private GridPane createLoginPane() {
        GridPane grid = new GridPane();
        grid.setAlignment(javafx.geometry.Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(40, 40, 40, 40));

        Label usernameLabel = new Label("Username:");
        usernameLabel.setStyle("-fx-font-size: 30px;");
        grid.add(usernameLabel, 0, 0);

        TextField usernameField = new TextField();
        usernameField.setStyle("-fx-font-size: 25px;");
        grid.add(usernameField, 1, 0);

        Label passwordLabel = new Label("Password:");
        passwordLabel.setStyle("-fx-font-size: 30px;");
        grid.add(passwordLabel, 0, 1);

        PasswordField passwordField = new PasswordField();
        passwordField.setStyle("-fx-font-size: 25px;");
        grid.add(passwordField, 1, 1);

        Button loginButton = new Button("Login");
        loginButton.setStyle("-fx-font-size: 25px;");
        grid.add(loginButton, 1, 2);

        loginButton.setOnAction((ActionEvent event) -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            if (validateLogin(username, password)) {
                showAlert(Alert.AlertType.INFORMATION, "Login Successful!", null, "Welcome " + username + "!");
                loadMainApp((Stage) usernameField.getScene().getWindow());
            } else {
                showAlert(Alert.AlertType.ERROR, "Login Failed!", null, "Invalid username or password.");
            }
        });

        return grid;
    }

    private void loadMainApp(Stage stage) {
        stage.setScene(new Scene(createMainPane(), 1000, 800));
        stage.setTitle("Hospital Management System - Main");
    }

    private GridPane createMainPane() {
        GridPane grid = new GridPane();
        grid.setHgap(30);
        grid.setVgap(30);
        grid.setPadding(new Insets(60, 60, 60, 60));

        TextField idField = new TextField();
        idField.setPromptText("ID");
        grid.add(idField, 0, 0);

        TextField nameField = new TextField();
        nameField.setPromptText("Patient Name");
        grid.add(nameField, 1, 0);

        TextField ageField = new TextField();
        ageField.setPromptText("Patient Age");
        grid.add(ageField, 2, 0);

        TextField genderField = new TextField();
        genderField.setPromptText("Gender");
        grid.add(genderField, 3, 0);

        TextField affectedByField = new TextField();
        affectedByField.setPromptText("Affected By");
        grid.add(affectedByField, 4, 0);

        VBox vbox = new VBox(30);
        Button addButton = new Button("Add");
        Button deleteButton = new Button("Delete");
        Button editButton = new Button("Edit");
        vbox.getChildren().addAll(addButton, deleteButton, editButton);
        grid.add(vbox, 7, 0);

        TableView<Patient> patientTable = new TableView<>();
        TableColumn<Patient, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Patient, String> nameColumn = new TableColumn<>("Patient Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Patient, Integer> ageColumn = new TableColumn<>("Patient Age");
        ageColumn.setCellValueFactory(new PropertyValueFactory<>("age"));

        TableColumn<Patient, String> genderColumn = new TableColumn<>("Gender");
        genderColumn.setCellValueFactory(new PropertyValueFactory<>("gender"));

        TableColumn<Patient, String> affectedByColumn = new TableColumn<>("Affected By");
        affectedByColumn.setCellValueFactory(new PropertyValueFactory<>("affectedBy"));

        patientTable.getColumns().addAll(idColumn, nameColumn, ageColumn, genderColumn, affectedByColumn);
        grid.add(patientTable, 0, 1, 6, 1);

        ObservableList<Patient> patientList = FXCollections.observableArrayList(getAllPatients());
        patientTable.setItems(patientList);

        patientTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                idField.setText(String.valueOf(newSelection.getId()));
                nameField.setText(newSelection.getName());
                ageField.setText(String.valueOf(newSelection.getAge()));
                genderField.setText(newSelection.getGender());
                affectedByField.setText(newSelection.getAffectedBy());
            }
        });

        addButton.setOnAction(event -> {
            try {
                Patient patient = new Patient();
                patient.setId(Integer.parseInt(idField.getText()));
                patient.setName(nameField.getText());
                patient.setAge(Integer.parseInt(ageField.getText()));
                patient.setGender(genderField.getText());
                patient.setAffectedBy(affectedByField.getText());

                addPatient(patient);
                patientList.add(patient);

                idField.clear();
                nameField.clear();
                ageField.clear();
                genderField.clear();
                affectedByField.clear();
                showAlert(Alert.AlertType.INFORMATION, "Patient Added", null, "Patient has been added successfully.");
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Invalid Input", null, "Please enter valid data.");
            }
        });

        deleteButton.setOnAction(event -> {
            Patient selectedPatient = patientTable.getSelectionModel().getSelectedItem();
            if (selectedPatient != null) {
                deletePatient(selectedPatient.getId());
                patientList.remove(selectedPatient);
                showAlert(Alert.AlertType.INFORMATION, "Patient Deleted", null, "Patient has been deleted successfully.");
            } else {
                showAlert(Alert.AlertType.ERROR, "No Selection", "No Patient Selected", "Please select a patient to delete.");
            }
        });

        editButton.setOnAction(event -> {
            Patient selectedPatient = patientTable.getSelectionModel().getSelectedItem();
            if (selectedPatient != null) {
                try {
                    selectedPatient.setId(Integer.parseInt(idField.getText()));
                    selectedPatient.setName(nameField.getText());
                    selectedPatient.setAge(Integer.parseInt(ageField.getText()));
                    selectedPatient.setGender(genderField.getText());
                    selectedPatient.setAffectedBy(affectedByField.getText());

                    updatePatient(selectedPatient);
                    patientTable.refresh();

                    idField.clear();
                    nameField.clear();
                    ageField.clear();
                    genderField.clear();
                    affectedByField.clear();
                    showAlert(Alert.AlertType.INFORMATION, "Patient Updated", null, "Patient details have been updated successfully.");
                } catch (NumberFormatException e) {
                    showAlert(Alert.AlertType.ERROR, "Invalid Input", null, "Please enter valid data.");
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "No Selection", "No Patient Selected", "Please select a patient to edit.");
            }
        });

        return grid;
    }

    private boolean validateLogin(String username, String password){
        String hardcodedUsername = "admin";
        String hardcodedPassword = "admin123";

        if (username.equals(hardcodedUsername) && password.equals(hardcodedPassword)) {
            return true;
        }
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/kjmn", "root", "k@ni11");
             PreparedStatement pstmt = con.prepareStatement("SELECT * FROM users WHERE username = ? AND password = ?")) {
            pstmt.setString(1, hardcodedUsername);
            pstmt.setString(2, hardcodedPassword);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException ex) {
            ex.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", null, "An error occurred while connecting to the database.");
            return false;
        }
    }

    private List<Patient> getAllPatients() {
        List<Patient> patients = new ArrayList<>();
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/kjmn","root","k@ni11");
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM hospital")) {
        while (rs.next()) {
            Patient patient = new Patient();
            patient.setId(rs.getInt("ID"));
            patient.setName(rs.getString("PatientName"));
            patient.setAge(rs.getInt("PatientAge"));
            patient.setGender(rs.getString("Gender"));
            patient.setAffectedBy(rs.getString("AffectedBy"));
            patients.add(patient);
        }
    } catch (SQLException ex) {
        ex.printStackTrace();
        showAlert(Alert.AlertType.ERROR, "Database Error", null, "An error occurred while retrieving patient data.");
    }
    return patients;
}


  private void addPatient(Patient patient) {
    String url = "jdbc:mysql://localhost:3306/kjmn";
    String user = "root";
    String password = "k@ni11";
    String sql = "INSERT INTO hospital(ID, PatientName, PatientAge, Gender, AffectedBy) VALUES (?, ?, ?, ?, ?)";
    
    try (Connection con = DriverManager.getConnection(url, user, password);
         PreparedStatement pstmt = con.prepareStatement(sql)) {
        
        pstmt.setInt(1, patient.getId());
        pstmt.setString(2, patient.getName());
        pstmt.setInt(3, patient.getAge());
        pstmt.setString(4, patient.getGender());
        pstmt.setString(5, patient.getAffectedBy());
        
        pstmt.executeUpdate();
        System.out.println("Patient added successfully!");
        
    } catch (SQLException ex) {
        ex.printStackTrace();
        showAlert(Alert.AlertType.ERROR, "Database Error", null, "An error occurred while adding the patient: " + ex.getMessage());
    }
}


   private void deletePatient(int id) {
    String url = "jdbc:mysql://localhost:3306/kjmn";
    String user = "root";
    String password = "k@ni11";
    String sql = "DELETE FROM hospital WHERE ID = ?";
    
    try (Connection con = DriverManager.getConnection(url, user, password);
         PreparedStatement pstmt = con.prepareStatement(sql)) {
        
        System.out.println("Connection established successfully.");
        
        pstmt.setInt(1, id);
        
        System.out.println("PreparedStatement created and parameter set.");
        
        int rowsAffected = pstmt.executeUpdate();
        
        System.out.println("Patient deleted successfully. Rows affected: " + rowsAffected);
        
    } catch (SQLException ex) {
        System.err.println("SQLException: " + ex.getMessage());
        System.err.println("SQLState: " + ex.getSQLState());
        System.err.println("VendorError: " + ex.getErrorCode());
        showAlert(Alert.AlertType.ERROR, "Database Error", null, "An error occurred while deleting the patient: " + ex.getMessage());
    }
}


   private void updatePatient(Patient patient) {
    String url = "jdbc:mysql://localhost:3306/kjmn";
    String user = "root";
    String password = "k@ni11";
    String sql = "UPDATE hospital SET PatientName = ?, PatientAge = ?, Gender = ?, AffectedBy = ? WHERE ID = ?";
    
    try (Connection con = DriverManager.getConnection(url, user, password);
         PreparedStatement pstmt = con.prepareStatement(sql)) {
        
        System.out.println("Connection established successfully.");
        
        pstmt.setString(1, patient.getName());
        pstmt.setInt(2, patient.getAge());
        pstmt.setString(3, patient.getGender());
        pstmt.setString(4, patient.getAffectedBy());
        pstmt.setInt(5, patient.getId());
        
        System.out.println("PreparedStatement created and parameters set.");
        
        int rowsAffected = pstmt.executeUpdate();
        
        System.out.println("Patient updated successfully. Rows affected: " + rowsAffected);
        
    } catch (SQLException ex) {
        System.err.println("SQLException: " + ex.getMessage());
        System.err.println("SQLState: " + ex.getSQLState());
        System.err.println("VendorError: " + ex.getErrorCode());
        showAlert(Alert.AlertType.ERROR, "Database Error", null, "An error occurred while updating the patient: " + ex.getMessage());
    }
}


    private void showAlert(Alert.AlertType alertType, String title, String headerText, String contentText) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    public static class Patient {
        private int id;
        private String name;
        private int age;
        private String gender;
        private String affectedBy;

        // Getters and setters for the Patient class properties
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public String getAffectedBy() {
            return affectedBy;
        }

        public void setAffectedBy(String affectedBy) {
            this.affectedBy = affectedBy;
        }
    }
}
