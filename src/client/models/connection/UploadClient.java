package client.models.connection;


import client.models.EstimationViewUpdater;
import shared.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
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
     * constructor for specific server
     */
    public UploadClient(String hostIP) {
        this.IP = hostIP;
    }

    /**
     * Initialize method for the variables
     * also start thread operations
     *
     * @param file           is file to be uploaded
     * @param locationToSave is the location to save file under within the USB
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

            Socket stringSocket = ConnectionBuilder.getInstance().buildClientStringSocket(this.IP);
            Socket byteSocket = ConnectionBuilder.getInstance().buildClientByteSocket(this.IP);

            BufferedWriter stringOutputStream = ConnectionBuilder.getInstance()
                    .buildStringOutputStream(stringSocket);

            DataOutputStream byteOutputStream = ConnectionBuilder.getInstance()
                    .buildByteOutputStream(byteSocket);

            BufferedReader stringInputStream = ConnectionBuilder.getInstance()
                    .buildStringInputStream(stringSocket);

            request = new Message();
            request.createUploadMessage(locationToSave);

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
                String parent = this.file.getParent();

                FileTransfer fileTransfer = new FileTransfer();

                EstimationViewUpdater updater = new EstimationViewUpdater(fileTransfer,
                        Methods.getInstance().calculateSize(this.file), stringSocket, byteSocket);

                updater.start();

                fileTransfer.sendFiles(stringOutputStream, byteOutputStream, this.file, parent);

                updater.finalUpdate();
            }

            stringInputStream.close();
            stringOutputStream.close();
            byteOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
