package com.textme.client;

import com.textme.client.controllers.MessagesController;
import com.textme.connection.Connection;
import com.textme.connection.PackageType;
import com.textme.connection.Package;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ExecutionException;

public class Client {
    private String clientLogin;

    Socket socket;
    Connection connection;

    public Client() throws IOException {
        socket = new Socket("127.0.0.1", 8080);
        connection = new Connection(socket);
    }

    public String getClientLogin() {
        return clientLogin;
    }

    public void setClientLogin(String clientLogin) {
        this.clientLogin = clientLogin;
    }

    public boolean registerUser(String login, String password) throws IOException, ExecutionException, InterruptedException {
        Package pack = new Package(PackageType.USER_REGISTRATION, login + " " + password);
        connection.send(pack);
        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                while (true) {
                    Package receive = connection.receive();
                    if (receive.getType() == PackageType.USER_REGISTERED_SUCCESSFULLY) {
                        return true;
                    } else if (receive.getType() == PackageType.USER_REGISTERED_ERROR) {
                        return false;
                    }
                }
            }
        };
        new Thread(task).start();
        return task.get();
    }


    @FXML
    public Boolean loginUser(String login, String pass) throws IOException, ExecutionException, InterruptedException {
        Package pack = new Package(PackageType.USER_LOGIN, login + " " + pass);
        connection.send(pack);
        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                while (true) {
                    Package receive = connection.receive();
                    if (receive.getType() == PackageType.USER_LOGIN_SUCCESSFULLY) { // todo: провірити, мб зависає через те, що нема ніяких повідомлень в ініціалізації
                        return true;
                    } else if (receive.getType() == PackageType.USER_LOGIN_ERROR) {
                        return false;
                    }
                }
            }
        };
        new Thread(task).start();
        return task.get();
    }

    public String[] getDialoguesList() throws IOException,
            ClassNotFoundException, ExecutionException, InterruptedException {
        Package pack = new Package(PackageType.GET_USER_DIALOGUES, clientLogin, "");
        connection.send(pack);
        Task<String[]> task = new Task<>() {
            @Override
            protected String[] call() throws Exception {
                while (true) {
                    Package receive = connection.receive();
                    if (receive.getType() == PackageType.SEND_USER_DIALOGUES) {
                        return receive.getMessageText().split(",");
                    }
                }
            }
        };
        new Thread(task).start();
        return task.get();
    }

    public Queue<String[]> getDialogueMessages(String toUser)
            throws IOException, ExecutionException, InterruptedException {
        Package pack = new Package(PackageType.GET_USERS_MESSAGES, clientLogin, toUser);
        connection.send(pack);
        Task<Queue<String[]>> task = new Task<>() {
            @Override
            protected Queue<String[]> call() throws Exception {
                Queue<String[]> messages = new LinkedList<>();
                while (true) {
                    Package receive = connection.receive();
                    if (receive.getType() == PackageType.SEND_USER_MESSAGES) {
                        String[] messageInfo = new String[3];
                        messageInfo[0] = receive.getMessageText();
                        messageInfo[1] = receive.getFromUser();
                        messageInfo[2] = receive.getToUser(); // todo: сортування повідомлень на сервері
                        messages.add(messageInfo);
                    }
                    if (receive.getType() == PackageType.SEND_USER_MESSAGES_END) {
                        return messages;
                    }
                }
            }
        };
        new Thread(task).start();
        return task.get();
    }

    public void sendNewTextMessage(String toUser, String text) throws IOException {
        Package pack = new Package(PackageType.SEND_TEXT_MESSAGE,
                text, clientLogin, toUser, new Timestamp(System.currentTimeMillis()));
        connection.send(pack);
    }

    public void listenMassages(String FromUser, ScrollPane sPane, VBox messageBox) { // todo: server side
        Task<Void> task = new Task<>() {
            @Override
            public Void call() throws IOException, ClassNotFoundException {
                while (true) {
                    Package receive = connection.receive();
                    if (receive.getType() == PackageType.GET_NEW_MESSAGE
                            && Objects.equals(receive.getToUser(), clientLogin)
                            && Objects.equals(receive.getFromUser(), FromUser)){
                        Platform.runLater(() ->
                                MessagesController.getInitializer().
                                        newMessage(sPane, messageBox, receive.getMessageText(), true));
                    }
                }
            }
        };
        new Thread(task).start();
    }
}
