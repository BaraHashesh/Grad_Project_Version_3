package shared.models;

import java.io.File;

/**
 * BasicFileData object is used as a replacement for the File object
 * to exchange file meta information over TCP using JSON format
 */
public class BasicFileData {

    private String path;
    private long size;
    private long lastModified;
    private boolean directory;

    /**
     * Constructor for BasicFileData
     *
     * @param file File object to be changed (transformed)
     */
    public BasicFileData(File file) {
        this.path = file.getPath();
        this.size = file.length();
        this.lastModified = file.lastModified();
        this.directory = file.isDirectory();
    }

    /**
     * Get method for size
     *
     * @return file size in bytes
     */
    public long getSize() {
        return size;
    }

    /**
     * Get method for lastModified
     *
     * @return Date at which file was last modified
     */
    public long getLastModified() {
        return lastModified;
    }

    /**
     * Get method for directory
     *
     * @return A Boolean that indicates wither the file is a directory or not
     */
    public boolean isDirectory() {
        return directory;
    }

    /**
     * Get method for path
     *
     * @return The path for the file
     */
    public String getPath() {
        return path;
    }

    /**
     * Set method for path
     *
     * @param path The new modified path for the file
     */
    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "BasicFileData{" +
                "path='" + path + '\'' +
                ", size=" + size +
                ", lastModified=" + lastModified +
                ", directory=" + directory +
                '}';
    }
}


