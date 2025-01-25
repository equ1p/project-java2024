package org.example.javafxdemo;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

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

    public void setCurrentUser(String currentUser) {
        this.currentUser = currentUser;
    }

    public void setDataToEdit(UserData data) {
        this.dataToEdit = data;

        titleField.setText(data.getTitle());
        loginField.setText(data.getLogin());
        passwordField.setText(data.getPassword());
    }

    @FXML
    private void handleSave() {
        UserData updatedData = new UserData(
                dataToEdit.getId(),
                titleField.getText(),
                loginField.getText(),
                passwordField.getText(),
                dataToEdit.getLastModified()
        );
        if (updatedData.getTitle().isEmpty() || updatedData.getLogin().isEmpty() || updatedData.getPassword().isEmpty()) {
            System.out.println("All fields are required!");
            return;
        }
        String tableName = "data_" + currentUser;
        String sql = "DELETE FROM " + tableName + " WHERE id = ?";
        try (Connection conn = DataBase.connect()) {
            assert conn != null;
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, dataToEdit.getId());
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        sql = "INSERT INTO " + tableName + " (title, login, password, last_modified) VALUES (?, ?, ?, datetime('now'))";
        try (Connection conn = DataBase.connect()) {
            assert conn != null;
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, titleField.getText());
                stmt.setString(2, loginField.getText());
                stmt.setString(3, passwordField.getText());
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
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

