package com.textme.client;

import com.textme.connection.Connection;
import com.textme.connection.PackageType;
import com.textme.connection.Package;
import javafx.concurrent.Task;
import javafx.fxml.FXML;

import java.io.IOException;
import java.net.Socket;
import java.sql.Timestamp;
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

    public Connection getConnection() {
        return connection;
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
                    if (receive.getType() == PackageType.USER_LOGIN_SUCCESSFULLY) {
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

    public void getDialoguesList() throws IOException,
            ClassNotFoundException, ExecutionException, InterruptedException {
        Package pack = new Package(PackageType.GET_USER_DIALOGUES, clientLogin, "");
        connection.send(pack);
    }

    public void getDialogueMessages(String toUser)
            throws IOException, ExecutionException, InterruptedException {
        Package pack = new Package(PackageType.GET_USERS_MESSAGES, clientLogin, toUser);
        connection.send(pack);
    }

    public void sendNewTextMessage(String toUser, String text) throws IOException {
        Package pack = new Package(PackageType.SEND_TEXT_MESSAGE,
                text, clientLogin, toUser, new Timestamp(System.currentTimeMillis()));
        connection.send(pack);
    }
}
