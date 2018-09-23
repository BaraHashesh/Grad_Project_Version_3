package client.models.connection;

import client.models.controllers.AlertHandler;
import client.models.controllers.EstimationUpdater;
import javafx.scene.control.Alert;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import shared.Constants;
import shared.FileTransfer;
import shared.JsonParser;
import shared.Methods;
import shared.models.Message;

import java.io.File;
import java.net.URI;
import java.util.concurrent.TimeUnit;

/**
 * Class responsible for creating the web socket responsible for handling upload operations
 */
class UploadWebSocket extends WebSocketClient {

    private File fileToUpload;

    /**
     * Constructor for the {@link UploadWebSocket}
     *
     * @param serverUri Is the server URI
     */
    UploadWebSocket(URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {

    }

    @Override
    public void onMessage(String s) {
        Message responseMessage = JsonParser.getInstance().fromJson(s, Message.class);

        /*
        Check if message is a success message
         */
        if (responseMessage.isSuccessMessage()) {
            FileTransfer fileTransfer = new FileTransfer();

            EstimationUpdater estimationUpdater = new EstimationUpdater(
                    fileTransfer, Methods.getInstance().calculateSize(this.fileToUpload), this
            );

            estimationUpdater.start();

            fileTransfer.send(this, this.fileToUpload);

            estimationUpdater.finalUpdate();

            responseMessage.createStreamEndMessage("");

            send(JsonParser.getInstance().toJson(responseMessage));
        }
        /*
        Check if error message
         */
        else if (responseMessage.isErrorMessage()) {
            AlertHandler.getInstance().start("Upload Error", "Unable to upload folder/file", Alert.AlertType.ERROR);
        }
    }

    @Override
    public void onClose(int i, String s, boolean b) {

    }

    @Override
    public void onError(Exception e) {

    }

    /**
     * set method for fileToUpload
     *
     * @param fileToUpload Is the file/folder to upload
     */
    void setFileToUpload(File fileToUpload) {
        this.fileToUpload = fileToUpload;
    }
}

/**
 * Class responsible for handling upload operations
 */
public class UploadClient {
    private UploadWebSocket uploadWebSocket;

    /**
     * Constructor for the {@link UploadClient}
     *
     * @param serverIP Is the server IP
     */
    public UploadClient(String serverIP) {
        try {
            this.uploadWebSocket = new UploadWebSocket(
                    new URI("wss://" + serverIP + ":" + Constants.TCP_PORT)
            );

            this.uploadWebSocket.setSocket(Methods.getInstance().buildFactory().createSocket());
            this.uploadWebSocket.connectBlocking(2000, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            AlertHandler.getInstance().start("Upload Error", "Unable to connect to server", Alert.AlertType.ERROR);
        }

    }

    /**
     * Method used to start upload operation
     *
     * @param fileToUpload   Is the file/folder to upload
     * @param locationToSave Is where to save the file/folder on the storage device
     */
    public void upload(File fileToUpload, String locationToSave) {
        this.uploadWebSocket.setFileToUpload(fileToUpload);

        Message requestMessage = new Message();
        requestMessage.createUploadMessage(locationToSave);

        this.uploadWebSocket.send(JsonParser.getInstance().toJson(requestMessage));
    }
}
