package client.models;

/**
 * ServerInfoRow object is used by the GUI to display information
 * about the servers (storage devices available for the user)
 */
public class ServerInfoRow {

    private String IP;

    /**
     * Constructor for ServerInfoRow object
     *
     * @param IP The IP of the storage device
     */
    public ServerInfoRow(String IP) {
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
