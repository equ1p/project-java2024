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
import java.security.KeyPair;
import java.security.PrivateKey;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MainWindowController {

    public Label MainText;

    public Button addButton;

    public Button LogOutButton;

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

    private PrivateKey rsaPrivateKey;

    public void setCurrentUser(String currentUser) {
        this.currentUser = currentUser;
        System.out.println("MainWindowController: Current user set to: " + currentUser);

        try {
            KeyPair keyPair = KeyStorage.loadUserKeys(currentUser);
            this.rsaPrivateKey = keyPair.getPrivate();
        } catch (Exception e) {
            System.out.println("Error loading private key for user " + currentUser + ": " + e.getMessage());
            e.printStackTrace();
        }
        loadData();
    }

    @FXML
    public void initialize() {

        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        titleColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item);
                    setAlignment(javafx.geometry.Pos.CENTER);
                    setStyle("-fx-font-size: 16px;");
                }
            }
        });
        dataTable.setItems(dataList);

        dataTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

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
                    try {
                        String decryptedTitle = EncryptionUtil.decrypt(rs.getString("title"), rsaPrivateKey);
                        String decryptedLogin = EncryptionUtil.decrypt(rs.getString("login"), rsaPrivateKey);
                        String decryptedPassword = EncryptionUtil.decrypt(rs.getString("password"), rsaPrivateKey);

                        dataList.add(new UserData(
                                rs.getInt("id"),
                                decryptedTitle,
                                decryptedLogin,
                                decryptedPassword,
                                rs.getString("last_modified")
                        ));
                    } catch (Exception e) {
                        System.out.println("Error during decryption: " + e.getMessage());
                        e.printStackTrace();
                    }
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

            stage.setMinWidth(400);
            stage.setMinHeight(350);

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

    @FXML
    private void handleLogOut(){
        currentUser = null;

        Stage currentStage = (Stage) dataTable.getScene().getWindow();

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FirstWindow.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 600, 400);

            Stage loginStage = new Stage();
            loginStage.setTitle("Login");
            loginStage.setScene(scene);
            loginStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
        currentStage.close();
    }

    private void showDetails(UserData data) {
        this.selectedData = data;

        dataTable.setVisible(false);
        addButton.setVisible(false);
        LogOutButton.setVisible(false);
        MainText.setVisible(false);
        detailsView.setVisible(true);

        titleLabel.setText(data.getTitle());
        loginLabel.setText("Login: " + data.getLogin());
        passwordLabel.setText("Password: " + data.getPassword());
        lastModifiedLabel.setText("Last Modified: " + data.getLastModified());
    }

    @FXML
    private void handleBack() {
        loadData();
        detailsView.setVisible(false);
        dataTable.setVisible(true);
        addButton.setVisible(true);
        LogOutButton.setVisible(true);
        MainText.setVisible(true);
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

            stage.setMinWidth(400);
            stage.setMinHeight(300);

            stage.setTitle("Edit Data");
            stage.setScene(scene);
            stage.showAndWait();
            loadData();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
