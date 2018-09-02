package server.models;

import shared.ConnectionBuilder;
import shared.JsonParser;
import shared.models.Message;

import java.io.*;
import java.net.Socket;


/**
 * ServerHandler Class is used to Handle connection Sockets that the server creates
 * for each client using threads
 */
public class ServerHandler implements Runnable {
    private Socket clientSocket;
    private Thread thread;

    /**
     * Constructor
     *
     * @param clientSocket Is the connection socket for the client
     */
    public ServerHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    /**
     * Start method for the thread
     */
    public void start() {
        if (thread == null) {
            thread = new Thread(this);
            thread.start();
        }
    }

    /**
     * Run method used to handle client request
     */
    @Override
    public void run() {
        Message clientRequest, serverResponse;

        try {
            DataInputStream dataInputStream = ConnectionBuilder.getInstance()
                    .buildInputStream(this.clientSocket);

            DataOutputStream dataOutputStream = ConnectionBuilder.getInstance()
                    .buildOutputStream(this.clientSocket);

            clientRequest = JsonParser.getInstance().fromJson(dataInputStream.readUTF(), Message.class);

            /*
            Check if browse request
             */
            if (clientRequest.isBrowseMessage()) {
                String path = clientRequest.getMessageInfo();

                String conflict = checkForConflict(path);
                /*
                 Check for conflicts
                  */
                if (conflict == null) {
                    String files = JsonParser.getInstance().toJson(StorageHandler.getInstance().browseFolder(path));

                    serverResponse = new Message();
                    serverResponse.createSuccessMessage(files);
                } else {
                    serverResponse = new Message();
                    serverResponse.createErrorMessage(conflict);
                }
                dataOutputStream.writeUTF(JsonParser.getInstance().toJson(serverResponse));
            }

            /*
            Check if delete request
             */
            if (clientRequest.isDeleteMessage()) {
                String path = clientRequest.getMessageInfo();

                String conflict = checkForConflict(path);
                /*
                 check for conflicts
                  */
                if (conflict == null) {
                    StorageHandler.getInstance().deleteFile(path);

                    serverResponse = new Message();
                    serverResponse.createSuccessMessage("");
                } else {
                    serverResponse = new Message();
                    serverResponse.createErrorMessage(conflict);
                }
                dataOutputStream.writeUTF(JsonParser.getInstance().toJson(serverResponse));
            }

            /*
            Check if download message
             */
            if (clientRequest.isDownloadMessage()) {
                String path = clientRequest.getMessageInfo();

                String conflict = checkForConflict(path);
                /*
                 check for conflicts
                  */
                if (conflict == null) {
                    serverResponse = new Message();
                    // Set the info of the response message to be the total size of the file/folder
                    serverResponse.createSuccessMessage(StorageHandler.getInstance().calculateSize(path) + "");


                    dataOutputStream.writeUTF(JsonParser.getInstance().toJson(serverResponse));

                    StorageHandler.getInstance().uploadFile(dataOutputStream, path);
                } else {
                    this.handleConflict(dataOutputStream, conflict);
                }
            }

            /*
            Check if upload request
             */
            if (clientRequest.isUploadMessage()) {
                String path = clientRequest.getMessageInfo();

                String conflict = checkForConflict(path);
                /*
                 check for conflicts
                  */
                if (conflict == null) {
                    serverResponse = new Message();
                    serverResponse.createSuccessMessage("");

                    dataOutputStream.writeUTF(JsonParser.getInstance().toJson(serverResponse));

                    StorageHandler.getInstance().downloadFile(dataInputStream, path);
                } else {
                    this.handleConflict(dataOutputStream, conflict);
                }
            }


            dataOutputStream.close();
            dataInputStream.close();
            this.clientSocket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * method used to check for read-write conflicts
     *
     * @param filePath is the path of the file to be checked
     * @return a string if there is a conflict, null otherwise
     */
    public String checkForConflict(String filePath) {
        return null;
    }

    /**
     * Method used to handle conflicts in Upload & Download requests
     *
     * @param outputStream Is the output stream
     * @throws IOException Output stream is not available
     */
    private void handleConflict(DataOutputStream outputStream, String conflict) throws IOException {
        Message serverResponse = new Message();
        serverResponse.createErrorMessage(conflict);

        outputStream.writeUTF(JsonParser.getInstance().toJson(serverResponse));
    }

}