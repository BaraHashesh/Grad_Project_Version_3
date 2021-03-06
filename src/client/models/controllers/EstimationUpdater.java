package client.models.controllers;

import client.controllers.EstimationController;
import org.java_websocket.client.WebSocketClient;
import shared.Constants;
import shared.FileTransfer;

/**
 * EstimationUpdater class is used to update the EstimationView FXML file
 * by using the estimationViewController class
 */
public class EstimationUpdater implements Runnable {

    private EstimationController estimationViewController;
    private long totalFileSize;
    private FileTransfer fileTransfer;
    private Thread thread;

    /**
     * Constructor for the EstimationUpdater object
     *
     * @param fileTransfer  Is the FileTransfer object responsible for the current file/folder
     * @param totalFileSize Is the size of the current file/folder in bytes
     * @param clientSocket  Is the stream socket
     */
    public EstimationUpdater(FileTransfer fileTransfer, long totalFileSize,
                             WebSocketClient clientSocket) {

        this.totalFileSize = totalFileSize;
        this.fileTransfer = fileTransfer;

        estimationViewController = EstimationController.getInstance(totalFileSize, clientSocket);

        estimationViewController.update(0);

    }

    /**
     * Start method for the EstimationUpdater Thread
     */
    public void start() {
        /*
        Check if thread hasn't start yet
         */
        if (thread == null) {
            thread = new Thread(this);
            thread.start();
        }
    }

    /**
     * Run method for the EstimationUpdater class
     * used to update the EstimationView FXML
     */
    @Override
    public void run() {
        /*
        While loop to continue updating until entire file/folder is transformed
         */
        while (this.totalFileSize > this.fileTransfer.getFileSizeStatus()) {
            try {
                Thread.sleep((long) (Constants.getInstance().UPDATE_RATE * 1000));
                this.estimationViewController.update(this.fileTransfer.getFileSizeStatus());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Method used to send last update to the EstimationController class
     */
    public void finalUpdate() {
        this.estimationViewController.update(this.totalFileSize);
    }
}
