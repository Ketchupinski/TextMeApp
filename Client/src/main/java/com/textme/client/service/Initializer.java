package com.textme.client.service;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import org.davidmoten.text.utils.WordWrap;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Objects;


public class Initializer {
    private static final String BUBBLE_CLASS = "bubble";
    private static final PseudoClass BUBBLE_IN_PSEUDO_CLASS =
            PseudoClass.getPseudoClass("incoming");
    private static final PseudoClass BUBBLE_OUT_PSEUDO_CLASS =
            PseudoClass.getPseudoClass("out-coming");
    @FXML
    public void newMessage(ScrollPane sPane, VBox vbox, String message, String fromUser, String toUser) {
        List<String> messageText = processMessage(message);
        for (String m : messageText) {
            Label label = messageProp(fromUser, toUser);
            label.setText(m);
            vbox.getChildren().add(label);
            sPane.setContent(vbox);
        }
    }

    public Label messageProp(String fromUser, String toUser) {
        Label label = new Label();
        label.getStyleClass().add(BUBBLE_CLASS);
        if (Objects.equals(fromUser, Connector.getClient().getClientLogin())) {
            label.pseudoClassStateChanged(BUBBLE_OUT_PSEUDO_CLASS, true);
        } else {
            label.pseudoClassStateChanged(BUBBLE_IN_PSEUDO_CLASS, true);
        }
        return label;
    }

    public List<String> processMessage(String text) {
        return WordWrap.from(text).maxWidth(40).insertHyphens(true).wrapToList();
    }

}
