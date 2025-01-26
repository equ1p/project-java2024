package org.example.javafxdemo;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;


import java.security.PublicKey;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class EditDataController {

    @FXML
    private TextField titleField;
    @FXML
    private TextField loginField;
    @FXML
    private TextField passwordField;

    private UserData dataToEdit;
    private String currentUser;

    private PublicKey rsaPublicKey;

    public void setCurrentUser(String currentUser) {
        this.currentUser = currentUser;

        try {
            this.rsaPublicKey = KeyStorage.loadUserKeys(currentUser).getPublic();
        } catch (Exception e) {
            System.out.println("Error loading public key for user " + currentUser + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void setDataToEdit(UserData data) {
        this.dataToEdit = data;

        titleField.setText(data.getTitle());
        loginField.setText(data.getLogin());
        passwordField.setText(data.getPassword());
    }

    @FXML
    private void handleSave() {
        String updatedTitle = titleField.getText();
        String updatedLogin = loginField.getText();
        String updatedPassword = passwordField.getText();

        if (updatedTitle.isEmpty() || updatedLogin.isEmpty() || updatedPassword.isEmpty()) {
            System.out.println("All fields are required!");
            return;
        }

        String encryptedTitle = null;
        String encryptedLogin = null;
        String encryptedPassword = null;

        try {
            encryptedTitle = EncryptionUtil.encrypt(updatedTitle, rsaPublicKey);
            encryptedLogin = EncryptionUtil.encrypt(updatedLogin, rsaPublicKey);
            encryptedPassword = EncryptionUtil.encrypt(updatedPassword, rsaPublicKey);
        } catch (Exception e) {
            System.out.println("Error during encryption: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        String tableName = "data_" + currentUser;
        String updateSql = "UPDATE " + tableName + " SET title = ?, login = ?, password = ?, last_modified = datetime('now') WHERE id = ?";

        try (Connection conn = DataBase.connect();
             PreparedStatement stmt = conn.prepareStatement(updateSql)) {

            stmt.setString(1, encryptedTitle);
            stmt.setString(2, encryptedLogin);
            stmt.setString(3, encryptedPassword);
            stmt.setInt(4, dataToEdit.getId());
            stmt.executeUpdate();

            System.out.println("Data updated successfully!");
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
            e.printStackTrace();
        }

        titleField.getScene().getWindow().hide();
    }

    @FXML
    private void handleRegenerate(){
        String newPassword = GeneratePassword.generateRandomPassword(20);
        passwordField.setText(newPassword);
    }
}

