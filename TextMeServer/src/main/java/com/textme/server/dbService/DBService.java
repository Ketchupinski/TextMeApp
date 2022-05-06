package com.textme.server.dbService;

import com.textme.server.dbService.dao.MessageDAO;
import com.textme.server.dbService.dao.UsersDAO;
import com.textme.server.dbService.dataSets.MessagesDataSet;
import com.textme.server.dbService.dataSets.UsersDataSet;

import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.*;

public class DBService {
    private final Connection connection;

    public DBService() {
        this.connection = getPostgresConnection();
    }

    public synchronized UsersDataSet getUser(long id) throws DBException {
        try {
            return (new UsersDAO(connection).getUserByID(id));
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

    public synchronized long getUserID(String login) throws DBException {
        try {
            return (new UsersDAO(connection).getUserId(login));
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

    public synchronized long addUser(String name, String pass) throws DBException {
        try {
            connection.setAutoCommit(false);
            UsersDAO dao = new UsersDAO(connection);
            if (!isUserExist(name)) { // check if this user already exist
                dao.insertUser(name, pass);
                connection.commit();
                return dao.getUserId(name);
            }
            return 0;
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ignore) {
            }
            throw new DBException(e);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException ignore) {
            }
        }
    }

    public synchronized boolean isUserExist(String name) throws DBException, SQLException {
        try {
            UsersDAO dao = new UsersDAO(connection);
            return dao.getUserId(name) != 0;
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

    public synchronized void addMessage(String text, String fromUserLogin, String toUserLogin) throws DBException {
        try {
            connection.setAutoCommit(false);
            UsersDAO userDAO = new UsersDAO(connection);
            MessageDAO messageDAO = new MessageDAO(connection);
            long fromUserID = userDAO.getUserId(fromUserLogin);
            long toUserID = userDAO.getUserId(toUserLogin);

            messageDAO.insertMessage(text, fromUserID, toUserID, new Timestamp(System.currentTimeMillis()));
            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ignore) {
            }
            throw new DBException(e);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException ignore) {
            }
        }
    }

    public synchronized void addMessage(String text, long fromUserID, long toUserID) throws DBException {
        try {
            connection.setAutoCommit(false);
            MessageDAO messageDAO = new MessageDAO(connection);
            messageDAO.insertMessage(text, fromUserID, toUserID, new Timestamp(System.currentTimeMillis()));
            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ignore) {
            }
            throw new DBException(e);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException ignore) {
            }
        }
    }

    public synchronized Queue<MessagesDataSet> mergingQueues
            (Queue<MessagesDataSet> qOne, Queue<MessagesDataSet> qTwo) {
        Queue<MessagesDataSet> mQueue = new PriorityQueue<>
                (Comparator.comparing(MessagesDataSet::getMessageTime));
        Iterator<MessagesDataSet> i1 = qOne.iterator();
        Iterator<MessagesDataSet> i2 = qTwo.iterator();
        do {
            if (i1.hasNext()) {
                mQueue.add(i1.next());
            }
            if (i2.hasNext()) {
                mQueue.add(i2.next());
            }
        } while (i1.hasNext() || i2.hasNext());
        return mQueue;
    }

    public synchronized Queue<MessagesDataSet> sortQueue(Queue<MessagesDataSet> queue) {
        Queue<MessagesDataSet> mQueue = new PriorityQueue<>
                (Comparator.comparing(MessagesDataSet::getMessageTime));
        mQueue.addAll(queue);
        return mQueue;
    }

    public synchronized Queue<MessagesDataSet> getMessages(String fromUserLogin, String toUserLogin) throws DBException {
        try {
            UsersDAO userDAO = new UsersDAO(connection);
            MessageDAO messageDAO = new MessageDAO(connection);
            long fromUserID = userDAO.getUserId(fromUserLogin);
            long toUserID = userDAO.getUserId(toUserLogin);
            Queue<MessagesDataSet> messagesOne = messageDAO.getMessagesByUsers(fromUserID, toUserID);
            Queue<MessagesDataSet> messagesTwo = messageDAO.getMessagesByUsers(toUserID, fromUserID);
            if (messagesOne != null && messagesTwo != null) {
                return mergingQueues(messagesOne, messagesTwo);
            } else if (messagesOne == null) {
                return sortQueue(messagesTwo);
            } else {
                return sortQueue(messagesOne);
            }
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

    public synchronized Queue<String> getUserDialogues(long fromUser) {
        try {
            MessageDAO messageDAO = new MessageDAO(connection);
            UsersDAO userDAO = new UsersDAO(connection);

            Queue<Long> dialogues = messageDAO.getUserDialogues(fromUser);
            Queue<String> dialoguesNames = new LinkedList<>();

            while(!dialogues.isEmpty()) {
                long userID = dialogues.poll();
                dialoguesNames.add(userDAO.getUserLogin(userID));
            }
            return dialoguesNames;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public synchronized void printConnectInfo() {
        try {
            System.out.println("DB name: " + connection.getMetaData().getDatabaseProductName());
            System.out.println("DB version: " + connection.getMetaData().getDatabaseProductVersion());
            System.out.println("Driver: " + connection.getMetaData().getDriverName());
            System.out.println("Autocommit: " + connection.getAutoCommit());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public synchronized static Connection getPostgresConnection() { // todo: make connection from prop file
        try {
            DriverManager.registerDriver((Driver) Class.forName("org.postgresql.Driver").
                    getDeclaredConstructor().newInstance());

            StringBuilder url = new StringBuilder();
            url.
                    append("jdbc:postgresql://").        //db type
                    append("localhost:").           //host name
                    append("5432/").                //port
                    append("postgres?").          //db name
                    append("user=postgres&").          //login
                    append("password=mady525");       //password

            System.out.println("URL: " + url + "\n");

            return DriverManager.getConnection(url.toString());
        } catch (SQLException | InstantiationException | IllegalAccessException |
                 ClassNotFoundException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }


}
