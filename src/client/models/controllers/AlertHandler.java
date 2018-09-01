package client.models.controllers;

import javafx.application.Platform;
import javafx.scene.control.Alert;


public class AlertHandler implements Runnable {

    private static AlertHandler instance = new AlertHandler();
    private String title, msg;
    private Alert.AlertType alertType;

    private AlertHandler() {
    }

    /**
     * Get method for instance
     *
     * @return An instance of AlertHandler object
     */
    public static AlertHandler getInstance() {
        return instance;
    }

    /**
     * Method used to start the AlertHandler thread
     *
     * @param msg Is the message to be shown to the user
     */
    public void start(String title, String msg, Alert.AlertType alertType) {
        this.title = title;
        this.msg = msg;
        this.alertType = alertType;
        Platform.runLater(this);
    }

    @Override
    public void run() {
        Alert alert = new Alert(this.alertType);

        alert.setTitle(this.title);

        alert.setHeaderText(null);

        alert.setContentText(this.msg);

        alert.showAndWait();
    }
}
