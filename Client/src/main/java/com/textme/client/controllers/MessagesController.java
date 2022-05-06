package com.textme.client.controllers;

import com.textme.client.Main;
import com.textme.client.service.Initializer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.Queue;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;

public class MessagesController implements Initializable {
    private static final Initializer initializer = new Initializer();

    public static Initializer getInitializer() {
        return initializer;
    }
    @FXML
    public ListView<String> messagesList;
    @FXML
    public TextField messageText;
    @FXML
    public ScrollPane textPane;
    @FXML
    public Button sendMessageButton;

    public VBox messagesBox;

    @FXML
    public void sendMessage() throws IOException {
        if (!messageText.getText().isEmpty()) {
            Main.getClient().sendNewTextMessage(
                    messagesList.getSelectionModel().getSelectedItem(),
                    messageText.getText());
            initializer.newMessage(textPane, messagesBox, messageText.getText(), false);
            messageText.clear();
        }
    }

    @FXML
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String[] dialogues;
        try {
            dialogues = Main.getClient().getDialoguesList();
        } catch (IOException | ClassNotFoundException | ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        messagesList.getItems().addAll(dialogues);
        messagesList.getSelectionModel().selectedItemProperty().addListener((observableValue, s, t1) -> {
            messagesBox.getChildren().clear();
            String currentSelected = messagesList.getSelectionModel().getSelectedItem();
            try {
                Queue<String[]> messages = Main.getClient().getDialogueMessages(currentSelected);
                initializer.processDialogueMessages(messages, textPane, messagesBox);
                Main.getClient().listenMassages(currentSelected, textPane, messagesBox); // starting thread that listen messages
            } catch (IOException | ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
