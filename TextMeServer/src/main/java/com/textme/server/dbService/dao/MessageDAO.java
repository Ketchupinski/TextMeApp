package com.textme.server.dbService.dao;

import com.textme.server.dbService.dataSets.MessagesDataSet;
import com.textme.server.dbService.executor.Executor;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class MessageDAO {
    private final Executor executor;

    public MessageDAO(Connection connection) {
        this.executor = new Executor(connection);
    }

    public MessagesDataSet getMessageByID(long id) {
        return executor.execQuery("select * from messages where id=" + id, result -> {
            result.next();
            return new MessagesDataSet(result.getLong(1),
                    result.getString(2),
                    result.getLong(3),
                    result.getLong(4),
                    result.getTimestamp(5)
            );});
    }

    public Queue<MessagesDataSet> getMessagesByUsers(long fromUserId, long toUserId) throws SQLException {
        return executor.execQuery("select * from messages where " +
                "from_user=" + fromUserId + "AND to_user=" + toUserId, result -> {
            Queue<MessagesDataSet> list = new LinkedList<>();
            if (result.next()) {
                do {
                    list.add(new MessagesDataSet(result.getLong(1),
                            result.getString(2),
                            result.getLong(3),
                            result.getLong(4),
                            result.getTimestamp(5)
                    ));
                } while (result.next());
                return list;
            }
            else
                return null;
        });
    }

    public Queue<Long> getUserDialogues(long fromUserId) throws SQLException {
        return executor.execQuery("select * from messages where " +
                "from_user=" + fromUserId + "or to_user=" + fromUserId, result -> {
            Queue<Long> list = new LinkedList<>();
            if (result.next()) {
                do {
                    if (result.getLong(3) == fromUserId) {
                        if (!list.contains(result.getLong(4))
                        ) {
                            list.add(result.getLong(4));
                        };
                    } else if (result.getLong(4) == fromUserId) {
                        if (!list.contains(result.getLong(3))
                        ) {
                            list.add(result.getLong(3));
                        };
                    }
                } while (result.next());
                return list;
            }
            else
                return null;
        });
    }

    public void insertMessage(String text, long fromUser, long toUser, Timestamp time) throws SQLException {
        executor.execUpdate("insert into messages (message_text, from_user, to_user, message_time) " +
                "values ('" + text + "', '" + fromUser + "', '" + toUser + "', '" + time + "')");
    }
}
