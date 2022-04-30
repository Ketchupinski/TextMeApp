package com.textme.client.gui.controllers;

import com.textme.client.gui.service.Initializer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

public class MessagesController {
    @FXML
    public ListView<String> messagesList;
    @FXML
    public TextField messageText;
    @FXML
    public ScrollPane textPane;
    @FXML
    public Button sendMessageButton;

    Pane messagesPane = new Pane(); // pane for scrollPane

    @FXML
    public void sendMessage(ActionEvent e) {
        Initializer initializer = new Initializer();
        initializer.addMessages(textPane, messagesPane, "tqwtwqtqwtqwtqwt\nqtwtqwtqwtqwtqwtqwt\nwqtqwtqtwqtqwtwqttw\n");
    }
}
