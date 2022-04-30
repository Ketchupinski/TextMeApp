package com.textme.client;

import com.textme.connection.Connection;
import com.textme.connection.Package;
import com.textme.connection.PackageType;

import java.io.IOException;
import java.net.Socket;

public class Client {
    Socket socket;
    Connection connection;

    public Client() throws IOException {
        socket = new Socket("127.0.0.1", 8080);
        connection = new Connection(socket);
    }

    public boolean registerUser(String login, String password) throws IOException, ClassNotFoundException {
        Package pack = new Package(PackageType.USER_REGISTRATION, login + " " + password);
        connection.send(pack);
        while(true) {
            Package receive = connection.receive();
            if (receive.getType() == PackageType.USER_REGISTERED_SUCCESSFULLY) {
                return true;
            } else if (receive.getType() == PackageType.USER_REGISTERED_ERROR) {
                return false;
            }
        }
    }


    public boolean loginUser(String login, String pass) throws IOException, ClassNotFoundException {
        Package pack = new Package(PackageType.USER_LOGIN, login + " " + pass);
        connection.send(pack);
        while(true) {
            Package receive = connection.receive();
            if(receive.getType() == PackageType.USER_LOGIN_SUCCESSFULLY) {
                return true;
            } else if (receive.getType() == PackageType.USER_LOGIN_ERROR) {
                return false;
            }
        }
    }
}
