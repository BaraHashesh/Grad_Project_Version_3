package client.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.io.IOException;

public class LoaderController {

    @FXML
    public WebView loader;

    private Stage stage;

    public Stage getStage(){
        try {
            AnchorPane parent = FXMLLoader.load(getClass()
                    .getResource("../resources/fxml/LoaderView.fxml"));

            Scene scene = new Scene(parent);

            loader = (WebView) scene.lookup("#loader");

            WebEngine engine = loader.getEngine();


            String url = getClass().getResource("../resources/html/Loader.html").toExternalForm();

            engine.load(url);

            this.stage = new Stage();
            this.stage.setScene(scene);

            return this.stage;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
