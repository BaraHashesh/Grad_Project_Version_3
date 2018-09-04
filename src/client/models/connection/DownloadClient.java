package client.models.connection;

import client.models.controllers.AlertHandler;
import client.models.controllers.EstimationUpdater;
import javafx.scene.control.Alert;
import shared.ConnectionBuilder;
import shared.FileTransfer;
import shared.JsonParser;
import shared.models.Message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

/**
 * DownloadClient class is used to download files from server on a separate thread
 */
public class DownloadClient implements Runnable {
    private String path;
    private String locationToSave;
    private String IP;
    private Thread thread;

    /**
     * Constructor for the DownloadClient object for a specific storage device
     *
     * @param hostIP Is the IP of the storage device
     */
    public DownloadClient(String hostIP) {
        this.IP = hostIP;
    }

    /**
     * Initialize method for the variables
     * also start thread operations
     *
     * @param path           Is the path of the file to be downloaded
     * @param locationToSave is the location to save file under within the user device
     */
    public void start(String path, String locationToSave) {
        this.path = path;
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
     * Download operations are done here
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
            request.createDownloadMessage(path);

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

                FileTransfer fileTransfer = new FileTransfer();

                long size = Long.parseLong(response.getMessageInfo());

                EstimationUpdater updater = new EstimationUpdater(fileTransfer,
                        size, clientSocket);

                updater.start();

                fileTransfer.receiveFiles(dataInputStream, locationToSave);

                updater.finalUpdate();
            }

            dataInputStream.close();
            dataOutputStream.close();
            clientSocket.close();

        } catch (Exception e) {
            e.printStackTrace();

            AlertHandler.getInstance().start("Server Error",
                    "Connection to server was lost", Alert.AlertType.ERROR);
        }
    }
}
