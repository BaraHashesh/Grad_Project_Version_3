package client.models.connection;

import client.controllers.BrowserController;
import client.models.controllers.AlertHandler;
import client.models.models.FileRowData;
import javafx.scene.control.Alert;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import shared.Constants;
import shared.JsonParser;
import shared.Methods;
import shared.models.Message;

import java.net.Socket;
import java.net.URI;
import java.util.concurrent.TimeUnit;

/**
 * Class responsible for creating the web socket responsible for handling browsing operations
 */
class BrowserWebSocket extends WebSocketClient {
    private BrowserController browserController;


    private BrowserWebSocket(URI serverUri) {
        super(serverUri);
    }

    BrowserWebSocket(URI serverUri, BrowserController browserController) {
        this(serverUri);
        this.browserController = browserController;
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {

    }

    @Override
    public void onMessage(String s) {
        Message replyMessage = JsonParser.getInstance().fromJson(s, Message.class);

        /*
        Check if replay is a success message
         */
        if (replyMessage.isSuccessMessage()) {
            FileRowData[] files = JsonParser.getInstance().fromJson(replyMessage.getMessageInfo(), FileRowData[].class);
            this.browserController.updateObservableList(files, true);
        }
        /*
        Check if replay message is an update message
         */
        if (replyMessage.isUpdateMessage()) {
            this.browserController.updateObservableList(replyMessage.getMessageInfo());
        }

        /*
        Check if replay message is an error message
         */
        else if (replyMessage.isErrorMessage()) {
            AlertHandler.getInstance().start("Error", "File Not Found/Unavailable", Alert.AlertType.ERROR);
            this.browserController.updateObservableList(null, false);
        }
    }

    @Override
    public void onClose(int i, String s, boolean b) {
        AlertHandler.getInstance().start("Disconnected", "Connection to Server was lost", Alert.AlertType.WARNING);
        this.browserController.close();
    }

    @Override
    public void onError(Exception e) {

    }
}

/**
 * Class responsible for handling browsing operations
 */
public class BrowsingClient {
    private BrowserWebSocket browserWebSocket;

    /**
     * Constructor for the BrowsingClient
     *
     * @param browserController is the browsing instance to be linked with the web socket
     */
    public BrowsingClient(BrowserController browserController) {

        try {
            browserWebSocket = new BrowserWebSocket(new URI("wss://" +
                    browserController.getServerIP() + ":" + Constants.TCP_PORT), browserController);

            browserWebSocket.setSocket(Methods.getInstance().buildFactory().createSocket());

            browserWebSocket.connectBlocking(2000, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            e.printStackTrace();

            AlertHandler.getInstance().start("Connection Failure", "Unable to connect to server", Alert.AlertType.ERROR);
        }
    }

    /**
     * Method used to send a browsing request to the server
     *
     * @param filePath Is the path to the directory to browse
     */
    public void browse(String filePath) {
        Message browseMessage = new Message();
        browseMessage.createBrowseMessage(filePath);

        this.browserWebSocket.send(JsonParser.getInstance().toJson(browseMessage));
    }


    /**
     * Method used to send a delete request to the server
     *
     * @param filePath Is the path of the file to be deleted
     */
    public void delete(String filePath) {
        Message browseMessage = new Message();
        browseMessage.createDeleteMessage(filePath);

        this.browserWebSocket.send(JsonParser.getInstance().toJson(browseMessage));
    }
}
