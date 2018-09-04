package client.models.connection;

import client.models.controllers.AlertHandler;
import client.models.models.FileRowData;
import javafx.scene.control.Alert;
import shared.ConnectionBuilder;
import shared.JsonParser;
import shared.models.Message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
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
    public FileRowData[] browserRequest(String path) {
        Message request, response;
        try {
            Socket clientSocket = ConnectionBuilder.getInstance().buildClientSocket(this.IP);

            DataOutputStream dataOutputStream = ConnectionBuilder.getInstance()
                    .buildOutputStream(clientSocket);

            DataInputStream dataInputStream = ConnectionBuilder.getInstance()
                    .buildInputStream(clientSocket);

            request = new Message();
            request.createBrowseMessage(path);

            dataOutputStream.writeUTF(JsonParser.getInstance().toJson(request));
            dataOutputStream.flush();

            response = JsonParser.getInstance().fromJson(dataInputStream.readUTF(), Message.class);

            clientSocket.close();
            dataInputStream.close();
            dataOutputStream.close();

            /*
             Check if operation was a success
              */
            if (response.isSuccessMessage()) {
                return JsonParser.getInstance().fromJson(response.getMessageInfo(), FileRowData[].class);
            } else {

                AlertHandler.getInstance().start("Server Error",
                        response.getMessageInfo(), Alert.AlertType.ERROR);

                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();

            AlertHandler.getInstance().start("Server Error",
                    "Unable to connect to server", Alert.AlertType.ERROR);

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
            Socket clientSocket = ConnectionBuilder.getInstance().buildClientSocket(this.IP);

            DataOutputStream dataOutputStream = ConnectionBuilder.getInstance()
                    .buildOutputStream(clientSocket);

            DataInputStream dataInputStream = ConnectionBuilder.getInstance()
                    .buildInputStream(clientSocket);

            request = new Message();
            request.createDeleteMessage(path);

            dataOutputStream.writeUTF(JsonParser.getInstance().toJson(request));
            dataOutputStream.flush();

            response = JsonParser.getInstance().fromJson(dataInputStream.readUTF(), Message.class);

            clientSocket.close();
            dataInputStream.close();
            dataOutputStream.close();

            // check if operation is possible
            if (response.isErrorMessage()) {

                AlertHandler.getInstance().start("Server Error",
                        response.getMessageInfo(), Alert.AlertType.ERROR);

                return false;
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();

            AlertHandler.getInstance().start("Server Error",
                    "Unable to connect to server", Alert.AlertType.ERROR);

            return false;
        }
    }
}
