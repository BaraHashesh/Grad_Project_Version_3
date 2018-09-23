package server.models;

import org.java_websocket.WebSocket;
import shared.*;
import shared.models.BasicFileData;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;

/**
 * This Class is used to access the USB and get data or directories from it
 * ROOT constant is used to indicated the path for the USB
 */
public class StorageHandler {

    private static final String ROOT = "/media/pi";

    private static StorageHandler instance = new StorageHandler();

    /**
     * Get method for instance
     *
     * @return An instance of {@link StorageHandler} object
     */
    public static StorageHandler getInstance() {
        return instance;
    }

    /**
     * Empty Constructor for {@link StorageHandler}
     */
    private StorageHandler(){

    }


    /**
     * Method used to get the list of child files for a given folder in the storage device
     *
     * @param folderURL Path to the folder (given root is an empty string i.e. "")
     * @return List of the child files for the folder
     */
    public BasicFileData[] browseFolder(String folderURL) {

        /*
        Check if folder is out of scope
         */
        if (folderURL.length() < ROOT.length())
            folderURL = ROOT;

        File folder = new File(folderURL);

        /*
        Check if folder exists
         */
        if (folder.exists()) {
            return ObjectParser.getInstance()
                    .fileToBasicFileData(folder
                            /*lambda expression here is used to remove hidden files*/
                            .listFiles(pathname -> !pathname.isHidden()));
        }

        return null;
    }


    /**
     * Method used to delete a file
     *
     * @param path is the path of the file within the USB
     */
    public void deleteFile(String path) {
        Methods.getInstance().deleteFile(new File(path));
    }

    /**
     * Method used to send files from the server to the client
     *
     * @param webSocket Is the webSocket of the connection
     * @param path      Is the path of the file/folder to be uploaded
     */
    public void sendFiles(WebSocket webSocket, String path) {
        File mainFile = new File(path);

        new FileTransfer().send(webSocket, mainFile);
    }

    /**
     * Method used to calculate the size of a file/folder
     *
     * @param path Is the path to the file/folder
     * @return The size of the file/folder in bytes
     */
    public long calculateSize(String path) {
        return Methods.getInstance().calculateSize(new File(path));
    }

    /**
     * Method used to check if a file/folder exists
     *
     * @param path The path for the file
     * @return If the file/folder exists or not
     */
    public boolean checkIfFileExists(String path) {
        /*
        Check if path is empty (Root path)
         */
        if (path.compareTo("") == 0)
            path = ROOT;

        File file = new File(path);

        return file.exists();
    }

    /**
     * Method used to obtain the path for the parent of the file/folder
     * @param filePath Is the path for the file/folder in question
     * @return The path of the parent for the file/folder in question
     */
    public String getParentPath(String filePath) {
        return new File(filePath).getParent();
    }

    /**
     * Method used to modify the path of the file for hierarchy reasons
     * @param path Is the path for the file
     * @return The modified bath
     */
    public String getFileHierarchy(String path) {
        /*
        Check if path is empty (Root path)
         */
        if (path.compareTo("") == 0){
            path = ROOT;
        }

        path += Constants.BACKWARD_DASH;

        return path;
    }
}