package com.textme.client.controllers;

import com.textme.client.service.Connector;
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

public class LoginController {
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
    public void loginUser(ActionEvent actionEvent) throws IOException, ExecutionException, InterruptedException {
        if (passCheckBox.isSelected()) {
            passField.setText(textPassField.getText());
        }
        if (!loginField.getText().isBlank() || !passField.getText().isBlank()) {
            String login = loginField.getText();
            String pass = passField.getText();
            boolean check = Connector.getClient().loginUser(login, pass);
            Alert alert;
            if (check) {
                alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText("You've login as " + login + "!");
                alert.showAndWait();
                Connector.getClient().setClientLogin(login); // save users login

                GUISceneService service = new GUISceneService();
                Node node = ((Node)actionEvent.getSource());
                String CSS = Objects.requireNonNull(Main.class.
                        getResource("massages-stylesheet.css")).toExternalForm();
                Stage stage = service.createStage("massages-view.fxml", node, CSS);
                stage.setResizable(false);
            }
            else {
                alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Login error!");
                alert.setHeaderText("Check your login and password");
                alert.setContentText("Try again with correct login and password or register if you`re new user");
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

}
