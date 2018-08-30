package shared;

import java.io.*;


/**
 * FileTransfer class is used to transfer and receive files
 */
public class FileTransfer {
    private static final int BUFFER = 1024 * 1024 * 4;
    private long transferredFileSize = 0;
    private File firstFile;
    private boolean pipe = true;

    /**
     * method used to recursively upload files/folders
     *
     * @param stringOutputStream Is output stream for strings
     * @param byteOutputStream   Is output stream for bytes
     * @param file               Is the main file/folder to be uploaded
     * @param mainPath           Is the parents path of the main file/folder(to establish relationship of files)
     */
    public void sendFiles(BufferedWriter stringOutputStream,
                          DataOutputStream byteOutputStream,
                          File file, String mainPath) {

        try {
            /*
            check if pipe was broken
             */
            if (!this.pipe)
                return;

            BasicFileData basicFileData = new BasicFileData(file);

            basicFileData.setPath(basicFileData.getPath().substring(mainPath.length()));

            /*
            Check if work is currently done on a windows machine
             */
            if (basicFileData.getPath().contains(Constants.FORWARD_DASH))
                basicFileData.setPath(basicFileData.getPath()
                        .replaceAll(Constants.DOUBLE_FORWARD_DASH, Constants.BACKWARD_DASH));

            /*
            Remove dash from the beginning (first character) of the path if it exists
             */
            if (basicFileData.getPath().startsWith("/"))
                basicFileData.setPath(basicFileData.getPath().substring(1));


            stringOutputStream.write(JsonParser.getInstance().toJson(basicFileData));
            stringOutputStream.write('\n');
            stringOutputStream.flush();

            /*
            Check if file is a directory
             */
            if (file.isDirectory()) {
                File[] list = file.listFiles();

                assert list != null;

                for (File subFile :
                        list) {
                    sendFiles(stringOutputStream, byteOutputStream, subFile, mainPath);
                }

            } else {
                FileInputStream fileData = new FileInputStream(file);
                byte[] buffer = new byte[BUFFER];
                long size = file.length();

                /*
                While loop to read a given file as bytes
                 */
                while (size != 0) {
                    int bytesRead = fileData.read(buffer, 0, buffer.length);

                    byteOutputStream.write(buffer, 0, bytesRead);

                    size -= bytesRead;

                    this.transferredFileSize += bytesRead;

                    byteOutputStream.flush();
                }
                fileData.close();
            }
        } catch (Exception e) {
            this.pipe = false; //Pipe was broken
            e.printStackTrace();
        }
    }

    /**
     * Method used to download a file/folder
     *
     * @param byteInputStream   Is the input stream to receive bytes from
     * @param stringInputStream Is the input stream to receive strings from
     * @param path              Is the location to save data under
     */
    public void receiveFiles(DataInputStream byteInputStream, BufferedReader stringInputStream, String path) {
        FileOutputStream output = null;
        try {
            for (String temp; (temp = stringInputStream.readLine()) != null; ) {

                BasicFileData basicFileData = JsonParser.getInstance().fromJson(temp, BasicFileData.class);

                /*
                Check if basicFileData is null
                 */
                if (basicFileData == null)
                    throw new Exception("No Meta Data");

                /*
                Check if file is a directory
                 */
                if (basicFileData.isDirectory()) {
                    File file = new File(path + basicFileData.getPath());

                    /*
                    Check if the file is the first one to be received
                     */
                    if (this.firstFile == null)
                        this.firstFile = file;

                    //noinspection ResultOfMethodCallIgnored
                    file.mkdirs();
                } else {
                    File file = new File(path + basicFileData.getPath());

                    output = new FileOutputStream(file);

                    long size = basicFileData.getSize();
                    byte[] buffer = new byte[BUFFER];

                    //check if file is the first to be received
                    if (this.firstFile == null)
                        this.firstFile = new File(path + basicFileData.getPath());

                    /*
                    While loop to read a given file from the byte stream
                     */
                    while (size > 0) {
                        int bytesRead;
                        /*
                        Check if file size to be read is bigger or smaller than the reading buffer
                         */
                        if (size > BUFFER)
                            bytesRead = byteInputStream.read(buffer, 0, BUFFER);
                        else
                            bytesRead = byteInputStream.read(buffer, 0, (int) size);

                        /*
                        Check if read operation was successful
                         */
                        if (bytesRead != -1) {
                            this.transferredFileSize += bytesRead;
                            size -= bytesRead;
                            output.write(buffer, 0, bytesRead);
                        } else
                            break;

                    }
                    output.close();
                }
            }
        } catch (Exception e) {
            try {
                assert output != null; //Check if output (FileOutputStream) is set
                output.close();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            Methods.getInstance().deleteFile(this.firstFile);
            e.printStackTrace();
        }
    }

    /**
     * get method for transferredFileSize
     *
     * @return the number of bytes that have been written/read so far
     */
    public long getTransferredFileSize() {
        return transferredFileSize;
    }
}