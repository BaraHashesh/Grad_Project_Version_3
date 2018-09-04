package server.models;

import shared.FileTransfer;
import shared.Methods;
import shared.ObjectParser;
import shared.models.BasicFileData;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;

/**
 * This Class is used to access the USB and get data or directories from it
 * ROOT constant is used to indicated the path for the USB
 */
public class StorageHandler {

    private static final String ROOT = "/home/barahashesh";

    private static StorageHandler instance = new StorageHandler();

    /**
     * Get method for instance
     *
     * @return An instance of StorageHandler object
     */
    public static StorageHandler getInstance() {
        return instance;
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
     * Method used to the declare main requirements to upload a file/folder
     *
     * @param dataOutputStream Is the output stream
     * @param path             Is the path of the file/folder to be uploaded
     */
    public void uploadFile(DataOutputStream dataOutputStream, String path) {
        File mainFile = new File(path);

        String parent = mainFile.getParent();

        FileTransfer fileTransfer = new FileTransfer();

        fileTransfer.sendFiles(dataOutputStream, mainFile, parent);
    }

    /**
     * Method used to receive a file/folder from client
     *
     * @param dataInputStream Is the input stream
     * @param path            Is the path to store the folder/file under
     */
    public void downloadFile(DataInputStream dataInputStream, String path) {

        /*
        Check if path is empty (Root path)
         */
        if (path.compareTo("") == 0)
            path = ROOT;

        path = path + "/";

        try {
            new FileTransfer().receiveFiles(dataInputStream, path);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
}