package org.example.javafxdemo;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PasswordManagerController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    private static String currentUser;

    public static void setCurrentUser(String username) {
        currentUser = username;
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Username or password cannot be empty!");
            return;
        }

        String sql = "SELECT encrypted_password FROM users WHERE username = ?";

        try (Connection conn = DataBase.connect()) {
            assert conn != null;
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, username);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    String encryptedPassword = rs.getString("encrypted_password");

                    String decryptedPassword = EncryptionUtil.decrypt(encryptedPassword);

                    if (decryptedPassword.equals(password)) {
                        setCurrentUser(username);
                        showMainWindow();
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Error", "Invalid username or password!");
                    }
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "User not found!");
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "Decryption failed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while logging in.");
        }
    }
    private void showMainWindow() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MainWindow.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            MainWindowController controller = fxmlLoader.getController();
            controller.setCurrentUser(currentUser);

            Stage stage = new Stage();

            stage.setMinWidth(500);
            stage.setMinHeight(550);
            stage.setMaxHeight(600);
            stage.setMaxWidth(800);

            stage.setTitle("Password Manager");
            stage.setScene(scene);
            stage.show();

            Stage currentStage = (Stage) usernameField.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSignIn() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(PasswordManagerApplication.class.getResource("RegisterController.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 400, 300);
            Stage stage = new Stage();

            stage.setMinWidth(400);
            stage.setMinHeight(300);

            stage.setTitle("Create Account");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
