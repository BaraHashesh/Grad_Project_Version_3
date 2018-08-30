package client.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import shared.Constants;
import shared.Methods;

import java.io.IOException;
import java.net.Socket;

/**
 * Controller for the EstimationView.fxml file
 */
public class EstimationViewController implements Runnable {

    @FXML
    public Label labelProgress, labelRemaining,
            infoSize, infoProgress, infoRemaining, infoSpeed, infoTime;
    private long totalFileSize, progress, previousValue;
    private Socket stringSocket, byteSocket;
    private Boolean initialized = false;
    private Stage stage;
    private Scene scene;

    /**
     * Initializer for the EstimationViewController
     *
     * @param totalFileSize Is the size of the current file/folder in bytes
     * @param stringSocket  Is the stream socket responsible for the JSON strings for the current file/folder
     * @param byteSocket    Is the stream socket responsible for the pure bytes for the current file/folder
     */
    public void initializeVariables(long totalFileSize,
                                    Socket stringSocket, Socket byteSocket) {

        this.totalFileSize = totalFileSize;
        this.stringSocket = stringSocket;
        this.byteSocket = byteSocket;
    }

    /**
     * Start method for the thread
     */
    public void update(long progress) {
        this.progress = progress;
        Platform.runLater(this);
    }

    /**
     * Set method for the EstimationView GUI
     */
    public void setStage() {
        try {
            AnchorPane parent = FXMLLoader.load(getClass()
                    .getResource("../resources/fxml/EstimationView.fxml"));

            this.scene = new Scene(parent, parent.getPrefWidth(), 178);

            this.labelProgress = (Label) this.scene.lookup("#labelProgress");
            this.labelRemaining = (Label) this.scene.lookup("#labelRemaining");

            this.infoSize = (Label) this.scene.lookup("#infoSize");
            this.infoProgress = (Label) this.scene.lookup("#infoProgress");
            this.infoRemaining = (Label) this.scene.lookup("#infoRemaining");
            this.infoSpeed = (Label) this.scene.lookup("#infoSpeed");
            this.infoTime = (Label) this.scene.lookup("#infoTime");


            this.stage = new Stage();
            this.stage.setScene(this.scene);

            this.stage.setOnCloseRequest(action -> {
                try {
                    this.stringSocket.close();
                    this.byteSocket.close();
                } catch (IOException ignored) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Run method to update the stage in a FXML thread
     */
    @Override
    public void run() {
        /*
        Check if stage is initialized
         */
        if (!this.initialized) {
            this.initializeStage();
            this.initialized = true;
        } else {

            /*
            Check if operations are done
             */
            if (this.progress == this.totalFileSize) {
                this.stage.close();
            } else {
                // speed in bytes per second
                double speed = (this.progress - this.previousValue) / Constants.UPDATE_RATE;

                this.previousValue = this.progress; // update previousValue

                long remaining = this.totalFileSize - this.progress; //remaining data in bytes

                /*
                update the infoProgress label
                 */
                Object[] sizeInfo = Methods.getInstance().reduceSize((double) this.progress);

                this.infoProgress.setText(Methods.getInstance()
                        .round((double) sizeInfo[0], 2) + " " + sizeInfo[1]);

                /*
                update the infoRemaining label
                 */
                sizeInfo = Methods.getInstance().reduceSize((double) remaining);

                this.infoRemaining.setText(Methods.getInstance()
                        .round((double) sizeInfo[0], 2) + " " + sizeInfo[1]);

                /*
                update the infoTime label
                 */
                double remainingTime = (double) remaining / speed; //Time in seconds

                this.infoTime.setText(Methods.getInstance().reduceTime((long) (remainingTime * 1000)));

                /*
                update the infoSpeed label
                 */
                sizeInfo = Methods.getInstance().reduceSize(speed);

                this.infoSpeed.setText(Methods.getInstance().round((double) sizeInfo[0], 2)
                        + " " + sizeInfo[1] + "/s");

                /*
                update the progress par
                 */
                double width = scene.getWidth();

                double percentage = (double) remaining / this.totalFileSize;

                this.labelProgress.setPrefWidth(width - percentage * width);

                this.labelRemaining.setPrefWidth(percentage * width);
            }
        }
    }

    /**
     * Initialize method for the stage
     */
    private void initializeStage() {

        this.setStage();

        this.previousValue = 0;

        Object[] sizeInfo = Methods.getInstance().reduceSize((double) this.totalFileSize);

        this.infoSize.setText(Methods.getInstance()
                .round((double) sizeInfo[0], 2) + " " + sizeInfo[1]);

        this.infoProgress.setText("0 B");

        this.infoRemaining.setText(Methods.getInstance()
                .round((double) sizeInfo[0], 2) + " " + sizeInfo[1]);

        this.infoSpeed.setText("Calculating!!!");
        this.infoTime.setText("Calculating!!!");

        this.labelProgress.setPrefWidth(0);
        this.labelRemaining.setPrefWidth(this.scene.getWidth());

        this.stage.show();
    }
}
