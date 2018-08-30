package client.models;

import client.controllers.EstimationViewController;
import shared.Constants;
import shared.FileTransfer;

import java.net.Socket;

/**
 * EstimationViewUpdater class is used to update the EstimationView FXML file
 * by using the estimationViewController class
 */
public class EstimationViewUpdater implements Runnable {

    private EstimationViewController estimationViewController;
    private long totalFileSize;
    private FileTransfer fileTransfer;
    private Thread thread;

    /**
     * Constructor for the EstimationViewUpdater object
     *
     * @param fileTransfer  Is the FileTransfer object responsible for the current file/folder
     * @param totalFileSize Is the size of the current file/folder in bytes
     * @param stringSocket  Is the stream socket responsible for the JSON strings for the current file/folder
     * @param byteSocket    Is the stream socket responsible for the pure bytes for the current file/folder
     */
    public EstimationViewUpdater(FileTransfer fileTransfer, long totalFileSize,
                                 Socket stringSocket, Socket byteSocket) {

        this.totalFileSize = totalFileSize;
        this.fileTransfer = fileTransfer;

        estimationViewController = new EstimationViewController();

        estimationViewController.initializeVariables(totalFileSize,
                stringSocket, byteSocket);

        estimationViewController.update(0);

    }

    /**
     * Start method for the EstimationViewUpdater Thread
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
     * Run method for the EstimationViewUpdater class
     * used to update the EstimationView FXML
     */
    @Override
    public void run() {
        long time = 1;
        /*
        While loop to continue updating until entire file/folder is transformed
         */
        while (this.totalFileSize > this.fileTransfer.getTransferredFileSize()) {
            try {
                Thread.sleep((long) (Constants.UPDATE_RATE * 1000));
                this.estimationViewController.update(this.fileTransfer.getTransferredFileSize());
                time++;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Method used to send last update to the EstimationViewController class
     */
    public void finalUpdate() {
        this.estimationViewController.update(this.totalFileSize);
    }
}
