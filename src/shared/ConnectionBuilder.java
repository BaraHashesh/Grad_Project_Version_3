package shared;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * ConnectionBuilder class is used to build connection sockets for both server and client
 * and is used to build input and output streams for both strings and bytes
 */
public class ConnectionBuilder {
    private static ConnectionBuilder instance = new ConnectionBuilder();

    /**
     * Empty Constructor
     */
    private ConnectionBuilder() {
    }

    /**
     * Get method for instance
     *
     * @return An instance of the ConnectionBuilder object
     */
    public static ConnectionBuilder getInstance() {
        return instance;
    }


    /**
     * Method used to build a client socket for byte exchange
     *
     * @param IP Is the IP of the server
     * @return A client socket
     * @throws IOException Unable to connect to server
     */
    public Socket buildClientSocket(String IP) throws IOException {
        return new Socket(IP, Constants.PORT_NUMBER);
    }

    /**
     * Method used to build a server socket for string exchange
     *
     * @return A Server socket
     * @throws IOException Port is busy
     */
    public ServerSocket buildServerSocket() throws IOException {
        return new ServerSocket(Constants.PORT_NUMBER);
    }


    /**
     * Method used to build an output stream for pure bytes
     *
     * @param socket Is the socket to build an output stream for
     * @return A DataOutputStream Object
     * @throws IOException Socket stream is closed or unavailable
     */
    public DataOutputStream buildOutputStream(Socket socket) throws IOException {
        return new DataOutputStream(
                new BufferedOutputStream(socket.getOutputStream())
        );
    }

    /**
     * Method used to build an input stream for pure bytes
     *
     * @param socket Is the socket to build an input stream for
     * @return A DataInputStream Object
     * @throws IOException Socket stream is closed or unavailable
     */
    public DataInputStream buildInputStream(Socket socket) throws IOException {
        return new DataInputStream(
                new BufferedInputStream(socket.getInputStream())
        );
    }
}
