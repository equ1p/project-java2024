package org.example.javafxdemo;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.security.KeyPair;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RegisterController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private void handleRegister() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Username or password cannot be empty!");
            return;
        }

        byte[] publicKeyBytes;
        byte[] privateKeyBytes;
        String encryptedPassword = null;

        try {
            KeyPair keyPair = EncryptionUtil.generateRSAKeyPair();
            publicKeyBytes = keyPair.getPublic().getEncoded();
            privateKeyBytes = keyPair.getPrivate().getEncoded();

            encryptedPassword = EncryptionUtil.encrypt(password, keyPair.getPublic());
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to generate keys or encrypt password!");
            return;
        }

        String sql = "INSERT INTO users (username, encrypted_password, public_key, private_key) VALUES (?, ?, ?, ?)";
        try (Connection conn = DataBase.connect()) {
            assert conn != null;
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, username);
                stmt.setString(2, encryptedPassword);
                stmt.setBytes(3, publicKeyBytes);
                stmt.setBytes(4, privateKeyBytes);
                stmt.executeUpdate();

                DataBase.createUserDataTable(username);

            showAlert(Alert.AlertType.INFORMATION, "Success", "User registered successfully!");
            closeWindow();
            }
        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE")) {
                showAlert(Alert.AlertType.ERROR, "Error", "Username already exists!");
            } else {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while registering.");
            }
        }
    }

    private void closeWindow() {
        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
