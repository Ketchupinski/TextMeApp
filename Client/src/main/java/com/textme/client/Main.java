package com.textme.client;

import com.textme.connection.Package;
import com.textme.connection.PackageType;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;


public class Main extends Application {
    public static void main(String[] args) {
        launch();
    }
    private final String CSS = Objects.requireNonNull(this.getClass().
            getResource("start-stylesheet.css")).toExternalForm();

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("start-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.getIcons().add(new Image(Objects.requireNonNull
                (Main.class.getResourceAsStream("TextMe_icon.png"))));
        scene.getStylesheets().add(CSS);
        stage.setResizable(false);
        stage.setTitle("TextMe");
        stage.setScene(scene);
        stage.show();

        stage.setOnCloseRequest(evt -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                    "Do you really want to close this application?", ButtonType.YES, ButtonType.NO);
            ButtonType result = alert.showAndWait().orElse(ButtonType.NO);
            if (ButtonType.NO.equals(result)) {
                evt.consume();
            } else {
                Package pack = new Package(PackageType.USER_LOG_OUT, "");
            }});
    }


}