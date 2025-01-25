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
        try (Connection conn = DataBase.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, titleField.getText());
            pstmt.setString(2, loginField.getText());
            pstmt.setString(3, passwordField.getText());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        titleField.getScene().getWindow().hide();
    }
}

