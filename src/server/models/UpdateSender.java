package server.models;

import shared.Constants;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

/**
 * UpdateSender class is used by server to send
 * update massages to the clients
 */
public class UpdateSender implements Runnable {

    private Thread thread = null;

    /**
     * Method used to start the thread of the UpdateSender
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
            DatagramSocket socket = new DatagramSocket();
            socket.setBroadcast(true);

            byte[] buffer = new byte[1];

            Enumeration<NetworkInterface> networkInterface = NetworkInterface.getNetworkInterfaces();

            String broadcastIP = networkInterface.nextElement().getInterfaceAddresses()
                    .get(1).getBroadcast().toString().substring(1);

            DatagramPacket packet = new DatagramPacket(buffer, buffer.length,
                    InetAddress.getByName(broadcastIP), Constants.UDP_UPDATE_PORT);

            socket.send(packet); // send a broadcast message for all devices
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
