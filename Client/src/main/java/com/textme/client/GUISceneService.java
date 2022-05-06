package com.textme.client;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class GUISceneService {
    public Stage createStage(String sceneName, Node node, String CSS) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(sceneName)));
        Stage stage = ((Stage) node.getScene().getWindow());
        Scene scene = new Scene(root);
        scene.getStylesheets().add(CSS);
        stage.setScene(scene);
        stage.setTitle("TextMe");
        return stage;
    }
}
