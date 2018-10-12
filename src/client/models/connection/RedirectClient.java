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
 * Client responsible for the copy phase of the operation
 */
class RedirectClientReceive extends WebSocketClient {

    private RedirectClientSend redirectClientSend;
    private EstimationUpdater estimationUpdater;
    private FileTransfer fileTransfer;
    private boolean status;
    private long current = 0;

    RedirectClientReceive(URI serverUri) {
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

            AlertHandler.getInstance().start("Copy Error",
                    "Unable to find file/folder", Alert.AlertType.WARNING);
        }
        /*
        check if file info message
         */
        else if (responseMessage.isFileInfoMessage()) {
            this.redirectClientSend.send(s);
        }
        /*
        Check if download is done
         */
        else if (responseMessage.isStreamEndMessage()) {
            this.status = true;
            this.estimationUpdater.finalUpdate();
            this.redirectClientSend.send(s);
            close();
        }
    }

    @Override
    public void onMessage(ByteBuffer bytes) {
        this.redirectClientSend.send(bytes);
        this.current += bytes.array().length;
        this.fileTransfer.setFileSizeStatus(this.current);
    }

    @Override
    public void onClose(int i, String s, boolean b) {
        /*
        Check if copy/paste was completed
         */
        if (!this.status) {
            this.fileTransfer.deleteFile();
            this.estimationUpdater.finalUpdate();
        }
    }

    @Override
    public void onError(Exception e) {

    }

    void setRedirectClientSend(RedirectClientSend redirectClientSend) {
        this.redirectClientSend = redirectClientSend;
    }
}


/**
 * Client responsible for the paste phase of the operation
 */
class RedirectClientSend extends WebSocketClient {

    private RedirectClientReceive redirectClientReceive;
    private String fileToRedirect;

    RedirectClientSend(URI serverUri) {
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
            Message requestMessage = new Message();
            requestMessage.createDownloadMessage(this.fileToRedirect);

            this.redirectClientReceive.send(JsonParser.getInstance().toJson(requestMessage));
        }
        /*
        Check if error message
         */
        else if (responseMessage.isErrorMessage()) {
            AlertHandler.getInstance().start("Paste Error",
                    "Unable to paste folder/file", Alert.AlertType.ERROR);

            this.redirectClientReceive.close();
            close();
        }
    }

    @Override
    public void onClose(int i, String s, boolean b) {

    }

    @Override
    public void onError(Exception e) {

    }

    void setRedirectClientReceive(RedirectClientReceive redirectClientReceive) {
        this.redirectClientReceive = redirectClientReceive;
    }

    void setFileToRedirect(String fileToRedirect) {
        this.fileToRedirect = fileToRedirect;
    }
}

/**
 * Class responsible for handling copy/paste operations
 */
public class RedirectClient {

    private RedirectClientSend redirectClientSend;
    private RedirectClientReceive redirectClientReceive;

    /**
     * Constructor for {@link RedirectClient}
     *
     * @param sourceServerIP The IP of the source server
     * @param targetServerIP The IP of the target server
     */
    public RedirectClient(String sourceServerIP, String targetServerIP) {
        try {
            this.redirectClientSend = new RedirectClientSend(
                    new URI("wss://" + targetServerIP + ":" + Constants.TCP_PORT)
            );

            this.redirectClientSend.setSocket(Methods.getInstance().buildFactory().createSocket());



            this.redirectClientSend.connectBlocking(2000, TimeUnit.MILLISECONDS);

        } catch (Exception e) {
            AlertHandler.getInstance().start("Paste Failure",
                    "Unable to connect to server", Alert.AlertType.ERROR);
        }

        try {
            this.redirectClientReceive = new RedirectClientReceive(
                    new URI("wss://" + sourceServerIP + ":" + Constants.TCP_PORT)
            );

            this.redirectClientReceive.setSocket(Methods.getInstance().buildFactory().createSocket());

            this.redirectClientReceive.connectBlocking(2000, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            AlertHandler.getInstance().start("Copy Failure",
                    "Unable to connect to server", Alert.AlertType.ERROR);

            assert this.redirectClientSend != null;
            this.redirectClientSend.close();
        }
    }

    /**
     * Method used to copy/paste the file from one device to another
     * @param sourceFile The file/folder to copy
     * @param destinationFolder The location to paste it to
     */
    public void copyPaste(String sourceFile, String destinationFolder){
        /*
        Check if both client are open
         */
        if(this.redirectClientReceive.isOpen() && this.redirectClientSend.isOpen()){
            this.redirectClientSend.setRedirectClientReceive(this.redirectClientReceive);
            this.redirectClientReceive.setRedirectClientSend(this.redirectClientSend);
            this.redirectClientSend.setFileToRedirect(sourceFile);

            Message requestMessage = new Message();
            requestMessage.createUploadMessage(destinationFolder);

            this.redirectClientSend.send(JsonParser.getInstance().toJson(requestMessage));
        }
    }
}
