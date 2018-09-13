package client.controllers;

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

/**
 * Controller for the ChooseBaseIPView.fxml file
 */
public class ChooseBaseIPController implements Runnable {

    private static ChooseBaseIPController instance;
    @FXML
    public TextField IP;
    public Button search, cancel;
    private Stage stage;
    private boolean searched = false;
    private ServerRowInfo[] serverRowInfo;
    private DiscoverySender broadCastSender;
    private String baseIP;

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
            try {
                FXMLLoader loader = new FXMLLoader(ChooseBaseIPController.class
                        .getResource("/client/resources/fxml/ChooseBaseIPView.fxml"));

                AnchorPane parent = loader.load();

                // Get the controller of the loaded scene
                instance = loader.getController();

                Scene scene = new Scene(parent);

                instance.stage = new Stage();

                instance.stage.setScene(scene);

                instance.stage.setTitle("IP Selector");

                instance.stage.setResizable(false);

                instance.stage.setOnCloseRequest(e-> System.exit(1));

            } catch (Exception e){
                e.printStackTrace();
            }
        }

        return instance;
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

            ChooseBaseIPController.getInstance().getStage().hide();

            LoaderController.getInstance().getStage().show();

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
            ChooseServerController chooseServerController = ChooseServerController.getInstance();
            chooseServerController.setObservableList(this.serverRowInfo);
            chooseServerController.setIP(this.baseIP);
            chooseServerController.getStage().show();

            ChooseBaseIPController.getInstance().getStage().close();
            LoaderController.getInstance().getStage().close();
        }

    }
}
