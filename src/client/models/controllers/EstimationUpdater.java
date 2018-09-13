package client.models.controllers;

import client.controllers.EstimationController;
import shared.Constants;
import shared.FileTransfer;

import java.net.Socket;

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
                             Socket clientSocket) {

        this.totalFileSize = totalFileSize;
        this.fileTransfer = fileTransfer;

        estimationViewController = new EstimationController();

        estimationViewController.initializeVariables(totalFileSize,
                clientSocket);

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
        while (this.totalFileSize > this.fileTransfer.getTransferredFileSize()) {
            try {
                Thread.sleep((long) (Constants.UPDATE_RATE * 1000));
                this.estimationViewController.update(this.fileTransfer.getTransferredFileSize());
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
