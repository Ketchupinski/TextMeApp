package com.textme.server;

import com.textme.connection.Connection;

import java.util.Queue;


public class ServerService {
    private final Connection connection;

    public ServerService(Connection connection) {
        this.connection = connection;
    }


}
