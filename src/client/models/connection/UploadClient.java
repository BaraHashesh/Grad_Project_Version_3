package client.models.connection;


import client.models.controllers.AlertHandler;
import client.models.controllers.EstimationUpdater;
import javafx.scene.control.Alert;
import shared.ConnectionBuilder;
import shared.FileTransfer;
import shared.JsonParser;
import shared.Methods;
import shared.models.Message;

import java.io.*;
import java.net.Socket;

/**
 * UploadClient class is used to upload files to storage device on a separate thread
 */
public class UploadClient implements Runnable {
    private File file;
    private String locationToSave;
    private String IP;
    private Thread thread;

    /**
     * Constructor for the UploadClient object for a specific storage device
     *
     * @param hostIP Is the IP of the storage device
     */
    public UploadClient(String hostIP) {
        this.IP = hostIP;
    }

    /**
     * Initialize method for the variables
     * also start thread operations
     *
     * @param file           Is file to be uploaded
     * @param locationToSave Is the location to save file under within the storage device
     */
    public void start(File file, String locationToSave) {
        this.file = file;
        this.locationToSave = locationToSave;

        /*
        Check if the thread is not running
         */
        if (thread == null) {
            thread = new Thread(this);
            thread.start();
        }
    }

    /**
     * Upload operations are done here
     */
    @Override
    public void run() {
        Message request, response;
        try {
            Socket clientSocket = ConnectionBuilder.getInstance().buildClientSocket(this.IP);

            DataOutputStream dataOutputStream = ConnectionBuilder.getInstance()
                    .buildOutputStream(clientSocket);

            DataInputStream dataInputStream = ConnectionBuilder.getInstance()
                    .buildInputStream(clientSocket);

            request = new Message();
            request.createUploadMessage(locationToSave);

            dataOutputStream.writeUTF(JsonParser.getInstance().toJson(request));
            dataOutputStream.flush();

            response = JsonParser.getInstance().fromJson(dataInputStream.readUTF(), Message.class);

            /*
            Check if operation was possible
             */
            if (response.isErrorMessage()) {

                AlertHandler.getInstance().start("Server Error",
                        response.getMessageInfo(), Alert.AlertType.ERROR);

            } else {
                String parent = this.file.getParent();

                FileTransfer fileTransfer = new FileTransfer();

                EstimationUpdater updater = new EstimationUpdater(fileTransfer,
                        Methods.getInstance().calculateSize(this.file), clientSocket);

                updater.start();

                fileTransfer.sendFiles(dataOutputStream, this.file, parent);

                Message streamEndMessage = new Message();
                streamEndMessage.createStreamEndMessage("");

                dataOutputStream.writeUTF(JsonParser.getInstance().toJson(streamEndMessage));
                dataOutputStream.flush();

                updater.finalUpdate();
            }

            dataOutputStream.close();
            dataInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();

            AlertHandler.getInstance().start("Server Error",
                    "Connection to server was lost", Alert.AlertType.ERROR);
        }
    }
}
