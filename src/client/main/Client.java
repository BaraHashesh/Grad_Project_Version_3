package client.main;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Main class for the client
 */
public class Client extends Application {

    public static void main(String[] args) {
        System.setProperty("java.net.preferIPv4Stack", "true");

        Application.launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

    }
}
