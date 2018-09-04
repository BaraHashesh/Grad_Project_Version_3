package client.models.connection;

import client.controllers.BrowserController;
import shared.Constants;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * UpdateReceiver class is used by the client to receive
 * update massages from the server
 */
public class UpdateReceiver implements Runnable {

    private Thread thread = null;

    /**
     * Method used to start the thread of the UpdateReceiver
     */
    public void start() {
        if (this.thread == null) {
            this.thread = new Thread(this);
            this.thread.start();
        }
    }

    /**
     * Run method used to receive discover messages from clients
     */
    @Override
    public void run() {
        try {
            DatagramSocket socket = new DatagramSocket(Constants.UDP_UPDATE_PORT);

            /*
            Infinite while loop to receive all discover messages
             */
            //noinspection InfiniteLoopStatement
            while (true) {
                byte[] buffer = new byte[1];

                /*
                receive discovery packet
                 */
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                String serverIP = packet.getAddress().toString().substring(1);

                if (BrowserController.getInstance().getServerIP().compareTo(serverIP) == 0) {
                    BrowserController.getInstance().updateObservableList();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
