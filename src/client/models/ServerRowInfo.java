package client.models;

/**
 * ServerRowInfo object is used by the GUI to display information
 * about the servers (storage devices available for the user)
 */
public class ServerRowInfo {

    private String ip;

    /**
     * Constructor for ServerRowInfo object
     *
     * @param IP The ip of the storage device
     */
    public ServerRowInfo(String IP) {
        this.ip = IP;
    }

    /**
     * Get method for ip
     *
     * @return The ip of the storage device
     */
    public String getIp() {
        return ip;
    }
}
