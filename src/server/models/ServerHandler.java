package server.models;

import shared.ConnectionBuilder;
import shared.JsonParser;
import shared.Message;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;


/**
 * ServerHandler Class is used to Handle connection Sockets that the server creates
 * for each client using threads
 */
public class ServerHandler implements Runnable {
    private Socket stringSocket;
    private Socket byteSocket;
    private Thread thread;

    /**
     * Constructor
     *
     * @param stringSocket Is the connection socket used to exchange information about status and
     *                     the meta data of the files
     * @param byteSocket   Is the connection socket used to in file data as bytes
     */
    public ServerHandler(Socket stringSocket, Socket byteSocket) {
        this.stringSocket = stringSocket;
        this.byteSocket = byteSocket;
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
            BufferedReader stringInputStream = ConnectionBuilder.getInstance()
                    .buildStringInputStream(this.stringSocket);

            BufferedWriter stringOutputStream = ConnectionBuilder.getInstance()
                    .buildStringOutputStream(this.stringSocket);

            DataInputStream byteInputStream = ConnectionBuilder.getInstance()
                    .buildByteInputStream(this.byteSocket);

            DataOutputStream byteOutputStream = ConnectionBuilder.getInstance()
                    .buildByteOutputStream(this.byteSocket);

            clientRequest = JsonParser.getInstance().fromJson(stringInputStream.readLine(), Message.class);

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
                stringOutputStream.write(JsonParser.getInstance().toJson(serverResponse));
                stringOutputStream.write('\n');
                stringOutputStream.flush();
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
                stringOutputStream.write(JsonParser.getInstance().toJson(serverResponse));
                stringOutputStream.write('\n');
                stringOutputStream.flush();
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


                    stringOutputStream.write(JsonParser.getInstance().toJson(serverResponse));
                    stringOutputStream.write('\n');
                    stringOutputStream.flush();

                    StorageHandler.getInstance().uploadFile(stringOutputStream, byteOutputStream, path);
                } else {
                    serverResponse = new Message();
                    serverResponse.createErrorMessage(conflict);

                    stringOutputStream.write(JsonParser.getInstance().toJson(serverResponse));
                    stringOutputStream.write('\n');
                    stringOutputStream.flush();
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

                    stringOutputStream.write(JsonParser.getInstance().toJson(serverResponse));
                    stringOutputStream.write('\n');
                    stringOutputStream.flush();

                    StorageHandler.getInstance().downloadFile(byteInputStream, stringInputStream, path);
                } else {
                    serverResponse = new Message();
                    serverResponse.createErrorMessage(conflict);

                    stringOutputStream.write(JsonParser.getInstance().toJson(serverResponse));
                    stringOutputStream.write('\n');
                    stringOutputStream.flush();
                }
            }


            stringInputStream.close();
            stringOutputStream.close();
            byteInputStream.close();
            byteOutputStream.close();
            this.byteSocket.close();
            this.stringSocket.close();

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

}