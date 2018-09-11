package client.controllers;

import client.models.connection.BrowsingClient;
import client.models.connection.DownloadClient;
import client.models.connection.UploadClient;
import client.models.controllers.AlertHandler;
import client.models.models.FileRowData;
import javafx.application.Platform;
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
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import shared.Constants;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Controller for the BrowserView.fxml file
 */
public class BrowserController implements Initializable {

    private static String serverIP;
    private static BrowsingClient browsingClient;
    private static BrowserController instance;
    @FXML
    public Label pathLabel;
    @FXML
    public Button backButton, deleteButton, downloadButton,
            uploadFileButton, uploadFolderButton, refreshButton;
    @FXML
    public TableView<FileRowData> fileTable;
    private ObservableList<FileRowData> observableList = FXCollections.observableArrayList();
    private Stage stage;

    private ArrayList<String> pathList = new ArrayList<>(10);

    /**
     * Get method for instance
     *
     * @return An instance of BrowserController
     */
    public static BrowserController getInstance() {
        /*
        Check if instance is already set
         */
        if (instance == null) {
            instance = new BrowserController();
            instance.setStage();
        }

        return instance;
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

        fileTable.setRowFactory(tv -> {
            TableRow<FileRowData> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    this.onRowDoubleClick();
                }
            });
            return row;
        });
    }

    /**
     * Method used to handle double click on rows in the table
     */
    private void onRowDoubleClick() {
        FileRowData fileRowData = fileTable.getSelectionModel().getSelectedItem();

        /*
        Check if a file was selected and it is a directory
         */
        if (fileRowData != null && fileRowData.isDirectory()) {
            FileRowData[] result = browsingClient.browserRequest(fileRowData.getPath());

            /*
            Check if browse was successful
             */
            if (result != null) {
                this.pathList.add(fileRowData.getParent());

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
                    .getResource("/client/resources/fxml/BrowserView.fxml"));

            Scene scene = new Scene(parent);

            this.fileTable = (TableView<FileRowData>) scene.lookup("#fileTable");

            this.stage = new Stage();

            this.stage.setScene(scene);

            this.stage.setTitle("File Browser");

            this.stage.setOnCloseRequest(e->{
                System.exit(1);
            });

            this.stage.setResizable(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * EventHandler used to handle click events on the back button
     */
    public void onBackButtonClicked() {
        /*
        Check if pathList is empty
         */
        if (this.pathList.size() != 0) {
            String path = this.pathList.get(this.pathList.size()-1);

            this.pathList.remove(this.pathList.size()-1);

            FileRowData[] result = browsingClient.browserRequest(path);

            this.setObservableList(result);

            this.pathLabel.setText(path);
        }
    }

    /**
     * EventHandler used to handle click events on the delete button
     */
    public void onDeleteButtonClicked() {
        FileRowData file = fileTable.getSelectionModel().getSelectedItem();

        /*
        Check if user selected a file
         */
        if (file == null) {
            showAlert();
        } else {
            browsingClient.deleteRequest(file.getPath());
        }
    }

    /**
     * EventHandler used to handle click events on the download button
     */
    public void onDownloadButtonClicked() {
        FileRowData file = fileTable.getSelectionModel().getSelectedItem();

        /*
        Check if there is a selected file
         */
        if (file == null) {
            this.showAlert();
        } else {
            DirectoryChooser chooser = new DirectoryChooser();
            chooser.setTitle("Place to Save");

            chooser.setInitialDirectory(new File("."));

            File directoryChooser = chooser.showDialog(null);

            /*
            Check if user selected a location
             */
            if (directoryChooser != null)
                new DownloadClient(serverIP).start(file.getPath(),
                        directoryChooser.getAbsolutePath() + Constants.BACKWARD_DASH);
        }
    }

    /**
     * Method used to display a warning message to the user
     */
    private void showAlert() {
        AlertHandler.getInstance().start("Invalid action",
                "Please select a File", Alert.AlertType.WARNING);
    }

    /**
     * EventHandler used to handle click events on the upload file button
     */
    public void onUploadFileButtonClicked() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("File to Upload");
        chooser.setInitialDirectory(new File("."));

        File FileChooser = chooser.showOpenDialog(null);

         /*
         Check if user selected a file
          */
        if (FileChooser != null)
            new UploadClient(serverIP).start(FileChooser, pathLabel.getText());
    }

    /**
     * EventHandler used to handle click events on the upload folder button
     */
    public void onUploadFolderButtonClicked() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Folder to Upload");
        chooser.setInitialDirectory(new File("."));

        File FileChooser = chooser.showDialog(null);

         /*
         Check if user selected a folder
          */
        if (FileChooser != null)
            new UploadClient(serverIP).start(FileChooser, pathLabel.getText());
    }

    /**
     * Set method for ServerIP
     *
     * @param IP The ip of the server
     */
    public void setIP(String IP) {
        serverIP = IP;
        browsingClient = new BrowsingClient(serverIP);

        FileRowData[] result = new BrowsingClient(serverIP).browserRequest("");

         /*
            Check if browse was successful
             */
        if (result != null) {
            this.setObservableList(result);
        }
    }

    /**
     * Set method for observableList
     *
     * @param fileRowData Is a a list of FileRowData objects
     */
    public void setObservableList(FileRowData... fileRowData) {
        this.observableList.clear();
        this.observableList.addAll(fileRowData);
        this.fileTable.setItems(this.observableList);
    }

    /**
     * Get method for stage
     *
     * @return A stage containing the GUI of the fxml file
     */
    public Stage getStage() {
        return this.stage;
    }

    /**
     * EventHandler used to handle click events on the refresh button
     */
    public void onRefreshButtonClicked() {

        FileRowData[] result = browsingClient.browserRequest(this.pathLabel.getText());

        /*
        Check if browse was successful
        */
        if (result != null) {
            this.setObservableList(result);
        }

    }

    /**
     * Method used to update the client Observable
     * list from an outside thread
     */
    public void updateObservableList() {
        Platform.runLater(() -> {

            FileRowData[] result = browsingClient
                    .browserRequest(((Label) stage.getScene().lookup("#pathLabel")).getText());

            /*
            Check if browse was successful
            */
            if (result != null) {
                setObservableList(result);
            }
        });
    }

    /**
     * Get method for serverIP
     *
     * @return The server IP the client is connected to
     */
    public String getServerIP() {
        return serverIP;
    }
}
