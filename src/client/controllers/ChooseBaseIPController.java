package client.controllers;

import client.main.Client;
import client.models.ServerRowInfo;
import client.models.connection.BroadCastSender;
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
 * Controller for the ChooseBaseIP.fxml file
 */
public class ChooseBaseIPController implements Runnable{

    @FXML
    public TextField IP;
    public Button search, cancel;

    private Stage stage;

    private boolean searched = false;
    private ServerRowInfo[] serverRowInfo;
    private BroadCastSender broadCastSender;


    /**
     * Get method for ChooseBaseIPController
     * @return An instance of the ChooseBaseIPController
     */
    public static ChooseBaseIPController getInstance(){
        ChooseBaseIPController instance = new ChooseBaseIPController();
        instance.setStage();

        return instance;
    }

    /**
     * Set & initialize method for the ChooseBaseIPView GUI
     */
    public void setStage() {
        try {
            AnchorPane parent = FXMLLoader.load(getClass()
                    .getResource("../resources/fxml/ChooseBaseIPView.fxml"));

            Scene scene = new Scene(parent);

            this.stage = new Stage();
            this.stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * EventHandler used to handle click events on the search button
     */
    public void onSearchButtonClicked() {
        String serverIP = this.IP.getText();

        /*
        Check if entered IP is a valid IP
         */
        if (!Constants.IPV4_REGEX.matcher(serverIP).matches()){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error Message");
            alert.setHeaderText(null);
            alert.setContentText("Please Enter a valid IP (IPv4)");
            alert.showAndWait();
        } else {
            this.broadCastSender = new BroadCastSender(serverIP);
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
     * @return A stage containing the GUI of the fxml file
     */
    public Stage getStage(){
        return this.stage;
    }

    @Override
    public void run() {
        /*
        Check if broadcast operations where concluded
         */
        if(!this.searched) {
            this.serverRowInfo = ObjectParser.getInstance()
                    .constructServerInfo(this.broadCastSender.getResults());

            this.searched = true;
            Platform.runLater(this);
        } else {
            Client.chooseServerController = ChooseServerController.getInstance();
            Client.chooseServerController.setObservableList(this.serverRowInfo);
            Client.chooseServerController.getStage().show();

            Client.chooseBaseIPController.getStage().close();
            Client.loaderController.getStage().close();
        }

    }
}
