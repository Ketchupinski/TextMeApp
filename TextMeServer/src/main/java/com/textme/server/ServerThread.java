package com.textme.server;

import com.textme.connection.Connection;
import com.textme.connection.Package;
import com.textme.connection.PackageType;
import com.textme.server.dbService.DBException;
import com.textme.server.dbService.DBService;
import com.textme.server.dbService.dataSets.MessagesDataSet;
import com.textme.server.dbService.dataSets.UsersDataSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Queue;


public class ServerThread extends Thread {
    private final Socket socket;
    private final DBService dbService;
    private final Logger logger;
    String lastFromUser;
    String lastToUser;

    Queue<MessagesDataSet> startDialogueMessages;
    Queue<String> startDialogues;

    private boolean checkingStarted = false;

    public ServerThread(Socket socket, DBService dbService, Logger logger) {
        this.socket = socket;
        this.dbService = dbService;
        this.logger = logger;
        logger.info("New thread started!");
    }

    @Override
    public void run() {
        try {
            Connection connection = new Connection(socket);
            listenResponse(connection);
            logger.info("Connected to client successfully");
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    private boolean checkEqualsQueuesMessages(Queue<MessagesDataSet> queueOne, Queue<MessagesDataSet> queueTwo) {
        if (queueOne.size() != queueTwo.size()) {
            return false;
        }
        Queue<MessagesDataSet> testOne = new PriorityQueue<>(queueOne);
        Queue<MessagesDataSet> testTwo = new PriorityQueue<>(queueTwo);
        while (!testOne.isEmpty() && !testTwo.isEmpty()) {
            MessagesDataSet messageOne = testOne.poll();
            MessagesDataSet messageTwo = testTwo.poll();
            assert messageTwo != null;
            if (messageOne.getMessageID() != messageTwo.getMessageID()) {
                return false;
            }
        }
        return true;
    }

    private boolean checkEqualsDialogues(Queue<String> queueOne, Queue<String> queueTwo) {
        if (queueOne.size() != queueTwo.size()) {
            return false;
        }
        Queue<String> testOne = new LinkedList<>(queueOne);
        Queue<String> testTwo = new LinkedList<>(queueTwo);
        while (!testOne.isEmpty() && !testTwo.isEmpty()) {
            String messageOne = testOne.poll();
            String messageTwo = testTwo.poll();
            assert messageTwo != null;
            if (!messageOne.equals(messageTwo)) {
                return false;
            }
        }
        return true;
    }

    public void listenResponse(Connection connection)
            throws IOException, ClassNotFoundException, DBException, SQLException {
        while (true) {
            Package response = connection.receive();
            if (response.getType() == PackageType.SEND_TEXT_MESSAGE &&
                    response.getMessageText() != null) {
                String messageText = response.getMessageText();
                try {
                    dbService.addMessage(messageText, response.getFromUser(), response.getToUser());
                    logger.info("New message has been added. From user: " + response.getFromUser() +
                            " To user: " + response.getToUser() +
                            " Message text: " + response.getMessageText());
                } catch (DBException e) {
                    e.printStackTrace();
                }
                Package pack = new Package(
                        PackageType.SEND_TEXT_MESSAGE, response.getMessageText(),
                        response.getFromUser(), response.getToUser(), response.getMessageDate());
                connection.send(pack);
                logger.info("New message has been sent. From user: " + response.getFromUser() +
                        " To user: " + response.getToUser() +
                        " Message text: " + response.getMessageText());
            } else if (response.getType() == PackageType.GET_USERS_MESSAGES
                    && response.getFromUser() != null
                    && response.getToUser() != null
            ) {
                Queue<MessagesDataSet> messages = dbService.getMessages(response.getFromUser(), response.getToUser());
                lastFromUser = response.getFromUser();
                lastToUser = response.getToUser();

                startDialogueMessages = new PriorityQueue<>(messages);
                while (!messages.isEmpty()) {
                    MessagesDataSet top = messages.poll();
                    String toUser = dbService.getUser(top.getToUserID()).getUserName();
                    String fromUser = dbService.getUser(top.getFromUserID()).getUserName();
                    connection.send(new Package(PackageType.UPDATING_MESSAGES,
                            top.getMessageText(),
                            fromUser,
                            toUser,
                            top.getMessageTime()));
                    logger.info("Sent updating messages from user: " + response.getFromUser() + " "
                                    + "to user: " + response.getToUser() + " "
                                    + "message text: " + top.getMessageText()
                            );
                }
                if (!checkingStarted) {
                    Thread checkingNewMessages = new Thread(() -> {
                        while (true) {
                            Queue<MessagesDataSet> currentMessages;
                            Queue<String> currentDialogues;
                            try {
                                Thread.sleep(100);
                                currentMessages = dbService.getMessages(lastFromUser, lastToUser);
                                long id = dbService.getUserID(lastFromUser);
                                currentDialogues = dbService.getUserDialogues(id);
                            } catch (DBException | InterruptedException e) {
                                throw new RuntimeException(e);
                            }

                            if (!checkEqualsQueuesMessages(startDialogueMessages, currentMessages)) {
                                int newMessages = currentMessages.size() - startDialogueMessages.size();
                                System.out.println("newMessages: " + newMessages);

                                Queue<MessagesDataSet> queueOne = new PriorityQueue<>(startDialogueMessages);
                                Queue<MessagesDataSet> queueTwo = new PriorityQueue<>(currentMessages);

                                while (!queueOne.isEmpty() || !queueTwo.isEmpty()) {
                                    if (queueOne.isEmpty()) {
                                        MessagesDataSet messageTwo = queueTwo.poll();
                                        startDialogueMessages.offer(messageTwo);
                                        try {
                                            if (messageTwo.getFromUserID() != dbService.getUserID(lastFromUser)) {
                                                try {
                                                    String fromUser = dbService.getUser(
                                                            messageTwo.getFromUserID()).getUserName();
                                                    String toUser = dbService.getUser(
                                                            messageTwo.getToUserID()).getUserName();
                                                    connection.send(new Package(
                                                            PackageType.INCOME_MESSAGE,
                                                            messageTwo.getMessageText(),
                                                            fromUser,
                                                            toUser,
                                                            messageTwo.getMessageTime()));
                                                    logger.info("Updated user message in dialogue between "
                                                            + lastFromUser + " and " + lastToUser +
                                                            " message text: " + messageTwo.getMessageText());
                                                } catch (DBException | IOException e) {
                                                    logger.error("Updating dialogue error. Cause: " + e.getMessage());
                                                }
                                            }
                                        } catch (DBException e) {
                                            logger.error("DB exception. Cause:" + e.getMessage());
                                        }
                                    } else {
                                        MessagesDataSet messageOne = queueOne.poll();
                                        MessagesDataSet messageTwo = queueTwo.poll();
                                        assert messageTwo != null;
                                        if (messageOne.getMessageID() != messageTwo.getMessageID()) {
                                            startDialogueMessages.offer(messageTwo);
                                            try {
                                                if (messageTwo.getFromUserID() != dbService.getUserID(lastFromUser)) {
                                                    String fromUser = dbService.getUser(
                                                            messageTwo.getFromUserID()).getUserName();
                                                    String toUser = dbService.getUser(
                                                            messageTwo.getToUserID()).getUserName();
                                                    connection.send(new Package(
                                                            PackageType.INCOME_MESSAGE,
                                                            messageTwo.getMessageText(),
                                                            fromUser,
                                                            toUser,
                                                            messageTwo.getMessageTime()));
                                                }
                                                } catch(DBException | IOException e){
                                                    throw new RuntimeException(e);
                                                }
                                        }
                                    }
                                }
                            }
                            if (!checkEqualsDialogues(startDialogues, currentDialogues)) {
                                StringBuilder result = new StringBuilder();
                                startDialogues = new LinkedList<>(currentDialogues);
                                while (!currentDialogues.isEmpty()) {
                                    result.append(currentDialogues.poll()).append(",");
                                }
                                Package pack = new Package(PackageType.SEND_USER_DIALOGUES, result.toString());
                                logger.info("Updated user dialogues to user" + lastFromUser);
                                try {
                                    connection.send(pack);
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                    });
                    checkingNewMessages.setDaemon(true);
                    checkingNewMessages.start();
                    checkingStarted = true;
                }
            } else if (response.getType() == PackageType.USER_REGISTRATION) {
                String info = response.getMessageText();
                String[] information = info.split(" ");
                if (dbService.isUserExist(information[0])) {
                    connection.send(new Package(PackageType.USER_REGISTERED_ERROR,
                            information[0] + " " + information[1]));
                    logger.info("New user registration error: " + information[1] + ". This user already exist!");
                } else {
                    dbService.addUser(information[0], information[1]);
                    String newUserGreetings = PropertiesService.getProperty("greeting_message");
                    dbService.addMessage(newUserGreetings, "admin", information[0]); // greeting message
                    connection.send(new Package(PackageType.USER_REGISTERED_SUCCESSFULLY,
                            information[0] + " " + information[1]));
                    logger.info("New user successfully registered: " + information[1]);
                }
            } else if (response.getType() == PackageType.USER_LOGIN) {
                String info = response.getMessageText();
                String[] information = info.split(" ");
                if (dbService.isUserExist(information[0])) {
                    long uID = dbService.getUserID(information[0]);
                    UsersDataSet user = dbService.getUser(uID);
                    if (Objects.equals(user.getUserPass(), information[1])) {
                        connection.send(new Package(PackageType.USER_LOGIN_SUCCESSFULLY,
                                information[0] + " " + information[1]));
                        logger.info("User " + information[1] + " successfully authorized");
                    } else {
                        connection.send(new Package(PackageType.USER_LOGIN_ERROR,
                                information[0] + " " + information[1]));
                        logger.info("User " + information[1] + " failed authorization");
                    }
                } else {
                    connection.send(new Package(PackageType.USER_LOGIN_ERROR,
                            information[0] + " " + information[1]));
                    logger.info("User " + information[1] + " failed authorization");
                }

            } else if (response.getType() == PackageType.USER_LOG_OUT) {
                logger.info("User " + lastFromUser + " logged out");
                connection.close();
                return;
            } else if (response.getType() == PackageType.GET_USER_DIALOGUES) {
                long id = dbService.getUserID(response.getFromUser());
                Queue<String> dialogues = dbService.getUserDialogues(id);
                startDialogues = new LinkedList<>(dialogues);

                StringBuilder result = new StringBuilder();
                while (!dialogues.isEmpty()) {
                    result.append(dialogues.poll()).append(",");
                }
                Package pack = new Package(PackageType.SEND_USER_DIALOGUES, result.toString());
                connection.send(pack);
                logger.info("User " + response.getFromUser() + " updated dialogues");
            }
        }
    }
}
