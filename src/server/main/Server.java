package server.main;

import server.models.BroadCastReceiver;
import server.models.ServerHandler;
import shared.ConnectionBuilder;

import java.net.ServerSocket;
import java.net.Socket;


public class Server {

    public static void main(String[] args) {
        try {
            System.setProperty("java.net.preferIPv4Stack", "true");

            new BroadCastReceiver().start();

            ServerSocket serverStringSocket = ConnectionBuilder.getInstance().buildServerStringSocket();
            ServerSocket serverByteSocket = ConnectionBuilder.getInstance().buildServerByteSocket();

            /*
            Infinite loop to handle multiple clients
             */
            //noinspection InfiniteLoopStatement
            while (true) {
                Socket stringSocket = serverStringSocket.accept();
                Socket byteSocket = serverByteSocket.accept();

                new ServerHandler(stringSocket, byteSocket).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
