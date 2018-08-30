package client.models.connection;

import shared.BasicFileData;
import shared.ConnectionBuilder;
import shared.JsonParser;
import shared.Message;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.net.Socket;

/**
 * BrowseClient class is used to browse the storage device and to delete files if required
 */
public class BrowsingClient {
    private String IP;

    /**
     * Constructor for the BrowsingClient object for a specific storage device
     *
     * @param hostIP Is the IP of the storage device
     */
    public BrowsingClient(String hostIP) {
        this.IP = hostIP;
    }

    /**
     * Method used to fetch the information of files under a certain directory
     *
     * @param path Is the path to the directory
     * @return Information about the files in the directory if it exists
     */
    public BasicFileData[] browserRequest(String path) {
        Message request, response;
        try {
            Socket clientStringsSocket = ConnectionBuilder.getInstance().buildClientStringSocket(this.IP);
            Socket clientBytesSocket = ConnectionBuilder.getInstance().buildClientByteSocket(this.IP);

            BufferedWriter stringOutputStream = ConnectionBuilder.getInstance()
                    .buildStringOutputStream(clientStringsSocket);

            BufferedReader stringInputStream = ConnectionBuilder.getInstance()
                    .buildStringInputStream(clientStringsSocket);

            request = new Message();
            request.createBrowseMessage(path);

            stringOutputStream.write(JsonParser.getInstance().toJson(request));
            stringOutputStream.write('\n');
            stringOutputStream.flush();

            response = JsonParser.getInstance().fromJson(stringInputStream.readLine(), Message.class);

            stringInputStream.close();
            stringOutputStream.close();
            clientBytesSocket.close();
            clientStringsSocket.close();

            /*
             Check if operation was a success
              */
            if (response.isSuccessMessage()) {
                return JsonParser.getInstance().fromJson(response.getMessageInfo(), BasicFileData[].class);
            } else {
                /*
                 * Handle error here
                 */
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Method used to delete a file/directory on the storage device
     *
     * @param path Is the path to the file/directory the storage device
     * @return A boolean that indicates wither delete operation was successful or not
     */
    public boolean deleteRequest(String path) {
        Message request, response;
        try {

            Socket clientStringsSocket = ConnectionBuilder.getInstance()
                    .buildClientStringSocket(this.IP);

            Socket clientBytesSocket = ConnectionBuilder.getInstance()
                    .buildClientByteSocket(this.IP);


            BufferedWriter stringOutputStream = ConnectionBuilder.getInstance()
                    .buildStringOutputStream(clientStringsSocket);

            BufferedReader stringInputStream = ConnectionBuilder.getInstance()
                    .buildStringInputStream(clientStringsSocket);


            request = new Message();
            request.createDeleteMessage(path);

            stringOutputStream.write(JsonParser.getInstance().toJson(request));
            stringOutputStream.write('\n');
            stringOutputStream.flush();

            response = JsonParser.getInstance().fromJson(stringInputStream.readLine(), Message.class);

            stringInputStream.close();
            stringOutputStream.close();
            clientBytesSocket.close();
            clientStringsSocket.close();

            // check if operation is possible
            if (response.isErrorMessage()) {
                /*
                 * Handle Error Here
                 */
                Object error;
                return false;
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
