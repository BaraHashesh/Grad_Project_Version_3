package client.controllers;

import client.models.connection.DiscoverySender;
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

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for the ChooseServerView.fxml file
 */
public class ChooseServerController implements Initializable, Runnable {

    private static String baseIP;
    private static ChooseServerController instance;

    @FXML
    public TableView<ServerRowInfo> serverInfoTable;
    @FXML
    public Button select, refresh;

    private ObservableList<ServerRowInfo> observableList = FXCollections.observableArrayList();
    private Stage stage;
    private boolean updated;
    private DiscoverySender broadCastSender;

    private boolean open;

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

            try {
                FXMLLoader loader = new FXMLLoader(ChooseServerController.class
                        .getResource("/client/resources/fxml/ChooseServerView.fxml"));

                AnchorPane parent = loader.load();

                // Get the controller of the loaded scene
                instance = loader.getController();

                instance.open = true;

                Scene scene = new Scene(parent);

                instance.stage = new Stage();
                instance.stage.setScene(scene);

                instance.stage.setTitle("Server Browser");

                instance.stage.setResizable(false);

                instance.stage.setOnCloseRequest(e -> {
                    instance.open = false;

                    /*
                    check if one stage was opened
                     */
                    if (BrowserController.getInstances().size() == 0)
                        System.exit(1);
                });

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return instance;
    }

    /**
     * Set method for observableList
     *
     * @param ServerRowInfo Is a a list of ServerRowInfo objects
     */
    void setObservableList(ServerRowInfo... ServerRowInfo) {
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
            BrowserController browserController = BrowserController.getInstance(server.getIp());
            assert browserController != null; // check if creating browser was successful
            browserController.getStage().show();
        } else {
            AlertHandler.getInstance().start("Invalid action",
                    "Please select a server", Alert.AlertType.WARNING);
        }
    }

    /**
     * Get method for stage
     *
     * @return A stage containing the GUI of the fxml file
     */
    Stage getStage() {
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

        ChooseServerController.getInstance().getStage().hide();

        LoaderController.getInstance().getStage().show();

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
            LoaderController.getInstance().getStage().close();
            ChooseServerController.getInstance().getStage().show();
        }
    }

    /**
     * Get method for open
     *
     * @return Wither the current server chooser is open or not
     */
    boolean isOpen() {
        return open;
    }
}
