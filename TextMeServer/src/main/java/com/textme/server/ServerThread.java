package com.textme.server;

import com.textme.connection.Connection;
import com.textme.connection.Package;
import com.textme.connection.PackageType;
import com.textme.server.dbService.DBException;
import com.textme.server.dbService.DBService;
import com.textme.server.dbService.dataSets.MessagesDataSet;
import com.textme.server.dbService.dataSets.UsersDataSet;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Queue;

public class ServerThread extends Thread {
    private final Socket socket;
    private final DBService dbService;

    public ServerThread(Socket socket, DBService dbService) {
        this.socket = socket;
        this.dbService = dbService;
        System.out.println("New thread started!");
    }

    @Override
    public void run() {
        try { // todo: user authorization system
            Connection connection = new Connection(socket);
            listenResponse(connection);
        } catch (Exception ignore) {
        }
    }

    @SuppressWarnings("InfiniteLoopStatement")
    public void listenResponse(Connection connection) throws IOException, ClassNotFoundException, DBException, SQLException {
        while(true) {
            Package response = connection.receive();

            if (response.getType() == PackageType.SEND_TEXT_MESSAGE &&
                    response.getMessageText() != null) {
                String messageText = response.getMessageText();
                try {
                    dbService.addMessage(messageText, response.getFromUser(), response.getToUser());
                } catch (DBException e) {
                    e.printStackTrace();
                }
                System.out.println("Message has been added!");
            } else if (response.getType() == PackageType.GET_USER_MESSAGES
                    && response.getFromUser() != null
                    && response.getToUser() != null
            ) {
                Queue<MessagesDataSet> messages = null;
                messages = dbService.getMessages(response.getFromUser(), response.getToUser());
                MessagesDataSet peek = Objects.requireNonNull(messages).peek();
                while(peek != null) {
                    connection.send(new Package(PackageType.GET_USER_MESSAGES,
                            Objects.requireNonNull(peek).getMessageText(),
                            response.getFromUser(),
                            response.getToUser(),
                            peek.getMessageTime()));
                    messages.remove();
                    peek = messages.peek();
                }

            } else if(response.getType() == PackageType.USER_REGISTRATION) {
                String info = response.getMessageText();
                String[] information = info.split(" ");
                if(dbService.isUserExist(information[0])) {
                    connection.send(new Package(PackageType.USER_REGISTERED_ERROR,
                            information[0] + " " + information[1]));
                } else {
                    dbService.addUser(information[0], information[1]);
                    connection.send(new Package(PackageType.USER_REGISTERED_SUCCESSFULLY,
                            information[0] + " " + information[1]));
                }
            } else if(response.getType() == PackageType.USER_LOGIN) {
                String info = response.getMessageText();
                String[] information = info.split(" ");
                if (dbService.isUserExist(information[0])) {
                    long uID = dbService.getUserID(information[0]);
                    UsersDataSet user = dbService.getUser(uID);
                    if (Objects.equals(user.getUserPass(), information[1])) {
                        connection.send(new Package(PackageType.USER_LOGIN_SUCCESSFULLY,
                                information[0] + " " + information[1]));
                    }
                    else {
                        connection.send(new Package(PackageType.USER_LOGIN_ERROR,
                                information[0] + " " + information[1]));
                    }
                } else {
                    connection.send(new Package(PackageType.USER_LOGIN_ERROR,
                            information[0] + " " + information[1]));
                }

            } else if (response.getType() == PackageType.USER_LOG_OUT) {
                connection.close();
                this.st
                return;
            }
        }
    }

}