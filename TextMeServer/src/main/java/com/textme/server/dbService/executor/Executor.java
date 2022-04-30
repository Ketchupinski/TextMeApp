package com.textme.server.dbService.executor;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Executor {

    private final Connection connection;

    public Executor(Connection connection) {
        this.connection = connection;
    }

    public void execUpdate(String update) {
        try {
            Statement stmt = connection.createStatement();
            stmt.execute(update);
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public <T> T execQuery(String query, ResultHandler<T> handler) {
        T value = null;
        try {
            Statement stmt = connection.createStatement();
            stmt.execute(query);
            ResultSet result = stmt.getResultSet();
            value = handler.handle(result);
            result.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return value;
    }
}
