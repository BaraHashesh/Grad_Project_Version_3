package client.main;

import client.controllers.ChooseBaseIPController;
import client.controllers.ChooseServerController;
import client.controllers.LoaderController;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Main class for the client
 */
public class Client extends Application {

    public static ChooseBaseIPController chooseBaseIPController;
    public static LoaderController loaderController;
    public static ChooseServerController chooseServerController;

    public static void main(String[] args) {
        System.setProperty("java.net.preferIPv4Stack", "true");
        Application.launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Client.chooseBaseIPController = new ChooseBaseIPController();
        Client.chooseBaseIPController.setStage();
        Client.chooseBaseIPController.getStage().show();
    }
}
