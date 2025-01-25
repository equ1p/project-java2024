package org.example.javafxdemo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MainWindowController {

    public Button addButton;
    @FXML
    private TableView<UserData> dataTable;

    @FXML
    private TableColumn<UserData, String> titleColumn;

    @FXML
    private VBox detailsView;

    @FXML
    private Label titleLabel;

    @FXML
    private Label loginLabel;

    @FXML
    private Label passwordLabel;

    @FXML
    private Label lastModifiedLabel;

    private UserData selectedData;

    private final ObservableList<UserData> dataList = FXCollections.observableArrayList();
    private String currentUser;

    public void setCurrentUser(String currentUser) {
        this.currentUser = currentUser;
        System.out.println("MainWindowController: Current user set to: " + currentUser);
        loadData();
    }

    @FXML
    public void initialize() {

        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        dataTable.setItems(dataList);

        dataTable.getSelectionModel().selectedItemProperty().addListener((_, oldSelection, newSelection) -> {
            if (newSelection != null) {
                showDetails(newSelection);
            }
        });
    }


    private void loadData() {
        String tableName = "data_" + currentUser;
        String sql = "SELECT * FROM " + tableName;

        try (Connection conn = DataBase.connect()) {
            assert conn != null;
            try (PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                dataList.clear();
                while (rs.next()) {
                    dataList.add(new UserData(
                            rs.getInt("id"),
                            rs.getString("title"),
                            rs.getString("login"),
                            rs.getString("password"),
                            rs.getString("last_modified")
                    ));
                }
                dataTable.setItems(dataList);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddData() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(PasswordManagerApplication.class.getResource("AddData.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            AddDataController controller = fxmlLoader.getController();
            controller.setCurrentUser(currentUser);

            Stage stage = new Stage();
            stage.setTitle("Add New Data");
            stage.setScene(scene);
            stage.showAndWait();

            loadData();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDelete() {
        String tableName = "data_" + currentUser;
        String sql = "DELETE FROM " + tableName + " WHERE id = ?";

        try (Connection conn = DataBase.connect()) {
            assert conn != null;
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, selectedData.getId());
                stmt.executeUpdate();
                dataList.remove(selectedData);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        handleBack();
    }

    private void showDetails(UserData data) {
        this.selectedData = data;

        dataTable.setVisible(false);
        addButton.setVisible(false);
        detailsView.setVisible(true);

        titleLabel.setText(data.getTitle());
        loginLabel.setText("Login: " + data.getLogin());
        passwordLabel.setText("Password: " + data.getPassword());
        lastModifiedLabel.setText("Last Modified: " + data.getLastModified());
    }

    @FXML
    private void handleBack() {
        detailsView.setVisible(false);
        dataTable.setVisible(true);
        addButton.setVisible(true);
    }

    @FXML
    private void handleChange(){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(PasswordManagerApplication.class.getResource("EditData.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            EditDataController controller = fxmlLoader.getController();
            controller.setCurrentUser(currentUser);
            controller.setDataToEdit(selectedData);

            Stage stage = new Stage();
            stage.setTitle("Edit Data");
            stage.setScene(scene);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
