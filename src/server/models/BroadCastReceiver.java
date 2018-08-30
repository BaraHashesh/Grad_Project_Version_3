package server.models;

import shared.ConnectionBuilder;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * BroadCastReceiver class is used by server to receive
 * discover massages from the clients and then replay to them
 */
public class BroadCastReceiver implements Runnable {

    private Thread thread = null;

    /**
     * Method used to start the thread of the broadcast receiving operations
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
            DatagramSocket socket = new DatagramSocket(ConnectionBuilder.BROAD_CAST_PORT);

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

                /*
                extract address and port from packet
                 */
                InetAddress address = packet.getAddress();
                int port = packet.getPort();

                /*
                replay to discovery packet
                 */
                packet = new DatagramPacket(buffer, buffer.length, address, port);
                socket.send(packet);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
