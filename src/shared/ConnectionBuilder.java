package shared;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

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
     * Method used to build a client socket for string exchange
     *
     * @param IP Is the IP of the server
     * @return A client socket used to send and receive JSON objects
     * @throws IOException Unable to connect to server
     */
    public Socket buildClientStringSocket(String IP) throws IOException {
        return new Socket(IP, Constants.STRING_PORT);
    }

    /**
     * Method used to build a client socket for byte exchange
     *
     * @param IP Is the IP of the server
     * @return A client socket used to send and receive pure bytes
     * @throws IOException Unable to connect to server
     */
    public Socket buildClientByteSocket(String IP) throws IOException {
        return new Socket(IP, Constants.BYTE_PORT);
    }

    /**
     * Method used to build a server socket for string exchange
     *
     * @return A Server socket used to send and receive JSON objects
     * @throws IOException Port is busy
     */
    public ServerSocket buildServerStringSocket() throws IOException {
        return new ServerSocket(Constants.STRING_PORT);
    }

    /**
     * Method used to build a server socket for byte exchange
     *
     * @return A Server socket used to send and receive pure bytes
     * @throws IOException Port is busy
     */
    public ServerSocket buildServerByteSocket() throws IOException {
        return new ServerSocket(Constants.BYTE_PORT);
    }

    /**
     * Method used to build an output stream for JSON strings
     *
     * @param socket Is the socket to build an output stream for
     * @return A BufferedWriter Object
     * @throws IOException Socket stream is closed or unavailable
     */
    public BufferedWriter buildStringOutputStream(Socket socket) throws IOException {
        return new BufferedWriter(
                new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8)
        );
    }

    /**
     * Method used to build an input stream for JSON strings
     *
     * @param socket Is the socket to build an input stream for
     * @return A BufferedReader Object
     * @throws IOException Socket stream is closed or unavailable
     */
    public BufferedReader buildStringInputStream(Socket socket) throws IOException {
        return new BufferedReader(
                new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8)
        );
    }

    /**
     * Method used to build an output stream for pure bytes
     *
     * @param socket Is the socket to build an output stream for
     * @return A BufferedOutputStream Object
     * @throws IOException Socket stream is closed or unavailable
     */
    public BufferedOutputStream buildByteOutputStream(Socket socket) throws IOException {
        return new BufferedOutputStream(socket.getOutputStream());
    }

    /**
     * Method used to build an input stream for pure bytes
     *
     * @param socket Is the socket to build an input stream for
     * @return A BufferedInputStream Object
     * @throws IOException Socket stream is closed or unavailable
     */
    public BufferedInputStream buildByteInputStream(Socket socket) throws IOException {
        return new BufferedInputStream(socket.getInputStream());
    }
}
