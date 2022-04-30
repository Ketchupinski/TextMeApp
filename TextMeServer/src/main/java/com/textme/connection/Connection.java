package com.textme.connection;

import java.io.*;
import java.net.Socket;

public class Connection implements Closeable {
    private final Socket socket;

    private final ObjectOutputStream writer;

    private final ObjectInputStream reader;


    public Connection(Socket socket) throws IOException {
        this.socket = socket;
        this.writer = new ObjectOutputStream(socket.getOutputStream());
        this.reader = new ObjectInputStream(socket.getInputStream());
    }

    public void send(Package p) throws IOException {
        synchronized (this.writer) {
            writer.writeObject(p);
        }
    }

    public Package receive() throws IOException, ClassNotFoundException {
        synchronized (this.reader) {
            return (Package) reader.readObject();
        }
    }

    @Override
    public void close() {
        try {
            socket.close();
            reader.close();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
