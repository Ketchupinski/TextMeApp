module com.textme.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires java.sql;
    requires com.github.davidmoten.wordwrap;

    opens com.textme.client to javafx.fxml;
    exports com.textme.client;
    exports com.textme.client.controllers;
    opens com.textme.client.controllers to javafx.fxml;
    exports com.textme.client.service;
    opens com.textme.client.service to javafx.fxml;
    opens com.textme.connection;
    exports com.textme.connection to javafx.fxml;
}