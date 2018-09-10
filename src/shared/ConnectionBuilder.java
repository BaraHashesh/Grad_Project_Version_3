package shared;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
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
     * Method used to build a client ssl tcp socket
     *
     * @param IP Is the IP of the server
     * @return A client socket
     * @throws IOException Unable to connect to server
     */
    public SSLSocket buildClientSocket(String IP) throws IOException {

        System.setProperty("javax.net.ssl.trustStore", "/yourKEYSTORE");
        System.setProperty("javax.net.ssl.trustStorePassword", "password");

        SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        SSLSocket sslsocket = (SSLSocket) factory.createSocket(IP, Constants.TCP_CONNECTION_PORT);

        sslsocket.setEnabledCipherSuites(factory.getSupportedCipherSuites());


        return sslsocket;
    }

    /**
     * Method used to build a ssl tcp server socket
     *
     * @return A Server socket
     * @throws IOException Port is busy
     */
    public SSLServerSocket buildServerSocket() throws IOException {

        SSLServerSocketFactory factory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();

        SSLServerSocket sslserversocket = (SSLServerSocket) factory
                .createServerSocket(Constants.TCP_CONNECTION_PORT);

        sslserversocket.setEnabledCipherSuites(factory.getSupportedCipherSuites());

        return sslserversocket;
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
