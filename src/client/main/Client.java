package client.main;

import client.models.FileRowData;
import client.models.connection.BroadCastSender;
import client.models.connection.UploadClient;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import shared.BasicFileData;
import shared.JsonParser;

import java.io.File;

/**
 * Main class for the client
 */
public class Client extends Application {

    public static void main(String[] args) {
        System.setProperty("java.net.preferIPv4Stack", "true");

//        BroadCastSender broadCastSender = new BroadCastSender("127.0.0.1");
//        broadCastSender.start();
//        System.out.println(broadCastSender.getResults());
//        new UploadClient("127.0.0.1").start(
//                new File("/home/barahashesh/Desktop/JetBrainsLic.txt"),
//                "/home/barahashesh"
//        );
//
        Application.launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.show();
    }

}
