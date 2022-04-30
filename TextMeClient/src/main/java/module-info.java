module com.textme.client.textmeclient {
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

    opens com.textme.client.gui to javafx.fxml;
    exports com.textme.client.gui;
    exports com.textme.client.gui.controllers;
    opens com.textme.client.gui.controllers to javafx.fxml;
    exports com.textme.client.gui.service;
    opens com.textme.client.gui.service to javafx.fxml;
}