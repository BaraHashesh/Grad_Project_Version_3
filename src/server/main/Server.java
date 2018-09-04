package server.main;

import server.models.DiscoveryReceiver;
import server.models.ServerHandler;
import shared.ConnectionBuilder;

import java.net.ServerSocket;
import java.net.Socket;


public class Server {

    public static void main(String[] args) {
        try {
            System.setProperty("java.net.preferIPv4Stack", "true");

            new DiscoveryReceiver().start();

            ServerSocket serverSocket = ConnectionBuilder.getInstance().buildServerSocket();

            /*
            Infinite loop to handle multiple clients
             */
            //noinspection InfiniteLoopStatement
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new ServerHandler(clientSocket).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
