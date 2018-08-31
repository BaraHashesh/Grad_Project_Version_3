package client.controllers;

import client.models.ServerRowInfo;
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

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for the ChooseServer.fxml file
 */
public class ChooseServerController implements Initializable {

    @FXML
    public TableView<ServerRowInfo> serverInfoTable;
    public Button select, cancel;

    private ObservableList<ServerRowInfo> observableList = FXCollections.observableArrayList();

    private Stage stage;


    /**
     * Get method for ChooseServerController
     * @return An instance of ChooseServerController object
     */
    public static ChooseServerController getInstance() {
        ChooseServerController instance = new ChooseServerController();
        instance.setStage();

        return instance;
    }

    /**
     * Get & initialize method for the ChooseBaseIPView GUI
     */
    public void setStage() {
        try {
            AnchorPane parent = FXMLLoader.load(getClass()
                    .getResource("../resources/fxml/ChooseServer.fxml"));

            Scene scene = new Scene(parent);

            this.serverInfoTable = (TableView<ServerRowInfo>) scene.lookup("#serverInfoTable");

            this.stage = new Stage();
            this.stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Set method for observableList
     * @param ServerRowInfo Is a a list of ServerRowInfo objects
     */
    public void setObservableList(ServerRowInfo... ServerRowInfo){
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
        if(server != null) {
//            FileRowData[] listOfFiles = new BrowsingClient(server.getIp()).browserRequest("");
            System.out.println(server.getIp());
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error Message");
            alert.setHeaderText(null);
            alert.setContentText("Please select a server");
            alert.showAndWait();
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
    public void initialize(URL location, ResourceBundle resources) {
        TableColumn<ServerRowInfo, String> ip = new TableColumn<>("Server IP");

        ip.setCellValueFactory(new PropertyValueFactory<>("ip"));

        serverInfoTable.getColumns().clear();
        serverInfoTable.getColumns().add(ip);
    }
}
