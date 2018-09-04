package client.controllers;

import client.main.Client;
import client.models.connection.DiscoverySender;
import client.models.connection.UpdateReceiver;
import client.models.controllers.AlertHandler;
import client.models.models.ServerRowInfo;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import shared.ObjectParser;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for the ChooseServer.fxml file
 */
public class ChooseServerController implements Initializable, Runnable {

    private static String baseIP;
    private static ChooseServerController instance;
    @FXML
    public TableView<ServerRowInfo> serverInfoTable;
    public Button select, cancel, refresh;
    private ObservableList<ServerRowInfo> observableList = FXCollections.observableArrayList();
    private Stage stage;
    private boolean updated;
    private DiscoverySender broadCastSender;

    /**
     * Get method for instance
     *
     * @return An instance of ChooseServerController object
     */
    public static ChooseServerController getInstance() {
        /*
        Check if instance is already set
         */
        if (instance == null) {
            instance = new ChooseServerController();
            instance.setStage();
        }

        return instance;
    }

    /**
     * Get & initialize method for the ChooseBaseIPView GUI
     */
    private void setStage() {
        try {
            AnchorPane parent = FXMLLoader.load(getClass()
                    .getResource("/client/resources/fxml/ChooseServer.fxml"));

            Scene scene = new Scene(parent);

            this.serverInfoTable = (TableView<ServerRowInfo>) scene.lookup("#serverInfoTable");

            this.stage = new Stage();
            this.stage.setScene(scene);

            this.stage.setTitle("Server Browser");

            this.stage.setResizable(false);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Set method for observableList
     *
     * @param ServerRowInfo Is a a list of ServerRowInfo objects
     */
    public void setObservableList(ServerRowInfo... ServerRowInfo) {
        this.observableList.clear();
        this.observableList.addAll(ServerRowInfo);
        this.serverInfoTable.setItems(this.observableList);
    }

    /**
     * EventHandler used to handle click events on the select button
     */
    public void onSelectButtonClicked() {
        ServerRowInfo server = serverInfoTable.getSelectionModel().getSelectedItem();

        /*
        Check if there is a selected server
         */
        if (server != null) {
            new UpdateReceiver().start();

            Client.browserController = BrowserController.getInstance();
            Client.browserController.setIP(server.getIp());
            Client.browserController.getStage().show();

            Client.chooseServerController.getStage().close();
        } else {
            AlertHandler.getInstance().start("Invalid action",
                    "Please select a server", Alert.AlertType.WARNING);
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
    public void initialize(URL location, ResourceBundle resources) {
        TableColumn<ServerRowInfo, String> ip = new TableColumn<>("Server IP");

        ip.setCellValueFactory(new PropertyValueFactory<>("ip"));

        serverInfoTable.getColumns().clear();
        serverInfoTable.getColumns().add(ip);
    }

    /**
     * EventHandler used to handle click events on the refresh button
     */
    public void onRefreshButtonClicked() {
        this.updated = false;

        Client.chooseServerController.getStage().hide();

        Client.loaderController.getStage().show();

        this.broadCastSender = new DiscoverySender(baseIP);

        this.broadCastSender.start();

        new Thread(this).start();
    }

    /**
     * Set method for baseIP
     *
     * @param IP Is the baseIP for the network
     */
    public void setIP(String IP) {
        baseIP = IP;
    }

    @Override
    public void run() {
        /*
        Check if update operation is done
         */
        if (!this.updated) {
            this.setObservableList(ObjectParser.getInstance()
                    .constructServerInfo(this.broadCastSender.getResults()));

            this.updated = true;

            Platform.runLater(this);
        } else {
            Client.loaderController.getStage().close();
            Client.chooseServerController.getStage().show();
        }
    }
}
