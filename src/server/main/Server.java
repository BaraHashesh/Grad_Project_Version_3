package server.main;

import server.models.DiscoveryReceiver;
import server.models.ServerHandler;
import shared.ConnectionBuilder;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLSocket;


public class Server {

    public static void main(String[] args) {
        try {
            System.setProperty("java.net.preferIPv4Stack", "true");
            System.setProperty("javax.net.ssl.KeyStore", "/KEYSTORE");
            System.setProperty("javax.net.ssl.keyStorePassword", "password");

            new DiscoveryReceiver().start();

            SSLServerSocket serverSocket = ConnectionBuilder.getInstance().buildServerSocket();

            /*
            Infinite loop to handle multiple clients
             */
            //noinspection InfiniteLoopStatement
            while (true) {
                SSLSocket clientSocket = (SSLSocket) serverSocket.accept();
                new ServerHandler(clientSocket).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
