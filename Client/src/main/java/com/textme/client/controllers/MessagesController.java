package com.textme.client.controllers;

import com.textme.client.service.ListenService;
import com.textme.client.service.Connector;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;

public class MessagesController implements Initializable {
    @FXML
    public ListView<String> messagesList;
    @FXML
    public TextField messageText;
    @FXML
    public ScrollPane textPane;
    @FXML
    public Button sendMessageButton;
    @FXML
    public VBox messagesBox;

    private final ListenService listenService = new ListenService(this);
    public Button newDialogue;
    public TextField newDialogueField;
    public Label userNick;

    @FXML
    public void sendMessage() throws IOException {
        if (!messageText.getText().isEmpty()) {
            Connector.getClient().sendNewTextMessage(
                    messagesList.getSelectionModel().getSelectedItem(),
                    messageText.getText());
            messageText.clear();
        }
    }

    @FXML
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        userNick.setText(Connector.getClient().getClientLogin());
        listenService.start();
        try {
            Connector.getClient().getDialoguesList();
        } catch (IOException | ClassNotFoundException | ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        messagesList.getSelectionModel().selectedItemProperty().addListener((observableValue, s, t1) -> {
            messagesBox.getChildren().clear();
            String currentSelected = messagesList.getSelectionModel().getSelectedItem();
            try {
                Connector.getClient().getDialogueMessages(currentSelected);
            } catch (IOException | ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }


    public void startNewDialogue(ActionEvent e)
            throws IOException, ExecutionException, ClassNotFoundException, InterruptedException {
        if (!newDialogueField.getText().isEmpty()) {
            Connector.getClient().sendNewTextMessage(newDialogueField.getText(), "Hi!");
            Connector.getClient().getDialoguesList();
        }
    }
}
