package client.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

/**
 * LoaderController is the controller class for the Loader.fxml file
 */
public class LoaderController {

    private static LoaderController instance;
    @FXML
    private WebView loaderWebView;
    private Stage stage;

    /**
     * Get method for instance
     *
     * @return An instance of LoaderController object
     */
    public static LoaderController getInstance() {
        /*
        Check if instance is already set
         */
        if (instance == null) {
            try {
                FXMLLoader loader = new FXMLLoader(ChooseServerController.class
                        .getResource("/client/resources/fxml/LoaderView.fxml"));

                AnchorPane parent = loader.load();

                // Get the controller of the loaded scene
                instance = loader.getController();

                Scene scene = new Scene(parent);

                instance.loaderWebView = (WebView) scene.lookup("#loader");

                WebEngine engine = instance.loaderWebView.getEngine();


                String url = LoaderController.class
                        .getResource("/client/resources/html/Loader.html").toExternalForm();

                engine.load(url);

                instance.stage = new Stage();
                instance.stage.setScene(scene);

                instance.stage.setTitle("Loading");

                instance.stage.setResizable(false);

                instance.stage.setOnCloseRequest(e->System.exit(1));

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return instance;
    }

    /**
     * Get method for stage
     *
     * @return A stage containing the GUI of the fxml file
     */
    Stage getStage() {
        return this.stage;
    }
}
