package server.main;


import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.DefaultSSLWebSocketServerFactory;
import org.java_websocket.server.WebSocketServer;
import server.models.DiscoveryReceiver;
import server.models.StorageHandler;
import shared.Constants;
import shared.FileTransfer;
import shared.JsonParser;
import shared.models.Message;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.security.KeyStore;
import java.util.HashMap;

class ServerHandler extends WebSocketServer {

    private HashMap<WebSocket, FileTransfer> fileTransferHashMap = new HashMap<>(10);
    private HashMap<WebSocket, String> fileLocationHashMap = new HashMap<>(10);
    private HashMap<WebSocket, Boolean> fileTransferStatusHashMap = new HashMap<>(10);

    public ServerHandler(int port) {
        super(new InetSocketAddress(port));
    }


    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {

    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {
        /*
        check if transfer operation for receiving files was completed
         */
        if (!this.fileTransferStatusHashMap.getOrDefault(webSocket, true))
            this.fileTransferHashMap.get(webSocket).deleteFile();

        this.fileTransferHashMap.remove(webSocket);
        this.fileTransferStatusHashMap.remove(webSocket);
        this.fileLocationHashMap.remove(webSocket);
    }

    @Override
    public void onMessage(WebSocket webSocket, String s) {
        try {
            Message requestMessage = JsonParser.getInstance().fromJson(s, Message.class);

            Message responseMessage = new Message();

            String msg = StorageHandler.getInstance()
                    .checkIfFileExists(requestMessage.getMessageInfo()) ? null : "File doesn't exist";

            /*
            Check if request is valid for required file/folder (file/folder exists)
             */
            if (msg != null & !requestMessage.isFileInfoMessage()) {
                responseMessage.createErrorMessage(msg);
                webSocket.send(JsonParser.getInstance().toJson(responseMessage));
            } else {
                /*
                Check if browse request
                 */
                if (requestMessage.isBrowseMessage()) {

                    responseMessage.createSuccessMessage(
                            JsonParser.getInstance().toJson(
                                    StorageHandler.getInstance().browseFolder(requestMessage.getMessageInfo())
                            )
                    );

                    webSocket.send(JsonParser.getInstance().toJson(responseMessage));
                }
                /*
                Check if delete message
                 */
                else if (requestMessage.isDeleteMessage()) {
                    responseMessage.createUpdateMessage(StorageHandler.getInstance()
                            .getParentPath(requestMessage.getMessageInfo()));

                    StorageHandler.getInstance().deleteFile(requestMessage.getMessageInfo());

                    this.broadcast(JsonParser.getInstance().toJson(responseMessage));
                }
                /*
                Check if download message
                 */
                else if (requestMessage.isDownloadMessage()) {
                    responseMessage.createSuccessMessage(
                            "" + StorageHandler.getInstance().calculateSize(requestMessage.getMessageInfo())
                    );

                    webSocket.send(JsonParser.getInstance().toJson(responseMessage));

                    StorageHandler.getInstance().sendFiles(webSocket, requestMessage.getMessageInfo());

                    responseMessage.createStreamEndMessage("");

                    webSocket.send(JsonParser.getInstance().toJson(responseMessage));

                    webSocket.close();
                }
                /*
                Check if upload message
                 */
                else if (requestMessage.isUploadMessage()) {
                    responseMessage.createSuccessMessage("");

                    webSocket.send(JsonParser.getInstance().toJson(responseMessage));

                    this.fileLocationHashMap.put(webSocket, requestMessage.getMessageInfo());
                    this.fileTransferStatusHashMap.put(webSocket, false);
                    this.fileTransferHashMap.put(webSocket, new FileTransfer());
                }
                /*
                Check if message is a file info message
                 */
                else if (requestMessage.isFileInfoMessage()) {
                    this.fileTransferHashMap.get(webSocket).receive(
                            requestMessage.getMessageInfo(),
                            StorageHandler.getInstance().getFileHierarchy(this.fileLocationHashMap.get(webSocket))
                    );
                }
                /*
                Check if end stream message
                 */
                else if (requestMessage.isStreamEndMessage()) {
                    responseMessage.createUpdateMessage(this.fileLocationHashMap.get(webSocket));

                    this.fileTransferStatusHashMap.remove(webSocket);
                    this.fileTransferStatusHashMap.put(webSocket, true);

                    this.broadcast(JsonParser.getInstance().toJson(responseMessage));

                    webSocket.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessage(WebSocket conn, ByteBuffer message) {
        this.fileTransferHashMap.get(conn).receive(message.array());
    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {

    }

    @Override
    public void onStart() {
        setConnectionLostTimeout(0);
        setConnectionLostTimeout(100);
    }
}


public class Server {

    public static void main(String[] args) {
        System.setProperty("java.net.preferIPv4Stack", "true");
        System.setProperty("javax.net.ssl.KeyStore", "/KEYSTORE");
        System.setProperty("javax.net.ssl.keyStorePassword", "password");

        try {
            new DiscoveryReceiver().start();

            ServerHandler serverHandler = new ServerHandler(Constants.TCP_PORT);

            /*
            load up the key store
            */
            String STORETYPE = Constants.KEYSTORE_TYPE;

            String STORE_PASSWORD = Constants.KEYSTORE_PASSWORD;
            String KEY_PASSWORD = Constants.KEYSTORE_PASSWORD;

            KeyStore ks = KeyStore.getInstance(STORETYPE);

            ks.load(Server.class.getResourceAsStream("/KEYSTORE"), STORE_PASSWORD.toCharArray());

            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(ks, KEY_PASSWORD.toCharArray());
            TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
            tmf.init(ks);

            SSLContext sslContext = null;
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

            serverHandler.setWebSocketFactory(new DefaultSSLWebSocketServerFactory(sslContext));

            serverHandler.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
