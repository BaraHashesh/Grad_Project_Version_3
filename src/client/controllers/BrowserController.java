package client.controllers;

import client.models.FileRowData;
import client.models.connection.BrowsingClient;
import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for the BrowserView.fxml file
 */
public class BrowserController implements Initializable {

    @FXML
    public Label pathLabel;

    @FXML
    public Button backButton, deleteButton, downloadButton, uploadFileButton, uploadFolderButton;

    @FXML
    public TableView<FileRowData> fileTable;

    private ObservableList<FileRowData> observableList = FXCollections.observableArrayList();

    private Stage stage;

    private static String serverIP;
    private static BrowsingClient browsingClient;
    private String parentDirectory = ""; //used to store the path of the previous directory

    /**
     * Get method for BrowserController
     * @return An instance of BrowserController
     */
    public static BrowserController getInstance() {
        BrowserController browserController = new BrowserController();
        browserController.setStage();
        return browserController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        TableColumn<FileRowData, Image> image = new TableColumn<>("");
        TableColumn<FileRowData, String> name = new TableColumn<>("name");
        TableColumn<FileRowData, String> type = new TableColumn<>("type");
        TableColumn<FileRowData, String> date = new TableColumn<>("Last Modified");
        TableColumn<FileRowData, String> size = new TableColumn<>("size");

        image.setCellValueFactory(new PropertyValueFactory<>("icon"));
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        type.setCellValueFactory(new PropertyValueFactory<>("type"));
        date.setCellValueFactory(new PropertyValueFactory<>("modifiedDate"));
        size.setCellValueFactory(new PropertyValueFactory<>("sizeInfo"));

        fileTable.getColumns().clear();

        fileTable.getColumns().add(image);
        fileTable.getColumns().add(name);
        fileTable.getColumns().add(type);
        fileTable.getColumns().add(date);
        fileTable.getColumns().add(size);

        fileTable.setRowFactory( tv -> {
            TableRow<FileRowData> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    this.handleRowDoubleClick();
                }
            });
            return row ;
        });
    }

    /**
     * Method used to handle double click on rows in the table
     */
    private void handleRowDoubleClick() {
        FileRowData fileRowData = fileTable.getSelectionModel().getSelectedItem();

        /*
        Check if a file was selected and it is a directory
         */
        if(fileRowData != null && fileRowData.isDirectory()) {
            FileRowData[] result = browsingClient.browserRequest(fileRowData.getPath());

            /*
            Check if browse was successful
             */
            if(result != null) {
                this.parentDirectory = fileRowData.getParent();
                /*
                Check if parent isn't null
                 */
                if(this.parentDirectory == null)
                    this.parentDirectory = "";

                this.setObservableList(result);
                this.pathLabel.setText(fileRowData.getPath());
            }
        }
    }

    /**
     * Set & initialize method for the BrowserView GUI
     */
    private void setStage() {
        try {
            AnchorPane parent = FXMLLoader.load(getClass()
                    .getResource("../resources/fxml/BrowserView.fxml"));

            Scene scene = new Scene(parent);

            this.fileTable = (TableView<FileRowData>) scene.lookup("#fileTable");

            this.stage = new Stage();
            this.stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * EventHandler used to handle click events on the back button
     */
    public void onBackButtonClicked() {
        /*
        Check if there're files in the current directory
         */
        if(observableList.toArray().length == 0) {

            FileRowData[] result = browsingClient.browserRequest(this.parentDirectory);

            /*
            Check if browse was successful
             */
            if(result != null) {
                this.setObservableList(result);
                this.pathLabel.setText(this.parentDirectory);
            }
        } else  {
            String path = this.observableList.get(0).getPreviousDirectory();

            FileRowData[] result = browsingClient.browserRequest(path);

            /*
            Check if browse was successful
             */
            if(result != null) {
                this.setObservableList(result);
                this.pathLabel.setText(
                        this.observableList.get(0).getParent());
            }
        }
    }

    /**
     * EventHandler used to handle click events on the delete button
     */
    public void onDeleteButtonClicked() {

    }
    /**
     * EventHandler used to handle click events on the download button
     */
    public void onDownloadButtonClicked() {

    }

    /**
     * EventHandler used to handle click events on the upload file button
     */
    public void onUploadFileButtonClicked() {

    }

    /**
     * EventHandler used to handle click events on the upload folder button
     */
    public void onUploadFolderButtonClicked() {

    }

    /**
     * Set method for ServerIP
     * @param IP The ip of the server
     */
    public void setIP(String IP) {
        serverIP = IP;
        browsingClient = new BrowsingClient(serverIP);

        FileRowData[] result = new BrowsingClient(serverIP).browserRequest("");

         /*
            Check if browse was successful
             */
        if(result != null) {
            this.setObservableList(result);
        }
    }

    /**
     * Set method for observableList
     * @param fileRowData Is a a list of FileRowData objects
     */
    public void setObservableList(FileRowData... fileRowData){
        this.observableList.clear();
        this.observableList.addAll(fileRowData);
        this.fileTable.setItems(this.observableList);
    }

    /**
     * Get method for stage
     * @return A stage containing the GUI of the fxml file
     */
    public Stage getStage(){
        return this.stage;
    }

    /**
     * Method used to display a warning alert to the user
     */
    private void showAlert() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Invalid action");
        alert.setHeaderText(null);
        alert.setContentText("Please select a File");
        alert.showAndWait();
    }
}
