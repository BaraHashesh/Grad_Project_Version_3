package client.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import shared.Constants;

import java.io.IOException;

/**
 * Controller for the ChooseBaseIPView.fxml file
 */
public class ChooseBaseIPController {

    @FXML
    public TextField IP;
    public Button search, cancel;

    private Stage stage;

    /**
     * Get & initialize method for the ChooseBaseIPView GUI
     * @return A Stage containing the ChooseBaseIPView GUI
     */
    public Stage getStage() {
        try {
            AnchorPane parent = FXMLLoader.load(getClass()
                    .getResource("../resources/fxml/ChooseBaseIPView.fxml"));

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
            System.out.println(serverIP);
        }
    }

    /**
     * EventHandler used to handle click events on the cancel button
     */
    public void onCancelButtonClicked() {
        System.exit(1);
    }

}
