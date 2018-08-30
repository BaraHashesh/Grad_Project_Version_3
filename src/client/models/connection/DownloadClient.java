package client.models.connection;


import client.models.EstimationUpdater;
import shared.ConnectionBuilder;
import shared.FileTransfer;
import shared.JsonParser;
import shared.models.Message;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
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
            Socket stringSocket = ConnectionBuilder.getInstance().buildClientStringSocket(this.IP);
            Socket byteSocket = ConnectionBuilder.getInstance().buildClientByteSocket(this.IP);

            BufferedWriter stringOutputStream = ConnectionBuilder.getInstance()
                    .buildStringOutputStream(stringSocket);

            DataInputStream byteInputStream = ConnectionBuilder.getInstance()
                    .buildByteInputStream(byteSocket);

            BufferedReader stringInputStream = ConnectionBuilder.getInstance()
                    .buildStringInputStream(stringSocket);


            request = new Message();
            request.createDownloadMessage(path);

            stringOutputStream.write(JsonParser.getInstance().toJson(request));
            stringOutputStream.write('\n');
            stringOutputStream.flush();

            response = JsonParser.getInstance().fromJson(stringInputStream.readLine(), Message.class);

            /*
             Check if operation was possible
              */
            if (response.isErrorMessage()) {
                /*
                 * Handle Error Here
                 */
            } else {

                FileTransfer fileTransfer = new FileTransfer();

                long size = Long.parseLong(response.getMessageInfo());

                EstimationUpdater updater = new EstimationUpdater(fileTransfer,
                        size, stringSocket, byteSocket);

                updater.start();

                fileTransfer.receiveFiles(byteInputStream, stringInputStream, locationToSave);

                updater.finalUpdate();
            }

            stringInputStream.close();
            byteInputStream.close();
            stringOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
