package client.models;

/**
 * ServerRowInfo object is used by the GUI to display information
 * about the servers (storage devices available for the user)
 */
public class ServerRowInfo {

    private String IP;

    /**
     * Constructor for ServerRowInfo object
     *
     * @param IP The IP of the storage device
     */
    public ServerRowInfo(String IP) {
        this.IP = IP;
    }

    /**
     * Get method for IP
     *
     * @return The IP of the storage device
     */
    public String getIP() {
        return IP;
    }
}
