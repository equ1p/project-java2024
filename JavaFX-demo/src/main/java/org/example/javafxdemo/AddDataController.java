package org.example.javafxdemo;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AddDataController {
    @FXML
    private TextField titleField;

    @FXML
    private TextField loginField;

    @FXML
    private PasswordField passwordField;

    private String currentUser;

    private PublicKey rsaPublicKey;

    public void setCurrentUser(String currentUser) {
        this.currentUser = currentUser;
        System.out.println("AddDataController: Current user set to: " + currentUser);

        try {
            rsaPublicKey = KeyStorage.loadUserKeys(currentUser).getPublic();
        } catch (Exception e) {
            System.out.println("Error loading public key for user " + currentUser + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAdd() {
        if (currentUser == null) {
            System.out.println("Error: Current user is null. Cannot add data.");
            return;
        }

        System.out.println("Adding data to table: data_" + currentUser);

        String title = titleField.getText();
        String login = loginField.getText();
        String password = passwordField.getText();

        if (title.isEmpty() || login.isEmpty() || password.isEmpty()) {
            System.out.println("All fields are required!");
            return;
        }

        String encryptedTitle = null;
        String encryptedLogin = null;
        String encryptedPassword = null;

        try {
            encryptedTitle = EncryptionUtil.encrypt(title, rsaPublicKey);
            encryptedLogin = EncryptionUtil.encrypt(login, rsaPublicKey);
            encryptedPassword = EncryptionUtil.encrypt(password, rsaPublicKey);
        } catch (Exception e) {
            System.out.println("Error during encryption: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        String tableName = "data_" + currentUser;
        String sql = "INSERT INTO " + tableName + " (title, login, password, last_modified) VALUES (?, ?, ?, datetime('now'))";

        try (Connection conn = DataBase.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, encryptedTitle);
            pstmt.setString(2, encryptedLogin);
            pstmt.setString(3, encryptedPassword);
            pstmt.executeUpdate();
            System.out.println("Data added successfully!");
            closeWindow();
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleGeneratePassword() {
        GeneratePassword pswd = new GeneratePassword();
        String newPassword = pswd.generateRandomPassword(20);

        passwordField.setText(newPassword);
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) titleField.getScene().getWindow();
        stage.close();
    }
}
