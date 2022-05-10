package com.textme.server;

import com.textme.server.dbService.DBService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

@SuppressWarnings("InfiniteLoopStatement")
public class Server {
    static final Logger logger = LogManager.getLogger(ServerThread.class.getName());
    public static void main(String[] args) throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(8080)) {
            DBService dbService = new DBService();

            while(true) {
                Socket socket = serverSocket.accept();
                new ServerThread(socket, dbService, logger).start();
            }
        }
    }
}
