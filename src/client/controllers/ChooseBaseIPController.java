package client.controllers;

import client.main.Client;
import client.models.connection.DiscoverySender;
import client.models.controllers.AlertHandler;
import client.models.models.ServerRowInfo;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import shared.Constants;
import shared.ObjectParser;

import java.io.IOException;

/**
 * Controller for the ChooseBaseIPView.fxml file
 */
public class ChooseBaseIPController implements Runnable {

    @FXML
    public TextField IP;
    public Button search, cancel;

    private Stage stage;

    private boolean searched = false;
    private ServerRowInfo[] serverRowInfo;
    private DiscoverySender broadCastSender;
    private String baseIP;

    private static ChooseBaseIPController instance;

    /**
     * Get method for instance
     *
     * @return An instance of the ChooseBaseIPController
     */
    public static ChooseBaseIPController getInstance() {
        /*
        Check if instance is already set
         */
        if (instance == null) {
            instance = new ChooseBaseIPController();
            instance.setStage();
        }

        return instance;
    }

    /**
     * Set & initialize method for the ChooseBaseIPView GUI
     */
    private void setStage() {
        try {
            AnchorPane parent = FXMLLoader.load(getClass()
                    .getResource("/client/resources/fxml/ChooseBaseIPView.fxml"));

            Scene scene = new Scene(parent);

            this.stage = new Stage();

            this.stage.setScene(scene);

            this.stage.setTitle("IP Selector");

            this.stage.setResizable(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * EventHandler used to handle click events on the search button
     */
    public void onSearchButtonClicked() {
        this.baseIP = this.IP.getText();

        /*
        Check if entered IP is a valid IP
         */
        if (!Constants.IPV4_REGEX.matcher(this.baseIP).matches()) {
            AlertHandler.getInstance().start("Invalid IP",
                    "Please Enter a valid IP (IPv4)", Alert.AlertType.ERROR);

        } else {
            this.broadCastSender = new DiscoverySender(this.baseIP);
            this.broadCastSender.start();

            Client.chooseBaseIPController.getStage().hide();

            Client.loaderController = LoaderController.getInstance();
            Client.loaderController.getStage().show();

            new Thread(this).start();
        }
    }

    /**
     * EventHandler used to handle click events on the cancel button
     */
    public void onCancelButtonClicked() {
        System.exit(1);
    }

    /**
     * Get method for stage
     *
     * @return A stage containing the GUI of the fxml file
     */
    public Stage getStage() {
        return this.stage;
    }

    @Override
    public void run() {
        /*
        Check if broadcast operations where concluded
         */
        if (!this.searched) {
            this.serverRowInfo = ObjectParser.getInstance()
                    .constructServerInfo(this.broadCastSender.getResults());

            this.searched = true;
            Platform.runLater(this);
        } else {
            Client.chooseServerController = ChooseServerController.getInstance();
            Client.chooseServerController.setObservableList(this.serverRowInfo);
            Client.chooseServerController.setIP(this.baseIP);
            Client.chooseServerController.getStage().show();

            Client.chooseBaseIPController.getStage().close();
            Client.loaderController.getStage().close();
        }

    }
}
