package shared;

import org.java_websocket.WebSocket;
import shared.models.BasicFileData;
import shared.models.Message;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;

/**
 * Class used by web sockets to send and receive data
 */
public class FileTransfer {
    private static final Object lock = new Object();
    private File firstFile;
    private BasicFileData currentBasicFileData;
    private FileOutputStream currentFileOutputStream;
    private boolean isConnectionAvailable = true;
    private long fileSizeStatus = 0;
    private long currentFileSize = 0;

    /**
     * Method used by web sockets to send files/folders
     *
     * @param webSocket Is the connection web socket
     * @param file      Is the file to be send/received
     */
    public void send(WebSocket webSocket, File file) {
        try {
            /*
            check if connection was disconnected
             */
            if (!this.isConnectionAvailable)
                return;

            /*
            check if this is the first file to be sent
             */
            if (this.firstFile == null)
                this.firstFile = file;

            BasicFileData basicFileData = new BasicFileData(file);

            basicFileData.setPath(
                    basicFileData.getPath().substring(this.firstFile.getParent().length())
            );

            /*
            Check if work is currently done on a windows machine
             */
            if (basicFileData.getPath().contains(Constants.getInstance().FORWARD_DASH))
                basicFileData.setPath(basicFileData.getPath()
                        .replaceAll(Constants.getInstance().DOUBLE_FORWARD_DASH, Constants.getInstance().BACKWARD_DASH));

            /*
            Remove dash from the beginning (first character) of the path if it exists
             */
            if (basicFileData.getPath().startsWith("/"))
                basicFileData.setPath(basicFileData.getPath().substring(1));


            Message fileInfoMessage = new Message();
            fileInfoMessage.createFileInfoMessage(JsonParser.getInstance().toJson(basicFileData));

            webSocket.send(JsonParser.getInstance().toJson(fileInfoMessage));

            /*
            Check if file is a directory
             */
            if (file.isDirectory()) {
                File[] list = file.listFiles();

                assert list != null;

                for (File subFile : list) {
                    send(webSocket, subFile);
                }

            } else {
                FileInputStream fileData = new FileInputStream(file);
                byte[] buffer = new byte[Constants.getInstance().BUFFER_SIZE];
                long size = file.length();

                /*
                While loop to read a given file as bytes
                 */
                while (size != 0) {
                    int bytesRead = fileData.read(buffer, 0, buffer.length);

                    webSocket.send(ByteBuffer.wrap(buffer, 0, bytesRead));

                    size -= bytesRead;

                    this.fileSizeStatus += bytesRead;

                    /*
                    flush web socket
                     */
                    while (true) {
                        synchronized (lock) {
                            /*
                            check if web socket has buffered data
                             */
                            if (!webSocket.hasBufferedData())
                                break;
                        }
                    }
                }

                fileData.close();
            }
        } catch (Exception e) {
            this.isConnectionAvailable = false; //connection was disconnected
            e.printStackTrace();
        }
    }

    /**
     * Method used to receive file meta data
     *
     * @param fileInfo Is the meta data of the file as a JSON string
     * @param path     Is the path for file to be saved at
     */
    public void receive(String fileInfo, String path) {
        try {
            this.currentBasicFileData = JsonParser.getInstance().fromJson(fileInfo, BasicFileData.class);

            File currentFile = new File(path + this.currentBasicFileData.getPath());

            /*
            Check if current file is first file
             */
            if (this.firstFile == null) {
                this.firstFile = currentFile;
            }

            /*
            check if file is a directory
             */
            if (currentBasicFileData.isDirectory())
                currentFile.mkdirs();
            else
                this.currentFileOutputStream = new FileOutputStream(currentFile);

            this.currentFileSize = 0;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void receive(byte[] fileData) {
        try {
            this.currentFileOutputStream.write(fileData);
            this.fileSizeStatus += fileData.length;
            this.currentFileSize += fileData.length;

            /*
            Check if all data was written to current file
             */
            if (this.currentFileSize == this.currentBasicFileData.getSize())
                this.currentFileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * get method for getFileSizeStatus
     *
     * @return the number of bytes that have been written/read so far
     */
    public long getFileSizeStatus() {
        return fileSizeStatus;
    }

    /**
     * set method for getFileSizeStatus
     *
     * @param fileSizeStatus new status size of the file
     */
    public void setFileSizeStatus(long fileSizeStatus) {
        this.fileSizeStatus = fileSizeStatus;
    }

    /**
     * Method used to delete files in case of error
     */
    public void deleteFile() {
        Methods.getInstance().deleteFile(this.firstFile);
    }
}
