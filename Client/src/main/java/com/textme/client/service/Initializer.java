package com.textme.client.service;
import com.textme.client.Main;
import com.textme.client.controllers.MessagesController;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import org.davidmoten.text.utils.WordWrap;

import java.util.Objects;
import java.util.Queue;


public class Initializer {
    @FXML
    private final MessagesController controller = new MessagesController();
    private static final String BUBBLE_CLASS = "bubble";
    private static final PseudoClass BUBBLE_IN_PSEUDO_CLASS =
            PseudoClass.getPseudoClass("incoming");
    private static final PseudoClass BUBBLE_OUT_PSEUDO_CLASS =
            PseudoClass.getPseudoClass("out-coming");
    @FXML
    public void newMessage(ScrollPane sPane, VBox vbox, String message, Boolean isIncoming) {
        Label label = messageProp(message, isIncoming);
        vbox.getChildren().add(label);
        sPane.setContent(vbox);
    }
    @FXML
    public void processDialogueMessages(Queue<String[]> messages, ScrollPane sPane, VBox vbox) {
        while(!messages.isEmpty()) {
            String[] top = messages.poll();
            Label label;
            if (Objects.equals(top[1], Main.getClient().getClientLogin())) {
                label = messageProp(top[0], false);
            } else {
                label = messageProp(top[0], true);
            }
            vbox.getChildren().add(label);
            sPane.setContent(vbox);
        }
    }
    public String processMessage(String text) {
        return WordWrap.from(text).maxWidth(35).insertHyphens(true).wrap();
    }
    public Label messageProp(String message, boolean isIncoming) {
        String messageText = processMessage(message);
        Label label = new Label(messageText);
        label.getStyleClass().add(BUBBLE_CLASS);
        if (isIncoming) {
            label.pseudoClassStateChanged(BUBBLE_IN_PSEUDO_CLASS, true);
        } else {
            label.pseudoClassStateChanged(BUBBLE_OUT_PSEUDO_CLASS, true);
        }
        return label;
    }
}
