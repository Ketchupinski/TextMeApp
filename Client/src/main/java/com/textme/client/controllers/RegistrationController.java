package com.textme.client.controllers;

import com.textme.client.GUISceneService;
import com.textme.client.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class RegistrationController {
    @FXML
    public AnchorPane pane = new AnchorPane();
    @FXML
    public Label logoLabel = new Label();
    @FXML
    public Label titleLabel = new Label();
    @FXML
    public TextField loginField = new TextField();
    @FXML
    public PasswordField passField = new PasswordField();
    @FXML
    public Label loginLabel = new Label();
    @FXML
    public Label passLabel = new Label();
    @FXML
    public Button createAccountField = new Button();
    @FXML
    public Button backButton = new Button();
    @FXML
    public CheckBox passCheckBox = new CheckBox();
    @FXML
    public TextField textPassField = new TextField();

    @FXML
    public void backToStart(ActionEvent e) throws IOException {
        GUISceneService service = new GUISceneService();
        Node node = ((Node)e.getSource());
        String CSS = Objects.requireNonNull(Main.class.
                getResource("start-stylesheet.css")).toExternalForm();
        Stage stage = service.createStage("start-view.fxml", node, CSS);
        stage.setResizable(false);
        stage.show();
    }

    @FXML
    public void registerUser(ActionEvent e) throws IOException, ExecutionException, InterruptedException {
        if (passCheckBox.isSelected()) {
            passField.setText(textPassField.getText());
        }
        if (!loginField.getText().isBlank() || !passField.getText().isBlank()) {
            String login = loginField.getText();
            String pass = passField.getText();
            if (Main.getClient().registerUser(login, pass)) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText("You've registered as " + login + "!");
                alert.setContentText("Now you can log in!");
                if(alert.showAndWait().get() == ButtonType.OK) {
                    backToStart(e);
                }
            }
            else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Registration error");
                alert.setHeaderText("Your nick name was already taken!");
                alert.setContentText("Try to log in or input another nick name!");
                alert.showAndWait();
            }
        }
        else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Wrong arguments");
            alert.setHeaderText("You haven't input any information to password or user name field!");
            alert.setContentText("Please, input arguments to user name and password fields");
            alert.showAndWait();
        }
    }

    @FXML
    public void changeVisibility(ActionEvent e) {
        if(passCheckBox.isSelected()) {
            passField.setVisible(false);
            textPassField.setText(passField.getText());
            textPassField.setVisible(true);
            return;
        }
        passField.setVisible(true);
        passField.setText(textPassField.getText());
        textPassField.setVisible(false);
    }
}
