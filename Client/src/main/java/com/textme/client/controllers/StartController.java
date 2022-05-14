package com.textme.client.controllers;

import com.textme.client.GUISceneService;
import com.textme.client.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class StartController {
    @FXML
    Button regButton = new Button();

    @FXML
    Button logButton = new Button();

    @FXML
    Pane pane = new Pane();

    @FXML
    Label logoLabel = new Label();

    @FXML
    Label welcomeLabel = new Label();

    @FXML
    Circle circleOne = new Circle();

    @FXML
    Circle circleTwo = new Circle();

    @FXML
    public void registration(ActionEvent e) throws IOException {
        GUISceneService service = new GUISceneService();
        Node node = ((Node)e.getSource());
        String CSS = Objects.requireNonNull(Main.class.
                getResource("login-registration-stylesheet.css")).toExternalForm();
        Stage stage = service.createStage("registration-view.fxml", node, CSS);
        stage.getIcons().add(new Image(Objects.requireNonNull
                (Main.class.getResourceAsStream("TextMe_icon.png"))));
        stage.setResizable(false);
        stage.show();
    }
    @FXML
    public void authorization(ActionEvent e) throws IOException {
        GUISceneService service = new GUISceneService();
        Node node = ((Node)e.getSource());
        String CSS = Objects.requireNonNull(Main.class.
                getResource("login-registration-stylesheet.css")).toExternalForm();
        Stage stage = service.createStage("login-view.fxml", node, CSS);
        stage.getIcons().add(new Image(Objects.requireNonNull
                (Main.class.getResourceAsStream("TextMe_icon.png"))));
        stage.setResizable(false);
        stage.show();

    }
}
