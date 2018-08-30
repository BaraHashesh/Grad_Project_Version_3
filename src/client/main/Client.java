package client.main;

import client.models.connection.UploadClient;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.File;

/**
 * Main class for the client
 */
public class Client extends Application {

    public static void main(String[] args) {
        System.setProperty("java.net.preferIPv4Stack", "true");

        new UploadClient("127.0.0.1").start(
                new File("/home/barahashesh/Desktop/JetBrainsLic.txt"),
                "/home/barahashesh"
        );

        Application.launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
    }

}
