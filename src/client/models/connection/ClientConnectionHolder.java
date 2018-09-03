package client.models.connection;

import shared.ConnectionBuilder;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

/**
 * ClientConnectionHolder is used store the connection socket
 * as well as the input & output streams of the client
 */
public class ClientConnectionHolder {

    private static ClientConnectionHolder instance = new ClientConnectionHolder();

    private String serverIP;
    private Socket connectionSocket;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;

    /**
     * Constructor for ClientConnectionHolder
     */
    private ClientConnectionHolder() {}


    /**
     * Method used to set the socket if it is unset otherwise it will do nothing
     * @param IP The IP of the server
     */
    public void setInstance(String IP) {
        /*
        Check if socket is set
         */
        if(instance.getConnectionSocket() == null) {
            try {
                instance.serverIP = IP;
                instance.connectionSocket = ConnectionBuilder.getInstance().buildClientSocket(IP);

                instance.dataInputStream = ConnectionBuilder.getInstance()
                        .buildInputStream(instance.connectionSocket);

                instance.dataOutputStream = ConnectionBuilder.getInstance()
                        .buildOutputStream(instance.connectionSocket);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }


    /**
     * Get method for instance
     * @return An instance of the ClientConnectionHolder object
     */
    public static ClientConnectionHolder getInstance(){
        return instance;
    }

    /**
     * Get method for connectionSocket
     * @return The clients connection socket to the server
     */
    public Socket getConnectionSocket() {
        return connectionSocket;
    }

    /**
     * Get method for dataInputStream
     * @return The clients input stream
     */
    public DataInputStream getDataInputStream() {
        return dataInputStream;
    }

    /**
     * Get method for dataOutputStream
     * @return The clients output stream
     */
    public DataOutputStream getDataOutputStream() {
        return dataOutputStream;
    }

    /**
     * Method used to reset the connection socket in case of failure
     */
    public void resetInstance(){
        instance.connectionSocket = null;
        instance.setInstance(instance.serverIP);
    }
}
