package org.example.javafxdemo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class PasswordManagerApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        DataBase.createUserTable();
        FXMLLoader fxmlLoader = new FXMLLoader(PasswordManagerApplication.class.getResource("FirstWindow.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);

        stage.setMinWidth(400);
        stage.setMinHeight(300);

        stage.getIcons().addAll(new Image("file:src/main/resources/org/example/javafxdemo/icon16x16.png"),
                new Image("file:src/main/resources/org/example/javafxdemo/icon32x32.png"),
                new Image("file:src/main/resources/org/example/javafxdemo/icon64x64.png"));
        scene.getStylesheets().add(PasswordManagerApplication.class.getResource("styles.css").toExternalForm());
        stage.setTitle("Password Manager");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}