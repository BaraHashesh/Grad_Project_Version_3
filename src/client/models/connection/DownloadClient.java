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

import java.net.URI;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;

/**
 * Class responsible for creating the web socket responsible for handling download operations
 */
class DownloadWebSocket extends WebSocketClient {

    private FileTransfer fileTransfer;
    private EstimationUpdater estimationUpdater;
    private String fileLocation;
    private boolean status;

    /**
     * Constructor for the {@link DownloadWebSocket}
     *
     * @param serverUri The URI of the server
     */
    DownloadWebSocket(URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {

    }

    @Override
    public void onMessage(String s) {
        Message responseMessage = JsonParser.getInstance().fromJson(s, Message.class);

        /*
        check if message is a success message
         */
        if (responseMessage.isSuccessMessage()) {
            this.fileTransfer = new FileTransfer();

            this.estimationUpdater = new EstimationUpdater(
                    fileTransfer, Long.parseLong(responseMessage.getMessageInfo()), this
            );

            estimationUpdater.start();

            this.status = false;
        }
        /*
        check if error message
         */
        else if (responseMessage.isErrorMessage()) {
            close();

            AlertHandler.getInstance().start("Download Error", "Unable to find file/folder", Alert.AlertType.WARNING);
        }
        /*
        check if file info message
         */
        else if (responseMessage.isFileInfoMessage()) {
            this.fileTransfer.receive(responseMessage.getMessageInfo(), this.fileLocation);
        }
        /*
        Check if download is done
         */
        else if (responseMessage.isStreamEndMessage()) {
            this.status = true;
            this.estimationUpdater.finalUpdate();
            close();
        }
    }

    @Override
    public void onMessage(ByteBuffer bytes) {
        this.fileTransfer.receive(bytes.array());
    }

    @Override
    public void onClose(int i, String s, boolean b) {
        /*
        Check if download was completed
         */
        if (!this.status)
            this.fileTransfer.deleteFile();
    }

    @Override
    public void onError(Exception e) {

    }

    /**
     * Set method for fileLocation
     *
     * @param fileLocation The location which is used to save files/folders under
     */
    public void setFileLocation(String fileLocation) {
        this.fileLocation = fileLocation;
    }
}

/**
 * Class responsible for handling download operations
 */
public class DownloadClient {
    private DownloadWebSocket downloadWebSocket;

    /**
     * Constructor for {@link DownloadClient}
     *
     * @param serverIP The IP of the server
     */
    public DownloadClient(String serverIP) {
        try {
            this.downloadWebSocket = new DownloadWebSocket(
                    new URI("wss://" + serverIP + ":" + Constants.TCP_PORT)
            );

            downloadWebSocket.setSocket(Methods.getInstance().buildFactory().createSocket());

            this.downloadWebSocket.connectBlocking(2000, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            AlertHandler.getInstance().start("Download Failure", "Unable to connect to server", Alert.AlertType.ERROR);
        }

    }

    /**
     * Method used to download the file
     *
     * @param locationToSaveUnder Is the location to save the file under
     * @param fileToDownload      Is the path of the file/folder to download
     */
    public void download(String locationToSaveUnder, String fileToDownload) {
        Message requestMessage = new Message();
        requestMessage.createDownloadMessage(fileToDownload);

        this.downloadWebSocket.setFileLocation(locationToSaveUnder);

        this.downloadWebSocket.send(JsonParser.getInstance().toJson(requestMessage));
    }
}
