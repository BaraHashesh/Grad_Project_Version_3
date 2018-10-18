package client.controllers;

import client.models.connection.BrowsingClient;
import client.models.connection.DownloadClient;
import client.models.connection.RedirectClient;
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

    private static String sourceIP;
    private static String sourcePath;

    private static ArrayList<BrowserController> instances = new ArrayList<>();
    @FXML
    public Label pathLabel;
    @FXML
    public Button backButton, deleteButton, downloadButton,
            uploadFileButton, uploadFolderButton, refreshButton,
            copyButton, pasteButton;
    @FXML
    public TableView<FileRowData> fileTable;
    private ObservableList<FileRowData> observableList = FXCollections.observableArrayList();
    private BrowsingClient browsingClient;
    private String serverIP;
    private ArrayList<String> pathList = new ArrayList<>(10);
    private Stage stage;

    /**
     * Get method to create an new instance of the browser
     *
     * @param IP is the ip for the storage device which the controller will be responsible for
     * @return An instance of the BrowserController object
     */
    public static BrowserController getInstance(String IP) {

        try {
            FXMLLoader loader = new FXMLLoader(BrowserController.class
                    .getResource("/client/resources/fxml/BrowserView.fxml"));

            AnchorPane parent = loader.load();

            // Get the controller of the loaded scene
            BrowserController instance = loader.getController();

            /*
            initialize the browser information
             */
            instance.stage = new Stage();
            instance.serverIP = IP;
            instance.browsingClient = new BrowsingClient(instance);

            /*
            create the stage for the browser
             */
            instance.stage.setScene(new Scene(parent));
            instance.stage.setTitle("File Browser");
            instance.stage.setResizable(false);

            getInstances().add(instance); // add the new browser instance to the list of instances

            /*
            create a Lambda method to handle the operation of closing the stage
             */
            instance.stage.setOnCloseRequest(e -> instance.close());

            instance.onRefreshButtonClicked();

            return instance;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get method for instances
     *
     * @return All available instances of the BrowserController
     */
    public static ArrayList<BrowserController> getInstances() {
        return instances;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        /*
        create column for the browsers table
         */
        TableColumn<FileRowData, Image> image = new TableColumn<>("");
        TableColumn<FileRowData, String> name = new TableColumn<>("name");
        TableColumn<FileRowData, String> type = new TableColumn<>("type");
        TableColumn<FileRowData, String> date = new TableColumn<>("Last Modified");
        TableColumn<FileRowData, String> size = new TableColumn<>("size");

        /*
        link the columns to their perspective properties
         */
        image.setCellValueFactory(new PropertyValueFactory<>("icon"));
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        type.setCellValueFactory(new PropertyValueFactory<>("type"));
        date.setCellValueFactory(new PropertyValueFactory<>("modifiedDate"));
        size.setCellValueFactory(new PropertyValueFactory<>("sizeInfo"));

        this.fileTable.getColumns().clear(); // remove any unwanted columns

        /*
        add generated columns to the table
         */
        this.fileTable.getColumns().add(image);
        this.fileTable.getColumns().add(name);
        this.fileTable.getColumns().add(type);
        this.fileTable.getColumns().add(date);
        this.fileTable.getColumns().add(size);

        /*
        create a Lambda method to handle click actions (double click) on the rows of the table
         */
        this.fileTable.setRowFactory(e -> {
            TableRow<FileRowData> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    onRowDoubleClick();
                }
            });
            return row;
        });
    }

    /**
     * EventHandler used to handle double click event on the rows of the table
     */
    private void onRowDoubleClick() {
        FileRowData fileRowData = this.fileTable.getSelectionModel().getSelectedItem();
        /*
        Check if a file was selected and if it is a directory
         */
        if (fileRowData.isDirectory()) {
            this.browsingClient.browse(fileRowData.getPath());

            this.pathList.add(fileRowData.getParent());

            this.pathLabel.setText(fileRowData.getPath());
        }
    }

    /**
     * EventHandler used to handle click events on the back button
     */
    public void onBackButtonClicked() {
        /*
        Check if the pathList is empty
         */
        if (this.pathList.size() != 0) {
            // get previous path and remove if from the path list
            String path = this.pathList.remove(this.pathList.size() - 1);

            this.browsingClient.browse(path);

            this.pathLabel.setText(path);
        }
    }

    /**
     * EventHandler used to handle click events on the delete button
     */
    public void onDeleteButtonClicked() {
        FileRowData file = this.fileTable.getSelectionModel().getSelectedItem();

        /*
        Check if user selected a file
         */
        if (file == null) {
            showAlert();
        } else {
            this.browsingClient.delete(file.getPath());
        }
    }

    /**
     * EventHandler used to handle click events on the download button
     */
    public void onDownloadButtonClicked() {
        FileRowData file = this.fileTable.getSelectionModel().getSelectedItem();

        /*
        Check if there is a selected file
         */
        if (file == null) {
            this.showAlert();
        } else {
            DirectoryChooser chooser = new DirectoryChooser();
            chooser.setTitle("Place to Save");

            chooser.setInitialDirectory(new File("."));

            File directoryChosen = chooser.showDialog(null);

            /*
            Check if user selected a location
             */
            if (directoryChosen != null)
                new DownloadClient(serverIP).download(
                        directoryChosen.getAbsolutePath() +
                                Constants.getInstance().BACKWARD_DASH, file.getPath()
                );
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

        File fileChosen = chooser.showOpenDialog(null);

         /*
         Check if user selected a file
          */
        if (fileChosen != null)
            new UploadClient(serverIP).upload(fileChosen, this.pathLabel.getText());
    }

    /**
     * EventHandler used to handle click events on the upload folder button
     */
    public void onUploadFolderButtonClicked() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Folder to Upload");
        chooser.setInitialDirectory(new File("."));

        File folderChosen = chooser.showDialog(null);

         /*
         Check if user selected a folder
          */
        if (folderChosen != null)
            new UploadClient(serverIP).upload(folderChosen, this.pathLabel.getText());
    }

    /**
     * Set method for observableList
     *
     * @param fileRowData Is a a list of FileRowData objects
     */
    private void setObservableList(FileRowData... fileRowData) {
        this.observableList.clear();
        this.observableList.addAll(fileRowData);
        this.fileTable.setItems(this.observableList);
    }

    /**
     * Get method for stage
     *
     * @return A stage containing the GUI of the fxml file
     */
    Stage getStage() {
        return this.stage;
    }

    /**
     * EventHandler used to handle click events on the refresh button
     */
    public void onRefreshButtonClicked() {
        this.browsingClient.browse(this.pathLabel.getText());
    }

    /**
     * Method used to update the client Observable if necessary
     *
     * @param pathToUpdate Is the path that had changes be done upon
     */
    public void updateObservableList(String pathToUpdate) {
        Platform.runLater(() -> {
            /*
            Check if the folder which was update is of importance to the current folder
             */
            if (this.pathLabel.getText().contains(pathToUpdate)) {
                this.browsingClient.browse(this.pathLabel.getText());
            }
        });
    }

    /**
     * Method used to update the client Observable
     * with new entries from an outside thread
     */
    public void updateObservableList(FileRowData[] files, boolean isSuccessful) {
        Platform.runLater(() -> {
            /*
            check if operation was successful
             */
            if (isSuccessful) {
                setObservableList(files);
            } else {
                this.pathList.remove(this.pathList.size() - 1);
                this.pathLabel.setText(this.pathList.get(this.pathList.size() - 1));
            }
        });
    }

    /**
     * Get method for serverIP
     *
     * @return The server IP the client is connected to
     */
    public String getServerIP() {
        return this.serverIP;
    }

    /**
     * method used to close the current browser
     */
    public void close() {

        Platform.runLater(() -> {
            this.stage.close();

            getInstances().remove(this);

            /*
            check if one stage is open or not
            */
            if (getInstances().size() == 0
                    && !ChooseServerController.getInstance().isOpen())
                System.exit(1);
        });
    }

    /**
     * EventHandler used to handle click events on the copy button
     */
    public void onCopyButtonClicked() {
        FileRowData file = this.fileTable.getSelectionModel().getSelectedItem();

        /*
        Check if there is a selected file
         */
        if (file == null) {
            this.showAlert();
        } else {
            sourceIP = this.getServerIP();
            sourcePath = file.getPath();
        }
    }

    /**
     * EventHandler used to handle click events on the paste button
     */
    public void onPasteButtonClicked() {

        if(sourceIP != null)
            new RedirectClient(sourceIP, this.getServerIP()).copyPaste(sourcePath, this.pathLabel.getText());
        else
            AlertHandler.getInstance().start("Paste/Error",
                    "Choose a file to copy first", Alert.AlertType.INFORMATION);
    }
}
