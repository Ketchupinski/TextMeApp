package com.textme.client.service;

import com.textme.client.controllers.MessagesController;
import com.textme.connection.Connection;
import com.textme.connection.Package;
import com.textme.connection.PackageType;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.io.IOException;
import java.util.Objects;

public class ListenService extends Service<Void> {
    private final Initializer initializer;
    private final MessagesController controller;
    private final Connection connection;

    public ListenService(MessagesController controller) {

        this.controller = controller;
        initializer = new Initializer();
        connection = Connector.getClient().getConnection();
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<>() {
            @Override
            protected Void call() throws IOException, ClassNotFoundException, InterruptedException {
                while(true) {
                    Package receive = connection.receive();
                    if (receive.getType() == PackageType.SEND_USER_DIALOGUES) {
                        String[] dialogues = receive.getMessageText().split(",");
                        Platform.runLater(() -> {
                            controller.messagesList.getItems().clear();
                            controller.messagesList.getItems().addAll(dialogues);
                        });
                    } else if(receive.getType() == PackageType.UPDATING_MESSAGES) {
                        Platform.runLater(() -> {
                            initializer.newMessage(controller.textPane, controller.messagesBox,
                                    receive.getMessageText(), receive.getFromUser(), receive.getToUser());
                        });
                    } else if(receive.getType() == PackageType.SEND_TEXT_MESSAGE
                            ||
                            receive.getType() == PackageType.INCOME_MESSAGE) {
                        if ((Objects.equals(
                                receive.getFromUser(), controller.messagesList.getSelectionModel().getSelectedItem())
                                &&
                                Objects.equals(
                                receive.getToUser(), Connector.getClient().getClientLogin()))
                                ||
                            (Objects.equals(
                                receive.getFromUser(), Connector.getClient().getClientLogin())
                                    &&
                                Objects.equals(
                                receive.getToUser(), controller.messagesList.getSelectionModel().getSelectedItem()))
                        ) {
                            // if there is dialogue with user who send us new message
                            Platform.runLater(() -> {
                                initializer.newMessage(controller.textPane, controller.messagesBox,
                                        receive.getMessageText(), receive.getFromUser(), receive.getToUser());
                            });
                        }
                    }
                }
            }
        };
    }
}
