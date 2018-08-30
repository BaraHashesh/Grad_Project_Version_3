package client.controllers;

import client.models.FileRowData;
import client.models.ServerRowInfo;
import client.models.connection.BrowsingClient;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Controller for the ChooseServer.fxml file
 */
public class ChooseServerController {

    @FXML
    public TableView<ServerRowInfo> serverInfoTable;
    public Button select, cancel;

    private Stage stage;

    /**
     * Get & initialize method for the ChooseBaseIPView GUI
     * @return A Stage containing the ChooseBaseIPView GUI
     */
    public Stage getStage() {
        try {
            AnchorPane parent = FXMLLoader.load(getClass()
                    .getResource("../resources/fxml/ChooseServer.fxml"));

            Scene scene = new Scene(parent);

            this.stage = new Stage();
            this.stage.setScene(scene);

            return this.stage;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
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
            FileRowData[] listOfFiles = new BrowsingClient(server.getIP()).browserRequest("");
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
        this.stage.close();
        System.exit(1);
    }
}
