package com.textme.server.dbService.dao;

import com.textme.server.dbService.dataSets.UsersDataSet;
import com.textme.server.dbService.executor.Executor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Queue;

public class UsersDAO {
    private final Executor executor;

    public UsersDAO(Connection connection) {
        this.executor = new Executor(connection);
    }

    public UsersDataSet getUserByID(long id) throws SQLException {
        return executor.execQuery("select * from users where id=" + id, result -> {
            if (result.next()) {
                return new UsersDataSet(result.getLong(1),
                        result.getString(2),
                        result.getString(3));
            } return null;
        });
    }

    public String getUserLogin(long id) throws SQLException {
        return executor.execQuery("select * from users where id=" + id, result -> {
            if (result.next()) {
                return result.getString(2);
            } return null;
        });
    }

    public long getUserId(String name) throws SQLException {
        return executor.execQuery("select * from users where user_name='" + name + "'", result -> {
            if(result.next()) {
                return result.getLong(1);
            } else {
                return Long.valueOf(0);
            }
        });
    }

    public void insertUser(String name, String pass) throws SQLException {
        executor.execUpdate("insert into users (user_name, user_pass) " +
                "values ('" + name + "', '" + pass + "')");
    }
}
