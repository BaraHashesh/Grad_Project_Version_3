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
 * Controller for the Estimation.fxml file
 */
public class EstimationController implements Runnable {

    @FXML
    public Label labelProgress, labelRemaining,
            infoSize, infoProgress, infoRemaining, infoSpeed, infoTime;

    private long totalFileSize, progress, previousValue;
    private Boolean initialized = false;
    private Stage stage;
    private Scene scene;

    private Socket clientSocket;

    public static EstimationController getInstance(long totalFileSize, Socket clientSocket) {

        EstimationController controller = null;

        try {
            FXMLLoader loader = new FXMLLoader(EstimationController.class
                    .getResource("/client/resources/fxml/EstimationView.fxml"));

            AnchorPane parent = loader.load();

            // Get the controller of the loaded scene
            controller = loader.getController();

            controller.totalFileSize =totalFileSize;
            controller.clientSocket = clientSocket;

            controller.scene = new Scene(parent, parent.getPrefWidth(), 178);

            EstimationController finalController = controller;

            Platform.runLater(() -> {
                finalController.stage = new Stage();
                finalController.stage.setScene(finalController.scene);

                finalController.stage.setTitle("File Transfer Estimator");

                finalController.stage.setResizable(false);


                finalController.stage.setOnCloseRequest(e -> {
                    try {
                        finalController.clientSocket.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                });
            });

        } catch (Exception e){
            e.printStackTrace();
        }

        return controller;
    }

    /**
     * Start method for the thread
     */
    public void update(long progress) {
        this.progress = progress;
        Platform.runLater(this);
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
                this.stage.hide();
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
                Check if there was progress
                 */
                if (speed != 0) {
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
                } else {
                    this.infoTime.setText("Computing!!!");
                    this.infoSpeed.setText("Computing!!!");
                }
            }
        }
    }

    /**
     * Initialize method for the stage
     */
    private void initializeStage() {
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
