package com.textme.client.gui.service;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.ResourceBundle;

public class Initializer implements Initializable {
    @FXML
    public void addToList(ListView<String> list) {
        String[] users = {"Tolik", "Vasya", "Kolyan"};
        list.getItems().addAll(users);
    }

    @FXML
    public void addMessages(ScrollPane sPane, Pane pane, String message) { // todo: Розбивання строки по довжині на декілька(переноси)
        Label label = new Label(message); // todo: добавити counter від повідомлення до повідомлення
        label.setLayoutX(5); // todo: Добавити оформлення повідомлення
        label.setLayoutY(50);

        pane.setLayoutX(5);
        pane.setLayoutY(1000);
        pane.getChildren().add(label);
        sPane.setContent(pane);
        System.out.println(pane.getLayoutX());
        System.out.println(pane.getLayoutY());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {}
}
